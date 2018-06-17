/*
 * ClassFileVersion.java    8:48 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Minor version of a {@code class} file.
 * It is the {@code minor_version} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#minor_version
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class MinorVersion extends U2ClassComponent {

    MinorVersion(final PosDataInputStream posDataInputStream)
            throws java.io.IOException {
        super(posDataInputStream);
    }
}
