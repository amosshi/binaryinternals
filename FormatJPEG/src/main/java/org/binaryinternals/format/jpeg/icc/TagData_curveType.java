/*
 * TagData_curveType.java    Nov 27, 2010, 17:47
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// `curveType`, see ICC.1 `curveType`: a one-dimensional curve, given by its `count`:
///
/// - `0`: the identity, no data follows
/// - `1`: a gamma value, as a `u8Fixed8Number` (the value divided by 256)
/// - `n`: a table of `n` 16-bit values
///
/// @author Amos Shi
public class TagData_curveType extends TagData {

    public final long count;
    public final int[] curveValues;

    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_curveType(final PosDataInputStream input) throws IOException {
        super(input);

        this.count = input.readUnsignedInt();
        if (this.count == 0) {
            this.curveValues = new int[1];
            this.curveValues[0] = 0;
        } else if (this.count == 1) {
            this.curveValues = new int[1];
            this.curveValues[0] = input.readUnsignedShort();   // Gamma, u8Fixed8Number
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

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    "count = %d".formatted(this.count));
            comp.setDescription("Count value specifying the number of entries (<code>n</code>) that follow.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            if (this.count == 1) {
                comp = new JTreeNodeFileComponent(
                        this.startPos + 12,
                        2,
                        "gamma = %.4f (0x%04X)".formatted(this.curveValues[0] / 256.0, this.curveValues[0]));
                comp.setDescription("Gamma value of the curve, an unsigned 8.8 fixed point number (<code>u8Fixed8Number</code>).");
                parentNode.add(new DefaultMutableTreeNode(comp));
            } else if (this.count > 1) {
                comp = new JTreeNodeFileComponent(
                        this.startPos + 12,
                        ((int) this.count) * 2,
                        "curve values");
                comp.setDescription("Actual curve values starting with the zeroth entry and ending with the entry <code>n-1</code>.");
                final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
                parentNode.add(node);

                for (int i = 0; i < this.count; i++) {
                    node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            this.startPos + 12 + i * 2,
                            2,
                            "value [%d] = %d".formatted(i, this.curveValues[i]))));
                }
            }
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
