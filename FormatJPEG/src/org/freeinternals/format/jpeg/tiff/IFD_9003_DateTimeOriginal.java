/*
 * IFD_9003_DateTimeOriginal.java    Oct 16, 2010, 10:45
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
 * @see IFD_8769_Exif#Category_F
 */
public class IFD_9003_DateTimeOriginal extends IFD_ASCII {

    public static final int COUNT = 20;
    public final Calendar datetime;

    public IFD_9003_DateTimeOriginal(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);

        final PosDataInputStream reader = super.getTiffOffsetReader();
        final byte[] buf = new byte[IFD_9003_DateTimeOriginal.COUNT];
        reader.readFully(buf, 0, IFD_9003_DateTimeOriginal.COUNT);
        this.datetime = IFD_0132_DateTime.buf2calendar(buf);
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
                IFDMessage.getString(IFDMessage.KEY_IFD_9003_Description) + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_F));
    }
}
