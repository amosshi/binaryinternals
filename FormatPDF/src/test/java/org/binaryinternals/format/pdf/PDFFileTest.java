package org.binaryinternals.format.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PDFFileTest {

    @TempDir
    Path dir;

    private PDFFile parse(final String content) throws IOException {
        final Path file = this.dir.resolve("test.pdf");
        Files.write(file, content.getBytes(StandardCharsets.ISO_8859_1));
        return new PDFFile(file);
    }

    /// The top-level components shall cover the whole file without gaps.
    private static void assertContiguous(final PDFFile pdf) {
        int pos = 0;
        for (final FileComponent comp : pdf.getFileComponents()) {
            assertEquals(pos, comp.getStartPos(), comp.toString());
            pos += comp.getLength();
        }
        assertEquals(pdf.fileByteArray.length, pos);
    }

    @Test
    void endobjOnTheSameLine() throws IOException {
        final PDFFile pdf = this.parse("""
                %PDF-1.4
                1 0 obj
                << /Type /Catalog >>endobj
                2 0 obj
                << /Length 5 >>
                stream
                abcde
                endstream endobj
                %%EOF""");
        assertContiguous(pdf);
        final List<FileComponent> objects = pdf.getFileComponents().stream()
                .filter(IndirectObject.class::isInstance)
                .toList();
        assertEquals(2, objects.size());
        assertInstanceOf(EndOfFile.class, pdf.getFileComponents().stream().toList().getLast());
    }

    @Test
    void crlfStreamExcludesTheEndOfLineBeforeEndstream() throws IOException {
        final PDFFile pdf = this.parse("%PDF-1.4\r\n1 0 obj\r\n<< /Length 3 >>\r\nstream\r\nabc\r\nendstream\r\nendobj\r\n%%EOF\r\n");
        assertContiguous(pdf);
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        pdf.generateTreeNode(root);
        assertTrue(Collections.list(root.depthFirstEnumeration()).stream()
                .map(n -> ((DefaultMutableTreeNode) n).getUserObject())
                .anyMatch(o -> o instanceof JTreeNodeFileComponent c && c.getText().equals("Stream Content: Length = 3")));
    }

    @Test
    void crossReferenceEntriesWithOneByteEndOfLine() throws IOException {
        final PDFFile pdf = this.parse("""
                %PDF-1.4
                xref
                0  2
                0000000000 65535 f
                0000000009 00000 n
                trailer
                << /Size 2 >>
                startxref
                9
                %%EOF
                """);
        assertContiguous(pdf);
        final CrossReferenceTable xref = pdf.getFileComponents().stream()
                .filter(CrossReferenceTable.class::isInstance)
                .map(CrossReferenceTable.class::cast)
                .findFirst()
                .orElseThrow();
        assertEquals(2, xref.Subsections.getFirst().Entries.size());
        assertEquals(9, xref.Subsections.getFirst().Entries.getLast().Offset);
    }

    @Test
    void consecutiveNewLinesAreSeparateLines() throws IOException {
        assertContiguous(this.parse("%PDF-1.4\n\n\n%%EOF"));
    }

    @Test
    void notPdf() {
        assertThrows(FileFormatException.class, () -> this.parse("Hello, this is not a PDF"));
    }
}
