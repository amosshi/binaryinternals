/*
 * GenerateClassfileTreeNode.java    September 07, 2019, 21:22
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.classfile;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Amos Shi
 */
public interface GenerateClassfileTreeNode {

    void generateTreeNode(final DefaultMutableTreeNode parentNode, ClassFile classFile);

}
