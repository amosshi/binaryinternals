/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.AccessFlag;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.constant.CONSTANT_Class_info;
import org.freeinternals.format.classfile.u2;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;

/**
 * The class for the {@code InnerClasses} attribute. The {@code InnerClasses}
 * attribute has the following format:
 *
 * <pre>
 *    InnerClasses_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_classes;
 *        {  u2 inner_class_info_index;
 *           u2 outer_class_info_index;
 *           u2 inner_name_index;
 *           u2 inner_class_access_flags;
 *        } classes[number_of_classes];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.1
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.6">
 * VM Spec: The InnerClasses Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class InnerClasses_attribute extends attribute_info {

    public final u2 number_of_classes;
    private final Class[] classes;

    InnerClasses_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.number_of_classes = new u2(posDataInputStream);
        if (this.number_of_classes.value > 0) {
            this.classes = new Class[this.number_of_classes.value];
            for (int i = 0; i < this.number_of_classes.value; i++) {
                this.classes[i] = new Class(posDataInputStream);
            }
        } else {
            this.classes = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code classes}[{@code index}].
     *
     * @param index Index of the classes
     * @return The value of {@code classes}[{@code index}]
     */
    public Class getClass(final int index) {
        Class cls = null;
        if (this.classes != null && this.classes.length > index) {
            cls = this.classes[index];
        }

        return cls;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final ClassFile classFile) {
        int i;
        final int numOfClasses = this.number_of_classes.value;
        DefaultMutableTreeNode treeNodeInnerClass;
        DefaultMutableTreeNode treeNodeInnerClassItem;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                "number_of_classes: " + numOfClasses)));
        if (numOfClasses > 0) {
            treeNodeInnerClass = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    this.getClass(numOfClasses - 1).getStartPos() + this.getClass(numOfClasses - 1).getLength() - (startPos + 8),
                    "classes[" + numOfClasses + "]"
            ));

            InnerClasses_attribute.Class cls;
            for (i = 0; i < numOfClasses; i++) {
                cls = this.getClass(i);

                treeNodeInnerClassItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        cls.getStartPos(),
                        cls.getLength(),
                        String.format("class %d: %s", i + 1, classFile.getCPDescription(cls.inner_class_info_index.value))
                ));
                cls.generateTreeNode(treeNodeInnerClassItem, classFile);
                treeNodeInnerClass.add(treeNodeInnerClassItem);
            }

            parentNode.add(treeNodeInnerClass);
        }
    }

    /**
     * The {@code classes} structure in {@code InnerClasses} attribute.
     *
     * @author Amos Shi
     */
    public static final class Class extends FileComponent implements GenerateTreeNodeClassFile {

        /**
         * The length of current component.
         */
        public static final int LENGTH = 8;

        /**
         * The value of the {@link inner_class_info_index} item must be a valid
         * index into the {@link ClassFile#constant_pool} table. The
         * {@link ClassFile#constant_pool} entry at that index must be a
         * {@link CONSTANT_Class_info} structure representing <code>C</code>.
         */
        public final u2 inner_class_info_index;
        /**
         * If C is not a member of a class or an interface - that is, if C is a
         * top-level class or interface or a local class or an anonymous class -
         * then the value of the outer_class_info_index item must be zero.
         *
         * Otherwise, the value of the outer_class_info_index item must be a
         * valid index into the constant_pool table, and the entry at that index
         * must be a CONSTANT_Class_info structure representing the class or
         * interface of which C is a member. The value of the
         * outer_class_info_index item must not equal the the value of the
         * inner_class_info_index item.
         */
        public final u2 outer_class_info_index;
        /**
         * If C is anonymous, the value of the inner_name_index item must be
         * zero.
         *
         * Otherwise, the value of the inner_name_index item must be a valid
         * index into the constant_pool table, and the entry at that index must
         * be a CONSTANT_Utf8_info structure that represents the original simple
         * name of C, as given in the source code from which this class file was
         * compiled.
         */
        public final u2 inner_name_index;
        public final u2 inner_class_access_flags;

        private Class(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.inner_class_info_index = new u2(posDataInputStream);
            this.outer_class_info_index = new u2(posDataInputStream);
            this.inner_name_index = new u2(posDataInputStream);
            this.inner_class_access_flags = new u2(posDataInputStream);
        }

        /**
         * Generate the modifier string from the
         * {@link #inner_class_access_flags} value.
         *
         * @return A string for modifier
         */
        public String getModifiers() {
            return AccessFlag.getInnerClassModifier(this.inner_class_access_flags.value);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
            final int startPosMoving = this.getStartPos();

            int cpIndex = this.inner_class_info_index.value;
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    2,
                    "inner_class_info_index: " + cpIndex + " - " + classFile.getCPDescription(cpIndex)
            )));

            cpIndex = this.outer_class_info_index.value;
            final String outer_class_info_index_desc = (cpIndex == 0) ? "" : " - " + classFile.getCPDescription(cpIndex);
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 2,
                    2,
                    "outer_class_info_index: " + cpIndex + outer_class_info_index_desc
            )));

            cpIndex = this.inner_name_index.value;
            final String inner_name_index_desc = (cpIndex == 0) ? "" : " - " + classFile.getCPDescription(cpIndex);
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 4,
                    2,
                    "inner_name_index: " + cpIndex + inner_name_index_desc
            )));

            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 6,
                    2,
                    "inner_class_access_flags: " + BytesTool.getBinaryString(this.inner_class_access_flags.value) + " - " + this.getModifiers()
            )));
        }
    }
}
