/*
 * XMP.java    Nov 06, 2010, 20:41
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.xmp;

import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;


/**
 *
 * @author Amos Shi
 * @see <a href="http://www.adobemediaplayer.com/devnet/xmp.html">Adobe XMP Developer Center </a>
 */
public class XMP extends FileComponent{

    public final byte[] rawData;
    //public final XMPMeta xmpMeta;

    public XMP(final PosDataInputStream input) throws FileFormatException {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.rawData = input.getBuf();
        StringBuilder sb = new StringBuilder(this.rawData.length + 1);
        for (byte b : this.rawData) {
            sb.append((char) b);
        }

        // TODO - Analysis the data via XMPMeta

//        try {
//            this.xmpMeta = XMPMetaFactory.parse(input);
//        } catch (XMPException ex) {
//            Logger.getLogger(XMP.class.getName()).log(Level.SEVERE, null, ex);
//            throw new JPEGFileFormatException(ex);
//        }

    }
}
