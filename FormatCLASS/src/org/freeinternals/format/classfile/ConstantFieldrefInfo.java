/*
 * ConstantFieldrefInfo.java    4:31 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The class for the {@code CONSTANT_Fieldref_info} structure in constant pool.
 * The {@code CONSTANT_Fieldref_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Fieldref_info {
 *        u1 tag;
 *        u2 class_index;
 *        u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#42041">
 * VM Spec:  The CONSTANT_Fieldref_info Structure
 * </a>
 */
public class ConstantFieldrefInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 class_index;
    public final u2 name_and_type_index;

    ConstantFieldrefInfo(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Fieldref.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.class_index = new u2(posDataInputStream.readUnsignedShort());
        this.name_and_type_index = new u2(posDataInputStream.readUnsignedShort());
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Fieldref.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: class_index=[%d], name_and_type_index=[%d].", 
                this.getName(), this.startPos, this.length, this.class_index.value, this.name_and_type_index.value);
    }
}
