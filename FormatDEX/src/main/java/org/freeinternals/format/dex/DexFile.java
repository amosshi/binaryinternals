/*
 * DEXFile.java    June 14, 2015, 22:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.biv.ui.dex.TreeNodeGenerator;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.dex.HeaderItem.Endian;

/**
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class DexFile extends FileFormat {

    /**
     * The constant array/string {@link #DEX_FILE_MAGIC} is the list of bytes
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
    public static final byte[] DEX_FILE_MAGIC1 = {'d', 'e', 'x', '\n'};
    public static final byte[] DEX_FILE_MAGIC2 = {'0', '3', '5', '\0'};

    /**
     * The file header.
     */
    public HeaderItem header;
    /**
     * String identifiers list, or <code>null</code> if
     * {@link HeaderItem#string_ids_off} is <code>0</code>.
     */
    public StringIdItem[] string_ids;
    public TypeIdItem[] type_ids;
    public ProtoIdItem[] proto_ids;
    public FieldIdItem[] field_ids;
    public MethodIdItem[] method_ids;
    public ClassDefItem[] class_defs;
    // public Dex_ubyte[] data;
    /**
     * The parsed file components.
     */
    public SortedMap<Long, FileComponent> data = new TreeMap<>();
    public Dex_ubyte[] link_data;

    public DexFile(File file) throws IOException, FileFormatException {
        super(file);

        // Check the file signature
        byte[] magic1 = new byte[DEX_FILE_MAGIC1.length];
        byte[] magic2 = new byte[DEX_FILE_MAGIC2.length];
        System.arraycopy(super.fileByteArray, 0, magic1, 0, DEX_FILE_MAGIC1.length);
        System.arraycopy(super.fileByteArray, 4, magic2, 0, DEX_FILE_MAGIC2.length);
        if (Tool.isByteArraySame(DEX_FILE_MAGIC1, magic1) == false
                || magic2[DEX_FILE_MAGIC2.length - 1] != DEX_FILE_MAGIC2[DEX_FILE_MAGIC2.length - 1]) {
            throw new FileFormatException("This is not a valid DEX file, because the DEX file signature does not exist at the beginning of this file.");
        }

        this.parse();
    }

    /**
     * Get the underlying String value for a {@link #string_ids} item.
     *
     * @param index Index in the {@link #string_ids} array
     * @return String value for the {@link #string_ids} item, or <code>null</code> for invalid index
     */
    public String getString(int index) {
        if (index < -1 || this.string_ids == null || index >= this.string_ids.length) {
            return null;
        }

        FileComponent fc = this.data.get(this.string_ids[index].string_data_off.value);
        if (fc instanceof StringDataItem) {
            return ((StringDataItem)fc).getString();
        } else {
            return null;
        }
    }
    
    public String getTypeDescriptor(int index){
        if (index < -1 || this.type_ids == null || index >= this.type_ids.length) {
            return null;
        }
        
        return this.getString(this.type_ids[index].descriptor_idx.intValue());
    }

    private void parse() throws IOException, FileFormatException {
        PosDataInputStream parseEndian = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        parseEndian.skip(DEX_FILE_MAGIC1.length);
        parseEndian.skip(DEX_FILE_MAGIC2.length);
        parseEndian.skip(Dex_uint.LENGTH);  // checksum
        parseEndian.skip(20);               // signature
        parseEndian.skip(Dex_uint.LENGTH);  // file_size
        parseEndian.skip(Dex_uint.LENGTH);  // header_size

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

        SortedMap<Long, Class> todoData = new TreeMap<>();

        // Header
        stream.skip(DEX_FILE_MAGIC1.length);
        stream.skip(DEX_FILE_MAGIC2.length);
        this.header = new HeaderItem(stream);

        // string_ids
        if (this.header.string_ids_off.intValue() == 0) {
            this.string_ids = null;
        } else {
            stream.flyTo(this.header.string_ids_off.intValue());
            this.string_ids = new StringIdItem[this.header.string_ids_size.intValue()];
            for (int i = 0; i < this.string_ids.length; i++) {
                this.string_ids[i] = new StringIdItem(stream);
                todoData.put(this.string_ids[i].string_data_off.value, StringDataItem.class);
            }
        }

        // type_ids
        if (this.header.type_ids_off.intValue() == 0) {
            this.type_ids = null;
        } else {
            stream.flyTo(this.header.type_ids_off.intValue());
            this.type_ids = new TypeIdItem[this.header.type_ids_size.intValue()];
            for (int i = 0; i < this.type_ids.length; i++) {
                this.type_ids[i] = new TypeIdItem(stream);
            }
        }

        // proto_ids
        if (this.header.proto_ids_off.intValue() == 0) {
            this.proto_ids = null;
        } else {
            stream.flyTo(this.header.proto_ids_off.intValue());
            this.proto_ids = new ProtoIdItem[this.header.proto_ids_size.intValue()];
            for (int i = 0; i < this.proto_ids.length; i++) {
                this.proto_ids[i] = new ProtoIdItem(stream);
            }
        }

        // field_ids
        if (this.header.field_ids_off.intValue() == 0) {
            this.field_ids = null;
        } else {
            stream.flyTo(this.header.field_ids_off.intValue());
            this.field_ids = new FieldIdItem[this.header.field_ids_size.intValue()];
            for (int i = 0; i < this.field_ids.length; i++) {
                this.field_ids[i] = new FieldIdItem(stream);
            }
        }

        // method_ids
        if (this.header.method_ids_off.intValue() == 0) {
            this.method_ids = null;
        } else {
            stream.flyTo(this.header.method_ids_off.intValue());
            this.method_ids = new MethodIdItem[this.header.method_ids_size.intValue()];
            for (int i = 0; i < this.method_ids.length; i++) {
                this.method_ids[i] = new MethodIdItem(stream);
            }
        }

        // class_defs
        if (this.header.class_defs_off.intValue() == 0) {
            this.class_defs = null;
        } else {
            stream.flyTo(this.header.class_defs_off.intValue());
            this.class_defs = new ClassDefItem[this.header.class_defs_size.intValue()];
            for (int i = 0; i < this.class_defs.length; i++) {
                this.class_defs[i] = new ClassDefItem(stream);
            }
        }

        // data
        for (Map.Entry<Long, Class> todoItem : todoData.entrySet()) {
            if (todoItem.getValue() == StringDataItem.class) {
                stream.flyTo(todoItem.getKey().intValue());
                this.data.put(todoItem.getKey(), new StringDataItem(stream));
            }

        }

    }

    @Override
    public String getContentTabName() {
        return "Android DEX File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        TreeNodeGenerator.generate(this, parentNode);
    }
}
