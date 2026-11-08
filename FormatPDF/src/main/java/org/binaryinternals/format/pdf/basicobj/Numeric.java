package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;

/// PDF basic object Integer or Real number, see `PDF 32000-1:2008` `7.3.3`:
/// Numeric Objects.
///
/// @author Amos
public final class Numeric extends FileComponent implements BasicObject {

    /// Numeric text in `Raw` format.
    private final String RawNumberText;

    Numeric(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.RawNumberText = PDFStatics.readRegularCharacters(stream);
        this.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeNum = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Numeric Object: " + this.RawNumberText));

        nodeNum.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawNumberText)));

        parentNode.add(nodeNum);
    }

    @Override
    public String toString() {
        return "Numeric Object: Start Position = %d, Length = %d, Raw Text = '%s'".formatted(
                super.startPos,
                super.length,
                this.RawNumberText);
    }

    public static final class StartByte {

        /// Sign '+'.
        public static final byte SIGN_ADD = 0x2B;
        /// Sign '-'.
        public static final byte SIGN_MINUS = 0x2D;
        /// Decimal point '.', for a real number like `.5`.
        public static final byte DECIMAL_POINT = 0x2E;
        /// Number '0'.
        public static final byte NUMBER_0 = 0x30;
        /// Number '1'.
        public static final byte NUMBER_1 = 0x31;
        /// Number '2'.
        public static final byte NUMBER_2 = 0x32;
        /// Number '3'.
        public static final byte NUMBER_3 = 0x33;
        /// Number '4'.
        public static final byte NUMBER_4 = 0x34;
        /// Number '5'.
        public static final byte NUMBER_5 = 0x35;
        /// Number '6'.
        public static final byte NUMBER_6 = 0x36;
        /// Number '7'.
        public static final byte NUMBER_7 = 0x37;
        /// Number '8'.
        public static final byte NUMBER_8 = 0x38;
        /// Number '9'.
        public static final byte NUMBER_9 = 0x39;

        private StartByte() {
        }
    }
}
