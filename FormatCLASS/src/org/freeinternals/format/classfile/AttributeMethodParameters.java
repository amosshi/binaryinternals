/*
 * AttributeMethodParameters.java    5:37 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The class for the {@code MethodParameters} attribute.
 * The {@code MethodParameters} attribute has the following format:
 *
 * <pre>
 *    MethodParameters_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u1 parameters_count;
 *        {   u2 name_index;
 *            u2 access_flags;
 *        } parameters[parameters_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 8.0
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.24">
 * VM Spec: The MethodParameters Attribute
 * </a>
 */
public class AttributeMethodParameters extends AttributeInfo {

    AttributeMethodParameters(
            final u2 nameIndex, 
            final String type,
            final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        // TODO - Find a test case for this attribute
        super.checkSize(posDataInputStream.getPos());
    }
}
