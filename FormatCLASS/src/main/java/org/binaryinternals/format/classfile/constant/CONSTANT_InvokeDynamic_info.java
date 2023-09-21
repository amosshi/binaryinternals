/*
 * ConstantInvokeDynamicInfo.java    12:44 AM, April 28, 2014
 *
 * Copyright 2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;

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
 *
 *      u2 bootstrap_method_attr_index;
 *      u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.4.10">
 * VM Spec: he CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info Structures
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101"})
public class CONSTANT_InvokeDynamic_info extends CONSTANT_Dynamic {

    CONSTANT_InvokeDynamic_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_InvokeDynamic.tag, posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_InvokeDynamic.name();
    }
}