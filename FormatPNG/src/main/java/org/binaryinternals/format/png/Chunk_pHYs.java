/*
 * Chunk_pHYs.java    Apr 30, 2011, 23:01
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `pHYs` chunk specifies the intended pixel size or aspect ratio for
/// display of the image.
///
/// @author Amos Shi
public final class Chunk_pHYs extends Chunk {

    public static final String CHUNK_TYPE_NAME = "pHYs";
    /// Pixels per unit, X axis.
    public final int AxisX;
    /// Pixels per unit, Y axis.
    public final int AxisY;
    /// Unit specifier.
    ///
    /// ```
    /// 0: unit is unknown
    /// 1: unit is the meter
    /// ```
    public final int Unit;

    Chunk_pHYs(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.AxisX = chunkDataStream.readInt();
        this.AxisY = chunkDataStream.readInt();
        this.Unit = chunkDataStream.read();
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                "Pixels per unit, X axis = %d".formatted(this.AxisX))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 4,
                4,
                "Pixels per unit, Y axis = %d".formatted(this.AxisY))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 8,
                1,
                "Unit specifier = %d".formatted(this.Unit))));
    }
}
