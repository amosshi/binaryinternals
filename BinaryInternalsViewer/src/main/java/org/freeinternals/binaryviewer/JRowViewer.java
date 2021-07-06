/*
 * JRowViewer.java    September 12, 2007, 2:28 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.binaryviewer;

import org.freeinternals.commonlib.ui.HTMLKit;
import java.awt.Component;
import javax.swing.JTextPane;

/**
 * Display rows of binary data.
 *
 * @author Amos Shi
 */
class JRowViewer extends JTextPane {

    private static final long serialVersionUID = 4876543219876500000L;
    /**
     * Width of the row viewer.
     */
    public static final int WIDTH_VALUE = 110;

    /**
     * Constructor.
     */
    public JRowViewer() {
        super();
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setEditable(false);
        this.setBorder(null);
        this.setContentType("text/html");
    }

    /**
     * Set revised Binary data.
     *
     * @param rowStart  Start row to be displayed
     * @param rowCount  Number of rows to be displayed
     * @param rowMax    Max row
     */
    public void setData(final int rowStart, final int rowCount, final int rowMax) {

        // Update contents
        this.setText(null);
        if (rowCount <= 0) {
            return;
        }
        if (rowStart >= rowMax) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(HTMLKit.START);

        int rowValue = rowStart * JBinaryViewer.ROW_ITEM_MAX;
        for (int i = 0; i < rowCount; i++) {
            if ((rowStart + i) < rowMax) {
                sb.append(HTMLKit.span(String.format("%08Xh", rowValue)));
                sb.append(HTMLKit.NEW_LINE);
                rowValue += JBinaryViewer.ROW_ITEM_MAX;
            }
        }

        sb.append(HTMLKit.END);
        this.setText(sb.toString());
    }
}
