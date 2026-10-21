/*
 * FileFormatException.java    Apr 12, 2011, 10:59
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib;

import java.io.IOException;

/// The file content does not follow its format, for example a corrupted or
/// truncated file.
///
/// It is an {@link IOException}, like {@link java.util.zip.ZipException}:
/// the file was read, but its bytes cannot be understood.
///
/// @author Amos Shi
public class FileFormatException extends IOException {

    private static final long serialVersionUID = 4876543219876500000L;

    /// Constructs an instance of `FileFormatException` with the
    /// specified detail message.
    /// @param msg the detail message.
    public FileFormatException(final String msg) {
        super(msg);
    }

    public FileFormatException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
