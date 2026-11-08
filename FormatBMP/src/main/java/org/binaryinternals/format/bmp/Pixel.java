/*
 * Pixel.java    Nov 04, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.bmp;

import org.binaryinternals.commonlib.GenerateTreeNode;

/// One entry of the [ColorTable].
///
/// @author Amos Shi
public sealed interface Pixel extends GenerateTreeNode permits PixelBGR, PixelRGBA {

    /// File position of the entry.
    ///
    /// @return Start position
    int getStartPos();

    /// Size of the entry in bytes.
    ///
    /// @return Length
    int getLength();
}
