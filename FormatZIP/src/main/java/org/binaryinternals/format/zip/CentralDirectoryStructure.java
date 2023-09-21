/**
 * CentralDirectoryStructure.java    May 08, 2011, 09:40
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.zip;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * File header of central directory structure.
 *
 * @author Amos Shi
 */
public class CentralDirectoryStructure extends FileComponent implements GenerateTreeNode {

    public final FileHeader header;

    CentralDirectoryStructure(PosDataInputStream stream) throws IOException, FileFormatException {
        this.startPos = stream.getPos();
        this.header = new FileHeader(stream);
        this.length = this.header.calcLength();
    }

    @Override
    @SuppressWarnings({"java:S1121"})
    public void generateTreeNode(DefaultMutableTreeNode parent) {
        int position = this.getStartPos();

        DefaultMutableTreeNode nodeCDS = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.getLength(),
                "Central Directory Structure: " + this.header.FileNameValue
        ));
        parent.add(nodeCDS);

        DefaultMutableTreeNode headerNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.header.calcLength(),
                "File header"));
        nodeCDS.add(headerNode);

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "central file header signature:" + BytesTool.getByteDataHexView(this.header.Signature),
                Icons.Signature
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("version made by = %d", this.header.VersionMadeBy),
                Icons.Versions
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("version needed to extract = %d", this.header.VersionNeededToExtract),
                Icons.Versions
        )));

        DefaultMutableTreeNode nodeBigFlag = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("general purpose bit flag = %02X %02X",
                        this.header.GeneralPurposeBitFlag[0],
                        this.header.GeneralPurposeBitFlag[1])
        ));
        headerNode.add(nodeBigFlag);
        for (int i = 0; i < 16; i++) {
            nodeBigFlag.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + ((i > 7) ? 1 : 0),
                    1,
                    String.format("Bit %02d = %d", i, this.header.getGeneralPurposeBitFlagBitValue(i)),
                    Icons.Tag
            )));
        }
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("compression method = %d", this.header.CompressionMethod))));

        DefaultMutableTreeNode nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file time = %s", this.header.LastModFileTimeValue.toString()),
                Icons.Time
        ));
        headerNode.add(nodeTime);
        this.header.LastModFileTimeValue.generateTreeNode(nodeTime);

        DefaultMutableTreeNode nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file date = %s", this.header.LastModFileDateValue.toString()),
                Icons.Calendar
        ));
        headerNode.add(nodeDate);
        this.header.LastModFileDateValue.generateTreeNode(nodeDate);

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                "crc-32: " + BytesTool.getByteDataHexView(this.header.CRC32),
                Icons.Checksum
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("compressed size = %d", this.header.CompressedSize),
                Icons.Size
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("uncompressed size = %d", this.header.UncompressedSize),
                Icons.Size
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("file name length = %d", this.header.FileNameLength),
                Icons.Length
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("extra field length = %d", this.header.ExtraFieldLength),
                Icons.Length
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("file comment length = %d", this.header.FileCommentLength),
                Icons.Length
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("disk number start = %d", this.header.DiskNumberStart))));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("internal file attributes = %02X %02X",
                        this.header.InternalFileAttributes[0],
                        this.header.InternalFileAttributes[1]),
                Icons.Annotations
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                String.format("external file attributes = %02X %02X %02X %02X",
                        this.header.ExternalFileAttributes[0],
                        this.header.ExternalFileAttributes[1],
                        this.header.ExternalFileAttributes[2],
                        this.header.ExternalFileAttributes[3]),
                Icons.Annotations
        )));
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("relative offset of local header = 0x%08X", this.header.RelativeOffsetOfLocalHeader),
                Icons.Offset
        )));
        position += 4;
        if (this.header.FileName != null) {
            headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.header.FileName.length,
                    String.format("file name = %s", this.header.FileNameValue),
                    Icons.Name
            )));
            position += this.header.FileName.length;
        }
        if (this.header.ExtraField != null) {
            headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.header.ExtraField.length,
                    "extra field")));
            position += this.header.ExtraField.length;
        }
        if (this.header.FileComment != null) {
            headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.header.FileComment.length,
                    "file comment")));
        }

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
                throw new FileFormatException("Signature does not match for 'central file header signature' at 0x" + Integer.toHexString(stream.getPos()) + ": found =" + BytesTool.getByteDataHexView(this.Signature) + ", expected =" + BytesTool.getByteDataHexView(ZIPFile.CENTRAL_FILE_HEADER));
            }

            this.VersionMadeBy = stream.readUnsignedShortInLittleEndian();
            this.VersionNeededToExtract = stream.readUnsignedShortInLittleEndian();

            readBytes = stream.read(this.GeneralPurposeBitFlag);
            if (readBytes != this.GeneralPurposeBitFlag.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.GeneralPurposeBitFlag.length, readBytes));
            }

            this.CompressionMethod = stream.readUnsignedShortInLittleEndian();

            int lastModFileTimeStartPos = stream.getPos();
            this.LastModFileTime = stream.readUnsignedShortInLittleEndian();
            this.LastModFileTimeValue = new MSDosTime(this.LastModFileTime, lastModFileTimeStartPos, PosDataInputStream.USHORT_LENGTH);

            int lastModFileDateStartPos = stream.getPos();
            this.LastModFileDate = stream.readUnsignedShortInLittleEndian();
            this.LastModFileDateValue = new MSDosDate(this.LastModFileDate, lastModFileDateStartPos, PosDataInputStream.USHORT_LENGTH);

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
         * @return The bit value, 0 or 1
         *
         * @see #GeneralPurposeBitFlag
         * @see LocalFileHeader#getGeneralPurposeBitFlagBitValue(int)
         */
        public int getGeneralPurposeBitFlagBitValue(int position) {
            return LocalFileHeader.parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
        }
    }
}
