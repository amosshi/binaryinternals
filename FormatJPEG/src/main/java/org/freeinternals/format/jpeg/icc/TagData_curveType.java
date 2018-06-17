/*
 * TagData_curveType.java    Nov 27, 2010, 17:47
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
 * TODO - Study cure
 *
 * @author Amos Shi
 */
public class TagData_curveType extends TagData {

    public final long count;
    public final int curveValues[];

    /**
     *
     * @param input
     * @throws IOException
     */
    public TagData_curveType(final PosDataInputStream input) throws IOException {
        super(input);

        this.count = input.readUnsignedInt();
        if (this.count == 0) {
            this.curveValues = new int[1];
            this.curveValues[0] = 0;
        } else if (this.count == 1) {
            this.curveValues = new int[1];
            this.curveValues[0] = 0;   // TODO - Read page 53
        } else {
            this.curveValues = new int[((int) this.count)];
            for (int i = 0; i < this.count; i++) {
                this.curveValues[i] = input.readUnsignedShort();
            }
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length > 8) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp;
            DefaultMutableTreeNode node;

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("count = %d", this.count));
            comp.setDescription("Count value specifying the number of entries (<code>n</code>) that follow.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            if (this.count > 1) {
                comp = new JTreeNodeFileComponent(
                        this.startPos + 12,
                        ((int) this.count) * 2,
                        "curve values");
                comp.setDescription("Actual curve values starting with the zeroth entry and ending with the entry <code>n-1</code>.");
                parentNode.add(node = new DefaultMutableTreeNode(comp));

                for (int i = 0; i < this.count; i++) {
                    node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            this.startPos + 12 + i * 2,
                            2,
                            String.format("value [%d] = %d", i, this.curveValues[i]))));
                }
            }
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
