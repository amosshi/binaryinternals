package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF `startxref` Object in File Trailer, see `PDF 32000-1:2008` `7.5.5`:
/// File Trailer.
///
/// @author Amos Shi
public final class StartXRef extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "startxref";
    /// The first line of current object.
    public final ASCIILine HeaderLine;
    /// Offset of the last cross-reference section.
    public final int Offset;
    /// [#Offset] line.
    public final ASCIILine OffsetLine;

    StartXRef(final PosDataInputStream stream, final ASCIILine line) throws IOException {
        super.startPos = stream.getPos() - line.length();
        this.HeaderLine = line;
        this.OffsetLine = stream.readASCIILine();
        try {
            this.Offset = Integer.parseInt(this.OffsetLine.line().strip());
        } catch (NumberFormatException ex) {
            throw new FileFormatException("Invalid 'startxref' offset at %d: '%s'".formatted(
                    stream.getPos() - this.OffsetLine.length(), this.OffsetLine.line()), ex);
        }
        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeTrailer = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Start X Ref"));
        parentNode.add(nodeTrailer);

        int pos = this.startPos;
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.line().length(),
                Texts.Signature + SIGNATURE)));
        pos += this.HeaderLine.line().length();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.newLineLength(),
                Texts.NewLine)));
        pos += this.HeaderLine.newLineLength();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.OffsetLine.line().length(),
                "Offset of last Cross Reference Section: %d (%08X)".formatted(this.Offset, this.Offset))));
        pos += this.OffsetLine.line().length();
        if (this.OffsetLine.newLineLength() > 0) {
            nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.OffsetLine.newLineLength(),
                    Texts.NewLine)));
        }
    }
}
