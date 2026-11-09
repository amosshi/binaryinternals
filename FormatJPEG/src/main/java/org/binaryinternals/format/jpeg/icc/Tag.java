/*
 * Tag.java    Nov 09, 2010, 21:46
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// A tag table entry of an ICC profile: the tag signature, and the offset and size of the tag data.
///
/// @author Amos Shi
public class Tag extends FileComponent {

    /// Length of a tag table entry in bytes.
    public static final int LENGTH = 12;
    /// Tag signature.
    public final String TagSignature;
    /// Offset to the beginning of the tag data, from the profile start.
    public final long Offset;
    /// Size of the tag data in bytes.
    public final long Size;
    /// Start position of the profile, the base of [#Offset].
    public final int BasePos;
    /// The tag data.
    public final TagData tagData;

    /// Reads a tag table entry and its tag data.
    ///
    /// @param input Input stream of the the ICC profile
    /// @throws IOException if the stream cannot be read
    public Tag(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = LENGTH;

        this.TagSignature = input.readASCII(4);
        this.Offset = input.readUnsignedInt();
        this.Size = input.readUnsignedInt();
        this.BasePos = input.getOffset();

        // copyOfRange would pad a tag beyond the profile with zeros
        Objects.checkFromIndexSize(this.Offset, this.Size, input.getBuf().length);
        final byte[] dataBuf = Arrays.copyOfRange(input.getBuf(), (int) this.Offset, (int) (this.Offset + this.Size));
        this.tagData = Tag.parse(new PosDataInputStream(
                new PosByteArrayInputStream(dataBuf),
                input.getOffset() + (int) this.Offset));
    }

    /// Add the tree nodes of the tag.
    ///
    /// @param parentNode Parent tree node
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.getStartPos(),
                4,
                "Signature = %s".formatted(this.TagSignature));
        comp.setDescription("Tag Signature");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                4,
                "Offset = %d".formatted(this.Offset));
        comp.setDescription("Offset to beginning of tag data element");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 8,
                4,
                "Size = %d".formatted(this.Size));
        comp.setDescription("Size of tag data element");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                (int) (this.BasePos + this.Offset),
                (int) this.Size,
                "Tag Data",
                Icons.Shortcut, null);
        final DefaultMutableTreeNode nodeTagData = new DefaultMutableTreeNode(comp);
        parentNode.add(nodeTagData);
        this.tagData.generateTreeNode(nodeTagData);
    }

    static TagData parse(final PosDataInputStream input) throws IOException {

        int tagType = input.readInt();
        input.reset();

        try {
            return switch (tagType) {
                case TagType.textType -> new TagData_textType(input);
                case TagType.XYZType -> new TagData_XYZType(input);
                case TagType.viewingConditionsType -> new TagData_viewingConditionsType(input);
                case TagType.curveType -> new TagData_curveType(input);
                case TagType.measurementType -> new TagData_measurementType(input);
                case TagType.signatureType -> new TagData_signatureType(input);
                default -> new TagData(input);
            };
        } catch (FileFormatException _) {
            // Malformed tag data: show it as raw bytes, keeping the rest of the profile
            input.reset();
            return new TagData(input);
        }
    }
}
