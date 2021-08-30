/*
 * GenerateTreeNodeFileFormat.java    August 29, 2021, 23:44
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;

/**
 * Interface for generating children tree nodes.
 *
 * @author Amos Shi
 */
public interface GenerateTreeNodeFileFormat {

    void generateTreeNode(final DefaultMutableTreeNode parentNode, FileFormat fileFormat);

    /**
     * Get the messages resource of current file format.
     *
     * @return {@link ResourceBundle} for messages, or <code>null</code> if not
     * exist
     */
    ResourceBundle getMessages();

    default DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value) {
        return addNode(parentNode, startPos, len, name, value, null, null);
    }

    default DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String fieldName, Object value, String msgkey, Icon icon) {
        JTreeNodeFileComponent fileComp = new JTreeNodeFileComponent(
                startPos,
                len,
                fieldName + ": " + value.toString()
        );
        if (msgkey != null) {
            fileComp.setDescription(getMessages().getString(msgkey));
        }
        fileComp.setIcon(icon);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileComp);
        parentNode.add(node);
        return node;
    }
}
