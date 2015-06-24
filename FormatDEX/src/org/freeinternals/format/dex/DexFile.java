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
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;

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

    public HeaderItem header;
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

    private void parse() throws IOException {
        PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        stream.skip(DEX_FILE_MAGIC1.length);
        stream.skip(DEX_FILE_MAGIC2.length);
    }

    @Override
    public String getContentTabName() {
        return "Android DEX File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
