/*
 * IFD_0128_ResolutionUnit.java    Sep 12, 2010, 22:57
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_0128_ResolutionUnit extends IFD_SHORT_COUNT1 {

    public IFD_0128_ResolutionUnit(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    public String getResolutionUnitName() {
        switch (super.value[0]) {
            case 1:
                return "No absolute unit of measurement";
            case 2:
                return "Inch";
            case 3:
                return "Centimeter";
            default:
                return "Unrecognized value";
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                2,
                String.format("Resolution Unit: %d - %s", this.value[0], this.getResolutionUnitName()));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_0128_Description));
        parentNode.add(new DefaultMutableTreeNode(comp));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                2,
                "Unused")));
    }
}
