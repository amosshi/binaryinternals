/*
 * ConstantUtf8Info.java    4:52 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.7">
 * VM Spec: The CONSTANT_Utf8_info Structure
 * </a>
 */
public class ConstantUtf8Info extends CPInfo {

    public final u2 length_utf8;
    public final byte[] bytes;
    
    /**
     * Buffer for {@link #getValue()}.
     */
    private String value = null;

    ConstantUtf8Info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(CPInfo.ConstantType.CONSTANT_Utf8.tag, false, ClassFile.Version.Format_45_3, JavaSEVersion.Version_1_0_2);
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
    public String getName() {
        return ConstantType.CONSTANT_Utf8.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: [%s].", this.getName(), this.startPos, super.length, this.getValue());
    }

    /**
     * Get the {@link #bytes} value as a String, using platform's default
     * charset.
     *
     * @return The content as String
     */
    public String getValue() {
        if (this.value == null) {
            this.value = new String(this.bytes);
        }
        
        return this.value;
    }
    
    @Override
    public String toString(CPInfo[] constant_pool) {
        return this.getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "length: " + this.length_utf8.value
        )));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                this.length_utf8.value,
                "bytes: " + this.getValue()
        )));
    }
}
