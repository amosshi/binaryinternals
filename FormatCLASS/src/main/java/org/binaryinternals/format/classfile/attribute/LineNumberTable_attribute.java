/*
 * AttributeLineNumberTable.java    5:28 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
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
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code LineNumberTable} attribute. The
 * {@code LineNumberTable} attribute has the following format:
 *
 * <pre>
 *    LineNumberTable_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 line_number_table_length;
 *        {  u2 start_pc;
 *           u2 line_number;
 *        } line_number_table[line_number_table_length];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.12">
 * VM Spec: The LineNumberTable Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class LineNumberTable_attribute extends attribute_info {

    public final u2 line_number_table_length;
    public final line_number_table[] lineNumberTable;

    LineNumberTable_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.line_number_table_length = new u2(posDataInputStream);
        if (this.line_number_table_length.value > 0) {
            this.lineNumberTable = new line_number_table[this.line_number_table_length.value];
            for (int i = 0; i < this.line_number_table_length.value; i++) {
                this.lineNumberTable[i] = new line_number_table(posDataInputStream);
            }
        } else {
            this.lineNumberTable = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code line_number_table}[{@code index}].
     *
     * @param index Index of the line number table
     * @return The value of {@code line_number_table}[{@code index}]
     */
    public line_number_table getLineNumberTable(final int index) {
        line_number_table lnt = null;
        if (this.lineNumberTable != null && this.lineNumberTable.length > index) {
            lnt = this.lineNumberTable[index];
        }

        return lnt;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int lnt_length = this.line_number_table_length.value;

        this.addNode(parentNode,
                super.startPos + 6,
                u2.LENGTH,
                "line_number_table_length",
                lnt_length,
                "msg_attr_line_number_table_length",
                Icons.Length
        );

        if (lnt_length > 0) {
            final DefaultMutableTreeNode treeNodeLnt = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    lnt_length * 4,
                    "line_number_table [" + lnt_length + "]",
                    MESSAGES.getString("msg_attr_line_number_table")
            ));

            DefaultMutableTreeNode treeNodeLntItem;
            LineNumberTable_attribute.line_number_table lnt;
            for (int i = 0; i < lnt_length; i++) {
                lnt = this.getLineNumberTable(i);

                treeNodeLntItem = this.addNode(treeNodeLnt,
                        lnt.getStartPos(),
                        lnt.getLength(),
                        String.valueOf(i + 1),
                        "line_number_table",
                        "msg_attr_line_number_table",
                        Icons.Row
                );
                lnt.generateTreeNode(treeNodeLntItem, classFile);
            }

            parentNode.add(treeNodeLnt);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_LineNumberTable";
    }

    /**
     * The {@code line_number_table} structure in {@code LineNumberTable}
     * attribute.
     *
     * @author Amos Shi
     * @see LineNumberTable_attribute
     */
    public static final class line_number_table extends FileComponent implements GenerateTreeNodeClassFile {

        public static final int LENGTH = 4;
        public final u2 start_pc;
        public final u2 line_number;

        private line_number_table(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.line_number = new u2(posDataInputStream);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final int startPosMoving = this.getStartPos();
            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "start_pc",
                    this.start_pc.value,
                    "msg_attr_line_number_table__start_pc",
                    Icons.Offset
            );
            this.addNode(parentNode,
                    startPosMoving + u2.LENGTH,
                    u2.LENGTH,
                    "line_number",
                    this.line_number.value,
                    "msg_attr_line_number_table__line_number",
                    Icons.Data
            );
        }
    }
}
