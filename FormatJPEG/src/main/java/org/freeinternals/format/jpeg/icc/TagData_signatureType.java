/*
 * TagData_signatureType.java    Nov 27, 2010, 17:25
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
public class TagData_signatureType extends TagData {

    public final static int LENGTH = 12;
    public final String signature;

    /**
     *
     * @param input
     * @throws IOException
     */
    public TagData_signatureType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            // TODO - throw error
        }

        this.signature = input.readASCII(4);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length == LENGTH) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("signature = %s", this.signature));
            comp.setDescription("Four-byte signature.");
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
