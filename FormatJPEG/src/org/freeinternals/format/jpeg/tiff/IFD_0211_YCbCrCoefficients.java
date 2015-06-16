/*
 * IFD_0211_YCbCrCoefficients.java    Oct 30, 2010, 17:39
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
public class IFD_0211_YCbCrCoefficients extends IFD_RATIONAL {

    public static final int COUNT = 3;

    public IFD_0211_YCbCrCoefficients(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_0211_Description));
    }
}
