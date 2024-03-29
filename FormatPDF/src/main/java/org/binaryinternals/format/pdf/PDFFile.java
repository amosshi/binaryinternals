/**
 * PDFFile.java May 17, 2011, 09:29
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved. 
 * Use is subject to license terms.
 */
package org.binaryinternals.format.pdf;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.core.FileFormatException;

/**
 * PDF File Object.
 *
 * @author Amos Shi
 * @see <a href="http://www.adobe.com/devnet/pdf/pdf_reference.html">PDF
 * Reference</a>
 */
public class PDFFile extends FileFormat {

    public PDFFile(final File file) throws IOException, FileFormatException {
        super(file);
        this.parse();
    }

    private void parse() throws IOException, FileFormatException {
        PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // File Header - Verify if this is an PDF file
        String pdfHeader = stream.readASCII(Header.PDF_HEADER.length());
        if (!Header.PDF_HEADER.equals(pdfHeader)) {
            throw new FileFormatException(String.format(
                    "This is not a PDF file. Expeted file header [%s], but it is [%s].",
                    Header.PDF_HEADER,
                    pdfHeader));
        }
        Header header = new Header(stream);
        super.addFileComponent(header);

        // Read PDF Components
        PosDataInputStream.ASCIILine line;
        while (stream.hasNext()) {
            line = stream.readASCIILine();

            if (line.line.equalsIgnoreCase(EndOfFile.SIGNATURE)) {                          // %%EOF
                super.addFileComponent(new EndOfFile(stream, line));
            } else if (line.line.charAt(0) == PDFStatics.DelimiterCharacter.PS_CHAR) {      // %, Comment line
                super.addFileComponent(new Comment(stream, line));
            } else if (line.line.matches(IndirectObject.SIGNATURE_START_REGEXP)) {          // 1 0 obj 
                super.addFileComponent(new IndirectObject(stream, line));
            } else if (line.line.trim().equalsIgnoreCase(CrossReferenceTable.SIGNATURE)) {  // xref
                super.addFileComponent(new CrossReferenceTable(stream, line));
            } else if (line.line.equalsIgnoreCase(Trailer.SIGNATURE)) {                     // trailer
                super.addFileComponent(new Trailer(stream, line));
            } else if (line.line.equalsIgnoreCase(StartXRef.SIGNATURE)) {                   // startxref
                super.addFileComponent(new StartXRef(stream, line));
            } else {
                super.addFileComponent(new EmptyLine(stream, line));
            }
        } // End While
    }

    @Override
    public String getContentTabName() {
        return "PDF File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode root) {
        super.components.values().stream().filter(comp -> (comp instanceof GenerateTreeNode)).forEachOrdered(comp -> {
            ((GenerateTreeNode) comp).generateTreeNode(root);
        });
    }
}
