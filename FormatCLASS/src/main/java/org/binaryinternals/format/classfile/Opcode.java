/*
 * Opcode.java    September 14, 2007, 10:27 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;

/**
 * Opcode parser to interpret the Java {@code code} byte array into human
 * readable text.
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-6.html">VM
 * Spec: The Java Virtual Machine Instruction Set</a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public final class Opcode {

    private static final Logger LOG = Logger.getLogger(Opcode.class.getName());

    /**
     * Opcode and non {@link ClassFile#constant_pool} index value. Example:
     * <code>bipush + immediate vlaue</code>,
     * <code>lload + local frame vlaue</code>
     */
    private static final String FORMAT_OPCODE_NUMBER = "%s %d";
    private static final String FORMAT_OPCODE_LOCAL_IINC = "%s index = %d const = %d";
    private static final String FORMAT_OPCODE_STRING = "%s %s";

    private Opcode() {
    }

    /**
     * The Java Virtual Machine Instruction Set.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-6.html#jvms-6.5">
     * VM Spec: Instructions
     * </a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum Instruction {

        /**
         * Do nothing.
         */
        nop(java.lang.classfile.Opcode.NOP.bytecode()),
        /**
         * Push null. Push the null object reference onto the operand stack.
         */
        aconst_null(java.lang.classfile.Opcode.ACONST_NULL.bytecode()),
        /**
         * Push int constant -1.
         *
         * An iconst_m1 instruction is type safe if one can validly push the
         * type int onto the incoming operand stack yielding the outgoing type
         * state.
         *
         * Push the int constant <code>i</code> (-1, 0, 1, 2, 3, 4 or 5) onto
         * the operand stack.
         */
        iconst_m1(java.lang.classfile.Opcode.ICONST_M1.bytecode()),
        /**
         * Push int constant 0.
         */
        iconst_0(java.lang.classfile.Opcode.ICONST_0.bytecode()),
        /**
         * Push int constant 1.
         */
        iconst_1(java.lang.classfile.Opcode.ICONST_1.bytecode()),
        /**
         * Push int constant 2.
         */
        iconst_2(java.lang.classfile.Opcode.ICONST_2.bytecode()),
        /**
         * Push int constant 3.
         */
        iconst_3(java.lang.classfile.Opcode.ICONST_3.bytecode()),
        /**
         * Push int constant 4.
         */
        iconst_4(java.lang.classfile.Opcode.ICONST_4.bytecode()),
        /**
         * Push int constant 5.
         */
        iconst_5(java.lang.classfile.Opcode.ICONST_5.bytecode()),
        /**
         * Push long constant 0.
         *
         * Push the long constant <code>l</code> (0 or 1) onto the operand
         * stack.
         */
        lconst_0(java.lang.classfile.Opcode.LCONST_0.bytecode()),
        /**
         * Push long constant 1.
         */
        lconst_1(java.lang.classfile.Opcode.LCONST_1.bytecode()),
        /**
         * Push float 0.0.
         *
         * Push the float constant <code>f</code> (0.0, 1.0, or 2.0) onto the
         * operand stack.
         */
        fconst_0(java.lang.classfile.Opcode.FCONST_0.bytecode()),
        /**
         * Push float 1.0.
         */
        fconst_1(java.lang.classfile.Opcode.FCONST_1.bytecode()),
        /**
         * Push float 2.0.
         */
        fconst_2(java.lang.classfile.Opcode.FCONST_2.bytecode()),
        /**
         * Push double 0.0.
         *
         * Push the double constant <code>d</code> (0.0 or 1.0) onto the operand
         * stack.
         */
        dconst_0(java.lang.classfile.Opcode.DCONST_0.bytecode()),
        /**
         * Push double 1.0.
         */
        dconst_1(java.lang.classfile.Opcode.DCONST_1.bytecode()),
        /**
         * Push the immediate byte value.
         *
         * The immediate byte is sign-extended to an int value. That value is
         * pushed onto the operand stack.
         */
        bipush(java.lang.classfile.Opcode.BIPUSH.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.immediateValue = pdis.readUnsignedByte();
                parsed.opCodeText = String.format(FORMAT_OPCODE_NUMBER, this.name(), parsed.immediateValue);
                return parsed;
            }
        },
        /**
         * Push the immediate short value.
         *
         * The immediate unsigned byte1 and byte2 values are assembled into an
         * intermediate short.
         *
         * The intermediate value is then sign-extended to an int value. That
         * value is pushed onto the operand stack.
         */
        sipush(java.lang.classfile.Opcode.SIPUSH.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.immediateValue = pdis.readUnsignedShort();
                parsed.opCodeText = String.format(FORMAT_OPCODE_NUMBER, this.name(), parsed.immediateValue);
                return parsed;
            }
        },
        /**
         * Push item from runtime constant pool.
         *
         * The index is an unsigned byte that must be a valid index into the
         * runtime constant pool of the current class.
         */
        ldc(java.lang.classfile.Opcode.LDC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedByte();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Push item from run-time constant pool (wide index).
         *
         * The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned
         * 16-bit index into the run-time constant pool of the current class.
         * The index must be a valid index into the run-time constant pool of
         * the current class.
         */
        ldc_w(java.lang.classfile.Opcode.LDC_W.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Push long or double from runtime constant pool (wide index).
         *
         * The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned
         * 16-bit index into the run-time constant pool of the current class.
         */
        ldc2_w(java.lang.classfile.Opcode.LDC2_W.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Load int from local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The local variable at
         * index must contain an int. The value of the local variable at index
         * is pushed onto the operand stack.
         */
        iload(java.lang.classfile.Opcode.ILOAD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Load long from local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte. Both index and index+1
         * must be indices into the local variable array of the current frame.
         * The local variable at index must contain a long. The value of the
         * local variable at index is pushed onto the operand stack.
         */
        lload(java.lang.classfile.Opcode.LLOAD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Load float from local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The local variable at
         * index must contain a float. The value of the local variable at index
         * is pushed onto the operand stack.
         */
        fload(java.lang.classfile.Opcode.FLOAD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Load double from local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte. Both index and index+1
         * must be indices into the local variable array of the current frame.
         * The local variable at index must contain a double. The value of the
         * local variable at index is pushed onto the operand stack.
         */
        dload(java.lang.classfile.Opcode.DLOAD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Load reference from local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The local variable at
         * index must contain a reference. The objectref in the local variable
         * at index is pushed onto the operand stack.
         */
        aload(java.lang.classfile.Opcode.ALOAD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Load int from local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The local variable at <code>n</code> must contain
         * an int. The value of the local variable at <code>n</code> is pushed
         * onto the operand stack.
         *
         * @see #iload_1
         * @see #iload_2
         * @see #iload_3
         */
        iload_0(java.lang.classfile.Opcode.ILOAD_0.bytecode()),
        /**
         * Load int from local variable at index 1.
         *
         * @see #iload_0
         */
        iload_1(java.lang.classfile.Opcode.ILOAD_1.bytecode()),
        /**
         * Load int from local variable at index 2.
         *
         * @see #iload_0
         */
        iload_2(java.lang.classfile.Opcode.ILOAD_2.bytecode()),
        /**
         * Load int from local variable at index 3.
         *
         * @see #iload_0
         */
        iload_3(java.lang.classfile.Opcode.ILOAD_3.bytecode()),
        /**
         * Load long from local variable at index 0.
         *
         * Both <code>n</code> and <code>n+1</code> must be indices into the
         * local variable array of the current frame. The local variable at
         * <code>n</code> must contain a long. The value of the local variable
         * at <code>n</code> is pushed onto the operand stack.
         *
         * @see #lload_1
         * @see #lload_2
         * @see #lload_3
         */
        lload_0(java.lang.classfile.Opcode.LLOAD_0.bytecode()),
        /**
         * Load long from local variable at index 1.
         *
         * @see #lload_0
         */
        lload_1(java.lang.classfile.Opcode.LLOAD_1.bytecode()),
        /**
         * Load long from local variable at index 2.
         *
         * @see #lload_0
         */
        lload_2(java.lang.classfile.Opcode.LLOAD_2.bytecode()),
        /**
         * Load long from local variable at index 3.
         *
         * @see #lload_0
         */
        lload_3(java.lang.classfile.Opcode.LLOAD_3.bytecode()),
        /**
         * Load float from local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The local variable at <code>n</code> must contain
         * a float. The value of the local variable at <code>n</code> is pushed
         * onto the operand stack.
         *
         * @see #fload_1
         * @see #fload_2
         * @see #fload_3
         */
        fload_0(java.lang.classfile.Opcode.FLOAD_0.bytecode()),
        /**
         * Load float from local variable at index 1.
         *
         * @see #fload_0
         */
        fload_1(java.lang.classfile.Opcode.FLOAD_1.bytecode()),
        /**
         * Load float from local variable at index 2.
         *
         * @see #fload_0
         */
        fload_2(java.lang.classfile.Opcode.FLOAD_2.bytecode()),
        /**
         * Load float from local variable at index 3.
         *
         * @see #fload_0
         */
        fload_3(java.lang.classfile.Opcode.FLOAD_3.bytecode()),
        /**
         * Load double from local variable at index 0.
         *
         * Both <code>n</code> and <code>n+1</code> must be indices into the
         * local variable array of the current frame. The local variable at
         * <code>n</code> must contain a double. The value of the local variable
         * at <code>n</code> is pushed onto the operand stack.
         *
         * @see #dload_1
         * @see #dload_2
         * @see #dload_3
         */
        dload_0(java.lang.classfile.Opcode.DLOAD_0.bytecode()),
        /**
         * Load double from local variable at index 1.
         *
         * @see #dload_0
         */
        dload_1(java.lang.classfile.Opcode.DLOAD_1.bytecode()),
        /**
         * Load double from local variable at index 2.
         *
         * @see #dload_0
         */
        dload_2(java.lang.classfile.Opcode.DLOAD_2.bytecode()),
        /**
         * Load double from local variable at index 3.
         *
         * @see #dload_0
         */
        dload_3(java.lang.classfile.Opcode.DLOAD_3.bytecode()),
        /**
         * Load reference from local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The local variable at <code>n</code> must contain
         * a reference. The objectref in the local variable at <code>n</code> is
         * pushed onto the operand stack.
         *
         * @see #aload_1
         * @see #aload_2
         * @see #aload_3
         */
        aload_0(java.lang.classfile.Opcode.ALOAD_0.bytecode()),
        /**
         * Load reference from local variable at index 1.
         *
         * @see #aload_0
         */
        aload_1(java.lang.classfile.Opcode.ALOAD_1.bytecode()),
        /**
         * Load reference from local variable at index 2.
         *
         * @see #aload_0
         */
        aload_2(java.lang.classfile.Opcode.ALOAD_2.bytecode()),
        /**
         * Load reference from local variable at index 3.
         *
         * @see #aload_0
         */
        aload_3(java.lang.classfile.Opcode.ALOAD_3.bytecode()),
        /**
         * Load int from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type int. The index must be of type int. Both
         * arrayref and index are popped from the operand stack. The int value
         * in the component of the array at index is retrieved and pushed onto
         * the operand stack.
         */
        iaload(java.lang.classfile.Opcode.IALOAD.bytecode()),
        /**
         * Load long from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type long. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The long
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        laload(java.lang.classfile.Opcode.LALOAD.bytecode()),
        /**
         * Load float from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type float. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The float
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        faload(java.lang.classfile.Opcode.FALOAD.bytecode()),
        /**
         * Load double from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type double. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The double
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        daload(java.lang.classfile.Opcode.DALOAD.bytecode()),
        /**
         * Load reference from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type reference. The index must be of type
         * int. Both arrayref and index are popped from the operand stack. The
         * reference value in the component of the array at index is retrieved
         * and pushed onto the operand stack.
         */
        aaload(java.lang.classfile.Opcode.AALOAD.bytecode()),
        /**
         * Load byte or boolean from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type reference. The index must be of type
         * int. Both arrayref and index are popped from the operand stack. The
         * reference value in the component of the array at index is retrieved
         * and pushed onto the operand stack.
         */
        baload(java.lang.classfile.Opcode.BALOAD.bytecode()),
        /**
         * Load char from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type byte or of type boolean. The index must
         * be of type int. Both arrayref and index are popped from the operand
         * stack. The byte value in the component of the array at index is
         * retrieved, sign-extended to an int value, and pushed onto the top of
         * the operand stack.
         */
        caload(java.lang.classfile.Opcode.CALOAD.bytecode()),
        /**
         * Load short from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type short. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The
         * component of the array at index is retrieved and sign-extended to an
         * int value. That value is pushed onto the operand stack.
         */
        saload(java.lang.classfile.Opcode.SALOAD.bytecode()),
        /**
         * Store int into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The value on the top
         * of the operand stack must be of type int. It is popped from the
         * operand stack, and the value of the local variable at index is set to
         * value.
         */
        istore(java.lang.classfile.Opcode.ISTORE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Store long into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte. Both <code>index</code>
         * and <code>index+1</code> must be indices into the local variable
         * array of the current frame. The value on the top of the operand stack
         * must be of type long. It is popped from the operand stack, and the
         * local variables at <code>index</code> and <code>index+1</code> are
         * set to value.
         */
        lstore(java.lang.classfile.Opcode.LSTORE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Store float into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The value on the top
         * of the operand stack must be of type float. It is popped from the
         * operand stack and undergoes value set conversion, resulting in value.
         * The value of the local variable at index is set to value.
         */
        fstore(java.lang.classfile.Opcode.FSTORE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Store double into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte. Both <code>index</code>
         * and <code>index+1</code> must be indices into the local variable
         * array of the current frame. The value on the top of the operand stack
         * must be of type double. It is popped from the operand stack and
         * undergoes value set conversion, resulting in value. The local
         * variables at <code>index</code> and <code>index+1</code> are set to
         * value.
         */
        dstore(java.lang.classfile.Opcode.DSTORE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Store reference into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The objectref on the
         * top of the operand stack must be of type returnAddress or of type
         * reference. It is popped from the operand stack, and the value of the
         * local variable at <code>index</code> is set to objectref.
         */
        astore(java.lang.classfile.Opcode.ASTORE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Store int into local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The value on the top of the operand stack must be
         * of type int. It is popped from the operand stack, and the value of
         * the local variable at <code>n</code> is set to value.
         *
         * @see #istore_1
         * @see #istore_2
         * @see #istore_3
         */
        istore_0(java.lang.classfile.Opcode.ISTORE_0.bytecode()),
        /**
         * Store int into local variable at index 1.
         *
         * @see #istore_0
         */
        istore_1(java.lang.classfile.Opcode.ISTORE_1.bytecode()),
        /**
         * Store int into local variable at index 2.
         *
         * @see #istore_0
         */
        istore_2(java.lang.classfile.Opcode.ISTORE_2.bytecode()),
        /**
         * Store int into local variable at index 3.
         *
         * @see #istore_0
         */
        istore_3(java.lang.classfile.Opcode.ISTORE_3.bytecode()),
        /**
         * Store long into local variable at index 0.
         *
         * Both <code>n</code> and <code>n+1</code> must be indices into the
         * local variable array of the current frame. The value on the top of
         * the operand stack must be of type long. It is popped from the operand
         * stack, and the local variables at <code>n</code> and <code>n+1</code>
         * are set to value.
         *
         * @see #lstore_1
         * @see #lstore_2
         * @see #lstore_3
         */
        lstore_0(java.lang.classfile.Opcode.LSTORE_0.bytecode()),
        /**
         * Store long into local variable at index 1.
         *
         * @see #lstore_0
         */
        lstore_1(java.lang.classfile.Opcode.LSTORE_1.bytecode()),
        /**
         * Store long into local variable at index 2.
         *
         * @see #lstore_0
         */
        lstore_2(java.lang.classfile.Opcode.LSTORE_2.bytecode()),
        /**
         * Store long into local variable at index 3.
         *
         * @see #lstore_0
         */
        lstore_3(java.lang.classfile.Opcode.LSTORE_3.bytecode()),
        /**
         * Store float into local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The value on the top of the operand stack must be
         * of type float. It is popped from the operand stack and undergoes
         * value set conversion, resulting in value. The value of the local
         * variable at <code>n</code> is set to value.
         *
         * @see #fstore_1
         * @see #fstore_2
         * @see #fstore_3
         */
        fstore_0(java.lang.classfile.Opcode.FSTORE_0.bytecode()),
        /**
         * Store float into local variable at index 1.
         *
         * @see #fstore_0
         */
        fstore_1(java.lang.classfile.Opcode.FSTORE_1.bytecode()),
        /**
         * Store float into local variable at index 2.
         *
         * @see #fstore_0
         */
        fstore_2(java.lang.classfile.Opcode.FSTORE_2.bytecode()),
        /**
         * Store float into local variable at index 3.
         *
         * @see #fstore_0
         */
        fstore_3(java.lang.classfile.Opcode.FSTORE_3.bytecode()),
        /**
         * Store double into local variable at index 0.
         *
         * Both <code>n</code> and <code>n+1</code> must be indices into the
         * local variable array of the current frame. The value on the top of
         * the operand stack must be of type double. It is popped from the
         * operand stack and undergoes value set conversion, resulting in value.
         * The local variables at <code>n</code> and <code>n+1</code>are set to
         * value.
         *
         * @see #dstore_1
         * @see #dstore_2
         * @see #dstore_3
         */
        dstore_0(java.lang.classfile.Opcode.DSTORE_0.bytecode()),
        /**
         * Store double into local variable at index 1.
         *
         * @see #dstore_0
         */
        dstore_1(java.lang.classfile.Opcode.DSTORE_1.bytecode()),
        /**
         * Store double into local variable at index 2.
         *
         * @see #dstore_0
         */
        dstore_2(java.lang.classfile.Opcode.DSTORE_2.bytecode()),
        /**
         * Store double into local variable at index 3.
         *
         * @see #dstore_0
         */
        dstore_3(java.lang.classfile.Opcode.DSTORE_3.bytecode()),
        /**
         * Store reference into local variable at index 0.
         *
         * The <code>n</code> must be an index into the local variable array of
         * the current frame. The objectref on the top of the operand stack must
         * be of type returnAddress or of type reference. It is popped from the
         * operand stack, and the value of the local variable at <code>n</code>
         * is set to objectref.
         *
         * @see #astore_1
         * @see #astore_2
         * @see #astore_3
         */
        astore_0(java.lang.classfile.Opcode.ASTORE_0.bytecode()),
        /**
         * Store reference into local variable at index 1.
         *
         * @see #astore_0
         */
        astore_1(java.lang.classfile.Opcode.ASTORE_1.bytecode()),
        /**
         * Store reference into local variable at index 2.
         *
         * @see #astore_0
         */
        astore_2(java.lang.classfile.Opcode.ASTORE_2.bytecode()),
        /**
         * Store reference into local variable at index 3.
         *
         * @see #astore_0
         */
        astore_3(java.lang.classfile.Opcode.ASTORE_3.bytecode()),
        /**
         * Store into int array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         *
         * The <code>arrayref</code> must be of type reference and must refer to
         * an array whose components are of type int. Both index and value must
         * be of type int. The <code>arrayref</code>, <code>index</code>, and
         * <code>value</code> are popped from the operand stack. The int value
         * is stored as the component of the array indexed by index.
         */
        iastore(java.lang.classfile.Opcode.IASTORE.bytecode()),
        /**
         * Store into long array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        lastore(java.lang.classfile.Opcode.LASTORE.bytecode()),
        /**
         * Store into float array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        fastore(java.lang.classfile.Opcode.FASTORE.bytecode()),
        /**
         * Store into double array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        dastore(java.lang.classfile.Opcode.DASTORE.bytecode()),
        /**
         * Store into reference array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        aastore(java.lang.classfile.Opcode.AASTORE.bytecode()),
        /**
         * Store into byte or boolean array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        bastore(java.lang.classfile.Opcode.BASTORE.bytecode()),
        /**
         * Store into char array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        castore(java.lang.classfile.Opcode.CASTORE.bytecode()),
        /**
         * Store into short array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        sastore(java.lang.classfile.Opcode.SASTORE.bytecode()),
        /**
         * Pop the top value from the operand stack.
         */
        pop(java.lang.classfile.Opcode.POP.bytecode()),
        /**
         * Pop the top one or two values from the operand stack.
         */
        pop2(java.lang.classfile.Opcode.POP2.bytecode()),
        /**
         * Duplicate the top value on the operand stack and push the duplicated
         * value onto the operand stack.
         */
        dup(java.lang.classfile.Opcode.DUP.bytecode()),
        /**
         * Duplicate the top value on the operand stack and insert the
         * duplicated value two values down in the operand stack.
         */
        dup_x1(java.lang.classfile.Opcode.DUP_X1.bytecode()),
        /**
         * Duplicate the top value on the operand stack and insert the
         * duplicated value two or three values down in the operand stack.
         */
        dup_x2(java.lang.classfile.Opcode.DUP_X2.bytecode()),
        /**
         * Duplicate the top one or two values on the operand stack and push the
         * duplicated value or values back onto the operand stack in the
         * original order.
         */
        dup2(java.lang.classfile.Opcode.DUP2.bytecode()),
        /**
         * Duplicate the top one or two values on the operand stack and insert
         * the duplicated values, in the original order, one value beneath the
         * original value or values in the operand stack.
         */
        dup2_x1(java.lang.classfile.Opcode.DUP2_X1.bytecode()),
        /**
         * Duplicate the top one or two values on the operand stack and insert
         * the duplicated values, in the original order, into the operand stack.
         */
        dup2_x2(java.lang.classfile.Opcode.DUP2_X2.bytecode()),
        /**
         * Swap the top two values on the operand stack.
         */
        swap(java.lang.classfile.Opcode.SWAP.bytecode()),
        /**
         * Add int in current Operand Stack <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. The int result is value1 + value2. The result
         * is pushed onto the operand stack.
         *
         * @see #ladd
         * @see #fadd
         * @see #dadd
         */
        iadd(java.lang.classfile.Opcode.IADD.bytecode()),
        /**
         * Add long in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        ladd(java.lang.classfile.Opcode.LADD.bytecode()),
        /**
         * Add float in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        fadd(java.lang.classfile.Opcode.FADD.bytecode()),
        /**
         * Add double in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        dadd(java.lang.classfile.Opcode.DADD.bytecode()),
        /**
         * Subtract int in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. The int result is value1 - value2. The result
         * is pushed onto the operand stack.
         *
         * @see #lsub
         * @see #fsub
         * @see #dsub
         */
        isub(java.lang.classfile.Opcode.ISUB.bytecode()),
        /**
         * Subtract long in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type long. The values are popped
         * from the operand stack. The long result is value1 - value2. The
         * result is pushed onto the operand stack.
         *
         * @see #isub
         */
        lsub(java.lang.classfile.Opcode.LSUB.bytecode()),
        /**
         * Subtract float.
         *
         * @see #isub
         */
        fsub(java.lang.classfile.Opcode.FSUB.bytecode()),
        /**
         * Subtract double.
         *
         * @see #isub
         */
        dsub(java.lang.classfile.Opcode.DSUB.bytecode()),
        /**
         * Multiply int in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. The int result is value1 * value2. The result
         * is pushed onto the operand stack.
         *
         * @see #lmul
         * @see #fmul
         * @see #dmul
         */
        imul(java.lang.classfile.Opcode.IMUL.bytecode()),
        /**
         * Multiply long.
         *
         * @see #imul
         */
        lmul(java.lang.classfile.Opcode.LMUL.bytecode()),
        /**
         * Multiply float.
         *
         * @see #imul
         */
        fmul(java.lang.classfile.Opcode.FMUL.bytecode()),
        /**
         * Multiply double.
         *
         * @see #imul
         */
        dmul(java.lang.classfile.Opcode.DMUL.bytecode()),
        /**
         * Divide int in current Operand Stack <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. The int result is the value of the Java
         * programming language expression value1 / value2. The result is pushed
         * onto the operand stack.
         *
         * @see #ldiv
         * @see #fdiv
         * @see #ddiv
         */
        idiv(java.lang.classfile.Opcode.IDIV.bytecode()),
        /**
         * Divide long.
         *
         * @see #idiv
         */
        ldiv(java.lang.classfile.Opcode.LDIV.bytecode()),
        /**
         * Divide float.
         *
         * @see #idiv
         */
        fdiv(java.lang.classfile.Opcode.FDIV.bytecode()),
        /**
         * Divide double.
         *
         * @see #idiv
         */
        ddiv(java.lang.classfile.Opcode.DDIV.bytecode()),
        /**
         * Remainder int in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. The int result is
         * <code>value1 - (value1 / value2) * value2</code>. The result is
         * pushed onto the operand stack.
         *
         * @see #lrem
         * @see #frem
         * @see #drem
         */
        irem(java.lang.classfile.Opcode.IREM.bytecode()),
        /**
         * Remainder long.
         *
         * @see #irem
         */
        lrem(java.lang.classfile.Opcode.LREM.bytecode()),
        /**
         * Remainder float.
         *
         * @see #irem
         */
        frem(java.lang.classfile.Opcode.FREM.bytecode()),
        /**
         * Remainder double.
         *
         * @see #irem
         */
        drem(java.lang.classfile.Opcode.DREM.bytecode()),
        /**
         * Negate int in current Operand Stack <code>..., value</code>.
         *
         * The value must be of type int. It is popped from the operand stack.
         * The int result is the arithmetic negation of <code>value</code>,
         * <code>-value</code>. The result is pushed onto the operand stack.
         *
         * @see #lneg
         * @see #fneg
         * @see #dneg
         */
        ineg(java.lang.classfile.Opcode.INEG.bytecode()),
        /**
         * Negate long.
         *
         * @see #ineg
         */
        lneg(java.lang.classfile.Opcode.LNEG.bytecode()),
        /**
         * Negate float.
         *
         * @see #ineg
         */
        fneg(java.lang.classfile.Opcode.FNEG.bytecode()),
        /**
         * Negate double.
         *
         * @see #ineg
         */
        dneg(java.lang.classfile.Opcode.DNEG.bytecode()),
        /**
         * Shift left int in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. An int result is calculated by shifting
         * value1 left by s bit positions, where s is the value of the low 5
         * bits of value2. The result is pushed onto the operand stack.
         *
         * @see #lshl
         */
        ishl(java.lang.classfile.Opcode.ISHL.bytecode()),
        /**
         * Shift left long.
         *
         * @see #ishl
         */
        lshl(java.lang.classfile.Opcode.LSHL.bytecode()),
        /**
         * Arithmetic shift right int, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. An int result is calculated by shifting
         * value1 right by s bit positions, with sign extension, where s is the
         * value of the low 5 bits of value2. The result is pushed onto the
         * operand stack.
         *
         * @see #lshr
         */
        ishr(java.lang.classfile.Opcode.ISHR.bytecode()),
        /**
         * Arithmetic shift right long.
         *
         * @see #ishr
         */
        lshr(java.lang.classfile.Opcode.LSHR.bytecode()),
        /**
         * Logical shift right int, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. The values are popped
         * from the operand stack. An int result is calculated by shifting
         * value1 right by s bit positions, with zero extension, where s is the
         * value of the low 5 bits of value2. The result is pushed onto the
         * operand stack.
         *
         * @see #lushr
         */
        iushr(java.lang.classfile.Opcode.IUSHR.bytecode()),
        /**
         * Logical shift right long.
         *
         * @see #iushr
         */
        lushr(java.lang.classfile.Opcode.LUSHR.bytecode()),
        /**
         * Boolean AND int, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. They are popped from the
         * operand stack. An int result is calculated by taking the bitwise AND
         * (conjunction) of value1 and value2. The result is pushed onto the
         * operand stack.
         *
         * @see #land
         */
        iand(java.lang.classfile.Opcode.IAND.bytecode()),
        /**
         * Boolean AND long.
         *
         * @see #iand
         */
        land(java.lang.classfile.Opcode.LAND.bytecode()),
        /**
         * Boolean OR int, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. They are popped from the
         * operand stack. An int result is calculated by taking the bitwise
         * inclusive OR of value1 and value2. The result is pushed onto the
         * operand stack.
         *
         * @see #lor
         */
        ior(java.lang.classfile.Opcode.IOR.bytecode()),
        /**
         * Boolean OR long.
         *
         * @see #ior
         */
        lor(java.lang.classfile.Opcode.LOR.bytecode()),
        /**
         * Boolean XOR int, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Both value1 and value2 must be of type int. They are popped from the
         * operand stack. An int result is calculated by taking the bitwise
         * exclusive OR of value1 and value2. The result is pushed onto the
         * operand stack.
         *
         * @see #lxor
         */
        ixor(java.lang.classfile.Opcode.IXOR.bytecode()),
        /**
         * Boolean XOR long.
         *
         * @see #ixor
         */
        lxor(java.lang.classfile.Opcode.LXOR.bytecode()),
        /**
         * Increment local variable by constant.
         *
         * The index is an unsigned byte that must be an index into the local
         * variable array of the current frame. The const is an immediate signed
         * byte. The local variable at index must contain an int. The value
         * const is first sign-extended to an int, and then the local variable
         * at index is incremented by that amount.
         */
        iinc(java.lang.classfile.Opcode.IINC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.lvIndex = pdis.readUnsignedByte();
                parsed.immediateValue = Integer.valueOf(pdis.readByte());
                parsed.opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, this.name(), parsed.lvIndex, parsed.immediateValue);
                return parsed;
            }
        },
        /**
         * Convert int to long, in current Operand Stack
         * <code>..., value</code>.
         *
         * The value on the top of the operand stack must be of type int. It is
         * popped from the operand stack and sign-extended to a long result.
         * That result is pushed onto the operand stack.
         */
        i2l(java.lang.classfile.Opcode.I2L.bytecode()),
        /**
         * Convert int to float.
         *
         * @see #i2l
         */
        i2f(java.lang.classfile.Opcode.I2F.bytecode()),
        /**
         * Convert int to double.
         *
         * @see #i2l
         */
        i2d(java.lang.classfile.Opcode.I2D.bytecode()),
        /**
         * Convert long to int, in current Operand Stack
         * <code>..., value</code>.
         *
         * The value on the top of the operand stack must be of type long. It is
         * popped from the operand stack and converted to an int result by
         * taking the low-order 32 bits of the long value and discarding the
         * high-order 32 bits. The result is pushed onto the operand stack.
         */
        l2i(java.lang.classfile.Opcode.L2I.bytecode()),
        /**
         * Convert long to float, in current Operand Stack
         * <code>..., value</code>.
         *
         * The l2f instruction performs a widening primitive conversion that may
         * lose precision because values of type float have only 24 significand
         * bits.
         */
        l2f(java.lang.classfile.Opcode.L2F.bytecode()),
        /**
         * Convert long to double, in current Operand Stack
         * <code>..., value</code>.
         *
         * The value on the top of the operand stack must be of type long. It is
         * popped from the operand stack and converted to a double result using
         * IEEE 754 round to nearest mode. The result is pushed onto the operand
         * stack.
         */
        l2d(java.lang.classfile.Opcode.L2D.bytecode()),
        /**
         * Convert float to int, in current Operand Stack
         * <code>..., value</code>.
         */
        f2i(java.lang.classfile.Opcode.F2I.bytecode()),
        /**
         * Convert float to long, in current Operand Stack
         * <code>..., value</code>.
         */
        f2l(java.lang.classfile.Opcode.F2L.bytecode()),
        /**
         * Convert float to double, in current Operand Stack
         * <code>..., value</code>.
         */
        f2d(java.lang.classfile.Opcode.F2D.bytecode()),
        /**
         * Convert double to int, in current Operand Stack
         * <code>..., value</code>.
         */
        d2i(java.lang.classfile.Opcode.D2I.bytecode()),
        /**
         * Convert double to long, in current Operand Stack
         * <code>..., value</code>.
         */
        d2l(java.lang.classfile.Opcode.D2L.bytecode()),
        /**
         * Convert double to float, in current Operand Stack
         * <code>..., value</code>.
         */
        d2f(java.lang.classfile.Opcode.D2F.bytecode()),
        /**
         * Convert int to byte.
         */
        i2b(java.lang.classfile.Opcode.I2B.bytecode()),
        /**
         * Convert int to char.
         */
        i2c(java.lang.classfile.Opcode.I2C.bytecode()),
        /**
         * Convert int to short.
         */
        i2s(java.lang.classfile.Opcode.I2S.bytecode()),
        /**
         * Compare long, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * Compare results are 1, 0, or -1.
         *
         * Both value1 and value2 must be of type long. They are both popped
         * from the operand stack, and a signed integer comparison is performed.
         * If value1 is greater than value2, the int value 1 is pushed onto the
         * operand stack. If value1 is equal to value2, the int value 0 is
         * pushed onto the operand stack. If value1 is less than value2, the int
         * value -1 is pushed onto the operand stack.
         */
        lcmp(java.lang.classfile.Opcode.LCMP.bytecode()),
        /**
         * Compare float, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * @see #lcmp
         * @see #fcmpg
         */
        fcmpl(java.lang.classfile.Opcode.FCMPL.bytecode()),
        /**
         * Compare float, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * The {@link #fcmpg} and {@link #fcmpl} instructions differ only in
         * their treatment of a comparison involving NaN.
         *
         * @see #lcmp
         * @see #fcmpl
         */
        fcmpg(java.lang.classfile.Opcode.FCMPG.bytecode()),
        /**
         * Compare double, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * @see #lcmp
         * @see #dcmpg
         */
        dcmpl(java.lang.classfile.Opcode.DCMPL.bytecode()),
        /**
         * Compare double, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * The {@link #dcmpg} and {@link #dcmpl} instructions differ only in
         * their treatment of a comparison involving NaN.
         *
         * @see #lcmp
         * @see #dcmpl
         */
        dcmpg(java.lang.classfile.Opcode.DCMPG.bytecode()),
        /**
         * Branch if int comparison with zero succeeds.
         *
         * ifeq succeeds if and only if value = 0.
         */
        ifeq(java.lang.classfile.Opcode.IFEQ.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        ifne(java.lang.classfile.Opcode.IFNE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        iflt(java.lang.classfile.Opcode.IFLT.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        ifge(java.lang.classfile.Opcode.IFGE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        ifgt(java.lang.classfile.Opcode.IFGT.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        ifle(java.lang.classfile.Opcode.IFLE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmpeq(java.lang.classfile.Opcode.IF_ICMPEQ.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmpne(java.lang.classfile.Opcode.IF_ICMPNE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmplt(java.lang.classfile.Opcode.IF_ICMPLT.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmpge(java.lang.classfile.Opcode.IF_ICMPGE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmpgt(java.lang.classfile.Opcode.IF_ICMPGT.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_icmple(java.lang.classfile.Opcode.IF_ICMPLE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_acmpeq(java.lang.classfile.Opcode.IF_ACMPEQ.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        if_acmpne(java.lang.classfile.Opcode.IF_ACMPNE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        /**
         * Branch always.
         *
         * Note: Add '_' suffix to avoid compile error since 'goto' is a Java
         * keyword, remove the '_' from the name when showing.
         *
         * @see #getName()
         */
        goto_(java.lang.classfile.Opcode.GOTO.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.branchbyte = Integer.valueOf(pdis.readShort());
                parsed.opCodeText = this.getName();
                return parsed;
            }
        },
        /**
         * Jump subroutine.
         */
        jsr(java.lang.classfile.Opcode.JSR.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseBranchbyteShort(curPos, pdis);
            }
        },
        /**
         * Return from subroutine.
         *
         * The index is an unsigned byte between 0 and 255, inclusive. The local
         * variable at index in the current frame must contain a value of type
         * returnAddress. The contents of the local variable are written into
         * the Java Virtual Machine's pc register, and execution continues
         * there.
         */
        ret(java.lang.classfile.Opcode.RET.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseLvindexUnsignedByte(curPos, pdis);
            }
        },
        /**
         * Access jump table by index and jump.
         */
        tableswitch(java.lang.classfile.Opcode.TABLESWITCH.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                // 0-3 byte pad, when needed
                super.skipPad(pdis);

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);

                parsed.tableSwitch = new tableswitch(pdis.readInt(), pdis.readInt(), pdis.readInt());
                for (int i = parsed.tableSwitch.lowbyte; i <= parsed.tableSwitch.highbyte; i++) {
                    parsed.tableSwitch.jumpoffsets.put(i, pdis.readInt());
                }

                parsed.opCodeText = String.format(FORMAT_OPCODE_STRING, this.name(), parsed.tableSwitch.toString(curPos));
                return parsed;
            }
        },
        /**
         * Access jump table by key match and jump.
         */
        lookupswitch(java.lang.classfile.Opcode.LOOKUPSWITCH.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                // 0-3 byte pad, when needed
                super.skipPad(pdis);

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);

                parsed.lookupSwitch = new lookupswitch(pdis.readInt(), pdis.readInt());
                for (int i = 0; i < parsed.lookupSwitch.npairs; i++) {
                    parsed.lookupSwitch.mapoffsets.put(pdis.readInt(), pdis.readInt());
                }

                parsed.opCodeText = String.format(FORMAT_OPCODE_STRING, this.name(), parsed.lookupSwitch.toString(curPos));
                return parsed;
            }
        },
        /**
         * Return int from method.
         *
         * The current method must have return type boolean, byte, char, short,
         * or int. The value must be of type int.
         *
         * If the current method is a synchronized method, the monitor entered
         * or reentered on invocation of the method is updated and possibly
         * exited as if by execution of a {@link #monitorexit} instruction in
         * the current thread.
         */
        ireturn(java.lang.classfile.Opcode.IRETURN.bytecode()),
        /**
         * Return long from method.
         *
         * The current method must have return type long. The value must be of
         * type long.
         *
         * @see #ireturn
         */
        lreturn(java.lang.classfile.Opcode.LRETURN.bytecode()),
        /**
         * Return float from method.
         *
         * The current method must have return type float. The value must be of
         * type float.
         *
         * @see #ireturn
         */
        freturn(java.lang.classfile.Opcode.FRETURN.bytecode()),
        /**
         * Return double from method.
         *
         * The current method must have return type double. The value must be of
         * type double.
         *
         * @see #ireturn
         */
        dreturn(java.lang.classfile.Opcode.DRETURN.bytecode()),
        /**
         * Return reference from method.
         *
         * The objectref must be of type reference and must refer to an object
         * of a type that is assignment compatible with the type represented by
         * the return descriptor of the current method.
         *
         * @see #ireturn
         * @see method_info#getDeclaration()
         */
        areturn(java.lang.classfile.Opcode.ARETURN.bytecode()),
        /**
         * Return void from method.
         *
         * The current method must have return type void.
         *
         * Note: Add '_' suffix to avoid compile error since 'return' is a Java
         * keyword, remove the '_' from the name when showing.
         *
         * @see #ireturn
         * @see #getName()
         */
        return_(java.lang.classfile.Opcode.RETURN.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.opCodeText = this.getName();
                return parsed;
            }
        },
        /**
         * Get static field from class.
         *
         * The value of the class or interface field is fetched and pushed onto
         * the operand stack.
         */
        getstatic(java.lang.classfile.Opcode.GETSTATIC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Set static field in class.
         *
         * On successful resolution of the field, the class or interface that
         * declared the resolved field is initialized if that class or interface
         * has not already been initialized.
         */
        putstatic(java.lang.classfile.Opcode.PUTSTATIC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Fetch field from object.
         */
        getfield(java.lang.classfile.Opcode.GETFIELD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Set field in object.
         */
        putfield(java.lang.classfile.Opcode.PUTFIELD.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke instance method; dispatch based on class.
         */
        invokevirtual(java.lang.classfile.Opcode.INVOKEVIRTUAL.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke instance method; direct invocation of instance initialization
         * methods and methods of the current class and its super types.
         */
        invokespecial(java.lang.classfile.Opcode.INVOKESPECIAL.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke a class (static) method.
         */
        invokestatic(java.lang.classfile.Opcode.INVOKESTATIC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke interface method.
         */
        invokeinterface(java.lang.classfile.Opcode.INVOKEINTERFACE.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                parsed.nArgs = pdis.readUnsignedByte();
                BytesTool.skip(pdis, 1);

                parsed.opCodeText = String.format("%s interface=%d, nargs=%d", this.name(), parsed.cpIndex, parsed.nArgs);
                return parsed;
            }
        },
        /**
         * Invoke a dynamically-computed call site.
         */
        invokedynamic(java.lang.classfile.Opcode.INVOKEDYNAMIC.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();

                // Skip 2 zero bytes
                BytesTool.skip(pdis, 2);
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Create new object.
         *
         * Note: Add '_' suffix to avoid compile error since 'new' is a Java
         * keyword, remove the '_' from the name when showing.
         *
         * @see #getName()
         */
        new_(java.lang.classfile.Opcode.NEW.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                parsed.opCodeText = this.getName();
                return parsed;
            }
        },
        /**
         * Create new array.
         *
         * The atype is a code that indicates the type of array to create. It
         * must take one of the following values in {@link NewarrayType}.
         *
         */
        newarray(java.lang.classfile.Opcode.NEWARRAY.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.arrayType = pdis.readUnsignedByte();
                parsed.opCodeText = String.format(FORMAT_OPCODE_STRING, Opcode.Instruction.newarray.name(), NewarrayType.valueOf(parsed.arrayType).name());
                return parsed;
            }
        },
        /**
         * Create new array of reference.
         *
         * The {@link #anewarray} instruction is used to create a single
         * dimension of an array of object references or part of a
         * multidimensional array.
         */
        anewarray(java.lang.classfile.Opcode.ANEWARRAY.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Get length of array.
         */
        arraylength(java.lang.classfile.Opcode.ARRAYLENGTH.bytecode()),
        /**
         * Throw exception or error.
         */
        athrow(java.lang.classfile.Opcode.ATHROW.bytecode()),
        /**
         * Check whether object is of given type.
         */
        checkcast(java.lang.classfile.Opcode.CHECKCAST.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parseCpindexUnsignedShort(curPos, pdis);
            }
        },
        /**
         * Determine if object is of given type.
         *
         * Note: Add '_' suffix to avoid compile error since 'instanceof' is a
         * Java keyword, remove the '_' from the name when showing.
         *
         * @see #getName()
         */
        instanceof_(java.lang.classfile.Opcode.INSTANCEOF.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                parsed.opCodeText = this.getName();
                return parsed;
            }
        },
        /**
         * Enter monitor for object.
         *
         * The objectref must be of type reference.
         *
         * Each object is associated with a monitor. A monitor is locked if and
         * only if it has an owner. The thread that executes
         * {@link #monitorenter} attempts to gain ownership of the monitor
         * associated with objectref,
         */
        monitorenter(java.lang.classfile.Opcode.MONITORENTER.bytecode()),
        /**
         * Exit monitor for object.
         *
         * The objectref must be of type reference.
         *
         * The thread that executes {@link #monitorexit} must be the owner of
         * the monitor associated with the instance referenced by objectref.
         */
        monitorexit(java.lang.classfile.Opcode.MONITOREXIT.bytecode()),
        /**
         * Extend local variable index by additional bytes.
         */
        wide(196) {
            /**
             * The opcode with wide targets.
             */
            private int wide_opcode;

            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                wide_opcode = pdis.readUnsignedByte();
                InstructionParsed parsed = new InstructionParsed(curPos, wide_opcode);
                parsed.isWide = true;

                String opCodeText;
                if (WIDE_SINGLE_OPCODES.contains(wide_opcode)) {
                    parsed.lvIndex = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, getWideName(Opcode.Instruction.valueOf(wide_opcode).name()), parsed.lvIndex);
                } else if (wide_opcode == Opcode.Instruction.iinc.code) {
                    parsed.lvIndex = pdis.readUnsignedShort();
                    parsed.immediateValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, getWideName(Opcode.Instruction.iinc.name()), parsed.lvIndex, parsed.immediateValue);
                } else {
                    opCodeText = String.format("%s [Unknown opcode]", Opcode.Instruction.wide.name());
                }

                parsed.opCodeText = opCodeText;
                return parsed;
            }

            @Override
            String getName() {
                return getWideName(Opcode.Instruction.valueOf(this.wide_opcode).name());
            }

            /**
             * Get the name with "wide " prefix. Only applied for {@link #wide}.
             *
             * @return opcode name with "wide " prefix
             */
            String getWideName(String s) {
                return Instruction.wide.name() + " " + s;
            }
        },
        /**
         * Create new multidimensional array.
         */
        multianewarray(java.lang.classfile.Opcode.MULTIANEWARRAY.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                int dimensions = pdis.readUnsignedByte();
                parsed.opCodeText = String.format("%s type=%d dimensions=%d", this.name(), parsed.cpIndex, dimensions);
                return parsed;
            }
        },
        /**
         * Branch if reference is null.
         */
        ifnull(java.lang.classfile.Opcode.IFNULL.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.branchbyte = pdis.readUnsignedShort();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Branch if reference not null.
         */
        ifnonnull(java.lang.classfile.Opcode.IFNONNULL.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.branchbyte = pdis.readUnsignedShort();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Branch always (wide index).
         */
        goto_w(java.lang.classfile.Opcode.GOTO_W.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.branchbyte = pdis.readInt();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Jump subroutine (wide index).
         */
        jsr_w(java.lang.classfile.Opcode.JSR_W.bytecode()) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.branchbyte = pdis.readInt();
                parsed.opCodeText = this.name();
                return parsed;
            }
        },
        /**
         * Reserved opcode <code>breakpoint</code>.
         */
        breakpoint(202, true),
        /**
         * Reserved opcode <code>impdep1</code>.
         */
        impdep1(254, true),
        /**
         * Reserved opcode <code>impdep2</code>.
         */
        impdep2(255, true),
        /**
         * Unknown Opcode. This should never happen.
         */
        UNKNOWN(-1, true);

        public static final String OPCODE_NAME_RESERVED_PREFIX = "[Reserved] ";
        private static final List<Integer> WIDE_SINGLE_OPCODES = new ArrayList<>();
        static {
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.iload.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.lload.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.fload.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.dload.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.aload.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.istore.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.lstore.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.fstore.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.dstore.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.astore.code);
            WIDE_SINGLE_OPCODES.add(Opcode.Instruction.ret.code);
        }

        /**
         * Internal code for an Instruction.
         */
        public final int code;

        public final boolean reserved;

        Instruction(int i) {
            this(i, false);
        }

        Instruction(int i, boolean r) {
            this.code = i;
            this.reserved = r;
        }

        /**
         * Remove the postfix "_" from the {@link #name()}. Only applied for
         * {@link #goto_}, {@link #return_}, {@link #new_}, and
         * {@link #instanceof_}.
         *
         * @return The postfix "_" from the name
         *
         * @see #goto_
         * @see #return_
         * @see #new_
         * @see #instanceof_
         * @see #reserved
         * @see #wide
         */
        String getName() {
            String name = super.name();

            if (name.endsWith("_")) {
                name = name.substring(0, name.length() - 1);
            }
            if (this.reserved) {
                name = Instruction.OPCODE_NAME_RESERVED_PREFIX + name;
            }

            return name;
        }

        /**
         * Get Opcode name.
         *
         * @param opcode Internal value of an opcode.
         * @return Opcode name
         */
        public static Instruction valueOf(int opcode) {
            Instruction result = Instruction.UNKNOWN;
            for (Instruction i : Instruction.values()) {
                if (i.code == opcode) {
                    result = i;
                    break;
                }
            }

            return result;
        }

        /**
         * <pre>
         * java:S1130 - "throws" declarations should not be superfluous --- The subclass throws IOException
         * java:S1172 - Unused method parameters should be removed --- `pdis` is used by children classes
         * </pre>
         */
        @SuppressWarnings({"java:S1130", "java:S1172"})
        protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.opCodeText = this.name();
            return parsed;
        }

        private InstructionParsed parseBranchbyteShort(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.branchbyte = Integer.valueOf(pdis.readShort());
            parsed.opCodeText = this.name();
            return parsed;
        }

        private InstructionParsed parseCpindexUnsignedShort(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.cpIndex = pdis.readUnsignedShort();
            parsed.opCodeText = this.name();
            return parsed;
        }

        private InstructionParsed parseLvindexUnsignedByte(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.lvIndex = pdis.readUnsignedByte();
            parsed.opCodeText = String.format(FORMAT_OPCODE_NUMBER, this.name(), parsed.lvIndex);
            return parsed;
        }

        /**
         * Skip 0-3 byte pad when needed, for {@link #tableswitch} and
         * {@link #lookupswitch}.
         *
         * @see #tableswitch
         * @see #lookupswitch
         */
        private void skipPad(final PosDataInputStream pdis) throws IOException {
            int skip = pdis.getPos() % 4;
            skip = (skip > 0) ? 4 - skip : skip;
            if (skip > 0) {
                BytesTool.skip(pdis, skip);
            }
        }

    }

    /**
     * Data types used by {@link Instruction#newarray}.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-6.html#jvms-6.5.newarray">
     * VM Spec: Table 6.5.newarray-A. Array type codes
     * </a>
     */
    public enum NewarrayType {

        T_UNKNOWN(-1),
        T_BOOLEAN(4),
        T_CHAR(5),
        T_FLOAT(6),
        T_DOUBLE(7),
        T_BYTE(8),
        T_SHORT(9),
        T_INT(10),
        T_LONG(11);

        public final int atype;

        NewarrayType(int i) {
            this.atype = i;
        }

        /**
         * Get the type name based on {@link #atype}.
         *
         * @param value Value to match {@link #atype}
         * @return Type name corresponding to <code>value</code>
         */
        public static NewarrayType valueOf(int value) {
            NewarrayType v = NewarrayType.T_UNKNOWN;
            for (NewarrayType type : NewarrayType.values()) {
                if (type.atype == value) {
                    v = type;
                    break;
                }
            }
            return v;
        }
    }

    /**
     * Parse the java byte code in a method as a string.
     *
     * @param code Byte array of method source code
     * @return Readable string of the method source code
     */
    public static List<InstructionParsed> parseCode(final byte[] code) {
        if ((code == null) || (code.length < 1)) {
            return new ArrayList<>();
        }

        List<InstructionParsed> codeResult = new ArrayList<>();
        final PosDataInputStream pdis = new PosDataInputStream(new PosByteArrayInputStream(code));
        while (pdis.getPos() < code.length) {
            try {
                final int curPos = pdis.getPos();
                final int opcode = pdis.read();

                codeResult.add(Instruction.valueOf(opcode).parse(curPos, pdis));
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "parseCode() with code length - {0}", code.length);
                LOG.log(Level.SEVERE, ioe.toString(), ioe);
                break;
            }
        }

        return codeResult;
    }

    /**
     * Instruction structure used by {@link Instruction#lookupswitch}.
     *
     * @see Instruction#lookupswitch
     */
    @SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
    public static class lookupswitch {

        public final int defaultbyte;
        public final int npairs;
        public final Map<Integer, Integer> mapoffsets = new LinkedHashMap<>();

        lookupswitch(int defaultByte, int nPairs) {
            this.defaultbyte = defaultByte;
            this.npairs = nPairs;
        }

        public String toString(int currentOffset) {
            final StringBuilder sb = new StringBuilder(256);
            sb.append('(').append(this.npairs).append(" Pairs)");
            this.mapoffsets.keySet().forEach(key -> {
                Integer value = this.mapoffsets.get(key);
                sb.append(String.format("%n    case %d. jump to %d (relative offset = %d)", key, value + currentOffset, value));
            });
            sb.append(String.format("%n    default. jump to %d (relative offset = %d) ", this.defaultbyte + currentOffset, this.defaultbyte));

            return sb.toString();
        }
    }

    /**
     * Instruction structure used by {@link Instruction#tableswitch}.
     *
     * @see Instruction#tableswitch
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-6.html#jvms-6.5.tableswitch">VM
     * Spec: The Java Virtual Machine Instruction Set</a>
     */
    @SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
    public static class tableswitch {

        public final int defaultbyte;
        public final int lowbyte;
        public final int highbyte;
        public final Map<Integer, Integer> jumpoffsets = new LinkedHashMap<>();

        tableswitch(int defaultByte, int lowByte, int highByte) {
            this.defaultbyte = defaultByte;
            this.lowbyte = lowByte;
            this.highbyte = highByte;
        }

        public String toString(int currentOffset) {
            final StringBuilder sb = new StringBuilder(256);
            sb.append(" (from ").append(this.lowbyte).append(" to ").append(this.highbyte).append(')');
            this.jumpoffsets.keySet().forEach(key -> {
                Integer value = this.jumpoffsets.get(key);
                sb.append(String.format("%n    case %d. jump to %d (relative offset = %d) ", key, value + currentOffset, value));
            });
            sb.append(String.format("%n   default. jump to %d (relative offset = %d) ", this.defaultbyte + currentOffset, this.defaultbyte));

            return sb.toString();
        }

    }

    /**
     * Parsed result for each opcode instruction.
     */
    public static class InstructionParsed {

        /**
         * Current offset of the opCode in the class file <code>Code</code>
         * attribute byte array.
         */
        public final int offset;

        /**
         * Whether current opcode is {@link Instruction#wide} or not.
         *
         * @see Instruction#wide
         */
        protected boolean isWide = false;

        /**
         * Array type for {@link Instruction#newarray}.
         *
         * @see Instruction#newarray
         */
        protected Integer arrayType;

        /**
         * JVM Opcode value.
         *
         * @see Instruction#code
         */
        public final int opCode;

        /**
         * Name for {@link #opCode}.
         *
         * @see Instruction#code
         * @see Instruction#getName()
         */
        public final String opCodeName;

        /**
         * Text of the {@link #opCode}. In case {@link #opCode} is
         * {@link Instruction#wide}, the {@link #opCodeText} contains the
         * following opCode after <code>wide</code> also.
         */
        protected String opCodeText;

        /**
         * Referenced {@link ClassFile#constant_pool} object index if exist. It
         * will be <code>null</code> if the {@link Instruction} did not
         * reference to any {@link ClassFile#constant_pool} object.
         *
         * @see Instruction#ldc
         * @see Instruction#ldc_w
         * @see Instruction#ldc2_w
         * @see Instruction#getstatic
         * @see Instruction#putstatic
         * @see Instruction#getfield
         * @see Instruction#putfield
         * @see Instruction#invokevirtual
         * @see Instruction#invokespecial
         * @see Instruction#invokestatic
         * @see Instruction#invokeinterface
         * @see Instruction#invokedynamic
         * @see Instruction#new_
         * @see Instruction#anewarray
         * @see Instruction#checkcast
         * @see Instruction#instanceof_
         * @see Instruction#multianewarray
         */
        protected Integer cpIndex = null;

        /**
         * Index into the Local Variable array of the current frame.
         *
         * @see Instruction#iload
         * @see Instruction#lload
         */
        protected Integer lvIndex = null;

        /**
         * The immediate numeric value, that value is pushed onto the operand
         * stack.
         *
         * @see Instruction#bipush
         * @see Instruction#iinc
         * @see Instruction#sipush
         *
         */
        protected Integer immediateValue = null;

        /**
         * Number of arguments for {@link Instruction#invokeinterface}
         * instruction.
         *
         * @see Instruction#invokeinterface
         */
        protected Integer nArgs = null;

        /**
         * Execution proceeds at that offset from the address of the opcode of
         * current instruction.
         *
         * @see Instruction#goto_
         * @see Instruction#goto_w
         * @see Instruction#if_acmpeq
         * @see Instruction#if_acmpne
         * @see Instruction#if_icmpeq
         * @see Instruction#if_icmpge
         * @see Instruction#if_icmpgt
         * @see Instruction#if_icmple
         * @see Instruction#if_icmplt
         * @see Instruction#if_icmpne
         * @see Instruction#ifeq
         * @see Instruction#ifne
         * @see Instruction#iflt
         * @see Instruction#ifge
         * @see Instruction#ifgt
         * @see Instruction#ifle
         * @see Instruction#ifnonnull
         * @see Instruction#ifnull
         * @see Instruction#jsr
         * @see Instruction#jsr_w
         */
        protected Integer branchbyte = null;

        /**
         * Parsed data for {@link Instruction#lookupswitch}.
         *
         * @see Instruction#lookupswitch
         */
        protected lookupswitch lookupSwitch = null;

        /**
         * Parsed data for {@link Instruction#tableswitch}.
         *
         * @see Instruction#tableswitch
         */
        protected tableswitch tableSwitch = null;

        InstructionParsed(int curPos, int opcode) {
            this.offset = curPos;
            this.opCode = opcode;
            this.opCodeName = Instruction.valueOf(opcode).getName();
        }

        /**
         * Get the absolute value of the branch byte, if {@link #branchbyte} is
         * not null.
         *
         * @return Absolute value of the {@link #branchbyte}; return
         * <code>null</code> in case {@link #branchbyte} is null
         * @see #branchbyte
         */
        public Integer getAbsoluteBranchByte() {
            return (this.branchbyte != null) ? this.offset + this.branchbyte : null;
        }

        /**
         * Getter for {@link #opCodeText}.
         *
         * @return {@link #opCodeText} value
         */
        public String getOpcodeText() {
            return this.opCodeText;
        }

        /**
         * Getter for {@link #cpIndex}.
         *
         * @return {@link #cpIndex} value. <code>null</code> if no constant pool
         * index.
         */
        public Integer getCpindex() {
            return this.cpIndex;
        }

        /**
         * Getter for {@link #immediateValue}.
         *
         * @return {@link #immediateValue} value. <code>null</code> if not
         * applicable
         */
        public Integer getImmediateValue() {
            return this.immediateValue;
        }

        /**
         * Getter for {@link #lvIndex}.
         *
         * @return {@link #lvIndex} value. <code>null</code> if no local
         * variable index.
         */
        public Integer getLvindex() {
            return this.lvIndex;
        }

        /**
         * Getter for {@link #lookupSwitch}.
         *
         * @return {@link #lookupSwitch} value. <code>null</code> if current
         * instruction is not {@link Instruction#lookupswitch}
         */
        public lookupswitch getLookupSwitch() {
            return this.lookupSwitch;
        }

        /**
         * Getter for {@link #nArgs}.
         *
         * @return {@link #nArgs} value. <code>null</code> if not applicable
         */
        public Integer getNargs() {
            return this.nArgs;
        }

        /**
         * Getter for {@link #tableSwitch}.
         *
         * @return {@link #tableSwitch} value. <code>null</code> if current
         * instruction is not {@link Instruction#tableswitch}
         */
        public tableswitch getTableSwitch() {
            return this.tableSwitch;
        }

        /**
         * Getter for {@link #isWide}.
         *
         * @return {@link #isWide} value.
         */
        public boolean isWide() {
            return this.isWide;
        }

        /**
         * Getter for {@link #arrayType}.
         *
         * @return {@link #arrayType} value.
         */
        public Integer getArrayType() {
            return this.arrayType;
        }

        @Override
        public String toString() {
            String s = String.format("%04d: %s", this.offset, this.opCodeText);

            if (this.branchbyte != null) {
                String branch = String.format(" %d (branch byte offset = %d)", this.getAbsoluteBranchByte(), this.branchbyte);
                s += branch;
            }

            return s;
        }

        /**
         * Get the {@link Instruction} analysis result with
         * {@link ClassFile#constant_pool} description.
         *
         * @param cf the {@link ClassFile}
         * @return {@link Instruction} analysis result
         */
        public String toString(ClassFile cf) {
            String s = this.toString();

            if (this.cpIndex != null) {
                String cpDesc = cf.getCPDescription(this.cpIndex);
                // Avoid too long description
                if (cpDesc.length() > 1000) {
                    cpDesc = cpDesc.substring(1, 1000);
                }
                s = s + "  " + cpDesc;
            }

            return s;
        }
    }
}
