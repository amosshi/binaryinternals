/*
 * TagData_signatureType.java    Nov 27, 2010, 17:25
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

/// `signatureType`: a 4-byte signature.
///
/// @author Amos Shi
public class TagData_signatureType extends TagData {

    /// Length of the tag data in bytes.
    public static final int LENGTH = 12;
    /// The signature.
    public final String signature;

    /// Reads the tag data.
    ///
    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_signatureType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            throw new FileFormatException("signatureType length is %d, expected %d.".formatted(super.length, LENGTH));
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
                    "signature = %s".formatted(this.signature));
            comp.setDescription("Four-byte signature.");
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }
}
