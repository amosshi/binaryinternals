/**
 * PNGFile.java Apr 19, 2011, 07:58
 *
 * Copyright 2011, FreeInternals.org. All rights reserved. Use is subject to
 * license terms.
 */
package org.freeinternals.format.zip;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.zip.ui.biv.GenerateTreeNode_CDE;
import org.freeinternals.format.zip.ui.biv.GenerateTreeNode_CDS;
import org.freeinternals.format.zip.ui.biv.GenerateTreeNode_LFH;

/**
 *
 * @author Amos Shi
 */
public class ZIPFile extends FileFormat {

    public static final int ZIPFILE_MIN_LENGTH = 22;
    /**
     * Central file header signature of File header in Central directory
     * structure.
     */
    public static final byte[] CENTRAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x01, (byte) 0x02};
    /**
     * Local file header signature in Local file header.
     */
    public static final byte[] LOCAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04};
    /**
     * Header signature of Digital signature in Central directory structure.
     */
    public static final byte[] DIGITAL_SIG_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x05};
    /**
     * End of central dir signature in End of central directory record.
     */
    public static final byte[] CENTRAL_END = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x06};
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

    CentralDirectoryStructure[] cds = null;
    EndOfCentralDirectoryRecord cde = null;
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

        // File header of central directory structure.
        if (this.cde.EntryTotalNumber < 1) {
            return;
        }
        stream.reset();
        stream.skip(this.cde.CentralDirectoryOffset);
        this.cds = new CentralDirectoryStructure[this.cde.EntryTotalNumber];
        for (int i = 0; i < this.cds.length; i++) {
            this.cds[i] = new CentralDirectoryStructure(stream);
        }

        // Local file header
        this.lfh = new LocalFileHeader[this.cds.length];
        for (int i = 0; i < this.cds.length; i++) {
            stream.reset();
            stream.skip(this.cds[i].header.RelativeOffsetOfLocalHeader);
            this.lfh[i] = new LocalFileHeader(stream);
        }

        // Add the components to a central list
        super.components.put(Long.valueOf(pos_cde), this.cde);
        if (this.cds != null) {
            for (CentralDirectoryStructure cds_item : this.cds) {
                super.components.put(Long.valueOf(cds_item.getStartPos()), cds_item);
            }
        }
        if (this.lfh != null) {
            for (LocalFileHeader lfh_item : this.lfh) {
                super.components.put(Long.valueOf(lfh_item.getStartPos()), lfh_item);
            }
        }
    }

    public String getContentTabName() {
        return "ZIP File";
    }

    public void generateTreeNode(DefaultMutableTreeNode root) {
        Iterator iterator = super.components.values().iterator();
        int lastPos = 0;
        int distance;
        while (iterator.hasNext()) {
            FileComponent value = (FileComponent) iterator.next();

            // Fill the gap first
            distance = value.getStartPos() - lastPos;
            if (distance > 0) {
                root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        lastPos,
                        distance,
                        String.format("Gap [%x, %x] length = %d", lastPos, lastPos + distance, distance))));
            }

            // Generate the tree nodes
            if (value instanceof LocalFileHeader) {
                GenerateTreeNode_LFH.LocalFileHeader((LocalFileHeader) value, root);
                lastPos = (int) (lastPos + ((LocalFileHeader) value).getSizeWithFileData());
            } else if (value instanceof CentralDirectoryStructure) {
                GenerateTreeNode_CDS.CentralDirectoryStructure((CentralDirectoryStructure) value, root);
            } else if (value instanceof EndOfCentralDirectoryRecord) {
                GenerateTreeNode_CDE.EndOfCentralDirectoryRecord((EndOfCentralDirectoryRecord) value, root);
            } else {
                root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        lastPos,
                        distance,
                        String.format("File Component [%x, %x] length = %d",
                                value.getStartPos(),
                                value.getStartPos() + value.getLength(),
                                value.getLength()))));
            }

            // Refersh the last position
            if (value instanceof LocalFileHeader) {
                lastPos = (int) (lastPos + ((LocalFileHeader) value).getSizeWithFileData());
            } else {
                lastPos = lastPos + value.getLength();
            }
        }

        distance = this.fileByteArray.length - lastPos;
        if (distance > 0) {
            root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos,
                    distance,
                    String.format("Gap [%x, %x] length = %d", lastPos, lastPos + distance, distance))));
        }
    }
}
