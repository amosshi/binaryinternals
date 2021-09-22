/**
 * PNGFile.java Apr 19, 2011, 07:58
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;

/**
 *
 * @author Amos Shi
 */
public class ZIPFile extends FileFormat implements GenerateTreeNode {

    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(ZIPFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    /**
     * Minimal size for a valid zip file.
     */
    public static final int ZIPFILE_MIN_LENGTH = 22;
    /**
     * Central file header signature of File header in Central directory
     * structure.
     */
    static final byte[] CENTRAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x01, (byte) 0x02};
    /**
     * Local file header signature in Local file header.
     */
    static final byte[] LOCAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04};
    /**
     * Header signature of Digital signature in Central directory structure.
     */
    public static final byte[] DIGITAL_SIG_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x05};
    /**
     * End of central dir signature in End of central directory record.
     */
    static final byte[] CENTRAL_END = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x06};
    /**
     * Signature in Zip64 end of central directory record.
     */
    public static final byte[] CENTRAL_ZIP64_RECORD = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x06};
    /**
     * Signature in Zip64 end of central directory locator.
     */
    public static final byte[] CENTRAL_ZIP64_LOCATOR = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x07};
    /**
     * Archive extra data signature in Archive extra data record.
     */
    public static final byte[] ARCHIVE_EXTRA_DATA = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x08};

    EndOfCentralDirectoryRecord cde = null;
    CentralDirectoryStructure[] cds = null;
    LocalFileHeader[] lfh = null;

    public ZIPFile(final File file) throws IOException, FileFormatException {
        super(file);

        // Check the file length
        if (this.fileByteArray.length < ZIPFILE_MIN_LENGTH) {
            throw new FileFormatException(String.format(
                    "The file length (%d) is less than the minimal allowed size.", this.fileByteArray.length));
        }

        this.parse();
    }

    private void parse() throws IOException, FileFormatException {
        PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // End of central directory record.
        stream.skipToEnd();
        int pos_cde = stream.backwardTo(ZIPFile.CENTRAL_END);
        if (pos_cde > -1) {
            this.cde = new EndOfCentralDirectoryRecord(stream);
        } else {
            throw new FileFormatException("This is not a valid zip file since cannot find 'end of central directory record'.");
        }
        super.components.put(Long.valueOf(pos_cde), this.cde);

        // If no entry, nothing to do
        if (this.cde.EntryTotalNumber < 1) {
            return;
        }

        // File header of central directory structure.
        stream.reset();
        BytesTool.skip(stream, this.cde.CentralDirectoryOffset);
        this.cds = new CentralDirectoryStructure[this.cde.EntryTotalNumber];
        for (int i = 0; i < this.cds.length; i++) {
            this.cds[i] = new CentralDirectoryStructure(stream);

            super.components.put(Long.valueOf(this.cds[i].getStartPos()), this.cds[i]);
        }

        // Local file header
        this.lfh = new LocalFileHeader[this.cds.length];
        for (int i = 0; i < this.cds.length; i++) {
            stream.reset();
            BytesTool.skip(stream, this.cds[i].header.RelativeOffsetOfLocalHeader);
            this.lfh[i] = new LocalFileHeader(stream);

            super.components.put(Long.valueOf(this.lfh[i].getStartPos()), this.lfh[i]);
        }
    }

    @Override
    public String getContentTabName() {
        return "ZIP File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode root) {
        int lastPos = 0;

        for (FileComponent value : super.components.values()) {
            // Fill the gap first
            int gap = value.getStartPos() - lastPos;
            if (gap > 0) {
                generateTreeNodeGap(root, lastPos, gap);
            }

            // Generate the tree nodes
            if (value instanceof GenerateTreeNode) {
                ((GenerateTreeNode) value).generateTreeNode(root);
                lastPos = value.getStartPos() + value.getLength();

                if (value instanceof LocalFileHeader) {
                    lastPos += ((LocalFileHeader)value).CompressedSize;
                }
            }
        }
    }
}
