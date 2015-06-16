package org.freeinternals.commonlib.util;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
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
    public DefaultFileComponent(int start, int len) {
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
    public DefaultFileComponent(int start, int len, String text) {
        this(start, len);
        this.treeNodeText = text;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.treeNodeText)));
    }
}
