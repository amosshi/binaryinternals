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
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code BootstrapMethods} attribute is a variable-length attribute in the
 * {@code attributes} table of a {@code ClassFile} structure. The
 * {@code BootstrapMethods} attribute records bootstrap method specifiers
 * referenced by {@code invokedynamic} instructions.
 *
 * @author Amos Shi
 * @since Java 7
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.23">
 * VM Spec: The BootstrapMethods Attribute
 * </a>
 */
@SuppressWarnings("java:S116") // Class variable fields should not have public accessibility --> No, we like the simplifed final value manner
public class AttributeBootstrapMethods extends AttributeInfo {

    /**
     * Determines the number of bootstrap method specifiers in the
     * {@link #bootstrap_methods} array.
     */
    public final u2 num_bootstrap_methods;

    /**
     * Each entry in the {@link #bootstrap_methods} table contains an index to a
     * {@link org.freeinternals.format.classfile.constant.ConstantMethodHandleInfo}
     * structure which specifies a bootstrap method, and a sequence (perhaps
     * empty) of indexes to static arguments for the bootstrap method.
     */
    public final BootstrapMethod[] bootstrap_methods;

    AttributeBootstrapMethods(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.FORMAT_51_0, JavaSEVersion.VERSION_7);

        this.num_bootstrap_methods = new u2(posDataInputStream);
        if (this.num_bootstrap_methods.value > 0) {
            this.bootstrap_methods = new BootstrapMethod[this.num_bootstrap_methods.value];
            for (int i = 0; i < this.num_bootstrap_methods.value; i++) {
                this.bootstrap_methods[i] = new BootstrapMethod(posDataInputStream);
            }
        } else {
            this.bootstrap_methods = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final ClassFile classFile) {
        int startPosMoving = this.getStartPos();

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                2,
                "num_bootstrap_methods: " + this.num_bootstrap_methods.value
        )));

        if (this.bootstrap_methods != null && this.bootstrap_methods.length > 0) {
            DefaultMutableTreeNode bootstrap_methods_node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    "bootstrap_methods"));
            parentNode.add(bootstrap_methods_node);

            for (int i = 0; i < this.bootstrap_methods.length; i++) {
                BootstrapMethod m = this.bootstrap_methods[i];
                DefaultMutableTreeNode bootstrap_method = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        m.getStartPos(),
                        m.getLength(),
                        String.format("bootstrap_method %d", i + 1)
                ));
                bootstrap_methods_node.add(bootstrap_method);
                this.generateSubnode(bootstrap_method, m, classFile);
            }
        } 
    }
    
    /**
     * Generate tree node for {@link BootstrapMethod}.
     */
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final BootstrapMethod m, final ClassFile classFile) {

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
            DefaultMutableTreeNode bootstrap_arguments = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    m.bootstrap_arguments.length * u2.LENGTH,
                    "bootstrap_arguments"));
            rootNode.add(bootstrap_arguments);

            for (int i = 0; i < m.bootstrap_arguments.length; i++) {
                DefaultMutableTreeNode bootstrap_method = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH,
                        "argument " + (i + 1) + ": " + m.bootstrap_arguments[i].value + " - " + classFile.getCPDescription(m.bootstrap_arguments[i].value)
                ));
                startPosMoving += u2.LENGTH;
                bootstrap_arguments.add(bootstrap_method);
            }
        }
    }
    
    public static final class BootstrapMethod extends FileComponent {

        /**
         * The value of the {@link #bootstrap_method_ref} item must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be a
         * {@link org.freeinternals.format.classfile.constant.ConstantMethodHandleInfo}
         * structure
         */
        public final u2 bootstrap_method_ref;

        /**
         * Gives the number of items in the {@link #bootstrap_arguments} array.
         */
        public final u2 num_bootstrap_arguments;

        /**
         * Each entry in the {@link #bootstrap_arguments} array must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be a {@link org.freeinternals.format.classfile.constant.ConstantStringInfo}, {@link org.freeinternals.format.classfile.constant.ConstantClassInfo},
         * {@link org.freeinternals.format.classfile.constant.ConstantIntegerInfo}, {@link org.freeinternals.format.classfile.constant.ConstantLongInfo},
         * {@link org.freeinternals.format.classfile.constant.ConstantFloatInfo}, {@link org.freeinternals.format.classfile.constant.ConstantDoubleInfo},
         * {@link org.freeinternals.format.classfile.constant.ConstantMethodHandleInfo},
         * or
         * {@link org.freeinternals.format.classfile.constant.ConstantMethodTypeInfo}
         * structure.
         */
        public final u2[] bootstrap_arguments;

        private BootstrapMethod(final PosDataInputStream posDataInputStream) throws IOException {
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
