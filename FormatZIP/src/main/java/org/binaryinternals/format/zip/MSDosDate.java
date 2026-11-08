/*
 * MSDosDate.java    May 14, 2011, 12:35
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// A date in the format used by MS-DOS.
///
/// The date is a packed 16-bit (2 bytes) value in which bits in the value
/// represent the day, month, and year.
///
/// ```
/// bit     00 - 04        05 - 08           09 - 15
/// value   day (1 - 31)   month (1 - 12)    years from 1980
/// max     31             15                127 (Year 2107)
/// ```
///
/// @author Amos Shi
/// @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-dosdatetimetofiletime">DosDateTimeToFileTime</a>
public final class MSDosDate extends FileComponent implements GenerateTreeNode {

    public final int Day;
    public final int Month;
    public final int Year;

    MSDosDate(final int value, final int start, final int len) {
        super(start, len);
        this.Day = value & 0x001F;
        this.Month = (value & 0x01E0) >> 5;
        this.Year = ((value & 0xFE00) >> 9) + 1980;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Year = %04d".formatted(this.Year))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Month = %02d".formatted(this.Month))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Day = %02d".formatted(this.Day))));
    }

    @Override
    public String toString() {
        return "%04d-%02d-%02d".formatted(this.Year, this.Month, this.Day);
    }
}
