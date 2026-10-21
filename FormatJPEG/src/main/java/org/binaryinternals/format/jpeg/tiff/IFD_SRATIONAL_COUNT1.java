/*
 * IFD_SRATIONAL_COUNT1.java    Oct 26, 2010, 12:43
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 *
 * @author Amos Shi
 */
public class IFD_SRATIONAL_COUNT1 extends IFD_SRATIONAL {

    public static final int COUNT = 1;

    public IFD_SRATIONAL_COUNT1(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {

        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue(){
        return false;
    }

    public SRational getValue() {
        return this.value[0];
    }
}
