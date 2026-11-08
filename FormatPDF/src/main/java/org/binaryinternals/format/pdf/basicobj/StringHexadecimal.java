package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Hexadecimal String, see `PDF 32000-1:2008` `7.3.4.3`:
/// Hexadecimal Strings.
///
/// @author Amos Shi
public final class StringHexadecimal extends FileComponent implements BasicObject {

    /// The hexadecimal digits, without white-space.
    private final String RawText;
    /// Whether the closing `>` is found.
    private final boolean closed;

    /// Each pair of hexadecimal digits defines one byte of the string.
    ///
    /// White-space characters (such as SPACE (20h), HORIZONTAL TAB (09h),
    /// CARRIAGE RETURN (0Dh), LINE FEED (0Ah), and FORM FEED (0Ch)) shall be
    /// ignored.
    StringHexadecimal(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        stream.skipNBytes(1);     // The '<' sign

        final StringBuilder sb = new StringBuilder(100);
        boolean found = false;
        while (stream.hasNext()) {
            final byte b = stream.readByte();
            if (b == PDFStatics.DelimiterCharacter.GT) {
                found = true;
                break;
            }
            if (!PDFStatics.isWhiteSpace(b)) {
                sb.append((char) (b & 0xFF));
            }
        }

        this.closed = found;
        this.RawText = sb.toString();
        super.length = stream.getPos() - super.startPos;
    }

    /// Get the String format of the Hexadecimal String.
    ///
    /// A hexadecimal string shall be written as a sequence of hexadecimal
    /// digits (0–9 and either A–F or a–f) encoded as ASCII characters and
    /// enclosed within angle brackets. If the final digit of a hexadecimal
    /// string is missing—that is, if there is an odd number of digits—the
    /// final digit shall be assumed to be 0. Characters other than hexadecimal
    /// digits are ignored.
    ///
    /// @return The string, each character is one byte
    public String getString() {
        final StringBuilder digits = new StringBuilder(this.RawText.length() + 1);
        this.RawText.chars().filter(HexFormat::isHexDigit).forEach(c -> digits.append((char) c));
        if (digits.length() % 2 != 0) {
            digits.append('0');
        }
        return new String(HexFormat.of().parseHex(digits), StandardCharsets.ISO_8859_1);
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Hexadecimal String");
        nodeComp.setDescription("Raw String Text: " + this.RawText);
        final DefaultMutableTreeNode nodeStr = new DefaultMutableTreeNode(nodeComp);

        final int dataLength = super.length - (this.closed ? 2 : 1);
        int pos = super.startPos;
        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.LT)));
        pos += 1;

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                dataLength,
                "Raw Data")));
        pos += dataLength;

        if (this.closed) {
            nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    1,
                    Texts.Signature + (char) PDFStatics.DelimiterCharacter.GT)));
        }

        parentNode.add(nodeStr);
    }

    @Override
    public String toString() {
        return "Hexadecimal String Object: Start Position = %d, Length = %d, Raw Text = '%s'".formatted(
                super.startPos,
                super.length,
                this.RawText);
    }
}
