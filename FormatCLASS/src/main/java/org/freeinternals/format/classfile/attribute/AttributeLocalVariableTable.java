/*
 * AttributeLocalVariableTable.java    5:33 AM, August 5, 2007
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
 * @since JDK 6.0
 * @see
 * <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#5956">
 * VM Spec: The LocalVariableTable Attribute
 * </a>
 */
public class AttributeLocalVariableTable extends AttributeInfo {

    public transient final u2 local_variable_table_length;
    private transient final LocalVariableTable[] localVariableTable;

    AttributeLocalVariableTable(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_45_3, JavaSEVersion.Version_1_0_2);

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

    /**
     * The {@code local_variable_table} structure in {@code LocalVariableTable}
     * attribute.
     *
     * @author Amos Shi
     * @since JDK 6.0
     * @see AttributeLocalVariableTable
     */
    public final class LocalVariableTable extends FileComponent {

        public final static int LENGTH = 10;
        /**
         * The given local variable must have a value at indices into the
         * {@link AttributeCode#code} array in the interval
         * <code>[start_pc, start_pc + length)</code>, that is, between
         * <code>start_pc</code> inclusive and <code>start_pc + length</code>
         * exclusive.
         */
        public transient final u2 start_pc;
        /**
         * See {@link LocalVariableTable#start_pc}.
         */
        public transient final u2 length;
        /**
         * Representing a valid unqualified name denoting a local variable.
         */
        public transient final u2 name_index;
        /**
         * Representing a field descriptor which encodes the type of a local
         * variable in the source program.
         */
        public transient final u2 descriptor_index;
        /**
         * The given local variable must be at {@link #index} in the local
         * variable array of the current frame.
         * <p>
         * If the local variable at {@link #index} is of type
         * <code>double</code> or <code>long</code>, it occupies both
         * <code>index</code> and <code>index + 1</code>.
         * </p>
         */
        public transient final u2 index;

        private LocalVariableTable(final PosDataInputStream posDataInputStream) throws IOException {
            super.startPos = posDataInputStream.getPos();
            super.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.length = new u2(posDataInputStream);
            this.name_index = new u2(posDataInputStream);
            this.descriptor_index = new u2(posDataInputStream);
            this.index = new u2(posDataInputStream);
        }
    }
}
