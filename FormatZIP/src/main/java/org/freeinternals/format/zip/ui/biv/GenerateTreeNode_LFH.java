/**
 * GenerateTreeNode_LFH.java    May 09, 2011, 22:46
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip.ui.biv;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.zip.LocalFileHeader;
import org.freeinternals.format.zip.MSDosDate;
import org.freeinternals.format.zip.MSDosTime;

/**
 *
 * @author Amos Shi
 */
public class GenerateTreeNode_LFH {

    /**
     * Generate tree nodes for local file header, as well as file data.
     */
    public static void LocalFileHeader(
            LocalFileHeader lfh,
            DefaultMutableTreeNode parent) {

        int position = lfh.getStartPos();
        DefaultMutableTreeNode nodeLfh;
        DefaultMutableTreeNode nodeBigFlag;
        DefaultMutableTreeNode nodeTime;
        DefaultMutableTreeNode nodeDate;

        // Local file header
        parent.add(nodeLfh = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                lfh.getLength(),
                "Local file header")));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "signature")));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("version needed to extract = %d", lfh.VersionNeededToExtract))));
        nodeLfh.add(nodeBigFlag = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("general purpose bit flag = %02X %02X",
                lfh.GeneralPurposeBitFlag[0],
                lfh.GeneralPurposeBitFlag[1]))));
        for (int i = 0; i < 16; i++) {
            nodeBigFlag.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position + ((i > 7) ? 1 : 0),
                    1,
                    String.format("Bit %02d = %d", i, lfh.getGeneralPurposeBitFlagBitValue(i)))));
        }
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("compression method = %d", lfh.CompressionMethod))));

        //
        nodeLfh.add(nodeTime = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file time = %s", lfh.LastModFileTimeValue.toString()))));
        GenerateTreeNode_LFH.MSDosTime(nodeTime, lfh.LastModFileTimeValue, position);

        //
        nodeLfh.add(nodeDate = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("last mod file date = %s", lfh.LastModFileDateValue.toString()))));
        GenerateTreeNode_LFH.MSDosDate(nodeDate, lfh.LastModFileDateValue, position);

        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                "crc-32")));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("compressed size = %d", lfh.CompressedSize))));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("uncompressed size = %d", lfh.UncompressedSize))));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("file name length = %d", lfh.FileNameLength))));
        nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("extra field length = %d", lfh.ExtraFieldLength))));
        position += 2;
        if (lfh.FileName != null) {
            nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    lfh.FileName.length,
                    String.format("file name = %s", lfh.FileNameValue))));
            position += lfh.FileName.length;
        }
        if (lfh.ExtraField != null) {
            nodeLfh.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    lfh.ExtraField.length,
                    "extra field")));
            position += lfh.ExtraField.length;
        }

        // File data
        if (lfh.CompressedSize > 0) {
            parent.add(nodeLfh = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position,
                    (int) lfh.CompressedSize, // TODO - We are in danger of very big file longer than int value size.
                    "File data")));
        }
    }

    static void MSDosTime(
            DefaultMutableTreeNode nodeTime,
            MSDosTime time,
            int startPos) {
        nodeTime.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Hour = %02d", time.Hour))));
        nodeTime.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Minute = %02d", time.Minute))));
        nodeTime.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Second = %02d (maybe inaccurate for one second)", time.Second))));
    }

    static void MSDosDate(
            DefaultMutableTreeNode nodeDate,
            MSDosDate date,
            int startPos) {
        nodeDate.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Year = %04d", date.Year))));
        nodeDate.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Month = %02d", date.Month))));
        nodeDate.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                String.format("Day = %02d", date.Day))));
    }
}
