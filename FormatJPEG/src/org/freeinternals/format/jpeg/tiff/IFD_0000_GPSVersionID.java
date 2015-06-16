/*
 * IFD_0000_GPSVersionID.java    Oct 30, 2010, 12:09
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;


/**
 *
 * @author Amos Shi
 * @see IFD_8825_GPS#Category_A
 */
public class IFD_0000_GPSVersionID extends IFD_BYTE {

    public static final int COUNT = 4;

    public IFD_0000_GPSVersionID(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    public String getGPSVersionID() {
        StringBuilder sb = new StringBuilder(3);
        sb.append(super.value[0]);
        sb.append('.');
        sb.append(super.value[1]);
        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_BYTE(
                parentNode,
                String.format("%s: %s", this.getTagName(), this.getGPSVersionID()),
                IFDMessage.getString(IFDMessage.KEY_IFD_GPS_0000_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8825_GPS_Category_A));
    }
}
