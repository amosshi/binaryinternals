/*
 * HeaderItem.java    June 18, 2015, 22:34
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;

/**
 * The <code>header_item</code> structure of the DEX file.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S116", "java:S1104"})
public class HeaderItem extends FileComponent {

    /**
     * adler32 checksum of the rest of the file (everything but magic and this
     * field); used to detect file corruption.
     */
    public Dex_uint checksum;

    /**
     * SHA-1 signature (hash) of the rest of the file (everything but {@link DexFile#DEX_FILE_MAGIC1}, {@link DexFile#DEX_FILE_MAGIC2},
     * {@link #checksum}, and this field {@link #signature}); used to uniquely
     * identify files.
     */
    public Dex_ubyte[] signature = new Dex_ubyte[20];

    /**
     * size of the entire file (including the {@link HeaderItem}), in bytes.
     */
    public Dex_uint file_size;

    /**
     * size of the header (this entire section), in bytes. This allows for at
     * least a limited amount of backwards/forwards compatibility without
     * invalidating the format.
     */
    public final Dex_uint header_size = new Dex_uint(0x70);

    /**
     * Endianness tag. The value is either {@link Endian#ENDIAN_CONSTANT} or
     * {@link Endian#REVERSE_ENDIAN_CONSTANT}.
     */
    public Dex_uint endian_tag;
    /**
     * Size of the link section, or 0 if this file isn't statically linked.
     */
    public Dex_uint link_size;
    /**
     * Offset from the start of the file to the link section, or 0 if
     * {@link #link_size} == 0. The offset, if non-zero, should be to an offset
     * into the {@link DexFile#link_data} section. The format of the data
     * pointed at is left unspecified by this document; this header field (and
     * the previous) are left as hooks for use by runtime implementations.
     */
    public Dex_uint link_off;
    public Dex_uint map_off;
    public Dex_uint string_ids_size;
    public Dex_uint string_ids_off;
    public Dex_uint type_ids_size;
    public Dex_uint type_ids_off;
    public Dex_uint proto_ids_size;
    public Dex_uint proto_ids_off;
    public Dex_uint field_ids_size;
    public Dex_uint field_ids_off;
    public Dex_uint method_ids_size;
    public Dex_uint method_ids_off;
    public Dex_uint class_defs_size;
    public Dex_uint class_defs_off;
    public Dex_uint data_size;
    public Dex_uint data_off;

    HeaderItem(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();

        this.checksum = stream.Dex_uint();
        for (int i = 0; i < this.signature.length; i++) {
            this.signature[i] = stream.Dex_ubyte();
        }
        this.file_size = stream.Dex_uint();
        BytesTool.skip(stream, Dex_uint.LENGTH);
        this.endian_tag = new Dex_uint(stream.readUnsignedInt()); // Always read from left to right
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

        super.length = this.header_size.intValue() - DexFile.DEX_FILE_MAGIC1.size() - DexFile.DEX_FILE_MAGIC2.size();
    }

    /**
     * The constant {@link Endian#ENDIAN_CONSTANT} is used to indicate the
     * endianness of the file in which it is found. Although the standard .dex
     * format is little-endian, implementations may choose to perform
     * byte-swapping. Should an implementation come across a header whose
     * endian_tag is {@link Endian#REVERSE_ENDIAN_CONSTANT} instead of
     * {@link Endian#ENDIAN_CONSTANT}, it would know that the file has been
     * byte-swapped from the expected form.
     */
    public enum Endian {

        /**
         * Little-endian, which is DEX standard.
         */
        ENDIAN_CONSTANT(0x12345678),
        /**
         * Big-endian.
         */
        REVERSE_ENDIAN_CONSTANT(0x78563412);

        /**
         * Internal value of the endianness.
         */
        public final int value;
        public final int byte1;
        public final int byte2;
        public final int byte3;
        public final int byte4;

        private Endian(int i) {
            this.value = i;
            this.byte1 = i >> 24;
            this.byte2 = (i << 8) >> 24;
            this.byte3 = (i << 16) >> 24;
            this.byte4 = (i << 24) >> 24;
        }

        public boolean equals(int i1, int i2, int i3, int i4) {
            return (i1 == this.byte1) && (i2 == this.byte2) && (i3 == this.byte3) && (i4 == this.byte4);
        }

        public static String toString(int i) {
            if (Endian.ENDIAN_CONSTANT.value == i) {
                return Endian.ENDIAN_CONSTANT.name();
            } else if (Endian.REVERSE_ENDIAN_CONSTANT.value == i) {
                return Endian.REVERSE_ENDIAN_CONSTANT.name();
            } else {
                return "Un-recognized !!!";
            }
        }
    }
}
