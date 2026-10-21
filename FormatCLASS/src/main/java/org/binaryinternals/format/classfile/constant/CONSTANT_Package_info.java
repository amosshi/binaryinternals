/*
 * ConstantPackageInfo.java    00:19 AM, July 19, 2018
 *
 * Copyright  2018, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.u2;

/// The class for the `CONSTANT_Package_info` structure in constant pool.
/// The `CONSTANT_Package_info` structure has the following format:
///
/// ```
///    CONSTANT_Package_info {
///        u1 tag;
///
///        u2 name_index;
///    }
/// ```
///
/// @author Amos Shi
/// @since Java 9
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.12">
/// VM Spec: The CONSTANT_Package_info Structure
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_Package_info extends cp_info {

    public static final int LENGTH = 3;

    /// The value of the {@link name_index} item must be a valid index into the
    /// `constant_pool` table. The constant_pool entry at that index must
    /// be a {@link CONSTANT_Utf8_info} structure representing a valid package
    /// name encoded in internal form.
    public final u2 name_index;

    CONSTANT_Package_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Package.tag, posDataInputStream.getPos() - 1, LENGTH);

        this.name_index = new u2(posDataInputStream);
    }

    @Override
    public String getDescription() {
        return "%s: Start Position: [%d], length: [%d], value: name_index=[%d].".formatted(
                this.getName(), this.startPos, this.length, this.name_index.value);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_package";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Package.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return ((CONSTANT_Utf8_info) constantPool[this.name_index.value]).getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int cpIndex = this.name_index.value;
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "name_index",
                TEXT_CPINDEX_VALUE.formatted(cpIndex, "package name", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_const_package_name_index",
                Icons.Name
        );
    }
}
