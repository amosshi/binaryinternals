/*
 * LocalFileHeader.java    May 10, 2011, 22:16
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.zip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.BytesTool;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Local file header, followed by the file data and the optional data
/// descriptor.
///
/// ```
/// local file header signature     4 bytes  (0x04034b50)
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
///
/// file name (variable size)
/// extra field (variable size)
/// ```
///
/// @author Amos Shi
public final class LocalFileHeader extends FileComponent implements GenerateTreeNode {

    /// Size of the fixed part of the header.
    public static final int MIN_LENGTH = 30;
    /// [#GeneralPurposeBitFlag] bit: `crc-32`, `compressed size` and
    /// `uncompressed size` are zero in the local header, and the values are in
    /// the data descriptor after the file data.
    public static final int FLAG_DATA_DESCRIPTOR = 3;
    /// Name of the [#GeneralPurposeBitFlag] bits, see `APPNOTE.TXT` 4.4.4.
    private static final List<String> FLAG_NAMES = List.of(
            "encrypted file",
            "compression option",
            "compression option",
            "data descriptor",
            "enhanced deflating",
            "compressed patched data",
            "strong encryption",
            "unused",
            "unused",
            "unused",
            "unused",
            "language encoding (UTF-8)",
            "reserved",
            "masked local header values",
            "reserved",
            "reserved");

    /// Local file header signature.
    public final byte[] Signature = new byte[4];
    /// Version needed to extract.
    public final int VersionNeededToExtract;
    /// General purpose bit flag.
    public final byte[] GeneralPurposeBitFlag = new byte[2];
    /// Compression method, see [Compression].
    public final int CompressionMethod;
    /// Last mod file time.
    public final int LastModFileTime;
    /// Parsed value of [#LastModFileTime].
    public final MSDosTime LastModFileTimeValue;
    /// Last mod file date.
    public final int LastModFileDate;
    /// Parsed value of [#LastModFileDate].
    public final MSDosDate LastModFileDateValue;
    /// CRC-32.
    public final byte[] CRC32 = new byte[4];
    /// Compressed size.
    public final long CompressedSize;
    /// Uncompressed size.
    public final long UncompressedSize;
    /// File name length.
    public final int FileNameLength;
    /// Extra field length.
    public final int ExtraFieldLength;
    /// File name. `null` when [#FileNameLength] is 0.
    public final byte[] FileName;
    /// Parsed value of [#FileName].
    public final String FileNameValue;
    /// Extra field. `null` when [#ExtraFieldLength] is 0.
    public final byte[] ExtraField;
    /// Parsed value of [#ExtraField].
    public final List<ExtraFieldBlock> ExtraFieldBlocks;
    /// Length of the file data following the header.
    ///
    /// Taken from the central directory, since [#CompressedSize] is zero
    /// when [#FLAG_DATA_DESCRIPTOR] is set, or `0xFFFFFFFF` for Zip64.
    public final int FileDataLength;
    /// The data descriptor after the file data, if any.
    private final DataDescriptor dataDescriptor;

    /// Read a local file header.
    ///
    /// @param stream Stream positioned at the local file header
    /// @param central The central directory file header of the same entry
    LocalFileHeader(final PosDataInputStream stream, final CentralDirectoryStructure.FileHeader central) throws IOException {
        this.startPos = stream.getPos();

        stream.readFully(this.Signature);
        if (!Arrays.equals(this.Signature, ZIPFile.LOCAL_FILE_HEADER)) {
            throw new FileFormatException(signatureMismatch("local file header signature", this.startPos, this.Signature, ZIPFile.LOCAL_FILE_HEADER));
        }

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

        this.FileName = readOptional(stream, this.FileNameLength);
        this.FileNameValue = decodeText(this.FileName);
        final int extraFieldStartPos = stream.getPos();
        this.ExtraField = readOptional(stream, this.ExtraFieldLength);
        this.ExtraFieldBlocks = ExtraFieldBlock.parse(this.ExtraField, extraFieldStartPos);

        this.length = MIN_LENGTH + this.FileNameLength + this.ExtraFieldLength;

        final int dataStartPos = this.startPos + this.length;
        this.FileDataLength = Math.clamp(central.CompressedSizeValue, 0, stream.getBuf().length - dataStartPos);
        this.dataDescriptor = (this.getGeneralPurposeBitFlagBitValue(FLAG_DATA_DESCRIPTOR) == 1)
                ? DataDescriptor.read(stream, dataStartPos + this.FileDataLength, ExtraFieldBlock.zip64ExtendedInfo(this.ExtraField).hasRemaining())
                : null;
    }

    /// The data descriptor after the file data, which exists when
    /// [#FLAG_DATA_DESCRIPTOR] is set.
    ///
    /// @return The data descriptor, or empty if there is none
    public Optional<DataDescriptor> getDataDescriptor() {
        return Optional.ofNullable(this.dataDescriptor);
    }

    /// Position after the file data and the data descriptor.
    ///
    /// @return The end position, exclusive
    public int getEndPos() {
        return this.getDataDescriptor()
                .map(d -> d.startPos() + d.length())
                .orElse(this.startPos + this.length + this.FileDataLength);
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        int position = this.getStartPos();

        final DefaultMutableTreeNode nodeLfh = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.getLength(),
                "Local file header - " + this.FileNameValue));
        parent.add(nodeLfh);

        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "signature:" + BytesTool.getByteDataHexView(this.Signature),
                Icons.Signature
        )));
        position += 4;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "version needed to extract = %d".formatted(this.VersionNeededToExtract),
                Icons.Versions
        )));
        position += 2;
        generateTreeNodeGeneralPurposeBitFlag(nodeLfh, position, this.GeneralPurposeBitFlag);
        position += 2;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                Compression.toText(this.CompressionMethod))));

        position += 2;
        final DefaultMutableTreeNode nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "last mod file time = %s".formatted(this.LastModFileTimeValue),
                Icons.Time
        ));
        nodeLfh.add(nodeTime);
        this.LastModFileTimeValue.generateTreeNode(nodeTime);

        position += 2;
        final DefaultMutableTreeNode nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "last mod file date = %s".formatted(this.LastModFileDateValue),
                Icons.Calendar
        ));
        nodeLfh.add(nodeDate);
        this.LastModFileDateValue.generateTreeNode(nodeDate);

        position += 2;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "crc-32:" + BytesTool.getByteDataHexView(this.CRC32),
                Icons.Checksum
        )));
        position += 4;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "compressed size = %d".formatted(this.CompressedSize),
                Icons.Size
        )));
        position += 4;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "uncompressed size = %d".formatted(this.UncompressedSize),
                Icons.Size
        )));
        position += 4;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "file name length = %d".formatted(this.FileNameLength),
                Icons.Length
        )));
        position += 2;
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "extra field length = %d".formatted(this.ExtraFieldLength),
                Icons.Length
        )));
        position += 2;
        if (this.FileName != null) {
            nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.FileName.length,
                    "file name = %s".formatted(this.FileNameValue),
                    Icons.Name
            )));
            position += this.FileName.length;
        }
        if (this.ExtraField != null) {
            generateTreeNodeExtraField(nodeLfh, position, this.ExtraField.length, this.ExtraFieldBlocks);
            position += this.ExtraField.length;
        }

        // File data
        if (this.FileDataLength > 0) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.FileDataLength,
                    "File data",
                    Icons.Data,
                    ZIPFile.MESSAGES.getString("MSG_COMPRESSED_FILE")
            )));
        }

        if (this.dataDescriptor != null) {
            this.dataDescriptor.generateTreeNode(parent);
        }
    }

    /// Get the corresponding bit value of `position`.
    ///
    /// There are two bytes in the [#GeneralPurposeBitFlag] field, the bit
    /// position is located as:
    ///
    /// ```
    /// byte        :  [ byte left/0 ]    [ byte right/1]
    /// bit position:  7 6 5 4 3 2 1 0    F E D C B A 9 8
    /// ```
    ///
    /// @param position Bit position, from 0 to 15
    /// @return The bit value, 0 or 1
    public int getGeneralPurposeBitFlagBitValue(final int position) {
        return parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
    }

    static int parseGeneralPurposeBitFlag(final byte[] buf, final int position) {
        if (position < 0 || position > 15) {
            throw new IllegalArgumentException("Invalid position value %d, [0, 15] is expected.".formatted(position));
        }
        return (buf[position / Byte.SIZE] >> (position % Byte.SIZE)) & 1;
    }

    /// Add the `general purpose bit flag` node, with one child per bit.
    static void generateTreeNodeGeneralPurposeBitFlag(final DefaultMutableTreeNode parent, final int position, final byte[] flag) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "general purpose bit flag = %02X %02X".formatted(flag[0], flag[1])));
        parent.add(node);
        for (int i = 0; i < FLAG_NAMES.size(); i++) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + i / Byte.SIZE,
                    1,
                    "Bit %02d = %d (%s)".formatted(i, parseGeneralPurposeBitFlag(flag, i), FLAG_NAMES.get(i)),
                    Icons.Tag
            )));
        }
    }

    /// Add the `extra field` node, with one child per [ExtraFieldBlock].
    static void generateTreeNodeExtraField(final DefaultMutableTreeNode parent, final int position, final int length, final List<ExtraFieldBlock> blocks) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                length,
                "extra field"));
        parent.add(node);
        blocks.forEach(b -> b.generateTreeNode(node));
    }

    /// Read `length` bytes, or return `null` if `length` is 0.
    static byte[] readOptional(final PosDataInputStream stream, final int length) throws IOException {
        if (length == 0) {
            return null;
        }
        final byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return bytes;
    }

    /// Decode a file name or comment.
    ///
    /// The names are decoded as UTF-8, the same as `java.util.zip`. Archives
    /// using a local code page, such as WinRAR with Chinese file names, are not
    /// decoded correctly.
    ///
    /// @return The text, or empty string for `null`
    static String decodeText(final byte[] bytes) {
        return (bytes == null) ? "" : new String(bytes, StandardCharsets.UTF_8);
    }

    static String signatureMismatch(final String name, final int pos, final byte[] found, final byte[] expected) {
        return "Signature does not match for '%s' at 0x%08X: found = %s, expected = %s".formatted(
                name, pos, BytesTool.getByteDataHexView(found), BytesTool.getByteDataHexView(expected));
    }

    /// The data descriptor, which follows the file data when
    /// [#FLAG_DATA_DESCRIPTOR] is set.
    ///
    /// ```
    /// signature (optional)    4 bytes  (0x08074b50)
    /// crc-32                  4 bytes
    /// compressed size         4 bytes, or 8 bytes for Zip64
    /// uncompressed size       4 bytes, or 8 bytes for Zip64
    /// ```
    ///
    /// @param startPos Start position in the file
    /// @param hasSignature Whether the optional signature exists
    /// @param sizeLength Length of each size field, 4, or 8 for Zip64
    /// @param crc32 CRC-32
    /// @param compressedSize Compressed size
    /// @param uncompressedSize Uncompressed size
    public record DataDescriptor(int startPos, boolean hasSignature, int sizeLength,
            long crc32, long compressedSize, long uncompressedSize) implements GenerateTreeNode {

        /// Read the data descriptor at `pos`.
        ///
        /// @return The data descriptor, or `null` if it does not fit the file
        static DataDescriptor read(final PosDataInputStream stream, final int pos, final boolean zip64) throws IOException {
            final byte[] buf = stream.getBuf();
            final int sigLength = ZIPFile.DATA_DESCRIPTOR.length;
            final boolean signature = pos + sigLength <= buf.length
                    && Arrays.equals(buf, pos, pos + sigLength, ZIPFile.DATA_DESCRIPTOR, 0, sigLength);
            final int sizeLength = zip64 ? Long.BYTES : Integer.BYTES;
            if (pos + (signature ? sigLength : 0) + Integer.BYTES + 2 * sizeLength > buf.length) {
                return null;
            }

            stream.flyTo(pos + (signature ? sigLength : 0));
            final long crc = stream.readUnsignedIntInLittleEndian();
            final long compressed = zip64 ? stream.readLongInLittleEndian() : stream.readUnsignedIntInLittleEndian();
            final long uncompressed = zip64 ? stream.readLongInLittleEndian() : stream.readUnsignedIntInLittleEndian();
            return new DataDescriptor(pos, signature, sizeLength, crc, compressed, uncompressed);
        }

        /// Length of the data descriptor.
        ///
        /// @return The length in bytes
        public int length() {
            return (this.hasSignature ? ZIPFile.DATA_DESCRIPTOR.length : 0) + Integer.BYTES + 2 * this.sizeLength;
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
            int position = this.startPos;

            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.length(),
                    "Data descriptor"));
            parentNode.add(node);

            if (this.hasSignature) {
                node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        position,
                        4,
                        "signature:" + BytesTool.getByteDataHexView(ZIPFile.DATA_DESCRIPTOR),
                        Icons.Signature)));
                position += 4;
            }
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    4,
                    "crc-32 = 0x%08X".formatted(this.crc32),
                    Icons.Checksum)));
            position += 4;
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.sizeLength,
                    "compressed size = %d".formatted(this.compressedSize),
                    Icons.Size)));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + this.sizeLength,
                    this.sizeLength,
                    "uncompressed size = %d".formatted(this.uncompressedSize),
                    Icons.Size)));
        }
    }
}
