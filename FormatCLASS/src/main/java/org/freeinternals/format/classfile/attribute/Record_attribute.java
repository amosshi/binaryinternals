/*
 * AttributeRecord.java    8:43 AM, August 12, 2021
 *
 * Copyright  2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
import org.freeinternals.format.classfile.constant.cp_info;
import org.freeinternals.format.classfile.u2;

/**
 *
 * The class for the {@code Record} attribute. The Record attribute indicates
 * that the current class is a record class, and stores information about the
 * record components of the record class. There may be at most one Record
 * attribute in the attributes table of a ClassFile structure.
 *
 * The {@code Record} attribute has the following format:
 *
 * <pre>
 *    Record_attribute {
 *        u2                    attribute_name_index;
 *        u4                    attribute_length;
 *
 *        u2                    components_count;
 *        record_component_info components[components_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 16
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.30">
 * VM Spec: The Record Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class Record_attribute extends attribute_info {

    /**
     * The value of the {@link #components_count} item indicates the number of
     * entries in the {@link #components} table.
     */
    public final u2 components_count;

    /**
     * Each entry in the {@link #components} table specifies a record component
     * of the current class, in the order the record components were declared.
     */
    public final record_component_info[] components;

    Record_attribute(final u2 nameIndex, final String type, final PosDataInputStream stream, final cp_info[] cp) throws IOException, FileFormatException {
        super(nameIndex, type, stream);
        this.components_count = new u2(stream);

        if (this.components_count.value > 0) {
            this.components = new record_component_info[this.components_count.value];
            for (int i = 0; i < this.components_count.value; i++) {
                this.components[i] = new record_component_info(stream, cp);
            }
        } else {
            this.components = null;
        }

        super.checkSize(stream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos + 6;

        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "components_count", this.components_count.value,
                "msg_attr_Record__components_count", Icons.Counter
        );

        if (this.components_count.value < 1) {
            return;
        }

        DefaultMutableTreeNode compsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                super.length - 6 - 2,
                String.format("components [%d]", this.components_count.value),
                MESSAGES.getString("msg_attr_record_component_info")
        ));
        parentNode.add(compsNode);

        for (int i = 0; i < this.components_count.value; i++) {
            record_component_info info = this.components[i];
            DefaultMutableTreeNode infoNode = this.addNode(compsNode,
                    info.getStartPos(),
                    info.getLength(),
                    String.format("component %d", i + 1),
                    ((ClassFile)classFile).getCPDescription(info.name_index.value),
                    "msg_attr_record_component_info",
                    Icons.Data
            );
            info.generateTreeNode(infoNode, (ClassFile)classFile);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Record";
    }

    /**
     * The <code>record_component_info</code> structure has the following
     * format.
     *
     * <pre>
     *    record_component_info {
     *        u2             name_index;
     *        u2             descriptor_index;
     *        u2             attributes_count;
     *        attribute_info attributes[attributes_count];
     *    }
     * </pre>
     *
     * <pre>
     * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S101")
    public static final class record_component_info extends FileComponent implements GenerateTreeNodeClassFile {

        /**
         * The value of the name_index item must be a valid index into the
         * {@link ClassFile#constant_pool} table. The <code>constant_pool</code>
         * entry at that index must be a <code>CONSTANT_Utf8_info</code>
         * structure representing a valid unqualified name denoting the record
         * component.
         */
        public final u2 name_index;

        /**
         * The value of the descriptor_index item must be a valid index into the
         * {@link ClassFile#constant_pool} table. The <code>constant_pool</code>
         * entry at that index must be a <code>CONSTANT_Utf8_info</code>
         * structure representing a field descriptor which encodes the type of
         * the record component.
         */
        public final u2 descriptor_index;

        /**
         * The value of the attributes_count item indicates the number of
         * additional attributes of this record component.
         */
        public final u2 attributes_count;

        /**
         * A record component can have any number of optional attributes
         * associated with it.
         */
        public final attribute_info[] attributes;

        private record_component_info(final PosDataInputStream stream, final cp_info[] cp) throws IOException, FileFormatException {
            super.startPos = stream.getPos();

            this.name_index = new u2(stream);
            this.descriptor_index = new u2(stream);
            this.attributes_count = new u2(stream);

            if (this.attributes_count.value > 0) {
                this.attributes = new attribute_info[this.attributes_count.value];
                for (int i = 0; i < this.attributes_count.value; i++) {
                    this.attributes[i] = attribute_info.parse(stream, cp);
                }
            } else {
                this.attributes = null;
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            ClassFile classFile = (ClassFile) format;
            int startPosMoving = super.getStartPos();

            int cpIndex = this.name_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "name_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "name", classFile.getCPDescription(cpIndex)),
                    "msg_attr_record_component_info__name_index", Icons.Name
            );
            startPosMoving += u2.LENGTH;

            cpIndex = this.descriptor_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "descriptor_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "descriptor", classFile.getCPDescription(cpIndex)),
                    "msg_attr_record_component_info__descriptor_index", Icons.Descriptor
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    FIELD_ATTR_COUNT, this.attributes_count.value,
                    "msg_attr_record_component_info__attributes_count", Icons.Counter
            );
            startPosMoving += u2.LENGTH;

            if (this.attributes_count.value > 0) {
                int attrLength = 0;
                for (attribute_info attr : this.attributes) {
                    attrLength += attr.getLength();
                }

                DefaultMutableTreeNode treeNodeAttributes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        attrLength,
                        String.format(FIELD_ATTRS, this.attributes_count.value),
                        MESSAGES.getString("msg_attr_record_component_info__attributes")
                ));

                for (int i = 0; i < this.attributes_count.value; i++) {
                    attribute_info attr = this.attributes[i];
                    DefaultMutableTreeNode treeNodeAttributeItem = this.addNode(treeNodeAttributes,
                            attr.getStartPos(),
                            attr.getLength(),
                            String.valueOf(i + 1),
                            attr.getName(),
                            attr.getMessageKey(),
                            Icons.Annotations
                    );
                    attr.generateTreeNodeCommon(treeNodeAttributeItem, classFile);
                }
                parentNode.add(treeNodeAttributes);
            }
        }
    }
}
