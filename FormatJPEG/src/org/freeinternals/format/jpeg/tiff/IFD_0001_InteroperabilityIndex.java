/*
 * IFD_0001_InteroperabilityIndex.java    Oct 30, 2010, 12:09
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
 * @see IFD_A005_Interoperability#Category_A
 */
public class IFD_0001_InteroperabilityIndex extends IFD_ASCII {

    public static final int COUNT = 4;

    public IFD_0001_InteroperabilityIndex(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_ASCII(
                parentNode,
                String.format("%s: %s", this.getTagName(), super.value),
                IFDMessage.getString(IFDMessage.KEY_IFD_ItO_0001_Description)
                + IFD_A005_Interoperability.Category_A);
    }
}
