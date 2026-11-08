/*
 * IFD_LONG_Pointer.java    Oct 31, 2010, 11:55
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An IFD entry whose value is the offset of a sub IFD, like the Exif, GPS and
/// Interoperability IFDs.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_G
public class IFD_LONG_Pointer extends IFD_LONG_COUNT1 {

    /// Max nesting of sub IFDs, a sub IFD pointing back to its parent would
    /// recurse for ever.
    private static final int DEPTH_MAX = 8;
    /// Nesting of the sub IFDs being parsed, bound while parsing a sub IFD.
    private static final ScopedValue<Integer> DEPTH = ScopedValue.newInstance();

    public final int ifd_number;
    final IFD[] ifd_sub;
    int next;

    public IFD_LONG_Pointer(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        // Sub IFD
        final PosDataInputStream reader = this.getSubIFDReader();
        this.ifd_number = IFDParse.readUnsignedShort(reader, byteOrder);
        this.ifd_sub = (this.ifd_number > 0) ? new IFD[this.ifd_number] : null;
    }

    /// A reader positioned at the sub IFD.
    private PosDataInputStream getSubIFDReader() throws IOException {
        final long offset = super.value[0];
        if (offset + 2 > this.tiff_ByteArray.length) {
            throw new FileFormatException("TIFF IFD %04X: the sub IFD offset %d is beyond the TIFF data length %d".formatted(
                    this.ifd_tag, offset, this.tiff_ByteArray.length));
        }
        final PosDataInputStream reader = new PosDataInputStream(
                new PosByteArrayInputStream(this.tiff_ByteArray),
                super.tiff_StartPos);
        reader.skipNBytes(offset);
        return reader;
    }

    /// Parse the entries of the sub IFD, and the offset of the next IFD.
    ///
    /// @param parser Parser of one entry
    /// @param tagSpace Tag space of the entries
    /// @throws IOException Read failed, or the sub IFDs are nested too deep
    protected final void parseSubIFDs(final EntryParser parser, final TagSpace tagSpace) throws IOException {
        final int depth = DEPTH.orElse(0);
        if (depth >= DEPTH_MAX) {
            throw new FileFormatException("TIFF IFD %04X: sub IFDs are nested more than %d levels".formatted(this.ifd_tag, DEPTH_MAX));
        }

        // Unbound again when the call returns, also on failure
        ScopedValue.where(DEPTH, depth + 1).call(() -> {
            final PosDataInputStream reader = this.getSubIFDReader();
            reader.skipNBytes(2);       // The entry count, read already
            for (int i = 0; i < this.ifd_number; i++) {
                this.ifd_sub[i] = parser.parse(reader, this.byte_order, this.tiff_StartPos, this.tiff_ByteArray);
                this.ifd_sub[i].setTagSpace(tagSpace);
            }
            this.next = IFDParse.readInt(reader, this.byte_order);
            return null;
        });
    }

    protected void generateTreeNode_NextIFD(DefaultMutableTreeNode parentNode) {
        if (this.ifd_sub != null) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.ifd_sub[this.ifd_sub.length - 1].getStartPos() + IFD.SIZE,
                    4,
                    "Next IFD: Offset within Tiff = %04X (%d)".formatted(this.next, this.next))));
        }
    }

    /// Parser of one IFD entry.
    @FunctionalInterface
    protected interface EntryParser {

        IFD parse(PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) throws IOException;
    }
}
