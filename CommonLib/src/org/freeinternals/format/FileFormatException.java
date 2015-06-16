/**
 * FileFormatException.java    Apr 12, 2011, 10:59
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format;

/**
 *
 * @author Amos Shi
 */
public class FileFormatException extends Exception {

    private static final long serialVersionUID = 4876543219876500000L;

    /**
     * Creates a new instance of <code>FileFormatException</code> without
     * detail message.
     */
    public FileFormatException() {
    }

    /**
     * Constructs an instance of <code>FileFormatException</code> with the
     * specified detail message.
     * @param msg the detail message.
     */
    public FileFormatException(String msg) {
        super(msg);
    }
}
