/*
 * MajorVersion.java    9:11 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Major version of a {@code class} file.
 * It is the {@code major_version} in {@code ClassFile} structure.
 *
 * <pre>
 * The Java virtual machine implementation of Sun's JDK release 1.0.2 supports 
 * class file format versions 45.0 through 45.3 inclusive. Sun's JDK releases 
 * 1.1.X can support class file formats of versions in the range 45.0 through 
 * 45.65535 inclusive. Implementations of version 1.2 of the Java 2 platform 
 * can support class file formats of versions in the range 45.0 through 46.0 
 * inclusive.
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#major_version
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#75883">
 * VM Spec: Comment 1 of the Class File Spec
 * </a>
 */
public class MajorVersion extends U2ClassComponent {

    MajorVersion(final PosDataInputStream posDataInputStream)
            throws java.io.IOException {
        super(posDataInputStream);
    }
}
