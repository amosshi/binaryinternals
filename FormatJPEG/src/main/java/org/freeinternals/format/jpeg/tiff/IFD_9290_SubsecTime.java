/*
 * IFD_9290_SubsecTime.java    Oct 27, 2010, 22:02
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
 * @see IFD_0132_DateTime
 * @see IFD_8769_Exif#Category_F
 * @see IFD_9291_SubsecTimeOriginal
 * @see IFD_9292_SubsecTimeDigitized
 */
public class IFD_9290_SubsecTime extends IFD_ASCII {

    public IFD_9290_SubsecTime(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_ASCII(
                parentNode,
                String.format("%s: %s", super.getTagName(), this.value),
                IFDMessage.getString(IFDMessage.KEY_IFD_9290_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_F));
    }
}
