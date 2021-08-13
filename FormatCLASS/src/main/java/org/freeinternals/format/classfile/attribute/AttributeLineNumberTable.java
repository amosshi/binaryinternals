/*
 * AttributeLineNumberTable.java    5:28 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.12">
 * VM Spec: The LineNumberTable Attribute
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class AttributeLineNumberTable extends AttributeInfo {

    public final u2 line_number_table_length;
    public final LineNumberTable[] lineNumberTable;

    AttributeLineNumberTable(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.line_number_table_length = new u2(posDataInputStream);
        if (this.line_number_table_length.value > 0) {
            this.lineNumberTable = new LineNumberTable[this.line_number_table_length.value];
            for (int i = 0; i < this.line_number_table_length.value; i++) {
                this.lineNumberTable[i] = new LineNumberTable(posDataInputStream);
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
    public LineNumberTable getLineNumberTable(final int index) {
        LineNumberTable lnt = null;
        if (this.lineNumberTable != null && this.lineNumberTable.length > index) {
            lnt = this.lineNumberTable[index];
        }

        return lnt;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        final int lnt_length = this.line_number_table_length.value;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                "line_number_table_length: " + lnt_length)));

        if (lnt_length > 0) {
            final DefaultMutableTreeNode treeNodeLnt = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    lnt_length * 4,
                    "line_number_table[" + lnt_length + "]"
            ));

            DefaultMutableTreeNode treeNodeLntItem;
            AttributeLineNumberTable.LineNumberTable lnt;
            for (int i = 0; i < lnt_length; i++) {
                lnt = this.getLineNumberTable(i);

                treeNodeLntItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        lnt.getStartPos(),
                        lnt.getLength(),
                        String.format("line_number_table [%d]", i)
                ));
                this.generateSubnode(treeNodeLntItem, lnt);
                treeNodeLnt.add(treeNodeLntItem);
            }

            parentNode.add(treeNodeLnt);
        }
    }


    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeLineNumberTable.LineNumberTable lnt) {
        if (lnt == null) {
            return;
        }

        final int startPosMoving = lnt.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                2,
                "start_pc: " + lnt.start_pc.value
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 2,
                2,
                "line_number: " + lnt.line_number.value
        )));
    }

    /**
     * The {@code line_number_table} structure in {@code LineNumberTable}
     * attribute.
     *
     * @author Amos Shi
     * @see AttributeLineNumberTable
     */
    public static final class LineNumberTable extends FileComponent {

        public static final int LENGTH = 4;
        public final u2 start_pc;
        public final u2 line_number;

        private LineNumberTable(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.line_number = new u2(posDataInputStream);
        }
    }
}
