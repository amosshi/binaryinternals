/*
 * XMP.java    Nov 06, 2010, 20:41
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.xmp;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Extensible Metadata Platform (XMP) packet, an XML document in UTF-8.
///
/// @author Amos Shi
public final class XMP extends FileComponent {

    private static final Logger LOG = Logger.getLogger(XMP.class.getName());
    public final byte[] rawData;
    /// The XMP packet as text.
    private final String xmpmeta;

    public XMP(final PosDataInputStream input) {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.rawData = input.getBuf();
        this.xmpmeta = new String(this.rawData, StandardCharsets.UTF_8);
        LOG.log(Level.FINE, this.xmpmeta);
    }

    /// The XMP packet as text.
    ///
    /// @return The XML text
    public String getXmpMeta() {
        return this.xmpmeta;
    }
}
