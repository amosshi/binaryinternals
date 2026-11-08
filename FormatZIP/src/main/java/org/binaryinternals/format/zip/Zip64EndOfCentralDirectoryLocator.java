/*
 * Zip64EndOfCentralDirectoryLocator.java    Nov 06, 2026
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

/// Zip64 end of central directory locator, immediately before the
/// [EndOfCentralDirectoryRecord].
///
/// ```
/// zip64 end of central dir locator signature                     4 bytes  (0x07064b50)
/// number of the disk with the start of the zip64 end of
///   central directory                                            4 bytes
/// relative offset of the zip64 end of central directory record   8 bytes
/// total number of disks                                          4 bytes
/// ```
///
/// @author Amos Shi
public final class Zip64EndOfCentralDirectoryLocator extends FileComponent implements GenerateTreeNode {

    /// Size of the locator.
    public static final int LENGTH = 20;

    /// Zip64 end of central directory locator signature.
    public final byte[] Signature = new byte[4];
    /// Number of the disk with the start of the zip64 end of central directory.
    public final long DiskNumberWithZip64End;
    /// Relative offset of the [Zip64EndOfCentralDirectoryRecord].
    public final long Zip64EndOffset;
    /// Total number of disks.
    public final long TotalDisks;

    Zip64EndOfCentralDirectoryLocator(final PosDataInputStream stream) throws IOException {
        super(stream.getPos(), LENGTH);

        stream.readFully(this.Signature);
        if (!Arrays.equals(this.Signature, ZIPFile.CENTRAL_ZIP64_LOCATOR)) {
            throw new FileFormatException(LocalFileHeader.signatureMismatch("zip64 end of central dir locator signature", this.startPos, this.Signature,
                            ZIPFile.CENTRAL_ZIP64_LOCATOR));
        }
        this.DiskNumberWithZip64End = stream.readUnsignedIntInLittleEndian();
        this.Zip64EndOffset = stream.readLongInLittleEndian();
        this.TotalDisks = stream.readUnsignedIntInLittleEndian();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parent) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Zip64 end of central directory locator"));
        parent.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                4,
                "signature:" + BytesTool.getByteDataHexView(this.Signature),
                Icons.Signature)));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                "number of the disk with the start of the zip64 end of central directory = %d".formatted(this.DiskNumberWithZip64End),
                Icons.Counter)));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 8,
                8,
                "relative offset of the zip64 end of central directory record = 0x%016X".formatted(this.Zip64EndOffset),
                Icons.Offset)));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 16,
                4,
                "total number of disks = %d".formatted(this.TotalDisks),
                Icons.Counter)));
    }
}
