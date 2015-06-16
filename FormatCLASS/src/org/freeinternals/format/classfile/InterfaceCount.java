/*
 * InterfaceCount.java    10:01 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Interfaces count of a {@code class} or {@code interface}.
 * It is the {@code interfaces_count} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#interfaces_count
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class InterfaceCount extends U2ClassComponent {

    InterfaceCount(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(posDataInputStream);
    }
}
