/*
 * JFrameTool.java    April 06, 2009, 01:38
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
     * Icon for counter.
     *
     * @return Icon for counter
     * @see <a href="https://icons8.com/icon/2U6ROkjIrXIA/abacus">Abacus</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Counter() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-abacus-16.png"));
    }

    /**
     * Icon for DEX file.
     *
     * @return Icon for dex file
     * @see <a href="https://icons8.com/icon/38933/apk">APK</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Dex() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-apk-20.png"));
    }

    /**
     * Icon for Java.
     *
     * @return Icon for Java
     */
    public static Icon icon4Java() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-java-20.png"));
    }

    /**
     * Icon for magic number.
     *
     * @return Icon for magic number
     * @see <a href="https://icons8.com/icon/q8t3iE9rg6YF/magic-wand">Magic Wand</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Magic() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-magic-wand-16.png"));
    }

    /**
     * Icon for shortcut.
     *
     * @return Shortcut icon
     *
     * @see <a href="https://icons8.com/icon/78265/shortcut">Shortcut</a> icon
     * by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Shortcut() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-shortcut-16.png"));
    }

    /**
     * Icon for version.
     *
     * @return Icon for versions
     * @see <a href="https://icons8.com/icon/21933/versions">Versions</a> icon by <a href="https://icons8.com">Icons8</a>
     * @see <a href="https://icons8.com/icon/59954/versions">Versions</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Versions() {
        return new ImageIcon(UITool.class.getResource("/image/icons8-versions-16.png"));
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
