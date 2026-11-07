/*
 * ExtraFieldBlock.java    Nov 06, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.zip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// One block of the `extra field` of a local file header or a central
/// directory file header, see `APPNOTE.TXT` 4.5.
///
/// ```
/// header ID   2 bytes
/// data size   2 bytes
/// data        (data size) bytes
/// ```
///
/// @param startPos Start position of the block in the file
/// @param headerId Header ID, which tells the type of the data
/// @param dataSize Size of the data
/// @author Amos Shi
public record ExtraFieldBlock(int startPos, int headerId, int dataSize) implements GenerateTreeNode {

    /// Size of `header ID` and `data size`.
    public static final int HEADER_SIZE = 4;
    /// Header ID of the Zip64 extended information extra field.
    public static final int ZIP64_EXTENDED_INFO = 0x0001;

    /// Split the `extra field` bytes into blocks. A truncated last block is cut
    /// to the available bytes.
    ///
    /// @param extra The `extra field` bytes, may be `null`
    /// @param startPos Start position of `extra` in the file
    /// @return The blocks
    static List<ExtraFieldBlock> parse(final byte[] extra, final int startPos) {
        final List<ExtraFieldBlock> fields = new ArrayList<>();
        if (extra == null) {
            return fields;
        }

        final ByteBuffer buf = ByteBuffer.wrap(extra).order(ByteOrder.LITTLE_ENDIAN);
        while (buf.remaining() >= HEADER_SIZE) {
            final int offset = buf.position();
            final int id = Short.toUnsignedInt(buf.getShort());
            final int size = Math.min(Short.toUnsignedInt(buf.getShort()), buf.remaining());
            fields.add(new ExtraFieldBlock(startPos + offset, id, size));
            buf.position(buf.position() + size);
        }
        return List.copyOf(fields);
    }

    /// The data of the Zip64 extended information extra field, in little
    /// endian, or an empty buffer if there is none.
    ///
    /// @param extra The `extra field` bytes, may be `null`
    /// @return The data
    static ByteBuffer zip64ExtendedInfo(final byte[] extra) {
        final ByteBuffer empty = ByteBuffer.allocate(0);
        if (extra == null) {
            return empty;
        }
        for (final ExtraFieldBlock field : parse(extra, 0)) {
            if (field.headerId == ZIP64_EXTENDED_INFO) {
                return ByteBuffer.wrap(extra, field.startPos + HEADER_SIZE, field.dataSize)
                        .slice()
                        .order(ByteOrder.LITTLE_ENDIAN);
            }
        }
        return empty;
    }

    /// Name of the known header IDs, see `APPNOTE.TXT` 4.5.2 and 4.6.
    ///
    /// @return The name, or `Unknown`
    public String getHeaderName() {
        return switch (this.headerId) {
            case ZIP64_EXTENDED_INFO -> "Zip64 extended information";
            case 0x0007 -> "AV Info";
            case 0x000A -> "NTFS";
            case 0x000D -> "UNIX";
            case 0x0017 -> "Strong Encryption Header";
            case 0x5455 -> "Extended Timestamp";
            case 0x5855 -> "Info-ZIP UNIX (original)";
            case 0x6375 -> "Info-ZIP Unicode Comment";
            case 0x7075 -> "Info-ZIP Unicode Path";
            case 0x7855 -> "Info-ZIP UNIX (new)";
            case 0x7875 -> "Info-ZIP UNIX UID/GID";
            case 0x9901 -> "AE-x encryption";
            case 0xA220 -> "Microsoft Open Packaging Growth Hint";
            case 0xCAFE -> "JAR marker";
            case 0xD935 -> "Android ZIP alignment";
            default -> "Unknown";
        };
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                HEADER_SIZE + this.dataSize,
                "0x%04X %s".formatted(this.headerId, this.getHeaderName()),
                Icons.Tag));
        parentNode.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                2,
                "header ID = 0x%04X".formatted(this.headerId))));
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + 2,
                2,
                "data size = %d".formatted(this.dataSize),
                Icons.Size)));
        if (this.dataSize > 0) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.startPos + HEADER_SIZE,
                    this.dataSize,
                    "data",
                    Icons.Data)));
        }
    }
}
