/*
 * ICCProfile.java    Nov 09, 2010, 21:39
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An ICC color profile, embedded in `APP2` segments: a header and a tag table.
///
/// @author Amos Shi
/// @see <a href="http://www.color.org/">INTERNATIONAL COLOR CONSORTIUM</a>
public class ICCProfile extends FileComponent implements GenerateTreeNode {

    /// The raw bytes of the profile.
    public final byte[] rawData;
    /// The profile header.
    public final Header header;
    /// Number of tags in the tag table.
    public final int tagCount;
    /// The tag table.
    public final Tag[] tagTable;

    /// Reads the profile.
    ///
    /// @param input Input stream of the the ICC profile
    /// @throws IOException if the stream cannot be read
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

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode nodeHeader = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.header.getStartPos(),
                this.header.getLength(),
                "Profile header"));
        parentNode.add(nodeHeader);
        this.header.generateTreeNode(nodeHeader);

        int lastPos = this.header.getStartPos() + this.header.getLength();
        nodeHeader = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos,
                4,
                "Tag count = %d".formatted(this.tagCount)));
        parentNode.add(nodeHeader);

        lastPos = lastPos + 4;

        // Tag data in the order of the offset; tags may share their data
        final SortedMap<Long, RefItem> sortedMap = new TreeMap<>();
        for (int i = 0; i < this.tagTable.length; i++) {
            final DefaultMutableTreeNode nodeTagTable = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos + Tag.LENGTH * i,
                    Tag.LENGTH,
                    "Tag[%d]".formatted(i)));
            parentNode.add(nodeTagTable);
            this.tagTable[i].generateTreeNode(nodeTagTable);

            sortedMap.putIfAbsent(this.tagTable[i].Offset, new RefItem(i, this.tagTable[i]));
        }

        lastPos = lastPos + this.tagTable.length * Tag.LENGTH;
        for (final RefItem ref : sortedMap.values()) {
            final int diff = (int) ((this.startPos + ref.tag().Offset) - lastPos);
            if (diff > 0) {
                generateTreeNodeDiff(parentNode, lastPos, diff, this.rawData, this.startPos);
            }

            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + (int) ref.tag().Offset,
                    (int) ref.tag().Size,
                    "Data of Tag [%d]".formatted(ref.i()))));
            lastPos = this.startPos + (int) ref.tag().Offset + (int) ref.tag().Size;
        }

        final int diff = (this.startPos + this.rawData.length) - lastPos;
        if (diff > 0) {
            generateTreeNodeDiff(parentNode, lastPos, diff, this.rawData, this.startPos);
        }
    }

    /// A tag with its index in the tag table.
    ///
    /// @param i Index in the tag table
    /// @param tag The tag
    private record RefItem(int i, Tag tag) {
    }
}
