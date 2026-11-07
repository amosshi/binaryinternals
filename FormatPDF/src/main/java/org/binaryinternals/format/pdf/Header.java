package org.binaryinternals.format.pdf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF file Header, see `PDF 32000-1:2008` `7.5.2`: File Header.
///
/// @author Amos Shi
public final class Header extends FileComponent implements GenerateTreeNode {

    /// The first line of a PDF file shall be a header consisting of the 5
    /// characters `%PDF-` followed by a version number of the form 1.N, where N
    /// is a digit between 0 and 7.
    public static final String PDF_HEADER = "%PDF-";
    /// PDF file version, like `1.0`, `1.1` ... `1.7`, `2.0`.
    public final ASCIILine Version;

    /// Read the version.
    ///
    /// @param stream Stream positioned after [#PDF_HEADER]
    Header(final PosDataInputStream stream) throws IOException {
        super.startPos = 0;
        this.Version = stream.readASCIILine();
        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                this.getStartPos(),
                super.length,
                "PDF Header: version = %s".formatted(this.Version.line()));
        nodeComp.setDescription(Texts.getString(Texts.PDF_FILE_HEADER));
        final DefaultMutableTreeNode treenodePDFHeader = new DefaultMutableTreeNode(nodeComp);

        int pos = this.startPos;
        treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                PDF_HEADER.length(),
                "PDF Signature")));
        pos += PDF_HEADER.length();
        treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.Version.line().length(),
                "PDF Version = " + this.Version)));
        pos += this.Version.line().length();
        if (this.Version.newLineLength() > 0) {
            treenodePDFHeader.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.Version.newLineLength(),
                    Texts.NewLine)));
        }
        parentNode.add(treenodePDFHeader);
    }
}
