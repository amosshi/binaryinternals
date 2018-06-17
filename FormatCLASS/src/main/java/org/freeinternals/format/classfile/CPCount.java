/*
 * CPCount.java    9:24 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Constant Pool Count of a {@code class} or {@code interface}.
 * It is the {@code constant_pool_count} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#constant_pool_count
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class CPCount extends U2ClassComponent {

    CPCount(final PosDataInputStream posDataInputStream)
            throws java.io.IOException {
        super(posDataInputStream);
    }
}
