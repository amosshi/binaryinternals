/**
 * BMPFile.java    Nov 28, 2010, 21:00
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.bmp;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class BMPFile extends FileFormat{

    public final BitmapFileHeader bitmapFileHeader;
    public final DIBHeader dibHeader;
    public final ColorTable colorTable;
    public final PixelArray pixelArray;

    public BMPFile(final File file) throws IOException, FileFormatException {
        super(file);

        // Parse
        PosDataInputStream input = new PosDataInputStream(
                new PosByteArrayInputStream(this.fileByteArray));
        this.bitmapFileHeader = new BitmapFileHeader(input);
        this.dibHeader = new DIBHeader(input);

        if (input.getPos() < this.bitmapFileHeader.offset) {
            this.colorTable = new ColorTable(input, this);
        } else {
            this.colorTable = null;
        }

        this.pixelArray = new PixelArray(this);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int lastPos = 0;
        int distance;

        this.bitmapFileHeader.generateTreeNode(parentNode);
        lastPos = lastPos + BitmapFileHeader.LENGTH;

        this.dibHeader.generateTreeNode(parentNode);
        lastPos = lastPos + this.dibHeader.size;

        if (this.colorTable != null) {
            this.colorTable.generateTreeNode(parentNode);
            lastPos = lastPos + this.colorTable.getLength();
        }

        distance = (this.pixelArray.getStartPos() - lastPos);
        if (distance > 0) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos,
                    distance,
                    String.format("GAP1 [0x%08X, %d]", lastPos, distance))));
        }

        this.pixelArray.generateTreeNode(parentNode);
    }

    @Override
    public String getContentTabName() {
        return "BMP File";
    }
}
