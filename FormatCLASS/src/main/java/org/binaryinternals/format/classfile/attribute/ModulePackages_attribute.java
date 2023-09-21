/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.constant.CONSTANT_Package_info;
import org.binaryinternals.format.classfile.u2;

/**
 *
 * The class for the {@code ModulePackages} attribute. The
 * {@code ModulePackages} attribute has the following format:
 *
 * <pre>
 *    ModulePackages_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 package_count;
 *        u2 package_index[package_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 9
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.26">
 * VM Spec: The ModulePackages Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class ModulePackages_attribute extends attribute_info {

    /**
     * The value of the package_count item indicates the number of entries in
     * the {@link #package_index}table.
     */
    public final u2 package_count;

    /**
     * The value of each entry in the {@link #package_index} table must be a
     * valid index into the {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that index must be a
     * {@link CONSTANT_Package_info} structure representing a package in the
     * current module.
     */
    public final u2[] package_index;

    ModulePackages_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.package_count = new u2(posDataInputStream);
        if (this.package_count.value > 0) {
            this.package_index = new u2[this.package_count.value];
            for (int i = 0; i < this.package_count.value; i++) {
                this.package_index[i] = new u2(posDataInputStream);
            }
        } else {
            this.package_index = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos + 6;

        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "package_count", this.package_count.value,
                "msg_attr_ModulePackages__package_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.package_count.value > 0) {
            final DefaultMutableTreeNode packageIndexesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * this.package_count.value,
                    String.format("package_index [%d]", this.package_count.value),
                    MESSAGES.getString("msg_attr_ModulePackages__package_index")
            ));
            parentNode.add(packageIndexesNode);

            for (int i = 0; i < this.package_index.length; i++) {
                int packageIndex = this.package_index[i].value;
                this.addNode(packageIndexesNode,
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        String.format("package_index %d", i + 1),
                        String.format(TEXT_CPINDEX_VALUE, packageIndex, "package", ((ClassFile)classFile).getCPDescription(packageIndex)),
                        "msg_attr_ModulePackages__package_index",
                        Icons.Package
                );
            }
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_ModulePackages";
    }
}
