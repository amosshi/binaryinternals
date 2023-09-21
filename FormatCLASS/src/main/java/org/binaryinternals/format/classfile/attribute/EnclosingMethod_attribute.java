/*
 * AttributeEnclosingMethod.java    10:48 AM, April 28, 2014
 *
 * Copyright  2004, BinaryInternals.org. All rights reserved.
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
 * An optional fixed-length attribute in the attributes table of a
 * {@code ClassFile} structure. A {@code class} must have an
 * {@code EnclosingMethod} attribute if and only if it is a local class or an
 * anonymous class. A A {@code class} may have no more than one
 * {@code EnclosingMethod} attribute.
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.7">
 * VM Spec: The EnclosingMethod Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class EnclosingMethod_attribute extends attribute_info {

    /**
     * Representing the innermost class that encloses the declaration of the
     * current class.
     */
    public final u2 class_index;
    /**
     * If the current class is not immediately enclosed by a method or
     * constructor, then the value of the <code>method_index</code> item must be
     * zero. Otherwise, the value of the <code>method_index</code> item must be
     * a valid index into the <code>constant_pool</code> table. The
     * <code>constant_pool</code> entry at that index must be a
     * <code>CONSTANT_NameAndType_info</code> structure representing the name
     * and type of a method in the class referenced by the {@link #class_index}
     * attribute above.
     */
    public final u2 method_index;

    EnclosingMethod_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.class_index = new u2(posDataInputStream);
        this.method_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        ClassFile classFile = (ClassFile) format;
        int startPosMoving = super.startPos + 6;

        int cpIndex = this.class_index.value;
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "class_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "class", classFile.getCPDescription(cpIndex)),
                "msg_attr_EnclosingMethod__class_index", Icons.Index
        );
        startPosMoving += u2.LENGTH;

        final String methodDesc = (this.method_index.value == 0) ? "" : classFile.getCPDescription(this.method_index.value);
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "method_index", String.format(TEXT_CPINDEX_VALUE, this.method_index.value, "method", methodDesc),
                "msg_attr_EnclosingMethod__method_index", Icons.Index
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_EnclosingMethod";
    }
}
