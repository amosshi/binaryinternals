package org.binaryinternals.format.dex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.format.dex.header_item.Endian;
import org.junit.jupiter.api.Test;

/// The `float` and `double` values of an `encoded_value`: the high-order
/// bytes of the IEEE754 value, zero-extended to the right.
class PosDataInputStreamDexTest {

    /// Usual DEX files: the endian tag bytes `78 56 34 12`, read little-endian.
    private static PosDataInputStreamDex littleEndian(final int... bytes) {
        return stream(Endian.REVERSE_ENDIAN_CONSTANT, bytes);
    }

    private static PosDataInputStreamDex stream(final Endian endian, final int... bytes) {
        final byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            buf[i] = (byte) bytes[i];
        }
        return new PosDataInputStreamDex(new PosByteArrayInputStream(buf), endian);
    }

    @Test
    void floatWithTrailingZeroBytesDropped() throws IOException {
        assertEquals(1.0f, littleEndian(0x80, 0x3F).readFloat(2));      // 0x3F800000
        assertEquals(-0.5f, littleEndian(0xBF).readFloat(1));           // 0xBF000000
    }

    @Test
    void floatWithAllBytes() throws IOException {
        assertEquals(Float.MIN_VALUE, littleEndian(0x01, 0x00, 0x00, 0x00).readFloat(4));  // 0x00000001
        assertEquals(3.1415927f, littleEndian(0xDB, 0x0F, 0x49, 0x40).readFloat(4));      // 0x40490FDB
    }

    @Test
    void doubleWithTrailingZeroBytesDropped() throws IOException {
        assertEquals(2.5, littleEndian(0x04, 0x40).readDouble(2));      // 0x4004000000000000
    }

    @Test
    void doubleWithAllBytes() throws IOException {
        final long bits = Double.doubleToLongBits(Math.PI);
        final int[] bytes = new int[Double.BYTES];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (int) (bits >>> (Byte.SIZE * i)) & 0xFF;
        }
        assertEquals(Math.PI, littleEndian(bytes).readDouble(Double.BYTES));
    }

    @Test
    void bigEndianFile() throws IOException {
        assertEquals(1.0f, stream(Endian.ENDIAN_CONSTANT, 0x3F, 0x80).readFloat(2));
        assertEquals(2.5, stream(Endian.ENDIAN_CONSTANT, 0x40, 0x04).readDouble(2));
    }

    @Test
    void invalidLength() {
        assertThrows(FileFormatException.class, () -> littleEndian(0, 0, 0, 0, 0).readFloat(5));
        assertThrows(FileFormatException.class, () -> littleEndian(0).readDouble(0));
    }
}
