/*
 * Opcode.java    September 14, 2007, 10:27 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.BytesTool;
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
    private static final String FORMAT_OPCODE_NUMBER = "%s %d";
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
                parsed.opCodeText = String.format(FORMAT_OPCODE_NUMBER, this.name(), intermediateByteValue);
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
                parsed.opCodeText = String.format(FORMAT_OPCODE_NUMBER, this.name(), intermediateShortValue);
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
        /**
         * Convert float to int, in current Operand Stack
         * <code>..., value</code>.
         */
        f2i(139),
        /**
         * Convert float to long, in current Operand Stack
         * <code>..., value</code>.
         */
        f2l(140),
        /**
         * Convert float to double, in current Operand Stack
         * <code>..., value</code>.
         */
        f2d(141),
        /**
         * Convert double to int, in current Operand Stack
         * <code>..., value</code>.
         */
        d2i(142),
        /**
         * Convert double to long, in current Operand Stack
         * <code>..., value</code>.
         */
        d2l(143),
        /**
         * Convert double to float, in current Operand Stack
         * <code>..., value</code>.
         */
        d2f(144),
        /**
         * Convert int to byte.
         */
        i2b(145),
        /**
         * Convert int to char.
         */
        i2c(146),
        /**
         * Convert int to short.
         */
        i2s(147),
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
        lcmp(148),
        /**
         * Compare float, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * @see #lcmp
         * @see #fcmpg
         */
        fcmpl(149),
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
        fcmpg(150),
        /**
         * Compare double, in current Operand Stack
         * <code>..., value1, value2</code>.
         *
         * @see #lcmp
         * @see #dcmpg
         */
        dcmpl(151),
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
        jsr(168) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Branchbyte_Short(curPos, pdis);
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
        ret(169) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Lvindex_UnsignedByte(curPos, pdis);
            }
        },
        /**
         * Access jump table by index and jump.
         */
        tableswitch(170) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                // 0-3 byte pad, when needed
                super.skipPad(pdis);

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);

                parsed.tableSwitch = new TableSwitch(pdis.readInt(), pdis.readInt(), pdis.readInt());
                for (int i = parsed.tableSwitch.lowbyte; i <= parsed.tableSwitch.highbyte; i++) {
                    parsed.tableSwitch.jumpoffsets.put(i, pdis.readInt());
                }

                parsed.opCodeText = String.format("%s %s", this.name(), parsed.tableSwitch.toString(curPos));
                return parsed;
            }
        },
        /**
         * Access jump table by key match and jump.
         */
        lookupswitch(171) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                // 0-3 byte pad, when needed
                super.skipPad(pdis);

                InstructionParsed parsed = new InstructionParsed(curPos, this.code);

                parsed.lookupSwitch = new LookupSwitch(pdis.readInt(), pdis.readInt());
                for (int i = 0; i < parsed.lookupSwitch.npairs; i++) {
                    parsed.lookupSwitch.mapoffsets.put(pdis.readInt(), pdis.readInt());
                }

                parsed.opCodeText = String.format("%s %s", this.name(), parsed.lookupSwitch.toString(curPos));
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
        ireturn(172),
        /**
         * Return long from method.
         *
         * The current method must have return type long. The value must be of
         * type long.
         *
         * @see #ireturn
         */
        lreturn(173),
        /**
         * Return float from method.
         *
         * The current method must have return type float. The value must be of
         * type float.
         *
         * @see #ireturn
         */
        freturn(174),
        /**
         * Return double from method.
         *
         * The current method must have return type double. The value must be of
         * type double.
         *
         * @see #ireturn
         */
        dreturn(175),
        /**
         * Return reference from method.
         *
         * The objectref must be of type reference and must refer to an object
         * of a type that is assignment compatible with the type represented by
         * the return descriptor of the current method.
         *
         * @see #ireturn
         * @see org.freeinternals.format.classfile.MethodInfo#getDeclaration()
         */
        areturn(176),
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
        return_(177) {
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
        getstatic(178) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Set static field in class.
         *
         * On successful resolution of the field, the class or interface that
         * declared the resolved field is initialized if that class or interface
         * has not already been initialized.
         */
        putstatic(179) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Fetch field from object.
         */
        getfield(180) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Set field in object.
         */
        putfield(181) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke instance method; dispatch based on class.
         */
        invokevirtual(182) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke instance method; direct invocation of instance initialization
         * methods and methods of the current class and its super types.
         */
        invokespecial(183) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke a class (static) method.
         */
        invokestatic(184) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Invoke interface method.
         */
        invokeinterface(185) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                int nArgs = pdis.readUnsignedByte();
                BytesTool.skipBytes(pdis, 1);

                parsed.opCodeText = String.format("%s interface=%d, nargs=%d", this.name(), parsed.cpIndex, nArgs);
                return parsed;
            }
        },
        /**
         * Invoke a dynamically-computed call site.
         */
        invokedynamic(186) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.cpIndex = pdis.readUnsignedShort();
                
                // Skip 2 zero bytes
                BytesTool.skipBytes(pdis, 2);
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
        new_(187) {
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
        newarray(188) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);

                int arrayType = pdis.readUnsignedByte();
                parsed.opCodeText = String.format("%s %s", Opcode.Instruction.newarray.name(), NewarrayType.getName(arrayType));
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
        anewarray(189) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
            }
        },
        /**
         * Get length of array.
         */
        arraylength(190),
        /**
         * Throw exception or error.
         */
        athrow(191),
        /**
         * Check whether object is of given type.
         */
        checkcast(192) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                return super.parse_Cpindex_UnsignedShort(curPos, pdis);
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
        instanceof_(193) {
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
        monitorenter(194),
        /**
         * Exit monitor for object.
         *
         * The objectref must be of type reference.
         *
         * The thread that executes {@link #monitorexit} must be the owner of
         * the monitor associated with the instance referenced by objectref.
         */
        monitorexit(195),
        /**
         * Extend local variable index by additional bytes.
         *
         * TODO - Refactor this method
         */
        wide(196) {
            @Override
            protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
                InstructionParsed parsed = new InstructionParsed(curPos, this.code);
                parsed.opCodeText = this.getText_wide(pdis);
                return parsed;
            }

            private String getText_wide(final PosDataInputStream pdis) throws IOException {
                final int opcode = pdis.readUnsignedByte();
                String opCodeText;

                int shortValue;
                int shortValue2;

                if (opcode == Opcode.Instruction.iload.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.iload.name()), shortValue);
                } else if (opcode == Opcode.Instruction.lload.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.lload.name()), shortValue);
                } else if (opcode == Opcode.Instruction.fload.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.fload.name()), shortValue);
                } else if (opcode == Opcode.Instruction.dload.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.dload.name()), shortValue);
                } else if (opcode == Opcode.Instruction.aload.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.aload.name()), shortValue);
                } else if (opcode == Opcode.Instruction.istore.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.istore.name()), shortValue);
                } else if (opcode == Opcode.Instruction.lstore.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.lstore.name()), shortValue);
                } else if (opcode == Opcode.Instruction.fstore.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.fstore.name()), shortValue);
                } else if (opcode == Opcode.Instruction.dstore.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.dstore.name()), shortValue);
                } else if (opcode == Opcode.Instruction.astore.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.astore.name()), shortValue);
                } else if (opcode == Opcode.Instruction.iinc.code) {
                    shortValue = pdis.readUnsignedShort();
                    shortValue2 = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, Instruction.getWideName(Opcode.Instruction.iinc.name()), shortValue, shortValue2);
                } else if (opcode == Opcode.Instruction.ret.code) {
                    shortValue = pdis.readUnsignedShort();
                    opCodeText = String.format(FORMAT_OPCODE_NUMBER, Instruction.getWideName(Opcode.Instruction.ret.name()), shortValue);
                } else {
                    opCodeText = String.format("%s [Unknown opcode]", Opcode.Instruction.wide.name());
                }

                return opCodeText;
            }

        },
        /**
         * Create new multidimensional array.
         */
        multianewarray(197) {
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
        ifnull(198) {
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
        ifnonnull(199) {
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
        goto_w(200) {
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
        jsr_w(201) {
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

        protected InstructionParsed parse(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.opCodeText = this.name();
            return parsed;

        }

        private InstructionParsed parse_Branchbyte_Short(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.branchbyte = Integer.valueOf(pdis.readShort());
            parsed.opCodeText = this.name();
            return parsed;
        }

        private InstructionParsed parse_Cpindex_UnsignedShort(final int curPos, final PosDataInputStream pdis) throws IOException {
            InstructionParsed parsed = new InstructionParsed(curPos, this.code);
            parsed.cpIndex = pdis.readUnsignedShort();
            parsed.opCodeText = this.name();
            return parsed;
        }

        private InstructionParsed parse_Lvindex_UnsignedByte(final int curPos, final PosDataInputStream pdis) throws IOException {
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
                BytesTool.skipBytes(pdis, skip);
            }
        }

    }

    /**
     * Data types used by {@link Instruction#newarray}.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5.newarray">
     * VM Spec: Table 6.5.newarray-A. Array type codes
     * </a>
     */
    public static enum NewarrayType {

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
        public static String getName(int value) {
            String n = "[ERROR: Unknown type]";
            for (NewarrayType type : NewarrayType.values()) {
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
                final int curPos = pdis.getPos();
                final int opcode = pdis.read();

                codeResult.add(Instruction.valueOf(opcode).parse(curPos, pdis));
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

    /**
     * Instruction structure used by {@link Instruction#lookupswitch}.
     *
     * @see Instruction#lookupswitch
     */
    public static class LookupSwitch {

        public final int defaultbyte;
        public final int npairs;
        public final LinkedHashMap<Integer, Integer> mapoffsets = new LinkedHashMap<>();

        LookupSwitch(int defaultByte, int nPairs) {
            this.defaultbyte = defaultByte;
            this.npairs = nPairs;
        }

        public String toString(int currentOffset) {
            final StringBuilder sb = new StringBuilder(256);
            sb.append('(').append(this.npairs).append(" Pairs)");
            this.mapoffsets.keySet().forEach((key) -> {
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5.tableswitch">VM
     * Spec: The Java Virtual Machine Instruction Set</a>
     */
    public static class TableSwitch {

        public final int defaultbyte;
        public final int lowbyte;
        public final int highbyte;
        public final LinkedHashMap<Integer, Integer> jumpoffsets = new LinkedHashMap<>();

        TableSwitch(int defaultByte, int lowByte, int highByte) {
            this.defaultbyte = defaultByte;
            this.lowbyte = lowByte;
            this.highbyte = highByte;
        }

        public String toString(int currentOffset) {
            final StringBuilder sb = new StringBuilder(256);
            sb.append(" (from ").append(this.lowbyte).append(" to ").append(this.highbyte).append(')');
            this.jumpoffsets.keySet().forEach((key) -> {
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

        /**
         * Parsed data for {@link Instruction#lookupswitch}.
         *
         * @see Instruction#lookupswitch
         */
        protected LookupSwitch lookupSwitch = null;

        /**
         * Parsed data for {@link Instruction#tableswitch}.
         *
         * @see Instruction#tableswitch
         */
        protected TableSwitch tableSwitch = null;

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

        /**
         * Getter for {@link #lookupSwitch}.
         *
         * @return {@link #lookupSwitch} value. <code>null</code> if current
         * instruction is not {@link Instruction#lookupswitch}
         */
        public LookupSwitch getLookupSwitch() {
            return this.lookupSwitch;
        }

        /**
         * Getter for {@link #tableSwitch}.
         *
         * @return {@link #tableSwitch} value. <code>null</code> if current
         * instruction is not {@link Instruction#tableswitch}
         */
        public TableSwitch getTableSwitch() {
            return this.tableSwitch;
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
