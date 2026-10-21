package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.binaryinternals.commonlib.FileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SignatureConvertorTest {

    @Test
    void parsePackageKeepsCase() {
        assertEquals("com.Acme.util", SignatureConvertor.parsePackage("com/Acme/util/Foo"));
        assertNull(SignatureConvertor.parsePackage("Foo"));
    }

    @Test
    void methodParameters() throws FileFormatException {
        assertEquals("()", SignatureConvertor.methodParameters2Readable("()V"));
        assertEquals("(int,java.lang.String,long[][],java.util.Map$Entry[])",
                SignatureConvertor.methodParameters2Readable("(ILjava/lang/String;[[J[Ljava/util/Map$Entry;)V"));
    }

    @Test
    void returnType() throws FileFormatException {
        assertEquals("void", SignatureConvertor.methodReturnTypeExtractor("()V").toString());
        assertEquals("java.lang.Object[]", SignatureConvertor.methodReturnTypeExtractor("(I)[Ljava/lang/Object;").toString());
        assertEquals("boolean", SignatureConvertor.methodReturnTypeExtractor("()Z").toString());
    }

    @Test
    void fieldDescriptor() throws FileFormatException {
        final SignatureConvertor.SignatureResult r = SignatureConvertor.fieldDescriptorExtractor("[[Ljava/lang/String;");
        assertEquals(2, r.ArrayDimension);
        assertEquals("java/lang/String", r.TypeBinaryName);
        assertEquals("java.lang.String", r.TypeJLSName);
        assertEquals("java.lang", r.TypePackage);
        assertEquals("java.lang.String[][]", r.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "(", "()", "(I", "I)V", "(Q)V", "Q", "L;", "[", "(Ljava/lang/String)V"})
    @Timeout(5)
    void invalidMethodDescriptor(final String descriptor) {
        assertThrows(FileFormatException.class, () -> SignatureConvertor.methodParameters2Readable(descriptor));
        assertThrows(FileFormatException.class, () -> SignatureConvertor.methodReturnTypeExtractor(descriptor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Q", "L;", "Ljava/lang/String", "[", "(I)V"})
    void invalidFieldDescriptor(final String descriptor) {
        assertThrows(FileFormatException.class, () -> SignatureConvertor.fieldDescriptorExtractor(descriptor));
    }
}
