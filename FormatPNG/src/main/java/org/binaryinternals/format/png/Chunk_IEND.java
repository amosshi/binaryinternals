/*
 * Chunk_IEND.java    Apr 30, 2011, 22:51
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.io.IOException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Image trailer.
///
/// The `IEND` chunk must appear LAST. It marks the end of the PNG data stream.
/// The chunk's data field is empty.
///
/// @author Amos Shi
public final class Chunk_IEND extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IEND";

    Chunk_IEND(final PosDataInputStream stream) throws IOException {
        super(stream);
    }
}
