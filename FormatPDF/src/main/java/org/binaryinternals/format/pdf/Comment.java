package org.binaryinternals.format.pdf;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF Comment, see `PDF 32000-1:2008` `7.2.3`: Comments.
///
/// @author Amos Shi
public final class Comment extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "%";
    /// Comment text.
    public final String Text;

    Comment(final PosDataInputStream stream, final ASCIILine line) {
        super(stream.getPos() - line.length(), line.length());
        this.Text = line.line();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeComment = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Comment"));
        nodeComment.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                this.Text.length(),
                "Text")));
        if (super.length > this.Text.length()) {
            nodeComment.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + this.Text.length(),
                    super.length - this.Text.length(),
                    Texts.NewLine)));
        }
        parentNode.add(nodeComment);
    }
}
