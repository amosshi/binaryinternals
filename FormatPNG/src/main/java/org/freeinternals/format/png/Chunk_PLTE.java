/**
 * Chunk_PLTE .java    May 01, 2011, 15:04
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 * The <span>PLTE</span> chunk contains from 1 to 256 palette entries.
 * <p>
 * Each entry is a three-byte series of the form:
 * </p>
 * <pre>
 * Red:   1 byte (0 = black, 255 = red)
 * Green: 1 byte (0 = black, 255 = green)
 * Blue:  1 byte (0 = black, 255 = blue)
 * </pre>
 * <p>
 * The number of entries is determined from the chunk length.
 * A chunk length not divisible by 3 is an error.
 * </p>
 * 
 * @author Amos Shi
 */
public class Chunk_PLTE extends Chunk {

    public static final String CHUNK_TYPE_NAME = "PLTE";
    public final PaletteEntry[] Palette;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_PLTE(PosDataInputStream stream, PNGFile png) throws IOException, FileFormatException {
        super(stream, png);

        // A chunk length not divisible by 3 is an error.
        if ((this.Length % 3) != 0) {
            throw new FileFormatException(
                    String.format("The chunk data lengh cannot be divisible by 3. value = %d", this.Length));
        }

        // Palete entry number should be >= 1.
        int count = this.Length / 3;
        if (count < 1) {
            throw new FileFormatException(
                    String.format("The palette entry is invalid. count = %d", count));
        }

        this.Palette = new PaletteEntry[count];
        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        for (int i = 0; i < this.Palette.length; i++) {
            this.Palette[i] = new PaletteEntry(
                    chunkDataStream.read(),
                    chunkDataStream.read(),
                    chunkDataStream.read());
        }
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;
        DefaultMutableTreeNode node;

        for (int i = 0; i < this.Palette.length; i++) {
            parent.add(node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    3,
                    String.format("Palette entry [%d] = [%03d, %03d, %03d]",
                    i,
                    this.Palette[i].Red,
                    this.Palette[i].Green,
                    this.Palette[i].Blue))));

            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    1,
                    String.format("Red = %03d", this.Palette[i].Red))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 1,
                    1,
                    String.format("Green = %03d", this.Palette[i].Green))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 2,
                    1,
                    String.format("Blue = %03d", this.Palette[i].Blue))));
            start += 3;
        }
    }

    public class PaletteEntry {

        public final int Red;
        public final int Green;
        public final int Blue;

        public PaletteEntry(int r, int g, int b) {
            this.Red = r;
            this.Green = g;
            this.Blue = b;
        }
    }
}
