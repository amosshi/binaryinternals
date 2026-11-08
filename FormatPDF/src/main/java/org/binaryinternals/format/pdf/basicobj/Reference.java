package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Reference of PDF Indirect Object, see `PDF 32000-1:2008` `7.3.10`:
/// Indirect Object.
///
/// @author Amos Shi
public final class Reference extends FileComponent implements BasicObject {

    static final byte SIGNATURE = 'R';
    public final byte Value;
    /// The object number, generation number and white-space before the `R`.
    final List<FileComponent> formerComponents = new ArrayList<>(3);

    Reference(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.Value = stream.readByte();
        this.length = stream.getPos() - super.startPos;
    }

    /// Length of the former objects.
    ///
    /// @return The length
    public int getFormerLength() {
        return this.formerComponents.stream().mapToInt(FileComponent::getLength).sum();
    }

    /// Start position, including the object number and generation number.
    @Override
    public int getStartPos() {
        return super.startPos - this.getFormerLength();
    }

    /// Length, including the object number and generation number.
    @Override
    public int getLength() {
        return this.getFormerLength() + this.length;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeRef = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos(),
                this.getLength(),
                "Reference to Indirect Object"));

        for (final FileComponent comp : this.formerComponents) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(nodeRef);
            }
        }

        nodeRef.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                1,
                String.valueOf((char) this.Value))));

        parentNode.add(nodeRef);
    }

    @Override
    public String toString() {
        return "Reference to Indirect Object: Start Position = %d, Length = %d, Raw Value = '%s'".formatted(
                super.startPos,
                super.length,
                (char) this.Value);
    }
}
