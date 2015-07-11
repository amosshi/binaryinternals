/*
 * ConstantMethodHandleInfo.java    12:04 AM, April 28, 2014
 *
 * Copyright 2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The {@code CONSTANT_MethodHandle_info} structure is used to represent a
 * method handle.
 *
 * <pre>
 *    CONSTANT_MethodHandle_info {
 *        u1 tag;
 *        u1 reference_kind;
 *        u2 reference_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.8">
 * VM Spec: The CONSTANT_MethodHandle_info Structure
 * </a>
 */
public class ConstantMethodHandleInfo extends CPInfo {

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

    ConstantMethodHandleInfo(final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(CPInfo.ConstantType.CONSTANT_MethodHandle.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        this.reference_kind = new u1((short) posDataInputStream.readUnsignedByte());
        this.reference_index = new u2(posDataInputStream.readUnsignedShort());
        super.length = LENGTH;
    }

    @Override
    public String getName() {
        return "MethodHandle";
    }

    @Override
    public String getDescription() {
        return "ConstantMethodHandleInfo: Start Position: [" + super.startPos 
                + "], length: [" + super.length 
                + "], reference_kind: [" + this.reference_kind.value
                + "], reference_index = [" + this.reference_index.value + "]";
   }

    /**
     * The value denotes the {@code kind} of this method handle, which
     * characterizes its bytecode behavior.
     *
     * @see <a
     * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-5.html#jvms-5.4.3.5">
     * VM Spec: Method Type and Method Handle Resolution
     * </a>
     */
    public static enum ReferenceKind {

        REF_getField(1),
        REF_getStatic(2),
        REF_putField(3),
        REF_putStatic(4),
        REF_invokeVirtual(5),
        REF_invokeStatic(6),
        REF_invokeSpecial(7),
        REF_newInvokeSpecial(8),
        REF_invokeInterface(9);

        public final int value;

        private ReferenceKind(int value) {
            this.value = value;
        }
    }
}
