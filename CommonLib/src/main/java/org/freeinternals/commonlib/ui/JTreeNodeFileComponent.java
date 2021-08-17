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
 * Tree node for a file component.
 *
 * @author Amos Shi
 */
public final class JTreeNodeFileComponent {

    /**
     * Binary data start position for current tree node.
     */
    private final int startPos;
    /**
     * Binary data length for current tree node.
     */
    private final int length;
    /**
     * Tree node text.
     */
    private final String text;

    /**
     * Icon for current tree node.
     */
    private Icon icon;
    /**
     * Description text for current tree node. 
     * It will be shown on the left bottom window on the screen.
     * It supports HTML.
     */
    private String description;
    /**
     * Detailed panel for current tree node.
     */
    private JPanel panelDetail = null;

    /**
     * Constructor.
     *
     * @param nodeStartPos Value for {@link #startPos}
     * @param nodeLength Value for {@link #length}
     * @param nodeText Value for {@link #text}
     */
    public JTreeNodeFileComponent(final int nodeStartPos, final int nodeLength, final String nodeText) {
        if (nodeStartPos < 0) {
            throw new IllegalArgumentException("Start position cannot be less than zero; it is '" + nodeStartPos + "'.");
        }

        if (nodeLength < 0) {
            throw new IllegalArgumentException("Length cannot be less than zero; it is '" + nodeLength + "'.");
        }

        if ((nodeText == null) || (nodeText.length() == 0)) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }

        this.startPos = nodeStartPos;
        this.length = nodeLength;
        this.text = nodeText;
    }

    /**
     * Constructor.
     *
     * @param nodeStartPos Value for {@link #startPos}
     * @param nodeLength Value for {@link #length}
     * @param nodeText Value for {@link #text}
     * @param desc Value for {@link #description}, could be null
     */
    public JTreeNodeFileComponent(final int nodeStartPos, final int nodeLength, final String nodeText, final String desc) {
        this(nodeStartPos, nodeLength, nodeText);
        this.description = desc;
    }

    /**
     * Constructor.
     *
     * @param nodeStartPos Value for {@link #startPos}
     * @param nodeLength Value for {@link #length}
     * @param nodeText Value for {@link #text}
     * @param nodeIcon Value for {@link #icon}, could be null
     * @param desc Value for {@link #description}, could be null
     */
    public JTreeNodeFileComponent(final int nodeStartPos, final int nodeLength, final String nodeText, final Icon nodeIcon, final String desc) {
        this(nodeStartPos, nodeLength, nodeText);
        this.setIcon(nodeIcon);
        this.description = desc;
    }

    @Override
    public String toString() {
        return this.text;
    }

    /**
     * Getter for {@link #startPos}.
     *
     * @return {@link #startPos} value
     */
    public int getStartPos() {
        return this.startPos;
    }

    /**
     * Getter for {@link #length}.
     *
     * @return {@link #length} value
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Get the last position of current node plus 1, which equals to the first
     * position of the next node. The value is
     * ({@link #startPos} + {@link #length}).
     *
     * @return Last position plus 1
     */
    public int getLastPosPlus1() {
        return this.startPos + this.length;
    }

    /**
     * Getter for {@link #text}.
     *
     * @return {@link #text} value
     */
    public String getText() {
        return this.text;
    }

    /**
     * Getter for {@link #icon}.
     *
     * @return {@link #icon} value
     */
    public Icon getIcon() {
        return this.icon;
    }
    
    /**
     * Setter for {@link #icon}.
     *
     * @param icon value for {@link #icon}
     */
    public void setIcon(Icon icon) {
        if (icon != null) {
            this.icon = icon;
        }
    }

    /**
     * Setter for {@link #description}.
     *
     * @param d Value for {@link #description}
     */
    public void setDescription(final String d) {
        this.description = d;
    }

    /**
     * Getter for {@link #description}.
     *
     * @return {@link #description} value
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for {@link #panelDetail}.
     *
     * @param p Value for {@link #panelDetail}
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value="EI_EXPOSE_REP2", justification="We need it")
    public void setDetailPanel(final JPanel p) {
        this.panelDetail = p;
    }
    
    /**
     * Indicates whether we have a detailed panel {@link #panelDetail}.
     *
     * @return true if {@link #panelDetail} is not null, else false
     */
    public boolean isDetailAvailable() {
        return this.panelDetail != null;
    }

    /**
     * Getter for {@link #panelDetail}.
     *
     * @return {@link #panelDetail} value
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value="EI_EXPOSE_REP", justification="We need it")
    public JPanel getDetailPanel() {
        return this.panelDetail;
    }

}
