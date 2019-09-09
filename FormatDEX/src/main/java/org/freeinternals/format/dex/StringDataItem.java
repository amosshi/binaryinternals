/*
 * StringDataItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class StringDataItem extends FileComponent {

    /**
     * size of this string, in UTF-16 code units (which is the "string length"
     * in many systems). That is, this is the decoded length of the string. (The
     * encoded length is implied by the position of the 0 byte.)
     */
    public Dex_uleb128 utf16_size;

    /**
     * a series of MUTF-8 code units (a.k.a. octets, a.k.a. bytes) followed by a
     * byte of value 0. See "MUTF-8 (Modified UTF-8) Encoding" above for details
     * and discussion about the data format.
     */
    public byte[] data;

    StringDataItem(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.utf16_size = stream.Dex_uleb128();
        if (this.utf16_size.value > 0) {
            this.data = new byte[this.utf16_size.value];
            int bytesRead = stream.read(this.data);
            if (bytesRead != this.data.length) {
                throw new IOException("Cannot read correct number of bytes for string_data_item. expected bytes = "
                        + this.data.length + ", atual bytes read = " + bytesRead);
            }
        } else {
            this.data = null;
        }
        super.length = stream.getPos() - super.startPos;
    }

    /**
     * Get the {@link #data} as a String, using platform's default charset.
     *
     * @return String for the content
     */
    public String getString() {
        return new String(this.data);
    }
}
