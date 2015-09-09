/*
 * DEXFile.java    June 14, 2015, 22:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.biv.ui.dex.TreeNodeGenerator;
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
    public Dex_ubyte[] data;
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
            this.field_ids = new FieldIdItem[this.header.field_ids_size.intValue()];
            for (int i = 0; i < this.field_ids.length; i++) {
                this.field_ids[i] = new FieldIdItem(stream);
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
