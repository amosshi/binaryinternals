/*
 * encoded_annotation.java    Aug 24, 2021
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// @author Amos Shi
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public final class encoded_annotation extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_uleb128 type_idx;
    private String type_jls;
    public final Type_uleb128 size;
    public final annotation_element[] elements;

    encoded_annotation(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.type_idx = stream.Dex_uleb128();

        this.size = stream.Dex_uleb128();
        if (this.size.value > 0) {
            this.elements = new annotation_element[this.size.value];
            for (int i = 0; i < this.size.value; i++) {
                this.elements[i] = new annotation_element(stream);
            }
        } else {
            this.elements = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    /// Get [#type_idx] name in Java Language Specification format.
    ///
    /// @param dexFile Current [DexFile]
    /// @return type name in JLS format
    /// @see #type_idx
    public String get_type_jls(DexFile dexFile) {
        if (this.type_jls == null) {
            this.type_jls = dexFile.type_ids[this.type_idx.value].get_descriptor_jls(dexFile);
        }
        return this.type_jls;
    }

    public String toString(DexFile dexFile) {
        return "%s type=%d | %s size=%d".formatted(
                this.getClass().getSimpleName(),
                this.type_idx.value,
                this.get_type_jls(dexFile),
                this.size.value);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile) format;
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                this.type_idx.length,
                "type_idx",
                FORMAT_STRING_STRING.formatted(this.type_idx, dexFile.get_type_ids_string(this.type_idx.value)),
                "msg_encoded_annotation__type_idx",
                Icons.Index);
        floatPos += this.type_idx.length;

        addNode(parentNode,
                floatPos,
                this.size.length,
                "size",
                this.size,
                "msg_encoded_annotation__size",
                Icons.Size);
        floatPos += this.size.length;

        if (this.elements != null) {
            DefaultMutableTreeNode elementsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    floatPos,
                    this.length - this.type_idx.length - this.size.length,
                    "elements [%d]".formatted(this.elements.length),
                    Icons.Data,
                    MESSAGES.getString("msg_encoded_annotation__elements")
            ));
            parentNode.add(elementsNode);

            for (int i = 0; i < this.elements.length; i++) {
                annotation_element element = this.elements[i];

                // Dev phase only
                if (element == null) {
                    continue;
                }

                DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        element.getStartPos(),
                        element.getLength(),
                        "%s[%d]".formatted(annotation_element.class.getSimpleName(), i),
                        Icons.Data,
                        MESSAGES.getString("msg_encoded_annotation__elements")
                ));

                elementsNode.add(elementNode);
                element.generateTreeNode(elementNode, dexFile);
            }

        }

    }

    public static final class annotation_element extends FileComponent implements GenerateTreeNodeDexFile {
        public final Type_uleb128 name_idx;
        public final encoded_value value;

        annotation_element(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.name_idx = stream.Dex_uleb128();

            this.value = new encoded_value(stream);

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile) format;
            int floatPos = super.startPos;
            addNode(parentNode,
                    floatPos,
                    this.name_idx.length,
                    "name_idx",
                    FORMAT_STRING_STRING.formatted(this.name_idx, dexFile.get_string_ids_string(this.name_idx.value)),
                    "msg_annotation_element__name_idx",
                    Icons.Index);
            floatPos += this.name_idx.length;

            DefaultMutableTreeNode valueNode = addNode(parentNode,
                    floatPos,
                    this.value.getLength(),
                    "value",
                    this.value,
                    "msg_annotation_element__value",
                    Icons.Data);
            this.value.generateTreeNode(valueNode, dexFile);
        }
    }
}
