/**
 * Chunk_tIME.java    Apr 30, 2011, 23:01
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
 * The <span>tIME</span> chunk gives the time of the last image
 * modification (<em>not</em> the time of initial image creation).
 * <p>
 * Universal Time (UTC, also called GMT) should be specified rather than local
 * time.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_tIME extends Chunk {

    public static final String CHUNK_TYPE_NAME = "tIME";
    public final int Year;
    public final int Month;
    public final int Day;
    public final int Hour;
    public final int Minute;
    public final int Second;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_tIME(PosDataInputStream stream, PNGFile png) throws IOException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.Year = chunkDataStream.readShort();
        this.Month = chunkDataStream.read();
        this.Day = chunkDataStream.read();
        this.Hour = chunkDataStream.read();
        this.Minute = chunkDataStream.read();
        this.Second = chunkDataStream.read();
    }

    @Override
    public String toString(){
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                this.Year, this.Month, this.Day,
                this.Hour, this.Minute, this.Second);
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                2,
                String.format("Year = %04d", this.Year))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 2,
                1,
                String.format("Month = %02d", this.Month))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Day = %02d", this.Day))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Hour = %02d", this.Hour))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Minute = %02d", this.Minute))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 1,
                1,
                String.format("Second = %02d", this.Second))));
    }
}
