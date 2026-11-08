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

    /// The pixel array, limited to the bytes in the file.
    ///
    /// @param dibHeader The DIB header
    /// @param startPos Start of the pixel array
    /// @param available Bytes in the file from `startPos`
    PixelArray(final DIBHeader dibHeader, final int startPos, final int available) {
        // The header may claim more data than the file has
        super(startPos, Math.clamp(dibHeader.calcImageSize(), 0, Math.max(available, 0)));
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
            // A top-down bitmap has a negative height, and the header may claim
            // more rows than the file has
            final long rowSize = this.dibHeader.getRowSize();
            final long rows = (rowSize > 0) ? Math.min(Math.abs((long) this.dibHeader.getHeight()), this.length / rowSize) : 0;
            for (int i = 0; i < rows; i++) {
                node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.startPos + (int) (rowSize * i),
                        (int) rowSize,                                      // At most this.length, when there is a row
                        "row [%d]".formatted(i))));
            }
        }
    }
}
