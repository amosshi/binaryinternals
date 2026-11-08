/*
 * IFD.java    Sep 06, 2010, 23:28
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

public class IFD extends FileComponent {

    public static final int SIZE = 12; // Size of IFD
    public final int tiff_StartPos;
    @SuppressWarnings("ProtectedField")
    protected byte[] tiff_ByteArray;
    public final int byte_order;
    public final int ifd_tag;
    public final int ifd_type;
    public final int ifd_count;
    @SuppressWarnings("ProtectedField")
    protected int ifd_value_offset;
    public final int data_size;                                                 // Total size of the IFD data
    private TagSpace tag_space = TagSpace.DEFAULT;

    protected IFD(final PosDataInputStream pDIS, int byteOrder, int tag, int type, int startPosTiff, byte[] byteArrayTiff) throws IOException {
        super.startPos = pDIS.getPos() - 4;
        super.length = IFD.SIZE;

        this.tiff_StartPos = startPosTiff;
        this.tiff_ByteArray = byteArrayTiff;
        this.byte_order = byteOrder;

        this.ifd_tag = tag;
        this.ifd_type = type;
        this.ifd_count = IFDParse.readInt(pDIS, byteOrder);

        this.data_size = dataSize(tag, type, this.ifd_count);
    }

    public IFD(final PosDataInputStream pDIS, int byteOrder, int tag, int type, int startPosTiff) throws IOException {
        super.startPos = pDIS.getPos() - 4;
        super.length = IFD.SIZE;

        this.tiff_StartPos = startPosTiff;
        this.byte_order = byteOrder;

        this.ifd_tag = tag;
        this.ifd_type = type;
        this.ifd_count = IFDParse.readInt(pDIS, byteOrder);
        this.ifd_value_offset = IFDParse.readInt(pDIS, byteOrder);                  // Maybe value

        this.data_size = dataSize(tag, type, this.ifd_count);
    }

    /// Total size of the values: count times the size of the type, or 0 for
    /// an unknown type.
    private static int dataSize(final int tag, final int type, final int count) throws FileFormatException {
        final long size = (long) Math.max(IFDType.getTypeLength(type), 0) * Integer.toUnsignedLong(count);
        if (size > Integer.MAX_VALUE) {
            throw new FileFormatException("IFD %04X: the data size %d is too big".formatted(tag, size));
        }
        return (int) size;
    }

    protected final void checkIFDCount(int countExpected) throws FileFormatException {
        checkIFDCount(this.ifd_tag, this.getTagNameForValidation(), this.ifd_count, countExpected);
    }

    static void checkIFDCount(int tag, int actualCount, int expectedCount) throws FileFormatException {
        checkIFDCount(tag, IFDTag.getTagName(tag), actualCount, expectedCount);
    }

    private static void checkIFDCount(int tag, String tagName, int actualCount, int expectedCount)
            throws FileFormatException {
        if (actualCount != expectedCount) {
            throw new FileFormatException("IFD %d (%s): ifd count must be %d. current value = %d".formatted(
                    tag,
                    tagName,
                    expectedCount,
                    actualCount));
        }
    }

    private String getTagNameForValidation() {
        return switch (this.tag_space) {
            case DEFAULT -> IFDTag.getTagName(this.ifd_tag);
            case GPS -> IFDTag.getTagNameGPS(this.ifd_tag);
            case INTERO -> IFDTag.getTagNameIntero(this.ifd_tag);
        };
    }

    protected final PosDataInputStream getTiffOffsetReader() throws IOException {
        return IFDParse.getTiffOffsetReader(this.tiff_ByteArray, this.ifd_value_offset, this.data_size);
    }

    // Note: Common tag only, none GPS, none InterO
    public String getTagName() {
        return this.getTagNameForValidation();
    }

    public TagSpace getTagSpace() {
        return this.tag_space;
    }

    protected final int readUnsignedShort(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readUnsignedShort(pDIS, this.byte_order);
    }

    protected final int readInt(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readInt(pDIS, this.byte_order);
    }

    protected final long readUnsignedInt(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readUnsignedInt(pDIS, this.byte_order);
    }

    void setTagSpace(TagSpace ts) {
        this.tag_space = ts;
    }

    public boolean isValue() {
        return this.data_size > 0 && this.data_size <= 4;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.startPos + 0,
                2,
                "Tag: %04X.H (%d)".formatted(this.ifd_tag, this.ifd_tag));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Tag));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 2,
                2,
                "Type: %d - %s".formatted(this.ifd_type, IFDType.getTypeName(this.ifd_type)));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Type));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                "Count: %d".formatted(this.ifd_count));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Count));
        parentNode.add(new DefaultMutableTreeNode(comp));

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    "Value: %X".formatted(this.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value));
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    "Offset: %d".formatted(this.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.tiff_StartPos + this.ifd_value_offset,
                    this.ifd_count * IFDType.getTypeLength(this.ifd_type),
                    "data",
                    Icons.Shortcut, null);
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }

    protected void generateTreeNode(DefaultMutableTreeNode parentNode, int pos) {

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 0,
                2,
                "Tag: %04X.H (%d)".formatted(this.ifd_tag, this.ifd_tag));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Tag));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                pos + 2,
                2,
                "Type: %d - %s".formatted(this.ifd_type, IFDType.getTypeName(this.ifd_type)));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Type));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                pos + 4,
                4,
                "Count: %d".formatted(this.ifd_count));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Count));
        parentNode.add(new DefaultMutableTreeNode(comp));
    }

    @SuppressWarnings("PublicInnerClass")
    public enum TagSpace {

        DEFAULT,
        GPS,
        INTERO
    }
}
