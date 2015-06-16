/**
 * Chunk_bKGD.java    May 01, 2011, 22:37
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
 * The <span>bKGD</span> chunk specifies a default background color to present
 * the image against.
 * <p>
 * Note that viewers are not bound to honor this chunk;
 * a viewer can choose to use a different background.
 * </p>
 * <p>
 * Note: For color type 3 (indexed color), only the field
 * <code>PaletteIndex</code> is valid.
 * For color types 0 and 4 (gray scale, with or without alpha), only the field
 * <code>Gray</code> is valid.
 * For color types 2 and 6 (true color, with or without alpha), only the fields
 * <code>Red</code>, <code>Green</code> & <code>Blue</code> are valid.
 * </p>
 * <p>
 * When a field is not valid, the value will be set as <code>-1</code>.
 * </p>
 * 
 * @author Amos Shi
 */
public class Chunk_bKGD extends Chunk {

    public static final String CHUNK_TYPE_NAME = "bKGD";

    /**
     * The value is the palette index of the color to be used as background.
     * <p>
     * For color type 3 (indexed color), the <span>bKGD</span> chunk contains:
     * </p>
     * <pre>
     * Palette index:  1 byte
     * </pre>
     */
    public final int PaletteIndex;
    /**
     * The value is the gray level to be used as background.
     * <p>
     * For color types 0 and 4 (gray scale, with or without alpha),
     * <span>bKGD</span> contains:
     * </p>
     * <pre>
     * Gray:  2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * </pre>
     * <p>
     * If the image bit depth is less than 16, the least significant bits
     * are used and the others are 0.
     * </p>
     */
    public final int Gray;
    /**
     * This is the RGB color to be used as background.
     * <p>
     * For color types 2 and 6 (true color, with or without alpha),
     * <span>bKGD</span> contains:
     * </p>
     * <pre>
     * Red:   2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * Green: 2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * Blue:  2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * </pre>
     * <p>
     * If the image bit depth is less than 16, the least significant bits are
     * used and the others are 0.
     * </p>
     */
    public final int Red;
    /**
     * Refer to field <code>Red</code>.
     *
     * @see Chunk_bKGD#Red
     */
    public final int Green;
    /**
     * Refer to field <code>Red</code>.
     *
     * @see Chunk_bKGD#Red
     */
    public final int Blue;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_bKGD(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        switch(this.ChunkData.length) {
            case 1:
                this.PaletteIndex = chunkDataStream.read();
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                break;
            case 2:
                this.PaletteIndex = -1;
                this.Gray = chunkDataStream.readShort();
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                break;
            case 6:
                this.PaletteIndex = -1;
                this.Gray = -1;
                this.Red = chunkDataStream.readShort();
                this.Green = chunkDataStream.readShort();
                this.Blue = chunkDataStream.readShort();
                break;
            default:
                // Un-recognized
                this.PaletteIndex = -1;
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                break;
        }
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        switch (this.ChunkData.length) {
            case 1:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("Palette index = %d", this.PaletteIndex))));
                break;

            case 2:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        2,
                        String.format("Gray = %d", this.Gray))));
                break;

            case 6:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        2,
                        String.format("Red = %d", this.Red))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 2,
                        2,
                        String.format("Green = %d", this.Green))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 4,
                        2,
                        String.format("Blue = %d", this.Blue))));
                break;

            default:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        this.ChunkData.length,
                        "Un-recognized")));
        }
    }
}
