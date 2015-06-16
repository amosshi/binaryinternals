/**
 * Chunk_IHDR.java    Apr 30, 2011, 11:58
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
 * Image header.
 * 
 * <p>
 * The <span>IHDR</span> chunk must appear FIRST.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_IHDR extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IHDR";

    /**
     * Width and height give the image dimensions in pixels.
     * <p>
     * They are 4-byte integers.
     * Zero is an invalid value.
     * The maximum for each is <code>2<sup>31</sup>-1</code> in order to
     * accommodate languages that have difficulty with unsigned 4-byte values.
     * </p>
     */
    public final int Width;
    /**
     * Hight of image dimensions in pixels.
     * 
     * @see Chunk_IHDR#Width
     */
    public final int Height;
    /**
     * Bit depth is a single-byte integer giving the number of bits per sample
     * or per palette index (not per pixel).
     * <p>
     * Valid values are 1, 2, 4, 8, and 16, although not all values are allowed
     * for all color types.
     * </p>
     */
    public final int BitDepth;
    /**
     * Color type is a single-byte integer that describes the interpretation of
     * the image data.
     * <p>
     * Color type codes represent sums of the following values:
     * 1 (palette used), 2 (color used), and 4 (alpha channel used).
     * Valid values are 0, 2, 3, 4, and 6.
     * </p>
     */
    public final int ColorType;
    /**
     * Compression method is a single-byte integer that indicates the method
     * used to compress the image data.
     * <p>
     * At present, only compression method 0 (deflate/inflate compression with
     * a sliding window of at most 32768 bytes) is defined.
     * All standard PNG images must be compressed with this scheme.
     * The compression method field is provided for possible future expansion
     * or proprietary variants. Decoders must check this byte and report an
     * error if it holds an unrecognized code.
     * </p>
     */
    public final int CompressionMethod;
    /**
     * Filter method is a single-byte integer that indicates the preprocessing
     * method applied to the image data before compression.
     * <p>
     * At present, only filter method 0 (adaptive filtering with five basic
     * filter types) is defined. As with the compression method field, decoders
     * must check this byte and report an error if it holds an unrecognized code.
     * </p>
     */
    public final int FilterMethod;
    /**
     * Interlace method is a single-byte integer that indicates the transmission
     * order of the image data.
     * <p>
     * Two values are currently defined: 0 (no interlace) or 1 (Adam7 interlace).
     * </p>
     */
    public final int InterlaceMethod;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType(){
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_IHDR(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Width = chunkDataStream.readInt();
        this.Height = chunkDataStream.readInt();
        this.BitDepth = chunkDataStream.read();
        this.ColorType = chunkDataStream.read();
        this.CompressionMethod = chunkDataStream.read();
        this.FilterMethod = chunkDataStream.read();
        this.InterlaceMethod = chunkDataStream.read();
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                String.format("Width = %d", this.Width))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 4,
                4,
                String.format("Height = %d", this.Height))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 4,
                1,
                String.format("Bit depth = %d", this.BitDepth))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Color type = %d", this.ColorType))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Compression method = %d", this.CompressionMethod))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Filter method = %d", this.FilterMethod))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 1,
                1,
                String.format("Interlace method = %d", this.InterlaceMethod))));
    }
}
