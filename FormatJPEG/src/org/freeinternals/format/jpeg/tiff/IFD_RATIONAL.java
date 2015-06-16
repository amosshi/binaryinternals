/*
 * IFD_RATIONAL.java    Sep 09, 2010, 12:47
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_RATIONAL extends IFD {

    public final Rational[] value;

    public IFD_RATIONAL(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.RATIONAL, startPosTiff, byteArrayTiff);
        super.ifd_value_offset = super.readInt(pDIS);                               // Offset

        // Read the rational values
        if (super.ifd_count > 0) {
            long n, d;

            final PosDataInputStream reader = super.getTiffOffsetReader();
            this.value = new Rational[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                n = super.readUnsignedInt(reader);
                d = super.readUnsignedInt(reader);
                this.value[i] = new Rational(n, d);
            }
        } else {
            this.value = new Rational[1];
            this.value[0] = new Rational(0, 0);
        }
    }

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

    /**
     *
     * @param parentNode
     * @param description 
     */
    void generateTreeNode_RATIONAL(DefaultMutableTreeNode parentNode, String description) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                String.format(ShortText.getString(ShortText.KEY_Offset_n), super.ifd_value_offset));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        if (super.ifd_count <= 0) {
            return;
        }
        for (int i = 0; i < super.ifd_count; i++) {
            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8,
                    8,
                    String.format(ShortText.getString(ShortText.KEY_rational_n), i),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(description);
            node = new DefaultMutableTreeNode(comp);
            parentNode.add(node);

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8,
                    4,
                    String.format(ShortText.getString(ShortText.KEY_numerator_n), this.value[0].numerator),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(ShortText.getString(ShortText.KEY_Numerator));
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + i * 8 + 4,
                    4,
                    String.format(ShortText.getString(ShortText.KEY_denominator_n), this.value[0].denominator),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(ShortText.getString(ShortText.KEY_Denominator));
            node.add(new DefaultMutableTreeNode(comp));
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public class Rational {

        public final long numerator;
        public final long denominator;

        private Rational(long n, long d) {
            this.numerator = n;
            this.denominator = d;
        }
    }
}
