package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;

/// PDF basic object Boolean, see `PDF 32000-1:2008` `7.3.2`: Boolean Objects.
///
/// @author Amos Shi
public final class Boolean extends FileComponent implements BasicObject {

    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    /// Boolean text in `Raw` format.
    private final String RawText;

    Boolean(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.RawText = PDFStatics.readRegularCharacters(stream);
        this.length = stream.getPos() - super.startPos;
    }

    public boolean getValue() {
        return switch (this.RawText) {
            case VALUE_TRUE -> true;
            case VALUE_FALSE -> false;
            default -> throw new IllegalArgumentException(
                    "Unrecognized boolean value. Value text = '%s', Position = %d (0x%X)".formatted(
                            this.RawText,
                            this.startPos,
                            this.startPos));
        };
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Boolean Object"));

        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawText)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return "Boolean Object: Start Position = %d, Length = %d, Raw Text = '%s'".formatted(
                super.startPos,
                super.length,
                this.RawText);
    }

    public static final class StartByte {

        private StartByte() {
        }

        /// Start byte for `true`.
        public static final byte TRUE = 't';
        /// Start byte for `false`.
        public static final byte FALSE = 'f';
    }
}
