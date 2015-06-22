/*
 * HeaderItem.java    June 18, 2015, 22:34
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * The <code>header_item</code> structure of the DEX file.
 *
 * @author Amos Shi
 */
public class HeaderItem {

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
    public Dex_uint link_size;
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

    /**
     * The constant {@link Endian#ENDIAN_CONSTANT} is used to indicate the
     * endianness of the file in which it is found. Although the standard .dex
     * format is little-endian, implementations may choose to perform
     * byte-swapping. Should an implementation come across a header whose
     * endian_tag is {@link Endian#REVERSE_ENDIAN_CONSTANT} instead of
     * {@link Endian#ENDIAN_CONSTANT}, it would know that the file has been
     * byte-swapped from the expected form.
     */
    public static enum Endian {

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

        private Endian(int i) {
            this.value = i;
        }
    }
}
