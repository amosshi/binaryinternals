package org.freeinternals.format.pdf;

/**
 * Static values of PDF file definition.
 *
 * @author Amos
 */
public class PDFStatics {

    /**
     * See
     * <pre>PDF 32000-1:2008</pre>
     * <code>7.2.2</code>: Table 1 – White-space characters.
     */
    public static class WhiteSpace {

        /**
         * Null (NUL). <p>Value
         * <code>0x00</code>.</p>
         */
        public static final byte NUL = 0x00;
        /**
         * HORIZONTAL TAB (HT).<p>Value
         * <code>0x09</code>.</p>
         */
        public static final byte HT = 0x09;
        /**
         * LINE FEED (LF). New line character.<p>Value
         * <code>0x0A</code>.</p>
         */
        public static final byte LF = 0x0A;
        /**
         * FORM FEED (FF).<p>Value
         * <code>0x0C</code>.</p>
         */
        public static final byte FF = 0x0C;
        /**
         * CARRIAGE RETURN (CR).<p>Value
         * <code>0x0D</code>.</p>
         */
        public static final byte CR = 0x0D;
        /**
         * SPACE (SP). <p>Value
         * <code>0x20</code>.</p>
         */
        public static final byte SP = 0x20;
    }

    /**
     * See
     * <pre>PDF 32000-1:2008</pre>
     * <code>7.2.2</code>: Table 2 – Delimiter characters.
     */
    public static class DelimiterCharacter {

        /**
         * LEFT PARENTHESIS:
         * <code>(</code>.
         */
        public static final byte LP = 0x28;
        /**
         * RIGHT PARENTHESIS:
         * <code>)</code>.
         */
        public static final byte RP = 0x29;
        /**
         * LESS-THAN SIGN:
         * <code>&#60;</code>.
         */
        public static final byte LT = 0x3C;
        /**
         * GREATER-THAN SIGN:
         * <code>&#62;</code>.
         */
        public static final byte GT = 0x3E;
        /**
         * LEFT SQUARE BRACKET:
         * <code>[</code>.
         */
        public static final byte LS = 0x5B;
        /**
         * RIGHT SQUARE BRACKET:
         * <code>]</code>.
         */
        public static final byte RS = 0x5D;
        /**
         * LEFT CURLY BRACKET:
         * <code>{</code>.
         */
        public static final byte LC = 0x7B;
        /**
         * RIGHT CURLY BRACKET:
         * <code>}</code>.
         */
        public static final byte RC = 0x7D;
        /**
         * SOLIDUS:
         * <code>/</code>.
         */
        public static final byte SO = 0x2F;
        /**
         * PERCENT SIGN:
         * <code>%</code>.
         */
        public static final byte PS = 0x25;
        /**
         * PERCENT SIGN:
         * <code>%</code>.
         *
         * @see #PS
         */
        public static final char PS_CHAR = '%';
    }
}
