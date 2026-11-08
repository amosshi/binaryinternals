package org.binaryinternals.format.pdf;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF End of File (`%%EOF`) marker, see `PDF 32000-1:2008` `7.5.5`: File
/// Trailer.
///
/// @author Amos Shi
public final class EndOfFile extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "%%EOF";
    /// The content of current line.
    public final ASCIILine Line;

    EndOfFile(final PosDataInputStream stream, final ASCIILine line) {
        super(stream.getPos() - line.length(), line.length());
        this.Line = line;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeEoF = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "End of File"));

        nodeEoF.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                this.Line.line().length(),
                Texts.Signature + SIGNATURE)));
        if (this.Line.newLineLength() > 0) {
            nodeEoF.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + this.Line.line().length(),
                    this.Line.newLineLength(),
                    Texts.NewLine)));
        }
        parentNode.add(nodeEoF);
    }
}
