/*
 * JFrameTool.java    April 06, 2009, 01:38
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
    /**
     * Max length for tree node string.
     *
     * @see #left(String)
     */
    public static final int TREENODE_STRING_MAXLEN = 64;

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

    private static final Map<String, Icon> iconCache = new HashMap<>();

    private static Icon icon(String url) {
        return iconCache.computeIfAbsent(url, k -> new ImageIcon(UITool.class.getResource(url)));
    }

    /**
     * Icon for access flags.
     *
     * @return Icon for access flags
     * @see <a href="https://icons8.com/icon/En14xvMmiGjB/approval">Approval</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4AccessFlag() {
        return icon("/image/icons8-approval-16.png");
    }

    /**
     * Icon for annotations.
     *
     * @return Icon for annotations
     */
    public static Icon icon4Annotations() {
        return icon("/image/icons8-bookmark-16.png");
    }

    /**
     * Icon for array.
     *
     * @return Icon for array
     * @see <a href="https://icons8.com/icon/78816/view-array">View Array</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Array() {
        return icon("/image/icons8-view-array-16.png");
    }

    /**
     * Icon for binary file.
     *
     * @return Icon for binary file
     * @see <a href="https://icons8.com/icon/38992/binary-file">Binary File</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4BinaryFile() {
        return icon("/image/icons8-binary-file-20.png");
    }

    /**
     * Icon for checksum.
     *
     * @return Icon for checksum
     * @see <a href="https://icons8.com/icon/sz8cPVwzLrMP/check-mark">Check
     * Mark</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Checksum() {
        return icon("/image/icons8-check-mark-16.png");
    }

    /**
     * Icon for counter.
     *
     * @return Icon for counter
     * @see <a href="https://icons8.com/icon/2U6ROkjIrXIA/abacus">Abacus</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Counter() {
        return icon("/image/icons8-abacus-16.png");
    }

    /**
     * Icon for raw Data.
     *
     * @return Icon for raw data
     * @see <a href="https://icons8.com/icon/84736/blockchain-technology">Blockchain Technology</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Data() {
        return icon("/image/icons8-blockchain-technology-16.png");
    }

    /**
     * Icon for DEX file.
     *
     * @return Icon for dex file
     * @see <a href="https://icons8.com/icon/38933/apk">APK</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Dex() {
        return icon("/image/icons8-apk-20.png");
    }

    /**
     * Icon for endian.
     *
     * @return Icon for endian
     * @see <a href="https://icons8.com/icon/Xf1Gx1HbxVsm/up-down-arrow">Up Down
     * Arrow</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Endian() {
        return icon("/image/icons8-up-down-arrow-16.png");
    }
    
    /**
     * Icon for field.
     *
     * @return Icon for field
     * @see <a href="https://icons8.com/icon/45099/play-property">Play Property</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Field() {
        return icon("/image/icons8-play-property-16.png");
    }

    /**
     * Icon for length.
     *
     * @return Icon for length
     * @see <a href="https://icons8.com/icon/44699/length">Length</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Length() {
        return icon("/image/icons8-length-16.png");
    }

    /**
     * Icon for Index.
     *
     * @return Icon for Index
     * @see <a href="https://icons8.com/icon/79485/one-finger">One Finger</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Index() {
        return icon("/image/icons8-one-finger-16.png");
    }

    /**
     * Icon for Java.
     *
     * @return Icon for Java
     */
    public static Icon icon4Java() {
        return icon("/image/icons8-java-20.png");
    }

    /**
     * Icon for Method.
     *
     * @return Icon for Method
     * @see <a href="https://icons8.com/icon/e5uh9CTQUVii/mechanistic-analysis">Mechanistic Analysis</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Method() {
        return icon("/image/icons8-mechanistic-analysis-16.png");
    }

    /**
     * Icon for Offset / Location / Index.
     *
     * @return Icon for Offset
     * @see <a href="https://icons8.com/icon/2gsR2g07AQvu/map-pin">Map Pin</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Offset() {
        return icon("/image/icons8-map-pin-16.png");
    }

    /**
     * Icon for parameter, of a method.
     *
     * @return Icon for Offset
     * @see <a href="https://icons8.com/icon/Pohj4RQVOJYd/filter">Filter</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Parameter() {
        return icon("/image/icons8-filter-16.png");
    }

    /**
     * Icon for magic number.
     *
     * @return Icon for magic number
     * @see <a href="https://icons8.com/icon/q8t3iE9rg6YF/magic-wand">Magic
     * Wand</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Magic() {
        return icon("/image/icons8-magic-wand-16.png");
    }

    /**
     * Icon for return type, of a method.
     *
     * @return return type icon
     *
     * @see <a href="https://icons8.com/icon/13107/return">Return</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Return() {
        return icon("/image/icons8-return-16.png");
    }

    /**
     * Icon for shortcut.
     *
     * @return Shortcut icon
     *
     * @see <a href="https://icons8.com/icon/i1z7pQ2orcJk/shortcut">Shortcut</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Shortcut() {
        return icon("/image/icons8-shortcut-16.png");
    }

    /**
     * Icon for signature.
     *
     * @return Icon for signature
     * @see
     * <a href="https://icons8.com/icon/bmicUxC0XDNt/signature">Signature</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Signature() {
        return icon("/image/icons8-signature-16.png");
    }

    /**
     * Icon for Size.
     *
     * @return Shortcut icon
     * @see <a href="https://icons8.com/icon/d8VomliGByyY/page-size">Page
     * Size</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Size() {
        return icon("/image/icons8-page-size-16.png");
    }

    /**
     * Icon for Tag or Type.
     *
     * @return tag icon
     * @see <a href="https://icons8.com/icon/pmzH4rF8Lrv9/tag">Tag</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Tag() {
        return icon("/image/icons8-tag-16.png");
    }

    /**
     * Icon for version.
     *
     * @return Icon for versions
     * @see <a href="https://icons8.com/icon/21933/versions">Versions</a> icon
     * by <a href="https://icons8.com">Icons8</a>
     * @see <a href="https://icons8.com/icon/59954/versions">Versions</a> icon
     * by <a href="https://icons8.com">Icons8</a>
     */
    public static Icon icon4Versions() {
        return icon("/image/icons8-versions-16.png");
    }

    /**
     * Get left part of string for tree node.
     *
     * @param s The String to get left part
     * @return Left part of string
     */
    public static String left(String s) {
        return left(s, TREENODE_STRING_MAXLEN);
    }

    /**
     * Get left part of string.
     *
     * @param s The String to get left part
     * @param length Length to get
     * @return Left part of string
     */
    public static String left(String s, int length) {
        if (s == null) {
            return s;
        } else if (s.length() < length) {
            return s;
        } else {
            return s.substring(0, length - 1) + " ...";
        }
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
