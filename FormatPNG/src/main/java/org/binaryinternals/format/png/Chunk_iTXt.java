/*
 * Chunk_iTXt.java    May 04, 2011, 16:05
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// This chunk is semantically equivalent to the `tEXt` and `zTXt` chunks, but
/// the textual data is in the UTF-8 encoding of the Unicode character set
/// instead of Latin-1.
///
/// This chunk contains:
///
/// ```
/// Keyword:             1-79 bytes (character string)
/// Null separator:      1 byte
/// Compression flag:    1 byte
/// Compression method:  1 byte
/// Language tag:        0 or more bytes (character string)
/// Null separator:      1 byte
/// Translated keyword:  0 or more bytes
/// Null separator:      1 byte
/// Text:                0 or more bytes
/// ```
///
/// @author Amos Shi
/// @see Chunk_tEXt
/// @see Chunk_zTXt
public final class Chunk_iTXt extends Chunk {

    /// Chunk type of the international textual data chunk.
    public static final String CHUNK_TYPE_NAME = "iTXt";
    /// Value of [#CompressionFlag] for uncompressed text.
    private static final int UNCOMPRESSED = 0;

    /// Refer to [Chunk_tEXt#Keyword].
    public final String Keyword;
    /// The compression flag is 0 for uncompressed text, 1 for compressed text.
    /// Only the text field may be compressed.
    public final int CompressionFlag;
    /// The only value presently defined for the compression method byte is 0,
    /// meaning zlib data stream with deflate compression. For uncompressed text,
    /// encoders should set the compression method to 0 and decoders should
    /// ignore it.
    public final int CompressionMethod;
    /// The language tag \[RFC-1766\] indicates the human language used by the
    /// translated keyword and the text. Unlike the keyword, the language tag is
    /// case-insensitive. It is an ASCII \[ISO-646\] string consisting of
    /// hyphen-separated words of 1-8 letters each (for example: cn, en-uk,
    /// no-bok, x-klingon). If the first word is two letters long, it is an ISO
    /// language code \[ISO-639\]. If the language tag is empty, the language is
    /// unspecified.
    public final String LanguageTag;
    /// The translated keyword and text both use the UTF-8 encoding of the
    /// Unicode character set \[ISO/IEC-10646-1\], and neither may contain a zero
    /// byte (null character).
    public final byte[] TranslatedKeyword;
    /// [#TranslatedKeyword] decoded as UTF-8.
    public final String TranslatedKeywordString;
    /// The text, unlike the other strings, is not null-terminated; its length is
    /// implied by the chunk length. It is `null` when the text is empty.
    ///
    /// @see #TranslatedKeyword
    public final byte[] Text;
    /// [#Text] decoded as UTF-8, decompressed first when [#CompressionFlag] is
    /// 1, or `null` when the text is empty or cannot be decompressed.
    public final String TextString;

    Chunk_iTXt(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();
        this.CompressionFlag = chunkDataStream.read();
        this.CompressionMethod = chunkDataStream.read();
        this.LanguageTag = chunkDataStream.readASCII();
        this.TranslatedKeyword = chunkDataStream.readBinary();
        this.TranslatedKeywordString = new String(this.TranslatedKeyword, StandardCharsets.UTF_8);

        final int rest = this.Length
                - this.Keyword.length() - 1
                - 1 - 1
                - this.LanguageTag.length() - 1
                - this.TranslatedKeyword.length - 1;
        this.Text = rest > 0 ? chunkDataStream.readNBytes(rest) : null;
        if (this.Text == null) {
            this.TextString = null;
        } else if (this.CompressionFlag == UNCOMPRESSED) {
            this.TextString = new String(this.Text, StandardCharsets.UTF_8);
        } else {
            this.TextString = this.CompressionMethod == 0 ? inflateText(this.Text, StandardCharsets.UTF_8) : null;
        }
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(this.Keyword, this.TextString);
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.Keyword.length(),
                "Keyword = %s".formatted(this.Keyword))));
        start += this.Keyword.length();
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));
        start += 1;
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Compression flag = %d".formatted(this.CompressionFlag))));
        start += 1;
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Compression method = %d".formatted(this.CompressionMethod))));
        start += 1;

        if (!this.LanguageTag.isEmpty()) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.LanguageTag.length(),
                    "Language tag = %s".formatted(this.LanguageTag))));
            start += this.LanguageTag.length();
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));
        start += 1;

        if (this.TranslatedKeyword.length > 0) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.TranslatedKeyword.length,
                    "Translated keyword = %s".formatted(this.TranslatedKeywordString))));
            start += this.TranslatedKeyword.length;
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));
        start += 1;

        if (this.Text != null) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.Text.length,
                    this.TextString == null ? "Text (compressed)"
                            : this.CompressionFlag == UNCOMPRESSED ? "Text = %s".formatted(this.TextString)
                            : "Text (compressed) = %s".formatted(this.TextString))));
        }
    }
}
