package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleTypeAnnotations Attribute.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S5993"})
public abstract class RuntimeTypeAnnotations_attribute extends attribute_info {

    public final u2 num_annotations;
    public final type_annotation[] annotations;

    public RuntimeTypeAnnotations_attribute(u2 nameIndex, String name, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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

    // 4.7.20. The RuntimeVisibleTypeAnnotations Attribute
    // 4.7.21. The RuntimeInvisibleTypeAnnotations Attribute
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat classFile) {
        int startPosMoving = super.startPos;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                u2.LENGTH,
                "num_annotations: " + this.num_annotations.value
        )));

        if (this.annotations != null && this.annotations.length > 0) {
            DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    "annotations"
            ));
            parentNode.add(annotationsNode);

            for (int i = 0; i < this.annotations.length; i++) {
                DefaultMutableTreeNode typeAnnotationNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.annotations[i].getStartPos(),
                        this.annotations[i].getLength(),
                        String.format("type_annotation %d", i + 1)
                ));
                annotationsNode.add(typeAnnotationNode);
                this.annotations[i].generateTreeNode(typeAnnotationNode, classFile);
            }
        }
    }

    /**
     * The <code>type_annotation</code> structure has the following format.
     *
     * <pre>
     * type_annotation {
     *   u1 target_type;
     *   union {
     *     type_parameter_target;
     *     supertype_target;
     *     type_parameter_bound_target;
     *     empty_target;
     *     formal_parameter_target;
     *     throws_target;
     *     localvar_target;
     *     catch_target;
     *     offset_target;
     *     type_argument_target;
     *   } target_info;
     *   type_path target_path;
     *
     *   u2        type_index;
     *   u2        num_element_value_pairs;
     *   {   u2            element_name_index;
     *       element_value value;
     *   } element_value_pairs[num_element_value_pairs];
     * }
     * </pre>
     */
    public static class type_annotation extends FileComponent implements GenerateTreeNodeClassFile {

        public final u1 target_type;
        public final type_parameter_target union_type_parameter_target;
        public final supertype_target union_supertype_target;
        public final type_parameter_bound_target union_type_parameter_bound_target;
        public final empty_target union_empty_target;
        public final formal_parameter_target union_method_formal_parameter_target;
        public final throws_target union_throws_target;
        public final localvar_target union_localvar_target;
        public final catch_target union_catch_target;
        public final offset_target union_offset_target;
        public final type_argument_target union_type_argument_target;
        public final type_path target_path;
        /**
         * The rest fields are the same as {@link Annotation}
         */
        public final Annotation at;

        /**
         * <pre>
         * java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
         * </pre>
         *
         * @param posDataInputStream Input stream
         * @throws IOException Read file failed
         * @throws FileFormatException Invalid file format
         */
        @SuppressWarnings("java:S3776")
        public type_annotation(PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            this.target_type = new u1(posDataInputStream);
            if (this.target_type.value == TargetType.VALUE_HEX_00.value
                    || this.target_type.value == TargetType.VALUE_HEX_01.value) {
                this.union_type_parameter_target = new type_parameter_target(posDataInputStream);
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_10.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = new supertype_target(posDataInputStream);
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_11.value
                    || this.target_type.value == TargetType.VALUE_HEX_12.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = new type_parameter_bound_target(posDataInputStream);
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_13.value
                    || this.target_type.value == TargetType.VALUE_HEX_14.value
                    || this.target_type.value == TargetType.VALUE_HEX_15.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = new empty_target(posDataInputStream);
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_16.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = new formal_parameter_target(posDataInputStream);
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_17.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = new throws_target(posDataInputStream);
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_40.value
                    || this.target_type.value == TargetType.VALUE_HEX_41.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = new localvar_target(posDataInputStream);
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_42.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = new catch_target(posDataInputStream);
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_43.value
                    || this.target_type.value == TargetType.VALUE_HEX_44.value
                    || this.target_type.value == TargetType.VALUE_HEX_45.value
                    || this.target_type.value == TargetType.VALUE_HEX_46.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = new offset_target(posDataInputStream);
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.VALUE_HEX_47.value
                    || this.target_type.value == TargetType.VALUE_HEX_48.value
                    || this.target_type.value == TargetType.VALUE_HEX_49.value
                    || this.target_type.value == TargetType.VALUE_HEX_4A.value
                    || this.target_type.value == TargetType.VALUE_HEX_4B.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = new type_argument_target(posDataInputStream);
            } else {
                // If nothing matches, we do not throw exception, but trying to go through
                // Since later on, we will check the Attribute Length anyway
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            }
            this.target_path = new type_path(posDataInputStream);
            this.at = new Annotation(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "target_type: 0x" + String.format("%02X", this.target_type.value)
            )));
            startPosMoving += u1.LENGTH;

            if (this.union_type_parameter_target != null) {
                DefaultMutableTreeNode typeParameterTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_type_parameter_target.getLength(),
                        type_annotation.type_parameter_target.UNION_NAME));
                parentNode.add(typeParameterTarget);
                typeParameterTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "type_parameter_index: " + this.union_type_parameter_target.type_parameter_index.value
                )));
                startPosMoving += u1.LENGTH;

            } else if (this.union_supertype_target != null) {
                DefaultMutableTreeNode supertypeTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_supertype_target.getLength(),
                        type_annotation.supertype_target.UNION_NAME
                ));
                parentNode.add(supertypeTarget);
                supertypeTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "supertype_index: " + this.union_supertype_target.supertype_index.value
                )));
                startPosMoving += u2.LENGTH;

            } else if (this.union_type_parameter_bound_target != null) {
                DefaultMutableTreeNode typeParameterBoundTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_type_parameter_bound_target.getLength(),
                        type_annotation.type_parameter_bound_target.UNION_NAME
                ));
                parentNode.add(typeParameterBoundTarget);
                typeParameterBoundTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "type_parameter_index: " + this.union_type_parameter_bound_target.type_parameter_index.value
                )));
                startPosMoving += u1.LENGTH;
                typeParameterBoundTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "bound_index: " + this.union_type_parameter_bound_target.bound_index.value
                )));
                startPosMoving += u1.LENGTH;

            } else if (this.union_empty_target != null) {
                // Do nothing since it is empty
            } else if (this.union_method_formal_parameter_target != null) {
                DefaultMutableTreeNode formalParameterTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_method_formal_parameter_target.getLength(),
                        "formal_parameter_target"
                ));
                parentNode.add(formalParameterTarget);
                formalParameterTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "formal_parameter_index: " + this.union_method_formal_parameter_target.formal_parameter_index.value
                )));
                startPosMoving += u1.LENGTH;

            } else if (this.union_throws_target != null) {
                DefaultMutableTreeNode throwsTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_throws_target.getLength(),
                        type_annotation.throws_target.UNION_NAME
                ));
                parentNode.add(throwsTarget);
                throwsTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "throws_type_index: " + this.union_throws_target.throws_type_index.value
                )));
                startPosMoving += u2.LENGTH;

            } else if (this.union_localvar_target != null) {
                DefaultMutableTreeNode localvarTargetNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_localvar_target.getLength(),
                        type_annotation.localvar_target.UNION_NAME
                ));
                startPosMoving += this.union_localvar_target.getLength();
                parentNode.add(localvarTargetNode);
                this.union_localvar_target.generateTreeNode(localvarTargetNode, fileFormat);

            } else if (this.union_catch_target != null) {
                DefaultMutableTreeNode catchTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_catch_target.getLength(),
                        type_annotation.catch_target.UNION_NAME
                ));
                parentNode.add(catchTarget);
                catchTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "exception_table_index: " + this.union_catch_target.exception_table_index.value
                )));
                startPosMoving += u2.LENGTH;

            } else if (this.union_offset_target != null) {
                DefaultMutableTreeNode offsetTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_offset_target.getLength(),
                        type_annotation.offset_target.UNION_NAME
                ));
                parentNode.add(offsetTarget);
                offsetTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "offset: " + this.union_offset_target.offset.value
                )));
                startPosMoving += u2.LENGTH;

            } else if (this.union_type_argument_target != null) {
                DefaultMutableTreeNode typeArgumentTarget = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.union_type_argument_target.getLength(),
                        type_annotation.type_argument_target.UNION_NAME
                ));
                parentNode.add(typeArgumentTarget);
                typeArgumentTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "offset: " + this.union_type_argument_target.offset.value
                )));
                startPosMoving += u2.LENGTH;
                typeArgumentTarget.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "type_argument_index: " + this.union_type_argument_target.type_argument_index.value
                )));
                startPosMoving += u1.LENGTH;
            }

            // target_path
            DefaultMutableTreeNode targetPathNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    this.target_path.getLength(),
                    "target_path"
            ));
            parentNode.add(targetPathNode);
            this.target_path.generateTreeNode(targetPathNode, fileFormat);

            // Annotation
            this.at.generateTreeNode(parentNode, classFile);
        }

        public enum TargetType {

            /**
             * type parameter declaration of generic class or interface.
             */
            VALUE_HEX_00(0x00),
            /**
             * type parameter declaration of generic method or constructor.
             */
            VALUE_HEX_01(0x01),
            /**
             * type in extends or implements clause of class declaration
             * (including the direct superclass or direct superinterface of an
             * anonymous class declaration), or in extends clause of interface
             * declaration.
             */
            VALUE_HEX_10(0x10),
            /**
             * type in bound of type parameter declaration of generic class or
             * interface.
             */
            VALUE_HEX_11(0x11),
            /**
             * type in bound of type parameter declaration of generic method or
             * constructor.
             */
            VALUE_HEX_12(0x12),
            /**
             * type in field declaration.
             */
            VALUE_HEX_13(0x13),
            /**
             * return type of method, or type of newly constructed object.
             */
            VALUE_HEX_14(0x14),
            /**
             * receiver type of method or constructor.
             */
            VALUE_HEX_15(0x15),
            /**
             * type in formal parameter declaration of method, constructor, or
             * lambda expression.
             */
            VALUE_HEX_16(0x16),
            /**
             * type in throws clause of method or constructor.
             */
            VALUE_HEX_17(0x17),
            /**
             * type in local variable declaration.
             */
            VALUE_HEX_40(0x40),
            /**
             * type in resource variable declaration.
             */
            VALUE_HEX_41(0x41),
            /**
             * type in exception parameter declaration.
             */
            VALUE_HEX_42(0x42),
            /**
             * type in instanceof expression.
             */
            VALUE_HEX_43(0x43),
            /**
             * type in new expression.
             */
            VALUE_HEX_44(0x44),
            /**
             * type in method reference expression using ::new.
             */
            VALUE_HEX_45(0x45),
            /**
             * type in method reference expression using ::Identifier.
             */
            VALUE_HEX_46(0x46),
            /**
             * type in cast expression.
             */
            VALUE_HEX_47(0x47),
            /**
             * type argument for generic constructor in new expression or
             * explicit constructor invocation statement.
             */
            VALUE_HEX_48(0x48),
            /**
             * type argument for generic method in method invocation expression.
             */
            VALUE_HEX_49(0x49),
            /**
             * type argument for generic constructor in method reference
             * expression using ::new.
             */
            VALUE_HEX_4A(0x4A),
            /**
             * type argument for generic method in method reference expression
             * using ::Identifier.
             */
            VALUE_HEX_4B(0x4B);

            /**
             * Internal value of the enum.
             */
            public final short value;

            TargetType(Integer v) {
                this.value = v.shortValue();
            }

            /**
             * Get union name in <code>type_annotation</code> structure.
             *
             * @param v {@link #value} of {@link TargetType}
             * @return <code>type_annotation</code> structure union name
             *
             * <pre>
             * java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
             * </pre>
             */
            @SuppressWarnings("java:S3776")
            public static String getTargetUnionName(short v) {
                String unionName;

                if (v == VALUE_HEX_00.value
                        || v == VALUE_HEX_01.value) {
                    unionName = type_parameter_target.UNION_NAME;
                } else if (v == VALUE_HEX_10.value) {
                    unionName = supertype_target.UNION_NAME;
                } else if (v == VALUE_HEX_11.value
                        || v == VALUE_HEX_12.value) {
                    unionName = type_parameter_bound_target.UNION_NAME;
                } else if (v == VALUE_HEX_13.value
                        || v == VALUE_HEX_14.value
                        || v == VALUE_HEX_15.value) {
                    unionName = empty_target.UNION_NAME;
                } else if (v == VALUE_HEX_16.value) {
                    unionName = formal_parameter_target.UNION_NAME;
                } else if (v == VALUE_HEX_17.value) {
                    unionName = throws_target.UNION_NAME;
                } else if (v == VALUE_HEX_40.value
                        || v == VALUE_HEX_41.value) {
                    unionName = localvar_target.UNION_NAME;
                } else if (v == VALUE_HEX_42.value) {
                    unionName = catch_target.UNION_NAME;
                } else if (v == VALUE_HEX_43.value
                        || v == VALUE_HEX_44.value
                        || v == VALUE_HEX_45.value
                        || v == VALUE_HEX_46.value) {
                    unionName = offset_target.UNION_NAME;
                } else if (v == VALUE_HEX_47.value
                        || v == VALUE_HEX_48.value
                        || v == VALUE_HEX_49.value
                        || v == VALUE_HEX_4A.value
                        || v == VALUE_HEX_4B.value) {
                    unionName = type_argument_target.UNION_NAME;
                } else {
                    unionName = "Un-recognized";
                }

                return unionName;
            }

        }

        /**
         * The type_parameter_target item indicates that an annotation appears
         * on the declaration of the i'th type parameter of a generic class,
         * generic interface, generic method, or generic constructor.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.20.1">
         * VM Spec: The target_info union - type_parameter_target
         * </a>
         */
        public static final class type_parameter_target extends FileComponent {

            public static final String UNION_NAME = "type_parameter_target";

            /**
             * The value of the type_parameter_index item specifies which type
             * parameter declaration is annotated. A type_parameter_index value
             * of 0 specifies the first type parameter declaration.
             */
            public final u1 type_parameter_index;

            protected type_parameter_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.type_parameter_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        /**
         * The supertype_target item indicates that an annotation appears on a
         * type in the extends or implements clause of a class or interface
         * declaration.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.20.1">
         * VM Spec: The target_info union - supertype_target
         * </a>
         */
        public static final class supertype_target extends FileComponent {

            public static final String UNION_NAME = "supertype_target";

            /**
             * A supertype_index value of 65535 specifies that the annotation
             * appears on the superclass in an extends clause of a class
             * declaration.
             */
            public static final int SUPERTYPE_INDEX_SUERCLASS = 65535;

            /**
             * A supertype_index value of 65535 specifies that the annotation
             * appears on the superclass in an extends clause of a class
             * declaration. Any other supertype_index value is an index into the
             * interfaces array of the enclosing ClassFile structure, and
             * specifies that the annotation appears on that superinterface in
             * either the implements clause of a class declaration or the
             * extends clause of an interface declaration.
             */
            public final u2 supertype_index;

            protected supertype_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.supertype_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class type_parameter_bound_target extends FileComponent {

            public static final String UNION_NAME = "type_parameter_bound_target";
            public final u1 type_parameter_index;
            public final u1 bound_index;

            protected type_parameter_bound_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.type_parameter_index = new u1(posDataInputStream);
                this.bound_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class empty_target extends FileComponent {

            public static final String UNION_NAME = "empty_target";

            protected empty_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                super.length = 0;
            }
        }

        public static final class formal_parameter_target extends FileComponent {

            public static final String UNION_NAME = "method_formal_parameter_target";
            public final u1 formal_parameter_index;

            protected formal_parameter_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.formal_parameter_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class throws_target extends FileComponent {

            public static final String UNION_NAME = "throws_target";
            public final u2 throws_type_index;

            protected throws_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.throws_type_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        /**
         * The <code>localvar_target</code> item indicates that an annotation
         * appears on the type in a local variable declaration, including a
         * variable declared as a resource in a try-with-resources statement.
         *
         * <pre>
         * localvar_target {
         *     u2 table_length;
         *     {   u2 start_pc;
         *         u2 length;
         *         u2 index;
         *     } table[table_length];
         * }
         * </pre>
         */
        public static final class localvar_target extends FileComponent implements GenerateTreeNodeClassFile {

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
                parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "table_length: " + this.table_length.value
                )));
                startPosMoving += u2.LENGTH;

                if (this.table == null || this.table.length < 1) {
                    return;
                }

                DefaultMutableTreeNode tables = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.getLength() - u2.LENGTH,
                        String.format("table[%d]", this.table.length)
                ));
                parentNode.add(tables);
                for (int i = 0; i < this.table.length; i++) {
                    startPosMoving = this.table[i].getStartPos();
                    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            this.table[i].getLength(),
                            String.format("table %d", i + 1)
                    ));
                    tables.add(tableNode);

                    tableNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            u2.LENGTH,
                            "start_pc: " + this.table[i].start_pc.value
                    )));
                    startPosMoving += u2.LENGTH;
                    tableNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            u2.LENGTH,
                            "length: " + this.table[i].length_code.value
                    )));
                    startPosMoving += u2.LENGTH;
                    tableNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            u2.LENGTH,
                            "index: " + this.table[i].index.value
                    )));
                }

            }

            public static final class Table extends FileComponent {

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

            }
        }

        public static final class catch_target extends FileComponent {

            public static final String UNION_NAME = "catch_target";
            public final u2 exception_table_index;

            protected catch_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.exception_table_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class offset_target extends FileComponent {

            public static final String UNION_NAME = "offset_target";
            public final u2 offset;

            protected offset_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public static final class type_argument_target extends FileComponent {

            public static final String UNION_NAME = "type_argument_target";
            public final u2 offset;
            public final u1 type_argument_index;

            protected type_argument_target(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                this.type_argument_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
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
                parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u1.LENGTH,
                        "path_length: " + this.path_length.value
                )));
                startPosMoving += u1.LENGTH;

                if (this.path == null || this.path.length < 1) {
                    return;
                }

                DefaultMutableTreeNode paths = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.getLength() - u1.LENGTH,
                        String.format("path[%d]", this.path.length)
                ));
                parentNode.add(paths);
                for (int i = 0; i < this.path.length; i++) {
                    startPosMoving = this.path[i].getStartPos();
                    DefaultMutableTreeNode pathNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            this.path[i].getLength(),
                            String.format("path[%d]", i + 1)
                    ));
                    paths.add(pathNode);

                    pathNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            u1.LENGTH,
                            "type_path_kind: " + this.path[i].type_path_kind.value
                    )));
                    startPosMoving += u1.LENGTH;
                    pathNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            u1.LENGTH,
                            "type_argument_index: " + this.path[i].type_argument_index.value
                    )));
                }
            }

            public static final class Path extends FileComponent {

                public final u1 type_path_kind;
                public final u1 type_argument_index;

                protected Path(final PosDataInputStream posDataInputStream) throws IOException {
                    super.startPos = posDataInputStream.getPos();
                    this.type_path_kind = new u1(posDataInputStream);
                    this.type_argument_index = new u1(posDataInputStream);
                    super.length = posDataInputStream.getPos() - super.startPos;
                }
            }

            public enum TypePathKind {

                /**
                 * Annotation is deeper in an array type.
                 */
                VALUE_0(0, "Annotation is deeper in an array type"),
                /**
                 * Annotation is deeper in a nested type.
                 */
                VALUE_1(1, "Annotation is deeper in a nested type"),
                /**
                 * Annotation is on the bound of a wildcard type argument of a
                 * parameterized type.
                 */
                VALUE_2(2, "Annotation is on the bound of a wildcard type argument of a parameterized type"),
                /**
                 * Annotation is on a type argument of a parameterized type.
                 */
                VALUE_3(3, "Annotation is on a type argument of a parameterized type");

                /**
                 * Internal value of the type path kind.
                 */
                public final short value;
                /**
                 * Description of the type path kind.
                 */
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
