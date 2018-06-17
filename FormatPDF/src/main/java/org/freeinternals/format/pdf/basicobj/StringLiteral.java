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
 * PDF basic object Literal String, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.4.2</code>: Literal Strings.
 *
 * @author Amos Shi
 */
class StringLiteral extends FileComponent implements GenerateTreeNode {

    /**
     * Escape character in a literal string. <p> Within a literal string, the
     * REVERSE SOLIDUS is used as an escape character. The character immediately
     * following the REVERSE SOLIDUS determines its precise interpretation as
     * shown in Table 3. </p>
     */
    public static final byte REVERSE_SOLIDUS = '\\';
    /**
     * Literal String text in
     * <code>Raw</code> format.
     */
    private String RawText;

    public StringLiteral(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.parse(stream);
        super.length = stream.getPos() - super.startPos;

        // System.out.println(this.toString());   // Deubg output
    }

    private void parse(PosDataInputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder(32);

        // Parentheses Level.
        // - Balanced pairs of parentheses within a string require no special treatment.
        // - Example: (Strings may contain balanced parentheses ( ) and special characters (*!&}^% and so on).)
        int parentheseLevel = 0;  // 

        boolean stop = false;
        while (stream.hasNext()) {
            byte next = stream.readByte();
            switch (next) {
                // Escape 
                case REVERSE_SOLIDUS:
                    // Skip the charactor after REVERSE SOLIDUS
                    stream.skip(1);
                    break;
                case PDFStatics.DelimiterCharacter.LP:
                    parentheseLevel++;
                    break;
                case PDFStatics.DelimiterCharacter.RP:
                    parentheseLevel--;
                    if (parentheseLevel <= 0) {
                        // To the end of current Liternal String
                        stop = true;
                    }
                    break;
                default:
                    builder.append((char) next);
                    break;
            }
            if (stop) {
                break;            // Stop current while-loop
            }
        }

        this.RawText = builder.toString();
    }

    public String getString() {
        // TODO - Change to the .getString()
        return this.RawText;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Literal String");
        nodeComp.setDescription("Raw String Text: " + this.RawText);
        DefaultMutableTreeNode nodeStr = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.LP)));
        pos += 1;

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                super.length - 2,
                "Raw Data")));
        pos += (super.length - 2);

        nodeStr.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.RP)));

        parentNode.add(nodeStr);
    }

    @Override
    public String toString() {
        return String.format("Literal String Object: Start Position = %d, Length = %d, Raw Text = '%s'",
                super.startPos,
                super.length,
                this.RawText);
    }
}
