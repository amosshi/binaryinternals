/*
 * BitmapFileHeader.java    Nov 28, 2010, 21:00
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.bmp;

import java.io.IOException;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The bitmap file header, at the start of the file.
///
/// @author Amos Shi
public final class BitmapFileHeader extends FileComponent implements GenerateTreeNode {

    /// Length of the bitmap file header in bytes.
    public static final int LENGTH = 14;
    /// The valid values of [#magic].
    private static final List<String> MAGICS = List.of("BM", "BA", "CI", "CP", "IC", "PT");

    /// The magic number used to identify the BMP file, usually 0x42 0x4D in hex,
    /// same as BM in ASCII.
    ///
    /// The following entries are possible:
    ///
    /// - **BM** – Windows 3.1x, 95, NT, ... etc.
    /// - **BA** – OS/2 Bitmap Array
    /// - **CI** – OS/2 Color Icon
    /// - **CP** – OS/2 Color Pointer
    /// - **IC** – OS/2 Icon
    /// - **PT** – OS/2 Pointer
    public final String magic;
    /// The fileSize of the BMP file in bytes.
    public final long fileSize;
    /// Reserved; actual value depends on the application that creates the image.
    public final byte[] creator1 = new byte[2];
    /// Reserved; actual value depends on the application that creates the image.
    public final byte[] creator2 = new byte[2];
    /// The offset, i.e. starting address, of the byte where the bitmap data can be found.
    public final long offset;

    BitmapFileHeader(final PosDataInputStream input) throws IOException {
        super(input.getPos(), LENGTH);

        this.magic = input.readASCII(2);
        if (!MAGICS.contains(this.magic)) {
            throw new FileFormatException(
                    "This is not a valid BMP file, because the magic number '%s' is not one of %s.".formatted(this.magic, MAGICS));
        }
        this.fileSize = input.readUnsignedIntInLittleEndian();
        input.readFully(this.creator1);
        input.readFully(this.creator2);
        this.offset = input.readUnsignedIntInLittleEndian();
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "Bitmap File Header [0x%08X, %d]".formatted(this.startPos, this.length),
                "This block of bytes is at the start of the file and is used to identify the file. A typical application reads this block first to ensure "
                        + "that the file is actually a BMP file and that it is not damaged."));
        parentNode.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                2,
                "magic number = %s".formatted(this.magic),
                "The magic number used to identify the BMP file, usually <code>0x42</code> <code>0x4D</code> in hex, same as <strong>BM</strong> in ASCII. ")));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 2,
                4,
                "file size = %d (0x%04X)".formatted(this.fileSize, this.fileSize),
                "The size of the BMP file in bytes.")));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 6,
                2,
                "reserved 1",
                "Reserved; actual value depends on the application that creates the image.")));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 8,
                2,
                "reserved 2",
                "Reserved; actual value depends on the application that creates the image.")));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 10,
                4,
                "offset = %d (0x%08X)".formatted(this.offset, this.offset),
                "The offset, i.e. starting address, of the byte where the bitmap data can be found.")));
    }
}
