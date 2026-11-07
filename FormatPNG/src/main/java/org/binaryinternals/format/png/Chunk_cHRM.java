/*
 * Chunk_cHRM.java    May 02, 2011, 09:31
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Applications that need device-independent specification of colors in a PNG
/// file can use the `cHRM` chunk to specify the 1931 CIE `x,y` chromaticities
/// of the red, green, and blue primaries used in the image, and the referenced
/// white point.
///
/// The `cHRM` chunk contains:
///
/// ```
/// White Point x: 4 bytes
/// White Point y: 4 bytes
/// Red x:         4 bytes
/// Red y:         4 bytes
/// Green x:       4 bytes
/// Green y:       4 bytes
/// Blue x:        4 bytes
/// Blue y:        4 bytes
/// ```
///
/// Each value is encoded as a 4-byte unsigned integer, representing the `x` or
/// `y` value times 100000. For example, a value of 0.3127 would be stored as
/// the integer 31270.
///
/// @author Amos Shi
public final class Chunk_cHRM extends Chunk {

    public static final String CHUNK_TYPE_NAME = "cHRM";
    /// The stored values are the chromaticities times this scale.
    private static final double SCALE = 100000;

    public final int WhitePointX;
    public final int WhitePointY;
    public final int RedX;
    public final int RedY;
    public final int GreenX;
    public final int GreenY;
    public final int BlueX;
    public final int BlueY;
    public final double WhitePointX_value;
    public final double WhitePointY_value;
    public final double RedX_value;
    public final double RedY_value;
    public final double GreenX_value;
    public final double GreenY_value;
    public final double BlueX_value;
    public final double BlueY_value;

    Chunk_cHRM(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.WhitePointX = chunkDataStream.readInt();
        this.WhitePointY = chunkDataStream.readInt();
        this.RedX = chunkDataStream.readInt();
        this.RedY = chunkDataStream.readInt();
        this.GreenX = chunkDataStream.readInt();
        this.GreenY = chunkDataStream.readInt();
        this.BlueX = chunkDataStream.readInt();
        this.BlueY = chunkDataStream.readInt();
        this.WhitePointX_value = this.WhitePointX / SCALE;
        this.WhitePointY_value = this.WhitePointY / SCALE;
        this.RedX_value = this.RedX / SCALE;
        this.RedY_value = this.RedY / SCALE;
        this.GreenX_value = this.GreenX / SCALE;
        this.GreenY_value = this.GreenY / SCALE;
        this.BlueX_value = this.BlueX / SCALE;
        this.BlueY_value = this.BlueY / SCALE;
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        addValue(parent, start, "White Point x", this.WhitePointX, this.WhitePointX_value);
        addValue(parent, start + 4, "White Point y", this.WhitePointY, this.WhitePointY_value);
        addValue(parent, start + 8, "Red x", this.RedX, this.RedX_value);
        addValue(parent, start + 12, "Red y", this.RedY, this.RedY_value);
        addValue(parent, start + 16, "Green x", this.GreenX, this.GreenX_value);
        addValue(parent, start + 20, "Green y", this.GreenY, this.GreenY_value);
        addValue(parent, start + 24, "Blue x", this.BlueX, this.BlueX_value);
        addValue(parent, start + 28, "Blue y", this.BlueY, this.BlueY_value);
    }

    private static void addValue(final DefaultMutableTreeNode parent, final int pos, final String name,
            final int stored, final double value) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                4,
                "%s = %05d or %f".formatted(name, stored, value))));
    }
}
