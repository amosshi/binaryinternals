/*
 * PixelBGR.java    Apr 05, 2011, 18:38
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.bmp;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The OS2 1x Palette Element.
///
/// @author Amos Shi
/// @see <a href="http://netghost.narod.ru/gff/graphics/summary/os2bmp.htm"> OS/2 Bitmap </a>
public final class PixelBGR extends FileComponent implements Pixel {

    public static final int LENGTH = 3;
    public final int Blue;
    public final int Green;
    public final int Red;

    PixelBGR(final PosDataInputStream input) throws IOException {
        super(input.getPos(), LENGTH);

        this.Blue = input.readUnsignedByte();
        this.Green = input.readUnsignedByte();
        this.Red = input.readUnsignedByte();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                1,
                "Blue = %d".formatted(this.Blue))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 1,
                1,
                "Green = %d".formatted(this.Green))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 2,
                1,
                "Red = %d".formatted(this.Red))));
    }
}
