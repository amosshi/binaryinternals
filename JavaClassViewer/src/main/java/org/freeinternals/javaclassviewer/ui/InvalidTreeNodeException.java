/*
 * InvalidTreeNodeException.java    August 15, 2007, 12:05 AM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class InvalidTreeNodeException extends java.lang.Exception {

    private static final long serialVersionUID = 4876543219876500003L;

    /**
     * Creates a new instance of <code>InvalidTreeNodeException</code> without detail message.
     */
    public InvalidTreeNodeException() {
    }

    /**
     * Constructs an instance of <code>InvalidTreeNodeException</code> with the specified detail message.
     * @param msg The detail message.
     */
    public InvalidTreeNodeException(final String msg) {
        super(msg);
    }
}
