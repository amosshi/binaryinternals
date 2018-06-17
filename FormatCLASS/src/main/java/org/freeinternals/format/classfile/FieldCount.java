/*
 * FieldsCount.java    10:10 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Fields Count of a {@code class} or {@code interface}. 
 * It is the {@code fields_count} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#fields_count
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class FieldCount extends U2ClassComponent {

    FieldCount(final PosDataInputStream posDataInputStream)
            throws java.io.IOException {
        super(posDataInputStream);
    }
}
