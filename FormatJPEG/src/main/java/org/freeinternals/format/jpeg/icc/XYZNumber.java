/*
 * XYZNumber.java    Nov 10, 2010, 09:17
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
public class XYZNumber {

    public static final int LENGTH = 12;
    public final s15Fixed16Number x;
    public final s15Fixed16Number y;
    public final s15Fixed16Number z;

    public XYZNumber(final PosDataInputStream input) throws IOException {
        this.x = new s15Fixed16Number(input);
        this.y = new s15Fixed16Number(input);
        this.z = new s15Fixed16Number(input);
    }

    @Override
    public String toString() {
        return String.format("[x=%s, y=%s, z=%s]",
                this.x.toString(), this.y.toString(), this.z.toString());
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode, int startPos) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                4,
                String.format("x = %s", this.x.toString()))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                4,
                String.format("y = %s", this.y.toString()))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 8,
                4,
                String.format("z = %s", this.z.toString()))));
    }
}
