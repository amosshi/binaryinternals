package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u2;

/// The `annotation` structure in `RuntimeVisibleAnnotations`
/// attribute.
///
/// It has the following format:
///
///
/// ```
/// annotation {
///   u2 type_index;
///   u2 num_element_value_pairs;
///   {   u2            element_name_index;
///       element_value value;
///   } element_value_pairs[num_element_value_pairs];
/// }
/// ```
///
/// @author Amos Shi
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16">
/// VM Spec: annotation structure
/// </a>
///
/// ```
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// java:S116  - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S1104", "java:S116"})
public class Annotation extends FileComponent implements GenerateTreeNodeClassFile {

    public final u2 type_index;
    public final u2 num_element_value_pairs;
    public final Annotation.element_value_pair[] element_value_pairs;

    protected Annotation(final PosDataInputStream posDataInputStream) throws IOException {
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

    /// Get the value of `num_element_value_pairs`[`index`].
    ///
    /// @param index Index of the num_element_value_pairs item
    /// @return The value of `num_element_value_pairs`[`index`]
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
                TEXT_CPINDEX_VALUE.formatted(cpIndex, "type", classFile.getCPDescription(cpIndex)),
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
                    "element_value_pairs [%d]".formatted(this.num_element_value_pairs.value),
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

    /// Each value of the {@link Annotation#element_value_pairs} table represents
    /// a single element-value pair in the annotation represented by this
    /// `annotation` structure.
    ///
    /// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16">
    /// VM Spec: The RuntimeVisibleAnnotations Attribute
    /// </a>
    ///
    /// ```
    /// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
    /// ```
    @SuppressWarnings({"java:S101"})
    public static final class element_value_pair extends FileComponent implements GenerateTreeNodeClassFile {

        /// The name of the element of the element-value pair represented by this
        /// {@link Annotation#element_value_pairs} entry.
        public final u2 element_name_index;
        /// Represents the value of the element-value pair represented by this
        /// {@link Annotation#element_value_pairs} entry.
        public final element_value value;

        protected element_value_pair(final PosDataInputStream posDataInputStream) throws IOException {
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
                    TEXT_CPINDEX_VALUE.formatted(cpIndex, "element name", classFile.getCPDescription(cpIndex)),
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

    /// The element_value structure is a discriminated union representing the
    /// value of an element-value pair.
    ///
    ///
    /// It has the following format:
    ///
    /// ```
    /// element_value {
    ///     u1 tag;
    ///     union {
    ///         u2 const_value_index;
    ///
    ///         {   u2 type_name_index;
    ///             u2 const_name_index;
    ///         } enum_const_value;
    ///
    ///         u2 class_info_index;
    ///
    ///         annotation annotation_value;
    ///
    ///         {   u2            num_values;
    ///             element_value values[num_values];
    ///         } array_value;
    ///     } value;
    /// }
    /// ```
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16.1">
    /// VM Spec: The element_value structure
    /// </a>
    @SuppressWarnings({"java:S101"})
    public static final class element_value extends FileComponent implements GenerateTreeNodeClassFile {

        /// The `tag` item uses a single ASCII character to indicate
        /// the type of the value of the element-value pair.
        ///
        /// @see TagEnum
        public final char tag;
        /// The `value` union, its type is determined by the
        /// {@link #tag}.
        public final Value value;

        protected element_value(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();

            this.tag = (char) posDataInputStream.readUnsignedByte();
            final TagEnum tagEnum = TagEnum.of(this.tag).orElseThrow(() -> new FileFormatException(
                    "Un-recognized tag value in structure element_value. tag=%c (%02X), offset=%d (%08X)".formatted(
                            this.tag, (int) this.tag, this.startPos, this.startPos)));
            this.value = switch (tagEnum) {
                case B, C, D, F, I, J, S, Z, s -> new ConstValue(new u2(posDataInputStream));
                case e -> new enum_const_value(posDataInputStream);
                case c -> new ClassInfo(new u2(posDataInputStream));
                case at -> new AnnotationValue(new Annotation(posDataInputStream));
                case array_type -> new array_value(posDataInputStream);
            };

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            final int unionPos = this.getStartPos() + 1;

            this.addNode(parentNode,
                    this.getStartPos(),
                    1,
                    "tag",
                    this.tag + " - " + element_value.TagEnum.getType(this.tag),
                    "msg_attr_element_value__tag",
                    Icons.Tag
            );

            switch (this.value) {
                case ConstValue(u2 constValueIndex) -> this.addNode(parentNode,
                        unionPos,
                        u2.LENGTH,
                        "const_value_index",
                        TEXT_CPINDEX_VALUE.formatted(constValueIndex.value, "const value", classFile.getCPDescription(constValueIndex.value)),
                        "msg_attr_element_value__const_value_index",
                        Icons.Constant
                );
                case enum_const_value enumConstValue -> {
                    final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            unionPos,
                            enum_const_value.LENGTH,
                            enum_const_value.class.getSimpleName(),
                            MESSAGES.getString("msg_attr_element_value__enum_const_value")
                    ));
                    parentNode.add(node);
                    enumConstValue.generateTreeNode(node, fileFormat);
                }
                case ClassInfo(u2 classInfoIndex) -> this.addNode(parentNode,
                        unionPos,
                        u2.LENGTH,
                        "class_info_index",
                        TEXT_CPINDEX_VALUE.formatted(classInfoIndex.value, "class info", classFile.getCPDescription(classInfoIndex.value)),
                        "msg_attr_element_value__class_info_index",
                        Icons.Name
                );
                case AnnotationValue(Annotation annotation) -> {
                    final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            unionPos,
                            annotation.getLength(),
                            "annotation_value",
                            MESSAGES.getString("msg_attr_element_value__annotation_value")
                    ));
                    parentNode.add(node);
                    annotation.generateTreeNode(node, classFile);
                }
                case array_value arrayValue -> {
                    final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            unionPos,
                            arrayValue.getLength(),
                            array_value.class.getSimpleName(),
                            MESSAGES.getString("msg_attr_element_value__array_value")
                    ));
                    parentNode.add(node);
                    arrayValue.generateTreeNode(node, fileFormat);
                }
            }
        }

        /// The `value` union of an {@link element_value}.
        public sealed interface Value permits ConstValue, enum_const_value, ClassInfo, AnnotationValue, array_value {
        }

        /// The `const_value_index` item, for the {@link #tag}
        /// `B C D F I J S Z s`.
        ///
        /// @param const_value_index Index of a constant in the constant pool
        public record ConstValue(u2 const_value_index) implements Value {
        }

        /// The `class_info_index` item, for the {@link #tag}
        /// `c`.
        ///
        /// @param class_info_index Index of a return descriptor in the constant
        /// pool
        public record ClassInfo(u2 class_info_index) implements Value {
        }

        /// The `annotation_value` item, for the {@link #tag}
        /// `@`.
        ///
        /// @param annotation_value The nested annotation
        public record AnnotationValue(Annotation annotation_value) implements Value {
        }

        /// The valid characters for the `tag` item.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16.1-130">
        /// VM Spec: The element_value structure
        /// </a>
        ///
        /// ```
        /// java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
        /// ```
        @SuppressWarnings("java:S115")
        public enum TagEnum {

            /// Type `byte`.
            B(java.lang.classfile.AnnotationValue.TAG_BYTE, "byte"),
            /// Type `char`.
            C(java.lang.classfile.AnnotationValue.TAG_CHAR, "char"),
            /// Type `double`.
            D(java.lang.classfile.AnnotationValue.TAG_DOUBLE, "double"),
            /// Type `float`.
            F(java.lang.classfile.AnnotationValue.TAG_FLOAT, "float"),
            /// Type `int`.
            I(java.lang.classfile.AnnotationValue.TAG_INT, "int"),
            /// Type `long`.
            J(java.lang.classfile.AnnotationValue.TAG_LONG, "long"),
            /// Type `short`.
            S(java.lang.classfile.AnnotationValue.TAG_SHORT, "short"),
            /// Type `boolean`.
            Z(java.lang.classfile.AnnotationValue.TAG_BOOLEAN, "boolean"),
            /// Type `String`.
            s(java.lang.classfile.AnnotationValue.TAG_STRING, "String"),
            /// Type `Enum type`.
            e(java.lang.classfile.AnnotationValue.TAG_ENUM, "Enum type"),
            /// Type `Class`.
            c(java.lang.classfile.AnnotationValue.TAG_CLASS, "Class"),
            /// Type `Annotation type`.
            at(java.lang.classfile.AnnotationValue.TAG_ANNOTATION, "Annotation type"),
            /// Type `Array type`.
            array_type(java.lang.classfile.AnnotationValue.TAG_ARRAY, "Array type");

            /// Internal value of the enum.
            public final char value;
            /// The type name for a enumeration type.
            ///
            /// See table
            /// `Table 4.7.16.1-A. Interpretation of tag values as types.`
            public final String Type;

            TagEnum(int v, String type) {
                this.value = (char) v;
                this.Type = type;
            }

            /// Get the type name for a enumeration {@link #value}.
            ///
            /// @param v a enumeration {@link #value}
            /// @return Corresponding {@link #Type} for the enumeration
            /// {@link #value}, or `Un-recognized` if it is not a
            /// valid enumeration value
            public static String getType(char v) {
                return of(v).map(e -> e.Type).orElse("Un-recognized");
            }

            /// Get the enumeration of a {@link #value}.
            ///
            /// @param v a enumeration {@link #value}
            /// @return The enumeration, or empty if it is not a valid value
            public static Optional<TagEnum> of(char v) {
                return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
            }
        }

        public static final class enum_const_value extends FileComponent implements GenerateTreeNodeClassFile, Value {

            public static final int LENGTH = 4;
            public final u2 type_name_index;
            public final u2 const_name_index;

            protected enum_const_value(final PosDataInputStream posDataInputStream) throws IOException {
                super(posDataInputStream.getPos(), LENGTH);

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
                        TEXT_CPINDEX_VALUE.formatted(cpIndex, "type name", classFile.getCPDescription(cpIndex)),
                        "msg_attr_element_value__enum_const_value__type_name_index",
                        Icons.Name
                );
                startPosMoving += u2.LENGTH;

                cpIndex = this.const_name_index.value;
                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "const_name_index",
                        TEXT_CPINDEX_VALUE.formatted(cpIndex, "const name", classFile.getCPDescription(cpIndex)),
                        "msg_attr_element_value__enum_const_value__const_name_index",
                        Icons.Name
                );
            }
        }

        public static final class array_value extends FileComponent implements GenerateTreeNodeClassFile, Value {

            public final u2 num_values;
            public final element_value[] values;

            protected array_value(final PosDataInputStream posDataInputStream)
                    throws IOException {
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
                            this.getLength() - u2.LENGTH,
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
