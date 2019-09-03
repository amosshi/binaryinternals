/*
 * Opcode.java    September 14, 2007, 10:27 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Opcode parser to interpret the Java {@code code} byte array into human
 * readable text.
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html">VM
 * Spec: The Java Virtual Machine Instruction Set</a>
 */
public final class Opcode {

    private static final Logger LOG = Logger.getLogger(Opcode.class.getName());

    /**
     * Opcode and non {@link ClassFile#constant_pool} index value. Example:
     * <code>bipush + immediate vlaue</code>,
     * <code>lload + local frame vlaue</code>
     */
    private static final String FORMAT_OPCODE_LOCAL = "%s %d";
    private static final String FORMAT_OPCODE_LOCAL_IINC = "%s index = %d const = %d";

    /**
     * The Java Virtual Machine Instruction Set.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5">
     * VM Spec: Instructions
     * </a>
     */
    public static enum Instruction {

        /**
         * Do nothing.
         */
        nop(0),
        /**
         * Push null. Push the null object reference onto the operand stack.
         */
        aconst_null(1),
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
        iconst_m1(2),
        /**
         * Push int constant 0.
         */
        iconst_0(3),
        /**
         * Push int constant 1.
         */
        iconst_1(4),
        /**
         * Push int constant 2.
         */
        iconst_2(5),
        /**
         * Push int constant 3.
         */
        iconst_3(6),
        /**
         * Push int constant 4.
         */
        iconst_4(7),
        /**
         * Push int constant 5.
         */
        iconst_5(8),
        /**
         * Push long constant 0.
         *
         * Push the long constant <code>l</code> (0 or 1) onto the operand
         * stack.
         */
        lconst_0(9),
        /**
         * Push long constant 1.
         */
        lconst_1(10),
        /**
         * Push float 0.0.
         *
         * Push the float constant <code>f</code> (0.0, 1.0, or 2.0) onto the
         * operand stack.
         */
        fconst_0(11),
        /**
         * Push float 1.0.
         */
        fconst_1(12),
        /**
         * Push float 2.0.
         */
        fconst_2(13),
        /**
         * Push double 0.0.
         *
         * Push the double constant <code>d</code> (0.0 or 1.0) onto the operand
         * stack.
         */
        dconst_0(14),
        /**
         * Push double 1.0.
         */
        dconst_1(15),
        /**
         * Push the immediate byte value.
         *
         * The immediate byte is sign-extended to an int value. That value is
         * pushed onto the operand stack.
         */
        bipush(16) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                int intermediateByteValue = pdis.readUnsignedByte();

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.opCodeText = String.format(FORMAT_OPCODE_LOCAL, this.name(), intermediateByteValue);
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
        sipush(17) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                int intermediateShortValue = pdis.readUnsignedShort();

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.opCodeText = String.format(FORMAT_OPCODE_LOCAL, this.name(), intermediateShortValue);
                return parsed;
            }
        },
        /**
         * Push item from runtime constant pool.
         *
         * The index is an unsigned byte that must be a valid index into the
         * runtime constant pool of the current class.
         */
        ldc(18) {
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
        ldc_w(19) {
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
        ldc2_w(20) {
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
        iload(21) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        lload(22) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        fload(23) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        dload(24) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        aload(25) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        iload_0(26),
        /**
         * Load int from local variable at index 1.
         *
         * @see #iload_0
         */
        iload_1(27),
        /**
         * Load int from local variable at index 2.
         *
         * @see #iload_0
         */
        iload_2(28),
        /**
         * Load int from local variable at index 3.
         *
         * @see #iload_0
         */
        iload_3(29),
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
        lload_0(30),
        /**
         * Load long from local variable at index 1.
         *
         * @see #lload_0
         */
        lload_1(31),
        /**
         * Load long from local variable at index 2.
         *
         * @see #lload_0
         */
        lload_2(32),
        /**
         * Load long from local variable at index 3.
         *
         * @see #lload_0
         */
        lload_3(33),
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
        fload_0(34),
        /**
         * Load float from local variable at index 1.
         *
         * @see #fload_0
         */
        fload_1(35),
        /**
         * Load float from local variable at index 2.
         *
         * @see #fload_0
         */
        fload_2(36),
        /**
         * Load float from local variable at index 3.
         *
         * @see #fload_0
         */
        fload_3(37),
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
        dload_0(38),
        /**
         * Load double from local variable at index 1.
         *
         * @see #dload_0
         */
        dload_1(39),
        /**
         * Load double from local variable at index 2.
         *
         * @see #dload_0
         */
        dload_2(40),
        /**
         * Load double from local variable at index 3.
         *
         * @see #dload_0
         */
        dload_3(41),
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
        aload_0(42),
        /**
         * Load reference from local variable at index 1.
         *
         * @see #aload_0
         */
        aload_1(43),
        /**
         * Load reference from local variable at index 2.
         *
         * @see #aload_0
         */
        aload_2(44),
        /**
         * Load reference from local variable at index 3.
         *
         * @see #aload_0
         */
        aload_3(45),
        /**
         * Load int from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type int. The index must be of type int. Both
         * arrayref and index are popped from the operand stack. The int value
         * in the component of the array at index is retrieved and pushed onto
         * the operand stack.
         */
        iaload(46),
        /**
         * Load long from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type long. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The long
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        laload(47),
        /**
         * Load float from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type float. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The float
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        faload(48),
        /**
         * Load double from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type double. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The double
         * value in the component of the array at index is retrieved and pushed
         * onto the operand stack.
         */
        daload(49),
        /**
         * Load reference from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type reference. The index must be of type
         * int. Both arrayref and index are popped from the operand stack. The
         * reference value in the component of the array at index is retrieved
         * and pushed onto the operand stack.
         */
        aaload(50),
        /**
         * Load byte or boolean from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type reference. The index must be of type
         * int. Both arrayref and index are popped from the operand stack. The
         * reference value in the component of the array at index is retrieved
         * and pushed onto the operand stack.
         */
        baload(51),
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
        caload(52),
        /**
         * Load short from array.
         *
         * The arrayref must be of type reference and must refer to an array
         * whose components are of type short. The index must be of type int.
         * Both arrayref and index are popped from the operand stack. The
         * component of the array at index is retrieved and sign-extended to an
         * int value. That value is pushed onto the operand stack.
         */
        saload(53),
        /**
         * Store int into local variable at <code>index</code>.
         *
         * The <code>index</code> is an unsigned byte that must be an index into
         * the local variable array of the current frame. The value on the top
         * of the operand stack must be of type int. It is popped from the
         * operand stack, and the value of the local variable at index is set to
         * value.
         */
        istore(54) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        lstore(55) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        fstore(56) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        dstore(57) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        astore(58) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
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
        istore_0(59),
        /**
         * Store int into local variable at index 1.
         *
         * @see #istore_0
         */
        istore_1(60),
        /**
         * Store int into local variable at index 2.
         *
         * @see #istore_0
         */
        istore_2(61),
        /**
         * Store int into local variable at index 3.
         *
         * @see #istore_0
         */
        istore_3(62),
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
        lstore_0(63),
        /**
         * Store long into local variable at index 1.
         *
         * @see #lstore_0
         */
        lstore_1(64),
        /**
         * Store long into local variable at index 2.
         *
         * @see #lstore_0
         */
        lstore_2(65),
        /**
         * Store long into local variable at index 3.
         *
         * @see #lstore_0
         */
        lstore_3(66),
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
        fstore_0(67),
        /**
         * Store float into local variable at index 1.
         *
         * @see #fstore_0
         */
        fstore_1(68),
        /**
         * Store float into local variable at index 2.
         *
         * @see #fstore_0
         */
        fstore_2(69),
        /**
         * Store float into local variable at index 3.
         *
         * @see #fstore_0
         */
        fstore_3(70),
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
        dstore_0(71),
        /**
         * Store double into local variable at index 1.
         *
         * @see #dstore_0
         */
        dstore_1(72),
        /**
         * Store double into local variable at index 2.
         *
         * @see #dstore_0
         */
        dstore_2(73),
        /**
         * Store double into local variable at index 3.
         *
         * @see #dstore_0
         */
        dstore_3(74),
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
        astore_0(75),
        /**
         * Store reference into local variable at index 1.
         *
         * @see #astore_0
         */
        astore_1(76),
        /**
         * Store reference into local variable at index 2.
         *
         * @see #astore_0
         */
        astore_2(77),
        /**
         * Store reference into local variable at index 3.
         *
         * @see #astore_0
         */
        astore_3(78),
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
        iastore(79),
        /**
         * Store into long array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        lastore(80),
        /**
         * Store into float array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        fastore(81),
        /**
         * Store into double array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        dastore(82),
        /**
         * Store into reference array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        aastore(83),
        /**
         * Store into byte or boolean array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        bastore(84),
        /**
         * Store into char array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        castore(85),
        /**
         * Store into short array in current Operand Stack
         * <code>..., arrayref, index, value</code>.
         */
        sastore(86),
        /**
         * Pop the top value from the operand stack.
         */
        pop(87),
        /**
         * Pop the top one or two values from the operand stack.
         */
        pop2(88),
        /**
         * Duplicate the top value on the operand stack and push the duplicated
         * value onto the operand stack.
         */
        dup(89),
        /**
         * Duplicate the top value on the operand stack and insert the
         * duplicated value two values down in the operand stack.
         */
        dup_x1(90),
        /**
         * Duplicate the top value on the operand stack and insert the
         * duplicated value two or three values down in the operand stack.
         */
        dup_x2(91),
        /**
         * Duplicate the top one or two values on the operand stack and push the
         * duplicated value or values back onto the operand stack in the
         * original order.
         */
        dup2(92),
        /**
         * Duplicate the top one or two values on the operand stack and insert
         * the duplicated values, in the original order, one value beneath the
         * original value or values in the operand stack.
         */
        dup2_x1(93),
        /**
         * Duplicate the top one or two values on the operand stack and insert
         * the duplicated values, in the original order, into the operand stack.
         */
        dup2_x2(94),
        /**
         * Swap the top two values on the operand stack.
         */
        swap(95),
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
        iadd(96),
        /**
         * Add long in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        ladd(97),
        /**
         * Add float in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        fadd(98),
        /**
         * Add double in current Operand Stack <code>..., value1, value2</code>.
         *
         * @see #iadd
         */
        dadd(99),
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
        isub(100),
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
        lsub(101),
        /**
         * Subtract float.
         *
         * @see #isub
         */
        fsub(102),
        /**
         * Subtract double.
         *
         * @see #isub
         */
        dsub(103),
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
        imul(104),
        /**
         * Multiply long.
         *
         * @see #imul
         */
        lmul(105),
        /**
         * Multiply float.
         *
         * @see #imul
         */
        fmul(106),
        /**
         * Multiply double.
         *
         * @see #imul
         */
        dmul(107),
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
        idiv(108),
        /**
         * Divide long.
         *
         * @see #idiv
         */
        ldiv(109),
        /**
         * Divide float.
         *
         * @see #idiv
         */
        fdiv(110),
        /**
         * Divide double.
         *
         * @see #idiv
         */
        ddiv(111),
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
        irem(112),
        /**
         * Remainder long.
         *
         * @see #irem
         */
        lrem(113),
        /**
         * Remainder float.
         *
         * @see #irem
         */
        frem(114),
        /**
         * Remainder double.
         *
         * @see #irem
         */
        drem(115),
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
        ineg(116),
        /**
         * Negate long.
         *
         * @see #ineg
         */
        lneg(117),
        /**
         * Negate float.
         *
         * @see #ineg
         */
        fneg(118),
        /**
         * Negate double.
         *
         * @see #ineg
         */
        dneg(119),
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
        ishl(120),
        /**
         * Shift left long.
         *
         * @see #ishl
         */
        lshl(121),
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
        ishr(122),
        /**
         * Arithmetic shift right long.
         *
         * @see #ishr
         */
        lshr(123),
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
        iushr(124),
        /**
         * Logical shift right long.
         *
         * @see #iushr
         */
        lushr(125),
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
        iand(126),
        /**
         * Boolean AND long.
         *
         * @see #iand
         */
        land(127),
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
        ior(128),
        /**
         * Boolean OR long.
         *
         * @see #ior
         */
        lor(129),
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
        ixor(130),
        /**
         * Boolean XOR long.
         *
         * @see #ixor
         */
        lxor(131),
        /**
         * Increment local variable by constant.
         *
         * The index is an unsigned byte that must be an index into the local
         * variable array of the current frame. The const is an immediate signed
         * byte. The local variable at index must contain an int. The value
         * const is first sign-extended to an int, and then the local variable
         * at index is incremented by that amount.
         */
        iinc(132) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.lvIndex = pdis.readUnsignedByte();
                int immediateSignedByteValue = pdis.readByte();
                parsed.opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, this.name(), parsed.lvIndex, immediateSignedByteValue);
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
        i2l(133),
        /**
         * Convert int to float.
         *
         * @see #i2l
         */
        i2f(134),
        /**
         * Convert int to double.
         *
         * @see #i2l
         */
        i2d(135),
        /**
         * Convert long to int, in current Operand Stack
         * <code>..., value</code>.
         *
         * The value on the top of the operand stack must be of type long. It is
         * popped from the operand stack and converted to an int result by
         * taking the low-order 32 bits of the long value and discarding the
         * high-order 32 bits. The result is pushed onto the operand stack.
         */
        l2i(136),
        /**
         * Convert long to float, in current Operand Stack
         * <code>..., value</code>.
         *
         * The l2f instruction performs a widening primitive conversion that may
         * lose precision because values of type float have only 24 significand
         * bits.
         */
        l2f(137),
        /**
         * Convert long to double, in current Operand Stack
         * <code>..., value</code>.
         *
         * The value on the top of the operand stack must be of type long. It is
         * popped from the operand stack and converted to a double result using
         * IEEE 754 round to nearest mode. The result is pushed onto the operand
         * stack.
         */
        l2d(138),
        f2i(139),
        f2l(140),
        f2d(141),
        d2i(142),
        d2l(143),
        d2f(144),
        i2b(145),
        i2c(146),
        i2s(147),
        lcmp(148),
        fcmpl(149),
        fcmpg(150),
        dcmpl(151),
        dcmpg(152),
        /**
         * Branch if int comparison with zero succeeds.
         *
         * ifeq succeeds if and only if value = 0.
         */
        ifeq(153) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        ifne(154) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        iflt(155) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        ifge(156) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        ifgt(157) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        ifle(158) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmpeq(159) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmpne(160) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmplt(161) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmpge(162) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmpgt(163) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_icmple(164) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_acmpeq(165) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        if_acmpne(166) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
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
        goto_(167) {
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
        jsr(168){
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
            }
        },
        ret(169),
        tableswitch(170),
        lookupswitch(171),
        ireturn(172),
        lreturn(173),
        freturn(174),
        dreturn(175),
        areturn(176),
        /**
         * Note: Add '_' suffix to avoid compile error since 'goto' is a Java
         * keyword, remove the '_' from the name when showing.
         */
        return_(177),
        getstatic(178),
        putstatic(179),
        getfield(180),
        putfield(181),
        invokevirtual(182),
        invokespecial(183),
        invokestatic(184),
        invokeinterface(185),
        invokedynamic(186),
        /**
         * Note: Add '_' suffix to avoid compile error since 'goto' is a Java
         * keyword, remove the '_' from the name when showing.
         */
        new_(187),
        newarray(188),
        anewarray(189),
        arraylength(190),
        athrow(191),
        checkcast(192),
        /**
         * Note: Add '_' suffix to avoid compile error since 'goto' is a Java
         * keyword, remove the '_' from the name when showing.
         */
        instanceof_(193),
        monitorenter(194),
        monitorexit(195),
        wide(196),
        multianewarray(197),
        ifnull(198),
        ifnonnull(199),
        goto_w(200),
        jsr_w(201),
        // Reserved opcodes
        breakpoint(202, true),
        impdep1(254, true),
        impdep2(255, true);

        public static final String OPCODE_NAME_UNKNOWN = "[Unknown opcode]";
        public static final String OPCODE_NAME_RESERVED_PREFIX = "[Reserved] ";

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
         * Get the name with "wide " prefix. Only applied for {@link #wide}.
         *
         * @return opcode name with "wide " prefix
         */
        static String getWideName(String s) {
            return Instruction.wide.name() + " " + s;
        }

        /**
         * Get Opcode name.
         *
         * @param opcode Internal value of an opcode.
         * @return Opcode name
         */
        public static String getOpcodeName(int opcode) {
            String name = Instruction.OPCODE_NAME_UNKNOWN;
            for (Instruction i : Instruction.values()) {
                if (i.code == opcode) {
                    name = i.getName();
                    break;
                }
            }

            return name;
        }

        protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.opCodeText = this.name();
            return parsed;

        }

        private InstructionParsed parse_Lvindex_UnsignedByte(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.lvIndex = pdis.readUnsignedByte();
            parsed.opCodeText = String.format(FORMAT_OPCODE_LOCAL, this.name(), parsed.lvIndex);
            return parsed;
        }

        private InstructionParsed parse_Branchbyte_Short(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.branchbyte = Integer.valueOf(pdis.readShort());
            parsed.opCodeText = this.name();
            return parsed;
        }

    }

    /**
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5.newarray">
     * VM Spec: Table 6.5.newarray-A. Array type codes
     * </a>
     */
    public static enum InstructionNewarrayType {

        T_BOOLEAN(4),
        T_CHAR(5),
        T_FLOAT(6),
        T_DOUBLE(7),
        T_BYTE(8),
        T_SHORT(9),
        T_INT(10),
        T_LONG(11);

        public final int atype;

        InstructionNewarrayType(int i) {
            this.atype = i;
        }

        /**
         * Get the type name based on {@link #atype}.
         *
         * @param value Value to match {@link #atype}
         * @return Type name corresponding to <code>value</code>
         */
        public static String getName(int value) {
            String n = "[ERROR: Unknown type]";
            for (InstructionNewarrayType type : InstructionNewarrayType.values()) {
                if (type.atype == value) {
                    n = type.name();
                    break;
                }
            }
            return n;
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
                codeResult.add(parseInstruction(pdis));
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "parseCode() with code length - {0}", code.length);
                LOG.log(Level.SEVERE, ioe.toString(), ioe);
                // We keep the System.err here, in case there is no logger settings exist
                System.err.println("parseCode() with code length - " + code.length);
                System.err.println(ioe.toString());
                break;
            }
        }

        return codeResult;
    }

    private static InstructionParsed parseInstruction(final PosDataInputStream pdis) throws IOException {
        final int curPos = pdis.getPos();
        final int opcode = pdis.read();
        InstructionParsed result = new InstructionParsed(curPos, opcode);

        int byteValue;
        int immediateSignedByteValue;

        if (Opcode.Instruction.nop.code == opcode) {
        } else if (Opcode.Instruction.ret.code == opcode) {
            // Return from subroutine
            // The index is an unsigned byte between 0 and 255, inclusive.
            // The local variable at index in the current frame (?.6) must contain a value of type returnAddress.
            // The contents of the local variable are written into the Java virtual machine's pc register, and execution continues there.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ret.name(), byteValue);
        } else if (Opcode.Instruction.tableswitch.code == opcode) {
            // Access jump table by index and jump
            int skip = pdis.getPos() % 4;
            skip = (skip > 0) ? 4 - skip : skip;
            if (skip > 0) {
                pdis.skipBytes(skip);
            }
            result.opCodeText = Opcode.getText_tableswitch(pdis);
        } else if (Opcode.Instruction.lookupswitch.code == opcode) {
            // Access jump table by key match and jump
            int skip = pdis.getPos() % 4;
            skip = (skip > 0) ? 4 - skip : skip;
            if (skip > 0) {
                pdis.skipBytes(skip);
            }
            result.opCodeText = Opcode.getText_lookupswitch(pdis);
            // opCodeText = Opcode.Instruction.lookupswitch.name();
        } else if (Opcode.Instruction.ireturn.code == opcode) {
            result.opCodeText = Opcode.Instruction.ireturn.name();
        } else if (Opcode.Instruction.lreturn.code == opcode) {
            result.opCodeText = Opcode.Instruction.lreturn.name();
        } else if (Opcode.Instruction.freturn.code == opcode) {
            result.opCodeText = Opcode.Instruction.freturn.name();
        } else if (Opcode.Instruction.dreturn.code == opcode) {
            result.opCodeText = Opcode.Instruction.dreturn.name();
        } else if (Opcode.Instruction.areturn.code == opcode) {
            result.opCodeText = Opcode.Instruction.areturn.name();
        } else if (Opcode.Instruction.return_.code == opcode) {
            result.opCodeText = Opcode.Instruction.return_.getName();
        } else if (Opcode.Instruction.getstatic.code == opcode) {
            // Get static field from class
            // --
            // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6),
            // where the value of the index is (indexbyte1 << 8) | indexbyte2.
            // The runtime constant pool item at that index must be a symbolic reference to a field (?.1),
            // which gives the name and descriptor of the field as well as a symbolic reference to the class or interface
            // in which the field is to be found.
            // The referenced field is resolved (?.4.3.2).
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.getstatic.name();
        } else if (Opcode.Instruction.putstatic.code == opcode) {
            // Set static field in class
            // --
            // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6),
            // where the value of the index is (indexbyte1 << 8) | indexbyte2.
            // The runtime constant pool item at that index must be a symbolic reference to a field (?.1),
            // which gives the name and descriptor of the field as well as a symbolic reference to the class or interface
            // in which the field is to be found. The referenced field is resolved (?.4.3.2).
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.putstatic.name();
        } else if (Opcode.Instruction.getfield.code == opcode) {
            // Fetch field from object
            // --
            // The objectref, which must be of type reference, is popped from the operand stack.
            // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6),
            // where the value of the index is (indexbyte1 << 8) | indexbyte2.
            // The runtime constant pool item at that index must be a symbolic reference to a field (?.1),
            // which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found.
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.getfield.name();
        } else if (Opcode.Instruction.putfield.code == opcode) {
            // Set field in object
            // --
            // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6),
            // where the value of the index is (indexbyte1 << 8) | indexbyte2.
            // The runtime constant pool item at that index must be a symbolic reference to a field (?.1),
            // which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found.
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.putfield.name();
        } else if (Opcode.Instruction.invokevirtual.code == opcode) {
            // Invoke instance method; dispatch based on class
            // --
            // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6),
            // where the value of the index is (indexbyte1 << 8) | indexbyte2.
            // The runtime constant pool item at that index must be a symbolic reference to a method (?.1),
            // which gives the name and descriptor (?.3.3) of the method as well as a symbolic reference to the class
            // in which the method is to be found.
            // The named method is resolved (?.4.3.3).
            // The method must not be an instance initialization method (?.9) or the class or interface initialization method (?.9).
            // Finally, if the resolved method is protected (?.6), and it is
            //   either a member of the current class
            //   or a member of a superclass of the current class,
            // then the class of objectref must be either the current class or a subclass of the current class.
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.invokevirtual.name();
        } else if (Opcode.Instruction.invokespecial.code == opcode) {
            // Invoke instance method;
            // special handling for superclass, private, and instance initialization method invocations
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.invokespecial.name();
        } else if (Opcode.Instruction.invokestatic.code == opcode) {
            // Invoke a class (static) method
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.invokestatic.name();
        } else if (Opcode.Instruction.invokeinterface.code == opcode) {
            // Invoke interface method
            result.cpIndex = pdis.readUnsignedShort();
            byteValue = pdis.readUnsignedByte();
            pdis.skipBytes(1);
            result.opCodeText = String.format("%s interface=%d, nargs=%d",
                    Opcode.Instruction.invokeinterface.name(),
                    result.cpIndex,
                    byteValue);
        } else if (Opcode.Instruction.invokedynamic.code == opcode) {
            // Invoke dynamic method
            result.cpIndex = pdis.readUnsignedShort();
            pdis.skipBytes(2);  // Skip 2 zero bytes
            result.opCodeText = Opcode.Instruction.invokedynamic.name();
        } else if (Opcode.Instruction.new_.code == opcode) {
            // Create new object
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.new_.getName();
        } else if (Opcode.Instruction.newarray.code == opcode) {
            // Create new array
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format("%s %s",
                    Opcode.Instruction.newarray.name(),
                    InstructionNewarrayType.getName(byteValue));
        } else if (Opcode.Instruction.anewarray.code == opcode) {
            // Create new array of reference
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.anewarray.name();
        } else if (Opcode.Instruction.arraylength.code == opcode) {
            result.opCodeText = Opcode.Instruction.arraylength.name();
        } else if (Opcode.Instruction.athrow.code == opcode) {
            result.opCodeText = Opcode.Instruction.athrow.name();
        } else if (Opcode.Instruction.checkcast.code == opcode) {
            // Check whether object is of given type
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.checkcast.name();
        } else if (Opcode.Instruction.instanceof_.code == opcode) {
            // Determine if object is of given type
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.instanceof_.getName();
        } else if (Opcode.Instruction.monitorenter.code == opcode) {
            result.opCodeText = Opcode.Instruction.monitorenter.name();
        } else if (Opcode.Instruction.monitorexit.code == opcode) {
            result.opCodeText = Opcode.Instruction.monitorexit.name();
        } else if (Opcode.Instruction.wide.code == opcode) {
            // Extend local variable index by additional bytes
            result.opCodeText = Opcode.getText_wide(pdis);
        } else if (Opcode.Instruction.multianewarray.code == opcode) {
            // Create new multidimensional array
            result.cpIndex = pdis.readUnsignedShort();
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format("%s type=%d dimensions=%d",
                    Opcode.Instruction.multianewarray.name(),
                    result.cpIndex,
                    byteValue);
        } else if (Opcode.Instruction.ifnull.code == opcode) {
            // Branch if reference is null
            result.branchbyte = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.ifnull.name();
        } else if (Opcode.Instruction.ifnonnull.code == opcode) {
            // Branch if reference not null
            result.branchbyte = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.ifnonnull.name();
        } else if (Opcode.Instruction.goto_w.code == opcode) {
            // Branch always (wide index)
            result.branchbyte = pdis.readInt();
            result.opCodeText = Opcode.Instruction.goto_w.name();
        } else if (Opcode.Instruction.jsr_w.code == opcode) {
            // Jump subroutine (wide index)
            // --
            // The unsigned branchbyte1, branchbyte2, branchbyte3, and branchbyte4 are used to
            // construct a signed 32-bit offset, where the offset is
            // (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4.
            result.branchbyte = pdis.readInt();
            result.opCodeText = Opcode.Instruction.jsr_w.name();
        } else if (Opcode.Instruction.breakpoint.code == opcode) {
            // Reserved opcodes
            result.opCodeText = Opcode.Instruction.breakpoint.name();
        } else if (Opcode.Instruction.impdep1.code == opcode) {
            result.opCodeText = Opcode.Instruction.impdep1.name();
        } else if (Opcode.Instruction.impdep2.code == opcode) {
            result.opCodeText = Opcode.Instruction.impdep2.name();
        } else {
            result.opCodeText = Instruction.OPCODE_NAME_UNKNOWN;
        }

        return result;
    }

    private static String getText_lookupswitch(final PosDataInputStream pdis)
            throws IOException {
        String space = "    ";
        final int defaultJump = pdis.readInt();

        final StringBuilder sb = new StringBuilder(200);
        sb.append(Opcode.Instruction.lookupswitch.name());
        sb.append(String.format(": default=%d", defaultJump));

        final int pairCount = pdis.readInt();
        int caseValue = 0;
        int offsetValue = 0;
        for (int i = 0; i < pairCount; i++) {
            caseValue = pdis.readInt();
            offsetValue = pdis.readInt();

            sb.append(String.format("\n%scase %d: %d", space, caseValue, offsetValue));
        }

        return sb.toString();
    }

    /**
     * <code>tableswitch</code> instruction.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5.tableswitch">VM
     * Spec: The Java Virtual Machine Instruction Set</a>
     */
    private static String getText_tableswitch(final PosDataInputStream pdis)
            throws IOException {
        String space = "    ";
        final int defaultJump = pdis.readInt();
        final int valueLow = pdis.readInt();
        final int valueHigh = pdis.readInt();
        final int tableLength = valueHigh - valueLow + 1;
        int offsetValue;

        final StringBuilder sb = new StringBuilder(200);
        sb.append(Opcode.Instruction.tableswitch.name());
        sb.append(String.format(" %d to %d: default=%d", valueLow, valueHigh, defaultJump));
        for (int i = 0; i < tableLength; i++) {
            offsetValue = pdis.readInt();
            sb.append(String.format("\n%s%d", space, offsetValue));
        }

        return sb.toString();
    }

    private static String getText_wide(final PosDataInputStream pdis)
            throws IOException {
        final int opcode = pdis.readUnsignedByte();
        String opCodeText;

        int shortValue;
        int shortValue2;

        if (opcode == Opcode.Instruction.iload.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.iload.name()), shortValue);
        } else if (opcode == Opcode.Instruction.lload.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.lload.name()), shortValue);
        } else if (opcode == Opcode.Instruction.fload.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.fload.name()), shortValue);
        } else if (opcode == Opcode.Instruction.dload.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.dload.name()), shortValue);
        } else if (opcode == Opcode.Instruction.aload.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.aload.name()), shortValue);
        } else if (opcode == Opcode.Instruction.istore.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.istore.name()), shortValue);
        } else if (opcode == Opcode.Instruction.lstore.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.lstore.name()), shortValue);
        } else if (opcode == Opcode.Instruction.fstore.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.fstore.name()), shortValue);
        } else if (opcode == Opcode.Instruction.dstore.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.dstore.name()), shortValue);
        } else if (opcode == Opcode.Instruction.astore.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.astore.name()), shortValue);
        } else if (opcode == Opcode.Instruction.iinc.code) {
            shortValue = pdis.readUnsignedShort();
            shortValue2 = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, Instruction.getWideName(Opcode.Instruction.iinc.name()), shortValue, shortValue2);
        } else if (opcode == Opcode.Instruction.ret.code) {
            shortValue = pdis.readUnsignedShort();
            opCodeText = String.format(FORMAT_OPCODE_LOCAL, Instruction.getWideName(Opcode.Instruction.ret.name()), shortValue);
        } else {
            opCodeText = String.format("%s [Unknown opcode]", Opcode.Instruction.wide.name());
        }

        return opCodeText;
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
         * JVM Opcode value.
         *
         * @see Instruction#code
         */
        public final int opCode;

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

        InstructionParsed(int curPos, int opcode) {
            this.offset = curPos;
            this.opCode = opcode;
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

        @Override
        public String toString() {
            String s = String.format("offset %04d: opcode [%02X] %s", this.offset, this.opCode, this.opCodeText);

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
                s = s + " - " + cpDesc;
            }

            return s;
        }
    }
}
