/*
 * AttributeMethodParameters.java    5:37 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.AccessFlag;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code MethodParameters} attribute. The
 * {@code MethodParameters} attribute has the following format:
 *
 * Parameter names aren’t compiled into the byte code by default. Hence we have
 * to add a compiler switch to enable named parameters:
 * <pre>
 *   javac -parameters
 * </pre>
 *
 * The MethodParameters attribute has the following format:
 * <pre>
 *    MethodParameters_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u1 parameters_count;
 *        {   u2 name_index;
 *            u2 access_flags;
 *        } parameters[parameters_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 8.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.24">
 * VM Spec: The MethodParameters Attribute
 * </a>
 */
public class AttributeMethodParameters extends AttributeInfo {

    /**
     * The value of the parameters_count item indicates the number of parameter
     * descriptors in the method descriptor referenced by the descriptor_index
     * of the attribute's enclosing method_info structure.
     */
    public transient final u1 parameters_count;
    public transient final Parameter[] parameters;

    AttributeMethodParameters(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_52_0, JavaSEVersion.Version_8);

        this.parameters_count = new u1(posDataInputStream, true);
        if (this.parameters_count.value > 0) {
            this.parameters = new Parameter[this.parameters_count.value];
            for (int i = 0; i < this.parameters_count.value; i++) {
                this.parameters[i] = new Parameter(posDataInputStream);
            }
        } else {
            this.parameters = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    public final class Parameter extends FileComponent {

        /**
         * The value of the name_index item must either be zero or a valid index
         * into the constant_pool table. If the value of the name_index item is
         * zero, then this parameters element indicates a formal parameter with
         * no name. If the value of the name_index item is nonzero, the
         * constant_pool entry at that index must be a CONSTANT_Utf8_info
         * structure representing a valid unqualified name denoting a formal
         * parameter.
         */
        public transient final u2 name_index;
        /**
         * The value of the access_flags item is as follows: 0x0010 (ACC_FINAL),
         * 0x1000 (ACC_SYNTHETIC), 0x8000 (ACC_MANDATED).
         *
         * @see AccessFlag#ForMethodParameters
         */
        public transient final u2 access_flags;

        private Parameter(final PosDataInputStream posDataInputStream) throws IOException {
            super.startPos = posDataInputStream.getPos();
            this.name_index = new u2(posDataInputStream);
            this.access_flags = new u2(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }

        public String getAccessFlagsText() {
            return AccessFlag.getMethodParametersModifier(this.access_flags.value);
        }
    }

}
