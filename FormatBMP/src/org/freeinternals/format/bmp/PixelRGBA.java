/**
 * PixelRGBA.java    Dec 03, 2010, 21:55
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
public class PixelRGBA extends FileComponent implements GenerateTreeNode {

    public static final int LENGTH = 4;
    public final int Red;
    public final int Green;
    public final int Blue;
    public final int Alpha;

    PixelRGBA(final PosDataInputStream input) throws IOException {
        this.startPos = input.getPos();
        this.length = PixelRGBA.LENGTH;

        this.Red = input.readUnsignedByte();
        this.Green = input.readUnsignedByte();
        this.Blue = input.readUnsignedByte();
        this.Alpha = input.readUnsignedByte();
    }

    public void generateTreeNode(DefaultMutableTreeNode parent) {
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                1,
                String.format("Red = %d", this.Red))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 1,
                1,
                String.format("Green = %d", this.Green))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 2,
                1,
                String.format("Blue = %d", this.Blue))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 3,
                1,
                String.format("Alpha = %d", this.Alpha))));
    }
}
