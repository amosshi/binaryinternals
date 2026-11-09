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

/// A TIFF Image File Directory (IFD) entry: a 12-byte tag, type, count and value or value offset.
///
/// Sub-classes parse the value of specific tags.
public class IFD extends FileComponent {

    /// Size of an IFD entry in bytes.
    public static final int SIZE = 12; // Size of IFD
    /// Start position of the TIFF header in the file; value offsets are relative to it.
    public final int tiff_StartPos;
    /// The TIFF data, from the TIFF header, to read the values at the value offset.
    @SuppressWarnings("ProtectedField")
    protected byte[] tiff_ByteArray;
    /// Byte order of the TIFF data.
    public final int byte_order;
    /// The tag that identifies the field.
    public final int ifd_tag;
    /// The field type, see [IFDType].
    public final int ifd_type;
    /// The number of values of the indicated type.
    public final int ifd_count;
    /// The value offset: the offset of the value from the TIFF header, or the value itself if it fits in 4 bytes.
    @SuppressWarnings("ProtectedField")
    protected int ifd_value_offset;
    /// Total size of the values in bytes.
    public final int data_size;                                                 // Total size of the IFD data
    private TagSpace tag_space = TagSpace.DEFAULT;

    /// Reads an IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param type The field type
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @throws IOException if the entry cannot be read
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

    /// Reads an IFD entry, with its tag and type already read; the value offset is read by the sub-class.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param type The field type
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
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

    /// Total size of the values: count times the size of the type, or 0 for
    /// an unknown type.
    private static int dataSize(final int tag, final int type, final int count) throws FileFormatException {
        final long size = (long) Math.max(IFDType.getTypeLength(type), 0) * Integer.toUnsignedLong(count);
        if (size > Integer.MAX_VALUE) {
            throw new FileFormatException("IFD %04X: the data size %d is too big".formatted(tag, size));
        }
        return (int) size;
    }

    /// Verify the count of the entry.
    ///
    /// @param countExpected The expected count
    /// @throws FileFormatException if the count is not the expected one
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

    /// Gets a reader of the value at the value offset.
    ///
    /// @return The reader, positioned at the value
    /// @throws IOException if the value offset is out of the TIFF data
    protected final PosDataInputStream getTiffOffsetReader() throws IOException {
        return IFDParse.getTiffOffsetReader(this.tiff_ByteArray, this.ifd_value_offset, this.data_size);
    }

    // Note: Common tag only, none GPS, none InterO
    /// Gets the tag name, in the tag space of the entry.
    ///
    /// @return The tag name
    public String getTagName() {
        return this.getTagNameForValidation();
    }

    /// Description of the tag: the `IFD_<tag>_Description` entry of `IFDMessage.properties`, with
    /// the tag in 4 upper case hex digits, like `IFD_A409_Description`.
    ///
    /// Common tag only, none GPS, none InterO.
    ///
    /// @return The description
    protected String getDescription() {
        return IFDMessage.getString(IFDMessage.KEY_FORMAT_IFD_DESCRIPTION.formatted(this.ifd_tag));
    }

    /// Gets the tag space of the entry.
    ///
    /// @return The tag space
    public TagSpace getTagSpace() {
        return this.tag_space;
    }

    /// Reads an unsigned 16-bit value, in the byte order of the TIFF data.
    ///
    /// @param pDIS The input stream
    /// @return The value
    /// @throws IOException if the stream cannot be read
    protected final int readUnsignedShort(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readUnsignedShort(pDIS, this.byte_order);
    }

    /// Reads a 32-bit value, in the byte order of the TIFF data.
    ///
    /// @param pDIS The input stream
    /// @return The value
    /// @throws IOException if the stream cannot be read
    protected final int readInt(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readInt(pDIS, this.byte_order);
    }

    /// Reads an unsigned 32-bit value, in the byte order of the TIFF data.
    ///
    /// @param pDIS The input stream
    /// @return The value
    /// @throws IOException if the stream cannot be read
    protected final long readUnsignedInt(final PosDataInputStream pDIS) throws IOException {
        return IFDParse.readUnsignedInt(pDIS, this.byte_order);
    }

    void setTagSpace(TagSpace ts) {
        this.tag_space = ts;
    }

    /// Whether the value is stored in the value offset field, as its size is at most 4 bytes.
    ///
    /// @return `true` if the value is in the entry
    public boolean isValue() {
        return this.data_size > 0 && this.data_size <= 4;
    }

    /// Add the tree nodes of the entry.
    ///
    /// @param parentNode Parent tree node
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

    /// Add the tree nodes of the tag, type and count fields.
    ///
    /// @param parentNode Parent tree node
    /// @param pos Start position of the entry
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

    /// The tag spaces: which tag definitions apply to an entry.
    @SuppressWarnings("PublicInnerClass")
    public enum TagSpace {

        /// Tags of the TIFF and Exif IFDs.
        DEFAULT,
        /// Tags of the GPS IFD.
        GPS,
        /// Tags of the Interoperability IFD.
        INTERO
    }
}
