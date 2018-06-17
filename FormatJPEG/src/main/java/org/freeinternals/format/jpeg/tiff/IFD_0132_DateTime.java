/*
 * IFD_0132_DateTime.java    Sep 11, 2010, 23:48
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import java.util.Calendar;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_0132_DateTime extends IFD_ASCII {

    public static final int COUNT = 20;
    public final Calendar datetime;

    public IFD_0132_DateTime(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);

        final PosDataInputStream reader = super.getTiffOffsetReader();
        final byte[] buf = new byte[IFD_0132_DateTime.COUNT];
        reader.readFully(buf, 0, IFD_0132_DateTime.COUNT);
        this.datetime = IFD_0132_DateTime.buf2calendar(buf);
    }

    /**
     * Convert a <code>YYYY:MM:DD HH:MM:SS</code> byte buffer into a <code>Calendar</code> instance.
     */
    static Calendar buf2calendar(final byte[] buf) {
        final int value_0 = 48;                                                 // ASCII value of '0'
        final int year = (Byte.valueOf(buf[0]).intValue() - value_0) * 1000
                + (Byte.valueOf(buf[1]).intValue() - value_0) * 100
                + (Byte.valueOf(buf[2]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[3]).intValue() - value_0);
        final int month = (Byte.valueOf(buf[5]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[6]).intValue() - value_0);
        final int date = (Byte.valueOf(buf[8]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[9]).intValue() - value_0);
        final int hourOfDay = (Byte.valueOf(buf[11]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[12]).intValue() - value_0);
        final int minute = (Byte.valueOf(buf[14]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[15]).intValue() - value_0);
        final int second = (Byte.valueOf(buf[17]).intValue() - value_0) * 10
                + (Byte.valueOf(buf[18]).intValue() - value_0);

        Calendar c = Calendar.getInstance();
        c.set(year, month, date, hourOfDay, minute, second);
        return c;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_ASCII(
                parentNode,
                String.format("%04d:%02d:%02d %02d:%02d:%02d",
                this.datetime.get(Calendar.YEAR),
                this.datetime.get(Calendar.MONTH),
                this.datetime.get(Calendar.DATE),
                this.datetime.get(Calendar.HOUR_OF_DAY),
                this.datetime.get(Calendar.MINUTE),
                this.datetime.get(Calendar.SECOND)),
                IFDMessage.getString(IFDMessage.KEY_IFD_0132_Description));
    }
}
