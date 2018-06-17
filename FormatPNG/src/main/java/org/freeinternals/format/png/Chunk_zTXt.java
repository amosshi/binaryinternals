/**
 * Chunk_zEXt.java    May 04, 2011, 16:06
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
 *
 * @author Amos Shi
 * @see Chunk_tEXt
 */
public class Chunk_zTXt extends Chunk {

    public static final String CHUNK_TYPE_NAME = "zTXt";
    /**
     * Refer to {@link Chunk_tEXt#Keyword}.
     * 
     * @see Chunk_tEXt#Keyword
     */
    public final String Keyword;
    public final int CompressionMethod;
    public final byte[] CompressedText;
    public final String CompressedTextString = null;      // TODO - Un-compress the text

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_zTXt(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Keyword = chunkDataStream.readASCII();
        this.CompressionMethod = chunkDataStream.read();

        int rest = this.ChunkData.length - this.Keyword.length() - 1 - 1;
        if (rest > 0) {
            this.CompressedText = new byte[rest];
            for (int i = 0; i < this.CompressedText.length; i++) {
                this.CompressedText[i] = chunkDataStream.readByte();
            }
        } else {
            this.CompressedText = null;
        }
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
                String.format("Compression method = %d", this.CompressionMethod))));

        if (this.CompressedText != null) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start = start + 1,
                    this.CompressedText.length,
                    "Compressed text")));
        }
    }
}
