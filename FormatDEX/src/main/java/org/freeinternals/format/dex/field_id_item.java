/*
 * FieldIdItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public class field_id_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * Item Size In Bytes.
     *
     * @see map_list.TypeCodes#TYPE_FIELD_ID_ITEM
     */
    public static final int ITEM_SIZE = 0x08;

    /**
     * index into the type_ids list for the definer of this field. This must be
     * a class type, and not an array or primitive type.
     */
    public Type_ushort class_idx;
    private String clazz = null;
    private String clazz_jls = null;

    /**
     * index into the type_ids list for the type of this field.
     */
    public Type_ushort type_idx;
    private String type = null;
    private String type_jls = null;

    /**
     * index into the string_ids list for the name of this field. The string
     * must conform to the syntax for MemberName, defined above.
     */
    public Type_uint name_idx;
    private String name = null;

    field_id_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_ushort();
        this.type_idx = stream.Dex_ushort();
        this.name_idx = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }

    /**
     * Get {@link #class_idx} name.
     *
     * @param dexFile Current {@link DexFile}
     * @return class name
     * @see #class_idx
     */
    public String get_class(DexFile dexFile) {
        if (this.clazz == null) {
            this.clazz = dexFile.get_type_ids_string(class_idx.value);
        }
        return this.clazz;
    }

    /**
     * Get {@link #class_idx} name in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return class name in JLS format
     * @see #class_idx
     */
    public String get_class_jls(DexFile dexFile) {
        if (this.clazz_jls == null) {
            this.clazz_jls = dexFile.type_ids[class_idx.value].get_descriptor_jls(dexFile).toString();
        }
        return this.clazz_jls;
    }

    /**
     * Get {@link #type_idx} name.
     *
     * @param dexFile Current {@link DexFile}
     * @return type name
     * @see #type_idx
     */
    public String get_type(DexFile dexFile) {
        if (this.type == null) {
            this.type = dexFile.get_type_ids_string(type_idx.value);
        }
        return this.type;
    }

    /**
     * Get {@link #type_idx} name in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return type name in JLS format
     * @see #type_idx
     */
    public String get_type_jls(DexFile dexFile) {
        if (this.type_jls == null) {
            this.type_jls = dexFile.type_ids[type_idx.value].get_descriptor_jls(dexFile).toString();
        }
        return this.type_jls;
    }

    /**
     * Get {@link #name_idx} name.
     *
     * @param dexFile Current {@link DexFile}
     * @return field name
     * @see #name_idx
     */
    public String get_name(DexFile dexFile) {
        if (this.name == null) {
            this.name = dexFile.get_string_ids_string(name_idx.intValue());
        }
        return this.name;
    }

    /**
     * String format of current {@link field_id_item}.
     *
     * @param dexFile Current {@link DexFile}
     * @return String format of a field
     */
    public String toString(DexFile dexFile) {
        return String.format("field %s.%s type %s", this.get_class_jls(dexFile), this.get_name(dexFile), this.get_type_jls(dexFile));
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;

        addNode(parentNode,
                floatPos,
                Type_ushort.LENGTH,
                "class_idx",
                String.format(FORMAT_STRING_STRING, this.class_idx, this.get_class_jls(dexFile)),
                "msg_field_id_item__class_idx",
                UITool.icon4Index());
        floatPos += Type_ushort.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_ushort.LENGTH,
                "type_idx",
                String.format(FORMAT_STRING_STRING, this.type_idx, this.get_type_jls(dexFile)),
                "msg_field_id_item__type_idx",
                UITool.icon4Index());
        floatPos += Type_ushort.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "name_idx",
                String.format(FORMAT_STRING_STRING, this.name_idx, this.get_name(dexFile)),
                "msg_field_id_item__name_idx",
                UITool.icon4Index());
    }
}
