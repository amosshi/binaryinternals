/*
 * AttributeLocalVariableTypeTable.java    11:08 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
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
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.14">
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
        super(nameIndex, type, posDataInputStream);

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

    public final static class LocalVariableTypeTable extends FileComponent {

        public static final int LENGTH = 10;
        public transient final u2 start_pc;
        public transient final u2 length;
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
            this.length = new u2(posDataInputStream);
            this.name_index = new u2(posDataInputStream);
            this.signature_index = new u2(posDataInputStream);
            this.index = new u2(posDataInputStream);
        }
    }

}
