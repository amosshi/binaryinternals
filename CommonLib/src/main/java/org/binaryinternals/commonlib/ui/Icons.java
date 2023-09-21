/*
 * Icons.java    Sep 21, 2021
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib.ui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Icons.
 *
 * @author amosshi
 */
@SuppressWarnings({"java:S115"})
public enum Icons {

    /**
     * Icon for access flags.
     *
     * @see <a href="https://icons8.com/icon/En14xvMmiGjB/approval">Approval</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    AccessFlag("icons8-approval-16.png"),
    /**
     * Icon for annotations.
     */
    Annotations("icons8-bookmark-16.png"),
    /**
     * Icon for array.
     *
     * @see <a href="https://icons8.com/icon/78816/view-array">View Array</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Array("icons8-view-array-16.png"),
    /**
     * Icon for binary file.
     *
     * @see <a href="https://icons8.com/icon/38992/binary-file">Binary File</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    BinaryFile("icons8-binary-file-20.png"),
    /**
     * Icon for Calendar / Date.
     *
     * @see <a href="https://icons8.com/icon/84997/calendar">Calendar</a> icon
     * by <a href="https://icons8.com">Icons8</a>
     */
    Calendar("icons8-calendar-16.png"),
    /**
     * Icon for checksum.
     *
     * @see <a href="https://icons8.com/icon/sz8cPVwzLrMP/check-mark">Check
     * Mark</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Checksum("icons8-check-mark-16.png"),

    /**
     * Icon for Java Class.
     */
    Class("icons8-java-16.png"),
    /**
     * Icon for Constant value.
     *
     * @see <a href="https://icons8.com/icon/22285/no-edit">No Edit</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Constant("icons8-no-edit-16.png"),
    /**
     * Icon for counter.
     *
     * @see <a href="https://icons8.com/icon/2U6ROkjIrXIA/abacus">Abacus</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Counter("icons8-abacus-16.png"),
    /**
     * Icon for raw Data.
     *
     * @see
     * <a href="https://icons8.com/icon/84736/blockchain-technology">Blockchain
     * Technology</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Data("icons8-blockchain-technology-16.png"),
    /**
     * Icon for Descriptor.
     *
     * @see <a href="https://icons8.com/icon/20843/information">Information</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Descriptor("icons8-information-16.png"),
    /**
     * Icon for DEX file.
     *
     * @see <a href="https://icons8.com/icon/38933/apk">APK</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Dex("icons8-apk-20.png"),
    /**
     * Icon for Empty.
     *
     * @see <a href="https://icons8.com/icon/96204/empty-battery">Empty Battery</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Empty("icons8-empty-battery-16.png"),
    /**
     * Icon for Exception.
     *
     * @see <a href="https://icons8.com/icon/110759/flash-on">Flash On</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Exception("icons8-flash-on-16.png"),
    /**
     * Icon for endian.
     *
     * @see <a href="https://icons8.com/icon/Xf1Gx1HbxVsm/up-down-arrow">Up Down
     * Arrow</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Endian("icons8-up-down-arrow-16.png"),
    /**
     * Icon for field.
     *
     * @see <a href="https://icons8.com/icon/45099/play-property">Play
     * Property</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Field("icons8-play-property-16.png"),
    /**
     * Icon for length.
     *
     * @see <a href="https://icons8.com/icon/44699/length">Length</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Length("icons8-length-16.png"),
    /**
     * Icon for Index.
     *
     * @see <a href="https://icons8.com/icon/79485/one-finger">One Finger</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Index("icons8-one-finger-16.png"),
    /**
     * Icon for Java.
     */
    Java("icons8-java-20.png"),
    /**
     * Icon for Kind / Type.
     *
     * @see <a href="https://icons8.com/icon/54136/category">Category</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Kind("icons8-category-16.png"),
    /**
     * Icon for Method.
     *
     * @see
     * <a href="https://icons8.com/icon/e5uh9CTQUVii/mechanistic-analysis">Mechanistic
     * Analysis</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Method("icons8-mechanistic-analysis-16.png"),
    /**
     * Icon for Module.
     *
     * @see <a href="https://icons8.com/icon/6OXjREBZeUan/module">Module</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Module("icons8-module-16.png"),
    /**
     * Icon for Name.
     *
     * @see <a href="https://icons8.com/icon/zbNHwSyrej7I/name-tag">Name Tag</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Name("icons8-name-tag-16.png"),
    /**
     * Icon for Offset / Location.
     *
     * @see <a href="https://icons8.com/icon/2gsR2g07AQvu/map-pin">Map Pin</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Offset("icons8-map-pin-16.png"),
    /**
     * Icon for package.
     *
     * @see <a href="https://icons8.com/icon/t81YhQKj04X8/package">Package</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Package("icons8-package-16.png"),
    /**
     * Icon for parameter, of a method.
     *
     * @see <a href="https://icons8.com/icon/Pohj4RQVOJYd/filter">Filter</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Parameter("icons8-filter-16.png"),
    /**
     * Icon for magic number.
     *
     * @see <a href="https://icons8.com/icon/q8t3iE9rg6YF/magic-wand">Magic
     * Wand</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Magic("icons8-magic-wand-16.png"),
    /**
     * Icon for Max.
     */
    Max("icons8-maxcdn-16.png"),
    /**
     * Icon for return type, of a method.
     *
     * @see <a href="https://icons8.com/icon/13107/return">Return</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Return("icons8-return-16.png"),
    /**
     * Icon for row / line.
     *
     * @see <a href="https://icons8.com/icon/3ZQ7JYqlkFtC/row">Row</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Row("icons8-row-16.png"),
    /**
     * Icon for shortcut.
     *
     * @see <a href="https://icons8.com/icon/i1z7pQ2orcJk/shortcut">Shortcut</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Shortcut("icons8-shortcut-16.png"),
    /**
     * Icon for signature.
     *
     * @see
     * <a href="https://icons8.com/icon/bmicUxC0XDNt/signature">Signature</a>
     * icon by <a href="https://icons8.com">Icons8</a>
     */
    Signature("icons8-signature-16.png"),
    /**
     * Icon for Size.
     *
     * @see <a href="https://icons8.com/icon/d8VomliGByyY/page-size">Page
     * Size</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Size("icons8-page-size-16.png"),
    /**
     * Icon for Stack / Call stack entry.
     *
     * @see <a href="https://icons8.com/icon/43878/stack">Stack</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Stack("icons8-stack-16.png"),
    /**
     * Icon for Tag or Type.
     *
     * @see <a href="https://icons8.com/icon/pmzH4rF8Lrv9/tag">Tag</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Tag("icons8-tag-16.png"),
    /**
     * Icon for time.
     *
     * @see <a href="https://icons8.com/icon/HIiMyiiPLluy/time">Time</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    Time("icons8-time-16.png"),
    /**
     * Icon for verification.
     *
     * @see <a href="https://icons8.com/icon/85086/verified-account">Verified Account</a> icon by <a href="https://icons8.com">Icons8</a>
     */
    Verification("icons8-verified-account-16.png"),
    /**
     * Icon for version.
     *
     * @see <a href="https://icons8.com/icon/59954/versions">Versions</a> icon
     * by <a href="https://icons8.com">Icons8</a>
     */
    Versions("icons8-versions-16.png"),
    /**
     * Icon for zip file.
     *
     * @see <a href="https://icons8.com/icon/49013/zip">ZIP</a> icon by
     * <a href="https://icons8.com">Icons8</a>
     */
    ZIP("icons8-zip-16.png");

    private static final Map<String, Icon> iconCache = new HashMap<>();

    private final String filename;

    private Icons(String filename) {
        this.filename = filename;
    }

    public Icon getIcon() {
        return icon(this.filename);
    }

    private static Icon icon(String url) {
        return iconCache.computeIfAbsent(url, k -> new ImageIcon(Icons.class.getResource("/image/" + url)));
    }

}
