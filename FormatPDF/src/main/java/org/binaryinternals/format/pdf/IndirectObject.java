package org.binaryinternals.format.pdf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;
import org.binaryinternals.format.pdf.basicobj.Analysis;
import org.binaryinternals.format.pdf.basicobj.Stream;

/// PDF Indirect Object, see `PDF 32000-1:2008` `7.3.10`: Indirect Object.
///
/// @author Amos Shi
public final class IndirectObject extends FileComponent implements GenerateTreeNode {

    private static final Logger LOGGER = Logger.getLogger(IndirectObject.class.getName());

    /// Start definition regular expression, matched at the start of a line.
    ///
    /// Example:
    ///
    /// ```
    /// 5534 0 obj
    /// 5491 0 obj SOME TEXT OF OBJECT
    /// 5491 0 obj<< SOME TEXT OF OBJECT
    /// ```
    public static final String SIGNATURE_START_REGEXP = "(\\d+)[ \\t\\f\\x00]+(\\d+)[ \\t\\f\\x00]+obj(?=[\\s\\x00()<>\\[\\]{}/%]|$)";
    static final Pattern SIGNATURE_START_PATTERN = Pattern.compile(SIGNATURE_START_REGEXP);
    /// Key word in the first line of current object.
    public static final String SIGNATURE_START = "obj";
    /// End line of current object.
    public static final String SIGNATURE_END = "endobj";

    /// A positive integer object number.
    ///
    /// Indirect objects may be numbered sequentially within a PDF file, but
    /// this is not required; object numbers may be assigned in any arbitrary
    /// order.
    private final int objectNumber;
    /// A non-negative integer generation number.
    ///
    /// In a newly created file, all indirect objects shall have generation
    /// numbers of 0. Nonzero generation numbers may be introduced when the file
    /// is later updated.
    private final int generationNumber;
    /// Length of [#objectNumber] and [#generationNumber].
    private final int numberLen;
    /// The `obj` keyword, with the new line after it.
    private final ASCIILine signatureStart;
    /// The `endobj` keyword, with the new line after it.
    private final ASCIILine signatureEnd;
    /// Length of the content between `obj` and `endobj`.
    private final int contentLength;
    /// Components of current object.
    private final List<FileComponent> components = new ArrayList<>();

    /// Parse the indirect object starting with `line`.
    ///
    /// @param stream The file stream, positioned after `line`
    /// @param line The line starting with [#SIGNATURE_START_REGEXP]
    /// @throws IOException Read failed, or `endobj` not found
    IndirectObject(final PosDataInputStream stream, final ASCIILine line) throws IOException {
        final Matcher m = SIGNATURE_START_PATTERN.matcher(line.line());
        if (!m.lookingAt()) {
            throw new IllegalArgumentException("Not an indirect object: " + line);
        }
        super.startPos = stream.getPos() - line.length();
        this.objectNumber = Integer.parseInt(m.group(1));
        this.generationNumber = Integer.parseInt(m.group(2));
        this.numberLen = m.end() - SIGNATURE_START.length();
        this.signatureStart = new ASCIILine(SIGNATURE_START, (m.end() == line.line().length()) ? line.newLineLength() : 0);

        final byte[] buf = stream.getBuf();
        final int offset = stream.getOffset();
        final int contentStart = super.startPos + this.numberLen + this.signatureStart.length();
        final int endKeyword = this.findEnd(buf, contentStart - offset) + offset;
        this.contentLength = endKeyword - contentStart;

        final int eol = eolLength(buf, endKeyword - offset + SIGNATURE_END.length());
        this.signatureEnd = new ASCIILine(SIGNATURE_END, eol);
        final int end = endKeyword + this.signatureEnd.length();
        stream.flyTo(end - offset);
        super.length = end - super.startPos;

        this.parseObject(stream, contentStart - offset);
    }

    /// Find the `endobj` keyword, skipping the stream data if any.
    private int findEnd(final byte[] buf, final int from) throws FileFormatException {
        int endobj = indexOfKeyword(buf, SIGNATURE_END, from);
        final int stream = indexOfKeyword(buf, Stream.SIGNATURE_START, from);
        if (stream >= 0 && (endobj < 0 || stream < endobj)) {
            final int endstream = indexOfKeyword(buf, Stream.SIGNATURE_END, stream);
            if (endstream >= 0) {
                endobj = indexOfKeyword(buf, SIGNATURE_END, endstream + Stream.SIGNATURE_END.length());
            }
        }
        if (endobj < 0) {
            throw new FileFormatException("The '%s' is not found for object %d %d at %d.".formatted(
                    SIGNATURE_END, this.objectNumber, this.generationNumber, super.startPos));
        }
        return endobj;
    }

    /// Index of `keyword` delimited by white-space, delimiters or the buffer
    /// bounds.
    static int indexOfKeyword(final byte[] buf, final String keyword, final int from) {
        final byte[] target = keyword.getBytes(StandardCharsets.ISO_8859_1);
        for (int i = Math.max(from, 0); i <= buf.length - target.length; i++) {
            if (Arrays.equals(buf, i, i + target.length, target, 0, target.length)
                    && (i == 0 || isBoundary(buf[i - 1]))
                    && (i + target.length == buf.length || isBoundary(buf[i + target.length]))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isBoundary(final byte b) {
        return PDFStatics.isWhiteSpace(b) || PDFStatics.isDelimiter(b);
    }

    /// Length of the end-of-line marker at `pos`: `CR LF`, `LF`, or `CR`.
    private static int eolLength(final byte[] buf, final int pos) {
        if (pos >= buf.length) {
            return 0;
        }
        if (buf[pos] == PDFStatics.WhiteSpace.CR) {
            return (pos + 1 < buf.length && buf[pos + 1] == PDFStatics.WhiteSpace.LF) ? 2 : 1;
        }
        return (buf[pos] == PDFStatics.WhiteSpace.LF) ? 1 : 0;
    }

    /// Parse the content between `obj` and `endobj`.
    ///
    /// @param root The file stream
    /// @param contentStart Start of the content in `root`
    private void parseObject(final PosDataInputStream root, final int contentStart) throws IOException {
        if (this.contentLength == 0) {
            return;
        }

        // Find the stream object first, it lasts to the end of the content
        final PosDataInputStream content = root.getPartialStream(contentStart, this.contentLength);
        Stream stream = null;
        while (content.hasNext()) {
            final ASCIILine line = content.readASCIILine();
            if (endsWithKeyword(line.line(), Stream.SIGNATURE_START)) {
                stream = new Stream(content, line);
                break;
            }
        }

        // Objects before the stream
        final int objectsLength = (stream == null) ? this.contentLength : stream.getStartPos() - content.getOffset();
        if (objectsLength > 0) {
            this.parseObjects(root.getPartialStream(contentStart, objectsLength));
        }

        if (stream != null) {
            this.components.add(stream);
        }
    }

    private void parseObjects(final PosDataInputStream objects) throws IOException {
        while (objects.hasNext()) {
            if (Analysis.parseNextObject(objects, this.components) == null) {
                final byte next = objects.readByte();
                LOGGER.log(Level.WARNING, "Unexpected byte in object {0} {1} at {2}: ''{3}''", new Object[]{
                    this.objectNumber, this.generationNumber, objects.getPos() - 1, (char) (next & 0xFF)});
            }
        }
    }

    private static boolean endsWithKeyword(final String line, final String keyword) {
        if (!line.endsWith(keyword)) {
            return false;
        }
        final int before = line.length() - keyword.length() - 1;
        return before < 0 || isBoundary((byte) line.charAt(before));
    }

    /// The object number.
    ///
    /// @return The object number
    public int getObjectNumber() {
        return this.objectNumber;
    }

    /// The generation number.
    ///
    /// @return The generation number
    public int getGenerationNumber() {
        return this.generationNumber;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Indirect Object: %d %d".formatted(this.objectNumber, this.generationNumber));
        nodeComp.setDescription(Texts.getString(Texts.PDF_INDIRECT_OBJECT));
        final DefaultMutableTreeNode nodeIO = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.numberLen,
                "Object Number = %d, Generation Number = %d".formatted(this.objectNumber, this.generationNumber))));
        pos += this.numberLen;

        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.signatureStart.line().length(),
                Texts.Signature + this.signatureStart.line())));
        pos += this.signatureStart.line().length();

        if (this.signatureStart.newLineLength() > 0) {
            nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.signatureStart.newLineLength(),
                    Texts.NewLine)));
            pos += this.signatureStart.newLineLength();
        }

        final DefaultMutableTreeNode nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.contentLength,
                "Indirect Object Content"));
        nodeIO.add(nodeContent);
        for (final FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(nodeContent);
            }
        }
        pos += this.contentLength;

        nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_END.length(),
                Texts.Signature + SIGNATURE_END)));
        pos += SIGNATURE_END.length();

        if (this.signatureEnd.newLineLength() > 0) {
            nodeIO.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.signatureEnd.newLineLength(),
                    Texts.NewLine)));
        }

        parentNode.add(nodeIO);
    }
}
