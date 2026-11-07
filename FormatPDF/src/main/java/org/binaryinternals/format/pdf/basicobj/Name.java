package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.HexFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Name, see `PDF 32000-1:2008` `7.3.5`: Name Objects.
///
/// @author Amos Shi
public final class Name extends FileComponent implements BasicObject {

    /// Prefix of a two-digit hexadecimal code in a name, like `#20` for a space.
    private static final char NUMBER_SIGN = '#';

    /// Name text in `Raw` format, without the leading `/`.
    private final String RawName;

    Name(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        stream.skipNBytes(1);
        this.RawName = PDFStatics.readRegularCharacters(stream);
        this.length = stream.getPos() - super.startPos; // The length includes 2 parts: '/' RawNameText
    }

    /// The name, with the `#xx` hexadecimal codes decoded.
    ///
    /// @return The name
    public String getName() {
        return decode(this.RawName);
    }

    static String decode(final String raw) {
        if (raw.indexOf(NUMBER_SIGN) < 0) {
            return raw;
        }

        final StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            final char c = raw.charAt(i);
            if (c == NUMBER_SIGN && i + 2 < raw.length()
                    && HexFormat.isHexDigit(raw.charAt(i + 1))
                    && HexFormat.isHexDigit(raw.charAt(i + 2))) {
                sb.append((char) HexFormat.fromHexDigits(raw, i + 1, i + 3));
                i += 2;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Name Object: " + this.getName()));

        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.SO)));
        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                super.length - 1,
                "Name = " + this.RawName)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return "Name Object: Start Position = %d, Length = %d, Raw Name = '%s'".formatted(
                super.startPos,
                super.length,
                this.RawName);
    }
}
