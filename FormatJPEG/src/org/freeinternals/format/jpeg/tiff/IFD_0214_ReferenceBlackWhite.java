/*
 * IFD_0214_ReferenceBlackWhite.java    Oct 04, 2010, 11:08
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
 * @see IFD_0213_YCbCrPositioning
 * @see PhotometricInterpretation
 */
public class IFD_0214_ReferenceBlackWhite extends IFD_RATIONAL {

    public static final int COUNT = 6;

    public IFD_0214_ReferenceBlackWhite(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    /**
     *
     * @param parentNode
     */
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_0214_Description));
    }
}
