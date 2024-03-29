/*
 * Classes_attribute.java    11:21 AM, August 14, 2021
 *
 * Copyright  2021, BinaryInternals.org. All rights reserved.
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
import org.binaryinternals.format.classfile.constant.CONSTANT_Class_info;
import org.binaryinternals.format.classfile.u2;

/**
 *
 * Attribute with {@link #number_of_classes} and {@link #classes}. The attribute has the following format:
 *
 * <pre>
 *    XXXX_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_classes;
 *        u2 classes[number_of_classes];
 *    }
 * </pre>
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from JVM spec instead
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116"})
public abstract class ClassesCount extends attribute_info {

    /**
     * The value of the number_of_classes item indicates the number of entries
     * in the {@link #classes}table.
     */
    public final u2 number_of_classes;

    /**
     * Each value in the classes array must be a valid index into the
     * {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that index must be a
     * {@link CONSTANT_Class_info} structure
     */
    public final u2[] classes;

    ClassesCount(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos + 6;

        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "number_of_classes", this.number_of_classes.value,
                this.getMessageKey_4_number_of_classes(), Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.number_of_classes.value > 0) {
            final DefaultMutableTreeNode numbersNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * this.number_of_classes.value,
                    String.format("classes [%d]", this.number_of_classes.value),
                    this.getMessageKey_4_classes()
            ));
            parentNode.add(numbersNode);

            for (int i = 0; i < this.classes.length; i++) {
                int classIndex = this.classes[i].value;
                this.addNode(numbersNode,
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        String.format("class %d", i + 1),
                        String.format(TEXT_CPINDEX_VALUE, classIndex, "class", ((ClassFile)classFile).getCPDescription(classIndex)),
                        this.getMessageKey_4_classes(),
                        Icons.Class
                );
            }
        }
    }

    /**
     * Get message key for {@link #number_of_classes}.
     *
     * @return Message key for {@link #number_of_classes}
     *
     * @see NestMembers_attribute
     * @see PermittedSubclasses_attribute
     */
    abstract String getMessageKey_4_number_of_classes();

    /**
     * Get message key for {@link #classes}.
     *
     * @return Message key for {@link #classes}
     *
     * @see NestMembers_attribute
     * @see PermittedSubclasses_attribute
     */
    abstract String getMessageKey_4_classes();
}
