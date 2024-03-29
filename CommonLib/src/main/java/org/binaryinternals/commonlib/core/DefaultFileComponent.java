package org.binaryinternals.commonlib.core;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * Default implementation for {@link FileComponent}.
 *
 * @author Amos Shi
 */
public class DefaultFileComponent extends FileComponent implements GenerateTreeNode {

    private String treeNodeText = "Default File Component";

    /**
     * Generates the {@link DefaultFileComponent}.
     *
     * @param start Start Position of the File component
     * @param len Length of the File component
     */
    public DefaultFileComponent(final int start, final int len) {
        super.startPos = start;
        super.length = len;
    }

    /**
     * Generates the {@link DefaultFileComponent}.
     *
     * @param start Start Position of the File component
     * @param len Length of the File component
     * @param text Text of the Tree Node
     */
    public DefaultFileComponent(final int start, final int len, final String text) {
        this(start, len);
        this.treeNodeText = text;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.treeNodeText)));
    }
}
