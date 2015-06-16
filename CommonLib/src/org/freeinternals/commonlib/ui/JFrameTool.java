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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Utility class for {@code JFrame}.
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JFrameTool {

    private JFrameTool() {
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

    public static void showPopup(JFrame frame, JPanel panel, String title) {
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
