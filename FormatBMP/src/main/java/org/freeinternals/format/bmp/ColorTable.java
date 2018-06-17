/**
 * ColorTable.java    Dec 01, 2010, 22:59
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.bmp;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class ColorTable extends FileComponent implements GenerateTreeNode {

    public final Object[] palette;

    ColorTable(final PosDataInputStream input, final BMPFile bmpFile) throws IOException {

        int bpp = bmpFile.dibHeader.getBitCount();

        if ((bpp < 0) || (bpp > 8)) {
            this.palette = null;
            return;
        }

        this.startPos = input.getPos();
        int size = 2 << (bpp - 1);

        if (bmpFile.dibHeader.data instanceof DIBHeader.BITMAPCOREHEADER) {
            this.length = size * PixelBGR.LENGTH;
            this.palette = new PixelBGR[size];
            for (int i = 0; i < size; i++) {
                this.palette[i] = new PixelBGR(input);
            }

            // TODO - Should we have another if branch?
        } else {
            this.length = size * PixelRGBA.LENGTH;
            this.palette = new PixelRGBA[size];
            for (int i = 0; i < size; i++) {
                this.palette[i] = new PixelRGBA(input);
            }
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.palette == null) {
            return;
        }

        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;
        DefaultMutableTreeNode nodePixel;
        String pixelTypeName;
        int pixelLength;

        comp = new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("Color Table [0x%08X, %d]", this.startPos, this.length));
        comp.setDescription("The Color Table is a block of bytes (a table) listing the colors used by the image.");
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        pixelTypeName = this.palette[0].getClass().getSimpleName();
        pixelLength = ((FileComponent) this.palette[0]).getLength();

        for (int i = 0; i < this.palette.length; i++) {
            node.add(nodePixel = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + pixelLength * i,
                    pixelLength,
                    String.format("%s [%d]", pixelTypeName, i))));
            ((GenerateTreeNode) this.palette[i]).generateTreeNode(nodePixel);
        }
    }
}
