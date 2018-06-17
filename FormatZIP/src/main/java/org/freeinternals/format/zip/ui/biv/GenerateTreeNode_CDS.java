/**
 * GenerateTreeNode_CDS.java    May 08, 2011, 10:13
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip.ui.biv;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.zip.CentralDirectoryStructure;

/**
 *
 * @author Amos Shi
 */
public class GenerateTreeNode_CDS {

    public static void CentralDirectoryStructure(
            CentralDirectoryStructure cds,
            DefaultMutableTreeNode parent) {

        DefaultMutableTreeNode nodeCDS, header, nodeBigFlag, nodeTime, nodeDate;
        int position = cds.getStartPos();

        parent.add(nodeCDS = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                cds.getLength(),
                "Central Directory Structure")));
        nodeCDS.add(header = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                cds.header.calcLength(),
                "File header")));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "central file header signature")));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("version made by = %d", cds.header.VersionMadeBy))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("version needed to extract = %d", cds.header.VersionNeededToExtract))));
        header.add(nodeBigFlag = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("general purpose bit flag = %02X %02X",
                cds.header.GeneralPurposeBitFlag[0],
                cds.header.GeneralPurposeBitFlag[1]))));
        for (int i = 0; i < 16; i++) {
            nodeBigFlag.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + ((i > 7) ? 1 : 0),
                    1,
                    String.format("Bit %02d = %d", i, cds.header.getGeneralPurposeBitFlagBitValue(i)))));
        }
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("compression method = %d", cds.header.CompressionMethod))));
        header.add(nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file time = %s", cds.header.LastModFileTimeValue.toString()))));
        GenerateTreeNode_LFH.MSDosTime(nodeTime, cds.header.LastModFileTimeValue, position);

        header.add(nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file date = %s", cds.header.LastModFileDateValue.toString()))));
        GenerateTreeNode_LFH.MSDosDate(nodeDate, cds.header.LastModFileDateValue, position);

        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                "crc-32")));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("compressed size = %d", cds.header.CompressedSize))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("uncompressed size = %d", cds.header.UncompressedSize))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("file name length = %d", cds.header.FileNameLength))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("extra field length = %d", cds.header.ExtraFieldLength))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("file comment length = %d", cds.header.FileCommentLength))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("disk number start = %d", cds.header.DiskNumberStart))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("internal file attributes = %02X %02X",
                cds.header.InternalFileAttributes[0],
                cds.header.InternalFileAttributes[1]))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                String.format("external file attributes = %02X %02X %02X %02X",
                cds.header.ExternalFileAttributes[0],
                cds.header.ExternalFileAttributes[1],
                cds.header.ExternalFileAttributes[2],
                cds.header.ExternalFileAttributes[3]))));
        header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("relative offset of local header = %d", cds.header.RelativeOffsetOfLocalHeader))));
        position += 4;
        if (cds.header.FileName != null) {
            header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    cds.header.FileName.length,
                    String.format("file name = %s", cds.header.FileNameValue))));
            position += cds.header.FileName.length;
        }
        if (cds.header.ExtraField != null) {
            header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    cds.header.ExtraField.length,
                    "extra field")));
            position += cds.header.ExtraField.length;
        }
        if (cds.header.FileComment != null) {
            header.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    cds.header.FileComment.length,
                    "file comment")));
        }
    }
}
