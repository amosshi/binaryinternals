/*
 * JTreeJPEGFile.java    September 01, 2010, 23:57
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Interface for generating children tree nodes.
 *
 * @author Amos Shi
 */
public interface GenerateTreeNode {

    /**
     * Generate children nodes for current file component.
     *
     * @param parentNode Parent tree node
     */
    void generateTreeNode(DefaultMutableTreeNode parentNode);

}
