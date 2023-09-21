/*
 * IFD_011A_XResolution.java    Oct 04, 2010, 10:48
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
 * @see IFD_0112_Orientation
 * @see IFD_0128_ResolutionUnit
 * @see IFD_011B_YResolution
 */
public class IFD_011A_XResolution extends IFD_RATIONAL_COUNT1 {

    public IFD_011A_XResolution(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_011A_Description));
    }
}
