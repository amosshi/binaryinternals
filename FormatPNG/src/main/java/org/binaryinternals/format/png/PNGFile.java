/*
 * PNGFile.java    Apr 19, 2011, 07:58
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Portable Network Graphics (PNG) file.
///
/// @author Amos Shi
/// @see <a href="http://www.LIBPNG.org">LIBPNG.org</a>
public class PNGFile extends FileFormat {

    private static final Logger LOGGER = Logger.getLogger(PNGFile.class.getName());

    /// The minimal `PNG` file length is 20.
    ///
    /// In which: 8 bytes for magic number; 12 bytes (4 bytes `Length`,
    /// 4 bytes `Chunk Type`, 4 bytes `CRC`) for the fist chunk.
    public static final int PNGFILE_MIN_LENGTH = 20;
    /// The first eight bytes of a `PNG` file always contain the `PNG` file
    /// signature.
    ///
    /// The signature values are: `137 80 78 71 13 10 26 10`.
    static final byte[] MAGIC = {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};

    /// Size of chunk type.
    static final int CHUNK_TYPE_SIZE = 4;

    public PNGFile(final Path file) throws IOException {
        super(file);

        // Check the file length
        if (this.fileByteArray.length < PNGFILE_MIN_LENGTH) {
            throw new FileFormatException(
                    "The file length (%d) is less than the minimal allowed PNG file.".formatted(this.fileByteArray.length));
        }

        // Check the file signature
        if (!Arrays.equals(MAGIC, 0, MAGIC.length, this.fileByteArray, 0, MAGIC.length)) {
            throw new FileFormatException("This is not a valid PNG file, because the PNG file signature does not exist at the beginning of this file.");
        }

        // Parse Chunks
        final PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(this.fileByteArray));
        stream.skipNBytes(MAGIC.length);

        int colorType = -1;
        while (stream.getPos() < this.fileByteArray.length) {
            final Chunk chunk = parseChunk(stream, colorType);
            super.components.put((long) chunk.getStartPos(), chunk);
            if (chunk instanceof Chunk_IHDR ihdr) {
                colorType = ihdr.ColorType;
            }
        }
    }

    /// Parse the chunk at the current position, and move the stream to the next chunk.
    ///
    /// A known chunk whose data cannot be parsed is kept as a generic [Chunk],
    /// so one malformed chunk does not hide the rest of the file.
    private static Chunk parseChunk(final PosDataInputStream stream, final int colorType) throws IOException {
        final int chunkStart = stream.getPos();
        final int length = stream.readInt();
        if (length < 0) {
            throw new FileFormatException("Invalid chunk length %d at position %d.".formatted(length, chunkStart));
        }
        final byte[] chunkType = new byte[CHUNK_TYPE_SIZE];
        stream.readFully(chunkType);
        stream.skipNBytes(length + (long) Chunk.CRC_SIZE);

        final String type = new String(chunkType, StandardCharsets.ISO_8859_1);
        try {
            return switch (type) {
                case Chunk_IHDR.CHUNK_TYPE_NAME -> new Chunk_IHDR(chunkStream(stream, chunkStart));
                case Chunk_PLTE.CHUNK_TYPE_NAME -> new Chunk_PLTE(chunkStream(stream, chunkStart));
                case Chunk_IDAT.CHUNK_TYPE_NAME -> new Chunk_IDAT(chunkStream(stream, chunkStart));
                case Chunk_IEND.CHUNK_TYPE_NAME -> new Chunk_IEND(chunkStream(stream, chunkStart));
                case Chunk_bKGD.CHUNK_TYPE_NAME -> new Chunk_bKGD(chunkStream(stream, chunkStart));
                case Chunk_cHRM.CHUNK_TYPE_NAME -> new Chunk_cHRM(chunkStream(stream, chunkStart));
                case Chunk_gAMA.CHUNK_TYPE_NAME -> new Chunk_gAMA(chunkStream(stream, chunkStart));
                case Chunk_hIST.CHUNK_TYPE_NAME -> new Chunk_hIST(chunkStream(stream, chunkStart));
                case Chunk_iTXt.CHUNK_TYPE_NAME -> new Chunk_iTXt(chunkStream(stream, chunkStart));
                case Chunk_pHYs.CHUNK_TYPE_NAME -> new Chunk_pHYs(chunkStream(stream, chunkStart));
                case Chunk_sBIT.CHUNK_TYPE_NAME -> new Chunk_sBIT(chunkStream(stream, chunkStart));
                case Chunk_sPLT.CHUNK_TYPE_NAME -> new Chunk_sPLT(chunkStream(stream, chunkStart));
                case Chunk_tEXt.CHUNK_TYPE_NAME -> new Chunk_tEXt(chunkStream(stream, chunkStart));
                case Chunk_tIME.CHUNK_TYPE_NAME -> new Chunk_tIME(chunkStream(stream, chunkStart));
                case Chunk_tRNS.CHUNK_TYPE_NAME -> new Chunk_tRNS(chunkStream(stream, chunkStart), colorType);
                case Chunk_zTXt.CHUNK_TYPE_NAME -> new Chunk_zTXt(chunkStream(stream, chunkStart));
                default -> new Chunk(chunkStream(stream, chunkStart));
            };
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, ex, () -> "Cannot parse chunk %s at position %d, show it as a generic chunk".formatted(type, chunkStart));
            return new Chunk(chunkStream(stream, chunkStart));
        }
    }

    /// A new stream positioned at the start of a chunk.
    private static PosDataInputStream chunkStream(final PosDataInputStream stream, final int chunkStart) throws IOException {
        final PosDataInputStream chunkStream = new PosDataInputStream(new PosByteArrayInputStream(stream.getBuf()));
        chunkStream.skipNBytes(chunkStart);
        return chunkStream;
    }

    @Override
    public String getContentTabName() {
        return "PNG File";
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode root) {
        root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                0,
                MAGIC.length,
                "PNG file signature")));

        for (FileComponent component : super.components.values()) {
            if (component instanceof Chunk chunk) {
                final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        chunk.getStartPos(),
                        chunk.getLength(),
                        chunk.getChunkTypeName()));
                root.add(node);
                chunk.generateTreeNode(node);
            }
        }
    }
}
