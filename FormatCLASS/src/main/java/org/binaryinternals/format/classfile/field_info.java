/*
 * field_info.java    3:57 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.attribute.attribute_info;
import org.binaryinternals.format.classfile.constant.cp_info;

/**
 * {@code Field} of a class or interface. The {@code Field} structure has the
 * following format:
 *
 * <pre>
 *    field_info {
 *        u2 access_flags;
 *        u2 name_index;
 *        u2 descriptor_index;
 *        u2 attributes_count;
 *        attribute_info attributes[attributes_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.5">
 * VM Spec: Fields
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class field_info extends FileComponent implements GenerateTreeNodeClassFile {

    public final u2 access_flags;
    public final u2 name_index;
    public final u2 descriptor_index;
    public final u2 attributes_count;
    public final attribute_info[] attributes;
    private String declaration;
    private String descriptor;
    private String name;

    field_info(final PosDataInputStream posDataInputStream, final cp_info[] cp) throws IOException, FileFormatException {
        this.startPos = posDataInputStream.getPos();
        this.length = -1;

        this.access_flags = new u2(posDataInputStream);
        this.name_index = new u2(posDataInputStream);
        this.descriptor_index = new u2(posDataInputStream);
        this.attributes_count = new u2(posDataInputStream);

        final int attrCount = this.attributes_count.value;
        if (attrCount > 0) {
            this.attributes = new attribute_info[attrCount];
            for (int i = 0; i < attrCount; i++) {
                this.attributes[i] = attribute_info.parse(posDataInputStream, cp);
            }
        } else {
            this.attributes = null;
        }

        this.calculateLength();
        this.parse(cp);
    }

    private void calculateLength() {
        this.length = 8;

        for (int i = 0; i < this.attributes_count.value; i++) {
            this.length += this.attributes[i].getLength();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get raw data

    /**
     * Get the declaration of the field. The declaration is generated by
     * {@code access_flags}, {@code name_index} and {@code descriptor_index}.
     *
     * @return {@code Field} declaration
     */
    public String getDeclaration() {
        return this.declaration;
    }

    /**
     * Get field descriptor.
     *
     * @return Field descriptor
     * @see #descriptor
     */
    public String getDescriptor() {
        return this.descriptor;
    }

    /**
     * Generate the modifier string from the {@link #access_flags} value.
     *
     * @return A string for modifier
     */
    public String getModifiers() {
        return AccessFlag.getFieldModifier(this.access_flags.value);
    }

    /**
     * Get field name.
     *
     * @return Field name
     * @see #name_index
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the declaration string.
     */
    private void parse(final cp_info[] cpInfo) throws FileFormatException {
        this.name = ClassFile.getConstantUtf8Value(this.name_index.value, cpInfo);
        this.descriptor = ClassFile.getConstantUtf8Value(this.descriptor_index.value, cpInfo);

        String type;
        try {
            type = SignatureConvertor.fieldDescriptorExtractor(this.descriptor).toString();
        } catch (FileFormatException se) {
            type = "[Unexpected signature type]: " + this.descriptor;
        }

        this.declaration = String.format("%s %s %s", this.getModifiers(), type, this.name);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        final ClassFile classFile = (ClassFile)fileFormat;
        final int floatPos = this.getStartPos();

        this.addNode(parentNode,
                floatPos,
                u2.LENGTH,
                FIELD_ACCESS_FLAGS,
                BytesTool.getBinaryString(this.access_flags.value) + " " + this.getModifiers(),
                "msg_field_info__access_flags",
                Icons.AccessFlag
        );

        final int nameIndex = this.name_index.value;
        this.addNode(parentNode,
                floatPos + 2,
                u2.LENGTH,
                "name_index",
                String.format(TEXT_CPINDEX_VALUE, nameIndex, "field name", classFile.getCPDescription(nameIndex)),
                "msg_field_info__name_index",
                Icons.Name
        );

        final int descriptorIndex = this.descriptor_index.value;
        this.addNode(parentNode,
                floatPos + 4,
                u2.LENGTH,
                "descriptor_index",
                String.format(TEXT_CPINDEX_VALUE, descriptorIndex, "field descriptor", classFile.getCPDescription(descriptorIndex)),
                "msg_field_info__descriptor_index",
                Icons.Descriptor
        );

        final int attributesCount = this.attributes_count.value;
        this.addNode(parentNode,
                floatPos + 6,
                u2.LENGTH,
                FIELD_ATTR_COUNT,
                attributesCount,
                "msg_field_info__attributes_count",
                Icons.Counter
        );

        if (attributesCount > 0) {
            final attribute_info lastAttr = this.attributes[attributesCount - 1];
            final DefaultMutableTreeNode treeNodeAttr =
                    new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    floatPos + 8,
                    lastAttr.getStartPos() + lastAttr.getLength() - floatPos - 8,
                    String.format(FIELD_ATTRS, attributesCount),
                    MESSAGES.getString("msg_field_info__attributes")
            ));

            DefaultMutableTreeNode treeNodeAttrItem;
            attribute_info attr;
            for (int i = 0; i < attributesCount; i++) {
                attr = this.attributes[i];
                treeNodeAttrItem = this.addNode(treeNodeAttr,
                        attr.getStartPos(),
                        attr.getLength(),
                        String.valueOf(i + 1),
                        attr.getName(),
                        attr.getMessageKey(),
                        Icons.Annotations
                );
                attr.generateTreeNodeCommon(treeNodeAttrItem, classFile);
            }
            parentNode.add(treeNodeAttr);
        }
    }
}
