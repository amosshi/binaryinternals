package org.binaryinternals.format.dex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.format.dex.header_item.Endian;
import org.junit.jupiter.api.Test;

/// The tree of a [code_item] covers every byte: instructions, padding, tries
/// and the catch handlers.
class CodeItemTest {

    /// A method with one instruction, guarded by one try with a catch-all
    /// handler. Little-endian, as usual DEX files.
    private static final int[] CODE_ITEM = {
        0x01, 0x00,                 // registers_size = 1
        0x00, 0x00,                 // ins_size
        0x00, 0x00,                 // outs_size
        0x01, 0x00,                 // tries_size = 1
        0x00, 0x00, 0x00, 0x00,     // debug_info_off
        0x01, 0x00, 0x00, 0x00,     // insns_size = 1
        0x0E, 0x00,                 // insns: return-void
        0x00, 0x00,                 // padding, as tries_size != 0 and insns_size is odd
        0x00, 0x00, 0x00, 0x00,     // try_item.start_addr
        0x01, 0x00,                 // try_item.insn_count
        0x01, 0x00,                 // try_item.handler_off
        0x01,                       // encoded_catch_handler_list.size = 1
        0x00,                       // encoded_catch_handler.size = 0: catch-all only
        0x00,                       // encoded_catch_handler.catch_all_addr
    };

    @Test
    void treeCoversEveryByte() throws IOException {
        final byte[] buf = new byte[CODE_ITEM.length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) CODE_ITEM[i];
        }
        final code_item item = new code_item(new PosDataInputStreamDex(new PosByteArrayInputStream(buf), Endian.REVERSE_ENDIAN_CONSTANT));
        assertEquals(buf.length, item.getLength());

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        item.generateTreeNode(root, null);  // No type lookup needed: the handler is catch-all only

        final List<JTreeNodeFileComponent> leaves = new ArrayList<>();
        final List<String> texts = new ArrayList<>();
        for (Object o : Collections.list(root.depthFirstEnumeration())) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
            if (node.getUserObject() instanceof JTreeNodeFileComponent c) {
                texts.add(c.getText());
                if (node.isLeaf()) {
                    leaves.add(c);
                }
            }
        }
        leaves.sort(Comparator.comparingInt(JTreeNodeFileComponent::getStartPos));

        int pos = 0;
        for (JTreeNodeFileComponent leaf : leaves) {
            assertEquals(pos, leaf.getStartPos(), "gap or overlap before '%s'".formatted(leaf.getText()));
            pos += leaf.getLength();
        }
        assertEquals(buf.length, pos, "the leaves end before the code_item");

        for (String name : List.of("insns: 1 code units", "padding:", "try_item[0]:", "insn_count:", "encoded_catch_handler[0]:", "catch_all_addr:")) {
            assertTrue(texts.stream().anyMatch(t -> t.startsWith(name)), name + " not in " + texts);
        }
    }
}
