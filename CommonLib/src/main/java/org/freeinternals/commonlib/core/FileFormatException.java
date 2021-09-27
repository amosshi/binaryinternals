/**
 * FileFormatException.java    Apr 12, 2011, 10:59
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

/**
 *
 * @author Amos Shi
 */
public class FileFormatException extends Exception {

    private static final long serialVersionUID = 4876543219876500000L;

    /**
     * Constructs an instance of <code>FileFormatException</code> with the
     * specified detail message.
     * @param msg the detail message.
     */
    public FileFormatException(final String msg) {
        super(msg);
    }

    public FileFormatException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
