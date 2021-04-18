/*
 * JAsciiDataViewer.java    September 12, 2007, 2:15 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.binaryviewer;

import org.freeinternals.commonlib.ui.HTMLKit;

/**
 * Display binary data as ASCII text.
 *
 * @author Amos Shi
 */
final class JAsciiDataViewer extends DataViewer {

    private static final long serialVersionUID = 4876543219876500004L;
    /**
     * Width of the ASCII data viewer section.
     */
    public static final int WIDTH_VALUE = 231;

    @Override
    protected void updateContent() {
        this.setText(null);
        byte[] data = super.getData();
        if (data == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(HTMLKit.start());

        final int dataLength = data.length;
        int breakCounter = 0;
        for (int i = 0; i < dataLength; i++) {
            if (this.getSelectedLength() > 0
                    && i >= this.getSelectedStartIndex()
                    && i < this.getSelectedStartIndex() + this.getSelectedLength()) {
                sb.append(HTMLKit.span(HTMLKit.getByteText(data[i]), HTMLKit.FONT_COLOR_YELLOW));
            } else {
                sb.append(HTMLKit.span(HTMLKit.getByteText(data[i])));
            }
            breakCounter++;

            if (breakCounter > JBinaryViewer.ROW_ITEM_MAX_INDEX) {
                sb.append(HTMLKit.newLine());
                breakCounter = 0;
            }
        }

        sb.append(HTMLKit.end());
        this.setText(sb.toString());
    }
}
