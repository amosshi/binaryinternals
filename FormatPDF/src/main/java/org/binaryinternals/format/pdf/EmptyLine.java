package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;
import org.binaryinternals.commonlib.PosDataInputStream;

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
                this.Line.line().length(),
                "Content")));
        nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Line.line().length(),
                this.Line.newLineLength(),
                Texts.NewLine)));
        parentNode.add(nodeEL);
    }
}
