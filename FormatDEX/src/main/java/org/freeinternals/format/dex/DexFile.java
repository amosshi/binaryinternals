/*
 * DEXFile.java    June 14, 2015, 22:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.dex.header_item.Endian;

/**
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S116", "java:S1104"})
public final class DexFile extends FileFormat {

    /**
     * The constant NO_INDEX is used to indicate that an index value is absent.
     * Embedded in {@link class_def_item} and {@link debug_info_item}
     */
    public static final Type_uint NO_INDEX = new Type_uint(0xffffffff);
    private static final Logger LOGGER = Logger.getLogger(DexFile.class.getName());

    /**
     * The constant array/string
     * {@link #DEX_FILE_MAGIC1}|{@link #DEX_FILE_MAGIC2} is the list of bytes
     * that must appear at the beginning of a <code>.dex</code> file in order
     * for it to be recognized as such.
     * <p>
     * The value intentionally contains a <code>newline</code> ("\n" or 0x0a)
     * and a <code>null</code> byte ("\0" or 0x00) in order to help in the
     * detection of certain forms of corruption. The value also encodes a format
     * version number as three decimal digits, which is expected to increase
     * monotonically over time as the format evolves.
     * </p>
     */
    public static final List<Byte> DEX_FILE_MAGIC1 = Collections.unmodifiableList(Arrays.asList(new Byte[]{'d', 'e', 'x', '\n'}));
    public static final List<Byte> DEX_FILE_MAGIC2 = Collections.unmodifiableList(Arrays.asList(new Byte[]{'0', '3', '5', '\0'}));

    /**
     * Magic value part 1.
     */
    public final byte[] magic1;
    /**
     * Magic value part 2.
     */
    public final byte[] magic2;

    /**
     * The file header.
     */
    public final header_item header;
    /**
     * String identifiers list, or <code>null</code>.
     */
    public final string_id_item[] string_ids;
    public final type_id_item[] type_ids;
    public final proto_id_item[] proto_ids;
    public final field_id_item[] field_ids;
    public final method_id_item[] method_ids;
    public final class_def_item[] class_defs;
    /**
     * The parsed file components.
     */
    public final SortedMap<Long, FileComponent> data = new TreeMap<>();
    public Type_ubyte[] link_data;

    /**
     * <pre>
     * java:S3776 - Cognitive Complexity of methods should not be too high - We need this logic together
     * </pre>
     *
     * @param file
     * @throws IOException
     * @throws FileFormatException
     */
    @SuppressWarnings("java:S3776")
    public DexFile(File file) throws IOException, FileFormatException {
        super(file);

        // Check the file signature
        this.magic1 = new byte[DEX_FILE_MAGIC1.size()];
        this.magic2 = new byte[DEX_FILE_MAGIC2.size()];
        System.arraycopy(super.fileByteArray, 0, magic1, 0, DEX_FILE_MAGIC1.size());
        System.arraycopy(super.fileByteArray, 4, magic2, 0, DEX_FILE_MAGIC2.size());

        byte[] magic1Const = new byte[]{DEX_FILE_MAGIC1.get(0), DEX_FILE_MAGIC1.get(1), DEX_FILE_MAGIC1.get(2), DEX_FILE_MAGIC1.get(3)};
        if (!BytesTool.isByteArraySame(magic1Const, magic1)
                || magic2[DEX_FILE_MAGIC2.size() - 1] != DEX_FILE_MAGIC2.get(DEX_FILE_MAGIC2.size() - 1)) {
            throw new FileFormatException("This is not a valid DEX file, because the DEX file signature does not exist at the beginning of this file.");
        }

        // Parse section by section
        PosDataInputStream parseEndian = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        BytesTool.skip(parseEndian, DEX_FILE_MAGIC1.size());
        BytesTool.skip(parseEndian, DEX_FILE_MAGIC2.size());
        BytesTool.skip(parseEndian, Type_uint.LENGTH);           // checksum
        BytesTool.skip(parseEndian, 20);                         // signature
        BytesTool.skip(parseEndian, Type_uint.LENGTH);           // file_size
        BytesTool.skip(parseEndian, Type_uint.LENGTH);           // header_size

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
        SortedMap<Long, Class<?>> todoData = new TreeMap<>();

        // Header
        BytesTool.skip(stream, DEX_FILE_MAGIC1.size());
        BytesTool.skip(stream, DEX_FILE_MAGIC2.size());
        this.header = new header_item(stream);

        // string_ids
        if (this.header.string_ids_off.intValue() == 0) {
            this.string_ids = null;
        } else {
            stream.flyTo(this.header.string_ids_off.intValue());
            this.string_ids = new string_id_item[this.header.string_ids_size.intValue()];
            for (int i = 0; i < this.string_ids.length; i++) {
                this.string_ids[i] = new string_id_item(stream);
                todoData.put(this.string_ids[i].string_data_off.value, string_data_item.class);
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
                    todoData.put(this.proto_ids[i].parameters_off.value, type_list.class);
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
                this.class_defs[i] = new class_def_item(stream);

                if (this.class_defs[i].interfaces_off.value != 0) {
                    todoData.put(this.class_defs[i].interfaces_off.value, type_list.class);
                }
                if (this.class_defs[i].annotations_off.value != 0) {
                    todoData.put(this.class_defs[i].annotations_off.value, annotations_directory_item.class);
                }
                if (this.class_defs[i].class_data_off.value != 0) {
                    todoData.put(this.class_defs[i].class_data_off.value, class_data_item.class);
                }
                if (this.class_defs[i].class_data_off.value != 0) {
                    todoData.put(this.class_defs[i].static_values_off.value, encoded_array_item.class);
                }
            }
        }

        // data
        for (Map.Entry<Long, Class<?>> todoItem : todoData.entrySet()) {
            this.parseData(todoItem.getKey(), todoItem.getValue(), stream);
        }
    }

    void parseData(Long offset, Class<?> type, PosDataInputStreamDex stream) throws IOException, FileFormatException{
        int breakPos = stream.getPos();

        Constructor<?> cons = type.getDeclaredConstructors()[0];
        stream.flyTo(offset.intValue());
        try {
            switch(cons.getParameterCount()) {
                case 1:
                    this.data.put(offset, (FileComponent) cons.newInstance(stream));
                    break;
                case 2:
                    this.data.put(offset, (FileComponent) cons.newInstance(stream, this));
                    break;
                default:
                    throw new FileFormatException(String.format("Coding issue: no suitable constructor for type %s at 0x%X", type.getSimpleName(), offset.intValue()));
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            String msg = String.format("newInstance failed for data item for type %s at 0x%X", type.getSimpleName(), offset.intValue());
            LOGGER.severe(msg);
            throw new IOException(msg, e);
        }

        stream.flyTo(breakPos);
    }

    static void check_uint(String fieldName, Type_uint uint, int streamPosition) throws FileFormatException {
        if (uint.value > Integer.MAX_VALUE) {
            throw new FileFormatException(String.format("%s is too big cannot be handled here: %d, position 0x%X", fieldName, uint.value, streamPosition));
        }
    }

    @Override
    public Icon getIcon() {
        return UITool.icon4Dex();
    }

    /**
     * Get the underlying String value for a {@link #string_ids} item.
     *
     * @param index Index in the {@link #string_ids} array
     * @return String value for the {@link #string_ids} item, or
     * <code>null</code> for invalid index
     */
    public String get_string_ids_string(int index) {
        if (index < -1 || this.string_ids == null || index >= this.string_ids.length) {
            LOGGER.log(Level.WARNING, "Return null for invalid string_ids index={0}", index);
            return null;
        }

        FileComponent fc = this.data.get(this.string_ids[index].string_data_off.value);
        if (fc instanceof string_data_item) {
            return ((string_data_item) fc).getString();
        } else {
            return null;
        }
    }

    /**
     * Get the underlying String value for a {@link #type_ids} item.
     *
     * @param index Index in the {@link #type_ids} array
     * @return String value for the {@link #type_ids} item, or <code>null</code>
     * for invalid index
     */
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
        (new JTreeDexFile()).generateTreeNode(parentNode, this);
    }
}
