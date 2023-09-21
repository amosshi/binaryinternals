/*
 * FileData.java    Nov 05, 2010, 23:17
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class FileData extends FileComponent implements GenerateTreeNode {

    private final boolean isCompressedData;

    FileData(final PosDataInputStream pDis, final int length, final boolean isCompressedData) throws IOException {
        super.startPos = pDis.getPos();
        super.length = length;
        this.isCompressedData = isCompressedData;

        if (length > 0) {
            BytesTool.skip(pDis, length);
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        String nodeName;

        if (isCompressedData) {
            nodeName = "Compressed Data";
        } else {
            nodeName = "File Data";
        }
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos(),
                this.getLength(),
                nodeName)));
    }
}
