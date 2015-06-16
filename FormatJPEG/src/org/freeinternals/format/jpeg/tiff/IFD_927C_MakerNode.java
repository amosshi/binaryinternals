/*
 * IFD_927C_MakerNode.java    Oct 27, 2010, 00:15
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
 * @see IFD_8769_Exif#Category_D
 */
public class IFD_927C_MakerNode extends IFD_UNDEFINED {

    public IFD_927C_MakerNode(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                this.getTagName(),
                IFDMessage.getString(IFDMessage.KEY_IFD_927C_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D));
    }
}
