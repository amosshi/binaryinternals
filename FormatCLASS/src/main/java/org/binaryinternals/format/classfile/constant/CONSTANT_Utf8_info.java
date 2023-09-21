/*
 * ConstantUtf8Info.java    4:52 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_Utf8_info} structure in constant pool. The
 * {@code CONSTANT_Utf8_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Utf8_info {
 *        u1 tag;
 *
 *        u2 length;
 *        u1 bytes[length];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.7">
 * VM Spec: The CONSTANT_Utf8_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_Utf8_info extends cp_info {

    public final u2 length_utf8;
    public final byte[] bytes;

    /**
     * Buffer for {@link #getValue()}.
     */
    private String value = null;

    CONSTANT_Utf8_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_Utf8.tag);
        super.startPos = posDataInputStream.getPos() - 1;

        this.length_utf8 = new u2(posDataInputStream);
        this.bytes = new byte[this.length_utf8.value];
        final int bytesRead = posDataInputStream.read(this.bytes);
        if (bytesRead != this.length_utf8.value) {
            throw new FileFormatException("Read bytes for CONSTANT_Utf8 error.");
        }

        super.length = this.length_utf8.value + 1 + 2;
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: [%s].", this.getName(), this.startPos, super.length, this.getValue());
    }

    @Override
    public String getMessageKey() {
        return "msg_const_utf8";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Utf8.name();
    }

    /**
     * Get the {@link #bytes} value as a String, using platform's default
     * charset.
     *
     * @return The content as String
     */
    public String getValue() {
        if (this.value == null) {
            this.value = new String(this.bytes, StandardCharsets.UTF_8);
        }

        return this.value;
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return this.getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int floatPos = super.startPos + 1;
        this.addNode(parentNode, floatPos, u2.LENGTH, "length", this.length_utf8.value, "msg_const_utf8_length", Icons.Length);
        floatPos += u2.LENGTH;
        this.addNode(parentNode, floatPos, this.length_utf8.value, "bytes", this.getValue(), "msg_const_utf8_bytes", Icons.Data);
    }
}
