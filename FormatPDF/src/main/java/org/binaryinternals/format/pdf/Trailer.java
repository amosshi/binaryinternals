package org.binaryinternals.format.pdf;

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
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;
import org.binaryinternals.format.pdf.basicobj.Analysis;

/// PDF File Trailer, see `PDF 32000-1:2008` `7.5.5`: File Trailer.
///
/// ```
/// trailer
///     << key1 value1
///        key2 value2
///        ...
///        keyn valuen
///     >>
/// ```
///
/// @author Amos Shi
public final class Trailer extends FileComponent implements GenerateTreeNode {

    private static final Logger LOGGER = Logger.getLogger(Trailer.class.getName());

    static final String SIGNATURE = "trailer";
    /// The first line of current object.
    public final ASCIILine HeaderLine;
    /// Position of the [#SIGNATURE] keyword.
    private final int signaturePos;
    /// The trailer dictionary and white-space.
    private final List<FileComponent> components = new ArrayList<>();

    Trailer(final PosDataInputStream stream, final ASCIILine line) throws IOException {
        this.HeaderLine = line;
        super.startPos = stream.getPos() - line.length();
        this.signaturePos = super.startPos + line.line().indexOf(SIGNATURE);

        // The trailer lasts until 'startxref'
        while (stream.hasNext()) {
            final ASCIILine next = stream.readASCIILine();
            if (StartXRef.SIGNATURE.equals(next.line().strip())) {
                stream.backward(next.length());
                break;
            }
        }
        super.length = stream.getPos() - super.startPos;

        final int contentStart = this.signaturePos + SIGNATURE.length();
        final int contentLength = super.startPos + super.length - contentStart;
        if (contentLength > 0) {
            this.parseContent(stream.getPartialStream(contentStart - stream.getOffset(), contentLength));
        }
    }

    private void parseContent(final PosDataInputStream content) throws IOException {
        while (content.hasNext()) {
            if (Analysis.parseNextObject(content, this.components) == null) {
                final byte next = content.readByte();
                LOGGER.log(Level.WARNING, "Unexpected byte in trailer at {0}: ''{1}''",
                        new Object[]{content.getPos() - 1, (char) (next & 0xFF)});
            }
        }
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeTrailer = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Trailer"));
        parentNode.add(nodeTrailer);

        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.signaturePos,
                SIGNATURE.length(),
                Texts.Signature + SIGNATURE)));

        final int contentStart = this.signaturePos + SIGNATURE.length();
        final int len = super.startPos + super.length - contentStart;
        if (len > 0) {
            final DefaultMutableTreeNode nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    contentStart,
                    len,
                    "Trailer Content"));
            nodeTrailer.add(nodeContent);
            for (final FileComponent comp : this.components) {
                if (comp instanceof GenerateTreeNode node) {
                    node.generateTreeNode(nodeContent);
                }
            }
        }
    }
}
