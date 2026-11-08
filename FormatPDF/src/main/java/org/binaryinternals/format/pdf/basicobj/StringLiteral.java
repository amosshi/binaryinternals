package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Literal String, see `PDF 32000-1:2008` `7.3.4.2`:
/// Literal Strings.
///
/// @author Amos Shi
public final class StringLiteral extends FileComponent implements BasicObject {

    /// Escape character in a literal string.
    ///
    /// Within a literal string, the REVERSE SOLIDUS is used as an escape
    /// character. The character immediately following the REVERSE SOLIDUS
    /// determines its precise interpretation as shown in Table 3.
    public static final byte REVERSE_SOLIDUS = '\\';
    /// Literal String text in `Raw` format, the bytes between the outer
    /// parentheses.
    private final String RawText;
    /// Whether the closing parenthesis is found.
    private final boolean closed;

    StringLiteral(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        stream.skipNBytes(1);     // The '(' sign

        // Balanced pairs of parentheses within a string require no special treatment.
        // Example: (Strings may contain balanced parentheses ( ) and special characters (*!&}^% and so on).)
        final byte[] buf = stream.getBuf();
        final int rawStart = stream.getPos() - stream.getOffset();
        int level = 1;
        boolean found = false;
        while (stream.hasNext()) {
            final byte next = stream.readByte();
            if (next == REVERSE_SOLIDUS) {
                if (stream.hasNext()) {
                    stream.skipNBytes(1);   // The escaped character can not change the level
                }
            } else if (next == PDFStatics.DelimiterCharacter.LP) {
                level++;
            } else if (next == PDFStatics.DelimiterCharacter.RP && --level == 0) {
                found = true;
                break;
            }
        }

        this.closed = found;
        final int rawEnd = stream.getPos() - stream.getOffset() - (found ? 1 : 0);
        this.RawText = new String(buf, rawStart, rawEnd - rawStart, StandardCharsets.ISO_8859_1);
        super.length = stream.getPos() - super.startPos;
    }

    /// The string with the escape sequences of Table 3 and the end-of-line
    /// markers decoded. Each character is one byte of the string.
    ///
    /// @return The string
    public String getString() {
        return unescape(this.RawText);
    }

    static String unescape(final String raw) {
        final int n = raw.length();
        final StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            final char c = raw.charAt(i);
            if (c == '\r') {
                // An end-of-line marker is treated as a single LINE FEED
                sb.append('\n');
                if (i + 1 < n && raw.charAt(i + 1) == '\n') {
                    i++;
                }
                continue;
            }
            if (c != REVERSE_SOLIDUS || i + 1 == n) {
                sb.append(c);
                continue;
            }

            final char e = raw.charAt(++i);
            switch (e) {
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case '(', ')', '\\' -> sb.append(e);
                case '\r' -> {
                    // Line continuation, the backslash and end-of-line marker are ignored
                    if (i + 1 < n && raw.charAt(i + 1) == '\n') {
                        i++;
                    }
                }
                case '\n' -> {
                    // Line continuation
                }
                default -> {
                    if (isOctalDigit(e)) {
                        // \ddd, one to three octal digits, high-order overflow is ignored
                        int value = 0;
                        int j = i;
                        while (j < n && j < i + 3 && isOctalDigit(raw.charAt(j))) {
                            value = value * 8 + (raw.charAt(j) - '0');
                            j++;
                        }
                        sb.append((char) (value & 0xFF));
                        i = j - 1;
                    } else {
                        sb.append(e);     // The REVERSE SOLIDUS is ignored
                    }
                }
            }
        }
        return sb.toString();
    }

    private static boolean isOctalDigit(final char c) {
        return c >= '0' && c <= '7';
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Literal String");
        nodeComp.setDescription("Raw String Text: " + this.RawText);
        final DefaultMutableTreeNode nodeStr = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.LP)));
        pos += 1;

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.RawText.length(),
                "Raw Data")));
        pos += this.RawText.length();

        if (this.closed) {
            nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    1,
                    Texts.Signature + (char) PDFStatics.DelimiterCharacter.RP)));
        }

        parentNode.add(nodeStr);
    }

    @Override
    public String toString() {
        return "Literal String Object: Start Position = %d, Length = %d, Raw Text = '%s'".formatted(
                super.startPos,
                super.length,
                this.RawText);
    }
}
