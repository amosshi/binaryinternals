/*
 * AttributesCount.java    10:19 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.U2ClassComponent;

/**
 * Attributes count of a {@code class} or {@code interface}. It is the
 * {@code attributes_count} in {@code ClassFile} structure.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#attributes_count
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class AttributeCount extends U2ClassComponent {

    AttributeCount(final PosDataInputStream posDataInputStream) throws IOException {
        super(posDataInputStream);
    }
}
