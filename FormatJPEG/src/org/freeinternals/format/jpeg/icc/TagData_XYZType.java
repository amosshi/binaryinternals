/*
 * TagData_XYZType.java    Nov 22, 2010, 23:16
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class TagData_XYZType extends TagData {

    public final XYZNumber[] data;

    /**
     *
     * @param input
     * @throws IOException
     */
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
            DefaultMutableTreeNode xyzNode;
            JTreeNodeFileComponent comp;

            this.generateTreeNode_TagDataType(parentNode);
            for (int i = 0; i < this.data.length; i++) {
                comp = new JTreeNodeFileComponent(
                        startPos + 8 + XYZNumber.LENGTH * i,
                        XYZNumber.LENGTH,
                        String.format("%s [%d]", XYZNumber.class.getSimpleName(), i));
                comp.setDescription(this.data[i].toString());
                parentNode.add(xyzNode = new DefaultMutableTreeNode(comp));
                this.data[i].generateTreeNode(xyzNode, this.startPos + 8 + XYZNumber.LENGTH * i);
            }
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
