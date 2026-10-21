/*
 * Tool.java    August 21, 2010, 23:07 AM
 *
 * Copyright 2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/// Utility Class for bytes array (binary data).
///
/// @author Amos Shi
public final class BytesTool {

    private BytesTool() {
    }

    /// Get binary string of an unsigned 16-bit (`u2`) value, padded with
    /// leading zero to 16 digits.
    ///
    /// @param i Integer value holding an unsigned 16-bit value
    /// @return  Binary string with leading zero
    public static String getBinaryString(int i) {
        return padZero(Integer.toBinaryString(i), 16);
    }

    /// Get binary string of an unsigned 32-bit (`u4`) value, padded with
    /// leading zero to 32 digits.
    ///
    /// @param l Long value holding an unsigned 32-bit value
    /// @return  Binary string with leading zero
    public static String getBinaryString(long l) {
        return padZero(Long.toBinaryString(l), 32);
    }

    private static String padZero(final String s, final int width) {
        return "0".repeat(Math.max(0, width - s.length())) + s;
    }

    /// Get a string for the `hex` view of byte array `data`.
    ///
    /// @param data Byte array
    /// @return A string representing the `hex` version of `data`
    public static String getByteDataHexView(final byte[] data) {
        if (data == null) {
            return "";
        }
        if (data.length < 1) {
            return "";
        }

        final HexFormat hex = HexFormat.of().withUpperCase();
        final StringBuilder sb = new StringBuilder(data.length * 4);
        final int length = data.length;
        int i;
        int lineBreakCounter = 0;
        for (i = 0; i < length; i++) {
            sb.append(' ').append(hex.toHexDigits(data[i]));
            lineBreakCounter++;
            if (lineBreakCounter == 16) {
                sb.append('\n');
                lineBreakCounter = 0;
            }
        }
        sb.append('\n');

        return sb.toString();
    }

    /// Checks if the `length` bytes of `buff` starting from
    /// `startPos` are all `0x00`, or all `0xFF`.
    ///
    /// @param buff The byte array
    /// @param startPos The start position to check
    /// @param length Number of bytes to check
    /// @return `true` if the bytes are all `0x00` or all `0xFF`
    public static boolean isByteArrayEmpty(final byte[] buff, final int startPos, final int length) {
        if (length < 1) {
            return false;
        }

        final byte first = buff[startPos];
        if (first != 0x00 && first != (byte) 0xFF) {
            return false;
        }
        for (int i = startPos + 1; i < startPos + length; i++) {
            if (buff[i] != first) {
                return false;
            }
        }
        return true;
    }

    /// Checks if the byte array `bigBin` starts from
    /// `start` is the same as `sampleBin`.
    ///
    /// @param bin1 The first byte array
    /// @param bin2 The second byte array
    /// @param start The start position for compare
    /// @return  `true` if the content are the same, else false
    public static boolean isByteArraySame(final byte[] bin1, final byte[] bin2, final int start) {
        if (bin1 == null || bin2 == null) {
            return false;
        }
        if (bin1.length == 0 || bin2.length == 0) {
            return false;
        }
        if (start < 0) {
            return false;
        }
        if (start + bin1.length > bin2.length) {
            return false;
        }

        return Arrays.equals(bin1, 0, bin1.length, bin2, start, start + bin1.length);
    }

    /// Read byte array from `zipFile` of entry `zipEntry`
    ///
    /// @param zipFile The `jar` or `zip` file
    /// @param zipEntry The entry to be read
    /// @return Byte array of the zip entry
    /// @throws IOException Error happened when reading the zip file
    public static byte[] readZipEntryAsBytes(final ZipFile zipFile, final ZipEntry zipEntry) throws IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("Parameter 'zipFile' is null.");
        }
        if (zipEntry == null) {
            throw new IllegalArgumentException("Parameter 'zipEntry' is null.");
        }

        final long fileSize = zipEntry.getSize();
        final byte[] contents;
        try (InputStream is = zipFile.getInputStream(zipEntry)) {
            contents = is.readAllBytes();
        }

        // The size is -1 when unknown, so only verify it when it is recorded
        if (fileSize >= 0 && contents.length != fileSize) {
            throw new IOException(
                    "File read error: expected = %d bytes, result = %d bytes. zipFile = %s, zipEntry = %s".formatted(
                    fileSize,
                    contents.length,
                    zipFile.getName(),
                    zipEntry.getName()));
        }
        return contents;
    }
}
