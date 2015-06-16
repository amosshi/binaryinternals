/*
 * IFD_9209_Flash.java    Oct 26, 2010, 19:11
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
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_9209_Flash extends IFD_SHORT_COUNT1 {

    public final int FlashFired;
    public final int FlashReturn;
    public final int FlashMode;
    public final int FlashFunction;
    public final int RedeyeMode;

    public IFD_9209_Flash(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        this.FlashFired = this.value[0] & 0x00000001;
        this.FlashReturn = (this.value[0] >> 1) & 0x000000003;
        this.FlashMode = (this.value[0] >> 3) & 0x000000003;
        this.FlashFunction = (this.value[0] >> 5) & 0x000000001;
        this.RedeyeMode = (this.value[0] >> 6) & 0x000000001;
    }

    public String getValueBinaryString(){
        StringBuilder sb = new StringBuilder(8);
        sb.append(Integer.toBinaryString(this.value[0]));
        while(sb.length() < 7){
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {

        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                2,
                String.format("%s: %s", super.getTagName(), this.getValueBinaryString()));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_9209_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                String.format("Flash fired: %d", this.FlashFired))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                String.format("Flash return: %d", this.FlashReturn))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                String.format("Flash mode: %d", this.FlashMode))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                String.format("Flash function: %d", this.FlashFunction))));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                2,
                "Unused")));
    }
}
