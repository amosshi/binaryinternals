/**
 * Chunk_pHYs.java    Apr 30, 2011, 23:01
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
 * The <span>pHYs</span> chunk specifies the intended pixel size or aspect ratio for display
 * of the image.
 *
 * @author Amos Shi
 */
public class Chunk_pHYs extends Chunk {

    public static final String CHUNK_TYPE_NAME = "pHYs";
    /** Pixels per unit, X axis.   */
    public final int AxisX;
    /** Pixels per unit, Y axis.   */
    public final int AxisY;
    /**
     * Unit specifier.
     * <pre>
     * 0: unit is unknown
     * 1: unit is the meter
     * </pre>
     */
    public final int Unit;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes(StandardCharsets.UTF_8);
    }

    public Chunk_pHYs(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.AxisX = chunkDataStream.readInt();
        this.AxisY = chunkDataStream.readInt();
        this.Unit = chunkDataStream.read();
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                String.format("Pixels per unit, X axis = %d", this.AxisX))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 4,
                4,
                String.format("Pixels per unit, Y axis = %d", this.AxisY))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 4,
                1,
                String.format("Unit specifier = %d", this.Unit))));
    }
}
