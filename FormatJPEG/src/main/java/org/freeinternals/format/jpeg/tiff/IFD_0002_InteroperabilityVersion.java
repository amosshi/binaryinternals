/*
 * IFD_0002_InteroperabilityVersion.java    Nov 06, 2010, 15:24
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
public class IFD_0002_InteroperabilityVersion extends IFD_UNDEFINED {

    public static final int COUNT = 4;

    public IFD_0002_InteroperabilityVersion(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);
    }

    public String getVersion() {
        final StringBuffer sb = new StringBuffer(COUNT + 1);
        for (int i = 0; i < COUNT; i++) {
            sb.append((char) super.value[i]);
        }
        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                String.format("%s: %s", this.getTagName(), this.getVersion()),
                null);
    }
}
