/*
 * AttributeLocalVariableTypeTable.java    11:08 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code LocalVariableTypeTable} attribute is an optional variable-length
 * attribute in the {@code attributes} table of a {@code Code} attribute; It may
 * be used by debuggers to determine the value of a given local variable during
 * the execution of a method.
 *
 * The {@code LocalVariableTypeTable} attribute has the following format:
 *
 * <pre>
 * LocalVariableTypeTable_attribute {
 *    u2 attribute_name_index;
 *    u4 attribute_length;
 *
 *    u2 local_variable_type_table_length;
 *    {   u2 start_pc;
 *        u2 length;
 *        u2 name_index;
 *        u2 signature_index;
 *        u2 index;
 *    } local_variable_type_table[local_variable_type_table_length];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.14">
 * VM Spec: The LocalVariableTypeTable Attribute
 * </a>
 */
public class AttributeLocalVariableTypeTable extends AttributeInfo {

    /**
     * Indicates the number of entries in the {@link #local_variable_type_table}
     * array.
     */
    public transient final u2 local_variable_type_table_length;
    /**
     * Indicates a range of code array offsets within which a local variable has
     * a value.
     */
    public transient final LocalVariableTypeTable[] local_variable_type_table;

    AttributeLocalVariableTypeTable(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_49_0, JavaSEVersion.Version_5_0);

        this.local_variable_type_table_length = new u2(posDataInputStream);
        if (this.local_variable_type_table_length.value > 0) {
            this.local_variable_type_table = new LocalVariableTypeTable[this.local_variable_type_table_length.value];
            for (int i = 0; i < this.local_variable_type_table_length.value; i++) {
                this.local_variable_type_table[i] = new LocalVariableTypeTable(posDataInputStream);
            }
        } else {
            this.local_variable_type_table = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                "local_variable_type_table_length: " + this.local_variable_type_table_length.value
        )));

        if (this.local_variable_type_table_length.value > 0) {
            DefaultMutableTreeNode lvtt_nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    this.local_variable_type_table_length.value * LocalVariableTypeTable.LENGTH,
                    "local_variable_type_table"
            ));
            parentNode.add(lvtt_nodes);

            for (int i = 0; i < this.local_variable_type_table_length.value; i++) {
                LocalVariableTypeTable item = this.local_variable_type_table[i];
                int item_startPos = item.getStartPos();
                DefaultMutableTreeNode lvtt_node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos,
                        item.getLength(),
                        "local_variable_type_table " + (i + 1)
                ));
                lvtt_nodes.add(lvtt_node);

                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos,
                        2,
                        "start_pc: " + item.start_pc.value
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 2,
                        2,
                        "length: " + item.lvtt_length.value
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 4,
                        2,
                        "signature_index: " + item.signature_index.value + " - " + classFile.getCPDescription(item.signature_index.value)
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 6,
                        2,
                        "name_index: " + item.name_index.value + " - " + classFile.getCPDescription(item.name_index.value)
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 8,
                        2,
                        "index: " + item.index.value
                )));
            }
        }
    }

    /**
     * Each entry in the local_variable_type_table array indicates a range of
     * code array offsets within which a local variable has a value. It also
     * indicates the index into the local variable array of the current frame at
     * which that local variable can be found. Each entry must contain the
     * following five items (the 5 instance fields of current class).
     */
    public final static class LocalVariableTypeTable extends FileComponent {

        public static final int LENGTH = 10;
        public final u2 start_pc;
        public final u2 lvtt_length;
        /**
         * Representing a valid unqualified name denoting a local variable.
         */
        public transient final u2 name_index;
        /**
         * Representing a field signature which encodes the type of a local
         * variable in the source program.
         */
        public transient final u2 signature_index;
        /**
         * The given local variable must be at {@link index} in the local
         * variable array of the current frame.
         */
        public transient final u2 index;

        protected LocalVariableTypeTable(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            super.startPos = posDataInputStream.getPos();
            super.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.lvtt_length = new u2(posDataInputStream);
            this.name_index = new u2(posDataInputStream);
            this.signature_index = new u2(posDataInputStream);
            this.index = new u2(posDataInputStream);
        }
    }

}
