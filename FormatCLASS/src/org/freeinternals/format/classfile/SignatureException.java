/*
 * SignatureException.java    September 21, 2007, 11:51 AM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
class SignatureException extends java.lang.Exception {

    private static final long serialVersionUID = 4876543219876500002L;

    /**
     * Creates a new instance of <code>SignatureException</code> without detail message.
     */
    public SignatureException() {
    }

    /**
     * Constructs an instance of <code>SignatureException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SignatureException(final String msg) {
        super(msg);
    }
}
