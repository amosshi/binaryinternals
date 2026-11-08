package org.binaryinternals.format.jpeg.tiff;

import org.junit.jupiter.api.Test;

/// Every key of [ShortText] must exist in `ShortText.properties`, so a missing entry or a typo
/// fails the build instead of the viewer.
class ShortTextTest {

    /// Every `KEY_` constant of [ShortText] exists in `ShortText.properties`.
    @Test
    void keyConstantsExist() throws IllegalAccessException {
        MessageKeys.assertConstantsExist(ShortText.class, ShortText::getString);
    }
}
