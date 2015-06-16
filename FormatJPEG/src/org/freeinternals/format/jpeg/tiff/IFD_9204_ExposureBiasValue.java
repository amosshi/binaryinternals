/*
 * IFD_9204_ExposureBiasValue.java    Sep 11, 2010, 23:48
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
public class IFD_9204_ExposureBiasValue extends IFD_SRATIONAL_COUNT1 {

    public IFD_9204_ExposureBiasValue(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SRATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_9204_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
    }
}
