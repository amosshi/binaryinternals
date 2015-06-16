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
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Opcode parser to interpret the Java {@code code} byte array into human
 * readable text.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a
 * href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/Instructions.doc.html">
 * VM Spec: The Java Virtual Machine Instruction Set
 * </a>
 */
public final class Opcode {

    private static final String FORMAT_SD = "%s %d";
    private static final String FORMAT_SDS = "%s %d [%s]";
    // Opcode value
    private static final int op_nop = 0;
    private static final int op_aconst_null = 1;
    private static final int op_iconst_m1 = 2;
    private static final int op_iconst_0 = 3;
    private static final int op_iconst_1 = 4;
    private static final int op_iconst_2 = 5;
    private static final int op_iconst_3 = 6;
    private static final int op_iconst_4 = 7;
    private static final int op_iconst_5 = 8;
    private static final int op_lconst_0 = 9;
    private static final int op_lconst_1 = 10;
    private static final int op_fconst_0 = 11;
    private static final int op_fconst_1 = 12;
    private static final int op_fconst_2 = 13;
    private static final int op_dconst_0 = 14;
    private static final int op_dconst_1 = 15;
    private static final int op_bipush = 16;
    private static final int op_sipush = 17;
    private static final int op_ldc = 18;
    private static final int op_ldc_w = 19;
    private static final int op_ldc2_w = 20;
    private static final int op_iload = 21;
    private static final int op_lload = 22;
    private static final int op_fload = 23;
    private static final int op_dload = 24;
    private static final int op_aload = 25;
    private static final int op_iload_0 = 26;
    private static final int op_iload_1 = 27;
    private static final int op_iload_2 = 28;
    private static final int op_iload_3 = 29;
    private static final int op_lload_0 = 30;
    private static final int op_lload_1 = 31;
    private static final int op_lload_2 = 32;
    private static final int op_lload_3 = 33;
    private static final int op_fload_0 = 34;
    private static final int op_fload_1 = 35;
    private static final int op_fload_2 = 36;
    private static final int op_fload_3 = 37;
    private static final int op_dload_0 = 38;
    private static final int op_dload_1 = 39;
    private static final int op_dload_2 = 40;
    private static final int op_dload_3 = 41;
    private static final int op_aload_0 = 42;
    private static final int op_aload_1 = 43;
    private static final int op_aload_2 = 44;
    private static final int op_aload_3 = 45;
    private static final int op_iaload = 46;
    private static final int op_laload = 47;
    private static final int op_faload = 48;
    private static final int op_daload = 49;
    private static final int op_aaload = 50;
    private static final int op_baload = 51;
    private static final int op_caload = 52;
    private static final int op_saload = 53;
    private static final int op_istore = 54;
    private static final int op_lstore = 55;
    private static final int op_fstore = 56;
    private static final int op_dstore = 57;
    private static final int op_astore = 58;
    private static final int op_istore_0 = 59;
    private static final int op_istore_1 = 60;
    private static final int op_istore_2 = 61;
    private static final int op_istore_3 = 62;
    private static final int op_lstore_0 = 63;
    private static final int op_lstore_1 = 64;
    private static final int op_lstore_2 = 65;
    private static final int op_lstore_3 = 66;
    private static final int op_fstore_0 = 67;
    private static final int op_fstore_1 = 68;
    private static final int op_fstore_2 = 69;
    private static final int op_fstore_3 = 70;
    private static final int op_dstore_0 = 71;
    private static final int op_dstore_1 = 72;
    private static final int op_dstore_2 = 73;
    private static final int op_dstore_3 = 74;
    private static final int op_astore_0 = 75;
    private static final int op_astore_1 = 76;
    private static final int op_astore_2 = 77;
    private static final int op_astore_3 = 78;
    private static final int op_iastore = 79;
    private static final int op_lastore = 80;
    private static final int op_fastore = 81;
    private static final int op_dastore = 82;
    private static final int op_aastore = 83;
    private static final int op_bastore = 84;
    private static final int op_castore = 85;
    private static final int op_sastore = 86;
    private static final int op_pop = 87;
    private static final int op_pop2 = 88;
    private static final int op_dup = 89;
    private static final int op_dup_x1 = 90;
    private static final int op_dup_x2 = 91;
    private static final int op_dup2 = 92;
    private static final int op_dup2_x1 = 93;
    private static final int op_dup2_x2 = 94;
    private static final int op_swap = 95;
    private static final int op_iadd = 96;
    private static final int op_ladd = 97;
    private static final int op_fadd = 98;
    private static final int op_dadd = 99;
    private static final int op_isub = 100;
    private static final int op_lsub = 101;
    private static final int op_fsub = 102;
    private static final int op_dsub = 103;
    private static final int op_imul = 104;
    private static final int op_lmul = 105;
    private static final int op_fmul = 106;
    private static final int op_dmul = 107;
    private static final int op_idiv = 108;
    private static final int op_ldiv = 109;
    private static final int op_fdiv = 110;
    private static final int op_ddiv = 111;
    private static final int op_irem = 112;
    private static final int op_lrem = 113;
    private static final int op_frem = 114;
    private static final int op_drem = 115;
    private static final int op_ineg = 116;
    private static final int op_lneg = 117;
    private static final int op_fneg = 118;
    private static final int op_dneg = 119;
    private static final int op_ishl = 120;
    private static final int op_lshl = 121;
    private static final int op_ishr = 122;
    private static final int op_lshr = 123;
    private static final int op_iushr = 124;
    private static final int op_lushr = 125;
    private static final int op_iand = 126;
    private static final int op_land = 127;
    private static final int op_ior = 128;
    private static final int op_lor = 129;
    private static final int op_ixor = 130;
    private static final int op_lxor = 131;
    private static final int op_iinc = 132;
    private static final int op_i2l = 133;
    private static final int op_i2f = 134;
    private static final int op_i2d = 135;
    private static final int op_l2i = 136;
    private static final int op_l2f = 137;
    private static final int op_l2d = 138;
    private static final int op_f2i = 139;
    private static final int op_f2l = 140;
    private static final int op_f2d = 141;
    private static final int op_d2i = 142;
    private static final int op_d2l = 143;
    private static final int op_d2f = 144;
    private static final int op_i2b = 145;
    private static final int op_i2c = 146;
    private static final int op_i2s = 147;
    private static final int op_lcmp = 148;
    private static final int op_fcmpl = 149;
    private static final int op_fcmpg = 150;
    private static final int op_dcmpl = 151;
    private static final int op_dcmpg = 152;
    private static final int op_ifeq = 153;
    private static final int op_ifne = 154;
    private static final int op_iflt = 155;
    private static final int op_ifge = 156;
    private static final int op_ifgt = 157;
    private static final int op_ifle = 158;
    private static final int op_if_icmpeq = 159;
    private static final int op_if_icmpne = 160;
    private static final int op_if_icmplt = 161;
    private static final int op_if_icmpge = 162;
    private static final int op_if_icmpgt = 163;
    private static final int op_if_icmple = 164;
    private static final int op_if_acmpeq = 165;
    private static final int op_if_acmpne = 166;
    private static final int op_goto = 167;
    private static final int op_jsr = 168;
    private static final int op_ret = 169;
    private static final int op_tableswitch = 170;
    private static final int op_lookupswitch = 171;
    private static final int op_ireturn = 172;
    private static final int op_lreturn = 173;
    private static final int op_freturn = 174;
    private static final int op_dreturn = 175;
    private static final int op_areturn = 176;
    private static final int op_return = 177;
    private static final int op_getstatic = 178;
    private static final int op_putstatic = 179;
    private static final int op_getfield = 180;
    private static final int op_putfield = 181;
    private static final int op_invokevirtual = 182;
    private static final int op_invokespecial = 183;
    private static final int op_invokestatic = 184;
    private static final int op_invokeinterface = 185;
    // 186: For historical reasons, opcode value 186 is not used.
    private static final int op_new = 187;
    private static final int op_newarray = 188;
    private static final int op_anewarray = 189;
    private static final int op_arraylength = 190;
    private static final int op_athrow = 191;
    private static final int op_checkcast = 192;
    private static final int op_instanceof = 193;
    private static final int op_monitorenter = 194;
    private static final int op_monitorexit = 195;
    private static final int op_wide = 196;
    private static final int op_multianewarray = 197;
    private static final int op_ifnull = 198;
    private static final int op_ifnonnull = 199;
    private static final int op_goto_w = 200;
    private static final int op_jsr_w = 201;
    // Opcode name text
    private static final String name_nop = "nop";
    private static final String name_aconst_null = "aconst_null";
    private static final String name_iconst_m1 = "iconst_m1";
    private static final String name_iconst_0 = "iconst_0";
    private static final String name_iconst_1 = "iconst_1";
    private static final String name_iconst_2 = "iconst_2";
    private static final String name_iconst_3 = "iconst_3";
    private static final String name_iconst_4 = "iconst_4";
    private static final String name_iconst_5 = "iconst_5";
    private static final String name_lconst_0 = "lconst_0";
    private static final String name_lconst_1 = "lconst_1";
    private static final String name_fconst_0 = "fconst_0";
    private static final String name_fconst_1 = "fconst_1";
    private static final String name_fconst_2 = "fconst_2";
    private static final String name_dconst_0 = "dconst_0";
    private static final String name_dconst_1 = "dconst_1";
    private static final String name_bipush = "bipush";
    private static final String name_sipush = "sipush";
    private static final String name_ldc = "ldc";
    private static final String name_ldc_w = "ldc_w";
    private static final String name_ldc2_w = "ldc2_w";
    private static final String name_iload = "iload";
    private static final String name_lload = "lload";
    private static final String name_fload = "fload";
    private static final String name_dload = "dload";
    private static final String name_aload = "aload";
    private static final String name_iload_0 = "iload_0";
    private static final String name_iload_1 = "iload_1";
    private static final String name_iload_2 = "iload_2";
    private static final String name_iload_3 = "iload_3";
    private static final String name_lload_0 = "lload_0";
    private static final String name_lload_1 = "lload_1";
    private static final String name_lload_2 = "lload_2";
    private static final String name_lload_3 = "lload_3";
    private static final String name_fload_0 = "fload_0";
    private static final String name_fload_1 = "fload_1";
    private static final String name_fload_2 = "fload_2";
    private static final String name_fload_3 = "fload_3";
    private static final String name_dload_0 = "dload_0";
    private static final String name_dload_1 = "dload_1";
    private static final String name_dload_2 = "dload_2";
    private static final String name_dload_3 = "dload_3";
    private static final String name_aload_0 = "aload_0";
    private static final String name_aload_1 = "aload_1";
    private static final String name_aload_2 = "aload_2";
    private static final String name_aload_3 = "aload_3";
    private static final String name_iaload = "iaload";
    private static final String name_laload = "laload";
    private static final String name_faload = "faload";
    private static final String name_daload = "daload";
    private static final String name_aaload = "aaload";
    private static final String name_baload = "baload";
    private static final String name_caload = "caload";
    private static final String name_saload = "saload";
    private static final String name_istore = "istore";
    private static final String name_lstore = "lstore";
    private static final String name_fstore = "fstore";
    private static final String name_dstore = "dstore";
    private static final String name_astore = "astore";
    private static final String name_istore_0 = "istore_0";
    private static final String name_istore_1 = "istore_1";
    private static final String name_istore_2 = "istore_2";
    private static final String name_istore_3 = "istore_3";
    private static final String name_lstore_0 = "lstore_0";
    private static final String name_lstore_1 = "lstore_1";
    private static final String name_lstore_2 = "lstore_2";
    private static final String name_lstore_3 = "lstore_3";
    private static final String name_fstore_0 = "fstore_0";
    private static final String name_fstore_1 = "fstore_1";
    private static final String name_fstore_2 = "fstore_2";
    private static final String name_fstore_3 = "fstore_3";
    private static final String name_dstore_0 = "dstore_0";
    private static final String name_dstore_1 = "dstore_1";
    private static final String name_dstore_2 = "dstore_2";
    private static final String name_dstore_3 = "dstore_3";
    private static final String name_astore_0 = "astore_0";
    private static final String name_astore_1 = "astore_1";
    private static final String name_astore_2 = "astore_2";
    private static final String name_astore_3 = "astore_3";
    private static final String name_iastore = "iastore";
    private static final String name_lastore = "lastore";
    private static final String name_fastore = "fastore";
    private static final String name_dastore = "dastore";
    private static final String name_aastore = "aastore";
    private static final String name_bastore = "bastore";
    private static final String name_castore = "castore";
    private static final String name_sastore = "sastore";
    private static final String name_pop = "pop";
    private static final String name_pop2 = "pop2";
    private static final String name_dup = "dup";
    private static final String name_dup_x1 = "dup_x1";
    private static final String name_dup_x2 = "dup_x2";
    private static final String name_dup2 = "dup2";
    private static final String name_dup2_x1 = "dup2_x1";
    private static final String name_dup2_x2 = "dup2_x2";
    private static final String name_swap = "swap";
    private static final String name_iadd = "iadd";
    private static final String name_ladd = "ladd";
    private static final String name_fadd = "fadd";
    private static final String name_dadd = "dadd";
    private static final String name_isub = "isub";
    private static final String name_lsub = "lsub";
    private static final String name_fsub = "fsub";
    private static final String name_dsub = "dsub";
    private static final String name_imul = "imul";
    private static final String name_lmul = "lmul";
    private static final String name_fmul = "fmul";
    private static final String name_dmul = "dmul";
    private static final String name_idiv = "idiv";
    private static final String name_ldiv = "ldiv";
    private static final String name_fdiv = "fdiv";
    private static final String name_ddiv = "ddiv";
    private static final String name_irem = "irem";
    private static final String name_lrem = "lrem";
    private static final String name_frem = "frem";
    private static final String name_drem = "drem";
    private static final String name_ineg = "ineg";
    private static final String name_lneg = "lneg";
    private static final String name_fneg = "fneg";
    private static final String name_dneg = "dneg";
    private static final String name_ishl = "ishl";
    private static final String name_lshl = "lshl";
    private static final String name_ishr = "ishr";
    private static final String name_lshr = "lshr";
    private static final String name_iushr = "iushr";
    private static final String name_lushr = "lushr";
    private static final String name_iand = "iand";
    private static final String name_land = "land";
    private static final String name_ior = "ior";
    private static final String name_lor = "lor";
    private static final String name_ixor = "ixor";
    private static final String name_lxor = "lxor";
    private static final String name_iinc = "iinc";
    private static final String name_i2l = "i2l";
    private static final String name_i2f = "i2f";
    private static final String name_i2d = "i2d";
    private static final String name_l2i = "l2i";
    private static final String name_l2f = "l2f";
    private static final String name_l2d = "l2d";
    private static final String name_f2i = "f2i";
    private static final String name_f2l = "f2l";
    private static final String name_f2d = "f2d";
    private static final String name_d2i = "d2i";
    private static final String name_d2l = "d2l";
    private static final String name_d2f = "d2f";
    private static final String name_i2b = "i2b";
    private static final String name_i2c = "i2c";
    private static final String name_i2s = "i2s";
    private static final String name_lcmp = "lcmp";
    private static final String name_fcmpl = "fcmpl";
    private static final String name_fcmpg = "fcmpg";
    private static final String name_dcmpl = "dcmpl";
    private static final String name_dcmpg = "dcmpg";
    private static final String name_ifeq = "ifeq";
    private static final String name_ifne = "ifne";
    private static final String name_iflt = "iflt";
    private static final String name_ifge = "ifge";
    private static final String name_ifgt = "ifgt";
    private static final String name_ifle = "ifle";
    private static final String name_if_icmpeq = "if_icmpeq";
    private static final String name_if_icmpne = "if_icmpne";
    private static final String name_if_icmplt = "if_icmplt";
    private static final String name_if_icmpge = "if_icmpge";
    private static final String name_if_icmpgt = "if_icmpgt";
    private static final String name_if_icmple = "if_icmple";
    private static final String name_if_acmpeq = "if_acmpeq";
    private static final String name_if_acmpne = "if_acmpne";
    private static final String name_goto = "goto";
    private static final String name_jsr = "jsr";
    private static final String name_ret = "ret";
    private static final String name_tableswitch = "tableswitch";
    private static final String name_lookupswitch = "lookupswitch";
    private static final String name_ireturn = "ireturn";
    private static final String name_lreturn = "lreturn";
    private static final String name_freturn = "freturn";
    private static final String name_dreturn = "dreturn";
    private static final String name_areturn = "areturn";
    private static final String name_return = "return";
    private static final String name_getstatic = "getstatic";
    private static final String name_putstatic = "putstatic";
    private static final String name_getfield = "getfield";
    private static final String name_putfield = "putfield";
    private static final String name_invokevirtual = "invokevirtual";
    private static final String name_invokespecial = "invokespecial";
    private static final String name_invokestatic = "invokestatic";
    private static final String name_invokeinterface = "invokeinterface";
    private static final String name_new = "name_new";
    private static final String name_newarray = "newarray";
    private static final String name_anewarray = "anewarray";
    private static final String name_arraylength = "arraylength";
    private static final String name_athrow = "athrow";
    private static final String name_checkcast = "checkcast";
    private static final String name_instanceof = "instanceof";
    private static final String name_monitorenter = "monitorenter";
    private static final String name_monitorexit = "monitorexit";
    private static final String name_wide = "wide";
    private static final String name_wide_iload = "wide iload";
    private static final String name_wide_lload = "wide lload";
    private static final String name_wide_fload = "wide fload";
    private static final String name_wide_dload = "wide dload";
    private static final String name_wide_aload = "wide aload";
    private static final String name_wide_istore = "wide istore";
    private static final String name_wide_lstore = "wide lstore";
    private static final String name_wide_fstore = "wide fstore";
    private static final String name_wide_dstore = "wide dstore";
    private static final String name_wide_astore = "wide astore";
    private static final String name_wide_iinc = "wide iinc";
    private static final String name_wide_ret = "wide ret";
    private static final String name_multianewarray = "multianewarray";
    private static final String name_ifnull = "ifnull";
    private static final String name_ifnonnull = "ifnonnull";
    private static final String name_goto_w = "goto_w";
    private static final String name_jsr_w = "jsr_w";
    // Quick opcodes
    // Reserved opcodes
    private static final int op_breakpoint = 202;
    private static final int op_impdep1 = 254;
    private static final int op_impdep2 = 255;
    private static final String name_breakpoint = "breakpoint";
    private static final String name_impdep1 = "impdep1";
    private static final String name_impdep2 = "impdep2";

    private Opcode() {
    }

    /**
     * Parse the java byte code in a method as a string.
     *
     * @param code Byte array of method source code
     * @param classFile The {@link ClassFile} object contains the method
     * @return Readable string of the method source code
     */
    public static CodeResult parseCode(final byte[] code, final ClassFile classFile) {
        if ((code == null) || (code.length < 1)) {
            return null;
        }

        CodeResult codeResult = new CodeResult();

        final PosByteArrayInputStream pbais = new PosByteArrayInputStream(code);
        final PosDataInputStream pdis = new PosDataInputStream(pbais);

        final StringBuilder sb = new StringBuilder(code.length * 10);
        OpcodeResult parseResult;
        while (pdis.getPos() < code.length) {
            try {
                parseResult = parseOpcode(pdis, classFile);
                sb.append(parseResult.OpcodeText);
                sb.append('\n');
                if (parseResult.CPIndex1 != -1) {
                    codeResult.CPIndexes.add(parseResult.CPIndex1);
                }
            } catch (IOException ioe) {
                //System.err.println(ioe.toString());
                //ioe.printStackTrace();
                break;
            }
        }

        codeResult.OpcodeTexts = sb.toString();
        return codeResult;
    }

    private static OpcodeResult parseOpcode(final PosDataInputStream pdis, final ClassFile classFile)
            throws IOException {
        final int curPos = pdis.getPos();
        final int opcode = pdis.read();
        OpcodeResult parseResult = new OpcodeResult();

        int byteValue = 0;
        int byteValue2 = 0;
        int shortValue = 0;
        int intValue = 0;

        String opcodeText = null;

        switch (opcode) {
            // Do nothing
            case Opcode.op_nop:
                opcodeText = Opcode.name_nop;
                break;

            // Push null
            // Push the null object reference onto the operand stack.
            case Opcode.op_aconst_null:
                opcodeText = Opcode.name_aconst_null;
                break;

            // Push int constant -1
            case Opcode.op_iconst_m1:
                opcodeText = Opcode.name_iconst_m1;
                break;

            // Push int constant
            // Push the int constant <i> (-1, 0, 1, 2, 3, 4 or 5) onto the operand stack. 
            // ???: -1, is iconst_m1 is a new one?
            case Opcode.op_iconst_0:
                opcodeText = Opcode.name_iconst_0;
                break;

            case Opcode.op_iconst_1:
                opcodeText = Opcode.name_iconst_1;
                break;

            case Opcode.op_iconst_2:
                opcodeText = Opcode.name_iconst_2;
                break;

            case Opcode.op_iconst_3:
                opcodeText = Opcode.name_iconst_3;
                break;

            case Opcode.op_iconst_4:
                opcodeText = Opcode.name_iconst_4;
                break;

            case Opcode.op_iconst_5:
                opcodeText = Opcode.name_iconst_5;
                break;

            // Push long constant
            // Push the long constant <l> (0 or 1) onto the operand stack. 
            case Opcode.op_lconst_0:
                opcodeText = Opcode.name_lconst_0;
                break;

            case Opcode.op_lconst_1:
                opcodeText = Opcode.name_lconst_1;
                break;

            // Push float
            // Push the float constant <f> (0.0, 1.0, or 2.0) onto the operand stack.
            case Opcode.op_fconst_0:
                opcodeText = Opcode.name_fconst_0;
                break;

            case Opcode.op_fconst_1:
                opcodeText = Opcode.name_fconst_1;
                break;

            case Opcode.op_fconst_2:
                opcodeText = Opcode.name_fconst_2;
                break;

            // Push double
            // Push the double constant <d> (0.0 or 1.0) onto the operand stack. 
            case Opcode.op_dconst_0:
                opcodeText = Opcode.name_dconst_0;
                break;

            case Opcode.op_dconst_1:
                opcodeText = Opcode.name_dconst_1;
                break;

            // Push the immediate byte value
            case Opcode.op_bipush:
                // The immediate byte is sign-extended to an int value. 
                // That value is pushed onto the operand stack.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_bipush, byteValue);
                break;

            // Push the immediate short value
            case Opcode.op_sipush:
                // The immediate unsigned byte1 and byte2 values are assembled into an intermediate short 
                // where the value of the short is (byte1 << 8) | byte2. 
                // The intermediate value is then sign-extended to an int value. 
                // That value is pushed onto the operand stack.
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.op_sipush, shortValue);
                break;

            // Push item from runtime constant pool
            case Opcode.op_ldc:
                // The index is an unsigned byte that must be a valid index into the runtime constant pool of the current class (?.6). 
                // The runtime constant pool entry at index 
                //   either must be a runtime constant of type int or float, 
                //   or must be a symbolic reference to a string literal (?.1).
                byteValue = pdis.readUnsignedByte();
                parseResult.CPIndex1 = byteValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_ldc, byteValue, classFile.getCPDescription(byteValue));
                break;

            // Push item from runtime constant pool (wide index)
            case Opcode.op_ldc_w:
                // The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index
                // into the runtime constant pool of the current class (?.6), 
                // where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2. 
                // The index must be a valid index into the runtime constant pool of the current class. 
                // The runtime constant pool entry at the index 
                //   either must be a runtime constant of type int or float, 
                //   or must be a symbolic reference to a string literal (?.1). 
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_ldc_w, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Push long or double from runtime constant pool (wide index)
            case Opcode.op_ldc2_w:
                // The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index 
                // into the runtime constant pool of the current class (?.6), 
                // where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2. 
                // The index must be a valid index into the runtime constant pool of the current class. 
                // The runtime constant pool entry at the index must be a runtime constant of type long or double (?.1). 
                // The numeric value of that runtime constant is pushed onto the operand stack as a long or double, respectively.
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_ldc2_w, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Load int from local variable
            case Opcode.op_iload:
                // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
                // The local variable at index must contain an int. 
                // The value of the local variable at index is pushed onto the operand stack.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_iload, byteValue);
                break;

            // Load long from local variable
            case Opcode.op_lload:
                // The index is an unsigned byte. 
                // Both index and index + 1 must be indices into the local variable array of the current frame (?.6). 
                // The local variable at index must contain a long. 
                // The value of the local variable at index is pushed onto the operand stack. 
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_lload, byteValue);
                break;

            // Load float from local variable
            case Opcode.op_fload:
                // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
                // The local variable at index must contain a float. 
                // The value of the local variable at index is pushed onto the operand stack.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_fload, byteValue);
                break;

            // Load double from local variable
            case Opcode.op_dload:
                // The index is an unsigned byte. 
                // Both index and index + 1 must be indices into the local variable array of the current frame (?.6). 
                // The local variable at index must contain a double. 
                // The value of the local variable at index is pushed onto the operand stack.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_dload, byteValue);
                break;

            // Load reference from local variable
            case Opcode.op_aload:
                // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
                // The local variable at index must contain a reference. 
                // The objectref in the local variable at index is pushed onto the operand stack.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_aload, byteValue);
                break;

            // Load int from local variable
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The local variable at <n> must contain an int. 
            // The value of the local variable at <n> is pushed onto the operand stack.
            case Opcode.op_iload_0:
                opcodeText = Opcode.name_iload_0;
                break;

            case Opcode.op_iload_1:
                opcodeText = Opcode.name_iload_1;
                break;

            case Opcode.op_iload_2:
                opcodeText = Opcode.name_iload_2;
                break;

            case Opcode.op_iload_3:
                opcodeText = Opcode.name_iload_3;
                break;

            case Opcode.op_lload_0:
                opcodeText = Opcode.name_lload_0;
                break;

            // Load long from local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6). 
            // The local variable at <n> must contain a long. 
            // The value of the local variable at <n> is pushed onto the operand stack. 
            case Opcode.op_lload_1:
                opcodeText = Opcode.name_lload_1;
                break;

            case Opcode.op_lload_2:
                opcodeText = Opcode.name_lload_2;
                break;

            case Opcode.op_lload_3:
                opcodeText = Opcode.name_lload_3;
                break;

            // Load float from local variable
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The local variable at <n> must contain a float. 
            // The value of the local variable at <n> is pushed onto the operand stack.
            case Opcode.op_fload_0:
                opcodeText = Opcode.name_fload_0;
                break;

            case Opcode.op_fload_1:
                opcodeText = Opcode.name_fload_1;
                break;

            case Opcode.op_fload_2:
                opcodeText = Opcode.name_fload_2;
                break;

            case Opcode.op_fload_3:
                opcodeText = Opcode.name_fload_3;
                break;

            // Load double from local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6). 
            // The local variable at <n> must contain a double. 
            // The value of the local variable at <n> is pushed onto the operand stack. 
            case Opcode.op_dload_0:
                opcodeText = Opcode.name_dload_0;
                break;

            case Opcode.op_dload_1:
                opcodeText = Opcode.name_dload_1;
                break;

            case Opcode.op_dload_2:
                opcodeText = Opcode.name_dload_2;
                break;

            case Opcode.op_dload_3:
                opcodeText = Opcode.name_dload_3;
                break;

            // Load reference from local variable
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The local variable at <n> must contain a reference. 
            // The objectref in the local variable at index is pushed onto the operand stack.
            case Opcode.op_aload_0:
                opcodeText = Opcode.name_aload_0;
                break;

            case Opcode.op_aload_1:
                opcodeText = Opcode.name_aload_1;
                break;

            case Opcode.op_aload_2:
                opcodeText = Opcode.name_aload_2;
                break;

            case Opcode.op_aload_3:
                opcodeText = Opcode.name_aload_3;
                break;

            // Load int from array
            // The arrayref must be of type reference and must refer to an array 
            // whose components are of type int. 
            // The index must be of type int. 
            // Both arrayref and index are popped from the operand stack. 
            // The int value in the component of the array at index is retrieved and pushed onto the operand stack.
            case Opcode.op_iaload:
                opcodeText = Opcode.name_iaload;
                break;

            // Load long from array
            case Opcode.op_laload:
                opcodeText = Opcode.name_laload;
                break;

            // Load float from array
            case Opcode.op_faload:
                opcodeText = Opcode.name_faload;
                break;

            // Load double from array
            case Opcode.op_daload:
                opcodeText = Opcode.name_daload;
                break;

            // Load reference from array
            case Opcode.op_aaload:
                opcodeText = Opcode.name_aaload;
                break;

            // Load byte or boolean from array
            case Opcode.op_baload:
                opcodeText = Opcode.name_baload;
                break;

            // Load char from array
            case Opcode.op_caload:
                opcodeText = Opcode.name_caload;
                break;

            // Load short from array
            case Opcode.op_saload:
                opcodeText = Opcode.name_saload;
                break;

            // Store int into local variable
            case Opcode.op_istore:
                // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
                // The value on the top of the operand stack must be of type int. 
                // It is popped from the operand stack, and the value of the local variable at index is set to value.
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_istore, byteValue);
                break;

            // Store long into local variable
            case Opcode.op_lstore:
                // The index is an unsigned byte. 
                // Both index and index + 1 must be indices into the local variable array of the current frame (?.6). 
                // The value on the top of the operand stack must be of type long. 
                // It is popped from the operand stack, and the local variables at index and index + 1 are set to value. 
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_lstore, byteValue);
                break;

            // Store float into local variable
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type float. 
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'. 
            // The value of the local variable at index is set to value'.
            case Opcode.op_fstore:
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_fstore, byteValue);
                break;

            // Store double into local variable
            // The index is an unsigned byte. Both index and index + 1 must be indices into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type double. 
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'. 
            // The local variables at index and index + 1 are set to value'. 
            case Opcode.op_dstore:
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_dstore, byteValue);
                break;

            // Store reference into local variable
            // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
            // The objectref on the top of the operand stack must be of type returnAddress or of type reference. 
            // It is popped from the operand stack, and the value of the local variable at index is set to objectref.
            case Opcode.op_astore:
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_dstore, byteValue);
                break;

            // Store int into local variable
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type int. 
            // It is popped from the operand stack, and the value of the local variable at <n> is set to value. 
            case Opcode.op_istore_0:
                opcodeText = Opcode.name_istore_0;
                break;

            case Opcode.op_istore_1:
                opcodeText = Opcode.name_istore_1;
                break;

            case Opcode.op_istore_2:
                opcodeText = Opcode.name_istore_2;
                break;

            case Opcode.op_istore_3:
                opcodeText = Opcode.name_istore_3;
                break;

            // Store long into local variable
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type long. 
            // It is popped from the operand stack, and the local variables at <n> and <n> + 1 are set to value.
            case Opcode.op_lstore_0:
                opcodeText = Opcode.name_lstore_0;
                break;

            case Opcode.op_lstore_1:
                opcodeText = Opcode.name_lstore_1;
                break;

            case Opcode.op_lstore_2:
                opcodeText = Opcode.name_lstore_2;
                break;

            case Opcode.op_lstore_3:
                opcodeText = Opcode.name_lstore_3;
                break;

            // Store float into local variable 
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type float. 
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'. 
            // The value of the local variable at <n> is set to value'.
            case Opcode.op_fstore_0:
                opcodeText = Opcode.name_fstore_0;
                break;

            case Opcode.op_fstore_1:
                opcodeText = Opcode.name_fstore_1;
                break;

            case Opcode.op_fstore_2:
                opcodeText = Opcode.name_fstore_2;
                break;

            case Opcode.op_fstore_3:
                opcodeText = Opcode.name_fstore_3;
                break;

            // Store double into local variable 
            // Both <n> and <n> + 1 must be indices into the local variable array of the current frame (?.6). 
            // The value on the top of the operand stack must be of type double. 
            // It is popped from the operand stack and undergoes value set conversion (?.8.3), resulting in value'. 
            // The local variables at <n> and <n> + 1 are set to value'.
            case Opcode.op_dstore_0:
                opcodeText = Opcode.name_dstore_0;
                break;

            case Opcode.op_dstore_1:
                opcodeText = Opcode.name_dstore_1;
                break;

            case Opcode.op_dstore_2:
                opcodeText = Opcode.name_dstore_2;
                break;

            case Opcode.op_dstore_3:
                opcodeText = Opcode.name_dstore_3;
                break;

            // Store reference into local variable 
            // The <n> must be an index into the local variable array of the current frame (?.6). 
            // The objectref on the top of the operand stack must be of type returnAddress or of type reference. 
            // It is popped from the operand stack, and the value of the local variable at <n> is set to objectref.
            case Opcode.op_astore_0:
                opcodeText = Opcode.name_astore_0;
                break;

            case Opcode.op_astore_1:
                opcodeText = Opcode.name_astore_1;
                break;

            case Opcode.op_astore_2:
                opcodeText = Opcode.name_astore_2;
                break;

            case Opcode.op_astore_3:
                opcodeText = Opcode.name_astore_3;
                break;

            // Store into int array
            // ..., arrayref, index, value
            // The arrayref must be of type reference and must refer to an array whose components are of type int. 
            // Both index and value must be of type int. 
            // The arrayref, index, and value are popped from the operand stack. 
            // The int value is stored as the component of the array indexed by index. 
            case Opcode.op_iastore:
                opcodeText = Opcode.name_iastore;
                break;

            // Store into long array
            // ..., arrayref, index, value
            case Opcode.op_lastore:
                opcodeText = Opcode.name_lastore;
                break;

            // Store into float array
            // ..., arrayref, index, value
            case Opcode.op_fastore:
                opcodeText = Opcode.name_fastore;
                break;

            // Store into double array
            // ..., arrayref, index, value
            case Opcode.op_dastore:
                opcodeText = Opcode.name_dastore;
                break;

            // Store into reference array
            // ..., arrayref, index, value
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_aastore:
                opcodeText = Opcode.name_aastore;
                break;

            // Store into byte or boolean array
            // ..., arrayref, index, value
            case Opcode.op_bastore:
                opcodeText = Opcode.name_bastore;
                break;

            // Store into char array
            // ..., arrayref, index, value
            case Opcode.op_castore:
                opcodeText = Opcode.name_castore;
                break;

            // Store into short array 
            // ..., arrayref, index, value
            case Opcode.op_sastore:
                opcodeText = Opcode.name_sastore;
                break;

            // Pop the top operand stack value
            // The pop instruction must not be used unless value is a value of a category 1 computational type (?.11.1).
            case Opcode.op_pop:
                opcodeText = Opcode.name_pop;
                break;

            // Pop the top one or two operand stack values
            // Form 1: ..., value2, value1
            //   where each of value1 and value2 is a value of a category 1 computational type (?.11.1).
            // Form 2: ..., value
            //   where value is a value of a category 2 computational type (?.11.1).
            case Opcode.op_pop2:
                opcodeText = Opcode.name_pop2;
                break;

            // Duplicate the top operand stack value
            // ..., value --> ..., value, value
            case Opcode.op_dup:
                opcodeText = Opcode.name_dup;
                break;

            // Duplicate the top operand stack value and insert two values down
            // ..., value2, value1 --> ..., value1, value2, value1
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_dup_x1:
                opcodeText = Opcode.name_dup_x1;
                break;

            // Duplicate the top operand stack value and insert two or three values down
            // Form 1: ..., value3, value2, value1 -->  ..., value1, value3, value2, value1
            // Form 2: ..., value2, value1 --> ..., value1, value2, value1
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_dup_x2:
                opcodeText = Opcode.name_dup_x2;
                break;

            // Duplicate the top one or two operand stack values
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_dup2:
                opcodeText = Opcode.name_dup2;
                break;

            // Duplicate the top one or two operand stack values and insert two or three values down
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_dup2_x1:
                opcodeText = Opcode.name_dup2_x1;
                break;

            // Duplicate the top one or two operand stack values and insert two, three, or four values down
            // ** Very complex, see the <VM Spec> for detail
            case Opcode.op_dup2_x2:
                opcodeText = Opcode.name_dup2_x2;
                break;

            // Swap the top two operand stack values
            // ..., value2, value1 --> ..., value1, value2
            // The swap instruction must not be used unless value1 and value2 are both values of a category 1 computational type (?.11.1).
            case Opcode.op_swap:
                opcodeText = Opcode.name_swap;
                break;

            case Opcode.op_iadd:
                opcodeText = Opcode.name_iadd;
                break;

            case Opcode.op_ladd:
                opcodeText = Opcode.name_ladd;
                break;

            case Opcode.op_fadd:
                opcodeText = Opcode.name_fadd;
                break;

            case Opcode.op_dadd:
                opcodeText = Opcode.name_dadd;
                break;

            case Opcode.op_isub:
                opcodeText = Opcode.name_isub;
                break;

            case Opcode.op_lsub:
                opcodeText = Opcode.name_lsub;
                break;

            case Opcode.op_fsub:
                opcodeText = Opcode.name_fsub;
                break;

            case Opcode.op_dsub:
                opcodeText = Opcode.name_dsub;
                break;

            case Opcode.op_imul:
                opcodeText = Opcode.name_imul;
                break;

            case Opcode.op_lmul:
                opcodeText = Opcode.name_lmul;
                break;

            case Opcode.op_fmul:
                opcodeText = Opcode.name_fmul;
                break;

            case Opcode.op_dmul:
                opcodeText = Opcode.name_dmul;
                break;

            case Opcode.op_idiv:
                opcodeText = Opcode.name_idiv;
                break;

            case Opcode.op_ldiv:
                opcodeText = Opcode.name_ldiv;
                break;

            case Opcode.op_fdiv:
                opcodeText = Opcode.name_fdiv;
                break;

            case Opcode.op_ddiv:
                opcodeText = Opcode.name_ddiv;
                break;

            case Opcode.op_irem:
                opcodeText = Opcode.name_irem;
                break;

            case Opcode.op_lrem:
                opcodeText = Opcode.name_lrem;
                break;

            case Opcode.op_frem:
                opcodeText = Opcode.name_frem;
                break;

            case Opcode.op_drem:
                opcodeText = Opcode.name_drem;
                break;

            case Opcode.op_ineg:
                opcodeText = Opcode.name_ineg;
                break;

            case Opcode.op_lneg:
                opcodeText = Opcode.name_lneg;
                break;

            case Opcode.op_fneg:
                opcodeText = Opcode.name_fneg;
                break;

            case Opcode.op_dneg:
                opcodeText = Opcode.name_dneg;
                break;

            case Opcode.op_ishl:
                opcodeText = Opcode.name_ishl;
                break;

            case Opcode.op_lshl:
                opcodeText = Opcode.name_lshl;
                break;

            case Opcode.op_ishr:
                opcodeText = Opcode.name_ishr;
                break;

            case Opcode.op_lshr:
                opcodeText = Opcode.name_lshr;
                break;

            case Opcode.op_iushr:
                opcodeText = Opcode.name_iushr;
                break;

            case Opcode.op_lushr:
                opcodeText = Opcode.name_lushr;
                break;

            case Opcode.op_iand:
                opcodeText = Opcode.name_iand;
                break;

            case Opcode.op_land:
                opcodeText = Opcode.name_land;
                break;

            case Opcode.op_ior:
                opcodeText = Opcode.name_ior;
                break;

            case Opcode.op_lor:
                opcodeText = Opcode.name_lor;
                break;

            case Opcode.op_ixor:
                opcodeText = Opcode.name_ixor;
                break;

            case Opcode.op_lxor:
                opcodeText = Opcode.name_lxor;
                break;

            // Increment local variable by constant
            case Opcode.op_iinc:
                // The index is an unsigned byte that must be an index into the local variable array of the current frame (?.6). 
                // The local variable at index must contain an int. 
                byteValue = pdis.readUnsignedByte();
                // The const is an immediate signed byte. 
                // The value const is first sign-extended to an int, and then the local variable at index is incremented by that amount.
                byteValue2 = pdis.readByte();
                opcodeText = String.format("%s %d %d        ; index const", Opcode.name_iinc, byteValue, byteValue2);
                break;

            case Opcode.op_i2l:
                opcodeText = Opcode.name_i2l;
                break;

            case Opcode.op_i2f:
                opcodeText = Opcode.name_i2f;
                break;

            case Opcode.op_i2d:
                opcodeText = Opcode.name_i2d;
                break;

            case Opcode.op_l2i:
                opcodeText = Opcode.name_l2i;
                break;

            case Opcode.op_l2f:
                opcodeText = Opcode.name_l2f;
                break;

            case Opcode.op_l2d:
                opcodeText = Opcode.name_l2d;
                break;

            case Opcode.op_f2i:
                opcodeText = Opcode.name_f2i;
                break;

            case Opcode.op_f2l:
                opcodeText = Opcode.name_f2l;
                break;

            case Opcode.op_f2d:
                opcodeText = Opcode.name_f2d;
                break;

            case Opcode.op_d2i:
                opcodeText = Opcode.name_d2i;
                break;

            case Opcode.op_d2l:
                opcodeText = Opcode.name_d2l;
                break;

            case Opcode.op_d2f:
                opcodeText = Opcode.name_d2f;
                break;

            case Opcode.op_i2b:
                opcodeText = Opcode.name_i2b;
                break;

            case Opcode.op_i2c:
                opcodeText = Opcode.name_i2c;
                break;

            case Opcode.op_i2s:
                opcodeText = Opcode.name_i2s;
                break;

            case Opcode.op_lcmp:
                opcodeText = Opcode.name_lcmp;
                break;

            case Opcode.op_fcmpl:
                opcodeText = Opcode.name_fcmpl;
                break;

            case Opcode.op_fcmpg:
                opcodeText = Opcode.name_fcmpg;
                break;

            case Opcode.op_dcmpl:
                opcodeText = Opcode.name_dcmpl;
                break;

            case Opcode.op_dcmpg:
                opcodeText = Opcode.name_dcmpg;
                break;

            // if<cond>: ifeq = 153 (0x99) ifne = 154 (0x9a) iflt = 155 (0x9b) ifge = 156 (0x9c) ifgt = 157 (0x9d) ifle = 158 (0x9e)
            // Branch if int comparison with zero succeeds
            //
            // If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, 
            // where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. 
            // Execution then proceeds at that offset from the address of the opcode of this if<cond> instruction. 
            // The target address must be that of an opcode of an instruction within the method that contains this if<cond> instruction.
            case Opcode.op_ifeq:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifeq, shortValue);
                break;

            case Opcode.op_ifne:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifne, shortValue);
                break;

            case Opcode.op_iflt:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_iflt, shortValue);
                break;

            case Opcode.op_ifge:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifge, shortValue);
                break;

            case Opcode.op_ifgt:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifgt, shortValue);
                break;

            case Opcode.op_ifle:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifle, shortValue);
                break;

            // if_icmp<cond>: if_icmpeq = 159 (0x9f) if_icmpne = 160 (0xa0) if_icmplt = 161 (0xa1) if_icmpge = 162 (0xa2) if_icmpgt = 163 (0xa3) if_icmple = 164 (0xa4)
            // Branch if int comparison succeeds
            case Opcode.op_if_icmpeq:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmpeq, shortValue);
                break;

            case Opcode.op_if_icmpne:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmpne, shortValue);
                break;

            case Opcode.op_if_icmplt:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmplt, shortValue);
                break;

            case Opcode.op_if_icmpge:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmpge, shortValue);
                break;

            case Opcode.op_if_icmpgt:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmpgt, shortValue);
                break;

            case Opcode.op_if_icmple:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_icmple, shortValue);
                break;

            // if_acmp<cond>: if_acmpeq = 165 (0xa5) if_acmpne = 166 (0xa6)
            // Branch if reference comparison succeeds
            // ..., value1, value2 
            // Both value1 and value2 must be of type reference. They are both popped from the operand stack and compared. 
            // 
            // If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, 
            // where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. 
            // Execution then proceeds at that offset from the address of the opcode of this if_acmp<cond> instruction. 
            // The target address must be that of an opcode of an instruction within the method that contains this if_acmp<cond> instruction.
            case Opcode.op_if_acmpeq:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_acmpeq, shortValue);
                break;

            case Opcode.op_if_acmpne:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_if_acmpne, shortValue);
                break;

            // Branch always
            // The unsigned bytes branchbyte1 and branchbyte2 are used to construct a signed 16-bit branchoffset, 
            // where branchoffset is (branchbyte1 << 8) | branchbyte2. 
            // Execution proceeds at that offset from the address of the opcode of this goto instruction. 
            // The target address must be that of an opcode of an instruction within the method that contains this goto instruction.
            case Opcode.op_goto:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_goto, shortValue);
                break;

            // Jump subroutine
            // The address of the opcode of the instruction immediately following this jsr instruction 
            // is pushed onto the operand stack as a value of type returnAddress. 
            // The unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, 
            // where the offset is (branchbyte1 << 8) | branchbyte2. 
            // Execution proceeds at that offset from the address of this jsr instruction. 
            // The target address must be that of an opcode of an instruction within the method that contains this jsr instruction.
            case Opcode.op_jsr:
                shortValue = pdis.readShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_jsr, shortValue);
                break;

            // Return from subroutine
            // The index is an unsigned byte between 0 and 255, inclusive. 
            // The local variable at index in the current frame (?.6) must contain a value of type returnAddress. 
            // The contents of the local variable are written into the Java virtual machine's pc register, and execution continues there.
            case Opcode.op_ret:
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ret, byteValue);
                break;

            // Access jump table by index and jump
            case Opcode.op_tableswitch:
                pdis.skipBytes(3);
                opcodeText = Opcode.getOpcodeTableSwitchText(pdis, 8);
                break;

            // Access jump table by key match and jump
            case Opcode.op_lookupswitch:
                pdis.skipBytes(3);
                opcodeText = Opcode.getOpcodeLookupSwitchText(pdis, 8);
                break;

            case Opcode.op_ireturn:
                opcodeText = Opcode.name_ireturn;
                break;

            case Opcode.op_lreturn:
                opcodeText = Opcode.name_lreturn;
                break;

            case Opcode.op_freturn:
                opcodeText = Opcode.name_freturn;
                break;

            case Opcode.op_dreturn:
                opcodeText = Opcode.name_dreturn;
                break;

            case Opcode.op_areturn:
                opcodeText = Opcode.name_areturn;
                break;

            case Opcode.op_return:
                opcodeText = Opcode.name_return;
                break;

            // Get static field from class
            case Opcode.op_getstatic:
                // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6), 
                // where the value of the index is (indexbyte1 << 8) | indexbyte2. 
                // The runtime constant pool item at that index must be a symbolic reference to a field (?.1), 
                // which gives the name and descriptor of the field as well as a symbolic reference to the class or interface 
                // in which the field is to be found. 
                // The referenced field is resolved (?.4.3.2). 
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_getstatic, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Set static field in class
            case Opcode.op_putstatic:
                // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6), 
                // where the value of the index is (indexbyte1 << 8) | indexbyte2. 
                // The runtime constant pool item at that index must be a symbolic reference to a field (?.1), 
                // which gives the name and descriptor of the field as well as a symbolic reference to the class or interface 
                // in which the field is to be found. The referenced field is resolved (?.4.3.2).
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_putstatic, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Fetch field from object
            case Opcode.op_getfield:
                // The objectref, which must be of type reference, is popped from the operand stack. 
                // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6), 
                // where the value of the index is (indexbyte1 << 8) | indexbyte2. 
                // The runtime constant pool item at that index must be a symbolic reference to a field (?.1), 
                // which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found.
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_getfield, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Set field in object
            case Opcode.op_putfield:
                // The unsigned indexbyte1 and indexbyte2 are used to construct an index into the runtime constant pool of the current class (?.6), 
                // where the value of the index is (indexbyte1 << 8) | indexbyte2. 
                // The runtime constant pool item at that index must be a symbolic reference to a field (?.1), 
                // which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found.
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_putfield, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Invoke instance method; dispatch based on class
            case Opcode.op_invokevirtual:
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
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_invokevirtual, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Invoke instance method; special handling for superclass, private, and instance initialization method invocations
            case Opcode.op_invokespecial:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_invokespecial, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Invoke a class (static) method
            case Opcode.op_invokestatic:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_invokestatic, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Invoke interface method
            case Opcode.op_invokeinterface:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                byteValue = pdis.readUnsignedByte();
                pdis.skipBytes(1);
                opcodeText = String.format("%s interface=%d [%s], nargs=%d",
                        Opcode.name_invokeinterface,
                        shortValue,
                        classFile.getCPDescription(shortValue),
                        byteValue);
                break;

            // Create new object
            case Opcode.op_new:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_new, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Create new array
            case Opcode.op_newarray:
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format("%s %s", Opcode.name_newarray, getOpcodeNewarrayAtype(byteValue));
                break;

            // Create new array of reference
            case Opcode.op_anewarray:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_anewarray, shortValue, classFile.getCPDescription(shortValue));
                break;

            case Opcode.op_arraylength:
                opcodeText = Opcode.name_arraylength;
                break;

            case Opcode.op_athrow:
                opcodeText = Opcode.name_athrow;
                break;

            // Check whether object is of given type
            case Opcode.op_checkcast:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_checkcast, shortValue, classFile.getCPDescription(shortValue));
                break;

            // Determine if object is of given type
            case Opcode.op_instanceof:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                opcodeText = String.format(FORMAT_SDS, Opcode.name_instanceof, shortValue, classFile.getCPDescription(shortValue));
                break;

            case Opcode.op_monitorenter:
                opcodeText = Opcode.name_monitorenter;
                break;

            case Opcode.op_monitorexit:
                opcodeText = Opcode.name_monitorexit;
                break;

            // Extend local variable index by additional bytes
            case Opcode.op_wide:
                opcodeText = Opcode.getOpcodeWideText(pdis);
                break;

            // Create new multidimensional array
            case Opcode.op_multianewarray:
                shortValue = pdis.readUnsignedShort();
                parseResult.CPIndex1 = shortValue;
                byteValue = pdis.readUnsignedByte();
                opcodeText = String.format("%s index=%d [%s] dimensions=%d",
                        Opcode.name_instanceof,
                        shortValue,
                        classFile.getCPDescription(shortValue),
                        byteValue);
                break;

            // Branch if reference is null
            case Opcode.op_ifnull:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifnull, shortValue);
                break;

            // Branch if reference not null
            case Opcode.op_ifnonnull:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_ifnonnull, shortValue);
                break;

            // Branch always (wide index)
            case Opcode.op_goto_w:
                intValue = pdis.readInt();
                opcodeText = String.format(FORMAT_SD, Opcode.name_goto_w, intValue);
                break;

            // Jump subroutine (wide index)
            case Opcode.op_jsr_w:
                // The unsigned branchbyte1, branchbyte2, branchbyte3, and branchbyte4 are used to 
                // construct a signed 32-bit offset, where the offset is 
                // (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4. 
                intValue = pdis.readInt();
                opcodeText = String.format(FORMAT_SD, Opcode.name_jsr_w, intValue);
                break;

            // Reserved opcodes
            case Opcode.op_breakpoint:
                opcodeText = "[Reserved] - " + Opcode.name_breakpoint;
                break;

            case Opcode.op_impdep1:
                opcodeText = "[Reserved] - " + Opcode.name_impdep1;
                break;

            case Opcode.op_impdep2:
                opcodeText = "[Reserved] - " + Opcode.name_impdep2;
                break;

            default:
                opcodeText = "[Unknown opcode]";
        }

        parseResult.OpcodeText = String.format("opcode [%02X] - %04d: %s", opcode, curPos, opcodeText);
        return parseResult;
    }

    /**
     * Used for opcode newarray only.
     *
     * @see
     * <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.newarray">
     * VM Spec: Opcode <code>newarray</code>
     * </a>
     *
     * @param type Array type codes
     * @return Array Type
     */
    private static String getOpcodeNewarrayAtype(final int type) {
        String value;
        switch (type) {
            case 4:
                value = "T_BOOLEAN";
                break;
            case 5:
                value = "T_CHAR";
                break;
            case 6:
                value = "T_FLOAT";
                break;
            case 7:
                value = "T_DOUBLE";
                break;
            case 8:
                value = "T_BYTE";
                break;
            case 9:
                value = "T_SHORT";
                break;
            case 10:
                value = "T_INT";
                break;
            case 11:
                value = "T_LONG";
                break;
            default:
                value = "[ERROR: Unknown type]";
                break;
        }

        return value;
    }

    private static String getOpcodeLookupSwitchText(final PosDataInputStream pdis, final int spaceLength)
            throws IOException {
        int i;
        String space;
        if (spaceLength < 1) {
            space = "";
        } else {
            final StringBuilder sbSpace = new StringBuilder(spaceLength);
            for (i = 0; i < spaceLength; i++) {
                sbSpace.appendCodePoint(' ');
            }
            space = sbSpace.toString();
        }

        final int defaultJump = pdis.readInt();

        final StringBuilder sb = new StringBuilder(200);
        sb.append(Opcode.name_lookupswitch);
        sb.append(String.format(": default=%d", defaultJump));

        final int pairCount = pdis.readInt();
        int caseValue = 0;
        int offsetValue = 0;
        for (i = 0; i < pairCount; i++) {
            caseValue = pdis.readInt();
            offsetValue = pdis.readInt();

            sb.append(String.format("\n%scase %d: %d", space, caseValue, offsetValue));
        }

        return sb.toString();
    }

    private static String getOpcodeTableSwitchText(final PosDataInputStream pdis, final int spaceLength)
            throws IOException {
        int i;
        String space;
        if (spaceLength < 1) {
            space = "";
        } else {
            final StringBuilder sbSpace = new StringBuilder(spaceLength);
            for (i = 0; i < spaceLength; i++) {
                sbSpace.appendCodePoint(' ');
            }
            space = sbSpace.toString();
        }

        final int defaultJump = pdis.readInt();
        final int valueLow = pdis.readInt();
        final int valueHigh = pdis.readInt();
        final int tableLength = valueHigh - valueLow + 1;
        int offsetValue;

        final StringBuilder sb = new StringBuilder(200);
        sb.append(Opcode.name_tableswitch);
        sb.append(String.format(" %d to %d: default=%d", valueLow, valueHigh, defaultJump));
        for (i = 0; i < tableLength; i++) {
            offsetValue = pdis.readInt();
            sb.append(String.format("\n%s%d", space, offsetValue));
        }

        return sb.toString();
    }

    private static String getOpcodeWideText(final PosDataInputStream pdis)
            throws IOException {
        final int opcode = pdis.readUnsignedByte();
        String opcodeText = null;

        int shortValue;
        int shortValue2;

        switch (opcode) {
            case Opcode.op_iload:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_iload, shortValue);
                break;

            case Opcode.op_lload:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_lload, shortValue);
                break;

            case Opcode.op_fload:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_fload, shortValue);
                break;

            case Opcode.op_dload:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_dload, shortValue);
                break;

            case Opcode.op_aload:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_aload, shortValue);
                break;

            case Opcode.op_istore:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_istore, shortValue);
                break;

            case Opcode.op_lstore:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_lstore, shortValue);
                break;

            case Opcode.op_fstore:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_fstore, shortValue);
                break;

            case Opcode.op_dstore:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_dstore, shortValue);
                break;

            case Opcode.op_astore:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_astore, shortValue);
                break;

            case Opcode.op_iinc:
                shortValue = pdis.readUnsignedShort();
                shortValue2 = pdis.readUnsignedShort();
                opcodeText = String.format("%s %d %d", Opcode.name_wide_iinc, shortValue, shortValue2);
                break;

            case Opcode.op_ret:
                shortValue = pdis.readUnsignedShort();
                opcodeText = String.format(FORMAT_SD, Opcode.name_wide_ret, shortValue);
                break;

            default:
                opcodeText = String.format("%s [Unknown opcode]", Opcode.name_wide);
        }

        return opcodeText;
    }

    public static class CodeResult {

        public String OpcodeTexts;
        public List<Integer> CPIndexes = new ArrayList<Integer>();
    }

    private static class OpcodeResult {

        String OpcodeText;
        int CPIndex1 = -1;
    }

}
