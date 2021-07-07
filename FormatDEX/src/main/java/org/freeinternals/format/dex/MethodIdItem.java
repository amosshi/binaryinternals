/*
 * MethodIdItem.java    June 23, 2015, 06:20
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
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S116", "java:S1104"})
public class MethodIdItem extends FileComponent{
    /**
     * Item Size In Bytes.
     */
    public static final int LENGTH = 8;

    /**
     * index into the type_ids list for the definer of this method. This must be
     * a class or array type, and not a primitive type.
     */
    public Dex_ushort class_idx;

    /**
     * index into the proto_ids list for the prototype of this method.
     */
    public Dex_ushort proto_idx;

    /**
     * index into the string_ids list for the name of this method. The string
     * must conform to the syntax for MemberName, defined above.
     */
    public Dex_uint name_idx;

    MethodIdItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_ushort();
        this.proto_idx = stream.Dex_ushort();
        this.name_idx = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }
}
