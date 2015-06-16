package org.freeinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

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
        super.startPos = stream.getPos() - line.Length();
        this.parse(stream);
        super.length = stream.getPos() - super.startPos;
    }

    private void parse(PosDataInputStream stream) throws IOException, FileFormatException {
        ASCIILine line;
        do {
            line = stream.readASCIILine();
            if (StartXRef.SIGNATURE.equalsIgnoreCase(line.Line)) {
                stream.backward(line.Length());
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
                this.HeaderLine.Line.length(),
                Texts.Signature + SIGNATURE)));
        pos += this.HeaderLine.Line.length();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.NewLineLength,
                Texts.NewLine)));
        pos += this.HeaderLine.NewLineLength;
        int len = super.length - this.HeaderLine.Length();
        if (len > 0) {
            nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    len,
                    "Trailer Content")));
        }

    }
}
