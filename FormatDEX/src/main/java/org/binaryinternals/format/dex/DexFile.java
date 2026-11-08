/*
 * DexFile.java    June 14, 2015, 22:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.dex.header_item.Endian;

/// @author Amos Shi
/// @see
/// <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
/// Dalvik Executable (DEX) format</a>
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S100", "java:S116", "java:S1104"})
public final class DexFile extends FileFormat {

    /// The constant NO_INDEX is used to indicate that an index value is absent.
    /// Embedded in {@link class_def_item} and {@link debug_info_item}
    public static final long NO_INDEX = 0xffffffffL;
    private static final Logger LOGGER = Logger.getLogger(DexFile.class.getName());

    /// The constant array/string
    /// {@link #DEX_FILE_MAGIC1}|{@link #DEX_FILE_MAGIC2} is the list of bytes
    /// that must appear at the beginning of a `.dex` file in order
    /// for it to be recognized as such.
    ///
    /// The value intentionally contains a `newline` ("\n" or 0x0a)
    /// and a `null` byte ("\0" or 0x00) in order to help in the
    /// detection of certain forms of corruption. The value also encodes a format
    /// version number as three decimal digits, which is expected to increase
    /// monotonically over time as the format evolves.
    static final byte[] DEX_FILE_MAGIC1 = {'d', 'e', 'x', '\n'};
    static final byte[] DEX_FILE_MAGIC2 = {'0', '3', '5', '\0'};

    /// Magic value part 1.
    public final byte[] magic1;
    /// Magic value part 2.
    public final byte[] magic2;

    /// The file header.
    public final header_item header;
    /// String identifiers list, or `null`.
    public final string_id_item[] string_ids;
    public final type_id_item[] type_ids;
    public final proto_id_item[] proto_ids;
    public final field_id_item[] field_ids;
    public final method_id_item[] method_ids;
    public final class_def_item[] class_defs;
    /// The parsed file components.
    public final SortedMap<Long, FileComponent> data = new TreeMap<>();
    public Type_ubyte[] link_data;

    /// ```
    /// java:S3776 - Cognitive Complexity of methods should not be too high - We need this logic together
    /// ```
    ///
    /// @param file Input file
    /// @throws IOException Read file failed
    /// @throws FileFormatException Invalid file format
    @SuppressWarnings("java:S3776")
    public DexFile(final Path file) throws IOException {
        super(file);

        // Check the file signature
        this.magic1 = Arrays.copyOfRange(super.fileByteArray, 0, DEX_FILE_MAGIC1.length);
        this.magic2 = Arrays.copyOfRange(super.fileByteArray, DEX_FILE_MAGIC1.length, DEX_FILE_MAGIC1.length + DEX_FILE_MAGIC2.length);

        if (!Arrays.equals(DEX_FILE_MAGIC1, magic1)
                || magic2[DEX_FILE_MAGIC2.length - 1] != DEX_FILE_MAGIC2[DEX_FILE_MAGIC2.length - 1]) {
            throw new FileFormatException("This is not a valid DEX file, because the DEX file signature does not exist at the beginning of this file.");
        }

        // Parse section by section
        PosDataInputStream parseEndian = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        parseEndian.skipNBytes(DEX_FILE_MAGIC1.length);
        parseEndian.skipNBytes(DEX_FILE_MAGIC2.length);
        parseEndian.skipNBytes(Type_uint.LENGTH);           // checksum
        parseEndian.skipNBytes(20);                         // signature
        parseEndian.skipNBytes(Type_uint.LENGTH);           // file_size
        parseEndian.skipNBytes(Type_uint.LENGTH);           // header_size

        int i1 = parseEndian.readUnsignedByte();
        int i2 = parseEndian.readUnsignedByte();
        int i3 = parseEndian.readUnsignedByte();
        int i4 = parseEndian.readUnsignedByte();

        Endian endian;
        if (Endian.ENDIAN_CONSTANT.equals(i1, i2, i3, i4)) {
            endian = Endian.ENDIAN_CONSTANT;
        } else if (Endian.REVERSE_ENDIAN_CONSTANT.equals(i1, i2, i3, i4)) {
            endian = Endian.REVERSE_ENDIAN_CONSTANT;
        } else {
            throw new FileFormatException("The dex file do not contain valid endian_tag. the value: 0x"
                    + Integer.toHexString(i1) + ", 0x"
                    + Integer.toHexString(i2) + ", 0x"
                    + Integer.toHexString(i3) + ", 0x"
                    + Integer.toHexString(i4));
        }

        PosDataInputStreamDex stream = new PosDataInputStreamDex(new PosByteArrayInputStream(super.fileByteArray), endian);
        SortedMap<Long, DataParser> todoData = new TreeMap<>();

        // Header
        stream.skipNBytes(DEX_FILE_MAGIC1.length);
        stream.skipNBytes(DEX_FILE_MAGIC2.length);
        this.header = new header_item(stream);

        // string_ids
        if (this.header.string_ids_off.intValue() == 0) {
            this.string_ids = null;
        } else {
            stream.flyTo(this.header.string_ids_off.intValue());
            this.string_ids = new string_id_item[this.header.string_ids_size.intValue()];
            for (int i = 0; i < this.string_ids.length; i++) {
                this.string_ids[i] = new string_id_item(stream);
                todoData.put(this.string_ids[i].string_data_off.value, (in, _) -> new string_data_item(in));
            }
        }

        // type_ids
        if (this.header.type_ids_off.intValue() == 0) {
            this.type_ids = null;
        } else {
            stream.flyTo(this.header.type_ids_off.intValue());
            this.type_ids = new type_id_item[this.header.type_ids_size.intValue()];
            for (int i = 0; i < this.type_ids.length; i++) {
                this.type_ids[i] = new type_id_item(stream);
            }
        }

        // proto_ids
        if (this.header.proto_ids_off.intValue() == 0) {
            this.proto_ids = null;
        } else {
            stream.flyTo(this.header.proto_ids_off.intValue());
            this.proto_ids = new proto_id_item[this.header.proto_ids_size.intValue()];
            for (int i = 0; i < this.proto_ids.length; i++) {
                this.proto_ids[i] = new proto_id_item(stream);
                if (this.proto_ids[i].parameters_off.value != 0) {
                    todoData.put(this.proto_ids[i].parameters_off.value, (in, _) -> new type_list(in));
                }
            }
        }

        // field_ids
        if (this.header.field_ids_off.intValue() == 0) {
            this.field_ids = null;
        } else {
            stream.flyTo(this.header.field_ids_off.intValue());
            this.field_ids = new field_id_item[this.header.field_ids_size.intValue()];
            for (int i = 0; i < this.field_ids.length; i++) {
                this.field_ids[i] = new field_id_item(stream);
            }
        }

        // method_ids
        if (this.header.method_ids_off.intValue() == 0) {
            this.method_ids = null;
        } else {
            stream.flyTo(this.header.method_ids_off.intValue());
            this.method_ids = new method_id_item[this.header.method_ids_size.intValue()];
            for (int i = 0; i < this.method_ids.length; i++) {
                this.method_ids[i] = new method_id_item(stream);
            }
        }

        // class_defs
        if (this.header.class_defs_off.intValue() == 0) {
            this.class_defs = null;
        } else {
            stream.flyTo(this.header.class_defs_off.intValue());
            this.class_defs = new class_def_item[this.header.class_defs_size.intValue()];
            for (int i = 0; i < this.class_defs.length; i++) {
                this.class_defs[i] = new class_def_item(stream, this);

                if (this.class_defs[i].interfaces_off.value != 0) {
                    todoData.put(this.class_defs[i].interfaces_off.value, (in, _) -> new type_list(in));
                }
                if (this.class_defs[i].annotations_off.value != 0) {
                    todoData.put(this.class_defs[i].annotations_off.value, annotations_directory_item::new);
                }
                if (this.class_defs[i].class_data_off.value != 0) {
                    todoData.put(this.class_defs[i].class_data_off.value, class_data_item::new);
                }
                if (this.class_defs[i].static_values_off.value != 0) {
                    todoData.put(this.class_defs[i].static_values_off.value, (in, _) -> new encoded_array_item(in));
                }
            }
        }

        // data
        for (Map.Entry<Long, DataParser> todoItem : todoData.entrySet()) {
            this.parseData(todoItem.getKey(), todoItem.getValue(), stream);
        }
    }

    /// Parse an item of the {@link #data} section at the current stream position.
    @FunctionalInterface
    interface DataParser {

        FileComponent parse(PosDataInputStreamDex stream, DexFile dex) throws IOException;
    }

    void parseData(final long offset, final DataParser parser, final PosDataInputStreamDex stream) throws IOException {
        int breakPos = stream.getPos();

        stream.flyTo((int) offset);
        try {
            this.data.put(offset, parser.parse(stream, this));
        } catch (RuntimeException e) {
            // For example an out of range offset or size in a corrupted file
            String msg = "Invalid data item at 0x%X".formatted((int) offset);
            LOGGER.severe(msg);
            throw new FileFormatException(msg, e);
        }

        stream.flyTo(breakPos);
    }

    static void check_uint(String fieldName, Type_uint uint, int streamPosition) throws FileFormatException {
        if (uint.value > Integer.MAX_VALUE) {
            throw new FileFormatException("%s is too big cannot be handled here: %d, position 0x%X".formatted(fieldName, uint.value, streamPosition));
        }
    }

    @Override
    public Optional<Icons> getIcon() {
        return Optional.of(Icons.Dex);
    }

    /// Get the underlying String value for a {@link #string_ids} item.
    ///
    /// @param index Index in the {@link #string_ids} array
    /// @return String value for the {@link #string_ids} item, or
    /// `null` for invalid index
    public String get_string_ids_string(int index) {
        if (index < -1 || this.string_ids == null || index >= this.string_ids.length) {
            LOGGER.log(Level.WARNING, "Return null for invalid string_ids index={0}", index);
            return null;
        }

        return this.data.get(this.string_ids[index].string_data_off.value) instanceof string_data_item item
                ? item.getString()
                : null;
    }

    /// Get the underlying String value for a {@link #type_ids} item.
    ///
    /// @param index Index in the {@link #type_ids} array
    /// @return String value for the {@link #type_ids} item, or `null`
    /// for invalid index
    public String get_type_ids_string(int index) {
        if (index < -1 || this.type_ids == null || index >= this.type_ids.length) {
            LOGGER.log(Level.WARNING, "Return null for invalid type_ids index={0}", index);
            return null;
        }

        return this.get_string_ids_string(this.type_ids[index].descriptor_idx.intValue());
    }

    @Override
    public String getContentTabName() {
        return "Android DEX File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        new JTreeDexFile().generateTreeNode(parentNode, this);
    }
}
