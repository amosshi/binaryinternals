/*
 * IFD_A404_DigitalZoomRatio.java    Oct 28, 2010, 23:30
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
 * @see IFD_8769_Exif#CATEGORY_G
 */
public class IFD_A404_DigitalZoomRatio extends IFD_RATIONAL_COUNT1 {

    public IFD_A404_DigitalZoomRatio(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_A404_Description)
                + IFD_8769_Exif.CATEGORY_G);
    }
}
