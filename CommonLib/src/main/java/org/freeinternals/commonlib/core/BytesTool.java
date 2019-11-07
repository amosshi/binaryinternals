/*
 * Tool.java    August 21, 2010, 23:07 AM
 *
 * Copyright 2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility Class for bytes array (binary data).
 *
 * @author Amos Shi
 */
public final class BytesTool {

    public static boolean isByteArrayEmpty(final byte[] buff, final int startPos, final int length) {
        boolean result = false;

        if (buff[startPos] == 0x00 || buff[startPos] == ((byte) 0xFF)) {
            result = true;
            for (int i = 1; i <= length; i++) {
                if (buff[startPos + i] != buff[startPos]) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Compares if the contents of two byte array are the same.
     * <p>
     * When either <code>bin1</code> or <code>bin2</code> is <code>null</code>,
     * <code>false</code> will be returned. When either <code>bin1</code> or
     * <code>bin2</code> is empty, <code>false</code> will be returned.
     * </p>
     *
     * @param bin1 The first byte array
     * @param bin2 The second byte array
     * @return  <code>true</code> if the content are the same, else false
     */
    public static boolean isByteArraySame(final byte[] bin1, final byte[] bin2) {
        if (bin1 == null || bin2 == null) {
            return false;
        }
        if (bin1.length == 0 || bin2.length == 0) {
            return false;
        }
        if (bin1.length != bin2.length) {
            return false;
        }

        boolean same = true;
        for (int i = 0; i < bin1.length; i++) {
            if (bin1[i] != bin2[i]) {
                same = false;
                break;
            }
        }

        return same;
    }

    /**
     * Checks if the byte array <code>bigBin</code> starts from
     * <code>start</code> is the same as <code>sampleBin</code>.
     *
     * @param bin1 The first byte array
     * @param bin2 The second byte array
     * @param start The start position for compare
     * @return  <code>true</code> if the content are the same, else false
     */
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

        boolean same = true;
        for (int i = 0; i < bin1.length; i++) {
            if (bin1[i] != bin2[start + i]) {
                same = false;
                break;
            }
        }

        return same;
    }

    /**
     * Get a string for the {@code hex} view of byte array {@code data}.
     *
     * @param data Byte array
     * @return A string representing the {@code hex} version of {@code data}
     */
    public static String getByteDataHexView(final byte[] data) {
        if (data == null) {
            return "";
        }
        if (data.length < 1) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(data.length * 5);
        final int length = data.length;
        int i;
        int lineBreakCounter = 0;
        for (i = 0; i < length; i++) {
            sb.append(String.format(" %02X", data[i]));
            lineBreakCounter++;
            if (lineBreakCounter == 16) {
                sb.append('\n');
                lineBreakCounter = 0;
            }
        }
        sb.append('\n');

        return sb.toString();
    }

    /**
     * Returns byte array from the {@code file}
     *
     * @param file The file
     * @return Byte array of the {@code file}, or {@code null} if error
     * happened.
     */
    public static byte[] readFileAsBytes(final File file) {
        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(BytesTool.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fileBytes;
    }

    /**
     * Read byte array from {@code zipFile} of entry {@code zipEntry}
     *
     * @param zipFile The {@code jar} or {@code zip} file
     * @param zipEntry The entry to be read
     * @return Byte array of the class file, or {@code null} if error happened.
     * @throws java.io.IOException Error happened when reading the zip file
     */
    public static byte[] readZipEntryAsBytes(final ZipFile zipFile, final ZipEntry zipEntry) throws IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("Parameter 'zipFile' is null.");
        }
        if (zipEntry == null) {
            throw new IllegalArgumentException("Parameter 'zipEntry' is null.");
        }

        final long fileSize = zipEntry.getSize();
        final byte contents[] = new byte[(int) fileSize];
        ByteBuffer byteBuf = ByteBuffer.allocate(contents.length);
        InputStream is = null;
        int bytesRead = 0;
        int bytesAll = 0;

        try {
            is = zipFile.getInputStream(zipEntry);
            while (true) {
                bytesRead = is.read(contents);
                if (bytesRead != -1) {
                    byteBuf.put(contents, 0, bytesRead);
                    bytesAll += bytesRead;
                } else {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BytesTool.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        if (bytesAll == fileSize) {
            return byteBuf.array();
        } else {
            throw new IOException(String.format(
                    "File read error: expected = %d bytes, result = %d bytes. zipFile = %s, zipEntry = %s",
                    fileSize,
                    byteBuf.array().length,
                    zipFile.getName(),
                    zipEntry.getName()));
        }
    }

}
