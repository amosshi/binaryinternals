/*
 * ConstantInterfaceMethodrefInfo.java    4:35 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.classfile.ClassFile;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
 * VM Spec: The CONSTANT_InterfaceMethodref_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_InterfaceMethodref_info extends CONSTANT_Ref_info {

    CONSTANT_InterfaceMethodref_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_InterfaceMethodref.tag, posDataInputStream);
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
        ClassFile classFile = (ClassFile) format;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "class_index: " + this.class_index.value + " - " + classFile.getCPDescription(this.class_index.value),
                UITool.icon4Offset(),
                MESSAGES.getString("msg_const_ref_class_index")
        )));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + this.name_and_type_index.value + " - " + classFile.getCPDescription(this.name_and_type_index.value),
                UITool.icon4Offset(),
                MESSAGES.getString("msg_const_ref_name_and_type_index")
        )));
    }
}
