/**
 * Chunk_cHRM.java    May 02, 2011, 09:31
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
 * Applications that need device-independent specification of colors in a PNG
 * file can use the <span>cHRM</span> chunk to specify the 1931 CIE
 * <code>x,y</code> chromaticities of the red, green, and blue primaries used
 * in the image, and the referenced white point.
 * <p>
 * The <span>cHRM</span> chunk contains:
 * </p>
 * <pre>
 * White Point x: 4 bytes
 * White Point y: 4 bytes
 * Red x:         4 bytes
 * Red y:         4 bytes
 * Green x:       4 bytes
 * Green y:       4 bytes
 * Blue x:        4 bytes
 * Blue y:        4 bytes
 * </pre>
 * <p>
 * Each value is encoded as a 4-byte unsigned integer, representing the
 * <code>x</code> or <code>y</code> value times 100000.
 * For example, a value of 0.3127 would be stored as the integer 31270.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_cHRM extends Chunk {

    public static final String CHUNK_TYPE_NAME = "cHRM";
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

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_cHRM(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.WhitePointX = chunkDataStream.readInt();
        this.WhitePointY = chunkDataStream.readInt();
        this.RedX = chunkDataStream.readInt();
        this.RedY = chunkDataStream.readInt();
        this.GreenX = chunkDataStream.readInt();
        this.GreenY = chunkDataStream.readInt();
        this.BlueX = chunkDataStream.readInt();
        this.BlueY = chunkDataStream.readInt();
        this.WhitePointX_value = ((double)this.WhitePointX) / 100000;
        this.WhitePointY_value = ((double)this.WhitePointY) / 100000;
        this.RedX_value = ((double)this.RedX) / 100000;
        this.RedY_value = ((double)this.RedY) / 100000;
        this.GreenX_value = ((double)this.GreenX) / 100000;
        this.GreenY_value = ((double)this.GreenY) / 100000;
        this.BlueX_value = ((double)this.BlueX) / 100000;
        this.BlueY_value = ((double)this.BlueY) / 100000;
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                String.format("While Point x = %05d or %f", this.WhitePointX, this.WhitePointX_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("While Point y = %05d or %f", this.WhitePointY, this.WhitePointY_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Red x = %05d or %f", this.RedX, this.RedX_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Red y = %05d or %f", this.RedY, this.RedY_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Green x = %05d or %f", this.GreenX, this.GreenX_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Green y = %05d or %f", this.GreenY, this.GreenY_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Blue x = %05d or %f", this.BlueX, this.BlueX_value))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 4,
                4,
                String.format("Blue y = %05d or %f", this.BlueY, this.BlueY_value))));
    }
}
