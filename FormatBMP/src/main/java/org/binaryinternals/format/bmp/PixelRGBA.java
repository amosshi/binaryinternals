/*
 * PixelRGBA.java    Dec 03, 2010, 21:55
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.bmp;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The Windows palette element, `RGBQUAD`, 4 bytes.
///
/// The bytes are stored in the order blue, green, red, then a reserved byte
/// that some applications use for alpha.
///
/// @author Amos Shi
/// @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-rgbquad">RGBQUAD structure</a>
public final class PixelRGBA extends FileComponent implements Pixel {

    /// Length of the element in bytes.
    public static final int LENGTH = 4;
    /// Blue intensity.
    public final int Blue;
    /// Green intensity.
    public final int Green;
    /// Red intensity.
    public final int Red;
    /// The reserved byte, usually 0.
    public final int Alpha;

    PixelRGBA(final PosDataInputStream input) throws IOException {
        super(input.getPos(), LENGTH);

        this.Blue = input.readUnsignedByte();
        this.Green = input.readUnsignedByte();
        this.Red = input.readUnsignedByte();
        this.Alpha = input.readUnsignedByte();
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
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 3,
                1,
                "Alpha = %d".formatted(this.Alpha))));
    }
}
