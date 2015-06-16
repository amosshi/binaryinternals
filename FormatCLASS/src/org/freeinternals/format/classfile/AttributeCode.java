/*
 * AttributeCode.java    5:09 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The class for the {@code Code} attribute.
 * The {@code Code} attribute has the following format:
 *
 * <pre>
 *    Code_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u2 max_stack;
 *        u2 max_locals;
 *        u4 code_length;
 *        u1 code[code_length];
 *        u2 exception_table_length;
 *        {
 *                u2 start_pc;
 *                u2 end_pc;
 *                u2 handler_pc;
 *                u2 catch_type;
 *        } exception_table[exception_table_length];
 *        u2 attributes_count;
 *        attribute_info attributes[attributes_count];
 *    }
 * </pre>
 *
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#1546">
 * VM Spec: The Code Attribute
 * </a>
 */
public class AttributeCode extends AttributeInfo {

    public transient final u2 max_stack;
    public transient final u2 max_locals;
    public transient final u4 code_length;
    private transient final byte[] code;
    public transient final u2 exception_table_length;
    public transient ExceptionTable[] exceptionTable;
    public transient final u2 attributes_count;
    public transient AttributeInfo[] attributes;

    AttributeCode(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream, final AbstractCPInfo[] cp)
            throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        int i;

        this.max_stack = new u2(posDataInputStream.readUnsignedShort());
        this.max_locals = new u2(posDataInputStream.readUnsignedShort());
        this.code_length = new u4(posDataInputStream.readInt());
        this.code = new byte[this.code_length.value];
        posDataInputStream.read(this.code);

        this.exception_table_length = new u2(posDataInputStream.readUnsignedShort());
        if (this.exception_table_length.value > 0) {
            this.exceptionTable = new ExceptionTable[this.exception_table_length.value];
            for (i = 0; i < this.exception_table_length.value; i++) {
                this.exceptionTable[i] = new ExceptionTable(posDataInputStream);
            }
        }

        this.attributes_count = new u2(posDataInputStream.readUnsignedShort());
        if (this.attributes_count.value > 0) {
            this.attributes = new AttributeInfo[this.attributes_count.value];
            for (i = 0; i < this.attributes_count.value; i++) {
                this.attributes[i] = AttributeInfo.parse(posDataInputStream, cp);
            }

        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code code}.
     *
     * @return The value of {@code code}
     */
    public byte[] getCode() {
        return this.code;
    }

    /**
     * Get the value of {@code exception_table}[{@code index}].
     *
     * @param index Index of the exception table
     * @return The value of {@code exception_table}[{@code index}]
     */
    public ExceptionTable getExceptionTable(final int index) {
        ExceptionTable et = null;
        if (this.exceptionTable != null) {
            et = this.exceptionTable[index];
        }
        return et;
    }

    /**
     * Get the value of {@code attributes}[{@code index}].
     *
     * @param index Zero-based index of the attributes
     * @return The value of {@code attributes}[{@code index}]
     */
    public AttributeInfo getAttribute(final int index) {
        return this.attributes[index];
    }

    /**
     * The {@code exception_table} structure in {@code Code} attribute.
     *
     * @author Amos Shi
     * @since JDK 6.0
     */
    public final class ExceptionTable extends FileComponent {
        
        public static final int LENGTH = 8;
        public transient final u2 start_pc;
        public transient final u2 end_pc;
        public transient final u2 handler_pc;
        public transient final u2 catch_type;

        private ExceptionTable(final PosDataInputStream posDataInputStream)
                throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.start_pc = new u2(posDataInputStream.readUnsignedShort());
            this.end_pc = new u2(posDataInputStream.readUnsignedShort());
            this.handler_pc = new u2(posDataInputStream.readUnsignedShort());
            this.catch_type = new u2(posDataInputStream.readUnsignedShort());
        }
    }
}
