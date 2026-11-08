/*
 * IFD_0128_ResolutionUnit.java    Sep 12, 2010, 22:57
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class IFD_0128_ResolutionUnit extends IFD_SHORT_COUNT1 {

    public IFD_0128_ResolutionUnit(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    public String getResolutionUnitName() {
        return switch (super.value[0]) {
            case 1 -> "No absolute unit of measurement";
            case 2 -> "Inch";
            case 3 -> "Centimeter";
            default -> "Unrecognized value";
        };
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        final JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                pos + 8,
                2,
                "Resolution Unit: %d - %s".formatted(this.value[0], this.getResolutionUnitName()));
        comp.setDescription(this.getDescription());
        parentNode.add(new DefaultMutableTreeNode(comp));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                2,
                "Unused")));
    }
}
