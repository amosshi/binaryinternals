/*
 * JTreeNodeClassComponent.java    23:58, August 14, 2007
 *
 * Copyright 2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JTreeNodeFileComponent {

    private final int startPos;
    private final int length;
    private final String text;

    private Icon icon;
    private String description;
    private JPanel panelDetail = null;
    private boolean isDetailAvailable = false;

    public JTreeNodeFileComponent(final int startPos, final int length, final String text) {
        if (startPos < 0) {
            throw new IllegalArgumentException("Start position cannot be less than zero; it is '" + startPos + "'.");
        }

        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be less than zero; it is '" + length + "'.");
        }

        if ((text == null) || (text.length() == 0)) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }

        this.startPos = startPos;
        this.length = length;
        this.text = text;
    }

    public JTreeNodeFileComponent(final int startPos, final int length, final String text, Icon icon) {
        this(startPos, length, text);
        this.icon = icon;
    }

    public JTreeNodeFileComponent(final int startPos, final int length, final String text, final String desc) {
        this(startPos, length, text);
        this.description = desc;
    }

    public JTreeNodeFileComponent(final int startPos, final int length, final String text, final String desc, Icon icon) {
        this(startPos, length, text);
        this.description = desc;
        this.icon = icon;
    }

    @Override
    public String toString() {
//        int endIndex;
//        if (this.length > 0) {
//            endIndex = this.startPos + this.length - 1;
//        } else {
//            endIndex = this.startPos;
//        }
//        return this.text + " [" + this.startPos + ", " + endIndex + "]";
        return this.text;
    }

    public int getStartPos() {
        return this.startPos;
    }

    public int getLength() {
        return this.length;
    }
    
    /**
     * Get the last position of current node plus 1, which equals to the first position of the next node.
     * The value is ({@link #startPos} + {@link #length}).
     * 
     * @return Last position plus 1
     */
    public int getLastPosPlus1(){
        return this.startPos + this.length;
    }

    public String getText() {
        return this.text;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDetailPanel(JPanel p) {
        this.panelDetail = p;
        this.isDetailAvailable = true;
    }

    public boolean isDetailAvailable() {
        return this.isDetailAvailable;
    }

    public JPanel getDetailPanel() {
        return this.panelDetail;
    }

}
