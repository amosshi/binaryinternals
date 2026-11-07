/*
 * Chunk_hIST.java    May 02, 2011, 11:33
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `hIST` chunk gives the approximate usage frequency of each color in the
/// color palette.
///
/// A `hIST` chunk can appear only when a `PLTE` chunk appears. If a viewer is
/// unable to provide all the colors listed in the palette, the histogram may
/// help it decide how to choose a subset of the colors for display.
///
/// The `hIST` chunk contains a series of 2-byte (16 bit) unsigned integers.
/// There must be exactly one entry for each entry in the `PLTE` chunk. Each
/// entry is proportional to the fraction of pixels in the image that have that
/// palette index; the exact scale factor is chosen by the encoder.
///
/// @author Amos Shi
public final class Chunk_hIST extends Chunk {

    public static final String CHUNK_TYPE_NAME = "hIST";
    /// One frequency per palette entry, or `null` when the chunk is empty.
    public final int[] Frequency;

    Chunk_hIST(final PosDataInputStream stream) throws IOException {
        super(stream);

        final int count = this.Length / 2;
        if (count > 0) {
            final PosDataInputStream chunkDataStream = this.getChunkDataStream();
            this.Frequency = new int[count];
            for (int i = 0; i < count; i++) {
                this.Frequency[i] = chunkDataStream.readUnsignedShort();
            }
        } else {
            this.Frequency = null;
        }
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        if (this.Frequency == null) {
            return;
        }

        final int start = this.getDataStartPos();
        for (int i = 0; i < this.Frequency.length; i++) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 2 * i,
                    2,
                    "Frequency [%d] = %d".formatted(i, this.Frequency[i]))));
        }
    }
}
