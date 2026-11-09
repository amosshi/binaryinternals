/*
 * Chunk_zTXt.java    May 04, 2011, 16:06
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Compressed textual data, in the format:
///
/// ```
/// Keyword:            1-79 bytes (character string)
/// Null separator:     1 byte
/// Compression method: 1 byte
/// Compressed text:    n bytes
/// ```
///
/// @author Amos Shi
/// @see Chunk_tEXt
public final class Chunk_zTXt extends Chunk {

    /// Chunk type of the compressed textual data chunk.
    public static final String CHUNK_TYPE_NAME = "zTXt";
    /// Refer to [Chunk_tEXt#Keyword].
    public final String Keyword;
    /// The only value presently defined is 0, meaning zlib data stream with
    /// deflate compression.
    public final int CompressionMethod;
    /// The compressed text, or `null` when there is none.
    public final byte[] CompressedText;
    /// [#CompressedText] decompressed, as Latin-1 like [Chunk_tEXt#Text], or
    /// `null` when there is none or it cannot be decompressed.
    public final String TextString;

    Chunk_zTXt(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();
        this.CompressionMethod = chunkDataStream.read();

        final int rest = this.Length - this.Keyword.length() - 1 - 1;
        this.CompressedText = rest > 0 ? chunkDataStream.readNBytes(rest) : null;
        this.TextString = this.CompressedText != null && this.CompressionMethod == 0
                ? inflateText(this.CompressedText, StandardCharsets.ISO_8859_1)
                : null;
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.Keyword.length(),
                "Keyword = %s".formatted(this.Keyword))));
        start += this.Keyword.length();
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));
        start += 1;
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Compression method = %d".formatted(this.CompressionMethod))));
        start += 1;

        if (this.CompressedText != null) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.CompressedText.length,
                    this.TextString != null ? "Compressed text = %s".formatted(this.TextString) : "Compressed text")));
        }
    }
}
