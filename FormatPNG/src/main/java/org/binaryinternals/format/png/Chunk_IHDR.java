/*
 * Chunk_IHDR.java    Apr 30, 2011, 11:58
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Image header.
///
/// The `IHDR` chunk must appear FIRST.
///
/// @author Amos Shi
public final class Chunk_IHDR extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IHDR";

    /// Width and height give the image dimensions in pixels.
    ///
    /// They are 4-byte integers. Zero is an invalid value. The maximum for each
    /// is <code>2<sup>31</sup>-1</code> in order to accommodate languages that
    /// have difficulty with unsigned 4-byte values.
    public final int Width;
    /// Height of image dimensions in pixels.
    ///
    /// @see Chunk_IHDR#Width
    public final int Height;
    /// Bit depth is a single-byte integer giving the number of bits per sample
    /// or per palette index (not per pixel).
    ///
    /// Valid values are 1, 2, 4, 8, and 16, although not all values are allowed
    /// for all color types.
    public final int BitDepth;
    /// Color type is a single-byte integer that describes the interpretation of
    /// the image data.
    ///
    /// Color type codes represent sums of the following values:
    /// 1 (palette used), 2 (color used), and 4 (alpha channel used).
    /// Valid values are 0, 2, 3, 4, and 6.
    public final int ColorType;
    /// Compression method is a single-byte integer that indicates the method
    /// used to compress the image data.
    ///
    /// At present, only compression method 0 (deflate/inflate compression with
    /// a sliding window of at most 32768 bytes) is defined. All standard PNG
    /// images must be compressed with this scheme. The compression method field
    /// is provided for possible future expansion or proprietary variants.
    /// Decoders must check this byte and report an error if it holds an
    /// unrecognized code.
    public final int CompressionMethod;
    /// Filter method is a single-byte integer that indicates the preprocessing
    /// method applied to the image data before compression.
    ///
    /// At present, only filter method 0 (adaptive filtering with five basic
    /// filter types) is defined. As with the compression method field, decoders
    /// must check this byte and report an error if it holds an unrecognized code.
    public final int FilterMethod;
    /// Interlace method is a single-byte integer that indicates the transmission
    /// order of the image data.
    ///
    /// Two values are currently defined: 0 (no interlace) or 1 (Adam7 interlace).
    public final int InterlaceMethod;

    Chunk_IHDR(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.Width = chunkDataStream.readInt();
        this.Height = chunkDataStream.readInt();
        this.BitDepth = chunkDataStream.read();
        this.ColorType = chunkDataStream.read();
        this.CompressionMethod = chunkDataStream.read();
        this.FilterMethod = chunkDataStream.read();
        this.InterlaceMethod = chunkDataStream.read();
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                "Width = %d".formatted(this.Width))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 4,
                4,
                "Height = %d".formatted(this.Height))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 8,
                1,
                "Bit depth = %d".formatted(this.BitDepth))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 9,
                1,
                "Color type = %d".formatted(this.ColorType))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 10,
                1,
                "Compression method = %d".formatted(this.CompressionMethod))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 11,
                1,
                "Filter method = %d".formatted(this.FilterMethod))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 12,
                1,
                "Interlace method = %d".formatted(this.InterlaceMethod))));
    }
}
