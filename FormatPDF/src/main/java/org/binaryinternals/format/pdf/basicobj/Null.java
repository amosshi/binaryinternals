package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;

/// PDF basic object Null, see `PDF 32000-1:2008` `7.3.9`: Null Object.
///
/// @author Amos Shi
public final class Null extends FileComponent implements BasicObject {

    public static final String VALUE = "null";
    /// Null text in `Raw` format.
    private final String RawText;

    Null(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.RawText = PDFStatics.readRegularCharacters(stream);
        this.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Null Object"));

        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawText)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return "Null Object: Start Position = %d (0x%X), Length = %d, Raw Text = '%s'".formatted(
                super.startPos,
                super.startPos,
                super.length,
                this.RawText);
    }

    public static final class StartByte {

        private StartByte() {
        }

        /// Start byte for `null`.
        public static final byte NULL = 'n';
    }
}
