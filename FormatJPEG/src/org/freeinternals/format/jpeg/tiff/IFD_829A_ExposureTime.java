/*
 * IFD_829A_ExposureTime.java    Oct 04, 2010, 14:29
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
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_829A_ExposureTime extends IFD_RATIONAL_COUNT1 {

    private final static String IFD_Description = IFDMessage.getString(IFDMessage.KEY_IFD_829A_Description)
            + IFD_8769_Exif.Category_G;

    public IFD_829A_ExposureTime(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /**
     *
     * @param parentNode
     */
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(parentNode, IFD_Description);
    }
}
