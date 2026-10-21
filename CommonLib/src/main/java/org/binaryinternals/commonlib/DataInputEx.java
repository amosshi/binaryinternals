/*
 * DataInputEx.java    01:47, Sep 06, 2010
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib;

import java.io.IOException;
import java.math.BigInteger;

/// The `DataInputEx` interface provides for reading bytes from a binary stream
/// as `little-endian` byte order and reconstructing the data into Java
/// primitive types.
///
/// Example about `big-endian` and `little-endian` for `int i = 0x05060708;`
///
/// ```
///                 0  1  2  3
/// big-endian   : 05 06 07 08
/// little-endian: 08 07 06 05
/// ```
///
/// In `Java`, multi-byte data items are always stored in `big-endian` order,
/// where the high bytes come first.
///
/// @author Amos Shi
/// @see <a href="http://en.wikipedia.org/wiki/Endianness"> Wikipedia:
/// Endianness</a>
public interface DataInputEx {

    /// Reads two input bytes and returns a `short` value in the range
    /// `-32768` through `32767`, inclusive. Let `a` be the first byte read and
    /// `b` be the second byte. The value returned is:
    ///
    /// ```
    /// (((b & 0xff) << 8) | (a & 0xff))
    /// ```
    ///
    /// @return the signed 16-bit value read.
    /// @throws IOException I/O Error
    short readShortInLittleEndian() throws IOException;

    /// Reads two input bytes and returns an `int` value in the range `0`
    /// through `65535`. Let `a` be the first byte read and `b` be the second
    /// byte. The value returned is:
    ///
    /// ```
    /// (((b & 0xff) << 8) | (a & 0xff))
    /// ```
    ///
    /// @return the unsigned 16-bit value read.
    /// @throws IOException I/O Error
    int readUnsignedShortInLittleEndian() throws IOException;

    /// Reads four input bytes and returns an `int` value. Let `a-d` be the
    /// first through fourth bytes read. The value returned is:
    ///
    /// ```
    /// (((d & 0xff) << 24) | ((c & 0xff) << 16) |
    ///  ((b & 0xff) << 8) | (a & 0xff))
    /// ```
    ///
    /// @return the `int` value read.
    /// @throws IOException if an I/O error occurs.
    int readIntInLittleEndian() throws IOException;

    /// Reads four input bytes as unsigned integer and returns a `long` value.
    ///
    /// @return the `long` value read.
    /// @throws IOException I/O Error
    long readUnsignedInt() throws IOException;

    /// Reads four input bytes in little-endian as unsigned integer and returns
    /// a `long` value.
    ///
    /// @return the `long` value read.
    /// @throws IOException I/O Error
    long readUnsignedIntInLittleEndian() throws IOException;

    /// Reads eight input bytes and returns a `long` value.
    ///
    /// @return the `long` value read.
    /// @throws IOException I/O Error
    long readLongInLittleEndian() throws IOException;

    /// Reads eight input bytes and returns an `unsigned long` value.
    ///
    /// @return the `unsigned long` value read
    /// @throws IOException I/O Error
    BigInteger readUnsignedLong() throws IOException;

    /// Reads eight input bytes in little-endian and returns an
    /// `unsigned long` value.
    ///
    /// @return the `unsigned long` value read
    /// @throws IOException I/O Error
    BigInteger readUnsignedLongInLittleEndian() throws IOException;

    /// Reads `length` input bytes as an ASCII string.
    ///
    /// @param length Then number of bytes to read
    /// @return the `String` value read.
    /// @throws IOException I/O Error
    String readASCII(int length) throws IOException;

    /// Reads until a null terminator, or the end of the buffer as an ASCII
    /// String.
    ///
    /// @return the `String` value read.
    /// @throws IOException I/O Error
    String readASCII() throws IOException;

    /// Reads until `end`, or the end of the buffer as an ASCII String.
    ///
    /// @param end Ending character
    /// @return the `String` value read.
    /// @throws IOException I/O Error
    String readASCIIUntil(byte end) throws IOException;

    /// Read until a null terminator, or the end of the buffer as binary.
    ///
    /// @return Binary byte array data
    /// @throws IOException I/O Error
    byte[] readBinary() throws IOException;

    /// Skip to the end of the buffer.
    ///
    /// @throws IOException I/O Error
    void skipToEnd() throws IOException;

    /// Go back for `i` bytes.
    ///
    /// The position will be back to `zero` when `i` is bigger than the current
    /// position.
    ///
    /// @param i the positions backwards, specified in byte
    /// @return the new position
    int backward(int i);

    /// Backward from current position until encountering the byte `b` for the
    /// first time.
    ///
    /// For example:
    ///
    /// ```
    ///   byte array index:  0   1   2   3   4   5   6   7   8   9  10  11  12
    ///   byte array data : 50  47  34  6A  1B  0A  0D  20  98  7D  54  20  0D
    ///   current position: *
    /// ```
    ///
    /// When passing into `0D`, the return value is `6`.
    ///
    /// @param b target byte
    /// @return the position, or `-1` if not found until the first byte
    int backwardTo(byte b);

    /// Backward from current position until encountering the byte array `b`
    /// for the first time.
    ///
    /// For example:
    ///
    /// ```
    ///   byte array index:  0   1   2   3   4   5   6   7   8   9  10  11  12
    ///   byte array data : 50  47  34  6A  1B  20  0D  0A  98  7D  54  20  0D
    ///   current position: *
    /// ```
    ///
    /// When passing into `0D 0A`, the return value is `6`.
    ///
    /// @param b target bytes
    /// @return the position, or `-1` if not found until the beginning
    int backwardTo(byte[] b);

    /// Fly to the specified `position`.
    ///
    /// @param position target position
    void flyTo(int position);
}
