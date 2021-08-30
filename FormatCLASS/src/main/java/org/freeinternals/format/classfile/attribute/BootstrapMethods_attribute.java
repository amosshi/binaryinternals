/*
 * AttributeBootstrapMethods.java    11:41 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.constant.CONSTANT_MethodHandle_info;
import org.freeinternals.format.classfile.constant.cp_info.ConstantType;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code BootstrapMethods} attribute is a variable-length attribute in the
 * {@code attributes} table of a {@code ClassFile} structure. The
 * {@code BootstrapMethods} attribute records bootstrap method specifiers
 * referenced by {@code invokedynamic} instructions.
 *
 * <pre>
 * BootstrapMethods_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 num_bootstrap_methods;
 *     {   u2 bootstrap_method_ref;
 *         u2 num_bootstrap_arguments;
 *         u2 bootstrap_arguments[num_bootstrap_arguments];
 *     } bootstrap_methods[num_bootstrap_methods];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 7
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.23">
 * VM Spec: The BootstrapMethods Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class BootstrapMethods_attribute extends attribute_info {

    /**
     * Determines the number of bootstrap method specifiers in the
     * {@link #bootstrap_methods} array.
     */
    public final u2 num_bootstrap_methods;

    /**
     * Each entry in the {@link #bootstrap_methods} table contains an index to a
     * {@link CONSTANT_MethodHandle_info} structure which specifies a bootstrap
     * method, and a sequence (perhaps empty) of indexes to static arguments for
     * the bootstrap method.
     */
    public final bootstrap_method[] bootstrap_methods;

    BootstrapMethods_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.num_bootstrap_methods = new u2(posDataInputStream);
        if (this.num_bootstrap_methods.value > 0) {
            this.bootstrap_methods = new bootstrap_method[this.num_bootstrap_methods.value];
            for (int i = 0; i < this.num_bootstrap_methods.value; i++) {
                this.bootstrap_methods[i] = new bootstrap_method(posDataInputStream);
            }
        } else {
            this.bootstrap_methods = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat classFile) {
        int startPosMoving = this.getStartPos();

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                2,
                "num_bootstrap_methods: " + this.num_bootstrap_methods.value
        )));

        if (this.bootstrap_methods != null && this.bootstrap_methods.length > 0) {
            DefaultMutableTreeNode bootstrapMethodsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    "bootstrap_methods"));
            parentNode.add(bootstrapMethodsNode);

            for (int i = 0; i < this.bootstrap_methods.length; i++) {
                bootstrap_method m = this.bootstrap_methods[i];
                DefaultMutableTreeNode bootstrapMethod = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        m.getStartPos(),
                        m.getLength(),
                        String.format("bootstrap_method %d", i + 1)
                ));
                bootstrapMethodsNode.add(bootstrapMethod);
                this.generateSubnode(bootstrapMethod, m, (ClassFile)classFile);
            }
        }
    }

    /**
     * Generate tree node for {@link bootstrap_method}.
     */
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final bootstrap_method m, final ClassFile classFile) {

        int startPosMoving = m.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "bootstrap_method_ref: " + m.bootstrap_method_ref.value + " - " + classFile.getCPDescription(m.bootstrap_method_ref.value)
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "num_bootstrap_arguments: " + m.num_bootstrap_arguments.value
        )));
        startPosMoving += u2.LENGTH;

        if (m.bootstrap_arguments != null && m.bootstrap_arguments.length > 0) {
            DefaultMutableTreeNode bootstrapArguments = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    m.bootstrap_arguments.length * u2.LENGTH,
                    "bootstrap_arguments"));
            rootNode.add(bootstrapArguments);

            for (int i = 0; i < m.bootstrap_arguments.length; i++) {
                DefaultMutableTreeNode bootstrapMethod = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "argument " + (i + 1) + ": " + m.bootstrap_arguments[i].value + " - " + classFile.getCPDescription(m.bootstrap_arguments[i].value)
                ));
                startPosMoving += u2.LENGTH;
                bootstrapArguments.add(bootstrapMethod);
            }
        }
    }

    /**
     *
     * <pre>
     * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
     * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings({"java:S101", "java:S116"})
    public static final class bootstrap_method extends FileComponent {

        /**
         * The value of the {@link #bootstrap_method_ref} item must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be a
         * {@link CONSTANT_MethodHandle_info} structure
         */
        public final u2 bootstrap_method_ref;

        /**
         * Gives the number of items in the {@link #bootstrap_arguments} array.
         */
        public final u2 num_bootstrap_arguments;

        /**
         * Each entry in the {@link #bootstrap_arguments} array must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be loadable (
         * {@link ConstantType#loadable} is <code>true</code>) .
         */
        public final u2[] bootstrap_arguments;

        private bootstrap_method(final PosDataInputStream posDataInputStream) throws IOException {
            super.startPos = posDataInputStream.getPos();

            this.bootstrap_method_ref = new u2(posDataInputStream);
            this.num_bootstrap_arguments = new u2(posDataInputStream);
            if (this.num_bootstrap_arguments.value > 0) {
                this.bootstrap_arguments = new u2[this.num_bootstrap_arguments.value];
                for (int i = 0; i < this.num_bootstrap_arguments.value; i++) {
                    this.bootstrap_arguments[i] = new u2(posDataInputStream);
                }
            } else {
                this.bootstrap_arguments = null;
            }

            super.length = posDataInputStream.getPos() - super.startPos;
        }
    }

}
