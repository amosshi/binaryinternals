/*
 * IFD_RATIONAL.java    Sep 09, 2010, 12:47
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

/// An IFD entry of type `RATIONAL`, unsigned fractions.
///
/// @author Amos Shi
public class IFD_RATIONAL extends IFD {

    /// The values.
    public final Rational[] value;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_RATIONAL(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, IFDType.RATIONAL, startPosTiff, byteArrayTiff);
        super.ifd_value_offset = IFDParse.readInt(pDIS, byteOrder);                 // Offset

        // Read the rational values
        if (super.ifd_count > 0) {
            final PosDataInputStream reader = IFDParse.getTiffOffsetReader(byteArrayTiff, super.ifd_value_offset, super.data_size);
            this.value = new Rational[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                final long n = IFDParse.readUnsignedInt(reader, byteOrder);
                final long d = IFDParse.readUnsignedInt(reader, byteOrder);
                this.value[i] = new Rational(n, d);
            }
        } else {
            this.value = new Rational[1];
            this.value[0] = new Rational(0, 0);
        }
    }

    /// Gets the values.
    ///
    /// @return The values
    public Rational[] getValues() {
        return this.value;
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_RATIONAL(parentNode, ShortText.getString(ShortText.KEY_Type_Rational));
    }

    void generateTreeNode_RATIONAL(DefaultMutableTreeNode parentNode, String description) {
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
                    ShortText.getString(ShortText.KEY_rational_n).formatted(i),
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

    /// An unsigned fraction.
    @SuppressWarnings("PublicInnerClass")
    public static class Rational {

        /// The numerator.
        public final long numerator;
        /// The denominator.
        public final long denominator;

        private Rational(long n, long d) {
            this.numerator = n;
            this.denominator = d;
        }
    }
}
