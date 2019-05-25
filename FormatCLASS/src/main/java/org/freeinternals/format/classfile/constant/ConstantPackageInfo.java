/*
 * ConstantPackageInfo.java    00:19 AM, July 19, 2018
 *
 * Copyright  2018, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_Package_info} structure in constant pool.
 * The {@code CONSTANT_Package_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Package_info {
 *        u1 tag;
 *        u2 name_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 10.0
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se10/html/jvms-4.html#jvms-4.4.12">
 * VM Spec: The CONSTANT_Package_info Structure
 * </a>
 */
public class ConstantPackageInfo extends CPInfo {

    public static final int LENGTH = 3;
    public final u2 name_index;

    ConstantPackageInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Package.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.name_index = new u2(posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Package.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: name_index=[%d].",
                this.getName(), this.startPos, this.length, this.name_index.value);
    }
}
