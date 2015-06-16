/*
 * IFD_9205_MaxApertureValue.java    Oct 26, 2010, 13:28
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
public class IFD_9205_MaxApertureValue extends IFD_RATIONAL_COUNT1 {

    public IFD_9205_MaxApertureValue(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /**
     *
     * @param parentNode
     */
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_9205_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G)
                );
    }
}
