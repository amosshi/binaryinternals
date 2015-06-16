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

    EndOfFile(PosDataInputStream stream, ASCIILine line) throws IOException, FileFormatException {
        super.startPos = stream.getPos() - line.Length();
        super.length = line.Length();
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
                this.Line.Line.length(),
                Texts.Signature + SIGNATURE)));
        nodeEoF.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Line.Line.length(),
                this.Line.NewLineLength,
                Texts.NewLine)));
        parentNode.add(nodeEoF);
    }
}
