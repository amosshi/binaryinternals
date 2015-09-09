/*
 * FieldIdItem.java    June 23, 2015, 06:20
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
public class FieldIdItem extends FileComponent {

    /**
     * Item Size In Bytes.
     */
    public static final int LENGTH = 8;

    /**
     * index into the type_ids list for the definer of this field. This must be
     * a class type, and not an array or primitive type.
     */
    public Dex_ushort class_idx;
    /**
     * index into the type_ids list for the type of this field.
     */
    public Dex_ushort type_idx;
    /**
     * index into the string_ids list for the name of this field. The string
     * must conform to the syntax for MemberName, defined above.
     */
    public Dex_uint name_idx;

    FieldIdItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_ushort();
        this.type_idx = stream.Dex_ushort();
        this.name_idx = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }
}
