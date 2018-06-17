/**
 * Chunk_IEND.java    Apr 30, 2011, 22:51
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Image trailer.
 * <p>
 * The <span>IEND</span> chunk must appear LAST.
 * It marks the end of the PNG data stream.
 * The chunk's data field is empty.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_IEND extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IEND";

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType(){
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_IEND(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);
    }
}
