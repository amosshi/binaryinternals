/*
 * IFD_010F_Make.java    Sep 13, 2010, 23:16
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
 * @see IFD_0110_Model
 * @see IFD_0131_Software
 */
public class IFD_010F_Make extends IFD_ASCII {

    public IFD_010F_Make(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_ASCII(
                parentNode,
                String.format("%s: %s", super.getTagName(), this.value),
                IFDMessage.getString(IFDMessage.KEY_IFD_010F_Description));
    }
}
