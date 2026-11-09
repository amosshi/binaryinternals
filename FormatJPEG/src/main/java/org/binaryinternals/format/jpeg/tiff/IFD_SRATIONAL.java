/*
 * IFD_SRATIONAL.java    Sep 09, 2010, 12:49
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An IFD entry of type `SRATIONAL`, signed fractions.
///
/// @author Amos Shi
public class IFD_SRATIONAL extends IFD {

    /// The values.
    public final SRational[] value;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_SRATIONAL(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, IFDType.SRATIONAL, startPosTiff, byteArrayTiff);
        super.ifd_value_offset = IFDParse.readInt(pDIS, byteOrder);                 // Offset

        // Read the rational values
        if (super.ifd_count > 0) {
            final PosDataInputStream reader = IFDParse.getTiffOffsetReader(byteArrayTiff, super.ifd_value_offset, super.data_size);
            this.value = new SRational[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                final int n = IFDParse.readInt(reader, byteOrder);
                final int d = IFDParse.readInt(reader, byteOrder);
                this.value[i] = new SRational(n, d);
            }
        } else {
            this.value = new SRational[1];
            this.value[0] = new SRational(0, 0);
        }
    }

    /// Gets the values.
    ///
    /// @return The values
    public SRational[] getValues() {
        return this.value;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_SRATIONAL(parentNode, ShortText.getString(ShortText.KEY_Type_SRational));
    }

    void generateTreeNode_SRATIONAL(DefaultMutableTreeNode parentNode, String description) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                ShortText.getString(ShortText.KEY_Offset_n).formatted(super.ifd_value_offset));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        if (super.ifd_count <= 0) {
            return;
        }
        for (int i = 0; i < super.ifd_count; i++) {
            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8,
                    8,
                    ShortText.getString(ShortText.KEY_srational_n).formatted(i),
                    Icons.Shortcut, null);
            comp.setDescription(description);
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
            parentNode.add(node);

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8,
                    4,
                    ShortText.getString(ShortText.KEY_numerator_n).formatted(this.value[0].numerator),
                    Icons.Shortcut, null);
            comp.setDescription(ShortText.getString(ShortText.KEY_Numerator));
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8 + 4,
                    4,
                    ShortText.getString(ShortText.KEY_denominator_n).formatted(this.value[0].denominator),
                    Icons.Shortcut, null);
            comp.setDescription(ShortText.getString(ShortText.KEY_Denominator));
            node.add(new DefaultMutableTreeNode(comp));
        }
    }

    /// A signed fraction.
    @SuppressWarnings("PublicInnerClass")
    public static class SRational {

        /// The numerator.
        public final int numerator;
        /// The denominator.
        public final int denominator;

        private SRational(int n, int d) {
            this.numerator = n;
            this.denominator = d;
        }
    }
}
