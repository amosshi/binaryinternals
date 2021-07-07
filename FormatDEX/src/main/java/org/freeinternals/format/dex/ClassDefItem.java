/*
 * ClassDefItem.java    June 23, 2015, 06:20
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
public class ClassDefItem extends FileComponent {

    /**
     * Item Size In Bytes.
     */
    public static final int LENGTH = 0x20;

    /**
     * index into the type_ids list for this class. This must be a class type,
     * and not an array or primitive type.
     */
    public Dex_uint class_idx;

    /**
     * access flags for the class (public, final, etc.). See "access_flags
     * Definitions" for details.
     */
    public Dex_uint access_flags;

    /**
     * index into the type_ids list for the superclass, or the constant value
     * NO_INDEX if this class has no superclass (i.e., it is a root class such
     * as Object). If present, this must be a class type, and not an array or
     * primitive type.
     */
    public Dex_uint superclass_idx;

    /**
     * offset from the start of the file to the list of interfaces, or 0 if
     * there are none. This offset should be in the data section, and the data
     * there should be in the format specified by "type_list" below. Each of the
     * elements of the list must be a class type (not an array or primitive
     * type), and there must not be any duplicates.
     */
    public Dex_uint interfaces_off;

    /**
     * index into the string_ids list for the name of the file containing the
     * original source for (at least most of) this class, or the special value
     * NO_INDEX to represent a lack of this information. The debug_info_item of
     * any given method may override this source file, but the expectation is
     * that most classes will only come from one source file.
     */
    public Dex_uint source_file_idx;

    /**
     * offset from the start of the file to the annotations structure for this
     * class, or 0 if there are no annotations on this class. This offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "annotations_directory_item" below, with all
     * items referring to this class as the definer.
     */
    public Dex_uint annotations_off;

    /**
     * offset from the start of the file to the associated class data for this
     * item, or 0 if there is no class data for this class. (This may be the
     * case, for example, if this class is a marker interface.) The offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "class_data_item" below, with all items referring
     * to this class as the definer.
     */
    public Dex_uint class_data_off;

    /**
     * offset from the start of the file to the list of initial values for
     * static fields, or 0 if there are none (and all static fields are to be
     * initialized with 0 or null). This offset should be in the data section,
     * and the data there should be in the format specified by
     * "encoded_array_item" below. The size of the array must be no larger than
     * the number of static fields declared by this class, and the elements
     * correspond to the static fields in the same order as declared in the
     * corresponding field_list. The type of each array element must match the
     * declared type of its corresponding field. If there are fewer elements in
     * the array than there are static fields, then the leftover fields are
     * initialized with a type-appropriate 0 or null.
     */
    public Dex_uint static_values_off;

    ClassDefItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_uint();
        this.access_flags = stream.Dex_uint();
        this.superclass_idx = stream.Dex_uint();
        this.interfaces_off = stream.Dex_uint();
        this.source_file_idx = stream.Dex_uint();
        this.annotations_off = stream.Dex_uint();
        this.class_data_off = stream.Dex_uint();
        this.static_values_off = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }
}
