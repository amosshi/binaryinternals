/*
 * AttributeLocalVariableTable.java    5:33 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code LocalVariableTable} attribute. The
 * {@code LocalVariableTable} attribute has the following format:
 *
 * <pre>
 *    LocalVariableTable_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 local_variable_table_length;
 *        {   u2 start_pc;
 *            u2 length;
 *            u2 name_index;
 *            u2 descriptor_index;
 *            u2 index;
 *        } local_variable_table[local_variable_table_length];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.13">
 * VM Spec: The LocalVariableTable Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class LocalVariableTable_attribute extends attribute_info {

    public final u2 local_variable_table_length;
    private final LocalVariableTable[] localVariableTable;

    LocalVariableTable_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.local_variable_table_length = new u2(posDataInputStream);
        if (this.local_variable_table_length.value > 0) {
            this.localVariableTable = new LocalVariableTable[this.local_variable_table_length.value];
            for (int i = 0; i < this.local_variable_table_length.value; i++) {
                this.localVariableTable[i] = new LocalVariableTable(posDataInputStream);
            }
        } else {
            this.localVariableTable = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code local_variable_table}[{@code index}].
     *
     * @param index Index of the local variable table
     * @return The value of {@code local_variable_table}[{@code index}]
     */
    public LocalVariableTable getLocalVariableTable(final int index) {
        LocalVariableTable lvt = null;
        if (this.localVariableTable != null && this.localVariableTable.length > 0) {
            lvt = this.localVariableTable[index];
        }

        return lvt;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat classFile) {
        final int lvt_length = this.local_variable_table_length.value;
        this.addNode(parentNode,
                super.startPos + 6,
                u2.LENGTH,
                "local_variable_table_length",
                lvt_length,
                "msg_attr_local_variable_table_length",
                Icons.Length
        );

        if (lvt_length > 0) {
            final DefaultMutableTreeNode treeNodeLvt = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    lvt_length * LocalVariableTable_attribute.LocalVariableTable.LENGTH,
                    "local_variable_table[" + lvt_length + "]",
                    MESSAGES.getString("msg_attr_local_variable_table")
            ));

            for (int i = 0; i < lvt_length; i++) {
                LocalVariableTable_attribute.LocalVariableTable lvt = this.getLocalVariableTable(i);
                DefaultMutableTreeNode treeNodeLvtItem = this.addNode(treeNodeLvt,
                        lvt.getStartPos(),
                        lvt.getLength(),
                        String.format("[%05d]", i + 1),
                        "local_variable_table",
                        "msg_attr_local_variable_table",
                        Icons.Row
                );
                lvt.generateTreeNode(treeNodeLvtItem, classFile);
            }

            parentNode.add(treeNodeLvt);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_LocalVariableTable";
    }

    /**
     * The {@code local_variable_table} structure in {@code LocalVariableTable}
     * attribute.
     *
     * @author Amos Shi
     * @see LocalVariableTable_attribute
     */
    public static final class LocalVariableTable extends FileComponent implements GenerateTreeNodeClassFile {

        public static final int LENGTH = 10;
        /**
         * The given local variable must have a value at indices into the
         * {@link Code_attribute#code} array in the interval
         * <code>[start_pc, start_pc + length)</code>, that is, between
         * <code>start_pc</code> inclusive and <code>start_pc + length</code>
         * exclusive.
         */
        public final u2 start_pc;
        /**
         * See {@link LocalVariableTable#start_pc}.
         */
        public final u2 length_code;
        /**
         * Representing a valid unqualified name denoting a local variable.
         */
        public final u2 name_index;
        /**
         * Representing a field descriptor which encodes the type of a local
         * variable in the source program.
         */
        public final u2 descriptor_index;
        /**
         * The given local variable must be at {@link #index} in the local
         * variable array of the current frame.
         * <p>
         * If the local variable at {@link #index} is of type
         * <code>double</code> or <code>long</code>, it occupies both
         * <code>index</code> and <code>index + 1</code>.
         * </p>
         */
        public final u2 index;

        private LocalVariableTable(final PosDataInputStream posDataInputStream) throws IOException {
            super.startPos = posDataInputStream.getPos();
            super.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.length_code = new u2(posDataInputStream);
            this.name_index = new u2(posDataInputStream);
            this.descriptor_index = new u2(posDataInputStream);
            this.index = new u2(posDataInputStream);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final int pos = this.getStartPos();
            final ClassFile classFile = (ClassFile) fileFormat;
            int cpIndex;

            this.addNode(parentNode,
                    pos,
                    u2.LENGTH,
                    "start_pc",
                    this.start_pc.value,
                    "msg_attr_local_variable_table__start_pc_length",
                    Icons.Offset
            );

            this.addNode(parentNode,
                    pos + 2,
                    u2.LENGTH,
                    "length",
                    this.length_code.value,
                    "msg_attr_local_variable_table__start_pc_length",
                    Icons.Length
            );

            cpIndex = this.name_index.value;
            this.addNode(parentNode,
                    pos + 4,
                    u2.LENGTH,
                    "name_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "name", classFile.getCPDescription(cpIndex)),
                    "msg_attr_local_variable_table__name_index",
                    Icons.Name
            );

            cpIndex = this.descriptor_index.value;
            this.addNode(parentNode,
                    pos + 6,
                    u2.LENGTH,
                    "descriptor_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "descriptor", classFile.getCPDescription(cpIndex)),
                    "msg_attr_local_variable_table__descriptor_index",
                    Icons.Descriptor
            );

            this.addNode(parentNode,
                    pos + 8,
                    u2.LENGTH,
                    "index",
                    this.index.value,
                    "msg_attr_local_variable_table__index",
                    Icons.Index
            );
        }
    }
}
