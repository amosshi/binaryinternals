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
 * An empty line in the PDF file.
 *
 * @author Amos Shi
 */
public class EmptyLine extends FileComponent implements GenerateTreeNode {

    /**
     * The content of current line.
     */
    public final ASCIILine Line;

    EmptyLine(PosDataInputStream stream, ASCIILine line) throws IOException, FileFormatException {
        super.startPos = stream.getPos() - line.Length();
        super.length = line.Length();
        this.Line = line;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Empty Line");
        DefaultMutableTreeNode nodeEL = new DefaultMutableTreeNode(nodeComp);
        nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                this.Line.Line.length(),
                "Content")));
        nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Line.Line.length(),
                this.Line.NewLineLength,
                Texts.NewLine)));
        parentNode.add(nodeEL);
    }
}
