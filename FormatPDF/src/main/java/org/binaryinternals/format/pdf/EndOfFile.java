package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 * PDF End of File (
 * <code>EOF</code>) object, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.5.5</code>: File Trailer.
 *
 * @author Amos Shi
 */
public class EndOfFile extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "%%EOF";
    /**
     * The content of current line.
     */
    public final ASCIILine Line;

    EndOfFile(PosDataInputStream stream, ASCIILine line) throws IOException {
        super.startPos = stream.getPos() - line.length();
        super.length = line.length();
        this.Line = line;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "End of File");
        DefaultMutableTreeNode nodeEoF = new DefaultMutableTreeNode(nodeComp);

        nodeEoF.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                this.Line.line().length(),
                Texts.Signature + SIGNATURE)));
        nodeEoF.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Line.line().length(),
                this.Line.newLineLength(),
                Texts.NewLine)));
        parentNode.add(nodeEoF);
    }
}
