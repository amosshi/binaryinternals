/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.u2;

/**
 *
 * The class for the {@code NestMembers} attribute. The {@code NestMembers}
 * attribute has the following format:
 *
 * <pre>
 *    NestMembers_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_classes;
 *        u2 classes[number_of_classes];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 11
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.29">
 * VM Spec: The NestMembers Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class NestMembers_attribute extends Classes_attribute {

    public NestMembers_attribute(u2 nameIndex, String type, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }
}
