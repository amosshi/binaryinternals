/*
 * ConstantInterfaceMethodrefInfo.java    4:35 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;

/**
 * The class for the {@code CONSTANT_InterfaceMethodref_info} structure in
 * constant pool. The {@code CONSTANT_InterfaceMethodref_info} structure has the
 * following format:
 *
 * <pre>
 *    CONSTANT_InterfaceMethodref_info {
 *        u1 tag;
 *
 *        u2 class_index;
 *        u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.2">
 * VM Spec: The CONSTANT_InterfaceMethodref_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_InterfaceMethodref_info extends CONSTANT_Ref {

    CONSTANT_InterfaceMethodref_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_InterfaceMethodref.tag, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_ref";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_InterfaceMethodref.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return super.toString4Method(constantPool);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        super.generateTreeNode(parentNode,
                (ClassFile) format,
                "interface name"
        );
    }
}
