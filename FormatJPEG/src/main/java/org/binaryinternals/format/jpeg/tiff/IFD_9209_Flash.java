/*
 * IFD_9209_Flash.java    Oct 26, 2010, 19:11
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x9209`, `Flash`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_G
public class IFD_9209_Flash extends IFD_SHORT_COUNT1 {

    /// Bit 0: whether the flash fired.
    public final int FlashFired;
    /// Bits 1 - 2: the status of returned light.
    public final int FlashReturn;
    /// Bits 3 - 4: the camera flash mode.
    public final int FlashMode;
    /// Bit 5: whether the flash function is present.
    public final int FlashFunction;
    /// Bit 6: whether the red-eye reduction mode is used.
    public final int RedeyeMode;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_9209_Flash(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        this.FlashFired = this.value[0] & 0x00000001;
        this.FlashReturn = (this.value[0] >> 1) & 0x000000003;
        this.FlashMode = (this.value[0] >> 3) & 0x000000003;
        this.FlashFunction = (this.value[0] >> 5) & 0x000000001;
        this.RedeyeMode = (this.value[0] >> 6) & 0x000000001;
    }

    /// Gets the flash value as 7 binary digits.
    ///
    /// @return The binary text
    public String getValueBinaryString() {
        StringBuilder sb = new StringBuilder(8);
        sb.append(Integer.toBinaryString(this.value[0]));
        while (sb.length() < 7) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {

        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        final JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                pos + 8,
                2,
                "%s: %s".formatted(super.getTagName(), this.getValueBinaryString()));
        comp.setDescription(this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
        parentNode.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                "Flash fired: %d".formatted(this.FlashFired))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                "Flash return: %d".formatted(this.FlashReturn))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                "Flash mode: %d".formatted(this.FlashMode))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                "Flash function: %d".formatted(this.FlashFunction))));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                2,
                "Unused")));
    }
}
