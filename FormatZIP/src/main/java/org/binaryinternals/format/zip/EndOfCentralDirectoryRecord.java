/*
 * EndOfCentralDirectoryRecord.java    May 08, 2011, 11:24
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
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

/// End of central directory record.
///
/// ```
/// end of central dir signature                                   4 bytes  (0x06054b50)
/// number of this disk                                            2 bytes
/// number of the disk with the start of the central directory     2 bytes
/// total number of entries in the central directory on this disk  2 bytes
/// total number of entries in the central directory               2 bytes
/// size of the central directory                                  4 bytes
/// offset of start of central directory with respect to the
///   starting disk number                                         4 bytes
/// .ZIP file comment length                                       2 bytes
/// .ZIP file comment                                              (variable size)
/// ```
///
/// @author Amos Shi
public final class EndOfCentralDirectoryRecord extends FileComponent implements GenerateTreeNode {

    /// Size of the record without the comment.
    public static final int MIN_LENGTH = 22;

    /// End of central directory signature.
    public final byte[] Signature = new byte[4];
    /// Number of this disk.
    public final int DiskNumber;
    /// Number of the disk with the start of the central directory.
    public final int DiskNumberWithSCD;
    /// Total number of entries in the central directory on this disk.
    public final int EntryTotalNumberDisk;
    /// Total number of entries in the central directory.
    public final int EntryTotalNumber;
    /// Size of the central directory.
    public final long CentralDirectorySize;
    /// Offset of start of central directory with respect to the starting disk number.
    public final long CentralDirectoryOffset;
    /// ZIP file comment length.
    public final int ZipFileCommentLength;
    /// ZIP file comment. `null` if [#ZipFileCommentLength] is zero.
    public final byte[] ZipFileComment;

    EndOfCentralDirectoryRecord(final PosDataInputStream stream) throws IOException {
        this.startPos = stream.getPos();

        stream.readFully(this.Signature);
        if (!Arrays.equals(this.Signature, ZIPFile.CENTRAL_END)) {
            throw new FileFormatException(LocalFileHeader.signatureMismatch("end of central directory signature", this.startPos, this.Signature,
                            ZIPFile.CENTRAL_END));
        }
        this.DiskNumber = stream.readUnsignedShortInLittleEndian();
        this.DiskNumberWithSCD = stream.readUnsignedShortInLittleEndian();
        this.EntryTotalNumberDisk = stream.readUnsignedShortInLittleEndian();
        this.EntryTotalNumber = stream.readUnsignedShortInLittleEndian();
        this.CentralDirectorySize = stream.readUnsignedIntInLittleEndian();
        this.CentralDirectoryOffset = stream.readUnsignedIntInLittleEndian();
        this.ZipFileCommentLength = stream.readUnsignedShortInLittleEndian();
        this.ZipFileComment = LocalFileHeader.readOptional(stream, this.ZipFileCommentLength);

        this.length = MIN_LENGTH + this.ZipFileCommentLength;
    }

    /// Whether any field is set to its maximum value, which means the actual
    /// value is in the [Zip64EndOfCentralDirectoryRecord].
    ///
    /// @return `true` if the archive needs the Zip64 record
    public boolean isZip64() {
        return this.DiskNumber == 0xFFFF
                || this.DiskNumberWithSCD == 0xFFFF
                || this.EntryTotalNumberDisk == 0xFFFF
                || this.EntryTotalNumber == 0xFFFF
                || this.CentralDirectorySize == CentralDirectoryStructure.FileHeader.ZIP64_MAGIC
                || this.CentralDirectoryOffset == CentralDirectoryStructure.FileHeader.ZIP64_MAGIC;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        int position = this.getStartPos();

        final DefaultMutableTreeNode nodeCDE = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                this.getLength(),
                "End of central directory record"));
        parent.add(nodeCDE);

        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "signature:" + BytesTool.getByteDataHexView(this.Signature),
                Icons.Signature
        )));
        position += 4;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "number of this disk = %d".formatted(this.DiskNumber),
                Icons.Counter
        )));
        position += 2;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "number of the disk with the start of the central directory = %d".formatted(this.DiskNumberWithSCD),
                Icons.Counter
        )));
        position += 2;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "total number of entries in the central directory on this disk = %d".formatted(this.EntryTotalNumberDisk),
                Icons.Counter
        )));
        position += 2;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                "total number of entries in the central directory = %d".formatted(this.EntryTotalNumber),
                Icons.Counter
        )));
        position += 2;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "size of the central directory = %d".formatted(this.CentralDirectorySize),
                Icons.Size
        )));
        position += 4;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "offset of start of central directory with respect to the starting disk number = 0x%08X".formatted(this.CentralDirectoryOffset),
                Icons.Offset
        )));
        position += 4;
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                2,
                ".ZIP file comment length = %d".formatted(this.ZipFileCommentLength),
                Icons.Length
        )));
        if (this.ZipFileComment != null) {
            nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + 2,
                    this.ZipFileComment.length,
                    ".ZIP file comment = %s".formatted(LocalFileHeader.decodeText(this.ZipFileComment)),
                    Icons.Annotations
            )));
        }
    }
}
