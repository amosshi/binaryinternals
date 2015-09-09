/*
 * StringIdItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;

/**
 *
 * @author Amos Shi
 */
public class StringIdItem extends FileComponent {

    /**
     * offset from the start of the file to the string data for this item. The
     * offset should be to a location in the data section, and the data should
     * be in the format specified by "string_data_item" below. There is no
     * alignment requirement for the offset.
     */
    public Dex_uint string_data_off;

    StringIdItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.string_data_off = stream.Dex_uint();
        super.length = Dex_uint.LENGTH;
    }
}
