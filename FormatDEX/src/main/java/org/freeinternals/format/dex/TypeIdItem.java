/*
 * TypeIdItem.java    June 23, 2015, 06:20
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
public class TypeIdItem extends FileComponent {

    /**
     * index into the string_ids list for the descriptor string of this type.
     * The string must conform to the syntax for TypeDescriptor, defined above.
     */
    public Dex_uint descriptor_idx;

    TypeIdItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.descriptor_idx = stream.Dex_uint();
        super.length = Dex_uint.LENGTH;
    }
}
