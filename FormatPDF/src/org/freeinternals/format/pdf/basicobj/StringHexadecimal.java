package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.pdf.PDFStatics;
import org.freeinternals.format.pdf.Texts;

/**
 * PDF basic object Hexadecimal String, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.4.3</code>: Hexadecimal Strings.
 *
 * @author Amos Shi
 */
public final class StringHexadecimal extends FileComponent implements GenerateTreeNode {

    /**
     * String text in
     * <code>Raw</code> format.
     */
    private String RawText;

    StringHexadecimal(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        stream.skip(1);
        //this.RawText = stream.readASCIIUntil(PDFStatics.DelimiterCharacter.GT);
        this.parse(stream);
        super.length = stream.getPos() - super.startPos;

        // System.out.println(this.toString());       // Deubg output
    }

    /**
     * Each pair of hexadecimal digits defines one byte of the string. <p>
     * White-space characters (such as SPACE (20h), HORIZONTAL TAB (09h),
     * CARRIAGE RETURN (0Dh), LINE FEED (0Ah), and FORM FEED (0Ch)) shall be
     * ignored. </p>
     */
    private void parse(PosDataInputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder(100);
        byte b;

        do {
            b = stream.readByte();
            if (b != PDFStatics.WhiteSpace.SP
                    && b != PDFStatics.WhiteSpace.HT
                    && b != PDFStatics.WhiteSpace.CR
                    && b != PDFStatics.WhiteSpace.LF
                    && b != PDFStatics.WhiteSpace.FF
                    && b != PDFStatics.WhiteSpace.NUL
                    && b != PDFStatics.DelimiterCharacter.GT) {
                sb.append((char) b);
            }
        } while (b != PDFStatics.DelimiterCharacter.GT && stream.hasNext());

        this.RawText = sb.toString();
    }

    /**
     * Get the String format of the Hexadecimal String. <p> A hexadecimal string
     * shall be written as a sequence of hexadecimal digits (0–9 and either A–F
     * or a–f) encoded as ASCII characters and enclosed within angle brackets
     * (using LESS-THAN SIGN (3Ch) and GREATER-THAN SIGN (3Eh)). </p> <p> If the
     * final digit of a hexadecimal string is missing—that is, if there is an
     * odd number of digits—the final digit shall be assumed to be 0. </p>
     */
    public String getString() {

        int len = this.RawText.length();
        int lenHalf;
        String text;
        StringBuilder sb = new StringBuilder(20);

        if (len > 0) {
            if (len != ((len >> 1) << 1)) {
                text = this.RawText + "0";
            } else {
                text = this.RawText;
            }

            lenHalf = text.length() / 2;
            for (int i = 0; i < lenHalf; i++) {
                int beginIndex = i * 2;
                sb.append((char) Integer.valueOf(
                        text.substring(beginIndex, beginIndex + 2),
                        16).byteValue());
            }
        }

        return sb.toString();
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Hexadecimal String");
        nodeComp.setDescription("Raw String Text: " + this.RawText);
        DefaultMutableTreeNode nodeStr = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + PDFStatics.DelimiterCharacter.LT)));
        pos += 1;

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                super.length - 2,
                "Raw Data")));
        pos += (super.length - 2);

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + PDFStatics.DelimiterCharacter.GT)));

        parentNode.add(nodeStr);
    }

    @Override
    public String toString() {
        return String.format("Hexadecimal String Object: Start Position = %d, Length = %d, Raw Text = '%s'",
                super.startPos,
                super.length,
                this.RawText);
    }
}
