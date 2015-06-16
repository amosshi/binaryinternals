/*
 * IFD_9101_ComponentsConfiguration.java    Oct 16, 2010, 15:22
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
 * @see IFD_0106_PhotometricInterpretation
 * @see IFD_8769_Exif#Category_C
 */
public class IFD_9101_ComponentsConfiguration extends IFD_UNDEFINED {

    public static final int COUNT = 4;

    public IFD_9101_ComponentsConfiguration(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    public String getValue(){
        final StringBuffer sb = new StringBuffer(20);
        sb.append(super.value[0]);
        sb.append(' ');
        sb.append(super.value[1]);
        sb.append(' ');
        sb.append(super.value[2]);
        sb.append(' ');
        sb.append(super.value[3]);

        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                String.format("%s: %s", this.getTagName(), this.getValue()),
                IFDMessage.getString(IFDMessage.KEY_IFD_9101_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C));
    }
}
