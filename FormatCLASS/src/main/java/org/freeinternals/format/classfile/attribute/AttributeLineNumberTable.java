/*
 * AttributeLineNumberTable.java    5:28 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
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
 */
public class AttributeLineNumberTable extends AttributeInfo {

    public transient final u2 line_number_table_length;
    public final transient LineNumberTable[] lineNumberTable;

    AttributeLineNumberTable(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_45_3, JavaSEVersion.Version_1_0_2);

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

    /**
     * The {@code line_number_table} structure in {@code LineNumberTable}
     * attribute.
     *
     * @author Amos Shi
     * @see AttributeLineNumberTable
     */
    public final class LineNumberTable extends FileComponent {

        public static final int LENGTH = 4;
        public transient final u2 start_pc;
        public transient final u2 line_number;

        private LineNumberTable(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.line_number = new u2(posDataInputStream);
        }
    }
}
