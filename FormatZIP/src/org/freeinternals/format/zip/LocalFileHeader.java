/**
 * LocalFileHeader.java    May 10, 2011, 22:16
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

import java.io.IOException;
import java.nio.charset.Charset;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;

/**
 * Local file header.
 * <pre>
 *      local file header signature     4 bytes  (0x04034b50)
 *      version needed to extract       2 bytes
 *      general purpose bit flag        2 bytes
 *      compression method              2 bytes
 *      last mod file time              2 bytes
 *      last mod file date              2 bytes
 *      crc-32                          4 bytes
 *      compressed size                 4 bytes
 *      uncompressed size               4 bytes
 *      file name length                2 bytes
 *      extra field length              2 bytes
 *
 *      file name (variable size)
 *      extra field (variable size)
 * </pre>
 *
 * @author Amos Shi
 */
public class LocalFileHeader extends FileComponent {

    /** Local file header signature. */
    public final byte[] Signature = new byte[4];
    /** Version needed to extract. */
    public final int VersionNeededToExtract;
    /** General purpose bit flag. */
    public final byte[] GeneralPurposeBitFlag = new byte[2];
    /** Compression method. */
    public final int CompressionMethod;
    /** Last mod file time. */
    public final int LastModFileTime;
    /**
     * Parsed value of {@link #LastModFileTime}.
     * 
     * @see #LastModFileTime
     */
    public final MSDosTime LastModFileTimeValue;
    /** Last mod file date. */
    public final int LastModFileDate;
    /**
     * Parsed value of {@link #LastModFileDate}.
     *
     * @see #LastModFileDate
     */
    public final MSDosDate LastModFileDateValue;
    /** CRC-32. */
    public final byte[] CRC32 = new byte[4];
    /** Compressed size. */
    public final long CompressedSize;
    /** Uncompressed size. */
    public final long UncompressedSize;
    /** File name length. */
    public final int FileNameLength;
    /** Extra field length. */
    public final int ExtraFieldLength;
    /** File name. <code>null</code> when {@link #FileNameLength} is 0. */
    public final byte[] FileName;
    /**
     * Parsed value of {@link #FileName}.
     * 
     * @see #FileName
     */
    public final String FileNameValue;
    /** Extra field. <code>null</code> when {@link #ExtraFieldLength} is 0. */
    public final byte[] ExtraField;

    LocalFileHeader(PosDataInputStream stream) throws IOException, FileFormatException {
        this.startPos = stream.getPos();
        stream.read(this.Signature);
        if (Tool.isByteArraySame(this.Signature, ZIPFile.LOCAL_FILE_HEADER) == false) {
            throw new FileFormatException("Signature does not match for 'local file header signature'.");
        }
        this.VersionNeededToExtract = stream.readUnsignedShort_LittleEndian();
        stream.read(this.GeneralPurposeBitFlag);
        this.CompressionMethod = stream.readUnsignedShort_LittleEndian();
        this.LastModFileTime = stream.readUnsignedShort_LittleEndian();
        this.LastModFileTimeValue = new MSDosTime(this.LastModFileTime);
        this.LastModFileDate = stream.readUnsignedShort_LittleEndian();
        this.LastModFileDateValue = new MSDosDate(this.LastModFileDate);
        stream.read(this.CRC32);
        this.CompressedSize = stream.readUnsignedInt_LittleEndian();
        this.UncompressedSize = stream.readUnsignedInt_LittleEndian();
        this.FileNameLength = stream.readUnsignedShort_LittleEndian();
        this.ExtraFieldLength = stream.readUnsignedShort_LittleEndian();
        if (this.FileNameLength > 0) {
            this.FileName = new byte[this.FileNameLength];
            stream.read(this.FileName);
            PosDataInputStream utf8 = new PosDataInputStream(new PosByteArrayInputStream(this.FileName));
            // this.FileNameValue = utf8.readUTF();              // Failed: EOFException
            // this.FileNameValue = new String(this.FileName);   // TODO - This logic is not working for Chinese charactor
            // this.FileNameValue = new String(this.FileName, Charset.forName("ISO-8859-1"));// Not working for WinRAR zip Chinese file name
            this.FileNameValue = new String(this.FileName, Charset.forName("UTF-8")); // Not working for WinRAR zip Chinese file name
            // this.FileNameValue = new String(this.FileName, Charset.forName("UTF-16")); // Not
            // this.FileNameValue = new String(this.FileName, Charset.forName("UTF-16BE")); // Not
            // this.FileNameValue = new String(this.FileName, Charset.forName("UTF-16LE"));  // Not
        } else {
            this.FileName = null;
            this.FileNameValue = "";  // We are not using NULL to make smaller dump
        }
        if (this.ExtraFieldLength > 0) {
            this.ExtraField = new byte[this.ExtraFieldLength];
            stream.read(this.ExtraField);
        } else {
            this.ExtraField = null;
        }
        this.length = 30 + this.FileNameLength + this.ExtraFieldLength;
    }

    /** Get the size of the local file header & the compressed data. */
    public long getSizeWithFileData() {
        return this.length + this.CompressedSize;
    }

    /**
     * Get the corresponding bit value of <code>position</code>.
     * <p>
     * There are two bytes in in the {@link #GeneralPurposeBitFlag} field, the
     * bit position is located as:
     * </p>
     * <pre>
     *   byte       :  [ byte left/0 ]    [ byte right/1]
     *   bit postion:  7 6 5 4 3 2 1 0    F E D C B A 9 8
     * </pre>
     *
     * @param position Bit position, from 1 to 15
     * @return  The bit value, 0 or 1
     */
    public int getGeneralPurposeBitFlagBitValue(int position) {
        return LocalFileHeader.parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
    }

    static int parseGeneralPurposeBitFlag(byte[] buf, int position){
        if (position < 0 || position > 15) {
            throw new IllegalArgumentException(String.format("Invalid postion value %d, [0, 15] is expeced.", position));
        }

        byte byteValue;
        int bitPos;
        if (position >= 0 && position <= 7) {
            byteValue = buf[0];
            bitPos = position;
        } else {
            byteValue = buf[1];
            bitPos = position - 8;
        }

        return (((byte) (((byte) (byteValue << (7 - bitPos))) >> 7)) & 0x0001);
    }
}
