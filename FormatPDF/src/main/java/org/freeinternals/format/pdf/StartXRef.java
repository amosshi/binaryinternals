package org.freeinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 * PDF
 * <code>startxref</code> Object in File Trailer, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.5.5</code>: File Trailer.
 *
 * @author Amos Shi
 */
public class StartXRef extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "startxref";
    /**
     * The first line of current object.
     */
    public final ASCIILine HeaderLine;
    /**
     * Offset of the last cross-reference section.
     */
    public final int Offset;
    /**
     * {@link #Offset} line.
     */
    public final ASCIILine OffsetLine;

    StartXRef(PosDataInputStream stream, ASCIILine line) throws IOException, FileFormatException {
        super.startPos = stream.getPos() - line.Length();
        this.HeaderLine = line;
        this.OffsetLine = stream.readASCIILine();
        this.Offset = Integer.valueOf(this.OffsetLine.Line);
        super.length = stream.getPos() - super.startPos;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Start X Ref");
        DefaultMutableTreeNode nodeTrailer = new DefaultMutableTreeNode(nodeComp);
        parentNode.add(nodeTrailer);

        int pos = this.startPos;
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.Line.length(),
                Texts.Signature + SIGNATURE)));
        pos += this.HeaderLine.Line.length();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.NewLineLength,
                Texts.NewLine)));
        pos += this.HeaderLine.NewLineLength;
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.OffsetLine.Line.length(),
                String.format("Offset of last Cross Reference Section: %d (%08X)", this.Offset, this.Offset))));
        pos += this.OffsetLine.Line.length();
        nodeTrailer.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.OffsetLine.NewLineLength,
                Texts.NewLine)));
    }
}
