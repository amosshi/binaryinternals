/*
 * Chunk_bKGD.java    May 01, 2011, 22:37
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `bKGD` chunk specifies a default background color to present the image
/// against.
///
/// Note that viewers are not bound to honor this chunk; a viewer can choose to
/// use a different background.
///
/// Note: For color type 3 (indexed color), only the field `PaletteIndex` is
/// valid. For color types 0 and 4 (gray scale, with or without alpha), only the
/// field `Gray` is valid. For color types 2 and 6 (true color, with or without
/// alpha), only the fields `Red`, `Green` and `Blue` are valid.
///
/// When a field is not valid, the value will be set as `-1`.
///
/// @author Amos Shi
public final class Chunk_bKGD extends Chunk {

    public static final String CHUNK_TYPE_NAME = "bKGD";

    /// The value is the palette index of the color to be used as background.
    ///
    /// For color type 3 (indexed color), the `bKGD` chunk contains:
    ///
    /// ```
    /// Palette index:  1 byte
    /// ```
    public final int PaletteIndex;
    /// The value is the gray level to be used as background.
    ///
    /// For color types 0 and 4 (gray scale, with or without alpha), `bKGD`
    /// contains:
    ///
    /// ```
    /// Gray:  2 bytes, range 0 .. (2^bitdepth)-1
    /// ```
    ///
    /// If the image bit depth is less than 16, the least significant bits are
    /// used and the others are 0.
    public final int Gray;
    /// This is the RGB color to be used as background.
    ///
    /// For color types 2 and 6 (true color, with or without alpha), `bKGD`
    /// contains:
    ///
    /// ```
    /// Red:   2 bytes, range 0 .. (2^bitdepth)-1
    /// Green: 2 bytes, range 0 .. (2^bitdepth)-1
    /// Blue:  2 bytes, range 0 .. (2^bitdepth)-1
    /// ```
    ///
    /// If the image bit depth is less than 16, the least significant bits are
    /// used and the others are 0.
    public final int Red;
    /// Refer to field [#Red].
    public final int Green;
    /// Refer to field [#Red].
    public final int Blue;

    Chunk_bKGD(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        switch (this.Length) {
            case 1 -> {
                this.PaletteIndex = chunkDataStream.read();
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
            }
            case 2 -> {
                this.PaletteIndex = -1;
                this.Gray = chunkDataStream.readUnsignedShort();
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
            }
            case 6 -> {
                this.PaletteIndex = -1;
                this.Gray = -1;
                this.Red = chunkDataStream.readUnsignedShort();
                this.Green = chunkDataStream.readUnsignedShort();
                this.Blue = chunkDataStream.readUnsignedShort();
            }
            default -> {
                // Un-recognized
                this.PaletteIndex = -1;
                this.Gray = -1;
                this.Red = -1;
                this.Green = -1;
                this.Blue = -1;
            }
        }
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        final int start = this.getDataStartPos();

        switch (this.Length) {
            case 1 -> parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    1,
                    "Palette index = %d".formatted(this.PaletteIndex))));
            case 2 -> parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    2,
                    "Gray = %d".formatted(this.Gray))));
            case 6 -> {
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start,
                        2,
                        "Red = %d".formatted(this.Red))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 2,
                        2,
                        "Green = %d".formatted(this.Green))));
                parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        start + 4,
                        2,
                        "Blue = %d".formatted(this.Blue))));
            }
            default -> parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    this.Length,
                    "Un-recognized")));
        }
    }
}
