/*
 * AttributeExceptions.java    5:18 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The class for the {@code Exceptions} attribute. The {@code Exceptions}
 * attribute has the following format:
 *
 * <pre>
 *    Exceptions_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u2 number_of_exceptions;
 *        u2 exception_index_table[number_of_exceptions];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.5">
 * VM Spec: The Exceptions Attribute
 * </a>
 */
public class AttributeExceptions extends AttributeInfo {

    /**
     * The value of the {@link #number_of_exceptions} item indicates the number
     * of entries in the {@link #exception_index_table}.
     */
    public transient final u2 number_of_exceptions;
    /**
     * Each value in the {@link #exception_index_table} array must be a valid
     * index into the {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that <code>index</code> must be
     * a CONSTANT_Class_info structure ({@link ConstantClassInfo}) representing
     * a class type that this method is declared to throw.
     */
    private transient final u2[] exception_index_table;

    AttributeExceptions(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
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
}
