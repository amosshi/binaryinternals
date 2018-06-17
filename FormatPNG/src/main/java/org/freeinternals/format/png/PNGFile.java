/**
 * PNGFile.java    Apr 19, 2011, 07:58
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 * @see <a href="http://www.LIBPNG.org">LIBPNG.org</a>
 */
public class PNGFile extends FileFormat {

    /**
     * The minimal <code>PNG</code> file length is 20.
     * <p>
     * In which: 8 bytes for magic number; 12 bytes (4 bytes <code>Length</code>,
     * 4 bytes <code>Chunk Type</code>, 4 bytes <code>CRC</code>) for the fist chunk.
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
    public static final byte[] MAGIC = {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
    /** 
     * Chunk type classes.
     */
    private static List<Class> ChunkTypes;

    private static void LoadChunkTypes() {
        ChunkTypes = Collections.synchronizedList(new ArrayList<Class>(100));

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

    public PNGFile(final File file) throws IOException, FileFormatException {
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
        if (Tool.isByteArraySame(MAGIC, magic) == false) {
            throw new FileFormatException("This is not a valid PNG file, because the PNG file signature does not exist at the beginning of this file.");
        }

        // Parse Chunks
        PosDataInputStream stream = new PosDataInputStream(
                new PosByteArrayInputStream(this.fileByteArray));
        stream.skip(PNGFile.MAGIC.length);

        while (stream.getPos() < this.fileByteArray.length) {
            super.addFileComponent(this.parseChunk(stream));
        }

    }

    private Chunk parseChunk(PosDataInputStream stream) throws IOException, FileFormatException {
        Chunk chunk = null;

        int length;
        byte[] chunkType = new byte[4];

        length = stream.readInt();                    // Read Chunk Length
        stream.read(chunkType);                       // Read chunk Type
        stream.skip((length > 0) ? length : 0);       // Skip Chunk Data
        stream.skip(4);                               // Skip CRC

        // GetChunkType
        Method mtd;
        byte[] type;

        PosDataInputStream streamChunk = new PosDataInputStream(
                new PosByteArrayInputStream(this.fileByteArray));
        streamChunk.skip(stream.getPos() - length - 12);

        for (Class cls : ChunkTypes) {
            try {
                mtd = cls.getDeclaredMethod("GetChunkType");
                type = (byte[]) mtd.invoke(null);
                if (Tool.isByteArraySame(chunkType, type) == true) {
                    Constructor c = cls.getConstructor(PosDataInputStream.class, PNGFile.class);
                    chunk = (Chunk) c.newInstance(streamChunk, this);
                    break;   // End the loop
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                Logger.getLogger(PNGFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (chunk == null) {
            chunk = new Chunk(streamChunk, this);
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
        for (FileComponent chunk : super.components.values()) {
            Chunk ck = (Chunk)chunk;
            root.add(node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    ck.getStartPos(),
                    ck.getLength(),
                    ck.getChunkTypeName()
            )));

            ((GenerateTreeNode) chunk).generateTreeNode(node);
        } // End while

    }
}
