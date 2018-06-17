/*
 * IFD_010E_ImageDescription.java    Sep 13, 2010, 23:15
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_010E_ImageDescription extends IFD_ASCII {

    private final static String IFD_Image_Description = "A string that describes the subject of the image."
            + "For example, a user may wish to attach a comment such as \"1988 company picnic\" to an image.";

    public IFD_010E_ImageDescription(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    String.format("Image Description: %s", this.value));
            comp.setDescription(IFD_010E_ImageDescription.IFD_Image_Description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    String.format("Offset: %d", super.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    String.format("Image Description: %s", this.value),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(IFD_010E_ImageDescription.IFD_Image_Description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }
}
