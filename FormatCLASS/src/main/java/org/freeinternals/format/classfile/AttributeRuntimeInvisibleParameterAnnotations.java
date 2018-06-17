/*
 * AttributeRuntimeVisibleParameterAnnotations.java    11:25 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The {@code RuntimeInvisibleParameterAnnotations} attribute is similar to the
 * {@code RuntimeVisibleParameterAnnotations} attribute, except that the annotations
 * represented by a {@code RuntimeInvisibleParameterAnnotations} attribute must not be
 * made available for return by reflective APIs, unless the Java Virtual Machine
 * has specifically been instructed to retain these annotations via some
 * implementation-specific mechanism such as a command line flag. In the absence
 * of such instructions, the Java Virtual Machine ignores this attribute.
 *
 * The {@code RuntimeInvisibleParameterAnnotations} attribute has the following
 * format:
 * <pre>
 * RuntimeInvisibleParameterAnnotations_attribute {
 *    u2 attribute_name_index;
 *    u4 attribute_length;
 *    u1 num_parameters;
 *    {   u2         num_annotations;
 *        annotation annotations[num_annotations];
 *    } parameter_annotations[num_parameters];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 8.0
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.19">
 * VM Spec: The RuntimeInvisibleParameterAnnotations attribute
 * </a>
 */
public class AttributeRuntimeInvisibleParameterAnnotations extends AttributeRuntimeParameterAnnotations {

    AttributeRuntimeInvisibleParameterAnnotations(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream)
            throws java.io.IOException, FileFormatException {

        super(nameIndex, type, posDataInputStream);
    }
}
