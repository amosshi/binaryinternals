package org.binaryinternals.format.pdf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Static values of PDF file definition.
///
/// @author Amos
public final class PDFStatics {

    private PDFStatics() {
    }

    /// Whether `b` is a white-space character, see [WhiteSpace].
    ///
    /// @param b The byte
    /// @return `true` for a white-space character
    public static boolean isWhiteSpace(final byte b) {
        return switch (b) {
            case WhiteSpace.NUL, WhiteSpace.HT, WhiteSpace.LF, WhiteSpace.FF, WhiteSpace.CR, WhiteSpace.SP -> true;
            default -> false;
        };
    }

    /// Whether `b` is a delimiter character, see [DelimiterCharacter].
    ///
    /// @param b The byte
    /// @return `true` for a delimiter character
    public static boolean isDelimiter(final byte b) {
        return switch (b) {
            case DelimiterCharacter.LP, DelimiterCharacter.RP,
                    DelimiterCharacter.LT, DelimiterCharacter.GT,
                    DelimiterCharacter.LS, DelimiterCharacter.RS,
                    DelimiterCharacter.LC, DelimiterCharacter.RC,
                    DelimiterCharacter.SO, DelimiterCharacter.PS -> true;
            default -> false;
        };
    }

    /// Read the regular characters, until a white-space or delimiter character,
    /// which is not consumed. Used for names, numbers and keywords, see
    /// `PDF 32000-1:2008` `7.2.2`.
    ///
    /// @param stream The stream
    /// @return The characters, may be empty
    /// @throws IOException Read failed
    public static String readRegularCharacters(final PosDataInputStream stream) throws IOException {
        final byte[] buf = stream.getBuf();
        final int start = stream.getPos() - stream.getOffset();
        int end = start;
        while (end < buf.length && !isWhiteSpace(buf[end]) && !isDelimiter(buf[end])) {
            end++;
        }
        stream.skipNBytes(end - start);
        return new String(buf, start, end - start, StandardCharsets.ISO_8859_1);
    }

    /// See `PDF 32000-1:2008` `7.2.2`: Table 1 – White-space characters.
    public static final class WhiteSpace {

        /// Null (NUL), value `0x00`.
        public static final byte NUL = 0x00;
        /// HORIZONTAL TAB (HT), value `0x09`.
        public static final byte HT = 0x09;
        /// LINE FEED (LF), new line character, value `0x0A`.
        public static final byte LF = 0x0A;
        /// FORM FEED (FF), value `0x0C`.
        public static final byte FF = 0x0C;
        /// CARRIAGE RETURN (CR), value `0x0D`.
        public static final byte CR = 0x0D;
        /// SPACE (SP), value `0x20`.
        public static final byte SP = 0x20;

        private WhiteSpace() {
        }
    }

    /// See `PDF 32000-1:2008` `7.2.2`: Table 2 – Delimiter characters.
    public static final class DelimiterCharacter {

        /// LEFT PARENTHESIS: `(`.
        public static final byte LP = 0x28;
        /// RIGHT PARENTHESIS: `)`.
        public static final byte RP = 0x29;
        /// LESS-THAN SIGN: `<`.
        public static final byte LT = 0x3C;
        /// GREATER-THAN SIGN: `>`.
        public static final byte GT = 0x3E;
        /// LEFT SQUARE BRACKET: `[`.
        public static final byte LS = 0x5B;
        /// RIGHT SQUARE BRACKET: `]`.
        public static final byte RS = 0x5D;
        /// LEFT CURLY BRACKET: `{`.
        public static final byte LC = 0x7B;
        /// RIGHT CURLY BRACKET: `}`.
        public static final byte RC = 0x7D;
        /// SOLIDUS: `/`.
        public static final byte SO = 0x2F;
        /// PERCENT SIGN: `%`.
        public static final byte PS = 0x25;
        /// PERCENT SIGN: `%`.
        ///
        /// @see #PS
        public static final char PS_CHAR = '%';

        private DelimiterCharacter() {
        }
    }
}
