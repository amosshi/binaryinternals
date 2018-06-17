/**
 * Chunk_tRNS.java    Apr 30, 2011, 23:36
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * The <span>tRNS</span> chunk specifies that the image uses simple
 * transparency: either alpha values associated with palette entries (for
 * indexed-color images) or a single transparent color (for gray scale and
 * true color images).
 * <p>
 * Although simple transparency is not as elegant as the full alpha channel,
 * it requires less storage space and is sufficient for many common cases.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_tRNS extends Chunk {

    public static final String CHUNK_TYPE_NAME = "tRNS";
    /**
     * A copy of {@link Chunk_IHDR:ColorType}.
     * 
     * @see Chunk_IHDR#ColorType
     */
    public final int ColorType;
    /**
     * The transparency value for color type 0 (gray scale).
     * <p>
     * For color type 0 (gray scale), the <span>tRNS</span> chunk contains a
     * single gray level value, stored in the format:
     * </p>
     * <pre>
     *   Gray:  2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * </pre>
     * <p>
     * (If the image bit depth is less than 16, the least significant bits are
     * used and the others are 0.)  Pixels of the specified gray level are to be
     * treated as transparent (equivalent to alpha value 0); all other pixels
     * are to be treated as fully opaque
     * (alpha value <code>2<sup>bitdepth</sup>-1</code>).
     * </p>
     * <p>
     * This field is <code>-1</code> when the color type is not 0.
     * </p>
     */
    public final int Gray;
    /**
     * The transparency value for color type 2 (true color).
     * <p>For color type 2 (true color), the <span>tRNS</span> chunk contains a
     * single RGB color value, stored in the format:
     * </p>
     * <pre>
     *   Red:   2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     *   Green: 2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     *   Blue:  2 bytes, range <code>0 .. (2^bitdepth)-1</code>
     * </pre>
     * <p>
     * (If the image bit depth is less than 16, the least significant bits are
     * used and the others are 0.)  Pixels of the specified color value are to
     * be treated as transparent (equivalent to alpha value 0); all other pixels
     * are to be treated as fully opaque (alpha value
     * <code>2<sup>bitdepth</sup>-1</code>).
     * </p>
     * <p>
     * This field is <code>-1</code> when the color type is not 2.
     * </p>
     */
    public final int Red;
    /**
     * Refer to {@link #Red}.
     *
     * @see #Red
     */
    public final int Green;
    /**
     * Refer to {@link #Red}.
     *
     * @see #Red
     */
    public final int Blue;
    /**
     * The transparency value for color type 3 (indexed color).
     * <p>
     * For color type 3 (indexed color), the <span>tRNS</span> chunk contains
     * a series of one-byte alpha values, corresponding to entries in the
     * <span>PLTE</span> chunk:
     * <pre>
     *    Alpha for palette index 0:  1 byte
     *    Alpha for palette index 1:  1 byte
     *    ...etc...
     * </pre>
     * </p>
     * <p>
     * Each entry indicates that pixels of the corresponding palette index must
     * be treated as having the specified alpha value.
     * Alpha values have the same interpretation as in an 8-bit full alpha
     * channel: 0 is fully transparent, 255 is fully opaque, regardless of image
     * bit depth.
     * The <span>tRNS</span> chunk must not contain more alpha values than there
     * are palette entries, but <span>tRNS</span> <em>can contain fewer values
     * than there are palette entries</em>. In this case, the alpha value for
     * all remaining palette entries is assumed to be 255. In the common case in
     * which only palette index 0 need be made transparent, only a one-byte
     * <span>tRNS</span> chunk is needed.
     * </p>
     * <p>
     * This field is <code>nul</code> when the color type is not 3.
     * </p>
     */
    public final int[] Alpha;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_tRNS(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        this.ColorType = this.getColorType(png);
        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        switch (this.ColorType) {
            case 0:
                this.Gray = chunkDataStream.readUnsignedShort();
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = null;
                break;
            case 2:
                this.Gray = -1;
                this.Red = chunkDataStream.readUnsignedShort();
                this.Green = chunkDataStream.readUnsignedShort();
                this.Blue = chunkDataStream.readUnsignedShort();
                this.Alpha = null;
                break;
            case 3:
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = new int[this.Length];
                for (int i = 0; i < this.Length; i++) {
                    this.Alpha[i] = chunkDataStream.read();
                }
                break;
            default:
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = null;
        }
    }

    private int getColorType(PNGFile png) {
        int colorType = -1;
        for (FileComponent chunk : png.getFileComponents()) {
            if (chunk instanceof Chunk_IHDR) {
                colorType = ((Chunk_IHDR) chunk).ColorType;
                break;
            }
        }

        return colorType;
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        // Color type 0 - Gray Scale
        if (this.Gray != -1) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    2,
                    String.format("Gray = %d", this.Gray))));
        }

        // Color Type 2 - True Color
        if (this.Red != -1) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    2,
                    String.format("Red = %d", this.Red))));
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += 2,
                    2,
                    String.format("Green = %d", this.Green))));
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += 2,
                    2,
                    String.format("Blue = %d", this.Blue))));
        }

        // Color Type 3 - Indexed Color
        if (this.Alpha != null) {
            for (int i = 0; i < this.Alpha.length; i++) {
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("Alpha[%d] = %d", i, this.Alpha[i]))));
                start++;
            }
        }
    }
}
