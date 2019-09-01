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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html">
 * VM Spec: The Java Virtual Machine Instruction Set
 * </a>
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
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-6.html#jvms-6.5">
     * VM Spec: Instructions
     * </a>
     */
    public static enum Instruction {

        nop(0),
        aconst_null(1),
        iconst_m1(2),
        iconst_0(3),
        iconst_1(4),
        iconst_2(5),
        iconst_3(6),
        iconst_4(7),
        iconst_5(8),
        lconst_0(9),
        lconst_1(10),
        fconst_0(11),
        fconst_1(12),
        fconst_2(13),
        dconst_0(14),
        dconst_1(15),
        bipush(16),
        sipush(17),
        ldc(18),
        ldc_w(19),
        ldc2_w(20),
        iload(21),
        lload(22),
        fload(23),
        dload(24),
        aload(25),
        iload_0(26),
        iload_1(27),
        iload_2(28),
        iload_3(29),
        lload_0(30),
        lload_1(31),
        lload_2(32),
        lload_3(33),
        fload_0(34),
        fload_1(35),
        fload_2(36),
        fload_3(37),
        dload_0(38),
        dload_1(39),
        dload_2(40),
        dload_3(41),
        aload_0(42),
        aload_1(43),
        aload_2(44),
        aload_3(45),
        iaload(46),
        laload(47),
        faload(48),
        daload(49),
        aaload(50),
        baload(51),
        caload(52),
        saload(53),
        istore(54),
        lstore(55),
        fstore(56),
        dstore(57),
        astore(58),
        istore_0(59),
        istore_1(60),
        istore_2(61),
        istore_3(62),
        lstore_0(63),
        lstore_1(64),
        lstore_2(65),
        lstore_3(66),
        fstore_0(67),
        fstore_1(68),
        fstore_2(69),
        fstore_3(70),
        dstore_0(71),
        dstore_1(72),
        dstore_2(73),
        dstore_3(74),
        astore_0(75),
        astore_1(76),
        astore_2(77),
        astore_3(78),
        iastore(79),
        lastore(80),
        fastore(81),
        dastore(82),
        aastore(83),
        bastore(84),
        castore(85),
        sastore(86),
        pop(87),
        pop2(88),
        dup(89),
        dup_x1(90),
        dup_x2(91),
        dup2(92),
        dup2_x1(93),
        dup2_x2(94),
        swap(95),
        iadd(96),
        ladd(97),
        fadd(98),
        dadd(99),
        isub(100),
        lsub(101),
        fsub(102),
        dsub(103),
        imul(104),
        lmul(105),
        fmul(106),
        dmul(107),
        idiv(108),
        ldiv(109),
        fdiv(110),
        ddiv(111),
        irem(112),
        lrem(113),
        frem(114),
        drem(115),
        ineg(116),
        lneg(117),
        fneg(118),
        dneg(119),
        ishl(120),
        lshl(121),
        ishr(122),
        lshr(123),
        iushr(124),
        lushr(125),
        iand(126),
        land(127),
        ior(128),
        lor(129),
        ixor(130),
        lxor(131),
        iinc(132),
        i2l(133),
        i2f(134),
        i2d(135),
        l2i(136),
        l2f(137),
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
        ifeq(153),
        ifne(154),
        iflt(155),
        ifge(156),
        ifgt(157),
        ifle(158),
        if_icmpeq(159),
        if_icmpne(160),
        if_icmplt(161),
        if_icmpge(162),
        if_icmpgt(163),
        if_icmple(164),
        if_acmpeq(165),
        if_acmpne(166),
        /** Note: Add '_' suffix to avoid compile error since 'goto' is a Java keyword, remove the '_' from the name when showing. */
        goto_(167),     
        jsr(168),
        ret(169),
        tableswitch(170),
        lookupswitch(171),
        ireturn(172),
        lreturn(173),
        freturn(174),
        dreturn(175),
        areturn(176),
        /** Note: Add '_' suffix to avoid compile error since 'goto' is a Java keyword, remove the '_' from the name when showing. */
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
        /** Note: Add '_' suffix to avoid compile error since 'goto' is a Java keyword, remove the '_' from the name when showing. */
        new_(187),
        newarray(188),
        anewarray(189),
        arraylength(190),
        athrow(191),
        checkcast(192),
        /** Note: Add '_' suffix to avoid compile error since 'goto' is a Java keyword, remove the '_' from the name when showing. */
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
        int intermediateShortValue;

        if (Opcode.Instruction.nop.code == opcode) {
            // Do nothing
            result.opCodeText = Opcode.Instruction.nop.name();
        } else if (Opcode.Instruction.aconst_null.code == opcode) {
            // Push null
            // Push the null object reference onto the operand stack.
            result.opCodeText = Opcode.Instruction.aconst_null.name();
        } else if (Opcode.Instruction.iconst_m1.code == opcode) {
            // An iconst_m1 instruction is type safe if one can validly push the type int onto the incoming operand stack yielding the outgoing type state. 
            // Push int constant -1
            result.opCodeText = Opcode.Instruction.iconst_m1.name();
        } else if (Opcode.Instruction.iconst_0.code == opcode) {
            // Push int constant
            // Push the int constant <i> (-1, 0, 1, 2, 3, 4 or 5) onto the operand stack.
            result.opCodeText = Opcode.Instruction.iconst_0.name();
        } else if (Opcode.Instruction.iconst_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.iconst_1.name();
        } else if (Opcode.Instruction.iconst_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.iconst_2.name();
        } else if (Opcode.Instruction.iconst_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.iconst_3.name();
        } else if (Opcode.Instruction.iconst_4.code == opcode) {
            result.opCodeText = Opcode.Instruction.iconst_4.name();
        } else if (Opcode.Instruction.iconst_5.code == opcode) {
            result.opCodeText = Opcode.Instruction.iconst_5.name();
        } else if (Opcode.Instruction.lconst_0.code == opcode) {
            // Push long constant
            // Push the long constant <l> (0 or 1) onto the operand stack.
            result.opCodeText = Opcode.Instruction.lconst_0.name();
        } else if (Opcode.Instruction.lconst_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.lconst_1.name();
        } else if (Opcode.Instruction.fconst_0.code == opcode) {
            // Push float
            // Push the float constant <f> (0.0, 1.0, or 2.0) onto the operand stack.
            result.opCodeText = Opcode.Instruction.fconst_0.name();
        } else if (Opcode.Instruction.fconst_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.fconst_1.name();
        } else if (Opcode.Instruction.fconst_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.fconst_2.name();
        } else if (Opcode.Instruction.dconst_0.code == opcode) {
            // Push double
            // Push the double constant <d> (0.0 or 1.0) onto the operand stack.
            result.opCodeText = Opcode.Instruction.dconst_0.name();
        } else if (Opcode.Instruction.bipush.code == opcode) {
            // Push the immediate byte value
            // --
            // The immediate byte is sign-extended to an int value.
            // That value is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.bipush.name(), byteValue);
        } else if (Opcode.Instruction.sipush.code == opcode) {
            // Push the immediate short value
            // --
            // The immediate unsigned byte1 and byte2 values are assembled into an intermediate short
            // where the value of the short is (byte1 << 8) | byte2.
            // The intermediate value is then sign-extended to an int value.
            // That value is pushed onto the operand stack.
            intermediateShortValue = pdis.readUnsignedShort();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.sipush.name(), intermediateShortValue);
        } else if (Opcode.Instruction.ldc.code == opcode) {
            // Push item from runtime constant pool
            // --
            // The index is an unsigned byte that must be a valid index into the runtime constant pool of the current class.
            // The runtime constant pool entry at index
            //   either must be a runtime constant of type int or float,
            //   or must be a symbolic reference to a string literal (?.1).
            result.cpIndex = pdis.readUnsignedByte();
            result.opCodeText = Opcode.Instruction.ldc.name();
        } else if (Opcode.Instruction.ldc_w.code == opcode) {
            // Push item from runtime constant pool (wide index)
            // --
            // The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index
            // into the runtime constant pool of the current class (?.6),
            // where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2.
            // The index must be a valid index into the runtime constant pool of the current class.
            // The runtime constant pool entry at the index
            //   either must be a runtime constant of type int or float,
            //   or must be a symbolic reference to a string literal (?.1).

            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.ldc_w.name();

        } else if (Opcode.Instruction.ldc2_w.code == opcode) {
            // Push long or double from runtime constant pool (wide index)
            // --
            // The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index
            // into the runtime constant pool of the current class (?.6),
            // where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2.
            // The index must be a valid index into the runtime constant pool of the current class.
            // The runtime constant pool entry at the index must be a runtime constant of type long or double (?.1).
            // The numeric value of that runtime constant is pushed onto the operand stack as a long or double, respectively.
            result.cpIndex = pdis.readUnsignedShort();
            result.opCodeText = Opcode.Instruction.ldc2_w.name();
        } else if (Opcode.Instruction.iload.code == opcode) {
            // Load int from local variable
            // --
            // The index is an unsigned byte that must be an index into the local variable array of the current frame.
            // The local variable at index must contain an int.
            // The value of the local variable at index is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.iload.name(), byteValue);
        } else if (Opcode.Instruction.lload.code == opcode) {
            // Load long from local variable
            // --
            // The index is an unsigned byte.
            // Both index and index + 1 must be indices into the local variable array of the current frame (2.6).
            // The local variable at index must contain a long.
            // The value of the local variable at index is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.lload.name(), byteValue);
        } else if (Opcode.Instruction.fload.code == opcode) {
            // Load float from local variable
            // -
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (2.6).
            // The local variable at index must contain a float.
            // The value of the local variable at index is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.fload.name(), byteValue);
        } else if (Opcode.Instruction.dload.code == opcode) {
            // Load double from local variable
            // -
            // The index is an unsigned byte.
            // Both index and index + 1 must be indices into the local variable array of the current frame (2.6).
            // The local variable at index must contain a double.
            // The value of the local variable at index is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.dload.name(), byteValue);
        } else if (Opcode.Instruction.aload.code == opcode) {
            // Load reference from local variable
            // --
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (2.6).
            // The local variable at index must contain a reference.
            // The objectref in the local variable at index is pushed onto the operand stack.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.aload.name(), byteValue);
        } else if (Opcode.Instruction.iload_0.code == opcode) {
            // Load int from local variable
            // The <n> must be an index into the local variable array of the current frame (2.6).
            // The local variable at <n> must contain an int.
            // The value of the local variable at <n> is pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.iload_0.name();
        } else if (Opcode.Instruction.iload_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.iload_1.name();
        } else if (Opcode.Instruction.iload_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.iload_2.name();
        } else if (Opcode.Instruction.iload_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.iload_3.name();
        } else if (Opcode.Instruction.lload_0.code == opcode) {
            // Load long from local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (2.6).
            // The local variable at <n> must contain a long.
            // The value of the local variable at <n> is pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.lload_0.name();
        } else if (Opcode.Instruction.lload_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.lload_1.name();
        } else if (Opcode.Instruction.lload_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.lload_2.name();
        } else if (Opcode.Instruction.lload_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.lload_3.name();
        } else if (Opcode.Instruction.fload_0.code == opcode) {
            // Load float from local variable
            // The <n> must be an index into the local variable array of the current frame (2.6).
            // The local variable at <n> must contain a float.
            // The value of the local variable at <n> is pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.fload_0.name();
        } else if (Opcode.Instruction.fload_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.fload_1.name();
        } else if (Opcode.Instruction.fload_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.fload_2.name();
        } else if (Opcode.Instruction.fload_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.fload_3.name();
        } else if (Opcode.Instruction.dload_0.code == opcode) {
            // Load double from local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (2.6).
            // The local variable at <n> must contain a double.
            // The value of the local variable at <n> is pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.dload_0.name();
        } else if (Opcode.Instruction.dload_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.dload_1.name();
        } else if (Opcode.Instruction.dload_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.dload_2.name();
        } else if (Opcode.Instruction.dload_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.dload_3.name();
        } else if (Opcode.Instruction.aload_0.code == opcode) {
            // Load reference from local variable
            // The <n> must be an index into the local variable array of the current frame (2.6).
            // The local variable at <n> must contain a reference.
            // The objectref in the local variable at index is pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.aload_0.name();
        } else if (Opcode.Instruction.aload_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.aload_1.name();
        } else if (Opcode.Instruction.aload_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.aload_2.name();
        } else if (Opcode.Instruction.aload_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.aload_3.name();
        } else if (Opcode.Instruction.iaload.code == opcode) {
            // Load int from array
            // The arrayref must be of type reference and must refer to an array
            // whose components are of type int.
            // The index must be of type int.
            // Both arrayref and index are popped from the operand stack.
            // The int value in the component of the array at index is retrieved and pushed onto the operand stack.
            result.opCodeText = Opcode.Instruction.iaload.name();
        } else if (Opcode.Instruction.laload.code == opcode) {
            // Load long from array
            result.opCodeText = Opcode.Instruction.laload.name();
        } else if (Opcode.Instruction.faload.code == opcode) {
            // Load float from array
            result.opCodeText = Opcode.Instruction.faload.name();
        } else if (Opcode.Instruction.daload.code == opcode) {
            // Load double from array
            result.opCodeText = Opcode.Instruction.daload.name();
        } else if (Opcode.Instruction.aaload.code == opcode) {
            // Load reference from array
            result.opCodeText = Opcode.Instruction.aaload.name();
        } else if (Opcode.Instruction.baload.code == opcode) {
            // Load byte or boolean from array
            result.opCodeText = Opcode.Instruction.baload.name();
        } else if (Opcode.Instruction.caload.code == opcode) {
            // Load char from array
            result.opCodeText = Opcode.Instruction.caload.name();
        } else if (Opcode.Instruction.saload.code == opcode) {
            // Load short from array
            result.opCodeText = Opcode.Instruction.saload.name();
        } else if (Opcode.Instruction.istore.code == opcode) {
            // Store int into local variable
            // --
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (2.6).
            // The value on the top of the operand stack must be of type int.
            // It is popped from the operand stack, and the value of the local variable at index is set to value.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.istore.name(), byteValue);
        } else if (Opcode.Instruction.lstore.code == opcode) {
            // Store long into local variable
            // --
            // The index is an unsigned byte.
            // Both index and index + 1 must be indices into the local variable array of the current frame (2.6).
            // The value on the top of the operand stack must be of type long.
            // It is popped from the operand stack, and the local variables at index and index + 1 are set to value.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.lstore.name(), byteValue);
        } else if (Opcode.Instruction.fstore.code == opcode) {
            // Store float into local variable
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (2.6).
            // The value on the top of the operand stack must be of type float.
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'.
            // The value of the local variable at index is set to value'.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.fstore.name(), byteValue);
        } else if (Opcode.Instruction.dstore.code == opcode) {
            // Store double into local variable
            // The index is an unsigned byte. Both index and index + 1 must be indices into the local variable array of the current frame (2.6).
            // The value on the top of the operand stack must be of type double.
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'.
            // The local variables at index and index + 1 are set to value'.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.dstore.name(), byteValue);
        } else if (Opcode.Instruction.astore.code == opcode) {
            // Store reference into local variable
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (2.6).
            // The objectref on the top of the operand stack must be of type returnAddress or of type reference.
            // It is popped from the operand stack, and the value of the local variable at index is set to objectref.
            byteValue = pdis.readUnsignedByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.astore.name(), byteValue);
        } else if (Opcode.Instruction.istore_0.code == opcode) {
            // Store int into local variable
            // The <n> must be an index into the local variable array of the current frame (?.6).
            // The value on the top of the operand stack must be of type int.
            // It is popped from the operand stack, and the value of the local variable at <n> is set to value.
            result.opCodeText = Opcode.Instruction.istore_0.name();
        } else if (Opcode.Instruction.istore_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.istore_1.name();
        } else if (Opcode.Instruction.istore_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.istore_2.name();
        } else if (Opcode.Instruction.istore_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.istore_3.name();
        } else if (Opcode.Instruction.lstore_0.code == opcode) {
            // Store long into local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6).
            // The value on the top of the operand stack must be of type long.
            // It is popped from the operand stack, and the local variables at <n> and <n> + 1 are set to value.
            result.opCodeText = Opcode.Instruction.lstore_0.name();
        } else if (Opcode.Instruction.lstore_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.lstore_1.name();
        } else if (Opcode.Instruction.lstore_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.lstore_2.name();
        } else if (Opcode.Instruction.lstore_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.lstore_3.name();
        } else if (Opcode.Instruction.fstore_0.code == opcode) {
            // Store float into local variable
            // The <n> must be an index into the local variable array of the current frame (?.6).
            // The value on the top of the operand stack must be of type float.
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'.
            // The value of the local variable at <n> is set to value'.
            result.opCodeText = Opcode.Instruction.fstore_0.name();
        } else if (Opcode.Instruction.fstore_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.fstore_1.name();
        } else if (Opcode.Instruction.fstore_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.fstore_2.name();
        } else if (Opcode.Instruction.fstore_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.fstore_3.name();
        } else if (Opcode.Instruction.dstore_0.code == opcode) {
            // Store double into local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6).
            // The value on the top of the operand stack must be of type double.
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'.
            // The local variables at <n> and <n> + 1 are set to value'.
            result.opCodeText = Opcode.Instruction.dstore_0.name();
        } else if (Opcode.Instruction.dstore_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.dstore_1.name();
        } else if (Opcode.Instruction.dstore_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.dstore_2.name();
        } else if (Opcode.Instruction.dstore_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.dstore_3.name();
        } else if (Opcode.Instruction.astore_0.code == opcode) {
            // Store reference into local variable
            // The <n> must be an index into the local variable array of the current frame (?.6).
            // The objectref on the top of the operand stack must be of type returnAddress or of type reference.
            // It is popped from the operand stack, and the value of the local variable at <n> is set to objectref.
            result.opCodeText = Opcode.Instruction.astore_0.name();
        } else if (Opcode.Instruction.astore_1.code == opcode) {
            result.opCodeText = Opcode.Instruction.astore_1.name();
        } else if (Opcode.Instruction.astore_2.code == opcode) {
            result.opCodeText = Opcode.Instruction.astore_2.name();
        } else if (Opcode.Instruction.astore_3.code == opcode) {
            result.opCodeText = Opcode.Instruction.astore_3.name();
        } else if (Opcode.Instruction.iastore.code == opcode) {
            // Store into int array
            // ..., arrayref, index, value
            // The arrayref must be of type reference and must refer to an array whose components are of type int.
            // Both index and value must be of type int.
            // The arrayref, index, and value are popped from the operand stack.
            // The int value is stored as the component of the array indexed by index.
            result.opCodeText = Opcode.Instruction.iastore.name();
        } else if (Opcode.Instruction.lastore.code == opcode) {
            // Store into long array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.lastore.name();
        } else if (Opcode.Instruction.fastore.code == opcode) {
            // Store into float array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.fastore.name();
        } else if (Opcode.Instruction.dastore.code == opcode) {
            // Store into double array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.dastore.name();
        } else if (Opcode.Instruction.aastore.code == opcode) {
            // Store into reference array
            // ..., arrayref, index, value
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.aastore.name();
        } else if (Opcode.Instruction.bastore.code == opcode) {
            // Store into byte or boolean array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.bastore.name();
        } else if (Opcode.Instruction.castore.code == opcode) {
            // Store into char array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.castore.name();
        } else if (Opcode.Instruction.sastore.code == opcode) {
            // Store into short array
            // ..., arrayref, index, value
            result.opCodeText = Opcode.Instruction.sastore.name();
        } else if (Opcode.Instruction.pop.code == opcode) {
            // Pop the top operand stack value
            // The pop instruction must not be used unless value is a value of a category 1 computational type (?.11.1).
            result.opCodeText = Opcode.Instruction.pop.name();
        } else if (Opcode.Instruction.pop2.code == opcode) {
            // Pop the top one or two operand stack values
            // Form 1: ..., value2, value1
            //   where each of value1 and value2 is a value of a category 1 computational type (?.11.1).
            // Form 2: ..., value
            //   where value is a value of a category 2 computational type (?.11.1).
            result.opCodeText = Opcode.Instruction.pop2.name();
        } else if (Opcode.Instruction.dup.code == opcode) {
            // Duplicate the top operand stack value
            // ..., value --> ..., value, value
            result.opCodeText = Opcode.Instruction.dup.name();
        } else if (Opcode.Instruction.dup_x1.code == opcode) {
            // Duplicate the top operand stack value and insert two values down
            // ..., value2, value1 --> ..., value1, value2, value1
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.dup_x1.name();
        } else if (Opcode.Instruction.dup_x2.code == opcode) {
            // Duplicate the top operand stack value and insert two or three values down
            // Form 1: ..., value3, value2, value1 -->  ..., value1, value3, value2, value1
            // Form 2: ..., value2, value1 --> ..., value1, value2, value1
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.dup_x2.name();
        } else if (Opcode.Instruction.dup2.code == opcode) {
            // Duplicate the top one or two operand stack values
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.dup2.name();
        } else if (Opcode.Instruction.dup2_x1.code == opcode) {
            // Duplicate the top one or two operand stack values and insert two or three values down
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.dup2_x1.name();
        } else if (Opcode.Instruction.dup2_x2.code == opcode) {
            // Duplicate the top one or two operand stack values and insert two, three, or four values down
            // ** Very complex, see the <VM Spec> for detail
            result.opCodeText = Opcode.Instruction.dup2_x2.name();
        } else if (Opcode.Instruction.swap.code == opcode) {
            // Swap the top two operand stack values
            // ..., value2, value1 --> ..., value1, value2
            // The swap instruction must not be used unless value1 and value2 are both values of a category 1 computational type (?.11.1).
            result.opCodeText = Opcode.Instruction.swap.name();
        } else if (Opcode.Instruction.iadd.code == opcode) {
            result.opCodeText = Opcode.Instruction.iadd.name();
        } else if (Opcode.Instruction.ladd.code == opcode) {
            result.opCodeText = Opcode.Instruction.ladd.name();
        } else if (Opcode.Instruction.fadd.code == opcode) {
            result.opCodeText = Opcode.Instruction.fadd.name();
        } else if (Opcode.Instruction.dadd.code == opcode) {
            result.opCodeText = Opcode.Instruction.dadd.name();
        } else if (Opcode.Instruction.isub.code == opcode) {
            result.opCodeText = Opcode.Instruction.isub.name();
        } else if (Opcode.Instruction.lsub.code == opcode) {
            result.opCodeText = Opcode.Instruction.lsub.name();
        } else if (Opcode.Instruction.fsub.code == opcode) {
            result.opCodeText = Opcode.Instruction.fsub.name();
        } else if (Opcode.Instruction.dsub.code == opcode) {
            result.opCodeText = Opcode.Instruction.dsub.name();
        } else if (Opcode.Instruction.imul.code == opcode) {
            result.opCodeText = Opcode.Instruction.imul.name();
        } else if (Opcode.Instruction.lmul.code == opcode) {
            result.opCodeText = Opcode.Instruction.lmul.name();
        } else if (Opcode.Instruction.fmul.code == opcode) {
            result.opCodeText = Opcode.Instruction.fmul.name();
        } else if (Opcode.Instruction.dmul.code == opcode) {
            result.opCodeText = Opcode.Instruction.dmul.name();
        } else if (Opcode.Instruction.idiv.code == opcode) {
            result.opCodeText = Opcode.Instruction.idiv.name();
        } else if (Opcode.Instruction.ldiv.code == opcode) {
            result.opCodeText = Opcode.Instruction.ldiv.name();
        } else if (Opcode.Instruction.fdiv.code == opcode) {
            result.opCodeText = Opcode.Instruction.fdiv.name();
        } else if (Opcode.Instruction.ddiv.code == opcode) {
            result.opCodeText = Opcode.Instruction.ddiv.name();
        } else if (Opcode.Instruction.irem.code == opcode) {
            result.opCodeText = Opcode.Instruction.irem.name();
        } else if (Opcode.Instruction.lrem.code == opcode) {
            result.opCodeText = Opcode.Instruction.lrem.name();
        } else if (Opcode.Instruction.frem.code == opcode) {
            result.opCodeText = Opcode.Instruction.frem.name();
        } else if (Opcode.Instruction.drem.code == opcode) {
            result.opCodeText = Opcode.Instruction.drem.name();
        } else if (Opcode.Instruction.ineg.code == opcode) {
            result.opCodeText = Opcode.Instruction.ineg.name();
        } else if (Opcode.Instruction.lneg.code == opcode) {
            result.opCodeText = Opcode.Instruction.lneg.name();
        } else if (Opcode.Instruction.fneg.code == opcode) {
            result.opCodeText = Opcode.Instruction.fneg.name();
        } else if (Opcode.Instruction.dneg.code == opcode) {
            result.opCodeText = Opcode.Instruction.dneg.name();
        } else if (Opcode.Instruction.ishl.code == opcode) {
            result.opCodeText = Opcode.Instruction.ishl.name();
        } else if (Opcode.Instruction.lshl.code == opcode) {
            result.opCodeText = Opcode.Instruction.lshl.name();
        } else if (Opcode.Instruction.ishr.code == opcode) {
            result.opCodeText = Opcode.Instruction.ishr.name();
        } else if (Opcode.Instruction.lshr.code == opcode) {
            result.opCodeText = Opcode.Instruction.lshr.name();
        } else if (Opcode.Instruction.iushr.code == opcode) {
            result.opCodeText = Opcode.Instruction.iushr.name();
        } else if (Opcode.Instruction.lushr.code == opcode) {
            result.opCodeText = Opcode.Instruction.lushr.name();
        } else if (Opcode.Instruction.iand.code == opcode) {
            result.opCodeText = Opcode.Instruction.iand.name();
        } else if (Opcode.Instruction.land.code == opcode) {
            result.opCodeText = Opcode.Instruction.land.name();
        } else if (Opcode.Instruction.ior.code == opcode) {
            result.opCodeText = Opcode.Instruction.ior.name();
        } else if (Opcode.Instruction.lor.code == opcode) {
            result.opCodeText = Opcode.Instruction.lor.name();
        } else if (Opcode.Instruction.ixor.code == opcode) {
            result.opCodeText = Opcode.Instruction.ixor.name();
        } else if (Opcode.Instruction.lxor.code == opcode) {
            result.opCodeText = Opcode.Instruction.lxor.name();
        } else if (Opcode.Instruction.iinc.code == opcode) {
            // Increment local variable by constant
            // --
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6).
            // The local variable at index must contain an int.
            byteValue = pdis.readUnsignedByte();
            // The const is an immediate signed byte.
            // The value const is first sign-extended to an int, and then the local variable at index is incremented by that amount.
            immediateSignedByteValue = pdis.readByte();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL_IINC, Opcode.Instruction.iinc.name(), byteValue, immediateSignedByteValue);
        } else if (Opcode.Instruction.i2l.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2l.name();
        } else if (Opcode.Instruction.i2f.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2f.name();
        } else if (Opcode.Instruction.i2d.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2d.name();
        } else if (Opcode.Instruction.l2i.code == opcode) {
            result.opCodeText = Opcode.Instruction.l2i.name();
        } else if (Opcode.Instruction.l2f.code == opcode) {
            result.opCodeText = Opcode.Instruction.l2f.name();
        } else if (Opcode.Instruction.l2d.code == opcode) {
            result.opCodeText = Opcode.Instruction.l2d.name();
        } else if (Opcode.Instruction.f2i.code == opcode) {
            result.opCodeText = Opcode.Instruction.f2i.name();
        } else if (Opcode.Instruction.f2l.code == opcode) {
            result.opCodeText = Opcode.Instruction.f2l.name();
        } else if (Opcode.Instruction.f2d.code == opcode) {
            result.opCodeText = Opcode.Instruction.f2d.name();
        } else if (Opcode.Instruction.d2i.code == opcode) {
            result.opCodeText = Opcode.Instruction.d2i.name();
        } else if (Opcode.Instruction.d2l.code == opcode) {
            result.opCodeText = Opcode.Instruction.d2l.name();
        } else if (Opcode.Instruction.d2f.code == opcode) {
            result.opCodeText = Opcode.Instruction.d2f.name();
        } else if (Opcode.Instruction.i2b.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2b.name();
        } else if (Opcode.Instruction.i2c.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2c.name();
        } else if (Opcode.Instruction.i2s.code == opcode) {
            result.opCodeText = Opcode.Instruction.i2s.name();
        } else if (Opcode.Instruction.lcmp.code == opcode) {
            result.opCodeText = Opcode.Instruction.lcmp.name();
        } else if (Opcode.Instruction.fcmpl.code == opcode) {
            result.opCodeText = Opcode.Instruction.fcmpl.name();
        } else if (Opcode.Instruction.fcmpg.code == opcode) {
            result.opCodeText = Opcode.Instruction.fcmpg.name();
        } else if (Opcode.Instruction.dcmpl.code == opcode) {
            result.opCodeText = Opcode.Instruction.dcmpl.name();
        } else if (Opcode.Instruction.dcmpg.code == opcode) {
            result.opCodeText = Opcode.Instruction.dcmpg.name();
        } else if (Opcode.Instruction.ifeq.code == opcode) {
            // if<cond>: ifeq = 153 (0x99) ifne = 154 (0x9a) iflt = 155 (0x9b) ifge = 156 (0x9c) ifgt = 157 (0x9d) ifle = 158 (0x9e)
            // Branch if int comparison with zero succeeds
            //
            // If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset,
            // where the offset is calculated to be (branchbyte1 << 8) | branchbyte2.
            // Execution then proceeds at that offset from the address of the opCode of this if<cond> instruction.
            // The target address must be that of an opCode of an instruction within the method that contains this if<cond> instruction.
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifeq.name(), result.branchbyte);
        } else if (Opcode.Instruction.ifne.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifne.name(), result.branchbyte);
        } else if (Opcode.Instruction.iflt.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.iflt.name(), result.branchbyte);
        } else if (Opcode.Instruction.ifge.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifge.name(), result.branchbyte);
        } else if (Opcode.Instruction.ifgt.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifgt.name(), result.branchbyte);
        } else if (Opcode.Instruction.ifle.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifle.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmpeq.code == opcode) {
            // if_icmp<cond>: if_icmpeq = 159 (0x9f) if_icmpne = 160 (0xa0) if_icmplt = 161 (0xa1) if_icmpge = 162 (0xa2) if_icmpgt = 163 (0xa3) if_icmple = 164 (0xa4)
            // Branch if int comparison succeeds
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_icmpeq.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmpne.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_icmpne.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmplt.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_icmplt.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmpge.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, result.opCodeText = Opcode.Instruction.if_icmpge.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmpgt.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_icmpgt.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_icmple.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_icmple.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_acmpeq.code == opcode) {
            // if_acmp<cond>: if_acmpeq = 165 (0xa5) if_acmpne = 166 (0xa6)
            // Branch if reference comparison succeeds
            // ..., value1, value2
            // Both value1 and value2 must be of type reference. They are both popped from the operand stack and compared.
            //
            // If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset,
            // where the offset is calculated to be (branchbyte1 << 8) | branchbyte2.
            // Execution then proceeds at that offset from the address of the opCode of this if_acmp<cond> instruction.
            // The target address must be that of an opCode of an instruction within the method that contains this if_acmp<cond> instruction.
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_acmpeq.name(), result.branchbyte);
        } else if (Opcode.Instruction.if_acmpne.code == opcode) {
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.if_acmpne.name(), result.branchbyte);
        } else if (Opcode.Instruction.goto_.code == opcode) {
            // Branch always
            // The unsigned bytes branchbyte1 and branchbyte2 are used to construct a signed 16-bit branchoffset,
            // where branchoffset is (branchbyte1 << 8) | branchbyte2.
            // Execution proceeds at that offset from the address of the opCode of this goto instruction.
            // The target address must be that of an opCode of an instruction within the method that contains this goto instruction.
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.goto_.getName(), result.branchbyte);
        } else if (Opcode.Instruction.jsr.code == opcode) {
            // Jump subroutine
            // The address of the opCode of the instruction immediately following this jsr instruction
            // is pushed onto the operand stack as a value of type returnAddress.
            // The unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset,
            // where the offset is (branchbyte1 << 8) | branchbyte2.
            // Execution proceeds at that offset from the address of this jsr instruction.
            // The target address must be that of an opCode of an instruction within the method that contains this jsr instruction.
            result.branchbyte = Integer.valueOf(pdis.readShort());
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.jsr.name(), result.branchbyte);
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
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifnull.name(), result.branchbyte);
        } else if (Opcode.Instruction.ifnonnull.code == opcode) {
            // Branch if reference not null
            result.branchbyte = pdis.readUnsignedShort();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.ifnonnull.name(), result.branchbyte);
        } else if (Opcode.Instruction.goto_w.code == opcode) {
            // Branch always (wide index)
            result.branchbyte = pdis.readInt();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.goto_w.name(), result.branchbyte);
        } else if (Opcode.Instruction.jsr_w.code == opcode) {
            // Jump subroutine (wide index)
            // --
            // The unsigned branchbyte1, branchbyte2, branchbyte3, and branchbyte4 are used to
            // construct a signed 32-bit offset, where the offset is
            // (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4.
            result.branchbyte = pdis.readInt();
            result.opCodeText = String.format(FORMAT_OPCODE_LOCAL, Opcode.Instruction.jsr_w.name(), result.branchbyte);
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
        String opCodeText = null;

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
         * will be <code>null</code> if the {@link Instruction} did not reference
         * to any {@link ClassFile#constant_pool} object.
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
         * Execution proceeds at that offset from the address of the opcode of current instruction.
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
         * @return {@link #cpIndex} value. <code>null</code> if no constant pool index.
         */
        public Integer getCpindex() {
            return this.cpIndex;
        }

        @Override
        public String toString() {
            return String.format("offset %04d: opcode [%02X] %s", this.offset, this.opCode, this.opCodeText);
        }

        /**
         * Get the {@link Instruction} analysis result with
         * {@link ClassFile#constant_pool} description.
         *
         * @param cf the {@link ClassFile}
         * @return {@link Instruction} analysis result
         */
        public String toString(ClassFile cf) {
            if (this.cpIndex != null) {
                String cpDesc = cf.getCPDescription(this.cpIndex);
                // Avoid too long description
                if (cpDesc.length() > 1000) {
                    cpDesc = cpDesc.substring(1, 1000);
                }
                return this.toString() + " - " + cpDesc;
            } else {
                return this.toString();
            }
        }
    }
}
