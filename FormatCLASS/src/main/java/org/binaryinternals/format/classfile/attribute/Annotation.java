package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u2;

/**
 * The {@code annotation} structure in {@code RuntimeVisibleAnnotations}
 * attribute.
 * <p>
 * It has the following format:
 * </p>
 *
 * <pre>
 * annotation {
 *   u2 type_index;
 *   u2 num_element_value_pairs;
 *   {   u2            element_name_index;
 *       element_value value;
 *   } element_value_pairs[num_element_value_pairs];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.16">
 * VM Spec: annotation structure
 * </a>
 *
 * <pre>
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * java:S116  - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S1104", "java:S116"})
public class Annotation extends FileComponent implements GenerateTreeNodeClassFile {

    public final u2 type_index;
    public final u2 num_element_value_pairs;
    public final Annotation.element_value_pair[] element_value_pairs;

    protected Annotation(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super.startPos = posDataInputStream.getPos();
        this.type_index = new u2(posDataInputStream);
        this.num_element_value_pairs = new u2(posDataInputStream);
        if (this.num_element_value_pairs.value > 0) {
            this.element_value_pairs = new element_value_pair[this.num_element_value_pairs.value];
            for (int i = 0; i < this.num_element_value_pairs.value; i++) {
                this.element_value_pairs[i] = new element_value_pair(posDataInputStream);
            }
        } else {
            this.element_value_pairs = null;
        }
        super.length = posDataInputStream.getPos() - super.startPos;
    }

    /**
     * Get the value of {@code num_element_value_pairs}[{@code index}].
     *
     * @param index Index of the num_element_value_pairs item
     * @return The value of {@code num_element_value_pairs}[{@code index}]
     */
    public Annotation.element_value_pair getElementvaluePair(final int index) {
        Annotation.element_value_pair p = null;
        if (this.element_value_pairs != null && index < this.element_value_pairs.length) {
            p = this.element_value_pairs[index];
        }
        return p;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        final ClassFile classFile = (ClassFile) fileFormat;
        int currentPos = this.startPos;

        int cpIndex = this.type_index.value;
        this.addNode(parentNode,
                currentPos,
                u2.LENGTH,
                "type_index",
                String.format(TEXT_CPINDEX_VALUE, cpIndex, "type", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_attr_annotation__type_index",
                Icons.Kind
        );
        currentPos += u2.LENGTH;

        this.addNode(parentNode,
                currentPos,
                u2.LENGTH,
                "num_element_value_pairs",
                this.num_element_value_pairs.value,
                "msg_attr_annotation__num_element_value_pairs",
                Icons.Counter
        );
        currentPos += u2.LENGTH;

        if (this.num_element_value_pairs.value > 0) {
            DefaultMutableTreeNode elementValuePairsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    currentPos,
                    this.getStartPos() + this.getLength() - currentPos,
                    String.format("element_value_pairs [%d]", this.num_element_value_pairs.value),
                    MESSAGES.getString("msg_attr_annotation__element_value_pairs")
            ));
            parentNode.add(elementValuePairsNode);

            for (int i = 0; i < this.num_element_value_pairs.value; i++) {
                Annotation.element_value_pair pair = this.getElementvaluePair(i);
                DefaultMutableTreeNode elementValuePairNode = this.addNode(elementValuePairsNode,
                        pair.getStartPos(),
                        pair.getLength(),
                        String.valueOf(i + 1),
                        "element_value_pair",
                        "msg_attr_annotation__element_value_pairs",
                        Icons.Data
                );

                pair.generateTreeNode(elementValuePairNode, fileFormat);
            }
        }
    }

    /**
     * Each value of the {@link Annotation#element_value_pairs} table represents
     * a single element-value pair in the annotation represented by this
     * <code>annotation</code> structure.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.16">
     * VM Spec: The RuntimeVisibleAnnotations Attribute
     * </a>
     *
     * <pre>
     * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings({"java:S101"})
    public static final class element_value_pair extends FileComponent implements GenerateTreeNodeClassFile {

        /**
         * The name of the element of the element-value pair represented by this
         * {@link Annotation#element_value_pairs} entry.
         */
        public final u2 element_name_index;
        /**
         * Represents the value of the element-value pair represented by this
         * {@link Annotation#element_value_pairs} entry.
         */
        public final element_value value;

        protected element_value_pair(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            this.startPos = posDataInputStream.getPos();

            this.element_name_index = new u2(posDataInputStream);
            this.value = new element_value(posDataInputStream);

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.element_name_index.value;
            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "element_name_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "element name", classFile.getCPDescription(cpIndex)),
                    "msg_attr_annotation__element_value_pairs_element_name_index",
                    Icons.Name
            );

            DefaultMutableTreeNode valueNode = this.addNode(parentNode,
                    startPosMoving + u2.LENGTH,
                    this.getLength() - u2.LENGTH,
                    "value",
                    "raw data",
                    "msg_attr_annotation__element_value_pairs_value",
                    Icons.Data
            );
            this.value.generateTreeNode(valueNode, fileFormat);
        }
    }

    /**
     * The element_value structure is a discriminated union representing the
     * value of an element-value pair.
     *
     * <p>
     * It has the following format:
     * </p>
     * <pre>
     * element_value {
     *     u1 tag;
     *     union {
     *         u2 const_value_index;
     *
     *         {   u2 type_name_index;
     *             u2 const_name_index;
     *         } enum_const_value;
     *
     *         u2 class_info_index;
     *
     *         annotation annotation_value;
     *
     *         {   u2            num_values;
     *             element_value values[num_values];
     *         } array_value;
     *     } value;
     * }
     * </pre>
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.16.1">
     * VM Spec: The element_value structure
     * </a>
     */
    @SuppressWarnings({"java:S101"})
    public static final class element_value extends FileComponent implements GenerateTreeNodeClassFile {

        /**
         * The <code>tag</code> item uses a single ASCII character to indicate
         * the type of the value of the element-value pair.
         *
         * @see TagEnum
         */
        public final char tag;
        /**
         * The value of {@link #union_const_value_index} might be null depending
         * on the {@link #tag} value
         */
        public final u2 union_const_value_index;
        /**
         * The value of {@link #union_enum_const_value} might be null depending
         * on the {@link #tag} value
         */
        public final enum_const_value union_enum_const_value;
        /**
         * The value of {@link #union_class_info_index} might be null depending
         * on the {@link #tag} value
         */
        public final u2 union_class_info_index;
        /**
         * The value of {@link #union_annotation_value} might be null depending
         * on the {@link #tag} value
         */
        public final Annotation union_annotation_value;
        /**
         * The value of {@link #union_array_value} might be null depending on
         * the {@link #tag} value
         */
        public final array_value union_array_value;

        protected element_value(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            this.startPos = posDataInputStream.getPos();

            this.tag = (char) posDataInputStream.read(); // Read 1 byte only
            if (this.tag == TagEnum.B.value
                    || this.tag == TagEnum.C.value
                    || this.tag == TagEnum.D.value
                    || this.tag == TagEnum.F.value
                    || this.tag == TagEnum.I.value
                    || this.tag == TagEnum.J.value
                    || this.tag == TagEnum.S.value
                    || this.tag == TagEnum.Z.value
                    || this.tag == TagEnum.s.value) {
                this.union_const_value_index = new u2(posDataInputStream);
                this.union_enum_const_value = null;
                this.union_class_info_index = null;
                this.union_annotation_value = null;
                this.union_array_value = null;

            } else if (this.tag == TagEnum.e.value) {
                this.union_const_value_index = null;
                this.union_enum_const_value = new enum_const_value(posDataInputStream);
                this.union_class_info_index = null;
                this.union_annotation_value = null;
                this.union_array_value = null;

            } else if (this.tag == TagEnum.c.value) {
                this.union_const_value_index = null;
                this.union_enum_const_value = null;
                this.union_class_info_index = new u2(posDataInputStream);
                this.union_annotation_value = null;
                this.union_array_value = null;

            } else if (this.tag == TagEnum.at.value) {
                this.union_const_value_index = null;
                this.union_enum_const_value = null;
                this.union_class_info_index = null;
                this.union_annotation_value = new Annotation(posDataInputStream);
                this.union_array_value = null;

            } else if (this.tag == TagEnum.array_type.value) {
                this.union_const_value_index = null;
                this.union_enum_const_value = null;
                this.union_class_info_index = null;
                this.union_annotation_value = null;
                this.union_array_value = new array_value(posDataInputStream);

            } else {
                throw new FileFormatException(String.format(
                        "Un-recognized tag value in strcutrue element_value. tag=%c (%02X), offset=%d (%08X)",
                        this.tag, (int) this.tag,
                        posDataInputStream.getPos(), posDataInputStream.getPos()
                ));
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            this.addNode(parentNode,
                    startPosMoving,
                    1,
                    "tag",
                    this.tag + " - " + element_value.TagEnum.getType(this.tag),
                    "msg_attr_element_value__tag",
                    Icons.Tag
            );
            startPosMoving += 1;

            if (this.union_const_value_index != null) {
                final int constValueIndex = this.union_const_value_index.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "const_value_index",
                        String.format(TEXT_CPINDEX_VALUE, constValueIndex, "const value", classFile.getCPDescription(constValueIndex)),
                        "msg_attr_element_value__const_value_index",
                        Icons.Constant
                );

            } else if (this.union_enum_const_value != null) {
                DefaultMutableTreeNode unionConstNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    enum_const_value.LENGTH,
                    enum_const_value.class.getSimpleName(),
                    MESSAGES.getString("msg_attr_element_value__enum_const_value")
                ));
                this.union_enum_const_value.generateTreeNode(unionConstNode, fileFormat);

            } else if (this.union_class_info_index != null) {
                int classInfoIndex = this.union_class_info_index.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "class_info_index",
                        String.format(TEXT_CPINDEX_VALUE, classInfoIndex, "class info", classFile.getCPDescription(classInfoIndex)),
                        "msg_attr_element_value__class_info_index",
                        Icons.Name
                );

            } else if (this.union_annotation_value != null) {
                DefaultMutableTreeNode unionAnnotationNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    this.union_annotation_value.getLength(),
                    "annotation_value",
                    MESSAGES.getString("msg_attr_element_value__annotation_value")
                ));
                this.union_annotation_value.generateTreeNode(unionAnnotationNode, classFile);

            } else if (this.union_array_value != null) {
                DefaultMutableTreeNode unionArrayValueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    this.union_array_value.getLength(),
                    array_value.class.getSimpleName(),
                    MESSAGES.getString("msg_attr_element_value__array_value")
                ));
                this.union_array_value.generateTreeNode(unionArrayValueNode, fileFormat);
            }
        }

        /**
         * The valid characters for the <code>tag</code> item.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.16.1-130">
         * VM Spec: The element_value structure
         * </a>
         *
         * <pre>
         * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
         * </pre>
         */
        @SuppressWarnings("java:S115")
        public enum TagEnum {

            /**
             * Type <code>byte</code>.
             */
            B('B', "byte"),
            /**
             * Type <code>char</code>.
             */
            C('C', "char"),
            /**
             * Type <code>double</code>.
             */
            D('D', "double"),
            /**
             * Type <code>float</code>.
             */
            F('F', "float"),
            /**
             * Type <code>int</code>.
             */
            I('I', "int"),
            /**
             * Type <code>long</code>.
             */
            J('J', "long"),
            /**
             * Type <code>short</code>.
             */
            S('S', "short"),
            /**
             * Type <code>boolean</code>.
             */
            Z('Z', "boolean"),
            /**
             * Type <code>String</code>.
             */
            s('s', "String"),
            /**
             * Type <code>Enum type</code>.
             */
            e('e', "Enum type"),
            /**
             * Type <code>Class</code>.
             */
            c('c', "Class"),
            /**
             * Type <code>Annotation type</code>.
             */
            at('@', "Annotation type"),
            /**
             * Type <code>Array type</code>.
             */
            array_type('[', "Array type");

            /**
             * Internal value of the enum.
             */
            public final char value;
            /**
             * The type name for a enumeration type.
             * <p>
             * See table
             * <code>Table 4.7.16.1-A. Interpretation of tag values as types.</code>
             * </p>
             */
            public final String Type;

            TagEnum(char v, String type) {
                this.value = v;
                this.Type = type;
            }

            /**
             * Get the type name for a enumeration {@link #value}.
             *
             * @param v a enumeration {@link #value}
             * @return Corresponding {@link #Type} for the enumeration
             * {@link #value}, or <code>Un-recognized</code> if it is not a
             * valid enumeration value
             */
            public static String getType(char v) {
                String result = "Un-recognized";
                for (TagEnum e : TagEnum.values()) {
                    if (e.value == v) {
                        result = e.Type;
                        break;
                    }
                }
                return result;
            }
        }

        public static final class enum_const_value extends FileComponent implements GenerateTreeNodeClassFile {

            public static final int LENGTH = 4;
            public final u2 type_name_index;
            public final u2 const_name_index;

            protected enum_const_value(final PosDataInputStream posDataInputStream) throws IOException {
                this.startPos = posDataInputStream.getPos();
                this.length = LENGTH;

                this.type_name_index = new u2(posDataInputStream);
                this.const_name_index = new u2(posDataInputStream);
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                final ClassFile classFile = (ClassFile) fileFormat;
                int startPosMoving = this.getStartPos();

                int cpIndex = this.type_name_index.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "type_name_index",
                        String.format(TEXT_CPINDEX_VALUE, cpIndex, "type name", classFile.getCPDescription(cpIndex)),
                        "msg_attr_element_value__enum_const_value__type_name_index",
                        Icons.Name
                );
                startPosMoving += u2.LENGTH;

                cpIndex = this.const_name_index.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "const_name_index",
                        String.format(TEXT_CPINDEX_VALUE, cpIndex, "const name", classFile.getCPDescription(cpIndex)),
                        "msg_attr_element_value__enum_const_value__const_name_index",
                        Icons.Name
                );
            }
        }

        public static final class array_value extends FileComponent implements GenerateTreeNodeClassFile {

            public final u2 num_values;
            public final element_value[] values;

            protected array_value(final PosDataInputStream posDataInputStream)
                    throws IOException, FileFormatException {
                this.startPos = posDataInputStream.getPos();

                this.num_values = new u2(posDataInputStream);
                if (this.num_values.value > 0) {
                    this.values = new element_value[this.num_values.value];
                    for (int i = 0; i < this.num_values.value; i++) {
                        this.values[i] = new element_value(posDataInputStream);
                    }
                } else {
                    this.values = null;
                }
                this.length = posDataInputStream.getPos() - this.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                int numValues = this.num_values.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "num_values",
                        numValues,
                        "msg_attr_element_value__array_value__num_values",
                        Icons.Counter
                );
                startPosMoving += u2.LENGTH;

                if (this.values != null && this.values.length > 0) {
                    DefaultMutableTreeNode valuesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            this.getLength() - 3,
                            "values[" + numValues + "]",
                            MESSAGES.getString("msg_attr_element_value__array_value__values")
                    ));
                    parentNode.add(valuesNode);

                    for (int i = 0; i < this.values.length; i++) {
                        DefaultMutableTreeNode valueNode = this.addNode(valuesNode,
                                this.values[i].getStartPos(),
                                this.values[i].getLength(),
                                String.valueOf(i + 1),
                                "value",
                                "msg_attr_element_value__array_value",
                                Icons.Data
                        );
                        this.values[i].generateTreeNode(valueNode, fileFormat);
                    }
                }
            } // End of generateTreeNode()
        }
    }
}
