/*
 * method_handle_item.java    Aug 17, 2021, 19:16
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
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
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class method_handle_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * Item Size In Bytes.
     *
     * @see map_list.TypeCodes#TYPE_METHOD_HANDLE_ITEM
     */
    public static final int ITEM_SIZE = 0x08;

    /**
     * type of the method handle.
     */
    public final Type_ushort method_handle_type;
    /**
     * (unused).
     */
    public final Type_ushort unused_1;
    /**
     * Field or method id depending on whether the method handle type is an
     * accessor or a method invoker.
     */
    public final Type_ushort field_or_method_id;
    /**
     * (unused).
     */
    public final Type_ushort unused_2;

    method_handle_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.method_handle_type = stream.Dex_ushort();
        this.unused_1 = stream.Dex_ushort();
        this.field_or_method_id = stream.Dex_ushort();
        this.unused_2 = stream.Dex_ushort();
        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        int floatPos = this.getStartPos();

        addNode(parentNode, floatPos, Type_ushort.LENGTH, "string_data_off", this.method_handle_type, "msg_method_handle_item__method_handle_type", Icons.Offset);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "unused", "(unused)", "msg_method_handle_item__unused", Icons.Empty);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "field_or_method_id", this.field_or_method_id, "msg_method_handle_item__field_or_method_id", Icons.Index);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "unused", "(unused)", "msg_method_handle_item__unused", Icons.Empty);
    }

    public enum Type {
        METHOD_HANDLE_TYPE_STATIC_PUT(0x00),
        METHOD_HANDLE_TYPE_STATIC_GET(0x01),
        METHOD_HANDLE_TYPE_INSTANCE_PUT(0x02),
        METHOD_HANDLE_TYPE_INSTANCE_GET(0x03),
        METHOD_HANDLE_TYPE_INVOKE_STATIC(0x04),
        METHOD_HANDLE_TYPE_INVOKE_INSTANCE(0x05),
        METHOD_HANDLE_TYPE_INVOKE_CONSTRUCTOR(0x06),
        METHOD_HANDLE_TYPE_INVOKE_DIRECT(0x07),
        METHOD_HANDLE_TYPE_INVOKE_INTERFACE(0x08);

        public final int value;

        private Type(int v) {
            this.value = v;
        }
    }
}
