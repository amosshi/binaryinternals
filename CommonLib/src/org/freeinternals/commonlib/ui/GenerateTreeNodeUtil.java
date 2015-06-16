/*
 * GenerateTreeNodeUtil.java    Nov 11, 2010, 23:32
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Amos Shi
 */
public class GenerateTreeNodeUtil {

    public static void generateTreeNode_Diff(
            DefaultMutableTreeNode parentNode, int lastEnd, int diff,
            byte[] buff, int buffStartPos) {
        String diffStr;

        if (GenerateTreeNodeUtil.isBuffEmpty(buff, lastEnd - buffStartPos, diff - 1)) {
            diffStr = String.format("Empty [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        } else {
            diffStr = String.format("Unknown [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        }
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastEnd,
                diff,
                diffStr)));
    }

    private static boolean isBuffEmpty(byte[] buff, int startPos, int length) {
        boolean result = false;

        if (buff[startPos] == 0x00 || buff[startPos] == ((byte) 0xFF)) {
            result = true;
            for (int i = 1; i <= length; i++) {
                if (buff[startPos + i] != buff[startPos]) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

}
