/*
 * AttributeCode.java    5:09 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
import org.freeinternals.format.classfile.Opcode;
import org.freeinternals.format.classfile.constant.cp_info;
import org.freeinternals.format.classfile.u2;
import org.freeinternals.format.classfile.u4;

/**
 * The class for the {@code Code} attribute. The {@code Code} attribute has the
 * following format:
 *
 * <pre>
 *    Code_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
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
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.3">
 * VM Spec: The Code Attribute
 * </a>
 *
 * <pre>
 * java:S101  - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * java:S116  - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S1104", "java:S116"})
public class Code_attribute extends attribute_info {

    public static final String ATTRIBUTE_CODE_NODE = "code";

    public final u2 max_stack;
    public final u2 max_locals;

    /**
     * The value of the {@link #code_length} item gives the number of bytes in
     * the code array for this method.
     *
     * The value of {@link #code_length} must be greater than <code>zero</code>
     * (as the code array must not be empty) and less than <code>65536</code>.
     */
    public final u4 code_length;
    public final byte[] code;
    public final u2 exception_table_length;
    public ExceptionTable[] exceptionTable;
    public final u2 attributes_count;
    public final attribute_info[] attributes;

    Code_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream, final cp_info[] cp) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        int i;

        this.max_stack = new u2(posDataInputStream);
        this.max_locals = new u2(posDataInputStream);
        this.code_length = new u4(posDataInputStream);
        this.code = new byte[this.code_length.value];
        int readBytes = posDataInputStream.read(this.code);
        if (readBytes != this.code_length.value) {
            throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.code_length.value, readBytes));
        }

        this.exception_table_length = new u2(posDataInputStream);
        if (this.exception_table_length.value > 0) {
            this.exceptionTable = new ExceptionTable[this.exception_table_length.value];
            for (i = 0; i < this.exception_table_length.value; i++) {
                this.exceptionTable[i] = new ExceptionTable(posDataInputStream);
            }
        }

        this.attributes_count = new u2(posDataInputStream);
        if (this.attributes_count.value > 0) {
            this.attributes = new attribute_info[this.attributes_count.value];
            for (i = 0; i < this.attributes_count.value; i++) {
                this.attributes[i] = attribute_info.parse(posDataInputStream, cp);
            }
        } else {
            this.attributes = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the {@link #code} parse result. This method will return an empty list
     * if {@link #code} is <code>null</code>.
     *
     * @return Parsed {@link Opcode} list
     */
    public List<Opcode.InstructionParsed> parseCode() {
        if (this.code != null && this.code.length > 0) {
            return Opcode.parseCode(this.code);
        } else {
            return new ArrayList<>();
        }
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

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat classFile) {
        int i;
        final int codeLength = this.code_length.value;
        DefaultMutableTreeNode treeNodeExceptionTableItem;
        DefaultMutableTreeNode treeNodeAttribute;
        DefaultMutableTreeNode treeNodeAttributeItem;

        this.addNode(parentNode,
                super.startPos + 6,
                2,
                "max_stack",
                this.max_stack.value,
                "msg_attr_Code__max_stack",
                Icons.Max
        );
        this.addNode(parentNode,
                super.startPos + 8,
                2,
                "max_locals",
                this.max_locals.value,
                "msg_attr_Code__max_locals",
                Icons.Max
        );
        this.addNode(parentNode,
                super.startPos + 10,
                4,
                "code_length",
                this.code_length.value,
                "msg_attr_Code__code_length",
                Icons.Length
        );
        this.addNode(parentNode,
                super.startPos + 14,
                codeLength,
                ATTRIBUTE_CODE_NODE,
                "byte codes",
                "msg_attr_Code__code",
                Icons.Data
        );
        this.addNode(parentNode,
                super.startPos + 14 + codeLength,
                2,
                "exception_table_length",
                this.exception_table_length.value,
                "msg_attr_exception_table_length",
                Icons.Length
        );

        // Add exception table
        if (this.exception_table_length.value > 0) {
            DefaultMutableTreeNode treeNodeExceptionTable = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 14 + codeLength + 2,
                    ExceptionTable.LENGTH * this.exception_table_length.value,
                    "exception_table[" + this.exception_table_length.value + "]",
                    MESSAGES.getString("msg_attr_exception_table")
            ));

            Code_attribute.ExceptionTable et;
            for (i = 0; i < this.exception_table_length.value; i++) {
                et = this.getExceptionTable(i);
                treeNodeExceptionTableItem = this.addNode(treeNodeExceptionTable,
                        et.getStartPos(),
                        et.getLength(),
                        String.valueOf(i + 1),
                        "exception_table",
                        "msg_attr_exception_table",
                        Icons.Exception
                );
                et.generateTreeNode(treeNodeExceptionTableItem, classFile);
            }

            parentNode.add(treeNodeExceptionTable);
        }

        // Add attributes
        final int attrCount = this.attributes_count.value;
        final int attrStartPos = super.startPos + 14 + codeLength + 2 + this.exception_table_length.value * ExceptionTable.LENGTH;
        this.addNode(parentNode,
                attrStartPos,
                2,
                "attributes_count",
                attrCount,
                "msg_attr_Code_attributes_count",
                Icons.Counter
        );
        if (attrCount > 0) {
            int attrLength = 0;
            for (attribute_info codeAttr : this.attributes) {
                attrLength += codeAttr.getLength();
            }

            treeNodeAttribute = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    attrStartPos + 2,
                    attrLength,
                    "attributes[" + attrCount + "]",
                    MESSAGES.getString("msg_attr_Code_attributes")
            ));

            for (i = 0; i < attrCount; i++) {
                attribute_info attr = this.attributes[i];
                treeNodeAttributeItem = this.addNode(treeNodeAttribute,
                        attr.getStartPos(),
                        attr.getLength(),
                        String.valueOf(i + 1),
                        attr.getName(),
                        attr.getMessageKey(),
                        Icons.Annotations
                );
                attr.generateTreeNodeCommon(treeNodeAttributeItem, (ClassFile) classFile);
            }

            parentNode.add(treeNodeAttribute);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Code";
    }

    /**
     * The {@code exception_table} structure in {@code Code} attribute.
     *
     * @author Amos Shi
     */
    public static final class ExceptionTable extends FileComponent implements GenerateTreeNodeClassFile {

        public static final int LENGTH = 8;
        public final u2 start_pc;
        public final u2 end_pc;
        public final u2 handler_pc;

        /**
         * If the value of the catch_type item is nonzero, it must be a valid
         * index into the constant_pool table. The constant_pool entry at that
         * index must be a CONSTANT_Class_info structure representing a class of
         * exceptions that this exception handler is designated to catch. The
         * exception handler will be called only if the thrown exception is an
         * instance of the given class or one of its subclasses.
         *
         * If the value of the catch_type item is zero, this exception handler
         * is called for all exceptions.
         */
        public final u2 catch_type;

        private ExceptionTable(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.start_pc = new u2(posDataInputStream);
            this.end_pc = new u2(posDataInputStream);
            this.handler_pc = new u2(posDataInputStream);
            this.catch_type = new u2(posDataInputStream);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final int startPosMoving = super.getStartPos();

            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "start_pc",
                    this.start_pc.value,
                    "msg_attr_exception_table__start_end_pc",
                    Icons.Offset
            );
            this.addNode(parentNode,
                    startPosMoving + 2,
                    u2.LENGTH,
                    "end_pc",
                    this.end_pc.value,
                    "msg_attr_exception_table__start_end_pc",
                    Icons.Offset
            );
            this.addNode(parentNode,
                    startPosMoving + 4,
                    u2.LENGTH,
                    "handler_pc",
                    this.handler_pc.value,
                    "msg_attr_exception_table__handler_pc",
                    Icons.Offset
            );

            final int catchType = this.catch_type.value;
            String catchTypeDesc = (catchType == 0) ? "" : " - " + ((ClassFile) fileFormat).getCPDescription(catchType);
            this.addNode(parentNode,
                    startPosMoving + 6,
                    2,
                    "catch_type",
                    catchType + catchTypeDesc,
                    "msg_attr_exception_table__catch_type",
                    Icons.Kind
            );
        }
    }
}
