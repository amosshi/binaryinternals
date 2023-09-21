/*
 * map_list.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.format.dex.annotation_set_item.annotation_item;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class map_list extends FileComponent implements GenerateTreeNode {

    public final Type_uint size;
    public final map_item[] list;

    map_list(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();

        this.size = stream.Dex_uint();
        DexFile.check_uint("map_list.size", this.size, stream.getPos());

        if (this.size.value > 0) {
            this.list = new map_item[(int) this.size.value];
            for (int i = 0; i < this.size.value; i++) {
                this.list[i] = new map_item(stream);
            }
        } else {
            this.list = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class map_item extends FileComponent implements GenerateTreeNode {

        /**
         * Item Size In Bytes.
         *
         * @see map_list.TypeCodes#TYPE_MAP_LIST
         */
        public static final int ITEM_SIZE = 12;

        public final Type_ushort type;
        public final Type_ushort unused;
        public final Type_uint size;
        public final Type_uint offset;

        map_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.type = stream.Dex_ushort();
            this.unused = stream.Dex_ushort();
            this.size = stream.Dex_uint();
            this.offset = stream.Dex_uint();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public enum TypeCodes {
        TYPE_HEADER_ITEM(header_item.class, 0x0000),
        TYPE_STRING_ID_ITEM(string_id_item.class, 0x0001),
        TYPE_TYPE_ID_ITEM(type_id_item.class, 0x0002),
        TYPE_PROTO_ID_ITEM(proto_id_item.class, 0x0003),
        TYPE_FIELD_ID_ITEM(field_id_item.class, 0x0004),
        TYPE_METHOD_ID_ITEM(method_id_item.class, 0x0005),
        TYPE_CLASS_DEF_ITEM(class_def_item.class, 0x0006),
        TYPE_CALL_SITE_ID_ITEM(null, 0x0007),
        TYPE_METHOD_HANDLE_ITEM(method_handle_item.class, 0x0008),
        TYPE_MAP_LIST(map_list.class, 0x1000),
        TYPE_TYPE_LIST(type_list.class, 0x1001),
        TYPE_ANNOTATION_SET_REF_LIST(annotation_set_ref_list.class, 0x1002),
        TYPE_ANNOTATION_SET_ITEM(annotation_set_item.class, 0x1003),
        TYPE_CLASS_DATA_ITEM(class_data_item.class, 0x2000),
        TYPE_CODE_ITEM(code_item.class, 0x2001),
        TYPE_STRING_DATA_ITEM(string_data_item.class, 0x2002),
        TYPE_DEBUG_INFO_ITEM(debug_info_item.class, 0x2003),
        TYPE_ANNOTATION_ITEM(annotation_item.class, 0x2004),
        TYPE_ENCODED_ARRAY_ITEM(encoded_array_item.class, 0x2005),
        TYPE_ANNOTATIONS_DIRECTORY_ITEM(annotations_directory_item.class, 0x2006),
        TYPE_HIDDENAPI_CLASS_DATA_ITEM(null, 0xF000);

        public final Type_ushort value;
        final Class<?> item_type;

        private TypeCodes(Class<?> clazz, int value) {
            this.value = new Type_ushort(value);
            this.item_type = clazz;
        }

    }

}
