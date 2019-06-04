/*
 * ConstantInvokeDynamicInfo.java    12:44 AM, April 28, 2014
 *
 * Copyright 2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code CONSTANT_InvokeDynamic_info} structure is used by an
 * {@code invokedynamic} instruction to specify a bootstrap method, the dynamic
 * invocation name, the argument and return types of the call, and optionally, a
 * sequence of additional constants called static arguments to the bootstrap
 * method.
 *
 * <pre>
 *    CONSTANT_InvokeDynamic_info {
 *      u1 tag;
 *      u2 bootstrap_method_attr_index;
 *      u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.10">
 * VM Spec: The CONSTANT_InvokeDynamic_info Structure
 * </a>
 */
public class ConstantInvokeDynamicInfo extends CPInfo {

    public static final int LENGTH = 5;
    /**
     * The value of the {@code bootstrap_method_attr_index} item must be a valid
     * index into the {@code bootstrap_methods} array of the bootstrap method
     * table of this class file.
     *
     */
    public final u2 bootstrap_method_attr_index;
    /**
     * The value of the {@code name_and_type_index} item must be a valid index
     * into the {@code constant_pool} table. The {@code constant_pool} entry at
     * that index must be a {@code CONSTANT_NameAndType_info} structure
     * representing a method name and method descriptor.
     */
    public final u2 name_and_type_index;

    ConstantInvokeDynamicInfo(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(CPInfo.ConstantType.CONSTANT_InvokeDynamic.tag, false, ClassFile.Version.Format_51_0, JavaSEVersion.Version_7);
        super.startPos = posDataInputStream.getPos() - 1;
        this.bootstrap_method_attr_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
        super.length = LENGTH;
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_InvokeDynamic.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], bootstrap_method_attr_index: [%d], name_and_type_index: [%d]. ",
                this.getName(),
                this.startPos,
                super.length,
                this.bootstrap_method_attr_index.value,
                this.name_and_type_index.value);
    }
}
