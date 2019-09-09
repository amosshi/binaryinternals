/*
 * AttributeEnclosingMethod.java    10:48 AM, April 28, 2014
 *
 * Copyright  2004, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.7">
 * VM Spec: The EnclosingMethod Attribute
 * </a>
 */
public class AttributeEnclosingMethod extends AttributeInfo {

    /**
     * Representing the innermost class that encloses the declaration of the
     * current class.
     */
    public transient final u2 class_index;
    /**
     * Representing the name and type of a method in the class referenced by the
     * {@link #class_index} attribute above.
     */
    public transient final u2 method_index;

    AttributeEnclosingMethod(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_49_0, JavaSEVersion.Version_5_0);

        this.class_index = new u2(posDataInputStream);
        this.method_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        int startPosMoving = super.startPos + 6;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "class_index: " + this.class_index.value + " - " + classFile.getCPDescription(this.class_index.value)
        )));
        startPosMoving += u2.LENGTH;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "method_index: " + this.method_index.value + " - " + classFile.getCPDescription(this.method_index.value)
        )));
    }
}
