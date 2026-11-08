/*
 * Zip64EndOfCentralDirectoryRecord.java    Nov 06, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import java.io.IOException;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.BytesTool;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Zip64 end of central directory record, used when a value of the
/// [EndOfCentralDirectoryRecord] does not fit its field.
///
/// ```
/// zip64 end of central dir signature                             4 bytes  (0x06064b50)
/// size of zip64 end of central directory record                  8 bytes
/// version made by                                                2 bytes
/// version needed to extract                                      2 bytes
/// number of this disk                                            4 bytes
/// number of the disk with the start of the central directory     4 bytes
/// total number of entries in the central directory on this disk  8 bytes
/// total number of entries in the central directory               8 bytes
/// size of the central directory                                  8 bytes
/// offset of start of central directory with respect to the
///   starting disk number                                         8 bytes
/// zip64 extensible data sector                                   (variable size)
/// ```
///
/// @author Amos Shi
public final class Zip64EndOfCentralDirectoryRecord extends FileComponent implements GenerateTreeNode {

    /// Size of the record without the extensible data sector.
    public static final int MIN_LENGTH = 56;
    /// Size of the signature and the `size of zip64 end of central directory
    /// record` field, which are not counted by that field.
    private static final int LEADING_LENGTH = 12;

    /// Zip64 end of central directory signature.
    public final byte[] Signature = new byte[4];
    /// Size of zip64 end of central directory record, excluding the leading 12 bytes.
    public final long RecordSize;
    /// Version made by.
    public final int VersionMadeBy;
    /// Version needed to extract.
    public final int VersionNeededToExtract;
    /// Number of this disk.
    public final long DiskNumber;
    /// Number of the disk with the start of the central directory.
    public final long DiskNumberWithSCD;
    /// Total number of entries in the central directory on this disk.
    public final long EntryTotalNumberDisk;
    /// Total number of entries in the central directory.
    public final long EntryTotalNumber;
    /// Size of the central directory.
    public final long CentralDirectorySize;
    /// Offset of start of central directory with respect to the starting disk number.
    public final long CentralDirectoryOffset;

    Zip64EndOfCentralDirectoryRecord(final PosDataInputStream stream) throws IOException {
        this.startPos = stream.getPos();

        stream.readFully(this.Signature);
        if (!Arrays.equals(this.Signature, ZIPFile.CENTRAL_ZIP64_RECORD)) {
            throw new FileFormatException(LocalFileHeader.signatureMismatch("zip64 end of central dir signature", this.startPos, this.Signature,
                            ZIPFile.CENTRAL_ZIP64_RECORD));
        }
        this.RecordSize = stream.readLongInLittleEndian();
        this.VersionMadeBy = stream.readUnsignedShortInLittleEndian();
        this.VersionNeededToExtract = stream.readUnsignedShortInLittleEndian();
        this.DiskNumber = stream.readUnsignedIntInLittleEndian();
        this.DiskNumberWithSCD = stream.readUnsignedIntInLittleEndian();
        this.EntryTotalNumberDisk = stream.readLongInLittleEndian();
        this.EntryTotalNumber = stream.readLongInLittleEndian();
        this.CentralDirectorySize = stream.readLongInLittleEndian();
        this.CentralDirectoryOffset = stream.readLongInLittleEndian();

        final long recordLength = LEADING_LENGTH + this.RecordSize;
        if (this.RecordSize < MIN_LENGTH - LEADING_LENGTH
                || this.RecordSize > (long) stream.getBuf().length - this.startPos - LEADING_LENGTH) {
            throw new FileFormatException("Invalid 'size of zip64 end of central directory record' at 0x%08X: %d".formatted(this.startPos + 4,
                            this.RecordSize));
        }
        this.length = (int) recordLength;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        int position = this.startPos;

        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.length,
                "Zip64 end of central directory record"));
        parent.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "signature:" + BytesTool.getByteDataHexView(this.Signature),
                Icons.Signature)));
        position += 4;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                8,
                "size of zip64 end of central directory record = %d".formatted(this.RecordSize),
                Icons.Size)));
        position += 8;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "version made by = %d".formatted(this.VersionMadeBy),
                Icons.Versions)));
        position += 2;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "version needed to extract = %d".formatted(this.VersionNeededToExtract),
                Icons.Versions)));
        position += 2;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "number of this disk = %d".formatted(this.DiskNumber),
                Icons.Counter)));
        position += 4;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "number of the disk with the start of the central directory = %d".formatted(this.DiskNumberWithSCD),
                Icons.Counter)));
        position += 4;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                8,
                "total number of entries in the central directory on this disk = %d".formatted(this.EntryTotalNumberDisk),
                Icons.Counter)));
        position += 8;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                8,
                "total number of entries in the central directory = %d".formatted(this.EntryTotalNumber),
                Icons.Counter)));
        position += 8;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                8,
                "size of the central directory = %d".formatted(this.CentralDirectorySize),
                Icons.Size)));
        position += 8;
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                8,
                "offset of start of central directory with respect to the starting disk number = 0x%016X".formatted(this.CentralDirectoryOffset),
                Icons.Offset)));
        if (this.length > MIN_LENGTH) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + 8,
                    this.length - MIN_LENGTH,
                    "zip64 extensible data sector",
                    Icons.Data)));
        }
    }
}
