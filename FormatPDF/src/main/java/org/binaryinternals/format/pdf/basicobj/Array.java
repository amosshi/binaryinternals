package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Array, see `PDF 32000-1:2008` `7.3.6`: Array Objects.
///
/// @author Amos Shi
public final class Array extends FileComponent implements BasicObject {

    private static final Logger LOGGER = Logger.getLogger(Array.class.getName());

    /// Components of current object, including white-space.
    private final List<FileComponent> components = new ArrayList<>();
    /// Whether the closing `]` is found.
    private final boolean closed;

    Array(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.closed = this.parse(stream);
        super.length = stream.getPos() - super.startPos;
    }

    private boolean parse(final PosDataInputStream stream) throws IOException {
        stream.skipNBytes(1);     // The '[' sign

        while (stream.hasNext()) {
            if (Analysis.parseNextObject(stream, this.components) == null) {
                final byte next = stream.readByte();
                if (next == PDFStatics.DelimiterCharacter.RS) {
                    return true;
                }
                LOGGER.log(Level.WARNING, () -> "Unexpected byte in Array at %d: '%c'".formatted(
                        stream.getPos() - 1, (char) Byte.toUnsignedInt(next)));
            }
        }
        return false;
    }

    /// The elements of the array.
    ///
    /// @return The elements, without white-space
    public List<BasicObject> getElements() {
        return this.components.stream()
                .filter(BasicObject.class::isInstance)
                .map(BasicObject.class::cast)
                .toList();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeArray = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Array Object"));

        int pos = super.startPos;
        nodeArray.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.LS)));
        pos += 1;

        final int contLen = super.length - (this.closed ? 2 : 1);
        final DefaultMutableTreeNode nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                contLen,
                "Array Content"));
        nodeArray.add(nodeContent);

        for (final FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(nodeContent);
            }
        }

        if (this.closed) {
            nodeArray.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + contLen,
                    1,
                    Texts.Signature + (char) PDFStatics.DelimiterCharacter.RS)));
        }

        parentNode.add(nodeArray);
    }
}
