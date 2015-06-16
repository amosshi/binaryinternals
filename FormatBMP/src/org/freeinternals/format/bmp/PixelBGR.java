/**
 * PixelBGR.java    Apr 05, 2011, 18:38
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
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
 * The OS2 1x Palette Element.
 *
 * @author Amos Shi
 * @see <a href="http://netghost.narod.ru/gff/graphics/summary/os2bmp.htm"> OS/2 Bitmap </a>
 */
public class PixelBGR extends FileComponent implements GenerateTreeNode {

    public static final int LENGTH = 3;
    public final int Blue;
    public final int Green;
    public final int Red;

    PixelBGR(final PosDataInputStream input) throws IOException {
        this.startPos = input.getPos();
        this.length = PixelBGR.LENGTH;

        this.Blue = input.readUnsignedByte();
        this.Green = input.readUnsignedByte();
        this.Red = input.readUnsignedByte();
    }

    public void generateTreeNode(DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                1,
                String.format("Blue = %d", this.Blue))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 1,
                1,
                String.format("Green = %d", this.Green))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 2,
                1,
                String.format("Red = %d", this.Red))));
    }
}
