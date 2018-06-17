/*
 * IFD_8822_ExposureProgram.java    Oct 11, 2010, 23:17
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
 */
public class IFD_8822_ExposureProgram extends IFD_SHORT_COUNT1 {

    private final static String IFD_Description = IFDMessage.getString(IFDMessage.KEY_IFD_8822_Description)
            + IFD_8769_Exif.Category_G;

    public IFD_8822_ExposureProgram(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(parentNode, IFD_Description);
    }
}
