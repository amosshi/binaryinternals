/**
 * CentralDirectoryStructure.java    May 08, 2011, 09:40
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

import java.io.IOException;
import java.nio.charset.Charset;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 * File header of central directory structure.
 *
 * @author Amos Shi
 */
public class CentralDirectoryStructure extends FileComponent {

    public final FileHeader header;

    CentralDirectoryStructure(PosDataInputStream stream) throws IOException, FileFormatException {
        this.startPos = stream.getPos();
        this.header = new FileHeader(stream);
        this.length = this.header.calcLength();
    }

    /**
     * File header of central directory structure.
     * <pre>
     *  central file header signature   4 bytes  (0x02014b50)
     *  version made by                 2 bytes
     *  version needed to extract       2 bytes
     *  general purpose bit flag        2 bytes
     *  compression method              2 bytes
     *  last mod file time              2 bytes
     *  last mod file date              2 bytes
     *  crc-32                          4 bytes
     *  compressed size                 4 bytes
     *  uncompressed size               4 bytes
     *  file name length                2 bytes
     *  extra field length              2 bytes
     *  file comment length             2 bytes
     *  disk number start               2 bytes
     *  internal file attributes        2 bytes
     *  external file attributes        4 bytes
     *  relative offset of local header 4 bytes
     *
     *  file name (variable size)
     *  extra field (variable size)
     *  file comment (variable size)
     * </pre>
     */
    public static class FileHeader {

        public final byte[] Signature = new byte[4];
        public final int VersionMadeBy;
        public final int VersionNeededToExtract;
        public final byte[] GeneralPurposeBitFlag = new byte[2];
        public final int CompressionMethod;
        public final int LastModFileTime;
        /**
         * Parsed value of {@link #LastModFileTime}.
         *
         * @see #LastModFileTime
         */
        public final MSDosTime LastModFileTimeValue;
        public final int LastModFileDate;
        /**
         * Parsed value of {@link #LastModFileDate}.
         *
         * @see #LastModFileDate
         */
        public final MSDosDate LastModFileDateValue;
        public final byte[] CRC32 = new byte[4];
        public final long CompressedSize;
        public final long UncompressedSize;
        public final int FileNameLength;
        public final int ExtraFieldLength;
        public final int FileCommentLength;
        public final int DiskNumberStart;
        public final byte[] InternalFileAttributes = new byte[2];
        public final byte[] ExternalFileAttributes = new byte[4];
        public final long RelativeOffsetOfLocalHeader;
        /**
         * Contains the file name or <code>null</code> if
         * {@link #FileNameLength} is zero.
         *
         * @see #FileNameLength
         */
        public final byte[] FileName;
        /**
         * Parsed value of {@link #FileName}.
         *
         * @see #FileName
         */
        public final String FileNameValue;
        /**
         * Contains extra fields or <code>null</code> if
         * {@link #ExtraFieldLength} is zero.
         *
         * @see #ExtraFieldLength
         */
        public final byte[] ExtraField;
        /**
         * Contains comment of the file or <code>null</code> if
         * {@link #FileCommentLength} is zero.
         *
         * @see #FileCommentLength
         */
        public final byte[] FileComment;

        FileHeader(PosDataInputStream stream) throws IOException, FileFormatException {
            int readBytes = stream.read(this.Signature);
            if (readBytes != this.Signature.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.Signature.length, readBytes));
            }
            if (BytesTool.isByteArraySame(this.Signature, ZIPFile.CENTRAL_FILE_HEADER) == false) {
                throw new FileFormatException("Signature does not match for 'central file header signature'.");
            }

            this.VersionMadeBy = stream.readUnsignedShortInLittleEndian();
            this.VersionNeededToExtract = stream.readUnsignedShortInLittleEndian();

            readBytes = stream.read(this.GeneralPurposeBitFlag);
            if (readBytes != this.GeneralPurposeBitFlag.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.GeneralPurposeBitFlag.length, readBytes));
            }

            this.CompressionMethod = stream.readUnsignedShortInLittleEndian();
            this.LastModFileTime = stream.readUnsignedShortInLittleEndian();
            this.LastModFileTimeValue = new MSDosTime(this.LastModFileTime);
            this.LastModFileDate = stream.readUnsignedShortInLittleEndian();
            this.LastModFileDateValue = new MSDosDate(this.LastModFileDate);

            readBytes = stream.read(this.CRC32);
            if (readBytes != this.CRC32.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.CRC32.length, readBytes));
            }

            this.CompressedSize = stream.readUnsignedIntInLittleEndian();
            this.UncompressedSize = stream.readUnsignedIntInLittleEndian();
            this.FileNameLength = stream.readUnsignedShortInLittleEndian();
            this.ExtraFieldLength = stream.readUnsignedShortInLittleEndian();
            this.FileCommentLength = stream.readUnsignedShortInLittleEndian();
            this.DiskNumberStart = stream.readUnsignedShortInLittleEndian();

            readBytes = stream.read(this.InternalFileAttributes);
            if (readBytes != this.InternalFileAttributes.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.InternalFileAttributes.length, readBytes));
            }

            readBytes = stream.read(this.ExternalFileAttributes);
            if (readBytes != this.ExternalFileAttributes.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.ExternalFileAttributes.length, readBytes));
            }

            this.RelativeOffsetOfLocalHeader = stream.readUnsignedIntInLittleEndian();

            if (this.FileNameLength > 0) {
                this.FileName = new byte[this.FileNameLength];
                readBytes = stream.read(this.FileName);
                if (readBytes != this.FileName.length) {
                    throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.FileName.length, readBytes));
                }

                this.FileNameValue = new String(this.FileName, Charset.forName("UTF-8")); // Not working for WinRAR zip Chinese file name
            } else {
                this.FileName = null;
                this.FileNameValue = "";  // We are not using NULL to avoid Exception
            }

            if (this.ExtraFieldLength > 0) {
                this.ExtraField = new byte[this.ExtraFieldLength];
                readBytes = stream.read(this.ExtraField);
                if (readBytes != this.ExtraField.length) {
                    throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.ExtraField.length, readBytes));
                }
            } else {
                this.ExtraField = null;
            }

            if (this.FileCommentLength > 0) {
                this.FileComment = new byte[this.FileCommentLength];
                readBytes = stream.read(this.FileComment);
                if (readBytes != this.FileComment.length) {
                    throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.FileComment.length, readBytes));
                }
            } else {
                this.FileComment = null;
            }
        }
        private int length = -1;

        public int calcLength() {
            if (this.length == -1) {
                this.length = 46 + this.FileNameLength + this.ExtraFieldLength + this.FileCommentLength;
            }

            return this.length;
        }

        /**
         * Get the corresponding bit value of <code>position</code>.
         *
         * @param position Bit position, from 1 to 15
         * @return  The bit value, 0 or 1
         *
         * @see #GeneralPurposeBitFlag
         * @see LocalFileHeader#getGeneralPurposeBitFlagBitValue(int)
         */
        public int getGeneralPurposeBitFlagBitValue(int position) {
            return LocalFileHeader.parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
        }
    }
}
