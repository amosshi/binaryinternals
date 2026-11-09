/*
 * Chunk_tEXt.java    Apr 30, 2011, 23:36
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Textual information that the encoder wishes to record with the image can be
/// stored in `tEXt` chunks.
///
/// Each `tEXt` chunk contains a keyword and a text string, in the format:
///
/// ```
/// Keyword:        1-79 bytes (character string)
/// Null separator: 1 byte
/// Text:           n bytes (character string)
/// ```
///
/// The keyword and text string are separated by a zero byte (null character).
/// Neither the keyword nor the text string can contain a null character. Note
/// that the text string is _not_ null-terminated (the length of the chunk is
/// sufficient information to locate the ending).
///
/// @author Amos Shi
public final class Chunk_tEXt extends Chunk {

    /// Chunk type of the textual data chunk.
    public static final String CHUNK_TYPE_NAME = "tEXt";
    /// Each of the text chunks contains as its first field a keyword that
    /// indicates the type of information represented by the text string.
    ///
    /// The following keywords are predefined and should be used where
    /// appropriate:
    ///
    /// ```
    ///    Title            Short (one line) title or caption for image
    ///    Author           Name of image's creator
    ///    Description      Description of image (possibly long)
    ///    Copyright        Copyright notice
    ///    Creation Time    Time of original image creation
    ///    Software         Software used to create the image
    ///    Disclaimer       Legal disclaimer
    ///    Warning          Warning of nature of content
    ///    Source           Device used to create the image
    ///    Comment          Miscellaneous comment; conversion from GIF comment
    /// ```
    public final String Keyword;
    /// The text string can be of any length from zero bytes up to the maximum
    /// permissible chunk size less the length of the keyword and separator.
    ///
    /// The text is interpreted according to the ISO/IEC 8859-1 (Latin-1)
    /// character set. The text string can contain any Latin-1 character.
    /// Newlines in the text string should be represented by a single linefeed
    /// character (decimal 10); use of other control characters in the text is
    /// discouraged.
    public final String Text;

    Chunk_tEXt(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();

        // The text is Latin-1, which readASCII decodes
        final int rest = this.Length - this.Keyword.length() - 1;
        this.Text = rest > 0 ? chunkDataStream.readASCII(rest) : "";
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(this.Keyword, this.Text);
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.Keyword.length(),
                "Keyword = %s".formatted(this.Keyword))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + this.Keyword.length(),
                1,
                "Null separator")));
        if (!this.Text.isEmpty()) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + this.Keyword.length() + 1,
                    this.Text.length(),
                    "Text = %s".formatted(this.Text))));
        }
    }
}
