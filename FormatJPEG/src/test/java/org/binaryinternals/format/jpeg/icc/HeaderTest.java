package org.binaryinternals.format.jpeg.icc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.junit.jupiter.api.Test;

/// The profile flags, device attributes and rendering intent of the ICC
/// profile header are decoded.
class HeaderTest {

    /// Header texts, with the given profile flags (bytes 44 - 47), device
    /// attributes (56 - 63) and rendering intent (64 - 67).
    private static List<String> texts(final int flags, final long attributes, final int intent) throws IOException {
        final ByteBuffer header = ByteBuffer.allocate(Header.LENGTH);  // Big-endian, as ICC profiles
        header.putInt(44, flags);
        header.putLong(56, attributes);
        header.putInt(64, intent);

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        new Header(new PosDataInputStream(new PosByteArrayInputStream(header.array()))).generateTreeNode(root);
        final List<String> texts = new ArrayList<>();
        for (Object o : Collections.list(root.depthFirstEnumeration())) {
            if (((DefaultMutableTreeNode) o).getUserObject() instanceof JTreeNodeFileComponent c) {
                texts.add(c.getText());
            }
        }
        return texts;
    }

    @Test
    void bitsSet() throws IOException {
        final List<String> texts = texts(0xABCD_0003, 0x1234_5678_0000_001FL, 3);

        for (String expected : List.of(
                "Profile flags = 0xABCD0003",
                "Vendor specific flags = 0xABCD",
                "Bit 0 = 1: Embedded profile",
                "Bit 1 = 1: Cannot be used independently of the embedded colour data",
                "Device attributes = 0x123456780000001F",
                "Vendor specific attributes = 0x12345678",
                "Bit 0 = 1: Transparency",
                "Bit 1 = 1: Matte",
                "Bit 2 = 1: Negative media polarity",
                "Bit 3 = 1: Black & white media",
                "Bit 4 = 1: Non-paper-based",
                "Rendering intent = 3 (ICC-absolute colorimetric)")) {
            assertTrue(texts.contains(expected), expected + " not in " + texts);
        }
    }

    @Test
    void bitsClear() throws IOException {
        final List<String> texts = texts(0, 0L, 0);

        for (String expected : List.of(
                "Bit 0 = 0: Not embedded",
                "Bit 1 = 0: Can be used independently of the embedded colour data",
                "Bit 0 = 0: Reflective",
                "Bit 4 = 0: Paper-based",
                "Rendering intent = 0 (Perceptual)")) {
            assertTrue(texts.contains(expected), expected + " not in " + texts);
        }
    }

    @Test
    void renderingIntentUsesLow16Bits() throws IOException {
        assertTrue(texts(0, 0L, 0x0001_0002).contains("Rendering intent = 65538 (Saturation)"));
        assertTrue(texts(0, 0L, 7).contains("Rendering intent = 7 (Unrecognized)"));
        assertEquals("Media-relative colorimetric", header(1).getRenderingIntentName());
    }

    private static Header header(final int intent) throws IOException {
        final ByteBuffer header = ByteBuffer.allocate(Header.LENGTH);
        header.putInt(64, intent);
        return new Header(new PosDataInputStream(new PosByteArrayInputStream(header.array())));
    }
}
