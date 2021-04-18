/*
 * JFrameTool.java    April 06, 2009, 01:38
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;

/**
 * Utility class for UI.
 *
 * @author Amos Shi
 */
public final class UITool {

    /**
     * Size ratio of the pop-up window and its parent.
     */
    public static final float POPUP_RATIO = 0.8f;

    private UITool() {
    }

    /**
     * Generate tree node for difference.
     *
     * @param parentNode Parent tree node
     * @param lastEnd Last end
     * @param diff Difference
     * @param buff Byte array data
     * @param buffStartPos Buffer start position
     */
    public static void generateTreeNodeDiff(
            final DefaultMutableTreeNode parentNode,
            final int lastEnd,
            final int diff,
            final byte[] buff, final int buffStartPos) {
        String diffStr;

        if (BytesTool.isByteArrayEmpty(buff, lastEnd - buffStartPos, diff - 1)) {
            diffStr = String.format("Empty [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        } else {
            diffStr = String.format("Unknown [0x%04X, 0x%04X] length = %d", lastEnd, lastEnd + diff - 1, diff);
        }
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastEnd,
                diff,
                diffStr)));
    }

    /**
     * Get Java system default icon for shortcut.
     *
     * @return Shortcut icon in Java
     */
    public static Icon getShortcutIcon() {
        return UIManager.getIcon("InternalFrame.maximizeIcon");
    }

    /**
     * Show a popup window with given message.
     *
     * @param frame Parent window
     * @param panel Content in panel
     * @param title Popup window title
     */
    public static void showPopup(final JFrame frame, final JPanel panel, final String title) {
        if (frame == null || panel == null) {
            return;
        }

        final JDialog popup = new JDialog(frame, title);
        popup.setSize(
                (int) Math.floor(frame.getWidth() * POPUP_RATIO),
                (int) Math.floor(frame.getHeight() * POPUP_RATIO));
        popup.setLayout(new BorderLayout());
        popup.add(panel, BorderLayout.CENTER);
        popup.setLocationRelativeTo(frame);
        popup.setVisible(true);
    }
}
