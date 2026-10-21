/*
 * GenerateTreeNode.java    September 01, 2010, 23:57
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib;

import javax.swing.tree.DefaultMutableTreeNode;

/// Interface for generating children tree nodes.
///
/// @author Amos Shi
public interface GenerateTreeNode {

    /// Generate children nodes for current file component.
    ///
    /// @param parentNode Parent tree node
    void generateTreeNode(final DefaultMutableTreeNode parentNode);

    /// Generate tree node for difference with Raw Data.
    ///
    /// @param parentNode Parent tree node
    /// @param lastEnd Last end
    /// @param diff Difference
    /// @param buff Byte array data
    /// @param buffStartPos Buffer start position
    default void generateTreeNodeDiff(
            final DefaultMutableTreeNode parentNode,
            final int lastEnd,
            final int diff,
            final byte[] buff, final int buffStartPos) {
        final String diffStr;

        if (BytesTool.isByteArrayEmpty(buff, lastEnd - buffStartPos, diff)) {
            diffStr = "Empty [0x%04X, 0x%04X] length = %d".formatted(lastEnd, lastEnd + diff - 1, diff);
        } else {
            diffStr = "Unknown [0x%04X, 0x%04X] length = %d".formatted(lastEnd, lastEnd + diff - 1, diff);
        }
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastEnd,
                diff,
                diffStr)));
    }

    /// Generate tree node for Gap.
    ///
    /// @param parentNode Parent tree node
    /// @param startPos Start position of the gap
    /// @param length Length of the gap
    default void generateTreeNodeGap(final DefaultMutableTreeNode parentNode, final int startPos, final int length) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                length,
                "Gap [%08X, %08X] length = %d".formatted(startPos, startPos + length, length))));
    }
}
