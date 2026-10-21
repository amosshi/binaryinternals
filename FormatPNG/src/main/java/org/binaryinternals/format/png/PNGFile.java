/*
 * PNGFile.java    Apr 19, 2011, 07:58
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.nio.file.Path;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 *
 * @author Amos Shi
 * @see <a href="http://www.LIBPNG.org">LIBPNG.org</a>
 */
public class PNGFile extends FileFormat {

    /**
     * The minimal <code>PNG</code> file length is 20.
     * <p>
     * In which: 8 bytes for magic number; 12 bytes (4 bytes
     * <code>Length</code>, 4 bytes <code>Chunk Type</code>, 4 bytes
     * <code>CRC</code>) for the fist chunk.
     * </p>
     */
    public static final int PNGFILE_MIN_LENGTH = 20;
    /**
     * The first eight bytes of a <code>PNG</code> file always contain the
     * <code>PNG</code> file signature.
     * <p>
     * The signature values are: <code>137 80 78 71 13 10 26 10</code>.
     * </p>
     */
    static final byte[] MAGIC = {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
    
    /**
     * Size of chunk type.
     */
    static final int CHUNK_TYPE_SIZE = 4;
    /**
     * Chunk type classes.
     */
    private static List<Class<? extends Chunk>> ChunkTypes;

    private static void LoadChunkTypes() {
        ChunkTypes = Collections.synchronizedList(new ArrayList<>(100));

        ChunkTypes.add(Chunk_IDAT.class);
        ChunkTypes.add(Chunk_IHDR.class);
        ChunkTypes.add(Chunk_IEND.class);
        ChunkTypes.add(Chunk_PLTE.class);

        ChunkTypes.add(Chunk_bKGD.class);
        ChunkTypes.add(Chunk_cHRM.class);
        ChunkTypes.add(Chunk_gAMA.class);
        ChunkTypes.add(Chunk_hIST.class);
        ChunkTypes.add(Chunk_iTXt.class);
        ChunkTypes.add(Chunk_pHYs.class);
        ChunkTypes.add(Chunk_sBIT.class);
        ChunkTypes.add(Chunk_sPLT.class);
        ChunkTypes.add(Chunk_tEXt.class);
        ChunkTypes.add(Chunk_tIME.class);
        ChunkTypes.add(Chunk_tRNS.class);
        ChunkTypes.add(Chunk_zTXt.class);
    }

    public PNGFile(final Path file) throws IOException {
        super(file);

        // Load the chunk types
        LoadChunkTypes();

        // Check the file length
        if (this.fileByteArray.length < PNGFILE_MIN_LENGTH) {
            throw new FileFormatException(String.format(
                    "The file length (%d) is less than the minimal allowed PNG file.", this.fileByteArray.length));
        }

        // Check the file signature
        byte[] magic = new byte[MAGIC.length];
        System.arraycopy(this.fileByteArray, 0, magic, 0, MAGIC.length);
        if (Arrays.equals(MAGIC, magic) == false) {
            throw new FileFormatException("This is not a valid PNG file, because the PNG file signature does not exist at the beginning of this file.");
        }

        // Parse Chunks
        PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(this.fileByteArray));
        stream.skipNBytes(PNGFile.MAGIC.length);

        int colorType = -1;
        while (stream.getPos() < this.fileByteArray.length) {
            final Chunk chunk = parseChunk(stream, colorType);
            super.components.put(Long.valueOf(chunk.getStartPos()), chunk);
            if (chunk instanceof Chunk_IHDR ihdr) {
                colorType = ihdr.ColorType;
            }
        }
    }

    private static Chunk parseChunk(PosDataInputStream stream, int colorType) throws IOException {
        Chunk chunk = null;

        int length;
        byte[] chunkType = new byte[CHUNK_TYPE_SIZE];

        // Read Chunk Length
        length = stream.readInt();

        // Read chunk Type
        if (stream.read(chunkType) != CHUNK_TYPE_SIZE) {
            throw new IOException("Read the chunk type field failed near position " + stream.getPos());
        }

        // Skip Chunk Data
        if (length > 0) {
            stream.skipNBytes(length);
        }

        // Skip CRC
        stream.skipNBytes(4);

        // GetChunkType
        Method mtd;
        byte[] type;

        PosDataInputStream streamChunk = new PosDataInputStream(new PosByteArrayInputStream(stream.getBuf()));
        streamChunk.skipNBytes(stream.getPos() - length - 12);

        for (Class<? extends Chunk> cls : ChunkTypes) {
            try {
                mtd = cls.getDeclaredMethod("GetChunkType");
                type = (byte[]) mtd.invoke(null);
                if (Arrays.equals(chunkType, type) == true) {
                    if (cls == Chunk_tRNS.class) {
                        Constructor<? extends Chunk> constructor = cls.getConstructor(PosDataInputStream.class, int.class);
                        chunk = constructor.newInstance(streamChunk, colorType);
                    } else {
                        Constructor<? extends Chunk> constructor = cls.getConstructor(PosDataInputStream.class);
                        chunk = constructor.newInstance(streamChunk);
                    }
                    break;   // End the loop
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                Logger.getLogger(PNGFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (chunk == null) {
            chunk = new Chunk(streamChunk);
        }

        return chunk;
    }

    @Override
    public String getContentTabName() {
        return "PNG File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode root) {

        root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                0,
                PNGFile.MAGIC.length,
                "PNG file signature")));

        DefaultMutableTreeNode node;
        for (FileComponent component : super.components.values()) {
            if (component instanceof Chunk ck) {
                root.add(node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        ck.getStartPos(),
                        ck.getLength(),
                        ck.getChunkTypeName()
                )));

                ck.generateTreeNode(node);
            }
        } // End while

    }
}
