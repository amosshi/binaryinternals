package org.freeinternals.commonlib.ui;

import java.awt.Font;

/**
 * HTML Kit for the <code>JTextPane</code> control.
 *
 * @author Amos
 */
public final class HTMLKit {

    /**
     * Default font.
     */
    public static final Font FONT = new Font(Font.DIALOG_INPUT, Font.PLAIN, 14);

    /**
     * Font color yellow.
     */
    public static final String FONT_COLOR_YELLOW = "yellow";

    /**
     * Font color orange.
     */
    public static final String FONT_COLOR_ORANGE = "#FFA500";

    /**
     * ASCII code 32.
     */
    private static final int ASCII_32 = 32;
    /**
     * ASCII code 127.
     */
    private static final int ASCII_127 = 127;
    /**
     * ASCII code 160.
     */
    private static final int ASCII_160 = 160;
    /**
     * ASCII code 255.
     */
    private static final int ASCII_255 = 255;

    private HTMLKit() {
    }

    /**
     * HTML start tags.
     */
    public static final String START = "<!DOCTYPE html><html><head></head><body>";

    /**
     * HTML end tags.
     */
    public static final String END = "\n</body>\n</html>";

    /**
     * HTML new line.
     */
    public static final String NEW_LINE = "<br />";

    /**
     * HTML space.
     */
    public static final String SPACE = "&nbsp;";

    /**
     * Get HTML format for text with new line.
     *
     * @param text Text inside the span
     * @return HTML code of span
     */
    public static String span(final String text) {
        return String.format("<span style=\"font-size:%dpx; font-family:%s;\">%s</span>",
                FONT.getSize() - 2,
                FONT.getFamily(),
                text);
    }

    /**
     * Get HTML format for text with new line and specified color.
     *
     * @param text Text inside the span
     * @param color Color of the text
     * @return HTML code of span
     */
    public static String span(final String text, final String color) {
        return String.format("<span style=\"background-color:%s; font-size:%dpx; font-family:%s;\">%s</span>",
                color,
                FONT.getSize() - 2,
                FONT.getFamily(),
                text);
    }

    /**
     * Get HTML marks for the byte.
     *
     * @param b Byte value
     * @return HTML mark for the byte value
     * @see <a href="http://ascii.cl/htmlcodes.htm">HTML Codes - Characters and
     * symbols</a>
     */
    public static String getByteText(final byte b) {
        String s = ".";

        int i = (b & 0xFF);
        if (((i > ASCII_32) && (i < ASCII_127))
                || ((i > ASCII_160) && (i <= ASCII_255))) {
            s = String.format("&#%d;", i);
        }

        return s;
    }

    /**
     * Escape HTML special character &lt; and &gt;.
     *
     * @param text Input text to process
     * @return Escaped result
     */
    public static String escapeFilter(final String text) {
        String result = null;
        if (text != null) {
            result = text.replace("<", "&lt;");
            result = result.replace(">", "&gt;");
        }

        return result;
    }
}
