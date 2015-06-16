/*
 * IFD_A210_FocalPlaneResolutionUnit.java    Oct 30, 2010, 18:12
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
 * @see IFD_A20E_FocalPlaneXResolution
 * @see IFD_A20E_FocalPlaneYResolution
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_A210_FocalPlaneResolutionUnit extends IFD_SHORT_COUNT1 {

    public IFD_A210_FocalPlaneResolutionUnit(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_A210_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G)
                );
    }
}
