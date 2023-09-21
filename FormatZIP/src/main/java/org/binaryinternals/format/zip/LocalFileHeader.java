/**
 * LocalFileHeader.java    May 10, 2011, 22:16
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
public class LocalFileHeader extends FileComponent implements GenerateTreeNode {

    /**
     * Local file header signature.
     */
    public final byte[] Signature = new byte[4];
    /**
     * Version needed to extract.
     */
    public final int VersionNeededToExtract;
    /**
     * General purpose bit flag.
     */
    public final byte[] GeneralPurposeBitFlag = new byte[2];
    /**
     * Compression method.
     */
    public final int CompressionMethod;
    /**
     * Last mod file time.
     */
    public final int LastModFileTime;
    /**
     * Parsed value of {@link #LastModFileTime}.
     *
     * @see #LastModFileTime
     */
    public final MSDosTime LastModFileTimeValue;
    /**
     * Last mod file date.
     */
    public final int LastModFileDate;
    /**
     * Parsed value of {@link #LastModFileDate}.
     *
     * @see #LastModFileDate
     */
    public final MSDosDate LastModFileDateValue;
    /**
     * CRC-32.
     */
    public final byte[] CRC32 = new byte[4];
    /**
     * Compressed size.
     */
    public final long CompressedSize;
    /**
     * Uncompressed size.
     */
    public final long UncompressedSize;
    /**
     * File name length.
     */
    public final int FileNameLength;
    /**
     * Extra field length.
     */
    public final int ExtraFieldLength;
    /**
     * File name. <code>null</code> when {@link #FileNameLength} is 0.
     */
    public final byte[] FileName;
    /**
     * Parsed value of {@link #FileName}.
     *
     * @see #FileName
     */
    public final String FileNameValue;
    /**
     * Extra field. <code>null</code> when {@link #ExtraFieldLength} is 0.
     */
    public final byte[] ExtraField;

    LocalFileHeader(PosDataInputStream stream) throws IOException, FileFormatException {
        this.startPos = stream.getPos();

        int readBytes = stream.read(this.Signature);
        if (readBytes != this.Signature.length) {
            throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.Signature.length, readBytes));
        }

        if (BytesTool.isByteArraySame(this.Signature, ZIPFile.LOCAL_FILE_HEADER) == false) {
            throw new FileFormatException("Signature does not match for 'local file header signature'.");
        }

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
        if (this.FileNameLength > 0) {
            this.FileName = new byte[this.FileNameLength];
            readBytes = stream.read(this.FileName);
            if (readBytes != this.FileName.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.FileName.length, readBytes));
            }

            // PosDataInputStream utf8 = new PosDataInputStream(new PosByteArrayInputStream(this.FileName));
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
            readBytes = stream.read(this.ExtraField);
            if (readBytes != this.ExtraField.length) {
                throw new IOException(String.format("Failed to read %d bytes, actual bytes read %d", this.ExtraField.length, readBytes));
            }
        } else {
            this.ExtraField = null;
        }

        this.length = 30 + this.FileNameLength + this.ExtraFieldLength;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parent) {
        int position = this.getStartPos();

        // Local file header
        DefaultMutableTreeNode nodeLfh = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
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
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("version needed to extract = %d", this.VersionNeededToExtract),
                Icons.Versions
        )));

        DefaultMutableTreeNode nodeBigFlag = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("general purpose bit flag = %02X %02X",
                        this.GeneralPurposeBitFlag[0],
                        this.GeneralPurposeBitFlag[1])
        ));
        nodeLfh.add(nodeBigFlag);
        for (int i = 0; i < 16; i++) {
            nodeBigFlag.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + ((i > 7) ? 1 : 0),
                    1,
                    String.format("Bit %02d = %d", i, this.getGeneralPurposeBitFlagBitValue(i)),
                    Icons.Tag
            )));
        }
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("compression method = %d", this.CompressionMethod))));

        //
        DefaultMutableTreeNode nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file time = %s", this.LastModFileTimeValue.toString()),
                Icons.Time
        ));
        nodeLfh.add(nodeTime);
        this.LastModFileTimeValue.generateTreeNode(nodeTime);

        //
        DefaultMutableTreeNode nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file date = %s", this.LastModFileDateValue.toString()),
                Icons.Calendar
        ));
        nodeLfh.add(nodeDate);
        this.LastModFileDateValue.generateTreeNode(nodeDate);

        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                "crc-32:" + BytesTool.getByteDataHexView(this.CRC32),
                Icons.Checksum
        )));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("compressed size = %d", this.CompressedSize),
                Icons.Size
        )));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("uncompressed size = %d", this.UncompressedSize),
                Icons.Size
        )));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("file name length = %d", this.FileNameLength),
                Icons.Length
        )));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("extra field length = %d", this.ExtraFieldLength),
                Icons.Length
        )));
        position += 2;
        if (this.FileName != null) {
            nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.FileName.length,
                    String.format("file name = %s", this.FileNameValue),
                    Icons.Name
            )));
            position += this.FileName.length;
        }
        if (this.ExtraField != null) {
            nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    this.ExtraField.length,
                    "extra field")));
            position += this.ExtraField.length;
        }

        // File data
        if (this.CompressedSize > 0) {
            parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    (int) this.CompressedSize, // Note. We are in danger of very big file longer than int value size.
                    "File data",
                    Icons.Data,
                    ZIPFile.MESSAGES.getString("MSG_COMPRESSED_FILE")
            )));
        }
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
     * @return The bit value, 0 or 1
     */
    public int getGeneralPurposeBitFlagBitValue(int position) {
        return LocalFileHeader.parseGeneralPurposeBitFlag(this.GeneralPurposeBitFlag, position);
    }

    static int parseGeneralPurposeBitFlag(byte[] buf, int position) {
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
