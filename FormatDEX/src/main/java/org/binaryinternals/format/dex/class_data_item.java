/*
 * class_data_item.java    June 23, 2015, 06:20
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
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class class_data_item extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_uleb128 static_fields_size;
    public final Type_uleb128 instance_fields_size;
    public final Type_uleb128 direct_methods_size;
    public final Type_uleb128 virtual_methods_size;

    public final encoded_field[] static_fields;
    public final encoded_field[] instance_fields;
    public final encoded_method[] direct_methods;
    public final encoded_method[] virtual_methods;

    /**
     * <pre>
     * java:S3776 - Cognitive Complexity of methods should not be too high - We need this logic together
     * </pre>
     */
    @SuppressWarnings("java:S3776")
    class_data_item(PosDataInputStreamDex stream, DexFile dexFile) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.static_fields_size = stream.Dex_uleb128();
        this.instance_fields_size = stream.Dex_uleb128();
        this.direct_methods_size = stream.Dex_uleb128();
        this.virtual_methods_size = stream.Dex_uleb128();

        if (this.static_fields_size.value > 0) {
            this.static_fields = new encoded_field[this.static_fields_size.value];
            for (int i = 0; i < this.static_fields_size.value; i++) {
                this.static_fields[i] = new encoded_field(stream);
            }
        } else {
            this.static_fields = null;
        }

        if (this.instance_fields_size.value > 0) {
            this.instance_fields = new encoded_field[this.instance_fields_size.value];
            for (int i = 0; i < this.instance_fields_size.value; i++) {
                this.instance_fields[i] = new encoded_field(stream);
            }
        } else {
            this.instance_fields = null;
        }

        if (this.direct_methods_size.value > 0) {
            this.direct_methods = new encoded_method[this.direct_methods_size.value];
            for (int i = 0; i < this.direct_methods_size.value; i++) {
                this.direct_methods[i] = new encoded_method(stream, dexFile);
            }
        } else {
            this.direct_methods = null;
        }

        if (this.virtual_methods_size.value > 0) {
            this.virtual_methods = new encoded_method[this.virtual_methods_size.value];
            for (int i = 0; i < this.virtual_methods_size.value; i++) {
                this.virtual_methods[i] = new encoded_method(stream, dexFile);
            }
        } else {
            this.virtual_methods = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    @SuppressWarnings("java:S3776")
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dex = (DexFile)format;
        int floatPos = super.startPos;

        addNode(parentNode, floatPos, this.static_fields_size.length, "static_fields_size", this.static_fields_size, "msg_class_data_item__static_fields_size", Icons.Size);
        floatPos += this.static_fields_size.length;
        addNode(parentNode, floatPos, this.instance_fields_size.length, "instance_fields_size", this.instance_fields_size, "msg_class_data_item__instance_fields_size", Icons.Size);
        floatPos += this.instance_fields_size.length;
        addNode(parentNode, floatPos, this.direct_methods_size.length, "direct_methods_size", this.direct_methods_size, "msg_class_data_item__direct_methods_size", Icons.Size);
        floatPos += this.direct_methods_size.length;
        addNode(parentNode, floatPos, this.virtual_methods_size.length, "virtual_methods_size", this.virtual_methods_size, "msg_class_data_item__virtual_methods_size", Icons.Size);

        if (this.static_fields != null) {
            int fieldLenSum = 0;
            for (encoded_field field : this.static_fields) {
                fieldLenSum += field.getLength();
            }

            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.static_fields[0].getStartPos(),
                    fieldLenSum,
                    String.format("static_fields [%,d]", this.static_fields.length),
                    Icons.Field,
                    MESSAGES.getString("msg_class_data_item__static_fields")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < this.static_fields.length; i++) {
                encoded_field item = this.static_fields[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("static_fields [%,d]", i),
                        Icons.Field,
                        encoded_field.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dex);
            }
        }

        if (this.instance_fields != null) {
            int fieldLenSum = 0;
            for (encoded_field field : this.instance_fields) {
                fieldLenSum += field.getLength();
            }

            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.instance_fields[0].getStartPos(),
                    fieldLenSum,
                    String.format("instance_fields [%,d]", this.instance_fields.length),
                    Icons.Field,
                    MESSAGES.getString("msg_class_data_item__instance_fields")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < this.instance_fields.length; i++) {
                encoded_field item = this.instance_fields[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("encoded_field [%,d]", i),
                        Icons.Field,
                        encoded_field.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dex);
            }
        }

        if (this.direct_methods != null) {
            int methodLenSum = 0;
            for (encoded_method method : this.direct_methods) {
                methodLenSum += method.getLength();
            }

            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.direct_methods[0].getStartPos(),
                    methodLenSum,
                    String.format("direct_methods [%,d]", this.direct_methods.length),
                    Icons.Method,
                    MESSAGES.getString("msg_class_data_item__direct_methods")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < this.direct_methods.length; i++) {
                encoded_method item = this.direct_methods[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("encoded_method [%,d]", i),
                        Icons.Method,
                        encoded_method.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dex);
            }
        }

        if (this.virtual_methods != null) {
            int methodLenSum = 0;
            for (encoded_method method : this.virtual_methods) {
                methodLenSum += method.getLength();
            }

            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.virtual_methods[0].getStartPos(),
                    methodLenSum,
                    String.format("virtual_methods [%,d]", this.virtual_methods.length),
                    Icons.Method,
                    MESSAGES.getString("msg_class_data_item__virtual_methods")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < this.virtual_methods.length; i++) {
                encoded_method item = this.virtual_methods[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("encoded_method [%,d]", i),
                        Icons.Method,
                        encoded_method.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dex);
            }
        }

    }

    public static class encoded_field extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uleb128 field_idx_diff;
        public final Type_uleb128 access_flags;

        encoded_field(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.field_idx_diff = stream.Dex_uleb128();
            this.access_flags = stream.Dex_uleb128();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile)format;
            int floatPos = super.startPos;

            field_id_item field = dexFile.field_ids[this.field_idx_diff.value];
            addNode(parentNode,
                    floatPos,
                    this.field_idx_diff.length,
                    "field_idx_diff",
                    String.format(FORMAT_STRING_STRING, this.field_idx_diff, (field == null) ? "null (should not happen)" : field.toString(dexFile)),
                    "msg_encoded_field__field_idx_diff",
                    Icons.Index);
            floatPos += this.field_idx_diff.length;

            addNode(parentNode,
                    floatPos,
                    access_flags.length,
                    "access_flags",
                    this.access_flags.toString() + " - " + BytesTool.getBinaryString(this.access_flags.value) + access_flag.getFieldModifier(this.access_flags.value),
                    "msg_class_def_item__access_flags",
                    Icons.AccessFlag
            );
        }
    }

    public static class encoded_method extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uleb128 method_idx_diff;
        public final Type_uleb128 access_flags;
        public final Type_uleb128 code_off;

        encoded_method(PosDataInputStreamDex stream, DexFile dexFile) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.method_idx_diff = stream.Dex_uleb128();
            this.access_flags = stream.Dex_uleb128();
            this.code_off = stream.Dex_uleb128();
            if (this.code_off.value != 0) {
                dexFile.parseData(Long.valueOf(this.code_off.value), code_item.class, stream);
            }
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile)format;
            int floatPos = super.startPos;

            method_id_item method = dexFile.method_ids[this.method_idx_diff.value];
            addNode(parentNode,
                    floatPos,
                    this.method_idx_diff.length,
                    "method_idx_diff",
                    String.format(FORMAT_STRING_STRING, this.method_idx_diff, (method == null) ? "null (should not happen)" : method.toString(dexFile)),
                    "msg_encoded_method__method_idx_diff",
                    Icons.Index);
            floatPos += this.method_idx_diff.length;

            addNode(parentNode,
                    floatPos,
                    this.access_flags.length,
                    "access_flags",
                    this.access_flags.toString() + " - " + BytesTool.getBinaryString(this.access_flags.value) + access_flag.getMethodModifier(this.access_flags.value),
                    "msg_encoded_method__access_flags",
                    Icons.AccessFlag
            );

            DefaultMutableTreeNode codeoffNode = addNode(parentNode,
                    floatPos,
                    this.code_off.length,
                    "code_off",
                    this.code_off,
                    "msg_encoded_method__code_off",
                    Icons.Offset);
            if (this.code_off.value != 0) {
                code_item item = (code_item) dexFile.data.get(Long.valueOf(this.code_off.value));
                DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        code_item.class.getSimpleName(),
                        Icons.Shortcut,
                        MESSAGES.getString("msg_code_item")
                ));
                codeoffNode.add(itemNode);
                item.generateTreeNode(itemNode, dexFile);
            }
        }
    }
}
