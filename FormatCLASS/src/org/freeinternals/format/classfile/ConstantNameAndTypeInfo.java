/*
 * ConstantNameAndTypeInfo.java    4:46 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The class for the {@code CONSTANT_NameAndType_info} structure in constant pool.
 * The {@code CONSTANT_NameAndType_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_NameAndType_info {
 *        u1 tag;
 *        u2 name_index;
 *        u2 descriptor_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#1327">
 * VM Spec: The CONSTANT_NameAndType_info Structure
 * </a>
 */
public class ConstantNameAndTypeInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 name_index;
    public final u2 descriptor_index;

    ConstantNameAndTypeInfo(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(CPInfo.ConstantType.CONSTANT_NameAndType.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.name_index = new u2(posDataInputStream.readUnsignedShort());
        this.descriptor_index = new u2(posDataInputStream.readUnsignedShort());
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_NameAndType.name();
    }

    @Override
    public String getDescription() {
        return String.format("ConstantNameAndTypeInfo: Start Position: [%d], length: [%d], value: name_index=[%d], descriptor_index=[%d].", this.startPos, this.length, this.name_index.value, this.descriptor_index.value);
    }
}
