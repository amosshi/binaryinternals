package org.freeinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream.ASCIILine;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * PDF file Header. , see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.5.2</code>: File Header.
 *
 * @author Amos Shi
 */
public class Header extends FileComponent implements GenerateTreeNode {

    /**
     * The first line of a PDF file shall be a header consisting of the 5
     * characters %PDF– followed by a version number of the form 1.N, where N is
     * a digit between 0 and 7.
     */
    public static final String PDF_HEADER = "%PDF-";
    /**
     * PDF file version. Example:
     * <code>1.0</code>,
     * <code>1.1</code>,
     * <code>1.2</code>,
     * <code>1.3</code>,
     * <code>1.4</code>,
     * <code>1.5</code>,
     * <code>1.6</code>,
     * <code>1.7</code>, etc.
     */
    public final ASCIILine Version;

    Header(PosDataInputStream stream) throws IOException {
        super.startPos = 0;
        this.Version = stream.readASCIILine();
        super.length = stream.getPos() - super.startPos;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                this.getStartPos(),
                super.length,
                String.format("PDF Header: version = %s", this.Version.Line));
        nodeComp.setDescription(Texts.getString(Texts.PDF_FILE_HEADER));
        DefaultMutableTreeNode treenodePDFHeader = new DefaultMutableTreeNode(nodeComp);

        int pos = this.startPos;
        treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                PDF_HEADER.length(),
                "PDF Signature")));
        pos += PDF_HEADER.length();
        treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.Version.Line.length(),
                "PDF Version = " + this.Version)));
        pos += this.Version.Line.length();
        treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.Version.NewLineLength,
                Texts.NewLine)));
        parentNode.add(treenodePDFHeader);
    }
}
