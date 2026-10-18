/*
 * JAsciiDataViewer.java    September 08, 2019, 19:17 PM
 *
 * Copyright  2019, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.binaryviewer;

import javax.swing.JTextPane;
import org.binaryinternals.commonlib.ui.HTMLKit;

/**
 * Display binary data.
 *
 * @author Amos Shi
 */
public abstract sealed class DataViewer extends JTextPane permits JRawDataViewer, JAsciiDataViewer {

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
     * Render a window of the data as HTML in one pass,
     * {@link JBinaryViewer#ROW_ITEM_MAX} bytes per row. The data is not copied.
     *
     * @param data Binary data, or null to clear the view
     * @param offset First byte of the window
     * @param length Number of bytes in the window
     * @param selectedStart Start of the high-lighted part, relative to the window
     * @param selectedLength Length of the high-lighted part
     */
    public void setContent(final byte[] data, final int offset, final int length,
            final int selectedStart, final int selectedLength) {
        if (data == null || length <= 0) {
            this.setText(null);
            return;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(HTMLKit.START);

        final int selectedEnd = selectedStart + selectedLength;
        for (int i = 0; i < length; i++) {
            final boolean selected = selectedLength > 0 && i >= selectedStart && i < selectedEnd;
            sb.append(this.formatByte(data[offset + i], selected));

            if ((i + 1) % JBinaryViewer.ROW_ITEM_MAX == 0) {
                sb.append(HTMLKit.NEW_LINE);
            }
        }

        sb.append(HTMLKit.END);
        this.setText(sb.toString());
    }

    /**
     * Format one byte as HTML.
     *
     * @param value The byte
     * @param selected Whether the byte is high-lighted
     * @return HTML of the byte
     */
    protected abstract String formatByte(byte value, boolean selected);
}
