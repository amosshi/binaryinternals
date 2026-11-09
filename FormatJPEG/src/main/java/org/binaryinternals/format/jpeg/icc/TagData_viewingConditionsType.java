/*
 * TagData_viewingConditionsType.java    Nov 27, 2010, 16:18
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class TagData_viewingConditionsType extends TagData {

    public static final int LENGTH = 36;
    public final XYZNumber illuminant;
    public final XYZNumber surround;
    public final long illuminantType;

    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_viewingConditionsType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            throw new FileFormatException("viewingConditionsType length is %d, expected %d.".formatted(super.length, LENGTH));
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
            xyzNode = new DefaultMutableTreeNode(comp);
            parentNode.add(xyzNode);
            this.illuminant.generateTreeNode(xyzNode, this.startPos + 8);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8 + XYZNumber.LENGTH,
                    XYZNumber.LENGTH,
                    "surround");
            comp.setDescription("CIE ’absolute’ XYZ values for surround (in which Y is in cd/m<sup>2</sup>)");
            xyzNode = new DefaultMutableTreeNode(comp);
            parentNode.add(xyzNode);
            this.surround.generateTreeNode(xyzNode, this.startPos + 8 + XYZNumber.LENGTH);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8 + XYZNumber.LENGTH + XYZNumber.LENGTH,
                    4,
                    "illuminant type = %d".formatted(this.illuminantType));
            comp.setDescription(null);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
