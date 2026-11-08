/*
 * Chunk_gAMA.java    May 01, 2011, 11:33
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `gAMA` chunk specifies the relationship between the image samples and
/// the desired display output intensity as a power function.
///
/// @author Amos Shi
public final class Chunk_gAMA extends Chunk {

    public static final String CHUNK_TYPE_NAME = "gAMA";

    /// The gamma times 100000.
    public final int Gamma;
    /// The display gamma, `100000 / Gamma`; for example `2.2` for `Gamma = 45455`.
    public final double Value;

    Chunk_gAMA(final PosDataInputStream stream) throws IOException {
        super(stream);

        this.Gamma = this.getChunkDataStream().readInt();
        this.Value = (1 / ((double) this.Gamma)) * 100000;
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getDataStartPos(),
                4,
                "Gamma = %d or 1/%f".formatted(this.Gamma, this.Value))));
    }
}
