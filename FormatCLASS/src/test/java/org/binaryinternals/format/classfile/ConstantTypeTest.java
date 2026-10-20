package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.constant.cp_info;
import org.binaryinternals.format.classfile.constant.cp_info.ConstantType;
import org.junit.jupiter.api.Test;

class ConstantTypeTest {

    @Test
    void everyTagHasAParser() {
        for (ConstantType type : ConstantType.values()) {
            try {
                final cp_info cp = ConstantType.parse(type.tag, stream());
                assertEquals(type.tag, cp.tag.value, type.name());
            } catch (Exception e) {
                // Zero bytes are not valid for every type, but the tag must be recognized
                assertFalse(String.valueOf(e.getMessage()).startsWith("Unrecognizable"), type.name());
            }
        }
    }

    @Test
    void unknownTag() {
        final FileFormatException e = assertThrows(FileFormatException.class, () -> ConstantType.parse(99, stream()));
        assertEquals("Unrecognizable constant pool type found. Constant pool tag: [99]; class file offset: [-1].", e.getMessage());
    }

    private static PosDataInputStream stream() {
        return new PosDataInputStream(new PosByteArrayInputStream(new byte[16]));
    }
}
