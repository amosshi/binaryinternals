/*
 * ConstantMethodrefInfo.java    4:34 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.ClassFile;

/**
 * The class for the {@code CONSTANT_Methodref_info} structure in constant pool.
 * The {@code CONSTANT_Methodref_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Methodref_info {
 *        u1 tag;
 *
 *        u2 class_index;
 *        u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.2">
 * VM Spec: The CONSTANT_Methodref_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_Methodref_info extends CONSTANT_Ref {

    CONSTANT_Methodref_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Methodref.tag, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_ref";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Methodref.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return super.toString4Method(constantPool);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        super.generateTreeNode(parentNode,
                (ClassFile) format,
                "class name"
        );
    }
}
