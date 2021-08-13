/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
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
import org.freeinternals.format.classfile.constant.CONSTANT_Class_info;
import org.freeinternals.format.classfile.u2;

/**
 *
 * The class for the {@code NestMembers} attribute. The {@code NestMembers}
 * attribute has the following format:
 *
 * <pre>
 *    NestMembers_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_classes;
 *        u2 classes[number_of_classes];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 11
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.29">
 * VM Spec: The NestMembers Attribute
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class NestMembers_attribute extends attribute_info {

    /**
     * The value of the number_of_classes item indicates the number of entries
     * in the {@link #classes}table.
     */
    public final u2 number_of_classes;

    /**
     * Each value in the classes array must be a valid index into the
     * {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that index must be a
     * {@link CONSTANT_Class_info} structure representing a class or interface
     * which is a member of the nest hosted by the current class or interface.
     */
    public final u2[] classes;

    NestMembers_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.number_of_classes = new u2(posDataInputStream);
        if (this.number_of_classes.value > 0) {
            this.classes = new u2[this.number_of_classes.value];
            for (int i = 0; i < this.number_of_classes.value; i++) {
                this.classes[i] = new u2(posDataInputStream);
            }
        } else {
            this.classes = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        int startPosMoving = super.startPos + 6;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "number_of_classes: " + this.number_of_classes.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.number_of_classes.value > 0) {
            final DefaultMutableTreeNode numbersNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * this.number_of_classes.value,
                    "classes[" + this.number_of_classes.value + "]"
            ));
            parentNode.add(numbersNode);

            for (int i = 0; i < this.classes.length; i++) {
                int classIndex = this.classes[i].value;
                numbersNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        "class [" + i + "]: " + classIndex + " - " + classFile.getCPDescription(classIndex)
                )));
            }
        }
    }
}
