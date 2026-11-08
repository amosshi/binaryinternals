/*
 * CentralDirectoryStructure.java    May 08, 2011, 09:40
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.BytesTool;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// File header of central directory structure.
///
/// @author Amos Shi
public final class CentralDirectoryStructure extends FileComponent implements GenerateTreeNode {

    public final FileHeader header;

    CentralDirectoryStructure(final PosDataInputStream stream) throws IOException {
        this.startPos = stream.getPos();
        this.header = new FileHeader(stream);
        this.length = this.header.calcLength();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        int position = this.getStartPos();

        final DefaultMutableTreeNode nodeCDS = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.getLength(),
                "Central Directory Structure: " + this.header.FileNameValue
        ));
        parent.add(nodeCDS);

        final DefaultMutableTreeNode headerNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
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
        position += 4;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "version made by = %d".formatted(this.header.VersionMadeBy),
                Icons.Versions
        )));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "version needed to extract = %d".formatted(this.header.VersionNeededToExtract),
                Icons.Versions
        )));
        position += 2;
        LocalFileHeader.generateTreeNodeGeneralPurposeBitFlag(headerNode, position, this.header.GeneralPurposeBitFlag);
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                Compression.toText(this.header.CompressionMethod))));

        position += 2;
        final DefaultMutableTreeNode nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "last mod file time = %s".formatted(this.header.LastModFileTimeValue),
                Icons.Time
        ));
        headerNode.add(nodeTime);
        this.header.LastModFileTimeValue.generateTreeNode(nodeTime);

        position += 2;
        final DefaultMutableTreeNode nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "last mod file date = %s".formatted(this.header.LastModFileDateValue),
                Icons.Calendar
        ));
        headerNode.add(nodeDate);
        this.header.LastModFileDateValue.generateTreeNode(nodeDate);

        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "crc-32: " + BytesTool.getByteDataHexView(this.header.CRC32),
                Icons.Checksum
        )));
        position += 4;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "compressed size = %s".formatted(zip64Text(this.header.CompressedSize, this.header.CompressedSizeValue)),
                Icons.Size
        )));
        position += 4;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "uncompressed size = %s".formatted(zip64Text(this.header.UncompressedSize, this.header.UncompressedSizeValue)),
                Icons.Size
        )));
        position += 4;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "file name length = %d".formatted(this.header.FileNameLength),
                Icons.Length
        )));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "extra field length = %d".formatted(this.header.ExtraFieldLength),
                Icons.Length
        )));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "file comment length = %d".formatted(this.header.FileCommentLength),
                Icons.Length
        )));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "disk number start = %d".formatted(this.header.DiskNumberStart))));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "internal file attributes = %02X %02X".formatted(
                        this.header.InternalFileAttributes[0],
                        this.header.InternalFileAttributes[1]),
                Icons.Annotations
        )));
        position += 2;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "external file attributes = %02X %02X %02X %02X".formatted(
                        this.header.ExternalFileAttributes[0],
                        this.header.ExternalFileAttributes[1],
                        this.header.ExternalFileAttributes[2],
                        this.header.ExternalFileAttributes[3]),
                Icons.Annotations
        )));
        position += 4;
        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                (this.header.RelativeOffsetOfLocalHeader == FileHeader.ZIP64_MAGIC)
                ? "relative offset of local header = 0xFFFFFFFF (Zip64: 0x%08X)".formatted(this.header.RelativeOffsetOfLocalHeaderValue)
                : "relative offset of local header = 0x%08X".formatted(this.header.RelativeOffsetOfLocalHeader),
                Icons.Offset
        )));
        position += 4;
        if (this.header.FileName != null) {
            headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.header.FileName.length,
                    "file name = %s".formatted(this.header.FileNameValue),
                    Icons.Name
            )));
            position += this.header.FileName.length;
        }
        if (this.header.ExtraField != null) {
            LocalFileHeader.generateTreeNodeExtraField(headerNode, position, this.header.ExtraField.length, this.header.ExtraFieldBlocks);
            position += this.header.ExtraField.length;
        }
        if (this.header.FileComment != null) {
            headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.header.FileComment.length,
                    "file comment = %s".formatted(LocalFileHeader.decodeText(this.header.FileComment)),
                    Icons.Annotations)));
        }
    }

    /// Text of a size field, with the Zip64 value when the field is
    /// [FileHeader#ZIP64_MAGIC].
    private static String zip64Text(final long value, final long zip64Value) {
        return (value == FileHeader.ZIP64_MAGIC)
                ? "0xFFFFFFFF (Zip64: %d)".formatted(zip64Value)
                : Long.toString(value);
    }

    /// File header of central directory structure.
    ///
    /// ```
    /// central file header signature   4 bytes  (0x02014b50)
    /// version made by                 2 bytes
    /// version needed to extract       2 bytes
    /// general purpose bit flag        2 bytes
    /// compression method              2 bytes
    /// last mod file time              2 bytes
    /// last mod file date              2 bytes
    /// crc-32                          4 bytes
    /// compressed size                 4 bytes
    /// uncompressed size               4 bytes
    /// file name length                2 bytes
    /// extra field length              2 bytes
    /// file comment length             2 bytes
    /// disk number start               2 bytes
    /// internal file attributes        2 bytes
    /// external file attributes        4 bytes
    /// relative offset of local header 4 bytes
    ///
    /// file name (variable size)
    /// extra field (variable size)
    /// file comment (variable size)
    /// ```
    public static final class FileHeader {

        /// Size of the fixed part of the header.
        public static final int MIN_LENGTH = 46;
        /// A 4-byte field with this value has its actual value in the Zip64
        /// extended information extra field.
        public static final long ZIP64_MAGIC = 0xFFFFFFFFL;

        public final byte[] Signature = new byte[4];
        public final int VersionMadeBy;
        public final int VersionNeededToExtract;
        public final byte[] GeneralPurposeBitFlag = new byte[2];
        /// Compression method, see [Compression].
        public final int CompressionMethod;
        public final int LastModFileTime;
        /// Parsed value of [#LastModFileTime].
        public final MSDosTime LastModFileTimeValue;
        public final int LastModFileDate;
        /// Parsed value of [#LastModFileDate].
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
        /// Contains the file name or `null` if [#FileNameLength] is zero.
        public final byte[] FileName;
        /// Parsed value of [#FileName].
        public final String FileNameValue;
        /// Contains extra fields or `null` if [#ExtraFieldLength] is zero.
        public final byte[] ExtraField;
        /// Parsed value of [#ExtraField].
        public final List<ExtraFieldBlock> ExtraFieldBlocks;
        /// Contains comment of the file or `null` if [#FileCommentLength] is
        /// zero.
        public final byte[] FileComment;
        /// [#UncompressedSize], or the Zip64 value when it is [#ZIP64_MAGIC].
        public final long UncompressedSizeValue;
        /// [#CompressedSize], or the Zip64 value when it is [#ZIP64_MAGIC].
        public final long CompressedSizeValue;
        /// [#RelativeOffsetOfLocalHeader], or the Zip64 value when it is
        /// [#ZIP64_MAGIC].
        public final long RelativeOffsetOfLocalHeaderValue;

        FileHeader(final PosDataInputStream stream) throws IOException {
            final int startPos = stream.getPos();
            stream.readFully(this.Signature);
            if (!Arrays.equals(this.Signature, ZIPFile.CENTRAL_FILE_HEADER)) {
                throw new FileFormatException(LocalFileHeader.signatureMismatch("central file header signature", startPos, this.Signature, ZIPFile.CENTRAL_FILE_HEADER));
            }

            this.VersionMadeBy = stream.readUnsignedShortInLittleEndian();
            this.VersionNeededToExtract = stream.readUnsignedShortInLittleEndian();
            stream.readFully(this.GeneralPurposeBitFlag);
            this.CompressionMethod = stream.readUnsignedShortInLittleEndian();

            final int lastModFileTimeStartPos = stream.getPos();
            this.LastModFileTime = stream.readUnsignedShortInLittleEndian();
            this.LastModFileTimeValue = new MSDosTime(this.LastModFileTime, lastModFileTimeStartPos, PosDataInputStream.USHORT_LENGTH);

            final int lastModFileDateStartPos = stream.getPos();
            this.LastModFileDate = stream.readUnsignedShortInLittleEndian();
            this.LastModFileDateValue = new MSDosDate(this.LastModFileDate, lastModFileDateStartPos, PosDataInputStream.USHORT_LENGTH);

            stream.readFully(this.CRC32);
            this.CompressedSize = stream.readUnsignedIntInLittleEndian();
            this.UncompressedSize = stream.readUnsignedIntInLittleEndian();
            this.FileNameLength = stream.readUnsignedShortInLittleEndian();
            this.ExtraFieldLength = stream.readUnsignedShortInLittleEndian();
            this.FileCommentLength = stream.readUnsignedShortInLittleEndian();
            this.DiskNumberStart = stream.readUnsignedShortInLittleEndian();
            stream.readFully(this.InternalFileAttributes);
            stream.readFully(this.ExternalFileAttributes);
            this.RelativeOffsetOfLocalHeader = stream.readUnsignedIntInLittleEndian();

            this.FileName = LocalFileHeader.readOptional(stream, this.FileNameLength);
            this.FileNameValue = LocalFileHeader.decodeText(this.FileName);
            final int extraFieldStartPos = stream.getPos();
            this.ExtraField = LocalFileHeader.readOptional(stream, this.ExtraFieldLength);
            this.ExtraFieldBlocks = ExtraFieldBlock.parse(this.ExtraField, extraFieldStartPos);
            this.FileComment = LocalFileHeader.readOptional(stream, this.FileCommentLength);

            // The Zip64 values only exist for the fields set to ZIP64_MAGIC, in this order
            final ByteBuffer zip64 = ExtraFieldBlock.zip64ExtendedInfo(this.ExtraField);
            this.UncompressedSizeValue = zip64Value(zip64, this.UncompressedSize);
            this.CompressedSizeValue = zip64Value(zip64, this.CompressedSize);
            this.RelativeOffsetOfLocalHeaderValue = zip64Value(zip64, this.RelativeOffsetOfLocalHeader);
        }

        private static long zip64Value(final ByteBuffer zip64, final long value) {
            return (value == ZIP64_MAGIC && zip64.remaining() >= Long.BYTES) ? zip64.getLong() : value;
        }

        public int calcLength() {
            return MIN_LENGTH + this.FileNameLength + this.ExtraFieldLength + this.FileCommentLength;
        }

        /// Get the corresponding bit value of `position`.
        ///
        /// @param position Bit position, from 0 to 15
        /// @return The bit value, 0 or 1
        ///
        /// @see #GeneralPurposeBitFlag
        /// @see LocalFileHeader#getGeneralPurposeBitFlagBitValue(int)
        public int getGeneralPurposeBitFlagBitValue(final int position) {
            return LocalFileHeader.parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
        }
    }
}
