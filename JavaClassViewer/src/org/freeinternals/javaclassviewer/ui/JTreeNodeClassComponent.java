/*
 * JTreeNodeClassComponent.java    23:58, August 14, 2007
 *
 * Copyright 2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
class JTreeNodeClassComponent {

    private final int startPos;
    private final int length;
    private final String text;

    public JTreeNodeClassComponent(final int startPos, final int length, final String text)
            throws InvalidTreeNodeException {
        if (startPos < 0) {
            throw new InvalidTreeNodeException("Start position cannot be less than zero; it is '" + startPos + "'.");
        }

        if (length < 0) {
            throw new InvalidTreeNodeException("Length cannot be less than zero; it is '" + length + "'.");
        }

        if ((text == null) || (text.length() == 0)) {
            throw new InvalidTreeNodeException("Text cannot be null or empty.");
        }

        this.startPos = startPos;
        this.length = length;
        this.text = text;
    }

    @Override
    public String toString() {
//        int endIndex;
//        if (this.length > 0)
//        {
//            endIndex = this.startPos + this.length - 1;
//        }
//        else
//        {
//            endIndex = this.startPos + this.length;
//        }
//
//        return this.text + " <" + this.startPos + ", " + endIndex + ">";

        return this.text;
    }

    public int getStartPos() {
        return this.startPos;
    }

    public int getLength() {
        return this.length;
    }

    public String getText() {
        return this.text;
    }
}
