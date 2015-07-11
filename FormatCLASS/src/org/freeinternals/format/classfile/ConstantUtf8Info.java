/*
 * ConstantUtf8Info.java    4:52 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The class for the {@code CONSTANT_Utf8_info} structure in constant pool.
 * The {@code CONSTANT_Utf8_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Utf8_info {
 *        u1 tag;
 *        u2 length;
 *        u1 bytes[length];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#7963">
 * VM Spec: The CONSTANT_Utf8_info Structure
 * </a>
 */
public class ConstantUtf8Info extends CPInfo {

    public final u2 length_utf8;
    private final byte[] bytes;

    ConstantUtf8Info(final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(CPInfo.ConstantType.CONSTANT_Utf8.tag);
        super.startPos = posDataInputStream.getPos() - 1;

        this.length_utf8 = new u2(posDataInputStream.readUnsignedShort());
        this.bytes = new byte[this.length_utf8.value];
        final int bytesRead = posDataInputStream.read(this.bytes);
        if (bytesRead != this.length_utf8.value) {
            throw new FileFormatException("Read bytes for CONSTANT_Utf8 error.");
        }

        super.length = this.length_utf8.value + 1 + 2;
    }

    @Override
    public String getName() {
        return "Utf8";
    }

    @Override
    public String getDescription() {
        return String.format("ConstantUtf8Info: Start Position: [%d], length: [%d], value: [%s].", this.startPos, super.length, this.getValue());
    }

    /**
     * Get a string for the content of the Utf8 info.
     *
     * @return String for the content
     */
    public String getValue() {
        return new String(this.bytes);
    }
}
