/*
 * JAsciiDataViewer.java    September 12, 2007, 2:15 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui.binviewer;

import org.freeinternals.commonlib.ui.HTMLKit;
import javax.swing.JTextPane;
import org.freeinternals.commonlib.ui.JBinaryViewer;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JAsciiDataViewer extends JTextPane {

    private static final long serialVersionUID = 4876543219876500004L;
    public static final int WIDTH_VALUE = 231;
    private byte[] Data = null;
    private int selectedStartIndex = 0;
    private int selectedLength = 0;

    public JAsciiDataViewer() {
        super();
        this.setEditable(false);
        this.setBorder(null);
        this.setContentType("text/html");
    }

    public void setData(final byte[] data) {
        this.Data = data;
        this.updateContent();
    }

    public void setSelection(final int startIndex, final int length) {
        this.selectedStartIndex = startIndex;
        this.selectedLength = length;
        this.updateContent();
    }

    private void updateContent() {
        this.setText(null);
        if (this.Data == null) {
            return;
        }

        StringBuilder sb = new StringBuilder(4096);
        sb.append(HTMLKit.Start());

        final int dataLength = this.Data.length;
        int breakCounter = 0;
        for (int i = 0; i < dataLength; i++) {
            if (this.selectedLength > 0
                    && i >= this.selectedStartIndex
                    && i < this.selectedStartIndex + this.selectedLength) {
                sb.append(HTMLKit.Span(HTMLKit.getByteText(this.Data[i]), HTMLKit.FONT_COLOR_YELLOW));
            } else {
                sb.append(HTMLKit.Span(HTMLKit.getByteText(this.Data[i])));
            }
            breakCounter++;

            if (breakCounter > JBinaryViewer.ROW_ITEM_MAX_INDEX) {
                sb.append(HTMLKit.NewLine());
                breakCounter = 0;
            }
        }

        sb.append(HTMLKit.End());
        this.setText(sb.toString());
    }
}
