/*
 * TagData_XYZType.java    Nov 22, 2010, 23:16
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class TagData_XYZType extends TagData {

    public final XYZNumber[] data;

    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_XYZType(final PosDataInputStream input) throws IOException {
        super(input);
        if (this.length > 8) {
            int dataLength = this.length - 8;
            int dataCount = dataLength / XYZNumber.LENGTH;
            this.data = new XYZNumber[dataCount];
            for (int i = 0; i < dataCount; i++) {
                this.data[i] = new XYZNumber(input);
            }
        } else {
            this.data = null;
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length > 8 && this.data != null) {

            this.generateTreeNode_TagDataType(parentNode);
            for (int i = 0; i < this.data.length; i++) {
                final JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                        startPos + 8 + XYZNumber.LENGTH * i,
                        XYZNumber.LENGTH,
                        "%s [%d]".formatted(XYZNumber.class.getSimpleName(), i));
                comp.setDescription(this.data[i].toString());
                final DefaultMutableTreeNode xyzNode = new DefaultMutableTreeNode(comp);
                parentNode.add(xyzNode);
                this.data[i].generateTreeNode(xyzNode, this.startPos + 8 + XYZNumber.LENGTH * i);
            }
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
