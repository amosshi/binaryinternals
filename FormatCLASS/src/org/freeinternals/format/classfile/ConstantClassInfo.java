/*
 * ConstantClassInfo.java    4:26 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The class for the {@code CONSTANT_Class_info} structure in constant pool.
 * The {@code CONSTANT_Class_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Class_info {
 *        u1 tag;
 *        u2 name_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#1221">
 * VM Spec:  The CONSTANT_Class_info Structure
 * </a>
 */
public class ConstantClassInfo extends AbstractCPInfo {

    public static final int LENGTH = 3;
    public final u2 name_index;

    ConstantClassInfo(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(AbstractCPInfo.CONSTANT_Class);

        this.startPos = posDataInputStream.getPos() - 1;
        this.length = LENGTH;

        this.name_index = new u2(posDataInputStream.readUnsignedShort());
    }

    @Override
    public String getName() {
        return "Class";
    }

    @Override
    public String getDescription() {
        return String.format("ConstantClassInfo: Start Position: [%d], length: [%d], value: name_index = [%d].", this.startPos, this.length, this.name_index.value);
    }

}
