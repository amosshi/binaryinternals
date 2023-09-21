/*
 * AttributeLocalVariableTypeTable.java    11:08 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u2;
import static org.binaryinternals.format.classfile.attribute.LocalVariableTable_attribute.local_variable_table.LENGTH;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.14">
 * VM Spec: The LocalVariableTypeTable Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class LocalVariableTypeTable_attribute extends attribute_info {

    private static final String MSG_ATTR_LVTT = "msg_attr_LocalVariableTypeTable";

    /**
     * Indicates the number of entries in the {@link #local_variable_type_table}
     * array.
     */
    public final u2 local_variable_type_table_length;
    /**
     * Indicates a range of code array offsets within which a local variable has
     * a value.
     */
    public final local_variable_type_table[] local_variable_type_table;

    LocalVariableTypeTable_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.local_variable_type_table_length = new u2(posDataInputStream);
        if (this.local_variable_type_table_length.value > 0) {
            this.local_variable_type_table = new local_variable_type_table[this.local_variable_type_table_length.value];
            for (int i = 0; i < this.local_variable_type_table_length.value; i++) {
                this.local_variable_type_table[i] = new local_variable_type_table(posDataInputStream);
            }
        } else {
            this.local_variable_type_table = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        final int tableLen = this.local_variable_type_table_length.value;
        this.addNode(parentNode,
                super.startPos + 6, u2.LENGTH,
                "local_variable_type_table_length", tableLen,
                "msg_attr_local_variable_type_table_length", Icons.Length
        );

        if (tableLen > 0) {
            DefaultMutableTreeNode lvttNodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    tableLen * LENGTH,
                    String.format("local_variable_type_table [%d]", tableLen),
                    MESSAGES.getString(MSG_ATTR_LVTT)
            ));
            parentNode.add(lvttNodes);

            for (int i = 0; i < tableLen; i++) {
                local_variable_type_table item = this.local_variable_type_table[i];
                DefaultMutableTreeNode lvttNode = this.addNode(lvttNodes,
                        item.getStartPos(),
                        item.getLength(),
                        String.format("%05d", i + 1),
                        "local_variable_type_table",
                        MSG_ATTR_LVTT,
                        Icons.Row
                );
                item.generateTreeNode(lvttNode, format);
            }
        }
    }

    @Override
    public String getMessageKey() {
        return MSG_ATTR_LVTT;
    }

    /**
     * Each entry in the local_variable_type_table array indicates a range of
     * code array offsets within which a local variable has a value. It also
     * indicates the index into the local variable array of the current frame at
     * which that local variable can be found. Each entry must contain the
     * following five items (the 5 instance fields of current class).
     */
    public static final class local_variable_type_table extends FileComponent implements GenerateTreeNodeClassFile {

        public static final int LENGTH = 10;
        public final u2 start_pc;
        public final u2 lvtt_length;
        /**
         * Representing a valid unqualified name denoting a local variable.
         */
        public final u2 name_index;
        /**
         * Representing a field signature which encodes the type of a local
         * variable in the source program.
         */
        public final u2 signature_index;
        /**
         * The given local variable must be at {@link index} in the local
         * variable array of the current frame.
         */
        public final u2 index;

        protected local_variable_type_table(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            super.startPos = posDataInputStream.getPos();
            super.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.lvtt_length = new u2(posDataInputStream);
            this.name_index = new u2(posDataInputStream);
            this.signature_index = new u2(posDataInputStream);
            this.index = new u2(posDataInputStream);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            final int itemStartPos = this.getStartPos();
            int cpIndex;

            this.addNode(parentNode,
                    itemStartPos,
                    u2.LENGTH,
                    "start_pc",
                    this.start_pc.value,
                    "msg_attr_local_variable_type_table__start_pc_length",
                    Icons.Offset
            );
            this.addNode(parentNode,
                    itemStartPos + 2,
                    u2.LENGTH,
                    "length",
                    this.lvtt_length.value,
                    "msg_attr_local_variable_type_table__start_pc_length",
                    Icons.Length
            );

            cpIndex = this.name_index.value;
            this.addNode(parentNode,
                    itemStartPos + 4,
                    u2.LENGTH,
                    "name_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "name", classFile.getCPDescription(cpIndex)),
                    "msg_attr_local_variable_type_table__name_index",
                    Icons.Name
            );

            cpIndex = this.signature_index.value;
            this.addNode(parentNode,
                    itemStartPos + 6,
                    u2.LENGTH,
                    "signature_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "signature", classFile.getCPDescription(cpIndex)),
                    "msg_attr_local_variable_type_table__signature_index",
                    Icons.Descriptor
            );

            this.addNode(parentNode,
                    itemStartPos + 8,
                    u2.LENGTH,
                    "index",
                    this.index.value,
                    "msg_attr_local_variable_type_table__index",
                    Icons.Index
            );
        }
    }

}
