/*
 * SuperClass.java    9:30 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Super class of a {@code class} or {@code interface}.
 * It is the {@code super_class} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#super_class
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class SuperClass extends U2ClassComponent
{
    SuperClass(final PosDataInputStream posDataInputStream)
        throws IOException
    {
        super(posDataInputStream);
    }
}
