package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.pdf.Texts;

/**
 * PDF basic object Stream, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.8</code>: Stream Objects.
 *
 * @author Amos Shi
 */
public class Stream extends FileComponent implements GenerateTreeNode {

    public static final String SIGNATURE_START = "stream";
    public static final String SIGNATURE_END = "endstream";
    /**
     * The signature length.
     */
    public final int SignatureLen;
    public ASCIILine SignatureEnd = null;

    public Stream(PosDataInputStream stream, PosDataInputStream.ASCIILine line) throws IOException, FileFormatException {
        this.SignatureLen = SIGNATURE_START.length() + line.NewLineLength;
        super.startPos = stream.getPos() - this.SignatureLen;
        this.parse(stream);
        this.length = stream.getPos() - super.startPos;
    }

    private void parse(PosDataInputStream stream) throws IOException, FileFormatException {
        ASCIILine line;
        while (stream.hasNext()) {
            line = stream.readASCIILine();
            if (line.Line.endsWith(Stream.SIGNATURE_END)) {
                this.SignatureEnd = line;
            }
        }

        if (this.SignatureEnd == null) {
            throw new FileFormatException(String.format(
                    "The 'endstream' tag is not found for current object. Object Start Offset = %d, Current Offset = %d",
                    super.startPos,
                    stream.getPos()));
        }
    }

    /**
     * Get the start position of the Encoded Stream Content.
     */
    public int getStreamStartPos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the Encoded Stream Content Length of current {@link Stream} object.
     * <p> The content length does not include the signature
     * <code>stream</code> or
     * <code>endstream</code>. </p>
     */
    public int getStreamLength() {
        return this.length - this.SignatureLen - 1 - this.SignatureEnd.Length();
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {

        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Stream");
        DefaultMutableTreeNode nodeStream = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_START.length(),
                Texts.Signature + SIGNATURE_START)));
        pos += SIGNATURE_START.length();

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.SignatureLen - SIGNATURE_START.length(),
                Texts.NewLine)));
        pos += (this.SignatureLen - SIGNATURE_START.length());

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.getStreamLength(),
                "Stream Content: Length = " + this.getStreamLength())));
        pos += this.getStreamLength();

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.NewLine)));
        pos += 1;

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.SignatureEnd.Line.length(),
                Texts.Signature + SIGNATURE_END)));
        pos += this.SignatureEnd.Line.length();

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.SignatureEnd.NewLineLength,
                Texts.NewLine)));

        parentNode.add(nodeStream);
    }
}
