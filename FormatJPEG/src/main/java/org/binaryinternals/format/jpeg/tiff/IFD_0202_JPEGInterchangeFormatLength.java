/*
 * IFD_0202_JPEGInterchangeFormatLength.java    Oct 31, 2010, 23:14
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
 * @see IFD_0201_JPEGInterchangeFormat
 */
public class IFD_0202_JPEGInterchangeFormatLength extends IFD_LONG_COUNT1 {

    public IFD_0202_JPEGInterchangeFormatLength(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /**
     * The length in bytes of the <code>JPEG</code> interchange format bit stream.
     * 
     * @return the length
     */
    public long getBitstreamLength(){
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_0202_Description));
    }
}
