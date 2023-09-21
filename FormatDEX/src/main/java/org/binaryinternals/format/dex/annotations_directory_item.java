/*
 * annotations_directory_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
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
public class annotations_directory_item extends FileComponent implements GenerateTreeNodeDexFile {

    private static final String MSG_ANNOTATIONS_OFF = "annotations_off";

    public final Type_uint class_annotations_off;
    public final Type_uint fields_size;
    public final Type_uint annotated_methods_size;
    public final Type_uint annotated_parameters_size;
    public final field_annotation[] field_annotations;
    public final method_annotation[] method_annotations;
    public final parameter_annotation[] parameter_annotations;

    annotations_directory_item(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
        super.startPos = stream.getPos();

        this.class_annotations_off = stream.Dex_uint();
        if ((this.class_annotations_off.value != 0) && (!dex.data.keySet().contains(this.class_annotations_off.value))) {
            dex.parseData(this.class_annotations_off.value, annotation_set_item.class, stream);
        }

        this.fields_size = stream.Dex_uint();
        this.annotated_methods_size = stream.Dex_uint();
        this.annotated_parameters_size = stream.Dex_uint();

        // field_annotations
        if (this.fields_size.value > 0) {
            this.field_annotations = new field_annotation[this.fields_size.intValue()];
            for (int i = 0; i < this.fields_size.value; i++) {
                this.field_annotations[i] = new field_annotation(stream, dex);
            }
        } else {
            this.field_annotations = null;
        }

        // method_annotations
        if (this.annotated_methods_size.value > 0) {
            this.method_annotations = new method_annotation[this.annotated_methods_size.intValue()];
            for (int i = 0; i < this.annotated_methods_size.value; i++) {
                this.method_annotations[i] = new method_annotation(stream, dex);
            }
        } else {
            this.method_annotations = null;
        }

        // parameter_annotations
        if (this.annotated_parameters_size.value > 0) {
            this.parameter_annotations = new parameter_annotation[this.annotated_parameters_size.intValue()];
            for (int i = 0; i < this.annotated_parameters_size.value; i++) {
                this.parameter_annotations[i] = new parameter_annotation(stream, dex);
            }
        } else {
            this.parameter_annotations = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile)format;
        int floatPos = super.startPos;

        DefaultMutableTreeNode classAnOffsetNode = addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "class_annotations_off",
                String.format(FORMAT_STRING_STRING, this.class_annotations_off, this.class_annotations_off),
                "msg_annotations_directory_item__class_annotations_off",
                Icons.Offset);
        floatPos += Type_uint.LENGTH;

        if (this.class_annotations_off.value != 0) {
            annotation_set_item classAn = (annotation_set_item) dexFile.data.get(this.class_annotations_off.value);
            DefaultMutableTreeNode classAnNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    classAn.getStartPos(),
                    classAn.getLength(),
                    classAn.getClass().getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString(msg_annotation_set_item)
            ));
            classAnOffsetNode.add(classAnNode);
            classAn.generateTreeNode(classAnNode, dexFile);
        }

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "fields_size",
                this.fields_size,
                "msg_annotations_directory_item__fields_size",
                Icons.Size);
        floatPos += Type_uint.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "annotated_methods_size",
                this.annotated_methods_size,
                "msg_annotations_directory_item__annotated_methods_size",
                Icons.Size);
        floatPos += Type_uint.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "annotated_parameters_size",
                this.annotated_parameters_size,
                "msg_annotations_directory_item__annotated_parameters_size",
                Icons.Size);

        if (this.field_annotations != null) {
            final int fieldLen = this.field_annotations.length;
            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.field_annotations[0].getStartPos(),
                    fieldLen * field_annotation.LENGTH,
                    String.format("field_annotations [%,d]", fieldLen),
                    Icons.Annotations,
                    MESSAGES.getString("msg_annotations_directory_item__field_annotations")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < fieldLen; i++) {
                field_annotation item = this.field_annotations[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("field_annotation [%,d]", i),
                        Icons.Annotations,
                        field_annotation.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dexFile);
            }
        }

        if (this.method_annotations != null) {
            final int methodLen = this.method_annotations.length;
            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.method_annotations[0].getStartPos(),
                    methodLen * method_annotation.LENGTH,
                    String.format("method_annotations [%,d]", methodLen),
                    Icons.Annotations,
                    MESSAGES.getString("msg_annotations_directory_item__method_annotations")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < methodLen; i++) {
                method_annotation item = this.method_annotations[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("method_annotation [%,d]", i),
                        Icons.Annotations,
                        method_annotation.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dexFile);
            }
        }

        if (this.parameter_annotations != null) {
            final int parameterLen = this.parameter_annotations.length;
            DefaultMutableTreeNode nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.parameter_annotations[0].getStartPos(),
                    parameterLen * parameter_annotation.LENGTH,
                    String.format("parameter_annotations [%,d]", parameterLen),
                    Icons.Annotations,
                    MESSAGES.getString("msg_annotations_directory_item__parameter_annotations")
            ));
            parentNode.add(nodes);

            for (int i = 0; i < parameterLen; i++) {
                parameter_annotation item = this.parameter_annotations[i];
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item.getStartPos(),
                        item.getLength(),
                        String.format("parameter_annotation [%,d]", i),
                        Icons.Annotations,
                        parameter_annotation.class.getSimpleName()
                ));
                nodes.add(node);
                item.generateTreeNode(node, dexFile);
            }
        }
    }

    public static class field_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        public static final int LENGTH = Type_uint.LENGTH + Type_uint.LENGTH;
        public final Type_uint field_idx;
        public final Type_uint annotations_off;

        field_annotation(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
            super.startPos = stream.getPos();

            this.field_idx = stream.Dex_uint();
            this.annotations_off = stream.Dex_uint();
            if (!dex.data.keySet().contains(this.annotations_off.value)) {
                dex.parseData(this.annotations_off.value, annotation_set_item.class, stream);
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile)format;
            int floatPos = super.startPos;

            addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    "field_idx",
                    String.format(FORMAT_STRING_STRING, this.field_idx, dexFile.field_ids[this.field_idx.intValue()].toString(dexFile)),
                    "msg_field_annotation__field_idx",
                    Icons.Index);
            floatPos += Type_uint.LENGTH;

            DefaultMutableTreeNode offsetNode = addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    MSG_ANNOTATIONS_OFF,
                    this.annotations_off,
                    "msg_field_annotation__annotations_off",
                    Icons.Shortcut);

            annotation_set_item value = (annotation_set_item) dexFile.data.get(this.annotations_off.value);
            DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    value.getStartPos(),
                    value.getLength(),
                    value.toString(),
                    Icons.Annotations,
                    MESSAGES.getString(msg_annotation_set_item)
            ));
            offsetNode.add(valueNode);
            value.generateTreeNode(valueNode, dexFile);
        }
    }

    public static class method_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        public static final int LENGTH = Type_uint.LENGTH + Type_uint.LENGTH;

        public final Type_uint method_idx;
        public final Type_uint annotations_off;

        method_annotation(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
            super.startPos = stream.getPos();

            this.method_idx = stream.Dex_uint();
            this.annotations_off = stream.Dex_uint();
            if (!dex.data.keySet().contains(this.annotations_off.value)) {
                dex.parseData(this.annotations_off.value, annotation_set_item.class, stream);
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile)format;
            int floatPos = super.startPos;

            addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    "method_idx",
                    String.format(FORMAT_STRING_STRING, this.method_idx, dexFile.method_ids[this.method_idx.intValue()].toString(dexFile)),
                    "msg_method_annotation__method_idx",
                    Icons.Index);
            floatPos += Type_uint.LENGTH;

            DefaultMutableTreeNode offsetNode = addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    MSG_ANNOTATIONS_OFF,
                    this.annotations_off,
                    "msg_method_annotation__annotations_off",
                    Icons.Offset);

            annotation_set_item value = (annotation_set_item) dexFile.data.get(this.annotations_off.value);
            DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    value.getStartPos(),
                    value.getLength(),
                    value.toString(),
                    Icons.Shortcut,
                    MESSAGES.getString(msg_annotation_set_item)
            ));
            offsetNode.add(valueNode);
            value.generateTreeNode(valueNode, dexFile);
        }
    }

    public static class parameter_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        public static final int LENGTH = Type_uint.LENGTH + Type_uint.LENGTH;

        public final Type_uint method_idx;
        public final Type_uint annotations_off;

        parameter_annotation(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.method_idx = stream.Dex_uint();
            this.annotations_off = stream.Dex_uint();
            if (!dex.data.keySet().contains(this.annotations_off.value)) {
                dex.parseData(this.annotations_off.value, annotation_set_ref_list.class, stream);
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile)format;
            int floatPos = super.startPos;

            addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    "method_idx",
                    String.format(FORMAT_STRING_STRING, this.method_idx, dexFile.method_ids[this.method_idx.intValue()].toString(dexFile)),
                    "msg_parameter_annotation__method_idx",
                    Icons.Index);
            floatPos += Type_uint.LENGTH;

            DefaultMutableTreeNode offsetNode = addNode(parentNode,
                    floatPos,
                    Type_uint.LENGTH,
                    MSG_ANNOTATIONS_OFF,
                    this.annotations_off,
                    "msg_parameter_annotation__annotations_off",
                    Icons.Offset);

            annotation_set_ref_list value = (annotation_set_ref_list) dexFile.data.get(this.annotations_off.value);
            DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    value.getStartPos(),
                    value.getLength(),
                    value.toString(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_annotation_set_ref_list")
            ));
            offsetNode.add(valueNode);
            value.generateTreeNode(valueNode, dexFile);
        }
    }
}
