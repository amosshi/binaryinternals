package org.binaryinternals.format.pdf;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// An empty line, or a line not recognized, in the PDF file.
///
/// @author Amos Shi
public final class EmptyLine extends FileComponent implements GenerateTreeNode {

    /// The content of current line.
    public final ASCIILine Line;

    EmptyLine(final PosDataInputStream stream, final ASCIILine line) {
        super(stream.getPos() - line.length(), line.length());
        this.Line = line;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeEL = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Empty Line"));
        if (!this.Line.line().isEmpty()) {
            nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos,
                    this.Line.line().length(),
                    "Content")));
        }
        if (this.Line.newLineLength() > 0) {
            nodeEL.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + this.Line.line().length(),
                    this.Line.newLineLength(),
                    Texts.NewLine)));
        }
        parentNode.add(nodeEL);
    }
}
