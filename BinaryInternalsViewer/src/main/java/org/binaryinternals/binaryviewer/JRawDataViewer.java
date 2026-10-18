/*
 * JRawDataViewer.java    September 12, 2007, 2:12 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.binaryviewer;

import org.binaryinternals.commonlib.ui.HTMLKit;
import java.util.HexFormat;

/**
 * Display binary data as HEX text.
 *
 * @author Amos Shi
 */
final class JRawDataViewer extends DataViewer {

    private static final long serialVersionUID = 4876543219876500009L;
    /**
     * Width of the raw data viewer section.
     */
    public static final int WIDTH_VALUE = 460;
    private static final HexFormat HEX = HexFormat.of().withUpperCase();

    @Override
    protected String formatByte(final byte value, final boolean selected) {
        final String hex = HEX.toHexDigits(value);
        return HTMLKit.SPACE + (selected
                ? HTMLKit.span(hex, HTMLKit.FONT_COLOR_ORANGE)
                : HTMLKit.span(hex));
    }
}
