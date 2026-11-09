/*
 * IFD_0002_InteroperabilityVersion.java    Nov 06, 2010, 15:24
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Interoperability tag `0x0002`, `InteroperabilityVersion`.
///
/// @author Amos Shi
public class IFD_0002_InteroperabilityVersion extends IFD_UNDEFINED {

    /// The expected number of values.
    public static final int COUNT = 4;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0002_InteroperabilityVersion(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    /// Gets the Interoperability version, like `0100`.
    ///
    /// @return The version
    public String getVersion() {
        return new String(super.value, 0, COUNT, StandardCharsets.ISO_8859_1);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                "%s: %s".formatted(this.getTagName(), this.getVersion()),
                null);
    }
}
