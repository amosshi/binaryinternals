/*
 * PosByteArrayInputStream.java    August 8, 2007, 12:44 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.commonlib;

import java.io.ByteArrayInputStream;

/// A [ByteArrayInputStream] that exposes its current read position.
///
/// @author Amos Shi
public final class PosByteArrayInputStream extends ByteArrayInputStream {

    /// Creates a new instance of PosByteArrayInputStream
    /// @param buf Buffer pool
    public PosByteArrayInputStream(final byte[] buf) {
        super(buf);
    }

    void setPos(int i) {
        this.pos = i;
    }

    /// Gets the current read position.
    ///
    /// @return Index of the next byte to read
    public int getPos() {
        return this.pos;
    }

    byte[] getBuf() {
        return this.buf;
    }
}
