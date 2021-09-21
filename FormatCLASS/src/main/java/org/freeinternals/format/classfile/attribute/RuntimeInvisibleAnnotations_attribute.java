/*
 * AttributeRuntimeInvisibleAnnotations.java    11:13 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code RuntimeInvisibleAnnotations} attribute is similar to the
 * {@code RuntimeVisibleAnnotations} attribute, except that the annotations
 * represented by a {@code RuntimeInvisibleAnnotations} attribute must not be
 * made available for return by reflective APIs, unless the Java Virtual Machine
 * has been instructed to retain these annotations via some
 * implementation-specific mechanism such as a command line flag. In the absence
 * of such instructions, the Java Virtual Machine ignores this attribute.
 *
 * The {@code RuntimeInvisibleAnnotations} attribute has the following format:
 * <pre>
 * RuntimeInvisibleAnnotations_attribute {
 *    u2         attribute_name_index;
 *    u4         attribute_length;
 *
 *    u2         num_annotations;
 *    annotation annotations[num_annotations];
 *}
 * </pre>
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.17">
 * VM Spec: The RuntimeInvisibleAnnotations Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeInvisibleAnnotations_attribute extends RuntimeAnnotations_attribute {

    RuntimeInvisibleAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeInvisibleAnnotations";
    }
}
