/*
 * Chunk_PLTE.java    May 01, 2011, 15:04
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `PLTE` chunk contains from 1 to 256 palette entries.
///
/// Each entry is a three-byte series of the form:
///
/// ```
/// Red:   1 byte (0 = black, 255 = red)
/// Green: 1 byte (0 = black, 255 = green)
/// Blue:  1 byte (0 = black, 255 = blue)
/// ```
///
/// The number of entries is determined from the chunk length.
/// A chunk length not divisible by 3 is an error.
///
/// @author Amos Shi
public final class Chunk_PLTE extends Chunk {

    /// Chunk type of the palette chunk.
    public static final String CHUNK_TYPE_NAME = "PLTE";
    /// The palette entries, 3 bytes each.
    public final PaletteEntry[] Palette;

    Chunk_PLTE(final PosDataInputStream stream) throws IOException {
        super(stream);

        // A chunk length not divisible by 3 is an error.
        if ((this.Length % 3) != 0) {
            throw new FileFormatException(
                    "The chunk data length is not divisible by 3. value = %d".formatted(this.Length));
        }

        // Palette entry number should be >= 1.
        final int count = this.Length / 3;
        if (count < 1) {
            throw new FileFormatException("The palette entry is invalid. count = %d".formatted(count));
        }

        this.Palette = new PaletteEntry[count];
        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        for (int i = 0; i < this.Palette.length; i++) {
            this.Palette[i] = new PaletteEntry(
                    chunkDataStream.read(),
                    chunkDataStream.read(),
                    chunkDataStream.read());
        }
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        int start = this.getDataStartPos();

        for (int i = 0; i < this.Palette.length; i++) {
            final PaletteEntry entry = this.Palette[i];
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    3,
                    "Palette entry [%d] = [%03d, %03d, %03d]".formatted(i, entry.red(), entry.green(), entry.blue())));
            parent.add(node);

            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    1,
                    "Red = %03d".formatted(entry.red()))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 1,
                    1,
                    "Green = %03d".formatted(entry.green()))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 2,
                    1,
                    "Blue = %03d".formatted(entry.blue()))));
            start += 3;
        }
    }

    /// One palette entry.
    ///
    /// @param red   Red, 0 = black, 255 = red
    /// @param green Green, 0 = black, 255 = green
    /// @param blue  Blue, 0 = black, 255 = blue
    public record PaletteEntry(int red, int green, int blue) {
    }
}
