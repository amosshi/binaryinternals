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
        super.startPos = stream.getPos() - line.length();
        super.length = line.length();
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
                this.Line.line.length(),
                "Content")));
        nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Line.line.length(),
                this.Line.newLineLength,
                Texts.NewLine)));
        parentNode.add(nodeEL);
    }
}
