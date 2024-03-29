/**
 * Chunk_gAMA.java    May 01, 2011, 11:33
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * The <span>gAMA</span> chunk specifies the relationship between the image 
 * samples and the desired display output intensity as a power function.
 *
 * 
 * @author Amos Shi
 */
public class Chunk_gAMA extends Chunk {

    public static final String CHUNK_TYPE_NAME = "gAMA";

    public final int Gamma;
    public final double Value;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes(StandardCharsets.UTF_8);
    }

    public Chunk_gAMA(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Gamma = chunkDataStream.readInt();
        this.Value = (1/((double)this.Gamma))*100000;
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                String.format("Gama = %d or 1/%f", this.Gamma, this.Value))));
    }
}
