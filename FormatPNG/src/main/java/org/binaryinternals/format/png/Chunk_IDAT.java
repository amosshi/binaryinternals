/*
 * Chunk_IDAT.java    May 02, 2011, 11:23
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `IDAT` chunk contains the actual image data.
///
/// The `IDAT` chunk contains the output data stream of the compression algorithm.
///
/// @author Amos Shi
public final class Chunk_IDAT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "IDAT";

    Chunk_IDAT(final PosDataInputStream stream) throws IOException {
        super(stream);
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getDataStartPos(),
                this.Length,
                "Image Data")));
    }
}
