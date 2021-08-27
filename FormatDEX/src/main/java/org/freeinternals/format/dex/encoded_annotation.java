/*
 * encoded_annotation.java    Aug 24, 2021
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.GenerateTreeNodeDexFile.MESSAGES;
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
public class encoded_annotation extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_uleb128 type_idx;
    private String type_jls = null;
    public final Type_uleb128 size;
    public final annotation_element[] elements;

    encoded_annotation(PosDataInputStreamDex stream) throws IOException, FileFormatException {
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

    /**
     * Get {@link #type_idx} name in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return type name in JLS format
     * @see #type_idx
     */
    public String get_type_jls(DexFile dexFile) {
        if (this.type_jls == null) {
            this.type_jls = dexFile.type_ids[this.type_idx.value].get_descriptor_jls(dexFile).toString();
        }
        return this.type_jls;
    }

    public String toString(DexFile dexFile) {
        return String.format("%s type=%d | %s size=%d",
                this.getClass().getSimpleName(),
                this.type_idx.value,
                this.get_type_jls(dexFile),
                this.size.value);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                this.type_idx.length,
                "type_idx",
                String.format(FORMAT_STRING_STRING, this.type_idx, dexFile.get_type_ids_string(this.type_idx.value)),
                "msg_encoded_annotation__type_idx",
                UITool.icon4Index());
        floatPos += this.type_idx.length;

        addNode(parentNode,
                floatPos,
                this.size.length,
                "size",
                this.size,
                "msg_encoded_annotation__size",
                UITool.icon4Size());
        floatPos += this.size.length;

        if (this.elements != null) {
            DefaultMutableTreeNode elementsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    floatPos,
                    this.length - this.type_idx.length - this.size.length,
                    String.format("elements [%d]", this.elements.length),
                    UITool.icon4Data(),
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
                        String.format("%s[%d]", annotation_element.class.getSimpleName(), i),
                        UITool.icon4Data(),
                        MESSAGES.getString("msg_encoded_annotation__elements")
                ));

                elementsNode.add(elementNode);
                element.generateTreeNode(elementNode, dexFile);
            }

        }

    }

    public static class annotation_element extends FileComponent implements GenerateTreeNodeDexFile {
        private static final Logger LOGGER = Logger.getLogger(annotation_element.class.getName());
        public final Type_uleb128 name_idx;
        public encoded_value value;

        annotation_element(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.name_idx = stream.Dex_uleb128();

            try {  // TODO Dev phase only
                this.value = new encoded_value(stream);
            } catch (FileFormatException e) {
                // This should never happen
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            int floatPos = super.startPos;
            addNode(parentNode,
                    floatPos,
                    this.name_idx.length,
                    "name_idx",
                    String.format(FORMAT_STRING_STRING, this.name_idx, dexFile.get_string_ids_string(this.name_idx.value)),
                    "msg_annotation_element__name_idx",
                    UITool.icon4Index());
            floatPos += this.name_idx.length;

            // Dev phase only
            if (this.value == null) {
                return;
            }

            DefaultMutableTreeNode valueNode = addNode(parentNode,
                    floatPos,
                    this.value.getLength(),
                    "value",
                    this.value,
                    "msg_annotation_element__value",
                    UITool.icon4Data());
            this.value.generateTreeNode(valueNode, dexFile);
        }
    }
}
