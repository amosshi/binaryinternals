/*
 * AttributeEnclosingMethod.java    10:48 AM, April 28, 2014
 *
 * Copyright  2004, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.u2;

/**
 * An optional fixed-length attribute in the attributes table of a
 * {@code ClassFile} structure. A {@code class} must have an
 * {@code EnclosingMethod} attribute if and only if it is a local class or an
 * anonymous class. A A {@code class} may have no more than one
 * {@code EnclosingMethod} attribute.
 *
 * @author Amos Shi
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.7">
 * VM Spec: The EnclosingMethod Attribute
 * </a>
 */
public class AttributeEnclosingMethod extends AttributeInfo {

    /**
     * Representing the innermost class that encloses the declaration of the
     * current class.
     */
    public transient final u2 class_index;
    /**
     * Representing the name and type of a method in the class referenced by the
     * {@link #class_index} attribute above.
     */
    public transient final u2 method_index;

    AttributeEnclosingMethod(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.class_index = new u2(posDataInputStream);
        this.method_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }
}
