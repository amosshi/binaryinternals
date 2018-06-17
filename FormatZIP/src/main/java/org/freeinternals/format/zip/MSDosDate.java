/**
 * MSDosDate.java    May 14, 2011, 12:35
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

/**
 * A date in the format used by MS-DOS.
 * <p>
 * The date is a packed 16-bit (2 bytes) value in which bits in the value
 * represent the day, month, and year.
 * </p>
 * <pre>
 *   bit     00 - 04        05 - 08           09 - 15
 *   value   day (1 - 31)   month (1 - 12)    years from 1980
 *   max     31             15                127 (Year 2107)
 * </pre>
 *
 * @author Amos Shi
 * @see <a href="http://www.vsft.com/hal/dostime.htm">MS DOS Date Time Format</a>
 */
public class MSDosDate {
    public final int Day;
    public final int Month;
    public final int Year;

    MSDosDate(int value) {
        this.Day   =   value & 0x0000001F;
        this.Month =  (value & 0x000001E0) >> 5;
        this.Year  = ((value & 0x0000FE00) >> 9) + 1980;
    }

    @Override
    public String toString(){
        return String.format("%04d-%02d-%02d", this.Year, this.Month, this.Day);
    }
}
