/*
 * IFD_SSHORT.java    Sep 09, 2010, 12:48
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.FileFormatException;


/**
 *
 * @author Amos Shi
 */
public class IFD_SSHORT extends IFD {

    public IFD_SSHORT(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.ASCII, startPosTiff, byteArrayTiff);
    }
}