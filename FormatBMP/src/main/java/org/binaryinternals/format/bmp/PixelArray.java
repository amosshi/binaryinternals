/*
 * PixelArray.java    Dec 03, 2010, 22:19
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.bmp;

import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.format.bmp.DIBHeader.CompressionMethod;

/// The pixels of the image, row by row.
///
/// @author Amos Shi
public final class PixelArray extends FileComponent implements GenerateTreeNode {

    /// Bits per pixel whose rows are shown.
    private static final Set<Integer> ROW_BIT_COUNTS = Set.of(1, 4, 8, 16, 24, 32);

    private final DIBHeader dibHeader;

    PixelArray(final DIBHeader dibHeader, final int startPos) {
        super(startPos, Math.toIntExact(dibHeader.calcImageSize()));
        this.dibHeader = dibHeader;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Pixel Array [0x%08X, %d]".formatted(this.startPos, this.length),
                "The pixel format is defined by the DIB Header. <br/>Each row in the Pixel Array is padded to a multiple of 4 bytes in size."));
        parentNode.add(node);

        if (this.dibHeader.getCompressionMethod() != CompressionMethod.BI_RGB.value) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos,
                    this.length,
                    "Compressed Data")));
        } else if (ROW_BIT_COUNTS.contains(this.dibHeader.getBitCount())) {
            // A top-down bitmap has a negative height
            final int rowSize = Math.toIntExact(this.dibHeader.getRowSize());
            final int rows = Math.abs(this.dibHeader.getHeight());
            for (int i = 0; i < rows; i++) {
                node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.startPos + rowSize * i,
                        rowSize,
                        "row [%d]".formatted(i))));
            }
        }
    }
}
