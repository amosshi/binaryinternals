/*
 * GenerateTreeNode.java    September 01, 2010, 23:57
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;

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
    void generateTreeNode(final DefaultMutableTreeNode parentNode);

    /**
     * Generate tree node for difference with Raw Data.
     *
     * @param parentNode Parent tree node
     * @param lastEnd Last end
     * @param diff Difference
     * @param buff Byte array data
     * @param buffStartPos Buffer start position
     */
    default void generateTreeNodeDiff(
            final DefaultMutableTreeNode parentNode,
            final int lastEnd,
            final int diff,
            final byte[] buff, final int buffStartPos) {
        String diffStr;

        if (BytesTool.isByteArrayEmpty(buff, lastEnd - buffStartPos, diff - 1)) {
            diffStr = String.format("Empty [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        } else {
            diffStr = String.format("Unknown [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        }
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastEnd,
                diff,
                diffStr)));
    }

    /**
     * Generate tree node for Gap.
     *
     * @param parentNode Parent tree node
     * @param startPos Start position of the gap
     * @param legnth Length of the gap
     */
    default void generateTreeNodeGap(final DefaultMutableTreeNode parentNode, final int startPos, final int legnth) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                legnth,
                String.format("Gap [%08X, %08X] length = %d", startPos, startPos + legnth, legnth))));
    }
}
