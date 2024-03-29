/*
 * class_def_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

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
public class class_def_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * Item Size In Bytes.
     *
     * @see map_list.TypeCodes#TYPE_CLASS_DEF_ITEM
     */
    public static final int ITEM_SIZE = 0x20;

    /**
     * index into the type_ids list for this class. This must be a class type,
     * and not an array or primitive type.
     */
    public final Type_uint class_idx;
    private String clazz_jls;

    /**
     * access flags for the class (public, final, etc.). See "access_flags
     * Definitions" for details.
     */
    public final Type_uint access_flags;

    /**
     * index into the type_ids list for the superclass, or the constant value
     * NO_INDEX if this class has no superclass (i.e., it is a root class such
     * as Object). If present, this must be a class type, and not an array or
     * primitive type.
     */
    public final Type_uint superclass_idx;
    private String superclass_jls = null;

    /**
     * offset from the start of the file to the list of interfaces, or 0 if
     * there are none. This offset should be in the data section, and the data
     * there should be in the format specified by "type_list" below. Each of the
     * elements of the list must be a class type (not an array or primitive
     * type), and there must not be any duplicates.
     */
    public final Type_uint interfaces_off;
    private type_list interfaces = null;

    /**
     * index into the string_ids list for the name of the file containing the
     * original source for (at least most of) this class, or the special value
     * NO_INDEX to represent a lack of this information. The debug_info_item of
     * any given method may override this source file, but the expectation is
     * that most classes will only come from one source file.
     */
    public final Type_uint source_file_idx;
    private String source_file = null;

    /**
     * offset from the start of the file to the annotations structure for this
     * class, or 0 if there are no annotations on this class. This offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "annotations_directory_item" below, with all
     * items referring to this class as the definer.
     */
    public final Type_uint annotations_off;
    private annotations_directory_item annotations = null;

    /**
     * offset from the start of the file to the associated class data for this
     * item, or 0 if there is no class data for this class. (This may be the
     * case, for example, if this class is a marker interface.) The offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "class_data_item" below, with all items referring
     * to this class as the definer.
     */
    public final Type_uint class_data_off;

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
    public final Type_uint static_values_off;

    class_def_item(final PosDataInputStreamDex stream, final DexFile dex) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.class_idx = stream.Dex_uint();
        this.access_flags = stream.Dex_uint();
        this.superclass_idx = stream.Dex_uint();
        this.interfaces_off = stream.Dex_uint();
        this.source_file_idx = stream.Dex_uint();
        this.annotations_off = stream.Dex_uint();
        this.class_data_off = stream.Dex_uint();
        if (this.class_data_off.value != 0) {
            dex.parseData(this.class_data_off.value, class_data_item.class, stream);
        }

        this.static_values_off = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
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
            this.clazz_jls = dexFile.type_ids[this.class_idx.intValue()].get_descriptor_jls(dexFile).toString();
        }
        return this.clazz_jls;
    }

    /**
     * Get {@link #superclass_idx} name in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return super class name in JLS format
     * @see #superclass_idx
     */
    public String get_superclass_jls(DexFile dexFile) {
        if (this.superclass_jls == null) {
            this.superclass_jls = (this.superclass_idx.value == DexFile.NO_INDEX)
                    ? ""
                    : dexFile.type_ids[this.superclass_idx.intValue()].get_descriptor_jls(dexFile).toString();
        }
        return this.superclass_jls;
    }

    /**
     * Get {@link #interfaces_off} value.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link type_list} value if {@link #interfaces_off} is not
     * <code>0</code>, or else <code>null</code>
     * @see #interfaces_off
     */
    public type_list get_interfaces(DexFile dexFile) {
        if (this.interfaces_off.value == 0) {
            return null;
        }

        if (this.interfaces == null) {
            this.interfaces = (type_list) dexFile.data.get(this.interfaces_off.value);
        }
        return this.interfaces;
    }

    public String get_interfaces_desc(DexFile dexFile) {
        type_list types = this.get_interfaces(dexFile);
        return (types == null) ? "(no interface)" : types.toString(dexFile);
    }

    /**
     * Get {@link #annotations_off} value.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link annotations_directory_item} value if
     * {@link #annotations_off} is not <code>0</code>, or else <code>null</code>
     * @see #annotations_off
     */
    public annotations_directory_item get_annotations(DexFile dexFile) {
        if (this.annotations_off.value == 0) {
            return null;
        }

        if (this.annotations == null) {
            this.annotations = (annotations_directory_item) dexFile.data.get(this.annotations_off.value);
        }
        return this.annotations;
    }

    /**
     * Get {@link #source_file_idx} value.
     *
     * @param dexFile Current {@link DexFile}
     * @return source file
     * @see #source_file_idx
     */
    public String get_source_file(DexFile dexFile) {
        if (this.source_file == null) {
            this.source_file = (this.source_file_idx.value == 0 || this.source_file_idx.value == DexFile.NO_INDEX)
                    ? "(lack of information)"
                    : dexFile.get_string_ids_string(this.source_file_idx.intValue());
        }
        return this.source_file;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile)format;
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "class_idx",
                String.format(FORMAT_STRING_STRING, this.class_idx, this.get_class_jls(dexFile)),
                "msg_class_def_item__class_idx",
                Icons.Index);
        floatPos += Type_uint.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "access_flags",
                this.access_flags.toString() + " - " + BytesTool.getBinaryString(this.access_flags.value) + access_flag.getClassModifier(this.access_flags.intValue()),
                "msg_class_def_item__access_flags",
                Icons.AccessFlag
        );
        floatPos += Type_uint.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "superclass_idx",
                String.format(FORMAT_STRING_STRING, this.superclass_idx, this.get_superclass_jls(dexFile)),
                "msg_class_def_item__superclass_idx",
                Icons.Index);
        floatPos += Type_uint.LENGTH;

        DefaultMutableTreeNode ifsOffsetNode = addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "interfaces_off",
                String.format(FORMAT_STRING_STRING, this.interfaces_off, this.get_interfaces_desc(dexFile)),
                "msg_class_def_item__interfaces_off",
                Icons.Offset);
        floatPos += Type_uint.LENGTH;

        type_list ifs = this.get_interfaces(dexFile);
        if (ifs != null) {
            DefaultMutableTreeNode ifsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    ifs.getStartPos(),
                    ifs.getLength(),
                    type_list.class.getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_class_def_item__interfaces_off")
            ));
            ifsOffsetNode.add(ifsNode);
            ifs.generateTreeNode(ifsNode, dexFile);
        }

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "source_file_idx",
                String.format(FORMAT_STRING_STRING, this.source_file_idx, this.get_source_file(dexFile)),
                "msg_class_def_item__source_file_idx",
                Icons.Index);
        floatPos += Type_uint.LENGTH;

        DefaultMutableTreeNode ansOffsetNode = addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "annotations_off",
                this.annotations_off,
                "msg_class_def_item__annotations_off",
                Icons.Offset);
        floatPos += Type_uint.LENGTH;

        annotations_directory_item ans = this.get_annotations(dexFile);
        if (ans != null) {
            DefaultMutableTreeNode ansNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    ans.getStartPos(),
                    ans.getLength(),
                    annotations_directory_item.class.getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_annotations_directory_item")
            ));
            ansOffsetNode.add(ansNode);
            ans.generateTreeNode(ansNode, dexFile);
        }

        DefaultMutableTreeNode offNode = addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "class_data_off",
                this.class_data_off,
                "msg_class_def_item__class_data_off",
                Icons.Offset);
        floatPos += Type_uint.LENGTH;
        if (this.class_data_off.value != 0) {
            class_data_item item = (class_data_item) dexFile.data.get(this.class_data_off.value);
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    item.getClass().getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_class_data_item")
            ));
            offNode.add(itemNode);
            item.generateTreeNode(itemNode, dexFile);
        }

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "static_values_off",
                this.static_values_off,
                "msg_class_def_item__static_values_off",
                Icons.Offset);

    }
}
