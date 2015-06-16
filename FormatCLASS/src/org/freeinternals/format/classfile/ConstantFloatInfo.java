/*
 * ConstantFloatInfo.java    4:41 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The class for the {@code CONSTANT_Float_info} structure in constant pool.
 * The {@code CONSTANT_Float_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Float_info {
 *        u1 tag;
 *        u4 bytes;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#21942">
 * VM Spec: The CONSTANT_Float_info Structure
 * </a>
 */
public class ConstantFloatInfo extends AbstractCPInfo {

    public static final int LENGTH = 5;
    public final Float floatValue;

    ConstantFloatInfo(final PosDataInputStream posDataInputStream)
        throws IOException
    {
        super(AbstractCPInfo.CONSTANT_Float);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;
        
        this.floatValue = posDataInputStream.readFloat();
    }
    
    @Override
    public String getName()
    {
        return "Float";
    }

    @Override
    public String getDescription()
    {
        return String.format("ConstantFloatInfo: Start Position: [%d], length: [%d], value: [%f].", this.startPos, this.length, this.floatValue);
    }
}
