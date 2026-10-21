/*
 * PDFFile.java May 17, 2011, 09:29
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved. 
 * Use is subject to license terms.
 */
package org.binaryinternals.format.pdf;

import java.nio.file.Path;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 * PDF File Object.
 *
 * @author Amos Shi
 * @see <a href="http://www.adobe.com/devnet/pdf/pdf_reference.html">PDF
 * Reference</a>
 */
public class PDFFile extends FileFormat {

    public PDFFile(final Path file) throws IOException, FileFormatException {
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
        super.components.put(Long.valueOf(header.getStartPos()), header);

        // Read PDF Components
        PosDataInputStream.ASCIILine line;
        while (stream.hasNext()) {
            line = stream.readASCIILine();

            if (line.line().equalsIgnoreCase(EndOfFile.SIGNATURE)) {                          // %%EOF
                addComponent(new EndOfFile(stream, line));
            } else if (line.line().charAt(0) == PDFStatics.DelimiterCharacter.PS_CHAR) {      // %, Comment line
                addComponent(new Comment(stream, line));
            } else if (line.line().matches(IndirectObject.SIGNATURE_START_REGEXP)) {          // 1 0 obj 
                addComponent(new IndirectObject(stream, line));
            } else if (line.line().trim().equalsIgnoreCase(CrossReferenceTable.SIGNATURE)) {  // xref
                addComponent(new CrossReferenceTable(stream, line));
            } else if (line.line().equalsIgnoreCase(Trailer.SIGNATURE)) {                     // trailer
                addComponent(new Trailer(stream, line));
            } else if (line.line().equalsIgnoreCase(StartXRef.SIGNATURE)) {                   // startxref
                addComponent(new StartXRef(stream, line));
            } else {
                addComponent(new EmptyLine(stream, line));
            }

        } // End While
    }

    private void addComponent(final FileComponent component) {
        super.components.put(Long.valueOf(component.getStartPos()), component);
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
