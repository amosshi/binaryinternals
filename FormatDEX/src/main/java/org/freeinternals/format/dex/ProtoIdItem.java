/*
 * ProtoIdItem.java    June 23, 2015, 06:20
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
public class ProtoIdItem extends FileComponent {

    /**
     * Item Size In Bytes.
     */
    public static final int LENGTH = 12;

    /**
     * index into the string_ids list for the short-form descriptor string of
     * this prototype. The string must conform to the syntax for
     * ShortyDescriptor, defined above, and must correspond to the return type
     * and parameters of this item.
     */
    public Dex_uint shorty_idx;

    /**
     * index into the type_ids list for the return type of this prototype.
     */
    public Dex_uint return_type_idx;

    /**
     * offset from the start of the file to the list of parameter types for this
     * prototype, or 0 if this prototype has no parameters. This offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "type_list" below. Additionally, there should be
     * no reference to the type void in the list.
     */
    public final Dex_uint parameters_off;

    ProtoIdItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.shorty_idx = stream.Dex_uint();
        this.return_type_idx = stream.Dex_uint();
        this.parameters_off = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }
}
