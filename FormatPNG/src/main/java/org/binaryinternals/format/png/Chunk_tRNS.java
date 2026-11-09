/*
 * Chunk_tRNS.java    Apr 30, 2011, 23:36
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `tRNS` chunk specifies that the image uses simple transparency: either
/// alpha values associated with palette entries (for indexed-color images) or a
/// single transparent color (for gray scale and true color images).
///
/// Although simple transparency is not as elegant as the full alpha channel, it
/// requires less storage space and is sufficient for many common cases.
///
/// @author Amos Shi
public final class Chunk_tRNS extends Chunk {

    /// Chunk type of the transparency chunk.
    public static final String CHUNK_TYPE_NAME = "tRNS";
    /// A copy of [Chunk_IHDR#ColorType].
    public final int ColorType;
    /// The transparency value for color type 0 (gray scale).
    ///
    /// For color type 0 (gray scale), the `tRNS` chunk contains a single gray
    /// level value, stored in the format:
    ///
    /// ```
    ///   Gray:  2 bytes, range 0 .. (2^bitdepth)-1
    /// ```
    ///
    /// (If the image bit depth is less than 16, the least significant bits are
    /// used and the others are 0.)  Pixels of the specified gray level are to be
    /// treated as transparent (equivalent to alpha value 0); all other pixels are
    /// to be treated as fully opaque (alpha value <code>2<sup>bitdepth</sup>-1</code>).
    ///
    /// This field is `-1` when the color type is not 0.
    public final int Gray;
    /// The transparency value for color type 2 (true color).
    ///
    /// For color type 2 (true color), the `tRNS` chunk contains a single RGB
    /// color value, stored in the format:
    ///
    /// ```
    ///   Red:   2 bytes, range 0 .. (2^bitdepth)-1
    ///   Green: 2 bytes, range 0 .. (2^bitdepth)-1
    ///   Blue:  2 bytes, range 0 .. (2^bitdepth)-1
    /// ```
    ///
    /// (If the image bit depth is less than 16, the least significant bits are
    /// used and the others are 0.)  Pixels of the specified color value are to
    /// be treated as transparent (equivalent to alpha value 0); all other pixels
    /// are to be treated as fully opaque (alpha value
    /// <code>2<sup>bitdepth</sup>-1</code>).
    ///
    /// This field is `-1` when the color type is not 2.
    public final int Red;
    /// Refer to [#Red].
    public final int Green;
    /// Refer to [#Red].
    public final int Blue;
    /// The transparency value for color type 3 (indexed color).
    ///
    /// For color type 3 (indexed color), the `tRNS` chunk contains a series of
    /// one-byte alpha values, corresponding to entries in the `PLTE` chunk:
    ///
    /// ```
    ///    Alpha for palette index 0:  1 byte
    ///    Alpha for palette index 1:  1 byte
    ///    ...etc...
    /// ```
    ///
    /// Each entry indicates that pixels of the corresponding palette index must
    /// be treated as having the specified alpha value. Alpha values have the same
    /// interpretation as in an 8-bit full alpha channel: 0 is fully transparent,
    /// 255 is fully opaque, regardless of image bit depth. The `tRNS` chunk must
    /// not contain more alpha values than there are palette entries, but `tRNS`
    /// _can contain fewer values than there are palette entries_. In this case,
    /// the alpha value for all remaining palette entries is assumed to be 255. In
    /// the common case in which only palette index 0 need be made transparent,
    /// only a one-byte `tRNS` chunk is needed.
    ///
    /// This field is `null` when the color type is not 3.
    public final int[] Alpha;

    Chunk_tRNS(final PosDataInputStream stream, final int colorType) throws IOException {
        super(stream);

        this.ColorType = colorType;
        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        switch (this.ColorType) {
            case 0 -> {
                this.Gray = chunkDataStream.readUnsignedShort();
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = null;
            }
            case 2 -> {
                this.Gray = -1;
                this.Red = chunkDataStream.readUnsignedShort();
                this.Green = chunkDataStream.readUnsignedShort();
                this.Blue = chunkDataStream.readUnsignedShort();
                this.Alpha = null;
            }
            case 3 -> {
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = new int[this.Length];
                for (int i = 0; i < this.Length; i++) {
                    this.Alpha[i] = chunkDataStream.read();
                }
            }
            default -> {
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
                this.Alpha = null;
            }
        }
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        switch (this.ColorType) {
            case 0 -> parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    2,
                    "Gray = %d".formatted(this.Gray))));
            case 2 -> {
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        2,
                        "Red = %d".formatted(this.Red))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 2,
                        2,
                        "Green = %d".formatted(this.Green))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 4,
                        2,
                        "Blue = %d".formatted(this.Blue))));
            }
            case 3 -> {
                for (int i = 0; i < this.Alpha.length; i++) {
                    parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            start + i,
                            1,
                            "Alpha[%d] = %d".formatted(i, this.Alpha[i]))));
                }
            }
            default -> {
                // The color type does not allow a tRNS chunk
            }
        }
    }
}
