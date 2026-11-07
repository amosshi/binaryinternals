/*
 * ColorTable.java    Dec 01, 2010, 22:59
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.bmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The color table, or palette, listing the colors used by the image.
///
/// @author Amos Shi
public final class ColorTable extends FileComponent implements GenerateTreeNode {

    /// Maximum bits per pixel that uses a palette by default.
    private static final int PALETTE_BIT_COUNT_MAX = 8;

    /// The palette entries, [PixelBGR] for `BITMAPCOREHEADER`, else [PixelRGBA].
    public final List<Pixel> palette;

    /// Read the color table.
    ///
    /// @param input Stream positioned after the DIB header
    /// @param dibHeader The DIB header
    /// @param pixelArrayOffset Where the pixel data starts; the table never reads past it
    ColorTable(final PosDataInputStream input, final DIBHeader dibHeader, final long pixelArrayOffset) throws IOException {
        this.startPos = input.getPos();

        final boolean core = dibHeader.data instanceof DIBHeader.BITMAPCOREHEADER;
        final int entrySize = core ? PixelBGR.LENGTH : PixelRGBA.LENGTH;
        final long tableEnd = Math.min(pixelArrayOffset, input.getBuf().length);
        final int count = (int) Math.min(entryCount(dibHeader), Math.max(0, (tableEnd - this.startPos) / entrySize));

        final List<Pixel> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(core ? new PixelBGR(input) : new PixelRGBA(input));
        }
        this.palette = Collections.unmodifiableList(entries);
        this.length = count * entrySize;
    }

    /// The `ColorsUsed` of the header, or the default of 2<sup>n</sup> colors
    /// for up to 8 bits per pixel.
    private static long entryCount(final DIBHeader dibHeader) {
        final long colorsUsed = dibHeader.getColorsUsed();
        if (colorsUsed > 0) {
            return colorsUsed;
        }
        final int bpp = dibHeader.getBitCount();
        return (bpp >= 1 && bpp <= PALETTE_BIT_COUNT_MAX) ? 1L << bpp : 0;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        if (this.palette.isEmpty()) {
            return;
        }

        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Color Table [0x%08X, %d]".formatted(this.startPos, this.length),
                "The Color Table is a block of bytes (a table) listing the colors used by the image."));
        parentNode.add(node);

        for (int i = 0; i < this.palette.size(); i++) {
            final Pixel pixel = this.palette.get(i);
            final DefaultMutableTreeNode nodePixel = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pixel.getStartPos(),
                    pixel.getLength(),
                    "%s [%d]".formatted(pixel.getClass().getSimpleName(), i)));
            node.add(nodePixel);
            pixel.generateTreeNode(nodePixel);
        }
    }
}
