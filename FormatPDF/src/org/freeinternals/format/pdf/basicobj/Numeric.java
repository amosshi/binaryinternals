package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.pdf.PDFStatics;

/**
 *
 * @author Amos
 */
public class Numeric extends FileComponent implements GenerateTreeNode {

    /**
     * Numeric text in
     * <code>Raw</code> format.
     */
    private final String RawNumberText;

    Numeric(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.RawNumberText = stream.readASCIIUntil(
                PDFStatics.WhiteSpace.SP,
                PDFStatics.WhiteSpace.CR,
                PDFStatics.WhiteSpace.LF,
                PDFStatics.DelimiterCharacter.LP,
                PDFStatics.DelimiterCharacter.RP,
                PDFStatics.DelimiterCharacter.LT,
                PDFStatics.DelimiterCharacter.GT,
                PDFStatics.DelimiterCharacter.LS,
                PDFStatics.DelimiterCharacter.RS,
                PDFStatics.DelimiterCharacter.LC,
                PDFStatics.DelimiterCharacter.RC,
                PDFStatics.DelimiterCharacter.SO,
                PDFStatics.DelimiterCharacter.PS);
        stream.backward(1);
        this.length = stream.getPos() - super.startPos;

        // System.out.println(this.toString());   // Deubg output
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Numeric Object: " + this.RawNumberText);
        DefaultMutableTreeNode nodeNum = new DefaultMutableTreeNode(nodeComp);

        nodeNum.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawNumberText)));

        parentNode.add(nodeNum);
    }
    
    @Override
    public String toString() {
        return String.format("Numeric Object: Start Position = %d, Length = %d, Raw Text = '%s'",
                super.startPos,
                super.length,
                this.RawNumberText);
    }    

    public static class StartByte {

        /**
         * Sign '+'.
         */
        public static final byte SIGN_ADD = 0x2B;
        /**
         * Sign '-'.
         */
        public static final byte SIGN_MINUS = 0x2D;
        /**
         * Number '0'.
         */
        public static final byte NUMBER_0 = 0x30;
        /**
         * Number '1'.
         */
        public static final byte NUMBER_1 = 0x31;
        /**
         * Number '2'.
         */
        public static final byte NUMBER_2 = 0x32;
        /**
         * Number '3'.
         */
        public static final byte NUMBER_3 = 0x33;
        /**
         * Number '4'.
         */
        public static final byte NUMBER_4 = 0x34;
        /**
         * Number '5'.
         */
        public static final byte NUMBER_5 = 0x35;
        /**
         * Number '6'.
         */
        public static final byte NUMBER_6 = 0x36;
        /**
         * Number '7'.
         */
        public static final byte NUMBER_7 = 0x37;
        /**
         * Number '8'.
         */
        public static final byte NUMBER_8 = 0x38;
        /**
         * Number '9'.
         */
        public static final byte NUMBER_9 = 0x39;
    }
}
