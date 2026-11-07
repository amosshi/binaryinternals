/*
 * PDFFile.java May 17, 2011, 09:29
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.pdf;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF File Object.
///
/// The file is read line by line; each line starts a header, comment,
/// indirect object, cross-reference table, trailer, `startxref` or end of
/// file marker.
///
/// @author Amos Shi
/// @see <a href="https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/PDF32000_2008.pdf">PDF 32000-1:2008</a>
public final class PDFFile extends FileFormat {

    public PDFFile(final Path file) throws IOException {
        super(file);
        this.parse();
    }

    private void parse() throws IOException {
        final PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // File Header - Verify if this is an PDF file
        if (super.fileByteArray.length < Header.PDF_HEADER.length()) {
            throw new FileFormatException("This is not a PDF file. The file is shorter than the header [%s].".formatted(Header.PDF_HEADER));
        }
        final String pdfHeader = stream.readASCII(Header.PDF_HEADER.length());
        if (!Header.PDF_HEADER.equals(pdfHeader)) {
            throw new FileFormatException(
                    "This is not a PDF file. Expected file header [%s], but it is [%s].".formatted(Header.PDF_HEADER, pdfHeader));
        }
        this.addFileComponent(new Header(stream));

        // Read PDF Components
        while (stream.hasNext()) {
            final ASCIILine line = stream.readASCIILine();
            final String text = line.line().strip();

            final FileComponent component;
            if (text.equals(EndOfFile.SIGNATURE)) {                                                        // %%EOF
                component = new EndOfFile(stream, line);
            } else if (text.startsWith(Comment.SIGNATURE)) {                                               // %, Comment line
                component = new Comment(stream, line);
            } else if (IndirectObject.SIGNATURE_START_PATTERN.matcher(line.line()).lookingAt()) {          // 1 0 obj
                component = new IndirectObject(stream, line);
            } else if (text.equals(CrossReferenceTable.SIGNATURE)) {                                       // xref
                component = new CrossReferenceTable(stream, line);
            } else if (text.startsWith(Trailer.SIGNATURE)) {                                               // trailer
                component = new Trailer(stream, line);
            } else if (text.equals(StartXRef.SIGNATURE)) {                                                 // startxref
                component = new StartXRef(stream, line);
            } else {
                component = new EmptyLine(stream, line);
            }
            this.addFileComponent(component);
        }
    }

    @Override
    public String getContentTabName() {
        return "PDF File";
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode root) {
        for (final FileComponent comp : super.components.values()) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(root);
            }
        }
    }
}
