/*
 * IFD_0106_PhotometricInterpretation.java    Oct 04, 2010, 14:55
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
 */
public class IFD_0106_PhotometricInterpretation extends IFD_SHORT_COUNT1 {

    public IFD_0106_PhotometricInterpretation(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {

        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_0106_Description));
    }
}
