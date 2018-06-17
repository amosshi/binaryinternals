/**
 * Chunk_iEXt.java    May 04, 2011, 16:05
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * This chunk is semantically equivalent to the <span>tEXt</span> and
 * <span>zTXt</span> chunks, but the textual data is in the UTF-8 encoding of
 * the Unicode character set instead of Latin-1.
 * <p>
 * This chunk contains:
 * </p>
 * <pre>
 * Keyword:             1-79 bytes (character string)
 * Null separator:      1 byte
 * Compression flag:    1 byte
 * Compression method:  1 byte
 * Language tag:        0 or more bytes (character string)
 * Null separator:      1 byte
 * Translated keyword:  0 or more bytes
 * Null separator:      1 byte
 * Text:                0 or more bytes
 * </pre>
 *
 * @author Amos Shi
 * @see Chunk_tEXt
 * @see Chunk_zTXt
 */
public class Chunk_iTXt extends Chunk {

    public static final String CHUNK_TYPE_NAME = "iTXt";
    /**
     * Refer to {@link Chunk_tEXt#Keyword}.
     * 
     * @see Chunk_tEXt#Keyword
     */
    public final String Keyword;
    /**
     * The compression flag is 0 for uncompressed text, 1 for compressed text.
     * Only the text field may be compressed.
     */
    public final int CompressionFlag;
    /**
     * The only value presently defined for the compression method byte is 0,
     * meaning zlib data stream with deflate compression.
     * For uncompressed text, encoders should set the compression method to 0
     * and decoders should ignore it.
     */
    public final int CompressionMethod;
    /**
     * The language tag [RFC-1766] indicates the human language used by the
     * translated keyword and the text.
     * Unlike the keyword, the language tag is case-insensitive.
     * It is an ASCII [ISO-646] string consisting of hyphen-separated words of
     * 1-8 letters each (for example: cn, en-uk, no-bok, x-klingon).
     * If the first word is two letters long, it is an ISO language code
     * [ISO-639]. If the language tag is empty, the language is unspecified.
     */
    // TODO - language tag [RFC-1766]
    // TODO - ASCII [ISO-646] string
    public final String LanguageTag;
    /**
     * The translated keyword and text both use the UTF-8 encoding of the
     * Unicode character set [ISO/IEC-10646-1], and neither may contain a zero
     * byte (null character).
     */
    // TODO - UTF-8 encoding, [ISO/IEC-10646-1]
    public final byte[] TranslatedKeyword;
    public final String TranslatedKeywordString = null;  // TODO - Parse the text
    /**
     * The text, unlike the other strings, is not null-terminated; its length is
     * implied by the chunk length.
     *
     * @see #TranslatedKeyword
     */
    public final byte[] Text;
    public final String TextString = null;  // TODO - Parse the text

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_iTXt(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();
        this.CompressionFlag = chunkDataStream.read();
        this.CompressionMethod = chunkDataStream.read();
        this.LanguageTag = chunkDataStream.readASCII();
        this.TranslatedKeyword = chunkDataStream.readBinary();

        int rest = this.Length
                - this.Keyword.length() - 1
                - 1 - 1
                - this.LanguageTag.length() - 1;
        if (this.TranslatedKeyword != null) {
            rest -= this.TranslatedKeyword.length;
        }
        rest -= 1;
        if (rest > 0) {
            this.Text = new byte[rest];
            chunkDataStream.read(this.Text);
        } else {
            this.Text = null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.Keyword, this.Text);
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.Keyword.length(),
                String.format("Keyword = %s", this.Keyword))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += this.Keyword.length(),
                1,
                "Null separator")));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 1,
                1,
                String.format("Compression flag = %d", this.CompressionFlag))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 1,
                1,
                String.format("Compression method = %d", this.CompressionMethod))));
        if (this.LanguageTag.length() > 0) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += 1,
                    this.LanguageTag.length(),
                    String.format("Language tag = %s", this.LanguageTag))));
            start += this.LanguageTag.length();
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));
        if (this.TranslatedKeyword != null) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += 1,
                    this.TranslatedKeyword.length,
                    "Translated keyword")));
            start += this.TranslatedKeyword.length;
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null separator")));

        if (this.Text != null) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += 1,
                    this.Text.length,
                    "Text")));
        }
    }
}
