/*
 * AttributeSourceFile.java    5:26 AM, August 5, 2007
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
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code SourceFile} attribute. The {@code SourceFile}
 * attribute has the following format:
 *
 * <pre>
 *    SourceFile_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 sourcefile_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.10">
 * VM Spec: The SourceFile Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class SourceFile_attribute extends attribute_info {

    public final u2 sourcefile_index;

    SourceFile_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 2) {
            throw new FileFormatException(String.format("The attribute_length of AttributeSourceFile is not 2, it is %d.", this.attribute_length.value));
        }

        this.sourcefile_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int cpIndex = this.sourcefile_index.value;
        this.addNode(parentNode,
                super.startPos + 6, u2.LENGTH,
                "sourcefile_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "source file", ((ClassFile)classFile).getCPDescription(cpIndex)),
                "msg_attr_SourceFile__sourcefile_index", Icons.Name
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_SourceFile";
    }
}
