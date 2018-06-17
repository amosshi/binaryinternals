/*
 * IFD_SRATIONAL.java    Sep 09, 2010, 12:49
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
public class IFD_SRATIONAL extends IFD {

    public final SRational[] value;

    public IFD_SRATIONAL(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.SRATIONAL, startPosTiff, byteArrayTiff);
        super.ifd_value_offset = super.readInt(pDIS);                               // Offset

        // Read the rational values
        if (super.ifd_count > 0) {
            int n, d;

            final PosDataInputStream reader = super.getTiffOffsetReader();
            this.value = new SRational[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                n = super.readInt(reader);
                d = super.readInt(reader);
                this.value[i] = new SRational(n, d);
            }
        } else {
            this.value = new SRational[1];
            this.value[0] = new SRational(0, 0);
        }
    }

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

    /**
     *
     * @param parentNode
     * @param description
     */
    void generateTreeNode_SRATIONAL(DefaultMutableTreeNode parentNode, String description) {
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
                    String.format(ShortText.getString(ShortText.KEY_srational_n), i),
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
    public class SRational {

        public final int numerator;
        public final int denominator;

        private SRational(int n, int d) {
            this.numerator = n;
            this.denominator = d;
        }
    }
}
