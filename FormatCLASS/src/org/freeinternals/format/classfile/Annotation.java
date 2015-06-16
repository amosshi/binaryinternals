package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

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
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16">
 * VM Spec: annotation structure
 * </a>
 * @author Amos Shi
 */
public class Annotation extends FileComponent {

    public transient final u2 type_index;
    public transient final u2 num_element_value_pairs;
    public transient final Annotation.ElementValuePair[] element_value_pairs;

    protected Annotation(final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        this.startPos = posDataInputStream.getPos();

        this.type_index = new u2(posDataInputStream.readUnsignedShort());
        this.num_element_value_pairs = new u2(posDataInputStream.readUnsignedShort());
        if (this.num_element_value_pairs.value > 0) {
            this.element_value_pairs = new ElementValuePair[this.num_element_value_pairs.value];
            for (int i = 0; i < this.num_element_value_pairs.value; i++) {
                this.element_value_pairs[i] = new ElementValuePair(posDataInputStream);
            }
        } else {
            this.element_value_pairs = null;
        }

        this.length = posDataInputStream.getPos() - this.startPos;
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
        public transient final u2 element_name_index;
        /**
         * Represents the value of the element-value pair represented by this
         * {@link Annotation#element_value_pairs} entry.
         */
        public transient final ElementValue value;

        protected ElementValuePair(final PosDataInputStream posDataInputStream)
                throws IOException, FileFormatException {
            this.startPos = posDataInputStream.getPos();

            this.element_name_index = new u2(posDataInputStream.readUnsignedShort());
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
        public transient final char tag;
        /**
         * The value of {@link #union_const_value_index} might be null depending
         * on the {@link #tag} value
         */
        public transient final u2 union_const_value_index;
        /**
         * The value of {@link #union_enum_const_value} might be null depending
         * on the {@link #tag} value
         */
        public final EnumConstValue union_enum_const_value;
        /**
         * The value of {@link #union_class_info_index} might be null depending
         * on the {@link #tag} value
         */
        public transient final u2 union_class_info_index;
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

        protected ElementValue(final PosDataInputStream posDataInputStream)
                throws IOException, FileFormatException {
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
                this.union_const_value_index = new u2(posDataInputStream.readUnsignedShort());
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
                this.union_class_info_index = new u2(posDataInputStream.readUnsignedShort());
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

            TagEnum(char v, String Type) {
                this.value = v;
                this.Type = Type;
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

            protected EnumConstValue(final PosDataInputStream posDataInputStream)
                    throws IOException {
                this.startPos = posDataInputStream.getPos();
                this.length = LENGTH;

                this.type_name_index = new u2(posDataInputStream.readUnsignedShort());
                this.const_name_index = new u2(posDataInputStream.readUnsignedShort());
            }
        }

        public final static class ArrayValue extends FileComponent {

            public final u2 num_values;
            public final ElementValue[] values;

            protected ArrayValue(final PosDataInputStream posDataInputStream)
                    throws IOException, FileFormatException {
                this.startPos = posDataInputStream.getPos();

                this.num_values = new u2(posDataInputStream.readUnsignedShort());
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
