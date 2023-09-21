/*
 * TagData.java    Nov 22, 2010, 23:12
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class TagData extends FileComponent implements GenerateTreeNode {

    public final int tagType;

    /**
     * 
     * @param input The input stream for this Tag Data only
     * @throws IOException Parse file failed
     */
    public TagData(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.tagType = input.readInt();

        // Reserved bytes
        BytesTool.skip(input, 4);
    }

    public String getTagType() {
        return TagType.getTypeSignature(this.tagType);
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_TagDataType(parentNode);
        if (this.length > 8) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + 8,
                    this.length - 8,
                    this.getTagType())));
        }
    }

    public void generateTreeNode_TagDataType(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.startPos,
                4,
                String.format("Signature = %s", TagType.getTypeSignature(this.tagType)));
        comp.setDescription("Identify what kind of data is contained within a tag.");
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.startPos + 4,
                4,
                "Reserved");
        comp.setDescription("Reserved for future expansion and must be set to 0.");
        parentNode.add(new DefaultMutableTreeNode(comp));
    }
}
