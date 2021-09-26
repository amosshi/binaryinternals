/*
 * CONSTANT_Dynamic.java    Sep 25, 2021
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * Parent of The {@code CONSTANT_Dynamic_info} and
 * {@code CONSTANT_InvokeDynamic_info} structures.
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
public abstract class CONSTANT_Dynamic extends cp_info {

    public static final int LENGTH = 5;

    /**
     * The value of the {@link #name_and_type_index} item must be a valid index
     * into the bootstrap_methods array of the bootstrap method table of this
     * class file.
     */
    public final u2 bootstrap_method_attr_index;

    /**
     * The value of the {@link #name_and_type_index} item must be a valid index
     * into the {@link ClassFile#constant_pool} table. The {@code constant_pool}
     * entry at that index must be a {@code CONSTANT_NameAndType_info} structure
     * representing a method name and method descriptor.
     *
     * In a {@code CONSTANT_Dynamic_info} structure, the indicated descriptor
     * must be a field descriptor.
     */
    public final u2 name_and_type_index;

    CONSTANT_Dynamic(final short tag, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(tag);

        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.bootstrap_method_attr_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
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
    public String toString(cp_info[] constantPool) {
        return String.format("bootstrap_method_attr_index=%d name_and_type_index=%s",
                this.bootstrap_method_attr_index.value,
                constantPool[this.name_and_type_index.value].toString(constantPool));
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                startPos + 1,
                2,
                "bootstrap_method_attr_index",
                this.bootstrap_method_attr_index.value,
                "msg_const_dynamic_bootstrap_method_attr_index",
                Icons.Index
        );

        final int cpIndex = this.name_and_type_index.value;
        this.addNode(parentNode,
                startPos + 3,
                2,
                "name_and_type_index",
                String.format(TEXT_CPINDEX_PUREVALUE, cpIndex, ((ClassFile)classFile).getCPDescription(cpIndex)),
                "msg_const_dynamic_name_and_type_index",
                Icons.Offset
        );
    }
}
