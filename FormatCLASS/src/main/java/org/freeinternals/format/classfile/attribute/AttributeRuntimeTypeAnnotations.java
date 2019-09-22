package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleTypeAnnotations Attribute.
 *
 * @author Amos Shi
 */
public class AttributeRuntimeTypeAnnotations extends AttributeInfo {

    public transient final u2 num_annotations;
    public transient final TypeAnnotation[] annotations;

    public AttributeRuntimeTypeAnnotations(u2 nameIndex, String name, PosDataInputStream posDataInputStream, ClassFile.Version format, JavaSEVersion javaSE) throws IOException, FileFormatException {
        super(nameIndex, name, posDataInputStream, format, javaSE);

        this.num_annotations = new u2(posDataInputStream);
        if (this.num_annotations.value > 0) {
            this.annotations = new TypeAnnotation[this.num_annotations.value];
            for (int i = 0; i < this.num_annotations.value; i++) {
                this.annotations[i] = new TypeAnnotation(posDataInputStream);
            }
        } else {
            this.annotations = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    // 4.7.20. The RuntimeVisibleTypeAnnotations Attribute
    // 4.7.21. The RuntimeInvisibleTypeAnnotations Attribute
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final ClassFile classFile) {
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
                DefaultMutableTreeNode annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.annotations[i].getStartPos(),
                        this.annotations[i].getLength(),
                        String.format("type_annotation %d", i + 1)
                ));
                annotationsNode.add(annotation);
                this.generateSubnode(annotation, this.annotations[i], classFile);
            }
        }
    }
    
    // 4.7.20, 4.7.21:  The RuntimeTypeAnnotations Attribute
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeRuntimeTypeAnnotations.TypeAnnotation ta, final ClassFile classFile) {

        int startPosMoving = ta.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u1.LENGTH,
                "target_type: 0x" + String.format("%02X", ta.target_type.value)
        )));
        startPosMoving += u1.LENGTH;
        // TODO - Refactor as JTreeNodeFileComponent to set description

        if (ta.union_type_parameter_target != null) {
            DefaultMutableTreeNode type_parameter_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_type_parameter_target.getLength(),
                    "type_parameter_target"));
            rootNode.add(type_parameter_target);
            type_parameter_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "type_parameter_index: " + ta.union_type_parameter_target.type_parameter_index.value
            )));
            startPosMoving += u1.LENGTH;

        } else if (ta.union_supertype_target != null) {
            DefaultMutableTreeNode supertype_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_supertype_target.getLength(),
                    "supertype_target"
            ));
            rootNode.add(supertype_target);
            supertype_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "supertype_index: " + ta.union_supertype_target.supertype_index.value
            )));
            startPosMoving += u2.LENGTH;

        } else if (ta.union_type_parameter_bound_target != null) {
            DefaultMutableTreeNode type_parameter_bound_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_type_parameter_bound_target.getLength(),
                    "type_parameter_bound_target"
            ));
            rootNode.add(type_parameter_bound_target);
            type_parameter_bound_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "type_parameter_index: " + ta.union_type_parameter_bound_target.type_parameter_index.value
            )));
            startPosMoving += u1.LENGTH;
            type_parameter_bound_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "bound_index: " + ta.union_type_parameter_bound_target.bound_index.value
            )));
            startPosMoving += u1.LENGTH;

        } else if (ta.union_empty_target != null) {
            // Do nothing since it is empty
        } else if (ta.union_method_formal_parameter_target != null) {
            DefaultMutableTreeNode formal_parameter_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_method_formal_parameter_target.getLength(),
                    "formal_parameter_target"
            ));
            rootNode.add(formal_parameter_target);
            formal_parameter_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "formal_parameter_index: " + ta.union_method_formal_parameter_target.formal_parameter_index.value
            )));
            startPosMoving += u1.LENGTH;

        } else if (ta.union_throws_target != null) {
            DefaultMutableTreeNode throws_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_throws_target.getLength(),
                    "throws_target"
            ));
            rootNode.add(throws_target);
            throws_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "throws_type_index: " + ta.union_throws_target.throws_type_index.value
            )));
            startPosMoving += u2.LENGTH;

        } else if (ta.union_localvar_target != null) {
            DefaultMutableTreeNode localvar_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_localvar_target.getLength(),
                    "localvar_target"
            ));
            startPosMoving += ta.union_localvar_target.getLength();
            rootNode.add(localvar_target);
            this.generateSubnode(localvar_target, ta.union_localvar_target);

        } else if (ta.union_catch_target != null) {
            DefaultMutableTreeNode catch_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_catch_target.getLength(),
                    "catch_target"
            ));
            rootNode.add(catch_target);
            catch_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "exception_table_index: " + ta.union_catch_target.exception_table_index.value
            )));
            startPosMoving += u2.LENGTH;

        } else if (ta.union_offset_target != null) {
            DefaultMutableTreeNode offset_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_offset_target.getLength(),
                    "offset_target"
            ));
            rootNode.add(offset_target);
            offset_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "offset: " + ta.union_offset_target.offset.value
            )));
            startPosMoving += u2.LENGTH;

        } else if (ta.union_type_argument_target != null) {
            DefaultMutableTreeNode type_argument_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    ta.union_type_argument_target.getLength(),
                    "type_argument_target"
            ));
            rootNode.add(type_argument_target);
            type_argument_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "offset: " + ta.union_type_argument_target.offset.value
            )));
            startPosMoving += u2.LENGTH;
            type_argument_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "type_argument_index: " + ta.union_type_argument_target.type_argument_index.value
            )));
            startPosMoving += u1.LENGTH;
        }

        // target_path
        DefaultMutableTreeNode target_path = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                ta.target_path.getLength(),
                "target_path"
        ));
        startPosMoving += ta.target_path.getLength();
        rootNode.add(target_path);
        this.generateSubnode(target_path, ta.target_path);

        // Annotation
        Annotation.generateSubnode(rootNode, ta, startPosMoving, classFile);
    }
    

    // 4.7.20. localvar_target
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeRuntimeTypeAnnotations.TypeAnnotation.LocalvarTarget lt) {

        int startPosMoving = lt.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "table_length: " + lt.table_length.value
        )));
        startPosMoving += u2.LENGTH;

        if (lt.table == null || lt.table.length < 1) {
            return;
        }

        DefaultMutableTreeNode tables = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                lt.getLength() - u2.LENGTH,
                String.format("table[%d]", lt.table.length)
        ));
        rootNode.add(tables);
        for (int i = 0; i < lt.table.length; i++) {
            startPosMoving = lt.table[i].getStartPos();
            DefaultMutableTreeNode table = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    lt.table[i].getLength(),
                    String.format("table %d", i + 1)
            ));
            tables.add(table);

            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "start_pc: " + lt.table[i].start_pc.value
            )));
            startPosMoving += u2.LENGTH;
            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "length: " + lt.table[i].length.value
            )));
            startPosMoving += u2.LENGTH;
            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH,
                    "index: " + lt.table[i].index.value
            )));
        }

    }

    // 4.7.20, 4.7.21:  The RuntimeTypeAnnotations Attribute
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeRuntimeTypeAnnotations.TypeAnnotation.TypePath tp) {

        int startPosMoving = tp.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u1.LENGTH,
                "path_length: " + tp.path_length.value
        )));
        startPosMoving += u1.LENGTH;

        if (tp.path == null || tp.path.length < 1) {
            return;
        }

        DefaultMutableTreeNode paths = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                tp.getLength() - u1.LENGTH,
                String.format("path[%d]", tp.path.length)
        ));
        rootNode.add(paths);
        for (int i = 0; i < tp.path.length; i++) {
            startPosMoving = tp.path[i].getStartPos();
            DefaultMutableTreeNode path = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    tp.path[i].getLength(),
                    String.format("path[%d]", i + 1)
            ));
            paths.add(path);

            path.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "type_path_kind: " + tp.path[i].type_path_kind.value
            )));
            startPosMoving += u1.LENGTH;
            path.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u1.LENGTH,
                    "type_argument_index: " + tp.path[i].type_argument_index.value
            )));
        }
    }
    
    
    public static class TypeAnnotation extends Annotation {

        public final u1 target_type;
        public final TypeParameterTarget union_type_parameter_target;
        public final SupertypeTarget union_supertype_target;
        public final TypeParameterBoundTarget union_type_parameter_bound_target;
        public final EmptyTarget union_empty_target;
        public final FormalParameterTarget union_method_formal_parameter_target;
        public final ThrowsTarget union_throws_target;
        public final LocalvarTarget union_localvar_target;
        public final CatchTarget union_catch_target;
        public final OffsetTarget union_offset_target;
        public final TypeArgumentTarget union_type_argument_target;
        public final TypePath target_path;

        public TypeAnnotation(PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            super(posDataInputStream, false);

            this.target_type = new u1(posDataInputStream);
            if (this.target_type.value == TargetType.Value00.value
                    || this.target_type.value == TargetType.Value01.value) {
                this.union_type_parameter_target = new TypeParameterTarget(posDataInputStream);
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value10.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = new SupertypeTarget(posDataInputStream);
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value11.value
                    || this.target_type.value == TargetType.Value12.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = new TypeParameterBoundTarget(posDataInputStream);
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value13.value
                    || this.target_type.value == TargetType.Value14.value
                    || this.target_type.value == TargetType.Value15.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = new EmptyTarget(posDataInputStream);
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value16.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = new FormalParameterTarget(posDataInputStream);
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value17.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = new ThrowsTarget(posDataInputStream);
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value40.value
                    || this.target_type.value == TargetType.Value41.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = new LocalvarTarget(posDataInputStream);
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value42.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = new CatchTarget(posDataInputStream);
                this.union_offset_target = null;
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value43.value
                    || this.target_type.value == TargetType.Value44.value
                    || this.target_type.value == TargetType.Value45.value
                    || this.target_type.value == TargetType.Value46.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = new OffsetTarget(posDataInputStream);
                this.union_type_argument_target = null;
            } else if (this.target_type.value == TargetType.Value47.value
                    || this.target_type.value == TargetType.Value48.value
                    || this.target_type.value == TargetType.Value49.value
                    || this.target_type.value == TargetType.Value4A.value
                    || this.target_type.value == TargetType.Value4B.value) {
                this.union_type_parameter_target = null;
                this.union_supertype_target = null;
                this.union_type_parameter_bound_target = null;
                this.union_empty_target = null;
                this.union_method_formal_parameter_target = null;
                this.union_throws_target = null;
                this.union_localvar_target = null;
                this.union_catch_target = null;
                this.union_offset_target = null;
                this.union_type_argument_target = new TypeArgumentTarget(posDataInputStream);
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
            this.target_path = new TypePath(posDataInputStream);
            super.initAnnotation(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }

        public enum TargetType {

            /**
             * type parameter declaration of generic class or interface.
             */
            Value00(0x00),
            /**
             * type parameter declaration of generic method or constructor.
             */
            Value01(0x01),
            /**
             * type in extends or implements clause of class declaration
             * (including the direct superclass or direct superinterface of an
             * anonymous class declaration), or in extends clause of interface
             * declaration.
             */
            Value10(0x10),
            /**
             * type in bound of type parameter declaration of generic class or
             * interface.
             */
            Value11(0x11),
            /**
             * type in bound of type parameter declaration of generic method or
             * constructor.
             */
            Value12(0x12),
            /**
             * type in field declaration.
             */
            Value13(0x13),
            /**
             * return type of method, or type of newly constructed object.
             */
            Value14(0x14),
            /**
             * receiver type of method or constructor.
             */
            Value15(0x15),
            /**
             * type in formal parameter declaration of method, constructor, or
             * lambda expression.
             */
            Value16(0x16),
            /**
             * type in throws clause of method or constructor.
             */
            Value17(0x17),
            /**
             * type in local variable declaration.
             */
            Value40(0x40),
            /**
             * type in resource variable declaration.
             */
            Value41(0x41),
            /**
             * type in exception parameter declaration.
             */
            Value42(0x42),
            /**
             * type in instanceof expression.
             */
            Value43(0x43),
            /**
             * type in new expression.
             */
            Value44(0x44),
            /**
             * type in method reference expression using ::new.
             */
            Value45(0x45),
            /**
             * type in method reference expression using ::Identifier.
             */
            Value46(0x46),
            /**
             * type in cast expression.
             */
            Value47(0x47),
            /**
             * type argument for generic constructor in new expression or
             * explicit constructor invocation statement.
             */
            Value48(0x48),
            /**
             * type argument for generic method in method invocation expression.
             */
            Value49(0x49),
            /**
             * type argument for generic constructor in method reference
             * expression using ::new.
             */
            Value4A(0x4A),
            /**
             * type argument for generic method in method reference expression
             * using ::Identifier.
             */
            Value4B(0x4B);

            /**
             * Internal value of the enum.
             */
            public final short value;

            TargetType(int v) {
                this.value = Integer.valueOf(v).shortValue();
            }

            /**
             * Get union name in <code>type_annotation</code> structure.
             *
             * @param v {@link #value} of {@link TargetType}
             * @return <code>type_annotation</code> structure union name
             */
            public static String getTargetUnionName(short v) {
                String unionName;

                if (v == Value00.value
                        || v == Value01.value) {
                    unionName = TypeParameterTarget.UNION_NAME;
                } else if (v == Value10.value) {
                    unionName = SupertypeTarget.UNION_NAME;
                } else if (v == Value11.value
                        || v == Value12.value) {
                    unionName = TypeParameterBoundTarget.UNION_NAME;
                } else if (v == Value13.value
                        || v == Value14.value
                        || v == Value15.value) {
                    unionName = EmptyTarget.UNION_NAME;
                } else if (v == Value16.value) {
                    unionName = FormalParameterTarget.UNION_NAME;
                } else if (v == Value17.value) {
                    unionName = ThrowsTarget.UNION_NAME;
                } else if (v == Value40.value
                        || v == Value41.value) {
                    unionName = LocalvarTarget.UNION_NAME;
                } else if (v == Value42.value) {
                    unionName = CatchTarget.UNION_NAME;
                } else if (v == Value43.value
                        || v == Value44.value
                        || v == Value45.value
                        || v == Value46.value) {
                    unionName = OffsetTarget.UNION_NAME;
                } else if (v == Value47.value
                        || v == Value48.value
                        || v == Value49.value
                        || v == Value4A.value
                        || v == Value4B.value) {
                    unionName = TypeArgumentTarget.UNION_NAME;
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
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20.1">
         * VM Spec: The target_info union - type_parameter_target
         * </a>
         */
        public final static class TypeParameterTarget extends FileComponent {

            public static final String UNION_NAME = "type_parameter_target";

            /**
             * The value of the type_parameter_index item specifies which type
             * parameter declaration is annotated. A type_parameter_index value
             * of 0 specifies the first type parameter declaration.
             */
            public transient final u1 type_parameter_index;

            protected TypeParameterTarget(final PosDataInputStream posDataInputStream) throws IOException {
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
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20.1">
         * VM Spec: The target_info union - supertype_target
         * </a>
         */
        public final static class SupertypeTarget extends FileComponent {

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
            public transient final u2 supertype_index;

            protected SupertypeTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.supertype_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class TypeParameterBoundTarget extends FileComponent {

            public static final String UNION_NAME = "type_parameter_bound_target";
            public transient final u1 type_parameter_index;
            public transient final u1 bound_index;

            protected TypeParameterBoundTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.type_parameter_index = new u1(posDataInputStream);
                this.bound_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class EmptyTarget extends FileComponent {

            public static final String UNION_NAME = "empty_target";

            protected EmptyTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                super.length = 0;
            }
        }

        public final static class FormalParameterTarget extends FileComponent {

            public static final String UNION_NAME = "method_formal_parameter_target";
            public transient final u1 formal_parameter_index;

            protected FormalParameterTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.formal_parameter_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class ThrowsTarget extends FileComponent {

            public static final String UNION_NAME = "throws_target";
            public transient final u2 throws_type_index;

            protected ThrowsTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.throws_type_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class LocalvarTarget extends FileComponent {

            public static final String UNION_NAME = "localvar_target";
            public transient final u2 table_length;
            public transient final Table[] table;

            protected LocalvarTarget(final PosDataInputStream posDataInputStream) throws IOException {
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

            public final static class Table extends FileComponent {

                public transient final u2 start_pc;
                public transient final u2 length;
                public transient final u2 index;

                protected Table(final PosDataInputStream posDataInputStream) throws IOException {
                    super.startPos = posDataInputStream.getPos();
                    this.start_pc = new u2(posDataInputStream);
                    this.length = new u2(posDataInputStream);
                    this.index = new u2(posDataInputStream);
                    super.length = posDataInputStream.getPos() - super.startPos;
                }

            }
        }

        public final static class CatchTarget extends FileComponent {

            public static final String UNION_NAME = "catch_target";
            public transient final u2 exception_table_index;

            protected CatchTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.exception_table_index = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class OffsetTarget extends FileComponent {

            public static final String UNION_NAME = "offset_target";
            public transient final u2 offset;

            protected OffsetTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class TypeArgumentTarget extends FileComponent {

            public static final String UNION_NAME = "type_argument_target";
            public transient final u2 offset;
            public transient final u1 type_argument_index;

            protected TypeArgumentTarget(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset = new u2(posDataInputStream);
                this.type_argument_index = new u1(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final static class TypePath extends FileComponent {

            public transient final u1 path_length;
            public final Path[] path;

            protected TypePath(final PosDataInputStream posDataInputStream) throws IOException {
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

            public final static class Path extends FileComponent {

                public transient final u1 type_path_kind;
                public transient final u1 type_argument_index;

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
                Value0(0, "Annotation is deeper in an array type"),
                /**
                 * Annotation is deeper in a nested type.
                 */
                Value1(1, "Annotation is deeper in a nested type"),
                /**
                 * Annotation is on the bound of a wildcard type argument of a
                 * parameterized type.
                 */
                Value2(2, "Annotation is on the bound of a wildcard type argument of a parameterized type"),
                /**
                 * Annotation is on a type argument of a parameterized type.
                 */
                Value3(3, "Annotation is on a type argument of a parameterized type");

                /**
                 * Internal value of the type path kind.
                 */
                public final short value;
                /**
                 * Description of the type path kind.
                 */
                public final String description;

                TypePathKind(int v, String d) {
                    this.value = Integer.valueOf(v).shortValue();
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
