/*
 * IFD_0213_YCbCrPositioning.java    Sep 11, 2010, 23:48
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
public class IFD_0213_YCbCrPositioning extends IFD_SHORT_COUNT1 {

    public IFD_0213_YCbCrPositioning(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {

        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    public int getYCbCrPositioning() {
        return super.getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_0213_Description));
    }
}
