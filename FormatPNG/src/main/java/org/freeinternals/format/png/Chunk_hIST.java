/**
 * Chunk_hIST.java    May 02, 2011, 11:33
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * The <span>hIST</span> chunk gives the approximate usage frequency of each
 * color in the color palette.
 * <p>
 * A <span>hIST</span> chunk can appear only when a <span>PLTE</span> chunk
 * appears. If a viewer is unable to provide all the colors listed in the
 * palette, the histogram may help it decide how to choose a subset of the
 * colors for display.
 * </p>
 * <p>
 * The <span>hIST</span> chunk contains a series of 2-byte (16 bit) unsigned
 * integers. There must be exactly one entry for each entry in the
 * <span>PLTE</span> chunk. Each entry is proportional to the fraction of pixels
 * in the image that have that palette index; the exact scale factor is chosen
 * by the encoder.
 * </p>
 * 
 * @author Amos Shi
 */
public class Chunk_hIST extends Chunk {

    public static final String CHUNK_TYPE_NAME = "hIST";
    public final int[] Frequency;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_hIST(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        int count = this.Length / 2;

        if (count > 0) {
            PosDataInputStream chunkDataStream = super.getChunkDataStream();
            this.Frequency = new int[count];
            for (int i = 0; i < count; i++) {
                this.Frequency[i] = chunkDataStream.readUnsignedShort();
            }
        } else {
            this.Frequency = null;
        }
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        if (this.Frequency == null) {
            return;
        }

        for (int i = 0; i < this.Frequency.length; i++) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    2,
                    String.format("Frequency [%d] = %d", i, this.Frequency[i]))));
            start += 2;
        }
    }
}
