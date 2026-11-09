/*
 * XYZNumber.java    Nov 10, 2010, 09:17
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The type `XYZNumber`: a set of three fixed signed 4-byte numbers, the CIEXYZ tristimulus values.
///
/// @author Amos Shi
public class XYZNumber {

    /// Length in bytes.
    public static final int LENGTH = 12;
    /// CIE X.
    public final s15Fixed16Number x;
    /// CIE Y.
    public final s15Fixed16Number y;
    /// CIE Z.
    public final s15Fixed16Number z;

    /// Reads the value.
    ///
    /// @param input Input stream of the the ICC profile
    /// @throws IOException if the stream cannot be read
    public XYZNumber(final PosDataInputStream input) throws IOException {
        this.x = new s15Fixed16Number(input);
        this.y = new s15Fixed16Number(input);
        this.z = new s15Fixed16Number(input);
    }

    @Override
    public String toString() {
        return "[x=%s, y=%s, z=%s]".formatted(
                this.x.toString(), this.y.toString(), this.z.toString());
    }

    /// Add the tree nodes of the three numbers.
    ///
    /// @param parentNode Parent tree node
    /// @param startPos Start position of the value
    public void generateTreeNode(DefaultMutableTreeNode parentNode, int startPos) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                4,
                "x = %s".formatted(this.x.toString()))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                4,
                "y = %s".formatted(this.y.toString()))));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 8,
                4,
                "z = %s".formatted(this.z.toString()))));
    }
}
