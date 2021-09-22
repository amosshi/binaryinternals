/*
 * ConstantDynamicInfo.java    May 17, 2019
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

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
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_Dynamic_info extends cp_info {

    public static final int LENGTH = 5;

    /**
     * {@code CONSTANT_Dynamic_info} structures are unique in that they are
     * syntactically allowed to refer to themselves via the bootstrap method
     * table. Rather than mandating that such cycles are detected when classes
     * are loaded (a potentially expensive check), we permit cycles initially
     * but mandate a failure at resolution.
     */
    public final u2 bootstrap_method_attr_index;

    /**
     * The value of the {@code name_and_type_index} item must be a valid index
     * into the {@code constant_pool} table. The {@code constant_pool} entry at
     * that index must be a {@code CONSTANT_NameAndType_info} structure
     * representing a method name and method descriptor.
     *
     * In a {@code CONSTANT_Dynamic_info} structure, the indicated descriptor
     * must be a field descriptor.
     */
    public final u2 name_and_type_index;

    CONSTANT_Dynamic_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_Dynamic.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        this.bootstrap_method_attr_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
        super.length = LENGTH;
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

    @Override
    public String getMessageKey() {
        return "msg_const_dynamic";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Dynamic.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return String.format("bootstrap_method_attr_index=%d name_and_type_index=%s",
                this.bootstrap_method_attr_index.value,
                constantPool[this.name_and_type_index.value].toString(constantPool));
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        // TODO - Find a test case to verify this chagne is working or not
        System.out.println("Congratulations. We verified the tree ndoe for ConstantDynamicInfo is working. We can delete this log output now.");

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "bootstrap_method_attr_index: " + this.bootstrap_method_attr_index.value,
                MESSAGES.getString("msg_const_dynamic_bootstrap_method_attr_index")
        )));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + this.name_and_type_index.value + " - " + ((ClassFile)classFile).getCPDescription(this.name_and_type_index.value),
                Icons.Offset,
                MESSAGES.getString("msg_const_dynamic_name_and_type_index")
        )));
    }
}
