/*
 * AttributeConstantValue.java    5:08 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code ConstantValue} attribute. The {@code ConstantValue}
 * attribute has the following format:
 *
 * <pre>
 *    ConstantValue_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 constantvalue_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.2">
 * VM Spec: The ConstantValue Attribute
 * </a>
 */
public class AttributeConstantValue extends AttributeInfo {

    /**
     * The {@link ClassFile#constant_pool} entry at that
     * {@link #constantvalue_index} gives the constant value represented by this
     * attribute.
     */
    public final u2 constantvalue_index;

    AttributeConstantValue(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2);

        if (this.attribute_length.value != 2) {
            throw new FileFormatException(String.format("The attribute_length of AttributeConstantValue is not 2, it is %d.", this.attribute_length.value));
        }
        this.constantvalue_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        int index = this.constantvalue_index.value;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                "constantvalue_index: " + index + " - " + classFile.getCPDescription(index)
        )));
    }
}
