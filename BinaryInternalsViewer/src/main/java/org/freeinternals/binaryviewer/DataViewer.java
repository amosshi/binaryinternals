/*
 * JAsciiDataViewer.java    September 08, 2019, 19:17 PM
 *
 * Copyright  2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.binaryviewer;

import javax.swing.JTextPane;

/**
 * Display binary data.
 *
 * @author Amos Shi
 */
public abstract class DataViewer extends JTextPane {

    /**
     * Binary data will be displayed.
     */
    private byte[] data = null;
    /**
     * Start index to be high-lighted.
     */
    private int selectedStartIndex = 0;
    /**
     * Length to be high-lighted.
     */
    private int selectedLength = 0;

    /**
     * Constructor.
     */
    DataViewer() {
        super();
        this.setEditable(false);
        this.setBorder(null);
        this.setContentType("text/html");
    }

    /**
     * Set the binary data to be displayed.
     *
     * @param bin Binary data.
     */
    public void setData(final byte[] bin) {
        if (bin == null) {
            this.data = null;
        } else {
            this.data = bin.clone();
        }

        this.updateContent();
    }

    /**
     * Set the selection part to be high-lighted.
     *
     * @param startIndex Start index to be high-lighted
     * @param length Length to be high-lighted
     */
    public void setSelection(final int startIndex, final int length) {
        this.selectedStartIndex = startIndex;
        this.selectedLength = length;
        this.updateContent();
    }

    /**
     * Return value of {@link #data}.
     *
     * @return Value of {@link #data}
     */
    protected byte[] getData() {
        return this.data;
    }

    /**
     * Return value of {@link #selectedStartIndex}.
     *
     * @return Value of {@link #selectedStartIndex}
     */
    protected int getSelectedStartIndex() {
        return this.selectedStartIndex;
    }

    /**
     * Return value of {@link #selectedLength}.
     *
     * @return Value of {@link #selectedLength}
     */
    protected int getSelectedLength() {
        return this.selectedLength;
    }

    protected abstract void updateContent();
}
