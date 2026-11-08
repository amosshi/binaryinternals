/*
 * TagData_measurementType.java    Nov 27, 2010, 17:07
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
public class TagData_measurementType extends TagData {

    public static final int LENGTH = 36;
    public final long standardObserver;
    public final XYZNumber measurementBacking;
    public final long measurementGeometry;
    public final long measurementFlare;
    public final long standardIlluminant;

    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_measurementType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            // TODO - throw error
        }

        this.standardObserver = input.readUnsignedInt();
        this.measurementBacking = new XYZNumber(input);
        this.measurementGeometry = input.readUnsignedInt();
        this.measurementFlare = input.readUnsignedInt();
        this.standardIlluminant = input.readUnsignedInt();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length > 8) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    "standard observer = %d".formatted(this.standardObserver));
            comp.setDescription(null);  //  TODO - load the values from table 40
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    startPos + 12,
                    XYZNumber.LENGTH,
                    "measurement backing");
            comp.setDescription("XYZ tristimulus values for measurement backing.");
            final DefaultMutableTreeNode xyzNode = new DefaultMutableTreeNode(comp);
            parentNode.add(xyzNode);
            this.measurementBacking.generateTreeNode(xyzNode, this.startPos + 12);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 24,
                    4,
                    "measurement geometry = %d".formatted(this.measurementGeometry));
            comp.setDescription(null);  //  TODO - load the values from table 41
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 28,
                    4,
                    "measurement flare = %d".formatted(this.measurementFlare));
            comp.setDescription(null);  //  TODO - load the values from table 42
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 32,
                    4,
                    "standard illuminant = %d".formatted(this.standardIlluminant));
            comp.setDescription(null);  //  TODO - load the values from table 43
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
