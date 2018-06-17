/**
 * Chunk_sBIT.java    May 01, 2011, 17:06
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
 * The <span>sBIT</span> chunk is provided in order to store the original number
 * of significant bits.
 *
 * @author Amos Shi
 */
public class Chunk_sBIT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "sBIT";

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_sBIT(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        switch (this.ChunkData.length) {
            case 1:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("significant = %d", this.ChunkData[0]))));
                break;

            case 2:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("significant source grayscale data = %d", this.ChunkData[0]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 1,
                        1,
                        String.format("significant source alpha data = %d", this.ChunkData[1]))));
                break;

            case 3:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("significant red = %d", this.ChunkData[0]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 1,
                        1,
                        String.format("significant green = %d", this.ChunkData[1]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 2,
                        1,
                        String.format("significant blue = %d", this.ChunkData[2]))));
                break;

            case 4:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        1,
                        String.format("significant red = %d", this.ChunkData[0]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 1,
                        1,
                        String.format("significant green = %d", this.ChunkData[1]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 2,
                        1,
                        String.format("significant blue = %d", this.ChunkData[2]))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 3,
                        1,
                        String.format("significant alpha = %d", this.ChunkData[3]))));
                break;

            default:
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        this.ChunkData.length,
                        "Un-recognized")));
        }
    }
}
