/*
 * IFD.java    Sep 06, 2010, 23:28
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;

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

    /**
     * 
     * @param pDIS
     * @param byteOrder
     * @param tag
     * @param type
     * @param startPosTiff
     * @param byteArrayTiff
     * @throws IOException
     * @throws JPEGFileFormatException 
     */
    protected IFD(final PosDataInputStream pDIS, int byteOrder, int tag, int type, int startPosTiff, byte[] byteArrayTiff) throws IOException, FileFormatException {
        super.startPos = pDIS.getPos() - 4;
        super.length = IFD.SIZE;

        this.tiff_StartPos = startPosTiff;
        this.tiff_ByteArray = byteArrayTiff;
        this.byte_order = byteOrder;

        this.ifd_tag = tag;
        this.ifd_type = type;
        this.ifd_count = this.readInt(pDIS);

        this.data_size = IFDType.getTypeLength(this.ifd_type) * this.ifd_count;
    }

    /**
     * 
     * @param pDIS
     * @param byteOrder
     * @param tag
     * @param type
     * @param startPosTiff
     * @throws IOException 
     */
    public IFD(final PosDataInputStream pDIS, int byteOrder, int tag, int type, int startPosTiff) throws IOException {
        super.startPos = pDIS.getPos() - 4;
        super.length = IFD.SIZE;

        this.tiff_StartPos = startPosTiff;
        this.byte_order = byteOrder;

        this.ifd_tag = tag;
        this.ifd_type = type;
        this.ifd_count = this.readInt(pDIS);
        this.ifd_value_offset = this.readInt(pDIS);                                 // Maybe value

        this.data_size = IFDType.getTypeLength(this.ifd_type) * this.ifd_count;
    }

    protected final void checkIFDCount(int countExpected) throws FileFormatException {
        if (this.ifd_count != countExpected) {
            throw new FileFormatException(String.format(
                    "IFD %d (%s): ifd count must be %d. current value = %d",
                    this.ifd_tag,
                    this.getTagName(),
                    countExpected,
                    this.ifd_count));
        }
    }

    protected final PosDataInputStream getTiffOffsetReader() throws IOException {
        final PosDataInputStream reader = new PosDataInputStream(new PosByteArrayInputStream(this.tiff_ByteArray));

        if (this.ifd_value_offset > this.tiff_ByteArray.length) {
            throw new ArrayIndexOutOfBoundsException("TIFF IFD: the data offset is bigger than tiff buffer length");
        }
        if (this.ifd_value_offset + this.data_size > this.tiff_ByteArray.length) {
            throw new ArrayIndexOutOfBoundsException("TIFF IFD: the data ending offset is bigger than tiff buffer length");
        }

        reader.skip(this.ifd_value_offset);
        return reader;
    }

    // Note: Common tag only, none GPS, none InterO
    public String getTagName() {
        String s = null;
        if (this.tag_space == TagSpace.DEFAULT) {
            s = IFDTag.getTagName(this.ifd_tag);
        } else if (this.tag_space == TagSpace.GPS) {
            s = IFDTag.getTagNameGPS(this.ifd_tag);
        } else if (this.tag_space == TagSpace.INTERO) {
            s = IFDTag.getTagNameIntero(this.ifd_tag);
        }

        // Temp
        if (this.getClass().getName().equalsIgnoreCase(IFD.class.getName())) {
            String care = String.format("- CARE ME !!!!! - %s, %04X, %d",
                    this.tag_space.toString(),
                    this.ifd_tag,
                    this.ifd_tag);
            System.out.println(care);
            s = s + care;
        }

        return s;
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
        if (this.byte_order == TIFFHeader.BYTEORDER_BIGENDIAN) {
            return pDIS.readUnsignedInt();
        } else {
            return pDIS.readUnsignedInt_LittleEndian();
        }
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
                String.format("Tag: %04X.H (%d)", this.ifd_tag, this.ifd_tag));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Tag));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 2,
                2,
                String.format("Type: %d - %s", this.ifd_type, IFDType.getTypeName(this.ifd_type)));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Type));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                String.format("Count: %d", this.ifd_count));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Count));
        parentNode.add(new DefaultMutableTreeNode(comp));

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("Value: %X", this.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value));
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("Offset: %d", this.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.tiff_StartPos + this.ifd_value_offset,
                    this.ifd_count * IFDType.getTypeLength(this.ifd_type),
                    "data",
                    ImageLoader.getShortcutIcon());
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }

    protected void generateTreeNode(DefaultMutableTreeNode parentNode, int pos) {

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 0,
                2,
                String.format("Tag: %04X.H (%d)", this.ifd_tag, this.ifd_tag));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Tag));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                pos + 2,
                2,
                String.format("Type: %d - %s", this.ifd_type, IFDType.getTypeName(this.ifd_type)));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Type));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                pos + 4,
                4,
                String.format("Count: %d", this.ifd_count));
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
