/*
 * MSDosTime.java    May 14, 2011, 12:35
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.zip;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// A time in the format used by MS-DOS.
///
/// The time is a packed 16-bit (2 bytes) value in which bits in the value
/// represent the hour, minute, and second.
///
/// ```
/// bit     00 - 04       05 - 10   11 - 15
/// value   second / 2    minute    hour
/// max     31            63        31
/// ```
///
/// @author Amos Shi
/// @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-dosdatetimetofiletime">DosDateTimeToFileTime</a>
public final class MSDosTime extends FileComponent implements GenerateTreeNode {

    public final int Second;
    public final int Minute;
    public final int Hour;

    MSDosTime(final int value, final int start, final int len) {
        super(start, len);
        this.Second = (value & 0x001F) * 2;  // Stored in 2-second units, odd seconds are lost
        this.Minute = (value & 0x07E0) >> 5;
        this.Hour = (value & 0xF800) >> 11;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Hour = %02d".formatted(this.Hour))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Minute = %02d".formatted(this.Minute))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Second = %02d (maybe inaccurate for one second)".formatted(this.Second))));
    }

    @Override
    public String toString() {
        return "%02d:%02d:%02d".formatted(this.Hour, this.Minute, this.Second);
    }
}
