/**
 * Chunk_IDAT.java    May 02, 2011, 11:23
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
 * The IDAT chunk contains the actual image data.
 * <p>
 * The IDAT chunk contains the output data stream of the compression algorithm.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_IDAT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IDAT";

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType(){
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_IDAT(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.Length,
                "Image Data")));
    }
}
