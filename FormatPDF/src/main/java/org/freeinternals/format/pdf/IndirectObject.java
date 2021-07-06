package org.freeinternals.format.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.pdf.basicobj.Analysis;
import org.freeinternals.format.pdf.basicobj.Reference;
import org.freeinternals.format.pdf.basicobj.Stream;

/**
 * PDF Indirect Object, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.10</code>: Indirect Object.
 *
 * @author Amos Shi
 */
public class IndirectObject extends FileComponent implements GenerateTreeNode {

    /**
     * Start definition regular expression. <p> Example: </p>
     * <pre>
     *  5534 0 obj
     *  5491 0 obj SOME TEXT OF OBJECT
     *  5491 0 objSOME TEXT OF OBJECT
     * </pre>
     *
     */
    public static final String SIGNATURE_START_REGEXP = "\\d+.*\\d+.*obj.*";
    /**
     * Key word in the first line of current object.
     */
    public static final String SIGNATURE_START = "obj";
    /**
     * End line of current object.
     */
    public static final String SIGNATURE_END = "endobj";
    /**
     * A positive integer object number. <p> Indirect objects may be numbered
     * sequentially within a PDF file, but this is not required; object numbers
     * may be assigned in any arbitrary order. </p>
     */
    private final int objectNumber;
    /**
     * A non-negative integer generation number. <p> In a newly created file,
     * all indirect objects shall have generation numbers of 0. Nonzero
     * generation numbers may be introduced when the file is later updated. </p>
     */
    private final int generationNumber;
    /**
     * Length of {@link #objectNumber} and {@link #generationNumber}.
     */
    private final int numberLen;
    /**
     * The last line of the indirect object.
     */
    private final ASCIILine signatureStart;
    /**
     * The last line of the indirect object.
     */
    private ASCIILine signatureEnd;
    /**
     * Component of current object.
     */
    private List<FileComponent> components = Collections.synchronizedList(new ArrayList<FileComponent>(100));

    IndirectObject(PosDataInputStream stream, ASCIILine line) throws IOException, FileFormatException {
        stream.backward(line.length());
        super.startPos = stream.getPos();
        this.objectNumber = Integer.parseInt(stream.readASCIIUntil(PDFStatics.WhiteSpace.SP));
        this.generationNumber = Integer.parseInt(stream.readASCIIUntil(PDFStatics.WhiteSpace.SP));
        this.numberLen = stream.getPos() - super.startPos;
        BytesTool.skip(stream, SIGNATURE_START.length());
        byte b1 = stream.readByte();
        byte b2 = stream.readByte();
        if (b1 == PDFStatics.WhiteSpace.CR && b2 == PDFStatics.WhiteSpace.LF) {
            this.signatureStart = new ASCIILine(SIGNATURE_START, 2);
        } else if (b1 == PDFStatics.WhiteSpace.CR || b1 == PDFStatics.WhiteSpace.LF) {
            this.signatureStart = new ASCIILine(SIGNATURE_START, 1);
            stream.backward(1);
        } else {
            this.signatureStart = new ASCIILine(SIGNATURE_START, 0);
            stream.backward(2);
        }

        // parse
        this.parseStartEnd(stream);

        // Furthur parse
        this.parseObject(stream);
    }

    private void parseStartEnd(PosDataInputStream stream) throws IOException {
        ASCIILine line;
        do {
            line = stream.readASCIILine();
            if (line.line.length() == SIGNATURE_END.length() && SIGNATURE_END.equalsIgnoreCase(line.line)) {
                super.length = stream.getPos() - super.startPos;
                this.signatureEnd = line;
                break;
            }
        } while (stream.getPos() < (stream.getBuf().length - 1));
    }

    private void parseObject(PosDataInputStream root) throws IOException, FileFormatException {
        PosDataInputStream stream = root.getPartialStream(super.startPos + this.numberLen + this.signatureStart.length(),
                super.length - this.numberLen - this.signatureStart.length() - this.signatureEnd.length());

        // Filter Stream Object First
        ASCIILine line;
        List<FileComponent> compStreams = new ArrayList<FileComponent>(2);

        while (stream.hasNext()) {
            line = stream.readASCIILine();
            if (line.line.endsWith(Stream.SIGNATURE_START)) {
                compStreams.add(new Stream(stream, line));
            }
        }

        // -- Calculate the lastIndex
        int lastIndex = super.startPos + super.length - this.signatureEnd.length();
        if (compStreams.size() > 0) {
            for (FileComponent comp : compStreams) {
                if (comp instanceof Stream) {
                    lastIndex = (lastIndex > comp.getStartPos()) ? comp.getStartPos() : lastIndex;
                }
            }
        }

        int pos = super.startPos + this.numberLen + this.signatureStart.length();
        int len = lastIndex - pos;
        stream = root.getPartialStream(pos, len);
        FileComponent comp;
        Analysis analysis = new Analysis();

        while (stream.hasNext()) {
            comp = analysis.parseNextObject(stream, this.components);
            if (comp == null) {
                // To Ensure Continue Analysis
                byte next1 = stream.readByte();
                // Show error message
                System.out.println(String.format("ERROR ======== IndirectObject.parseObject() - Object number = %d, Generation number = %d, Location = %d (%X), Value = %s",
                        this.objectNumber,
                        this.generationNumber,
                        stream.getPos(),
                        stream.getPos(),
                        String.valueOf((char) next1)));
            }
        } // End while

        // Add the stream object we've found earlier
        if (compStreams.size() > 0) {
            for (FileComponent compStream : compStreams) {
                this.components.add(compStream);
            }
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                String.format("Indirect Object: %d %d", this.objectNumber, this.generationNumber));
        nodeComp.setDescription(Texts.getString(Texts.PDF_INDIRECT_OBJECT));
        DefaultMutableTreeNode nodeIO = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.numberLen,
                String.format("Object Number = %d, Generation Number = %d", this.objectNumber, this.generationNumber))));
        pos += this.numberLen;

        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.signatureStart.line.length(),
                Texts.Signature + this.signatureStart.line)));
        pos += this.signatureStart.line.length();

        if (this.signatureStart.newLineLength > 0) {
            nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.signatureStart.newLineLength,
                    Texts.NewLine)));
            pos += this.signatureStart.newLineLength;
        }

        int contLen = super.length - this.numberLen - this.signatureStart.length() - this.signatureEnd.length();
        DefaultMutableTreeNode nodeContent;
        nodeIO.add(nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                contLen,
                "Indirect Object Content")));

        for (FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode) {
                ((GenerateTreeNode) comp).generateTreeNode(nodeContent);
            }
        }

        pos += contLen;
        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_END.length(),
                Texts.Signature + SIGNATURE_END)));
        pos += SIGNATURE_END.length();
        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.signatureEnd.newLineLength,
                Texts.NewLine)));

        parentNode.add(nodeIO);
    }
}
