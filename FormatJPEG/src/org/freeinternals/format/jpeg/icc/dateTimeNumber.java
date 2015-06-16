/*
 * dateTimeNumber.java    Nov 09, 2010, 23:02
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The type <code>dateTimeNumber</code> defined in ICC Specification
 * ICC.1:2004-10 (Profile version 4.2.0.0).
 *
 * @author Amos Shi
 */
public class dateTimeNumber {

    public final int year;
    public final int month;
    public final int day;
    public final int hour;
    public final int minute;
    public final int second;

    public dateTimeNumber(final PosDataInputStream input) throws IOException {
        this.year = input.readUnsignedShort();
        this.month = input.readUnsignedShort();
        this.day = input.readUnsignedShort();
        this.hour = input.readUnsignedShort();
        this.minute = input.readUnsignedShort();
        this.second = input.readUnsignedShort();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }
}
