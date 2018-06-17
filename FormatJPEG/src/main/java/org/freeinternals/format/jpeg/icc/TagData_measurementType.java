/*
 * TagData_measurementType.java    Nov 27, 2010, 17:07
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
public class TagData_measurementType extends TagData {

    public final static int LENGTH = 36;
    public final long standardObserver;
    public final XYZNumber measurementBacking;
    public final long measurementGeometry;
    public final long measurementFlare;
    public final long standardIlluminant;

    /**
     *
     * @param input
     * @throws IOException
     */
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

            DefaultMutableTreeNode xyzNode;
            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("standard observer = %d", this.standardObserver));
            comp.setDescription(null);  //  TODO - load the values from table 40
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    startPos + 12,
                    XYZNumber.LENGTH,
                    "measurement backing");
            comp.setDescription("XYZ tristimulus values for measurement backing.");
            parentNode.add(xyzNode = new DefaultMutableTreeNode(comp));
            this.measurementBacking.generateTreeNode(xyzNode, this.startPos + 12);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 24,
                    4,
                    String.format("measurement geometry = %d", this.measurementGeometry));
            comp.setDescription(null);  //  TODO - load the values from table 41
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 28,
                    4,
                    String.format("measurement flare = %d", this.measurementFlare));
            comp.setDescription(null);  //  TODO - load the values from table 42
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 32,
                    4,
                    String.format("standard illuminant = %d", this.standardIlluminant));
            comp.setDescription(null);  //  TODO - load the values from table 43
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
