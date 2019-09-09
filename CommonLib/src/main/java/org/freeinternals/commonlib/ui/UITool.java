/*
 * JFrameTool.java    April 06, 2009, 01:38
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
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
public class UITool {

    private UITool() {
    }

    /**
     * Set a {@code JFrame} window to screen center.
     *
     * @param f The target window
     */
    public static void centerJFrame(final JFrame f) {
        // Set main window size
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        f.setSize(
                (int) (d.getWidth() * 0.7),
                (int) (d.getHeight() * 0.7));

        // Center the main window
        f.setLocationRelativeTo(null);
    }
    

    /**
     *
     * @param parentNode
     * @param lastEnd
     * @param diff
     * @param buff
     * @param buffStartPos
     */
    public static void generateTreeNode_Diff(
            DefaultMutableTreeNode parentNode,
            int lastEnd,
            int diff,
            byte[] buff, int buffStartPos) {
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
    
    
    public static Icon getShortcutIcon() {
        return UIManager.getIcon("InternalFrame.maximizeIcon");
    }
    

    public static void showPopup(final JFrame frame, final JPanel panel, final String title) {
        if (frame == null || panel == null) {
            return;
        }

        final JDialog popup = new JDialog(frame, title);
        popup.setSize(
                (int) Math.floor(frame.getWidth() * 0.8),
                (int) Math.floor(frame.getHeight() * 0.8));
        popup.setLayout(new BorderLayout());
        popup.add(panel, BorderLayout.CENTER);
        popup.setLocationRelativeTo(frame);
        popup.setVisible(true);
    }
}
