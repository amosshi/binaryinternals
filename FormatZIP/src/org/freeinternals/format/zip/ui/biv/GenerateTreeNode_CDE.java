/**
 * GenerateTreeNode_CDE.java    May 09, 2011, 22:46
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.zip.ui.biv;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.zip.EndOfCentralDirectoryRecord;

/**
 *
 * @author Amos Shi
 */
public class GenerateTreeNode_CDE {

    public static void EndOfCentralDirectoryRecord(
            EndOfCentralDirectoryRecord cde,
            DefaultMutableTreeNode parent) {

        int position = cde.getStartPos();
        DefaultMutableTreeNode nodeCDE;

        parent.add(nodeCDE = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                cde.getLength(),
                "End of central directory record")));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position,
                4,
                "signature")));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format("number of this disk = %d", cde.DiskNumber))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("number of the disk with the start of the central directory = %d", cde.DiskNumberWithSCD))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("total number of entries in the central directory on this disk = %d", cde.EntryTotalNumberDisk))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                2,
                String.format("total number of entries in the central directory = %d", cde.EntryTotalNumber))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 2,
                4,
                String.format("size of the central directory = %d", cde.CentralDirectorySize))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                4,
                String.format("offset of start of central directory with respect to the starting disk number = %d", cde.CentralDirectoryOffset))));
        nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                position += 4,
                2,
                String.format(".ZIP file comment length = %d", cde.ZipFileCommentLength))));
        if (cde.ZipFileComment != null) {
            nodeCDE.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    position += 2,
                    cde.ZipFileComment.length,
                    ".ZIP file comment")));
        }
    }
}
