/*
 * JAsciiDataViewer.java    September 12, 2007, 2:15 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.binaryviewer;

import org.binaryinternals.commonlib.ui.HTMLKit;

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
    protected String formatByte(final byte value, final boolean selected) {
        return selected
                ? HTMLKit.span(HTMLKit.getByteText(value), HTMLKit.FONT_COLOR_YELLOW)
                : HTMLKit.span(HTMLKit.getByteText(value));
    }
}
