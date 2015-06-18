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
import org.freeinternals.biv.plugin.FileFormat;
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
    public static final byte[] DEX_FILE_MAGIC = {'d', 'e', 'x', '\n', '0', '3', '5', '\0'};

    public DexFile(File file) throws IOException, FileFormatException {
        super(file);
        
        // Check the file signature
        byte[] magic = new byte[DEX_FILE_MAGIC.length];
        System.arraycopy(super.fileByteArray, 0, magic, 0, DEX_FILE_MAGIC.length);
        if (Tool.isByteArraySame(DEX_FILE_MAGIC, magic) == false) {
            throw new FileFormatException("This is not a valid DEX file, because the DEX file signature does not exist at the beginning of this file.");
        }
        
        this.parse();
    }

    private void parse() throws IOException {
        PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));
        
        stream.skip(DEX_FILE_MAGIC.length);
    }

    @Override
    public String getContentTabName() {
        return "Android DEX File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
