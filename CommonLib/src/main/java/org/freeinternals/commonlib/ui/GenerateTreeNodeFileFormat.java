/*
 * GenerateTreeNodeFileFormat.java    August 29, 2021, 23:44
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.util.ResourceBundle;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;

/**
 * Interface for generating children tree nodes.
 *
 * @author Amos Shi
 */
public interface GenerateTreeNodeFileFormat {

    static final String FORMAT_STRING_STRING = "%s - %s";

    /**
     * Max length for tree node string.
     *
     * @see #left(String)
     */
    static final int TREENODE_STRING_MAXLEN = 64;


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

    default DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String fieldName, Object value, String msgkey, Icons icon) {
        JTreeNodeFileComponent fileComp = new JTreeNodeFileComponent(
                startPos,
                len,
                fieldName + ": " + value.toString()
        );
        if (msgkey != null) {
            fileComp.setDescription(getMessages().getString(msgkey));
        }
        if (icon != null) {
            fileComp.setIcon(icon.getIcon());
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileComp);
        parentNode.add(node);
        return node;
    }


    /**
     * Get left part of string for tree node.
     *
     * @param s The String to get left part
     * @return Left part of string
     */
    default String left(String s) {
        return left(s, TREENODE_STRING_MAXLEN);
    }

    /**
     * Get left part of string.
     *
     * @param s The String to get left part
     * @param length Length to get
     * @return Left part of string
     */
    default String left(String s, int length) {
        if (s == null) {
            return s;
        } else if (s.length() < length) {
            return s;
        } else {
            return s.substring(0, length - 1) + " ...";
        }
    }
}
