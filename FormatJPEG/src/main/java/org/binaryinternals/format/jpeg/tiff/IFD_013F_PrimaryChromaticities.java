/*
 * IFD_013F_PrimaryChromaticities.java    Oct 30, 2010, 16:29
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_013F_PrimaryChromaticities extends IFD_RATIONAL {

    public static final int COUNT = 6;

    public IFD_013F_PrimaryChromaticities(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_013F_Description));
    }
}
