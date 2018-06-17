/**
 * EndOfCentralDirectoryRecord.java    May 08, 2011, 11:24
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;

/**
 * End of central directory record.
 * <pre>
 *      end of central dir signature                                   4 bytes  (0x06054b50)
 *      number of this disk                                            2 bytes
 *      number of the disk with the start of the central directory     2 bytes
 *      total number of entries in the central directory on this disk  2 bytes
 *      total number of entries in the central directory               2 bytes
 *      size of the central directory                                  4 bytes
 *      offset of start of central directory with respect to the
 *        starting disk number                                         4 bytes
 *      .ZIP file comment length                                       2 bytes
 *      .ZIP file comment                                              (variable size)
 * </pre>
 *
 * @author Amos Shi
 */
public class EndOfCentralDirectoryRecord extends FileComponent {

    /** End of central directory signature. */
    public final byte[] Signature = new byte[4];
    /** Number of this disk. */
    public final int DiskNumber;
    /** Number of the disk with the start of the central directory. */
    public final int DiskNumberWithSCD;
    /** Total number of entries in the central directory on this disk. */
    public final int EntryTotalNumberDisk;
    /** Total number of entries in the central directory. */
    public final int EntryTotalNumber;
    /** Size of the central directory. */
    public final long CentralDirectorySize;
    /** Offset of start of central directory with respect to the starting disk number. */
    public final long CentralDirectoryOffset;
    /** ZIP file comment length. */
    public final int ZipFileCommentLength;
    /** ZIP file comment. <code>null</code> of <@link #ZipFileCommentLength> is zero. */
    public final byte[] ZipFileComment;

    EndOfCentralDirectoryRecord(PosDataInputStream stream) throws IOException, FileFormatException {
        this.startPos = stream.getPos();
        stream.read(this.Signature);
        if (Tool.isByteArraySame(this.Signature, ZIPFile.CENTRAL_END) == false) {
            throw new FileFormatException("Signature does not match for 'end of central directory signature'.");
        }
        this.DiskNumber = stream.readUnsignedShort_LittleEndian();
        this.DiskNumberWithSCD = stream.readUnsignedShort_LittleEndian();
        this.EntryTotalNumberDisk = stream.readUnsignedShort_LittleEndian();
        this.EntryTotalNumber = stream.readUnsignedShort_LittleEndian();
        this.CentralDirectorySize = stream.readUnsignedInt_LittleEndian();
        this.CentralDirectoryOffset = stream.readUnsignedInt_LittleEndian();
        this.ZipFileCommentLength = stream.readUnsignedShort_LittleEndian();
        if (this.ZipFileCommentLength > 0) {
            this.ZipFileComment = new byte[this.ZipFileCommentLength];
            stream.read(this.ZipFileComment);
        } else {
            this.ZipFileComment = null;
        }

        this.length = 22 + this.ZipFileCommentLength;
    }
}
