/*
 * Chunk_tIME.java    Apr 30, 2011, 23:01
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `tIME` chunk gives the time of the last image modification (_not_ the
/// time of initial image creation).
///
/// Universal Time (UTC, also called GMT) should be specified rather than local
/// time.
///
/// @author Amos Shi
public final class Chunk_tIME extends Chunk {

    public static final String CHUNK_TYPE_NAME = "tIME";
    /// Complete year, for example 1995; not 95. A 2-byte unsigned integer.
    public final int Year;
    /// 1-12.
    public final int Month;
    /// 1-31.
    public final int Day;
    /// 0-23.
    public final int Hour;
    /// 0-59.
    public final int Minute;
    /// 0-60, for leap seconds.
    public final int Second;

    Chunk_tIME(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.Year = chunkDataStream.readUnsignedShort();
        this.Month = chunkDataStream.read();
        this.Day = chunkDataStream.read();
        this.Hour = chunkDataStream.read();
        this.Minute = chunkDataStream.read();
        this.Second = chunkDataStream.read();
    }

    @Override
    public String toString() {
        return "%04d-%02d-%02d %02d:%02d:%02d".formatted(
                this.Year, this.Month, this.Day,
                this.Hour, this.Minute, this.Second);
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                2,
                "Year = %04d".formatted(this.Year))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 2,
                1,
                "Month = %02d".formatted(this.Month))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 3,
                1,
                "Day = %02d".formatted(this.Day))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 4,
                1,
                "Hour = %02d".formatted(this.Hour))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 5,
                1,
                "Minute = %02d".formatted(this.Minute))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + 6,
                1,
                "Second = %02d".formatted(this.Second))));
    }
}
