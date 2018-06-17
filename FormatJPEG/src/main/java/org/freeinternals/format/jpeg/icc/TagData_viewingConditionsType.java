/*
 * TagData_viewingConditionsType.java    Nov 27, 2010, 16:18
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
public class TagData_viewingConditionsType extends TagData {

    public final static int LENGTH = 36;
    public final XYZNumber illuminant;
    public final XYZNumber surround;
    public final long illuminantType;

    /**
     *
     * @param input
     * @throws IOException
     */
    public TagData_viewingConditionsType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            // TODO - throw error
        }

        this.illuminant = new XYZNumber(input);
        this.surround = new XYZNumber(input);
        this.illuminantType = input.readUnsignedInt();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length == LENGTH) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp;
            DefaultMutableTreeNode xyzNode;

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    XYZNumber.LENGTH,
                    "illuminant");
            comp.setDescription("CIE ’absolute’ XYZ values for illuminant (in which Y is in cd/m<sup>2</sup>)");
            parentNode.add(xyzNode = new DefaultMutableTreeNode(comp));
            this.illuminant.generateTreeNode(xyzNode, this.startPos + 8);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8 + XYZNumber.LENGTH,
                    XYZNumber.LENGTH,
                    "surround");
            comp.setDescription("CIE ’absolute’ XYZ values for surround (in which Y is in cd/m<sup>2</sup>)");
            parentNode.add(xyzNode = new DefaultMutableTreeNode(comp));
            this.surround.generateTreeNode(xyzNode, this.startPos + 8 + XYZNumber.LENGTH);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8 + XYZNumber.LENGTH + XYZNumber.LENGTH,
                    4,
                    String.format("illuminant type = %d", this.illuminantType));
            comp.setDescription(null);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
