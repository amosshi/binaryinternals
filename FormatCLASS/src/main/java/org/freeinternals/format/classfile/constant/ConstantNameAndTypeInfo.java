/*
 * ConstantNameAndTypeInfo.java    4:46 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_NameAndType_info} structure in constant
 * pool. The {@code CONSTANT_NameAndType_info} structure has the following
 * format:
 *
 * <pre>
 *    CONSTANT_NameAndType_info {
 *        u1 tag;
 * 
 *        u2 name_index;
 *        u2 descriptor_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.6">
 * VM Spec: The CONSTANT_NameAndType_info Structure
 * </a>
 */
public class ConstantNameAndTypeInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 name_index;
    public final u2 descriptor_index;

    ConstantNameAndTypeInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_NameAndType.tag, false, ClassFile.Version.Format_45_3, JavaSEVersion.Version_1_0_2);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.name_index = new u2(posDataInputStream);
        this.descriptor_index = new u2(posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_NameAndType.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: name_index=[%d], descriptor_index=[%d].",
                this.getName(), this.startPos, this.length, this.name_index.value, this.descriptor_index.value);
    }
    
    @Override
    public String toString(CPInfo[] constant_pool) {
        return null;
    }
}
