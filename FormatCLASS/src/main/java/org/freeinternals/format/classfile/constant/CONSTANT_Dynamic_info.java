/*
 * ConstantDynamicInfo.java    May 17, 2019
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The {@code CONSTANT_Dynamic_info} structure is used to represent a
 * dynamically-computed constant, an arbitrary value that is produced by
 * invocation of a bootstrap method in the course of an {@code ldc} instruction,
 * among others. The auxiliary type specified by the structure constrains the
 * type of the dynamically-computed constant.
 *
 * <pre>
 *    CONSTANT_Dynamic_info {
 *      u1 tag;
 *
 *      u2 bootstrap_method_attr_index;
 *      u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.10">
 * VM Spec: he CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info Structures
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101"})
public class CONSTANT_Dynamic_info extends CONSTANT_Dynamic {

    CONSTANT_Dynamic_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_Dynamic.tag, posDataInputStream);

        // TODO - Find a test case to verify this chagne is working or not
        System.out.println("Congratulations. We verified the tree ndoe for ConstantDynamicInfo is working. We can delete this log output now.");
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Dynamic.name();
    }
}
