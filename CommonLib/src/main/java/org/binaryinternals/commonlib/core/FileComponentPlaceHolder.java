/*
 * FileComponentPlaceHolder.java    10:32 PM, August 9, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib.core;

import java.io.IOException;

/// Place holder for a range of bytes which are skipped without being parsed.
///
/// @author Amos Shi
public final class FileComponentPlaceHolder extends FileComponent {

    /// Skip `length` bytes from current position of
    /// `posDataInputStream`.
    ///
    /// @param posDataInputStream The stream to read from
    /// @param length Number of bytes to skip; zero is used when it is negative
    /// @throws IOException Failed to skip the bytes
    public FileComponentPlaceHolder(final PosDataInputStream posDataInputStream, final int length) throws IOException {
        final int start = posDataInputStream.getPos();
        final int len = Math.max(length, 0);
        posDataInputStream.skipNBytes(len);
        super(start, len);
    }
}
