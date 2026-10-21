/*
 * ConstantDynamicInfo.java    May 17, 2019
 *
 * Copyright 2019, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The `CONSTANT_Dynamic_info` structure is used to represent a
/// dynamically-computed constant, an arbitrary value that is produced by
/// invocation of a bootstrap method in the course of an `ldc` instruction,
/// among others. The auxiliary type specified by the structure constrains the
/// type of the dynamically-computed constant.
///
/// ```
///    CONSTANT_Dynamic_info {
///      u1 tag;
///
///      u2 bootstrap_method_attr_index;
///      u2 name_and_type_index;
///    }
/// ```
///
/// @author Amos Shi
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.10">
/// VM Spec: he CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info Structures
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101"})
public class CONSTANT_Dynamic_info extends CONSTANT_Dynamic {

    CONSTANT_Dynamic_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Dynamic.tag, posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Dynamic.name();
    }
}
