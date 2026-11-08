package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Stream, see `PDF 32000-1:2008` `7.3.8`: Stream Objects.
///
/// ```
/// stream EOL
/// ... data ...
/// EOL endstream
/// ```
///
/// @author Amos Shi
public final class Stream extends FileComponent implements BasicObject {

    public static final String SIGNATURE_START = "stream";
    public static final String SIGNATURE_END = "endstream";
    private static final byte[] SIGNATURE_END_BYTES = SIGNATURE_END.getBytes(StandardCharsets.ISO_8859_1);
    /// The signature length, `stream` and the end-of-line marker after it.
    public final int signatureLen;
    /// Length of the stream data.
    private final int dataLength;
    /// Length of the end-of-line marker before `endstream`.
    private final int eolBeforeEnd;
    /// The `endstream` keyword, with the bytes after it as the new line.
    private final ASCIILine signatureEnd;

    /// Parse the stream, which lasts to the end of `stream`.
    ///
    /// @param stream The indirect object content, positioned after the line
    /// ending with `stream`
    /// @param line The line ending with `stream`
    /// @throws IOException Read failed, or `endstream` not found
    public Stream(final PosDataInputStream stream, final ASCIILine line) throws IOException {
        final byte[] buf = stream.getBuf();
        final int offset = stream.getOffset();
        final int keywordEnd = stream.getPos() - offset - line.newLineLength();
        final int eol = eolLength(buf, keywordEnd);
        this.signatureLen = SIGNATURE_START.length() + eol;
        super.startPos = offset + keywordEnd - SIGNATURE_START.length();

        // The last 'endstream', since the data may contain anything
        final int dataStart = keywordEnd + eol;
        final int endKeyword = lastIndexOf(buf, SIGNATURE_END_BYTES, dataStart);
        if (endKeyword < 0) {
            throw new FileFormatException(
                    "The 'endstream' tag is not found for current object. Object Start Offset = %d, Current Offset = %d".formatted(
                            super.startPos,
                            stream.getPos()));
        }

        // The end-of-line marker before 'endstream' is not part of the data
        int dataEnd = endKeyword;
        if (dataEnd > dataStart && buf[dataEnd - 1] == PDFStatics.WhiteSpace.LF) {
            dataEnd--;
        }
        if (dataEnd > dataStart && buf[dataEnd - 1] == PDFStatics.WhiteSpace.CR) {
            dataEnd--;
        }
        this.dataLength = dataEnd - dataStart;
        this.eolBeforeEnd = endKeyword - dataEnd;
        this.signatureEnd = new ASCIILine(SIGNATURE_END, buf.length - endKeyword - SIGNATURE_END.length());

        stream.skipNBytes(buf.length - (stream.getPos() - offset));
        this.length = stream.getPos() - super.startPos;
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

    private static int lastIndexOf(final byte[] buf, final byte[] target, final int from) {
        for (int i = buf.length - target.length; i >= from; i--) {
            if (Arrays.equals(buf, i, i + target.length, target, 0, target.length)) {
                return i;
            }
        }
        return -1;
    }

    /// Get the start position of the Encoded Stream Content.
    ///
    /// @return The start position
    public int getStreamStartPos() {
        return super.startPos + this.signatureLen;
    }

    /// Get the Encoded Stream Content Length of current [Stream] object.
    ///
    /// The content length does not include the signature `stream` or
    /// `endstream`, nor the end-of-line markers around the content.
    ///
    /// @return The length
    public int getStreamLength() {
        return this.dataLength;
    }

    public ASCIILine getSignatureEnd() {
        return this.signatureEnd;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeStream = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Stream"));

        int pos = super.startPos;
        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_START.length(),
                Texts.Signature + SIGNATURE_START)));
        pos += SIGNATURE_START.length();

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.signatureLen - SIGNATURE_START.length(),
                Texts.NewLine)));
        pos += this.signatureLen - SIGNATURE_START.length();

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.dataLength,
                "Stream Content: Length = " + this.dataLength)));
        pos += this.dataLength;

        if (this.eolBeforeEnd > 0) {
            nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.eolBeforeEnd,
                    Texts.NewLine)));
            pos += this.eolBeforeEnd;
        }

        nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_END.length(),
                Texts.Signature + SIGNATURE_END)));
        pos += SIGNATURE_END.length();

        if (this.signatureEnd.newLineLength() > 0) {
            nodeStream.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.signatureEnd.newLineLength(),
                    Texts.NewLine)));
        }

        parentNode.add(nodeStream);
    }
}
