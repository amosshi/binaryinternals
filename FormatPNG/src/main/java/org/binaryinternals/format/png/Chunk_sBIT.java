/*
 * Chunk_sBIT.java    May 01, 2011, 17:06
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `sBIT` chunk is provided in order to store the original number of
/// significant bits.
///
/// It holds one byte per channel of the image color type.
///
/// @author Amos Shi
public final class Chunk_sBIT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "sBIT";

    Chunk_sBIT(final PosDataInputStream stream) throws IOException {
        super(stream);
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        final List<String> channels = switch (this.ChunkData.length) {
            case 1 -> List.of("significant");
            case 2 -> List.of("significant source grayscale data", "significant source alpha data");
            case 3 -> List.of("significant red", "significant green", "significant blue");
            case 4 -> List.of("significant red", "significant green", "significant blue", "significant alpha");
            default -> List.of();
        };

        if (channels.isEmpty()) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.ChunkData.length,
                    "Un-recognized")));
            return;
        }

        for (int i = 0; i < channels.size(); i++) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + i,
                    1,
                    "%s = %d".formatted(channels.get(i), Byte.toUnsignedInt(this.ChunkData[i])))));
        }
    }
}
