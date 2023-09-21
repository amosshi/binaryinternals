/*
 * AttributeExceptions.java    5:18 AM, August 5, 2007
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
import org.binaryinternals.format.classfile.constant.CONSTANT_Class_info;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code Exceptions} attribute. The {@code Exceptions}
 * attribute has the following format:
 *
 * <pre>
 *    Exceptions_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_exceptions;
 *        u2 exception_index_table[number_of_exceptions];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.5">
 * VM Spec: The Exceptions Attribute
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class Exceptions_attribute extends attribute_info {

    /**
     * The value of the {@link #number_of_exceptions} item indicates the number
     * of entries in the {@link #exception_index_table}.
     */
    public final u2 number_of_exceptions;
    /**
     * Each value in the {@link #exception_index_table} array must be a valid
     * index into the {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that <code>index</code> must be
     * a {@link CONSTANT_Class_info} structure representing a class type that
     * this method is declared to throw.
     */
    private final u2[] exception_index_table;

    Exceptions_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.number_of_exceptions = new u2(posDataInputStream);
        if (this.number_of_exceptions.value > 0) {
            this.exception_index_table = new u2[this.number_of_exceptions.value];
            for (int i = 0; i < this.number_of_exceptions.value; i++) {
                this.exception_index_table[i] = new u2(posDataInputStream);
            }
        } else {
            this.exception_index_table = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code exception_index_table}[{@code index}].
     *
     * @param index Index of the exception table
     * @return The value of {@code exception_index_table}[{@code index}] or
     * <code>-1</code> if <code>index</code> is not valid
     */
    public int getExceptionIndexTableItem(final int index) {
        int i = -1;
        if (this.exception_index_table != null) {
            i = this.exception_index_table[index].value;
        }

        return i;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int i;
        final int numOfExceptions = this.number_of_exceptions.value;

        this.addNode(parentNode,
                startPos + 6,
                u2.LENGTH,
                "number_of_exceptions",
                numOfExceptions,
                "msg_attr_Exceptions__number_of_exceptions",
                Icons.Counter
        );
        if (numOfExceptions > 0) {
            DefaultMutableTreeNode treeNodeExceptions = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    numOfExceptions * u2.LENGTH,
                    String.format("exceptions [%d]", numOfExceptions),
                    MESSAGES.getString("msg_attr_Exceptions__exception_index_table")
            ));

            for (i = 0; i < numOfExceptions; i++) {
                int cpIndex = this.getExceptionIndexTableItem(i);
                this.addNode(treeNodeExceptions,
                        startPos + 10 + i * u2.LENGTH,
                        u2.LENGTH,
                        String.format("exception_index_table %d", i + 1),
                        String.format(TEXT_CPINDEX_VALUE, cpIndex, "exception type", ((ClassFile) classFile).getCPDescription(cpIndex)),
                        "msg_attr_Exceptions__exception_index_table",
                        Icons.Exception
                );
            }
            parentNode.add(treeNodeExceptions);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Exceptions";
    }
}
