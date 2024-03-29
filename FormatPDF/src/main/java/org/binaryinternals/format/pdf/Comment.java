package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * PDF Comment, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.2.3</code>: Comments.
 *
 * @author Amos Shi
 */
public class Comment extends FileComponent implements GenerateTreeNode {

    /**
     * Comment text.
     */
    public final String Text;

    Comment(PosDataInputStream stream, ASCIILine line) throws IOException {
        super.startPos = stream.getPos() - line.length();
        super.length = line.length();
        this.Text = line.line;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Comment");
        DefaultMutableTreeNode nodeComment = new DefaultMutableTreeNode(nodeComp);
        nodeComment.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                this.Text.length(),
                "Text")));
        nodeComment.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + this.Text.length(),
                super.length - this.Text.length(),
                "New Line")));
        parentNode.add(nodeComment);
    }
}
