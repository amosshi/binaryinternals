/*
 * header_item.java    June 18, 2015, 22:34
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// The `header_item` structure of the DEX file.
///
/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public final class header_item extends FileComponent implements GenerateTreeNodeDexFile {

    /// Size of the header item in bytes.
    ///
    /// @see map_list.TypeCodes#TYPE_HEADER_ITEM
    public static final int ITEM_SIZE = 0x70;

    /// adler32 checksum of the rest of the file (everything but magic and this
    /// field); used to detect file corruption.
    public final Type_uint checksum;

    /// SHA-1 signature (hash) of the rest of the file (everything but [DexFile#DEX_FILE_MAGIC1], [DexFile#DEX_FILE_MAGIC2],
    /// [#checksum], and this field [#signature]); used to uniquely
    /// identify files.
    public final Type_ubyte[] signature = new Type_ubyte[20];

    /// size of the entire file (including the [header_item]), in bytes.
    public final Type_uint file_size;

    /// size of the header (this entire section), in bytes. This allows for at
    /// least a limited amount of backwards/forwards compatibility without
    /// invalidating the format.
    public final Type_uint header_size = new Type_uint(0x70);

    /// Endianness tag. The value is either [Endian#ENDIAN_CONSTANT] or
    /// [Endian#REVERSE_ENDIAN_CONSTANT].
    public final Type_uint endian_tag;
    /// Size of the link section, or 0 if this file isn't statically linked.
    public final Type_uint link_size;
    /// Offset from the start of the file to the link section, or 0 if
    /// [#link_size] == 0. The offset, if non-zero, should be to an offset
    /// into the [DexFile#link_data] section. The format of the data
    /// pointed at is left unspecified by this document; this header field (and
    /// the previous) are left as hooks for use by runtime implementations.
    public final Type_uint link_off;
    /// Offset from the start of the file to the map item.
    public final Type_uint map_off;
    /// Count of strings in the string identifiers list.
    public final Type_uint string_ids_size;
    /// Offset from the start of the file to the string identifiers list, or `0` if [#string_ids_size] is `0`.
    public final Type_uint string_ids_off;
    /// Count of elements in the type identifiers list, at most 65535.
    public final Type_uint type_ids_size;
    /// Offset from the start of the file to the type identifiers list, or `0` if [#type_ids_size] is `0`.
    public final Type_uint type_ids_off;
    /// Count of elements in the prototype identifiers list, at most 65535.
    public final Type_uint proto_ids_size;
    /// Offset from the start of the file to the prototype identifiers list, or `0` if [#proto_ids_size] is `0`.
    public final Type_uint proto_ids_off;
    /// Count of elements in the field identifiers list.
    public final Type_uint field_ids_size;
    /// Offset from the start of the file to the field identifiers list, or `0` if [#field_ids_size] is `0`.
    public final Type_uint field_ids_off;
    /// Count of elements in the method identifiers list.
    public final Type_uint method_ids_size;
    /// Offset from the start of the file to the method identifiers list, or `0` if [#method_ids_size] is `0`.
    public final Type_uint method_ids_off;
    /// Count of elements in the class definitions list.
    public final Type_uint class_defs_size;
    /// Offset from the start of the file to the class definitions list, or `0` if [#class_defs_size] is `0`.
    public final Type_uint class_defs_off;
    /// Size of data section in bytes, an even multiple of `sizeof(uint)`.
    public final Type_uint data_size;
    /// Offset from the start of the file to the start of the data section.
    public final Type_uint data_off;

    header_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();

        this.checksum = stream.Dex_uint();
        for (int i = 0; i < this.signature.length; i++) {
            this.signature[i] = stream.Dex_ubyte();
        }
        this.file_size = stream.Dex_uint();
        stream.skipNBytes(Type_uint.LENGTH);
        this.endian_tag = new Type_uint(stream.readUnsignedInt()); // Always read from left to right
        this.link_size = stream.Dex_uint();
        this.link_off = stream.Dex_uint();
        this.map_off = stream.Dex_uint();
        this.string_ids_size = stream.Dex_uint();
        this.string_ids_off = stream.Dex_uint();
        this.type_ids_size = stream.Dex_uint();
        this.type_ids_off = stream.Dex_uint();
        this.proto_ids_size = stream.Dex_uint();
        this.proto_ids_off = stream.Dex_uint();
        this.field_ids_size = stream.Dex_uint();
        this.field_ids_off = stream.Dex_uint();
        this.method_ids_size = stream.Dex_uint();
        this.method_ids_off = stream.Dex_uint();
        this.class_defs_size = stream.Dex_uint();
        this.class_defs_off = stream.Dex_uint();
        this.data_size = stream.Dex_uint();
        this.data_off = stream.Dex_uint();

        super.length = this.header_size.intValue() - DexFile.DEX_FILE_MAGIC1.length - DexFile.DEX_FILE_MAGIC2.length;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        DefaultMutableTreeNode nodeTemp;
        int floatPos = this.getStartPos();

        DefaultMutableTreeNode headerNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                this.getLength(),
                "header_item"));
        parentNode.add(headerNode);

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "checksum", this.checksum, "msg_header_item_checksum", Icons.Checksum);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, this.signature.length, "signature", Type_ubyte.toString(this.signature), "msg_header_item_signature",
                Icons.Signature);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "file_size", this.file_size, "msg_header_item_file_size", Icons.Size);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "header_size", this.header_size, "msg_header_item_header_size", Icons.Size);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                Type_uint.LENGTH,
                "endian_tag: " + this.endian_tag + " / " + Endian.toString(this.endian_tag.intValue()),
                Icons.Endian,
                GenerateTreeNodeDexFile.MESSAGES.getString("msg_header_item_endian_tag")
        )));
        floatPos += Type_uint.LENGTH;

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "link_size", this.link_size, "msg_header_item_link_size", Icons.Size);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "link_off", this.link_off, "msg_header_item_link_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "map_off", this.map_off, "msg_header_item_map_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "string_ids_size", this.string_ids_size, "msg_header_item_string_ids_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "string_ids_off", this.string_ids_off, "msg_header_item_string_ids_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "type_ids_size", this.type_ids_size, "msg_header_item_type_ids_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "type_ids_off", this.type_ids_off, "msg_header_item_type_ids_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "proto_ids_size", this.proto_ids_size, "msg_header_item_proto_ids_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "proto_ids_off", this.proto_ids_off, "msg_header_item_proto_ids_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "field_ids_size", this.field_ids_size, "msg_header_item_field_ids_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "field_ids_off", this.field_ids_off, "msg_header_item_field_ids_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "method_ids_size", this.method_ids_size, "msg_header_item_method_ids_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "method_ids_off", this.method_ids_off, "msg_header_item_method_ids_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "class_defs_size", this.class_defs_size, "msg_header_item_class_defs_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "class_defs_off", this.class_defs_off, "msg_header_item_class_defs_off", Icons.Offset);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = addNode(headerNode, floatPos, Type_uint.LENGTH, "data_size", this.data_size, "msg_header_item_data_size", Icons.Counter);
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

        addNode(headerNode, floatPos, Type_uint.LENGTH, "data_off", this.data_off, "msg_header_item_data_off", Icons.Offset);
    }

    /// The constant [Endian#ENDIAN_CONSTANT] is used to indicate the
    /// endianness of the file in which it is found. Although the standard .dex
    /// format is little-endian, implementations may choose to perform
    /// byte-swapping. Should an implementation come across a header whose
    /// endian_tag is [Endian#REVERSE_ENDIAN_CONSTANT] instead of
    /// [Endian#ENDIAN_CONSTANT], it would know that the file has been
    /// byte-swapped from the expected form.
    public enum Endian {

        /// Little-endian, which is DEX standard.
        ENDIAN_CONSTANT(0x12345678),
        /// Big-endian.
        REVERSE_ENDIAN_CONSTANT(0x78563412);

        /// Internal value of the endianness.
        public final int value;
        /// Byte 1 of the value, the most significant one.
        public final int byte1;
        /// Byte 2 of the value.
        public final int byte2;
        /// Byte 3 of the value.
        public final int byte3;
        /// Byte 4 of the value, the least significant one.
        public final int byte4;

        Endian(int i) {
            this.value = i;
            this.byte1 = i >> 24;
            this.byte2 = (i << 8) >> 24;
            this.byte3 = (i << 16) >> 24;
            this.byte4 = (i << 24) >> 24;
        }

        /// Map to [ByteOrder]. The existing parser reads
        /// [#ENDIAN_CONSTANT] as [ByteOrder#BIG_ENDIAN].
        ///
        /// @return the matching byte order
        public ByteOrder toByteOrder() {
            return this == ENDIAN_CONSTANT ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        }

        /// Checks whether the 4 bytes are this endian tag.
        ///
        /// @param i1 Byte 1, the most significant one
        /// @param i2 Byte 2
        /// @param i3 Byte 3
        /// @param i4 Byte 4, the least significant one
        /// @return `true` if the bytes match
        public boolean equals(int i1, int i2, int i3, int i4) {
            return (i1 == this.byte1) && (i2 == this.byte2) && (i3 == this.byte3) && (i4 == this.byte4);
        }

        /// Gets the name of an endian tag value.
        ///
        /// @param i The endian tag value
        /// @return The enum name, or `Un-recognized !!!`
        public static String toString(int i) {
            return Arrays.stream(values())
                    .filter(item -> item.value == i)
                    .findFirst()
                    .map(Endian::name)
                    .orElse("Un-recognized !!!");
        }
    }
}
