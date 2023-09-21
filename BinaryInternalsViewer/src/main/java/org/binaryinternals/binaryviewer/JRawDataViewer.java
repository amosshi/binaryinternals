/*
 * JRawDataViewer.java    September 12, 2007, 2:12 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.binaryviewer;

import org.binaryinternals.commonlib.ui.HTMLKit;

/**
 * Display binary data as HEX text.
 *
 * @author Amos Shi
 */
final class JRawDataViewer extends DataViewer {

    private static final long serialVersionUID = 4876543219876500000L;
    /**
     * Width of the raw data viewer section.
     */
    public static final int WIDTH_VALUE = 460;

    @Override
    protected void updateContent() {
        this.setText(null);

        byte[] data = super.getData();
        if (data == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(HTMLKit.START);

        final int dataLength = data.length;
        int breakCounter = 0;
        for (int i = 0; i < dataLength; i++) {
            sb.append(HTMLKit.SPACE);
            if (this.getSelectedLength() > 0
                    && i >= this.getSelectedStartIndex()
                    && i < this.getSelectedStartIndex() + this.getSelectedLength()) {
                sb.append(HTMLKit.span(String.format("%02X", data[i]), HTMLKit.FONT_COLOR_ORANGE));
            } else {
                sb.append(HTMLKit.span(String.format("%02X", data[i])));
            }
            breakCounter++;

            if (breakCounter > JBinaryViewer.ROW_ITEM_MAX_INDEX) {
                sb.append(HTMLKit.NEW_LINE);
                breakCounter = 0;
            }
        }

        sb.append(HTMLKit.END);
        this.setText(sb.toString());
    }
}
