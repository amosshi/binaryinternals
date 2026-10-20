package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u1;
import org.binaryinternals.format.classfile.u2;

/// The Runtime(In)VisibleTypeAnnotations Attribute.
///
/// @author Amos Shi
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We respect the name from JVM spec instead
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S5993"})
public abstract class RuntimeTypeAnnotations extends attribute_info {

    public final u2 num_annotations;
    public final type_annotation[] annotations;

    public RuntimeTypeAnnotations(u2 nameIndex, String name, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, name, posDataInputStream);

        this.num_annotations = new u2(posDataInputStream);
        if (this.num_annotations.value > 0) {
            this.annotations = new type_annotation[this.num_annotations.value];
            for (int i = 0; i < this.num_annotations.value; i++) {
                this.annotations[i] = new type_annotation(posDataInputStream);
            }
        } else {
            this.annotations = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /// Get message key for {@link #annotations}.
    ///
    /// @return Message key for {@link #annotations}
    ///
    /// @see RuntimeVisibleTypeAnnotations_attribute
    /// @see RuntimeInvisibleTypeAnnotations_attribute
    abstract String getMessageKey_4_annotations();

    /// Get message key for {@link #num_annotations}.
    ///
    /// @return Message key for {@link #num_annotations}
    ///
    /// @see RuntimeVisibleTypeAnnotations_attribute
    /// @see RuntimeInvisibleTypeAnnotations_attribute
    abstract String getMessageKey_4_num_annotations();

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat classFile) {
        int startPosMoving = super.startPos;
        this.addNode(parentNode,
                startPosMoving + 6,
                u2.LENGTH,
                "num_annotations",
                this.num_annotations.value,
                this.getMessageKey_4_num_annotations(),
                Icons.Counter
        );

        if (this.annotations != null && this.annotations.length > 0) {
            DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    "annotations [%d]".formatted(this.annotations.length),
                    MESSAGES.getString(this.getMessageKey_4_annotations())
            ));
            parentNode.add(annotationsNode);

            for (int i = 0; i < this.annotations.length; i++) {
                DefaultMutableTreeNode typeAnnotationNode = this.addNode(annotationsNode,
                        this.annotations[i].getStartPos(),
                        this.annotations[i].getLength(),
                        String.valueOf(i + 1),
                        "type_annotation",
                        this.getMessageKey_4_annotations(),
                        Icons.Annotations
                );
                this.annotations[i].generateTreeNode(typeAnnotationNode, classFile);
            }
        }
    }

    /// The `type_annotation` structure has the following format.
    ///
    /// ```
    /// type_annotation {
    ///   u1 target_type;
    ///   union {
    ///     type_parameter_target;
    ///     supertype_target;
    ///     type_parameter_bound_target;
    ///     empty_target;
    ///     formal_parameter_target;
    ///     throws_target;
    ///     localvar_target;
    ///     catch_target;
    ///     offset_target;
    ///     type_argument_target;
    ///   } target_info;
    ///   type_path target_path;
    ///
    ///   u2        type_index;
    ///   u2        num_element_value_pairs;
    ///   {   u2            element_name_index;
    ///       element_value value;
    ///   } element_value_pairs[num_element_value_pairs];
    /// }
    /// ```
    public static class type_annotation extends FileComponent implements GenerateTreeNodeClassFile {

        public final u1 target_type;
        /// The `target_info` union, determined by the
        /// {@link #target_type}; it is null for an unrecognized
        /// {@link #target_type}.
        public final target_info union_target_info;
        public final type_path target_path;
        /// The rest fields are the same as {@link Annotation}
        public final Annotation at;

        /// Parse a `type_annotation` structure.
        ///
        /// @param posDataInputStream Input stream
        /// @throws IOException Read file failed
        /// @throws FileFormatException Invalid file format
        public type_annotation(PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            super.startPos = posDataInputStream.getPos();
            this.target_type = new u1(posDataInputStream);
            this.union_target_info = switch (TargetType.of(this.target_type.value).orElse(null)) {
                case VALUE_HEX_00, VALUE_HEX_01 -> new type_parameter_target(posDataInputStream);
                case VALUE_HEX_10 -> new supertype_target(posDataInputStream);
                case VALUE_HEX_11, VALUE_HEX_12 -> new type_parameter_bound_target(posDataInputStream);
                case VALUE_HEX_13, VALUE_HEX_14, VALUE_HEX_15 -> new empty_target(posDataInputStream);
                case VALUE_HEX_16 -> new formal_parameter_target(posDataInputStream);
                case VALUE_HEX_17 -> new throws_target(posDataInputStream);
                case VALUE_HEX_40, VALUE_HEX_41 -> new localvar_target(posDataInputStream);
                case VALUE_HEX_42 -> new catch_target(posDataInputStream);
                case VALUE_HEX_43, VALUE_HEX_44, VALUE_HEX_45, VALUE_HEX_46 -> new offset_target(posDataInputStream);
                case VALUE_HEX_47, VALUE_HEX_48, VALUE_HEX_49, VALUE_HEX_4A, VALUE_HEX_4B -> new type_argument_target(posDataInputStream);
                // Unrecognized: go through, since the attribute length is checked later
                case null -> null;
            };
            this.target_path = new type_path(posDataInputStream);
            this.at = new Annotation(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            this.addNode(parentNode,
                    startPosMoving,
                    u1.LENGTH,
                    "target_type",
                    "0x%02X".formatted(this.target_type.value),
                    "msg_attr_type_annotation__target_type",
                    Icons.Tag
            );
            startPosMoving += u1.LENGTH;

            switch (this.union_target_info) {
                case type_parameter_target target -> {
                    DefaultMutableTreeNode typeParameterTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.type_parameter_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__type_parameter_target")
                    ));
                    parentNode.add(typeParameterTarget);

                    this.addNode(typeParameterTarget,
                            startPosMoving,
                            u1.LENGTH,
                            "type_parameter_index",
                            target.type_parameter_index.value,
                            "msg_attr_type_annotation__target_info__type_parameter_target__type_parameter_index",
                            Icons.Index
                    );
                }
                case supertype_target target -> {
                    DefaultMutableTreeNode supertypeTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.supertype_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__supertype_target")
                    ));
                    parentNode.add(supertypeTarget);

                    this.addNode(supertypeTarget,
                            startPosMoving,
                            u2.LENGTH,
                            "supertype_index",
                            target.supertype_index.value,
                            "msg_attr_type_annotation__target_info__supertype_target__supertype_index",
                            Icons.Index
                    );
                }
                case type_parameter_bound_target target -> {
                    DefaultMutableTreeNode typeParameterBoundTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.type_parameter_bound_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__type_parameter_bound_target")
                    ));
                    parentNode.add(typeParameterBoundTarget);
                    target.generateTreeNode(typeParameterBoundTarget, fileFormat);
                }
                case empty_target _ -> {
                    // Do nothing since it is empty
                }
                case formal_parameter_target target -> {
                    DefaultMutableTreeNode formalParameterTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.formal_parameter_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__formal_parameter_target")
                    ));
                    parentNode.add(formalParameterTarget);

                    this.addNode(formalParameterTarget,
                            startPosMoving,
                            u1.LENGTH,
                            "formal_parameter_index",
                            target.formal_parameter_index.value,
                            "msg_attr_type_annotation__target_info__formal_parameter_target__formal_parameter_index",
                            Icons.Index
                    );
                }
                case throws_target target -> {
                    DefaultMutableTreeNode throwsTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.throws_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__throws_target")
                    ));
                    parentNode.add(throwsTarget);

                    this.addNode(throwsTarget,
                            startPosMoving, u2.LENGTH,
                            "throws_type_index", target.throws_type_index.value,
                            "msg_attr_type_annotation__target_info__throws_target__throws_type_index",
                            Icons.Index
                    );
                }
                case localvar_target target -> {
                    DefaultMutableTreeNode localvarTargetNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.localvar_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__localvar_target")
                    ));
                    parentNode.add(localvarTargetNode);

                    target.generateTreeNode(localvarTargetNode, fileFormat);
                }
                case catch_target target -> {
                    DefaultMutableTreeNode catchTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.catch_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__catch_target")
                    ));
                    parentNode.add(catchTarget);

                    this.addNode(catchTarget,
                            startPosMoving, u2.LENGTH,
                            "exception_table_index", target.exception_table_index.value,
                            "msg_attr_type_annotation__target_info__catch_target__exception_table_index",
                            Icons.Index
                    );
                }
                case offset_target target -> {
                    DefaultMutableTreeNode offsetTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.offset_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__offset_target")
                    ));
                    parentNode.add(offsetTarget);

                    this.addNode(offsetTarget,
                            startPosMoving, u2.LENGTH,
                            "offset", target.offset.value,
                            "msg_attr_type_annotation__target_info__offset_target__offset",
                            Icons.Offset
                    );
                }
                case type_argument_target target -> {
                    DefaultMutableTreeNode typeArgumentTargetNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            target.getLength(),
                            type_annotation.type_argument_target.UNION_NAME,
                            MESSAGES.getString("msg_attr_type_annotation__target_info__type_argument_target")
                    ));
                    parentNode.add(typeArgumentTargetNode);

                    target.generateTreeNode(typeArgumentTargetNode, fileFormat);
                }
                case null -> {
                    // Unrecognized target_type
                }
            }
            if (this.union_target_info != null) {
                startPosMoving += this.union_target_info.getLength();
            }

            // target_path
            DefaultMutableTreeNode targetPathNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    this.target_path.getLength(),
                    "target_path",
                    MESSAGES.getString("msg_attr_type_annotation__target_path")
            ));
            parentNode.add(targetPathNode);
            this.target_path.generateTreeNode(targetPathNode, fileFormat);

            // Annotation
            this.at.generateTreeNode(parentNode, classFile);
        }

        public enum TargetType {

            /// type parameter declaration of generic class or interface.
            VALUE_HEX_00(0x00),
            /// type parameter declaration of generic method or constructor.
            VALUE_HEX_01(0x01),
            /// type in extends or implements clause of class declaration
            /// (including the direct superclass or direct superinterface of an
            /// anonymous class declaration), or in extends clause of interface
            /// declaration.
            VALUE_HEX_10(0x10),
            /// type in bound of type parameter declaration of generic class or
            /// interface.
            VALUE_HEX_11(0x11),
            /// type in bound of type parameter declaration of generic method or
            /// constructor.
            VALUE_HEX_12(0x12),
            /// type in field declaration.
            VALUE_HEX_13(0x13),
            /// return type of method, or type of newly constructed object.
            VALUE_HEX_14(0x14),
            /// receiver type of method or constructor.
            VALUE_HEX_15(0x15),
            /// type in formal parameter declaration of method, constructor, or
            /// lambda expression.
            VALUE_HEX_16(0x16),
            /// type in throws clause of method or constructor.
            VALUE_HEX_17(0x17),
            /// type in local variable declaration.
            VALUE_HEX_40(0x40),
            /// type in resource variable declaration.
            VALUE_HEX_41(0x41),
            /// type in exception parameter declaration.
            VALUE_HEX_42(0x42),
            /// type in instanceof expression.
            VALUE_HEX_43(0x43),
            /// type in new expression.
            VALUE_HEX_44(0x44),
            /// type in method reference expression using ::new.
            VALUE_HEX_45(0x45),
            /// type in method reference expression using ::Identifier.
            VALUE_HEX_46(0x46),
            /// type in cast expression.
            VALUE_HEX_47(0x47),
            /// type argument for generic constructor in new expression or
            /// explicit constructor invocation statement.
            VALUE_HEX_48(0x48),
            /// type argument for generic method in method invocation expression.
            VALUE_HEX_49(0x49),
            /// type argument for generic constructor in method reference
            /// expression using ::new.
            VALUE_HEX_4A(0x4A),
            /// type argument for generic method in method reference expression
            /// using ::Identifier.
            VALUE_HEX_4B(0x4B);

            /// Internal value of the enum.
            public final short value;

            TargetType(Integer v) {
                this.value = v.shortValue();
            }

            /// Get union name in `type_annotation` structure.
            ///
            /// @param v {@link #value} of {@link TargetType}
            /// @return `type_annotation` structure union name
            /// Get the enum of a `target_type` value.
            ///
            /// @param v The `target_type` value
            /// @return The enum, or empty for an unrecognized value
            public static Optional<TargetType> of(short v) {
                return Arrays.stream(values()).filter(t -> t.value == v).findFirst();
            }

            public static String getTargetUnionName(short v) {
                return switch (of(v).orElse(null)) {
                    case VALUE_HEX_00, VALUE_HEX_01 -> type_parameter_target.UNION_NAME;
                    case VALUE_HEX_10 -> supertype_target.UNION_NAME;
                    case VALUE_HEX_11, VALUE_HEX_12 -> type_parameter_bound_target.UNION_NAME;
                    case VALUE_HEX_13, VALUE_HEX_14, VALUE_HEX_15 -> empty_target.UNION_NAME;
                    case VALUE_HEX_16 -> formal_parameter_target.UNION_NAME;
                    case VALUE_HEX_17 -> throws_target.UNION_NAME;
                    case VALUE_HEX_40, VALUE_HEX_41 -> localvar_target.UNION_NAME;
                    case VALUE_HEX_42 -> catch_target.UNION_NAME;
                    case VALUE_HEX_43, VALUE_HEX_44, VALUE_HEX_45, VALUE_HEX_46 -> offset_target.UNION_NAME;
                    case VALUE_HEX_47, VALUE_HEX_48, VALUE_HEX_49, VALUE_HEX_4A, VALUE_HEX_4B -> type_argument_target.UNION_NAME;
                    case null -> "Un-recognized";
                };
            }

        }

        /// The `target_info` union of a {@link type_annotation}.
        public sealed interface target_info permits type_parameter_target, supertype_target, type_parameter_bound_target,
                empty_target, formal_parameter_target, throws_target, localvar_target, catch_target, offset_target,
                type_argument_target {

            /// Get the length of the item.
            ///
            /// @return Length in bytes
            int getLength();
        }

        /// The type_parameter_target item indicates that an annotation appears
        /// on the declaration of the i'th type parameter of a generic class,
        /// generic interface, generic method, or generic constructor.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.20.1">
        /// VM Spec: The target_info union - type_parameter_target
        /// </a>
        public static final class type_parameter_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "type_parameter_target";

            /// The value of the type_parameter_index item specifies which type
            /// parameter declaration is annotated. A type_parameter_index value
            /// of 0 specifies the first type parameter declaration.
            public final u1 type_parameter_index;

            protected type_parameter_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.type_parameter_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        /// The supertype_target item indicates that an annotation appears on a
        /// type in the extends or implements clause of a class or interface
        /// declaration.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.20.1">
        /// VM Spec: The target_info union - supertype_target
        /// </a>
        public static final class supertype_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "supertype_target";

            /// A supertype_index value of 65535 specifies that the annotation
            /// appears on the superclass in an extends clause of a class
            /// declaration.
            public static final int SUPERTYPE_INDEX_SUERCLASS = 65535;

            /// A supertype_index value of 65535 specifies that the annotation
            /// appears on the superclass in an extends clause of a class
            /// declaration. Any other supertype_index value is an index into the
            /// interfaces array of the enclosing ClassFile structure, and
            /// specifies that the annotation appears on that superinterface in
            /// either the implements clause of a class declaration or the
            /// extends clause of an interface declaration.
            public final u2 supertype_index;

            protected supertype_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.supertype_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class type_parameter_bound_target extends FileComponent implements GenerateTreeNodeClassFile, target_info {

            public static final int LENGTH = u1.LENGTH + u1.LENGTH;
            public static final String UNION_NAME = "type_parameter_bound_target";
            public final u1 type_parameter_index;
            public final u1 bound_index;

            protected type_parameter_bound_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.type_parameter_index = new u1(posDataInputStream);
                this.bound_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving,
                        u1.LENGTH,
                        "type_parameter_index",
                        this.type_parameter_index.value,
                        "msg_attr_type_annotation__target_info__type_parameter_bound_target__type_parameter_index",
                        Icons.Index
                );
                startPosMoving += u1.LENGTH;

                this.addNode(parentNode,
                        startPosMoving,
                        u1.LENGTH,
                        "bound_index",
                        this.bound_index.value,
                        "msg_attr_type_annotation__target_info__type_parameter_bound_target__bound_index",
                        Icons.Index
                );
            }
        }

        public static final class empty_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "empty_target";

            protected empty_target(final PosDataInputStream posDataInputStream) throws IOException {
                super(posDataInputStream.getPos(), 0);
            }
        }

        public static final class formal_parameter_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "formal_parameter_target";
            public final u1 formal_parameter_index;

            protected formal_parameter_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.formal_parameter_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class throws_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "throws_target";
            public final u2 throws_type_index;

            protected throws_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.throws_type_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        /// The `localvar_target` item indicates that an annotation
        /// appears on the type in a local variable declaration, including a
        /// variable declared as a resource in a try-with-resources statement.
        ///
        /// ```
        /// localvar_target {
        ///     u2 table_length;
        ///     {   u2 start_pc;
        ///         u2 length;
        ///         u2 index;
        ///     } table[table_length];
        /// }
        /// ```
        public static final class localvar_target extends FileComponent implements GenerateTreeNodeClassFile, target_info {

            public static final String UNION_NAME = "localvar_target";
            public final u2 table_length;
            public final Table[] table;

            protected localvar_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.table_length = new u2(posDataInputStream);
                if (this.table_length.value > 0) {
                    this.table = new Table[this.table_length.value];
                    for (int i = 0; i < this.table_length.value; i++) {
                        this.table[i] = new Table(posDataInputStream);
                    }
                } else {
                    this.table = null;
                }
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving,
                        u2.LENGTH,
                        "table_length",
                        this.table_length.value,
                        "msg_attr_type_annotation__target_info__localvar_target__table_length",
                        Icons.Length
                );
                startPosMoving += u2.LENGTH;

                if (this.table == null || this.table.length < 1) {
                    return;
                }

                DefaultMutableTreeNode tablesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.getLength() - u2.LENGTH,
                        "table [%d]".formatted(this.table.length),
                        MESSAGES.getString("msg_attr_type_annotation__target_info__localvar_target__table")
                ));
                parentNode.add(tablesNode);
                for (int i = 0; i < this.table.length; i++) {
                    DefaultMutableTreeNode tableNode = this.addNode(tablesNode,
                            this.table[i].getStartPos(),
                            this.table[i].getLength(),
                            String.valueOf(i + 1),
                            "table",
                            "msg_attr_type_annotation__target_info__localvar_target__table",
                            Icons.Data
                    );
                    this.table[i].generateTreeNode(tableNode, fileFormat);
                }
            }

            public static final class Table extends FileComponent implements GenerateTreeNodeClassFile {

                public final u2 start_pc;
                public final u2 length_code;
                public final u2 index;

                protected Table(final PosDataInputStream posDataInputStream) throws IOException {
                    super.startPos = posDataInputStream.getPos();
                    this.start_pc = new u2(posDataInputStream);
                    this.length_code = new u2(posDataInputStream);
                    this.index = new u2(posDataInputStream);
                    super.length = posDataInputStream.getPos() - super.startPos;
                }

                @Override
                public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                    int startPosMoving = this.getStartPos();
                    this.addNode(parentNode,
                            startPosMoving, u2.LENGTH,
                            "start_pc", this.start_pc.value,
                            "msg_attr_type_annotation__target_info__localvar_target__table__start_pc_length",
                            Icons.Offset
                    );
                    startPosMoving += u2.LENGTH;

                    this.addNode(parentNode,
                            startPosMoving, u2.LENGTH,
                            "length", this.length_code.value,
                            "msg_attr_type_annotation__target_info__localvar_target__table__start_pc_length",
                            Icons.Length
                    );
                    startPosMoving += u2.LENGTH;

                    this.addNode(parentNode,
                            startPosMoving, u2.LENGTH,
                            "index", this.index.value,
                            "msg_attr_type_annotation__target_info__localvar_target__table__index",
                            Icons.Index
                    );
                } // End of generateTreeNode()
            }
        }

        public static final class catch_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "catch_target";
            public final u2 exception_table_index;

            protected catch_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.exception_table_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class offset_target extends FileComponent implements target_info {

            public static final String UNION_NAME = "offset_target";
            public final u2 offset;

            protected offset_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class type_argument_target extends FileComponent implements GenerateTreeNodeClassFile, target_info {

            public static final String UNION_NAME = "type_argument_target";
            public final u2 offset;
            public final u1 type_argument_index;

            protected type_argument_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                this.type_argument_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        "offset", this.offset.value,
                        "msg_attr_type_annotation__target_info__type_argument_target__offset",
                        Icons.Offset
                );
                startPosMoving += u2.LENGTH;

                this.addNode(parentNode,
                        startPosMoving, u1.LENGTH,
                        "type_argument_index", this.type_argument_index.value,
                        "msg_attr_type_annotation__target_info__type_argument_target__type_argument_index",
                        Icons.Index
                );
            }
        }

        public static final class type_path extends FileComponent implements GenerateTreeNodeClassFile {

            public final u1 path_length;
            public final Path[] path;

            protected type_path(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.path_length = new u1(posDataInputStream);
                if (this.path_length.value > 0) {
                    this.path = new Path[this.path_length.value];
                    for (int i = 0; i < this.path_length.value; i++) {
                        this.path[i] = new Path(posDataInputStream);
                    }
                } else {
                    this.path = null;
                }
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving, u1.LENGTH,
                        "path_length", this.path_length.value,
                        "msg_attr_type_path__path_length",
                        Icons.Length
                );
                startPosMoving += u1.LENGTH;

                if (this.path == null || this.path.length < 1) {
                    return;
                }

                DefaultMutableTreeNode pathsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.getLength() - u1.LENGTH,
                        "path [%d]".formatted(this.path.length),
                        MESSAGES.getString("msg_attr_type_path__path")
                ));
                parentNode.add(pathsNode);
                for (int i = 0; i < this.path.length; i++) {
                    DefaultMutableTreeNode pathNode = this.addNode(pathsNode,
                            this.path[i].getStartPos(),
                            this.path[i].getLength(),
                            String.valueOf(i + 1),
                            "path",
                            "msg_attr_type_path__path",
                            Icons.Data
                    );
                    this.path[i].generateTreeNode(pathNode, fileFormat);
                }
            }

            public static final class Path extends FileComponent implements GenerateTreeNodeClassFile {

                /// @see TypePathKind
                public final u1 type_path_kind;
                public final u1 type_argument_index;

                protected Path(final PosDataInputStream posDataInputStream) throws IOException {
                    super.startPos = posDataInputStream.getPos();
                    this.type_path_kind = new u1(posDataInputStream);
                    this.type_argument_index = new u1(posDataInputStream);
                    super.length = posDataInputStream.getPos() - super.startPos;
                }

                @Override
                public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                    int startPosMoving = this.getStartPos();

                    short pathKind = this.type_path_kind.value;
                    this.addNode(parentNode,
                            startPosMoving, u1.LENGTH,
                            "type_path_kind",
                            pathKind + " + " + TypePathKind.getDescription(pathKind),
                            "msg_attr_type_path__type_path_kind",
                            Icons.Kind
                    );
                    startPosMoving += u1.LENGTH;

                    this.addNode(parentNode,
                            startPosMoving, u1.LENGTH,
                            "type_argument_index",
                            this.type_argument_index.value,
                            "msg_attr_type_path__type_argument_index",
                            Icons.Index
                    );
                }
            }

            public enum TypePathKind {

                /// Annotation is deeper in an array type.
                VALUE_0(0, "Annotation is deeper in an array type"),
                /// Annotation is deeper in a nested type.
                VALUE_1(1, "Annotation is deeper in a nested type"),
                /// Annotation is on the bound of a wildcard type argument of a
                /// parameterized type.
                VALUE_2(2, "Annotation is on the bound of a wildcard type argument of a parameterized type"),
                /// Annotation is on a type argument of a parameterized type.
                VALUE_3(3, "Annotation is on a type argument of a parameterized type");

                /// Internal value of the type path kind.
                public final short value;
                /// Description of the type path kind.
                public final String description;

                TypePathKind(Integer v, String d) {
                    this.value = v.shortValue();
                    this.description = d;
                }

                public static String getDescription(short v) {
                    for (TypePathKind item : TypePathKind.values()) {
                        if (item.value == v) {
                            return item.description;
                        }
                    }

                    return "Un-Recognized";
                }
            }
        }
    }
}
