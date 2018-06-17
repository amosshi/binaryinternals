/*
 * IFD_0201_JPEGInterchangeFormat.java    Oct 31, 2010, 23:14
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
public class IFD_0201_JPEGInterchangeFormat extends IFD_LONG_COUNT1 {

    public IFD_0201_JPEGInterchangeFormat(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    public boolean isBitstreamPresent(){
        return (super.value[0] > 0);
    }

    /**
     * The <code>JPEG</code> interchange format bit stream offset within the <code>TIFF</code>.
     * This attribute is valid only if {@link  #isBitstreamPresent} returns <code>true</code>.
     * @return the offset value
     */
    public long getBitstreamOffset(){
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_0201_Description));
    }
}
