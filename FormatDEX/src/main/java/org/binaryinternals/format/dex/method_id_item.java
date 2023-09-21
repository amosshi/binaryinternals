/*
 * method_id_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.ui.Icons;

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
public class method_id_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * Item Size In Bytes.
     *
     * @see map_list.TypeCodes#TYPE_METHOD_ID_ITEM
     */
    public static final int ITEM_SIZE = 0x08;

    /**
     * index into the type_ids list for the definer of this method. This must be
     * a class or array type, and not a primitive type.
     */
    public final Type_ushort class_idx;
    private String clazz = null;
    private String clazz_jls = null;

    /**
     * index into the proto_ids list for the prototype of this method.
     */
    public final Type_ushort proto_idx;

    /**
     * index into the string_ids list for the name of this method. The string
     * must conform to the syntax for MemberName, defined above.
     */
    public final Type_uint name_idx;
    private String name = null;

    method_id_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_ushort();
        this.proto_idx = stream.Dex_ushort();
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
            // The class string could be an array, so we use the TypeJLSName
            this.clazz_jls = dexFile.type_ids[class_idx.value].get_descriptor_jls(dexFile).TypeJLSName;
        }
        return this.clazz_jls;
    }

    /**
     * Get {@link #proto_idx} value.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link #proto_idx} value
     */
    public proto_id_item get_proto(DexFile dexFile) {
        return dexFile.proto_ids[this.proto_idx.value];
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
     * String format of current {@link method_id_item}.
     *
     * @param dexFile Current {@link DexFile}
     * @return String format of a method
     */
    public String toString(DexFile dexFile) {
        proto_id_item proto = this.get_proto(dexFile);
        type_list params = proto.get_parameters(dexFile);
        return String.format("%s %s.%s(%s)",
                proto.get_return_type_jls(dexFile),
                this.get_class_jls(dexFile),
                this.get_name(dexFile),
                (params == null) ? "" : params.toString(dexFile)
        );
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile)format;
        int floatPos = super.startPos;

        addNode(parentNode,
                floatPos,
                Type_ushort.LENGTH,
                "class_idx",
                String.format(FORMAT_STRING_STRING, this.class_idx, this.get_class_jls(dexFile)),
                "msg_method_id_item__class_idx",
                Icons.Index);
        floatPos += Type_ushort.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_ushort.LENGTH,
                "proto_idx",
                this.get_proto(dexFile).toString(dexFile),
                "msg_method_id_item__proto_idx",
                Icons.Index);
        floatPos += Type_ushort.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "name_idx",
                String.format(FORMAT_STRING_STRING, this.name_idx, this.get_name(dexFile)),
                "msg_method_id_item__name_idx",
                Icons.Index);
    }
}
