package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class OpcodeTest {

    @Test
    void valueOfMatchesTheFirstInstructionWithTheOpcode() {
        for (int code = -1; code <= 256; code++) {
            final int c = code;
            final Opcode.Instruction expected = Arrays.stream(Opcode.Instruction.values())
                    .filter(i -> i.code == c && i != Opcode.Instruction.UNKNOWN)
                    .findFirst()
                    .orElse(Opcode.Instruction.UNKNOWN);
            assertEquals(expected, Opcode.Instruction.valueOf(code), "opcode " + code);
        }
    }

    @Test
    void newarrayTypeNames() {
        assertEquals("T_BOOLEAN", Opcode.newarrayTypeName(4));
        assertEquals("T_LONG", Opcode.newarrayTypeName(11));
        assertEquals("T_UNKNOWN", Opcode.newarrayTypeName(3));
    }
}
