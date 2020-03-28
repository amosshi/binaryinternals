package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.16">
 * VM Spec: annotation structure
 * </a>
 */
public class Annotation extends FileComponent {

    public u2 type_index;
    public u2 num_element_value_pairs;
    public Annotation.ElementValuePair[] element_value_pairs;

    protected Annotation(final PosDataInputStream posDataInputStream, boolean init) throws IOException, FileFormatException {
        super.startPos = posDataInputStream.getPos();
        if (init) {
            this.initAnnotation(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }
    }

    protected Annotation(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        this(posDataInputStream, true);
    }

    protected final void initAnnotation(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        this.type_index = new u2(posDataInputStream);
        this.num_element_value_pairs = new u2(posDataInputStream);
        if (this.num_element_value_pairs.value > 0) {
            this.element_value_pairs = new ElementValuePair[this.num_element_value_pairs.value];
            for (int i = 0; i < this.num_element_value_pairs.value; i++) {
                this.element_value_pairs[i] = new ElementValuePair(posDataInputStream);
            }
        } else {
            this.element_value_pairs = null;
        }
    }

    /**
     * Get the value of {@code num_element_value_pairs}[{@code index}].
     *
     * @param index Index of the num_element_value_pairs item
     * @return The value of {@code num_element_value_pairs}[{@code index}]
     */
    public Annotation.ElementValuePair getElementvaluePair(final int index) {
        Annotation.ElementValuePair p = null;
        if (this.element_value_pairs != null && index < this.element_value_pairs.length) {
            p = this.element_value_pairs[index];
        }
        return p;
    }
    
    
    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    protected static void generateSubnode(final DefaultMutableTreeNode rootNode, final Annotation.ElementValue elementValue, final ClassFile classFile) {

        int startPosMoving = elementValue.getStartPos();
        char tag = elementValue.tag;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                1,
                "tag: " + tag + " - " + ElementValue.TagEnum.getType(tag)
        )));
        startPosMoving += 1;

        if (elementValue.union_const_value_index != null) {
            int constValueIndex = elementValue.union_const_value_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "const_value_index: " + constValueIndex + " - " + classFile.getCPDescription(constValueIndex)
            )));
        } else if (elementValue.union_enum_const_value != null) {
            int cp_index = elementValue.union_enum_const_value.type_name_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "type_name_index: " + cp_index + " - " + classFile.getCPDescription(cp_index)
            )));
            startPosMoving += u2.LENGTH;

            cp_index = elementValue.union_enum_const_value.const_name_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "const_name_index: " + cp_index + " - " + classFile.getCPDescription(cp_index)
            )));
        } else if (elementValue.union_class_info_index != null) {
            int classInfoIndex = elementValue.union_class_info_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "class_info_index: " + classInfoIndex + " - " + classFile.getCPDescription(classInfoIndex)
            )));
        } else if (elementValue.union_annotation_value != null) {
            generateSubnode(rootNode, elementValue.union_annotation_value, classFile);
        } else if (elementValue.union_array_value != null) {
            int num_values = elementValue.union_array_value.num_values.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "num_values: " + num_values
            )));
            startPosMoving += u2.LENGTH;

            if (elementValue.union_array_value.values != null
                    && elementValue.union_array_value.values.length > 0) {
                DefaultMutableTreeNode values = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        elementValue.getLength() - 3,
                        "values: " + num_values
                ));
                rootNode.add(values);

                for (int i = 0; i < elementValue.union_array_value.values.length; i++) {
                    DefaultMutableTreeNode value = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            elementValue.union_array_value.values[i].getStartPos(),
                            elementValue.union_array_value.values[i].getLength(),
                            "value " + (i + 1)
                    ));
                    values.add(value);
                    generateSubnode(value, elementValue.union_array_value.values[i], classFile);
                }
            }
        }
    }
    

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    private static void generateSubnode(final DefaultMutableTreeNode rootNode, final Annotation.ElementValuePair pair, final ClassFile classFile) {

        int startPosMoving = pair.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                2,
                "element_name_index: " + pair.element_name_index.value + " - " + classFile.getCPDescription(pair.element_name_index.value)
        )));
        DefaultMutableTreeNode value = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 2,
                pair.getLength() - 2,
                "value"
        ));
        rootNode.add(value);

        generateSubnode(value, pair.value, classFile);

    }
    
    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    protected static void generateSubnode(final DefaultMutableTreeNode rootNode, final Annotation a, final ClassFile classFile) {
        generateSubnode(rootNode, a, a.getStartPos(), classFile);
    }

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    protected static void generateSubnode(final DefaultMutableTreeNode rootNode, final Annotation a, int currentPos, final ClassFile classFile) {

        // int currentPos = a.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                u2.LENGTH,
                "type_index: " + a.type_index.value + " - " + classFile.getCPDescription(a.type_index.value)
        )));
        currentPos += u2.LENGTH;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                u2.LENGTH,
                "num_element_value_pairs: " + a.num_element_value_pairs.value
        )));
        currentPos += u2.LENGTH;

        if (a.num_element_value_pairs.value > 0) {
            DefaultMutableTreeNode element_value_pairs = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    currentPos,
                    a.getStartPos() + a.getLength() - currentPos,
                    "element_value_pairs"
            ));
            rootNode.add(element_value_pairs);

            for (int i = 0; i < a.num_element_value_pairs.value; i++) {
                Annotation.ElementValuePair pair = a.getElementvaluePair(i);
                DefaultMutableTreeNode element_value_pair_node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pair.getStartPos(),
                        pair.getLength(),
                        String.format("element_value_pair %d", i + 1)
                ));
                element_value_pairs.add(element_value_pair_node);

                generateSubnode(element_value_pair_node, pair, classFile);
            }
        }
    }
    

    /**
     * Each value of the {@link Annotation#element_value_pairs} table represents
     * a single element-value pair in the annotation represented by this
     * <code>annotation</code> structure.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16">
     * VM Spec: The RuntimeVisibleAnnotations Attribute
     * </a>
     */
    public final static class ElementValuePair extends FileComponent {

        /**
         * The name of the element of the element-value pair represented by this
         * {@link Annotation#element_value_pairs} entry.
         */
        public final u2 element_name_index;
        /**
         * Represents the value of the element-value pair represented by this
         * {@link Annotation#element_value_pairs} entry.
         */
        public final ElementValue value;

        protected ElementValuePair(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            this.startPos = posDataInputStream.getPos();

            this.element_name_index = new u2(posDataInputStream);
            this.value = new ElementValue(posDataInputStream);

            this.length = posDataInputStream.getPos() - this.startPos;
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16.1">
     * VM Spec: The element_value structure
     * </a>
     */
    public final static class ElementValue extends FileComponent {

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
        public final EnumConstValue union_enum_const_value;
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
        public final ArrayValue union_array_value;

        protected ElementValue(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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
                this.union_enum_const_value = new EnumConstValue(posDataInputStream);
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

            } else if (this.tag == TagEnum.leftbracket.value) {
                this.union_const_value_index = null;
                this.union_enum_const_value = null;
                this.union_class_info_index = null;
                this.union_annotation_value = null;
                this.union_array_value = new ArrayValue(posDataInputStream);

            } else {
                throw new FileFormatException(String.format(
                        "Un-recognized tag value in strcutrue element_value. tag=%c (%02X), offset=%d (%08X)",
                        this.tag, (int) this.tag,
                        posDataInputStream.getPos(), posDataInputStream.getPos()
                ));
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        /**
         * The valid characters for the <code>tag</code> item.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16.1-130">
         * VM Spec: The element_value structure
         * </a>
         */
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
            leftbracket('[', "Array type");

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

        public final static class EnumConstValue extends FileComponent {

            public static final int LENGTH = 4;
            public final u2 type_name_index;
            public final u2 const_name_index;

            protected EnumConstValue(final PosDataInputStream posDataInputStream) throws IOException {
                this.startPos = posDataInputStream.getPos();
                this.length = LENGTH;

                this.type_name_index = new u2(posDataInputStream);
                this.const_name_index = new u2(posDataInputStream);
            }
        }

        public final static class ArrayValue extends FileComponent {

            public final u2 num_values;
            public final ElementValue[] values;

            protected ArrayValue(final PosDataInputStream posDataInputStream)
                    throws IOException, FileFormatException {
                this.startPos = posDataInputStream.getPos();

                this.num_values = new u2(posDataInputStream);
                if (this.num_values.value > 0) {
                    this.values = new ElementValue[this.num_values.value];
                    for (int i = 0; i < this.num_values.value; i++) {
                        this.values[i] = new ElementValue(posDataInputStream);
                    }
                } else {
                    this.values = null;
                }
                this.length = posDataInputStream.getPos() - this.startPos;
            }
        }
    }
}
