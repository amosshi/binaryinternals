/*
 * ICCProfile.java    Nov 09, 2010, 21:39
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.util.Tool;

/**
 *
 * @author Amos Shi
 * @see <a href="http://www.color.org/">INTERNATIONAL COLOR CONSORTIUM</a>
 */
public class ICCProfile extends FileComponent {

    public final byte[] rawData;
    public final Header header;
    public final int tagCount;
    public final Tag[] tagTable;

    public ICCProfile(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.rawData = input.getBuf();
        this.header = new Header(input);
        this.tagCount = input.readInt();
        if (this.tagCount > 0) {
            this.tagTable = new Tag[this.tagCount];
            for (int i = 0; i < this.tagCount; i++) {
                this.tagTable[i] = new Tag(input);
            }
        } else {
            this.tagTable = null;
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode nodeHeader;
        DefaultMutableTreeNode nodeTagTable;
        int lastPos;
        int diff;

        parentNode.add(nodeHeader = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.header.getStartPos(),
                this.header.getLength(),
                "Profile header")));
        this.header.generateTreeNode(nodeHeader);

        parentNode.add(nodeHeader = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = this.header.getStartPos() + this.header.getLength(),
                4,
                String.format("Tag count = %d", this.tagCount))));

        lastPos = lastPos + 4;

        ConcurrentSkipListMap<Long, RefItem> sortedMap = new ConcurrentSkipListMap<Long, RefItem>();
        for (int i = 0; i < this.tagTable.length; i++) {
            parentNode.add(nodeTagTable = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos + Tag.LENGTH * i,
                    Tag.LENGTH,
                    String.format("Tag[%d]", i))));
            this.tagTable[i].generateTreeNode(nodeTagTable);

            if (sortedMap.get(this.tagTable[i].Offset) == null) {
                RefItem refItem = new RefItem();
                refItem.i = i;
                refItem.tag = this.tagTable[i];
                sortedMap.put(refItem.tag.Offset, refItem);
            }
        }

        lastPos = lastPos + this.tagTable.length * Tag.LENGTH;
        for (RefItem ref : sortedMap.values()) {
            diff = (int) ((this.startPos + ref.tag.Offset) - lastPos);
            if (diff > 0) {
                Tool.generateTreeNode_Diff(
                        parentNode, lastPos, diff,
                        this.rawData, this.startPos);
            }

            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + (int) ref.tag.Offset,
                    (int) ref.tag.Size,
                    String.format("Data of Tag [%d]", ref.i))));
            lastPos = this.startPos + (int) ref.tag.Offset + (int) ref.tag.Size;
        }

        diff = (this.startPos + this.rawData.length) - lastPos;
        if (diff > 0) {
            Tool.generateTreeNode_Diff(
                    parentNode, lastPos, diff,
                    this.rawData, this.startPos);
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    private class RefItem {

        int i;
        Tag tag;
    }
}
