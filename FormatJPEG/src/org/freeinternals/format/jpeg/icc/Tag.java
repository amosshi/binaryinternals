/*
 * Tag.java    Nov 09, 2010, 21:46
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;

/**
 *
 * @author Amos Shi
 */
public class Tag extends FileComponent {

    public final static int LENGTH = 12;
    public final String TagSignature;
    public final long Offset;
    public final long Size;
    public final int BasePos;
    public final TagData tagData;

    public Tag(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = LENGTH;

        this.TagSignature = input.readASCII(4);
        this.Offset = input.readUnsignedInt();
        this.Size = input.readUnsignedInt();
        this.BasePos = input.getOffset();

        byte[] dataBuf = new byte[(int) this.Size];
        System.arraycopy(input.getBuf(), (int) this.Offset, dataBuf, 0, dataBuf.length);
        this.tagData = Tag.parse(new PosDataInputStream(
                new PosByteArrayInputStream(dataBuf),
                input.getOffset() + (int) this.Offset));
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode nodeTagData;

        comp = new JTreeNodeFileComponent(
                this.getStartPos(),
                4,
                String.format("Signature = %s", this.TagSignature));
        comp.setDescription("Tag Signature");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                4,
                String.format("Offset = %d", this.Offset));
        comp.setDescription("Offset to beginning of tag data element");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 8,
                4,
                String.format("Size = %d", this.Size));
        comp.setDescription("Size of tag data element");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                (int) (this.BasePos + this.Offset),
                (int) this.Size,
                "Tag Data",
                ImageLoader.getShortcutIcon());
        parentNode.add(nodeTagData = new DefaultMutableTreeNode(comp));
        this.tagData.generateTreeNode(nodeTagData);
    }

    static TagData parse(final PosDataInputStream input) throws IOException {

        int tagType = input.readInt();
        input.reset();

        switch (tagType) {
            case TagType.textType:
                return new TagData_textType(input);
            case TagType.XYZType:
                return new TagData_XYZType(input);
            case TagType.viewingConditionsType:
                return new TagData_viewingConditionsType(input);
            case TagType.curveType:
                return new TagData_curveType(input);
            case TagType.measurementType:
                return new TagData_measurementType(input);
            case TagType.signatureType:
                return new TagData_signatureType(input);
            default:
                return new TagData(input);
        }
    }
}
