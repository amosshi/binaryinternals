/**
 * PixelArray.java    Dec 03, 2010, 22:19
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.bmp;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.bmp.DIBHeader.CompressionMethod;

/**
 *
 * @author Amos Shi
 */
public class PixelArray extends FileComponent implements GenerateTreeNode {

    private final BMPFile bmpFile;

    PixelArray(final BMPFile bmpFile) throws IOException {
        this.bmpFile = bmpFile;
        this.startPos = (int) bmpFile.bitmapFileHeader.offset;
        this.length = (int) bmpFile.dibHeader.calcImageSize();
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("Pixel Array [0x%08X, %d]", this.startPos, this.length));
        comp.setDescription("The pixel format is defined by the DIB Header. <br/>Each row in the Pixel Array is padded to a multiple of 4 bytes in size.");
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        if (this.bmpFile.dibHeader.getCompressionMethod() == CompressionMethod.BI_RGB.value) {
            switch (this.bmpFile.dibHeader.getBitCount()) {
                case 1:
                    this.generateTreeNode_bpp_1(node);
                    break;
                case 4:
                    this.generateTreeNode_bpp_4(node);
                    break;
                case 8:
                    this.generateTreeNode_bpp_8(node);
                    break;
                case 16:
                    this.generateTreeNode_bpp_16(node);
                    break;
                case 24:
                    this.generateTreeNode_bpp_24(node);
                    break;
                case 32:
                    this.generateTreeNode_bpp_32(node);
                    break;
                default:
            }
        } else {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos,
                    this.length,
                    "Compressed Data")));
        }
    }

    /** Generate tree nodes for 1-bit per pixel (1 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_1(DefaultMutableTreeNode parentNode) {
        int rowSize = ((this.bmpFile.dibHeader.getWidth() + 31) >> 5) << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }

    /** Generate tree nodes for 4-bit per pixel (4 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_4(DefaultMutableTreeNode parentNode) {
        int rowSize = ((this.bmpFile.dibHeader.getWidth() + 7) >> 3) << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }

    /** Generate tree nodes for 8-bit per pixel (8 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_8(DefaultMutableTreeNode parentNode) {
        int rowSize = ((this.bmpFile.dibHeader.getWidth() + 3) >> 2) << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }

    /** Generate tree nodes for 16-bit per pixel (16 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_16(DefaultMutableTreeNode parentNode) {
        int rowSize = (((this.bmpFile.dibHeader.getWidth() << 1) + 3) >> 2) << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }

    /** Generate tree nodes for 24-bit per pixel (24 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_24(DefaultMutableTreeNode parentNode) {
        int rowSize = (((this.bmpFile.dibHeader.getWidth() << 1) + this.bmpFile.dibHeader.getWidth() + 3) >> 2) << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }

    /** Generate tree nodes for 32-bit per pixel (32 <code>bpp</code>) format. */
    private void generateTreeNode_bpp_32(DefaultMutableTreeNode parentNode) {
        int rowSize = this.bmpFile.dibHeader.getWidth() << 2;
        for (int i = 0; i < this.bmpFile.dibHeader.getHeight(); i++) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + rowSize * i,
                    rowSize,
                    String.format("row [%d]", i))));
        }
    }
}
