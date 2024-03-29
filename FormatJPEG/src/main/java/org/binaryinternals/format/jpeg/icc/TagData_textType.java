/*
 * TagData_textType.java    Nov 22, 2010, 23:16
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class TagData_textType extends TagData {

    public final String text;

    /**
     *
     * @param input Input stream of the file
     * @throws IOException Parse file failed
     */
    public TagData_textType(final PosDataInputStream input) throws IOException {
        super(input);
        StringBuilder sb = new StringBuilder(this.length - 8);
        for (int i = 8; i < this.length; i++) {
            sb.append((char) input.readByte());
        }
        this.text = sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length > 8) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    this.length - 8,
                    this.getTagType());
            comp.setDescription(this.text);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
