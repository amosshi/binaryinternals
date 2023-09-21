package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.core.FileFormatException;

/**
 * PDF File Trailer, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.5.5</code>: File Trailer.
 *
 * @author Amos Shi
 */
public class Trailer extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "trailer";
    /**
     * The first line of current object.
     */
    public final ASCIILine HeaderLine;

    Trailer(PosDataInputStream stream, ASCIILine line) throws IOException, FileFormatException {
        this.HeaderLine = line;
        super.startPos = stream.getPos() - line.length();
        this.parse(stream);
        super.length = stream.getPos() - super.startPos;
    }

    private void parse(PosDataInputStream stream) throws IOException, FileFormatException {
        ASCIILine line;
        do {
            line = stream.readASCIILine();
            if (StartXRef.SIGNATURE.equalsIgnoreCase(line.line)) {
                stream.backward(line.length());
                break;
            }
        } while (stream.hasNext());
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Trailer");
        DefaultMutableTreeNode nodeTrailer = new DefaultMutableTreeNode(nodeComp);
        parentNode.add(nodeTrailer);

        int pos = this.startPos;
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.line.length(),
                Texts.Signature + SIGNATURE)));
        pos += this.HeaderLine.line.length();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.newLineLength,
                Texts.NewLine)));
        pos += this.HeaderLine.newLineLength;
        int len = super.length - this.HeaderLine.length();
        if (len > 0) {
            nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    len,
                    "Trailer Content")));
        }

    }
}
