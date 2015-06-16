/*
 * IFD_A300_FileSource.java    Oct 28, 2010, 13:31
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
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_A300_FileSource extends IFD_UNDEFINED {

    public static final int COUNT = 1;

    public IFD_A300_FileSource(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    public int getFileSource(){
        return (int)super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                String.format("%s: %d", this.getTagName(), this.getFileSource()),
                IFDMessage.getString(IFDMessage.KEY_IFD_A300_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
    }
}
