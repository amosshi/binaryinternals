/*
 * Chunk.java    Apr 21, 2011, 22:46
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// A PNG chunk. Chunks without a dedicated class, such as `sRGB`, are shown
/// with this generic class.
///
/// @author Amos Shi
/// @see <a href="http://www.LIBPNG.org">PNG (Portable Network Graphics) Specification</a>
public sealed class Chunk extends FileComponent implements GenerateTreeNode
        permits Chunk_IHDR, Chunk_PLTE, Chunk_IDAT, Chunk_IEND,
        Chunk_bKGD, Chunk_cHRM, Chunk_gAMA, Chunk_hIST, Chunk_iTXt, Chunk_pHYs,
        Chunk_sBIT, Chunk_sPLT, Chunk_tEXt, Chunk_tIME, Chunk_tRNS, Chunk_zTXt {

    /// Size of the [#Length] field.
    static final int LENGTH_SIZE = 4;
    /// Size of [#CRC].
    static final int CRC_SIZE = 4;
    /// Most bytes of decompressed text kept by [#inflateText(byte\[\], Charset)].
    static final int INFLATED_TEXT_MAX = 1024 * 1024;

    /// A 4-byte unsigned integer giving the number of bytes in the chunk's data
    /// field.
    ///
    /// The length counts **only** the data field, **not** itself, the chunk
    /// type code, or the CRC. Zero is a valid length. Although encoders and
    /// decoders should treat the length as unsigned, its value must not exceed
    /// <code>2<sup>31</sup>-1</code> bytes.
    public final int Length;
    /// A 4-byte chunk type code.
    ///
    /// For convenience in description and in examining PNG files, type codes
    /// are restricted to consist of uppercase and lowercase ASCII letters
    /// (A-Z and a-z, or 65-90 and 97-122 decimal). However, encoders and
    /// decoders must treat the codes as fixed binary values, not character
    /// strings. For example, it would not be correct to represent the type code
    /// `IDAT` by the EBCDIC equivalents of those letters. Additional naming
    /// conventions for chunk types are discussed in the next section.
    public final byte[] ChunkType = new byte[PNGFile.CHUNK_TYPE_SIZE];
    /// The data bytes appropriate to the chunk type, if any.
    ///
    /// This field can be `null` for zero length.
    public final byte[] ChunkData;
    /// A 4-byte CRC (Cyclic Redundancy Check) calculated on the preceding bytes
    /// in the chunk, including the chunk type code and chunk data fields, but
    /// **not** including the length field.
    ///
    /// The CRC is always present, even for chunks containing no data.
    public final byte[] CRC = new byte[CRC_SIZE];

    Chunk(final PosDataInputStream stream) throws IOException {
        this.startPos = stream.getPos();

        this.Length = stream.readInt();  // Chunk Length
        this.length = LENGTH_SIZE + PNGFile.CHUNK_TYPE_SIZE + this.Length + CRC_SIZE;

        // Chunk Type
        stream.readFully(this.ChunkType);

        // Chunk Data
        if (this.Length > 0) {
            this.ChunkData = new byte[this.Length];
            stream.readFully(this.ChunkData);
        } else {
            this.ChunkData = null;
        }

        // CRC
        stream.readFully(this.CRC);
    }

    public String getChunkTypeName() {
        return new String(this.ChunkType, StandardCharsets.ISO_8859_1);
    }

    /// File position of the first byte of [#ChunkData].
    protected final int getDataStartPos() {
        return this.startPos + LENGTH_SIZE + PNGFile.CHUNK_TYPE_SIZE;
    }

    /// Decompress a zlib data stream with deflate compression (compression
    /// method 0), as used by the `zTXt` and `iTXt` chunks.
    ///
    /// @param compressed The zlib data stream
    /// @param charset Charset of the text
    /// @return The text, cut at [#INFLATED_TEXT_MAX] bytes, or `null` when
    ///     `compressed` is not a valid zlib data stream
    static String inflateText(final byte[] compressed, final Charset charset) {
        final Inflater inflater = new Inflater();
        try {
            inflater.setInput(compressed);
            final ByteArrayOutputStream text = new ByteArrayOutputStream();
            final byte[] buf = new byte[8192];
            while (!inflater.finished() && text.size() < INFLATED_TEXT_MAX) {
                final int n = inflater.inflate(buf);
                if (n == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
                    return null;  // Truncated data stream
                }
                text.write(buf, 0, Math.min(n, INFLATED_TEXT_MAX - text.size()));
            }
            return text.toString(charset);
        } catch (DataFormatException _) {
            return null;
        } finally {
            inflater.end();
        }
    }

    /// A stream over [#ChunkData], which reports positions in the file.
    ///
    /// @return The stream, or `null` when the chunk has no data
    protected final PosDataInputStream getChunkDataStream() {
        return this.ChunkData == null
                ? null
                : new PosDataInputStream(new PosByteArrayInputStream(this.ChunkData), this.getDataStartPos());
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                LENGTH_SIZE,
                "Length = %d".formatted(this.Length))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + LENGTH_SIZE,
                PNGFile.CHUNK_TYPE_SIZE,
                "Chunk Type = %s".formatted(this.getChunkTypeName()))));

        final int dataStart = this.getDataStartPos();
        if (this.Length > 0) {
            final DefaultMutableTreeNode chunkDataNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    dataStart,
                    this.Length,
                    "Chunk Data"));
            parent.add(chunkDataNode);
            this.generateTreeNodeChunkData(chunkDataNode);
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dataStart + this.Length,
                CRC_SIZE,
                "CRC")));
    }

    /// Add the fields of [#ChunkData] to the tree. A chunk without a dedicated
    /// class shows its data as one node.
    ///
    /// @param parent The "Chunk Data" node
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        // Nothing more is known about the data of a generic chunk
    }
}
