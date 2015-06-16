/**
 * Chunk_tEXt.java    Apr 30, 2011, 23:36
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
 * Textual information that the encoder wishes to record with the image can be 
 * stored in <span>tEXt</span> chunks.
 * <p>
 * Each <span class="cn">tEXt</span> chunk contains a keyword and a text string,
 * in the format:
 * </p>
 * <pre>
 * Keyword:        1-79 bytes (character string)
 * Null separator: 1 byte
 * Text:           n bytes (character string)
 * </pre>
 * <p>
 * The keyword and text string are separated by a zero byte (null character).  
 * Neither the keyword nor the text string can contain a null character.  
 * Note that the text string is <em>not</em> null-terminated (the length of the 
 * chunk is sufficient information to locate the ending).
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_tEXt extends Chunk {

    public static final String CHUNK_TYPE_NAME = "tEXt";
    /**
     * Each of the <span>text</span> chunks contains as its first field a
     * keyword that indicates the type of information represented by the text
     * string.
     * <p>
     * The following keywords are predefined and should be used where
     * appropriate:
     * </p>
     * <pre>
     *    Title            Short (one line) title or caption for image
     *    Author           Name of image's creator
     *    Description      Description of image (possibly long)
     *    Copyright        Copyright notice
     *    Creation Time    Time of original image creation
     *    Software         Software used to create the image
     *    Disclaimer       Legal disclaimer
     *    Warning          Warning of nature of content
     *    Source           Device used to create the image
     *    Comment          Miscellaneous comment; conversion from GIF comment
     * </pre>
     */
    public final String Keyword;
    /**
     * The text string can be of any length from zero bytes up to the maximum
     * permissible chunk size less the length of the keyword and separator.
     * <p>
     * The text is interpreted according to the ISO/IEC 8859-1 (Latin-1)
     * character set <span>ISO/IEC-8859-1</span>.
     * The text string can contain any Latin-1 character.
     * Newlines in the text string should be represented by a single linefeed
     * character (decimal 10); use of other control characters in the text is
     * discouraged.
     * </p>
     */
    public final String Text;
    // TODO - ISO/IEC 8859-1

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_tEXt(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();

        int rest = this.ChunkData.length - this.Keyword.length() - 1;
        if (rest > 0) {
            this.Text = chunkDataStream.readASCII(rest);
        } else {
            this.Text = "";
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
                start = start + this.Keyword.length(),
                1,
                "Null separator")));
        if (this.Text.length() > 0) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start = start + 1,
                    this.Text.length(),
                    String.format("Text = %s", this.Text))));
        }
    }
}
