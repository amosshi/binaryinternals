/*
 * AttributeBootstrapMethods.java    11:41 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code BootstrapMethods} attribute is a variable-length attribute in the
 * {@code attributes} table of a {@code ClassFile} structure. The
 * {@code BootstrapMethods} attribute records bootstrap method specifiers
 * referenced by {@code invokedynamic} instructions.
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.23">
 * VM Spec: The BootstrapMethods Attribute
 * </a>
 */
public class AttributeBootstrapMethods extends AttributeInfo {

    /**
     * Determines the number of bootstrap method specifiers in the
     * {@link #bootstrap_methods} array.
     */
    public transient final u2 num_bootstrap_methods;

    /**
     * Each entry in the {@link #bootstrap_methods} table contains an index to a
     * {@link ConstantMethodHandleInfo} structure which specifies a bootstrap
     * method, and a sequence (perhaps empty) of indexes to static arguments for
     * the bootstrap method.
     */
    public transient final BootstrapMethod[] bootstrap_methods;

    AttributeBootstrapMethods(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

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

    public final class BootstrapMethod extends FileComponent {

        /**
         * The value of the {@link #bootstrap_method_ref} item must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be a
         * {@link ConstantMethodHandleInfo} structure
         */
        public transient final u2 bootstrap_method_ref;

        /**
         * Gives the number of items in the {@link #bootstrap_arguments} array.
         */
        public transient final u2 num_bootstrap_arguments;

        /**
         * Each entry in the {@link #bootstrap_arguments} array must be a valid
         * index into the {@link ClassFile#constant_pool} table, the
         * <code>constant_pool</code> entry at that index must be a {@link ConstantStringInfo}, {@link ConstantClassInfo},
         * {@link ConstantIntegerInfo}, {@link ConstantLongInfo},
         * {@link ConstantFloatInfo}, {@link ConstantDoubleInfo},
         * {@link ConstantMethodHandleInfo}, or {@link ConstantMethodTypeInfo}
         * structure.
         */
        public transient final u2[] bootstrap_arguments;

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
