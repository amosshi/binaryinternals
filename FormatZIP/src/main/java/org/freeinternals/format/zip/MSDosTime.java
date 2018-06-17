/**
 * MSDosTime.java    May 14, 2011, 12:35
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

/**
 * A time in the format used by MS-DOS.
 * <p>
 * The time is a packed 16-bit (2 bytes) value in which bits in the value
 * represent the hour, minute, and second.
 * </p>
 * <pre>
 *   bit     00 - 04   05 - 10   11 - 15
 *   value   second    minute    hour
 *   max     31        63        31
 * </pre>
 *
 * @author Amos Shi
 * @see <a href="http://www.vsft.com/hal/dostime.htm">MS DOS Date Time Format</a>
 */
public class MSDosTime {

    public final int Second;
    public final int Minute;
    public final int Hour;

    MSDosTime(int value) {
        this.Second = (value & 0x0000001F) * 2 ;  // Double the value, inaccurate for one second
        this.Minute = (value & 0x000007E0) >> 5;
        this.Hour   = (value & 0x0000F800) >> 11;
    }

    @Override
    public String toString(){
        return String.format("%02d:%02d:%02d", this.Hour, this.Minute, this.Second);
    }
}
