/*
 * ConstantMethodHandleInfo.java    12:04 AM, April 28, 2014
 *
 * Copyright 2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.u1;
import org.binaryinternals.format.classfile.u2;

/**
 * The {@code CONSTANT_MethodHandle_info} structure is used to represent a
 * method handle.
 *
 * <pre>
 *    CONSTANT_MethodHandle_info {
 *        u1 tag;
 *
 *        u1 reference_kind;
 *        u2 reference_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.8">
 * VM Spec: The CONSTANT_MethodHandle_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_MethodHandle_info extends cp_info {

    public static final int LENGTH = 5;
    /**
     * The value of the {@code reference_kind} item must be in the range 1 to 9.
     * The value denotes the {@code kind} of this method handle, which
     * characterizes its byte code behavior.
     */
    public final u1 reference_kind;
    /**
     * The value of the {@code reference_index} item must be a valid index into
     * the {@code constant_pool} table.
     */
    public final u2 reference_index;

    CONSTANT_MethodHandle_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_MethodHandle.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        this.reference_kind = new u1(posDataInputStream, true);
        this.reference_index = new u2(posDataInputStream);
        super.length = LENGTH;
    }

    @Override
    public String getDescription() {
        return this.getName() + ": Start Position: [" + super.startPos
                + "], length: [" + super.length
                + "], reference_kind: [" + this.reference_kind.value
                + "], reference_index = [" + this.reference_index.value + "]";
    }

    @Override
    public String getMessageKey() {
        return "msg_const_methodhandle";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_MethodHandle.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return String.format("reference_kind=%s reference_index=%s",
                ReferenceKind.name(this.reference_kind.value),
                constantPool[this.reference_index.value].toString(constantPool));
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                super.startPos + 1,
                1,
                "reference_kind",
                this.reference_kind.value + " - " + CONSTANT_MethodHandle_info.ReferenceKind.name(this.reference_kind.value),
                "msg_const_methodhandle_reference_kind",
                Icons.Kind
        );
        this.addNode(parentNode,
                super.startPos + 2,
                2,
                "reference_index",
                this.reference_index.value + " - " + ((ClassFile)classFile).getCPDescription(this.reference_index.value),
                "msg_const_methodhandle_reference_index",
                Icons.Offset
        );
    }

    /**
     * The value denotes the {@code kind} of this method handle, which
     * characterizes its bytecode behavior.
     *
     * @see <a
     * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-5.html#jvms-5.4.3.5">
     * VM Spec: Method Type and Method Handle Resolution
     * </a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum ReferenceKind {

        REF_getField(1),
        REF_getStatic(2),
        REF_putField(3),
        REF_putStatic(4),
        REF_invokeVirtual(5),
        REF_invokeStatic(6),
        REF_invokeSpecial(7),
        REF_newInvokeSpecial(8),
        REF_invokeInterface(9);

        /**
         * ID of the {@link ReferenceKind}.
         */
        public final int value;

        private ReferenceKind(int value) {
            this.value = value;
        }

        /**
         * Get the {@link ReferenceKind} name based on its internal
         * {@link #value}.
         *
         * @param value Internal {@link #value}
         * @return {@link ReferenceKind} name
         */
        public static String name(int value) {
            String result = "Un-recognized";
            for (ReferenceKind item : ReferenceKind.values()) {
                if (item.value == value) {
                    result = item.name();
                    break;
                }
            }

            return result;
        }
    }
}
