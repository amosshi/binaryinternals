package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.binaryinternals.commonlib.FileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SignatureConvertorTest {

    @Test
    void javaName() {
        assertEquals("java.util.Map$Entry", SignatureConvertor.javaName("java/util/Map$Entry"));
        assertEquals("Foo", SignatureConvertor.javaName("Foo"));
    }

    @Test
    void methodParameters() throws FileFormatException {
        assertEquals("()", SignatureConvertor.methodParameters("()V"));
        assertEquals("(int,java.lang.String,long[][],java.util.Map$Entry[])",
                SignatureConvertor.methodParameters("(ILjava/lang/String;[[J[Ljava/util/Map$Entry;)V"));
    }

    @Test
    void returnType() throws FileFormatException {
        assertEquals("void", SignatureConvertor.methodReturnType("()V"));
        assertEquals("java.lang.Object[]", SignatureConvertor.methodReturnType("(I)[Ljava/lang/Object;"));
        assertEquals("boolean", SignatureConvertor.methodReturnType("()Z"));
    }

    @Test
    void fieldType() throws FileFormatException {
        assertEquals("java.lang.String[][]", SignatureConvertor.fieldType("[[Ljava/lang/String;"));
        assertEquals("int", SignatureConvertor.fieldType("I"));
        assertEquals("java.util.Map$Entry", SignatureConvertor.fieldType("Ljava/util/Map$Entry;"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "(", "()", "(I", "I)V", "(Q)V", "Q", "L;", "[", "(Ljava/lang/String)V"})
    @Timeout(5)
    void invalidMethodDescriptor(final String descriptor) {
        assertThrows(FileFormatException.class, () -> SignatureConvertor.methodParameters(descriptor));
        assertThrows(FileFormatException.class, () -> SignatureConvertor.methodReturnType(descriptor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Q", "L;", "Ljava/lang/String", "[", "(I)V"})
    void invalidFieldDescriptor(final String descriptor) {
        assertThrows(FileFormatException.class, () -> SignatureConvertor.fieldType(descriptor));
    }
}
