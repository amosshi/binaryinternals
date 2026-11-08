/*
 * BMPFile.java    Nov 28, 2010, 21:00
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.bmp;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Bitmap image, or Device Independent Bitmap (DIB), file.
///
/// @author Amos Shi
public class BMPFile extends FileFormat {

    public final BitmapFileHeader bitmapFileHeader;
    public final DIBHeader dibHeader;
    /// The color table, or `null` when the pixel data follows the DIB header.
    public final ColorTable colorTable;
    public final PixelArray pixelArray;

    public BMPFile(final Path file) throws IOException {
        super(file);

        // Parse
        final PosDataInputStream input = new PosDataInputStream(new PosByteArrayInputStream(this.fileByteArray));
        this.bitmapFileHeader = new BitmapFileHeader(input);
        this.dibHeader = new DIBHeader(input);

        this.colorTable = input.getPos() < this.bitmapFileHeader.offset
                ? new ColorTable(input, this.dibHeader, this.bitmapFileHeader.offset)
                : null;

        final int pixelArrayOffset = Math.toIntExact(this.bitmapFileHeader.offset);
        this.pixelArray = new PixelArray(this.dibHeader, pixelArrayOffset, this.fileByteArray.length - pixelArrayOffset);
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        int lastPos = 0;

        this.bitmapFileHeader.generateTreeNode(parentNode);
        lastPos += BitmapFileHeader.LENGTH;

        this.dibHeader.generateTreeNode(parentNode);
        lastPos += this.dibHeader.size;

        if (this.colorTable != null) {
            this.colorTable.generateTreeNode(parentNode);
            lastPos += this.colorTable.getLength();
        }

        final int distance = this.pixelArray.getStartPos() - lastPos;
        if (distance > 0) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos,
                    distance,
                    "GAP1 [0x%08X, %d]".formatted(lastPos, distance))));
        }

        this.pixelArray.generateTreeNode(parentNode);
    }

    @Override
    public String getContentTabName() {
        return "BMP File";
    }
}
