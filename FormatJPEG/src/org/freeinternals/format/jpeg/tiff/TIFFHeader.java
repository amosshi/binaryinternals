/*
 * TIFFHeader.java    Sep 07, 2010, 21:45
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 */
public class TIFFHeader extends FileComponent {

    public static final int SIZE = 8; // Size of TIFF Header
    public static final int BYTEORDER_LITTLEENDIAN = 18761; // "II"
    public static final int BYTEORDER_BIGENDIAN = 19789; // "MM"
    public static final int ARBITRARY_NUMBER_42 = 42;
    /** The byte order used within the file. */
    public final int byte_order;
    /**
     * An arbitrary but carefully chosen number (<code>42</code>)
     * that further identifies the file as a <code>TIFF</code> file.
     */
    public final int arbitray_number;
    /** The offset (in bytes) of the first IFD. */
    public final int offset_0ifd;

    public TIFFHeader(final PosDataInputStream pDisMarker) 
            throws IOException, FileFormatException {
        super.startPos = pDisMarker.getPos();
        super.length = SIZE;
        this.byte_order = pDisMarker.readUnsignedShort();
        if (this.byte_order == TIFFHeader.BYTEORDER_BIGENDIAN) {
            this.arbitray_number = pDisMarker.readUnsignedShort();
            this.offset_0ifd = pDisMarker.readInt();
        } else if (this.byte_order == TIFFHeader.BYTEORDER_LITTLEENDIAN) {
            this.arbitray_number = pDisMarker.readUnsignedShort_LittleEndian();
            this.offset_0ifd = pDisMarker.readInt_LittleEndian();
        } else {
            throw new FileFormatException(String.format("Marker APP01: Un-recognized TIFF header byte order value: expected value is %x or %s, current value is %x.", TIFFHeader.BYTEORDER_BIGENDIAN, TIFFHeader.BYTEORDER_LITTLEENDIAN, this.byte_order));
        }
    }

    public String getByteOrderName() {
        switch (this.byte_order) {
            case BYTEORDER_LITTLEENDIAN:
                return "little-endian byte order";
            case BYTEORDER_BIGENDIAN:
                return "big-endian byte order";
            default:
                return "Unknown byte order";
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.startPos + 0,
                2,
                String.format("byte order: %X - %s", this.byte_order, this.getByteOrderName()));
        comp.setDescription("The byte order used within the file. Legal values are:<br />"
                + "<ul>"
                + "<li> <strong>II</strong> (4949.H): In the <code>II</code> format, byte order is always from the least significant byte to the most significant byte, for both 16-bit and 32-bit integers This is called <i>little-endian</i> byte order. </li>"
                + "<li> <strong>MM</strong> (4D4D.H): In the <code>MM</code> format, byte order is always from most significant to least significant, for both 16-bit and 32-bit integers. This is called <i>big-endian</i> byte order.</li>"
                + "</ul>");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 2,
                2,
                String.format("arbitrary number: %d", this.arbitray_number));
        comp.setDescription("An arbitrary but carefully chosen number (<code>42</code>) that further identifies the file as a TIFF file.<br />"
                + "The byte order depends on the value of <i>byte order</i>.");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                String.format("first IFD offset: %d", this.offset_0ifd));
        comp.setDescription("The <code>offset</code> (in bytes) of the first IFD. "
                + "The directory may be at any location in the file after the header but <i>must begin on a word boundary</i>. "
                + "In particular, an Image File Directory may follow the image data it describes. "
                + "Readers must follow the pointers wherever they may lead."
                + "<br />"
                + "The term <strong><i>byte offset</i></strong> is always used in this document to refer to a location with respect to the beginning of the TIFF file. "
                + "The first byte of the file has an offset of <strong>0</strong>."
                );
        parentNode.add(new DefaultMutableTreeNode(comp));
    }
}
