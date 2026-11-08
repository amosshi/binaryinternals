/*
 * TIFFHeader.java    Sep 07, 2010, 21:45
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.nio.ByteOrder;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

public class TIFFHeader extends FileComponent {

    public static final int SIZE = 8; // Size of TIFF Header
    public static final int BYTEORDER_LITTLEENDIAN = 18761; // "II"
    public static final int BYTEORDER_BIGENDIAN = 19789; // "MM"
    public static final int ARBITRARY_NUMBER_42 = 42;
    /// The byte order used within the file.
    public final int byte_order;
    /// An arbitrary but carefully chosen number (`42`)
    /// that further identifies the file as a `TIFF` file.
    public final int arbitray_number;
    /// The offset (in bytes) of the first IFD.
    public final int offset_0ifd;

    public TIFFHeader(final PosDataInputStream pDisMarker) 
            throws IOException {
        super.startPos = pDisMarker.getPos();
        super.length = SIZE;
        this.byte_order = pDisMarker.readUnsignedShort();
        if (this.byte_order == TIFFHeader.BYTEORDER_BIGENDIAN
                || this.byte_order == TIFFHeader.BYTEORDER_LITTLEENDIAN) {
            final ByteOrder order = toByteOrder(this.byte_order);
            this.arbitray_number = pDisMarker.readUnsignedShort(order);
            this.offset_0ifd = pDisMarker.readInt(order);
        } else {
            throw new FileFormatException("Marker APP01: Un-recognized TIFF header byte order value: expected value is %x or %s, current value is %x.".formatted(TIFFHeader.BYTEORDER_BIGENDIAN, TIFFHeader.BYTEORDER_LITTLEENDIAN, this.byte_order));
        }
    }

    /// Convert the TIFF byte order value to [ByteOrder].
    ///
    /// @param byteOrder [#BYTEORDER_BIGENDIAN] or [#BYTEORDER_LITTLEENDIAN]
    /// @return [ByteOrder#BIG_ENDIAN] for "MM", otherwise [ByteOrder#LITTLE_ENDIAN]
    public static ByteOrder toByteOrder(final int byteOrder) {
        return byteOrder == BYTEORDER_BIGENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public String getByteOrderName() {
        return switch (this.byte_order) {
            case BYTEORDER_LITTLEENDIAN -> "little-endian byte order";
            case BYTEORDER_BIGENDIAN -> "big-endian byte order";
            default -> "Unknown byte order";
        };
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.startPos + 0,
                2,
                "byte order: %X - %s".formatted(this.byte_order, this.getByteOrderName()));
        comp.setDescription("""
                The byte order used within the file. Legal values are:<br />\
                <ul>\
                <li> <strong>II</strong> (4949.H): In the <code>II</code> format, byte order is always from the least significant byte to the most significant byte, for both 16-bit and 32-bit integers This is called <i>little-endian</i> byte order. </li>\
                <li> <strong>MM</strong> (4D4D.H): In the <code>MM</code> format, byte order is always from most significant to least significant, for both 16-bit and 32-bit integers. This is called <i>big-endian</i> byte order.</li>\
                </ul>""");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 2,
                2,
                "arbitrary number: %d".formatted(this.arbitray_number));
        comp.setDescription("""
                An arbitrary but carefully chosen number (<code>42</code>) that further identifies the file as a TIFF file.<br />\
                The byte order depends on the value of <i>byte order</i>.""");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                "first IFD offset: %d".formatted(this.offset_0ifd));
        comp.setDescription("""
                The <code>offset</code> (in bytes) of the first IFD.\s\
                The directory may be at any location in the file after the header but <i>must begin on a word boundary</i>.\s\
                In particular, an Image File Directory may follow the image data it describes.\s\
                Readers must follow the pointers wherever they may lead.\
                <br />\
                The term <strong><i>byte offset</i></strong> is always used in this document to refer to a location with respect to the beginning of the TIFF file.\s\
                The first byte of the file has an offset of <strong>0</strong>."""
                );
        parentNode.add(new DefaultMutableTreeNode(comp));
    }
}
