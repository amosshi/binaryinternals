/**
 * BitmapFileHeader.java    Nov 28, 2010, 21:00
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
public class BitmapFileHeader extends FileComponent implements GenerateTreeNode {

    public static final int LENGTH = 14;

    /**
     * The magic number used to identify the BMP file, usually 0x42 0x4D in hex,
     * same as BM in ASCII.
     *
     * The following entries are possible:
     * <ul>
     * <li> <strong>BM</strong> – Windows 3.1x, 95, NT, ... etc. </li>
     * <li> <strong>BA</strong> – OS/2 Bitmap Array</li>
     * <li> <strong>CI</strong> – OS/2 Color Icon</li>
     * <li> <strong>CP</strong> – OS/2 Color Pointer</li>
     * <li> <strong>IC</strong> – OS/2 Icon</li>
     * <li> <strong>PT</strong> – OS/2 Pointer</li>
     * </ul>
     */
    public final String magic;
    /** The fileSize of the BMP file in bytes */
    public final long fileSize;
    /** Reserved; actual value depends on the application that creates the image */
    public final byte[] creator1 = new byte[2];
    /** Reserved; actual value depends on the application that creates the image */
    public final byte[] creator2 = new byte[2];
    /** The offset, i.e. starting address, of the byte where the bitmap data can be found. */
    public final long offset;

    BitmapFileHeader(final PosDataInputStream input) throws IOException{
        this.startPos = input.getPos();
        this.length = LENGTH;

        this.magic = input.readASCII(2);
        this.fileSize = input.readUnsignedInt_LittleEndian();
        input.readFully(this.creator1);
        input.readFully(this.creator2);
        this.offset = input.readUnsignedInt_LittleEndian();

        if (this.fileSize != input.getBuf().length) {
            // File size exception
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("Bitmap File Header [0x%08X, %d]", this.startPos, this.length));
        comp.setDescription("This block of bytes is at the start of the file and is used to identify the file. A typical application reads this block first to ensure that the file is actually a BMP file and that it is not damaged.");
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos,
                2,
                String.format("magic number = %s", this.magic));
        comp.setDescription("The magic number used to identify the BMP file, usually <code>0x42</code> <code>0x4D</code> in hex, same as <strong>BM</strong> in ASCII. ");
        node.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 2,
                4,
                String.format("file size = %d (0x%04X)", this.fileSize, this.fileSize));
        comp.setDescription("The size of the BMP file in bytes.");
        node.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 6,
                2,
                "reserved 1");
        comp.setDescription("Reserved; actual value depends on the application that creates the image.");
        node.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 8,
                2,
                "reserved 2");
        comp.setDescription("Reserved; actual value depends on the application that creates the image.");
        node.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 10,
                4,
                String.format("offset = %d (0x%08X)", this.offset, this.offset));
        comp.setDescription("The offset, i.e. starting address, of the byte where the bitmap data can be found.");
        node.add(new DefaultMutableTreeNode(comp));
    }



}
