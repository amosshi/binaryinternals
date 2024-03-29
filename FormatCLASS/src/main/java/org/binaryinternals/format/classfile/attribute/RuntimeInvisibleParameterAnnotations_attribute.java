/*
 * AttributeRuntimeVisibleParameterAnnotations.java    11:25 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 * The {@code RuntimeInvisibleParameterAnnotations} attribute is similar to the
 * {@code RuntimeVisibleParameterAnnotations} attribute, except that the
 * annotations represented by a {@code RuntimeInvisibleParameterAnnotations}
 * attribute must not be made available for return by reflective APIs, unless
 * the Java Virtual Machine has specifically been instructed to retain these
 * annotations via some implementation-specific mechanism such as a command line
 * flag. In the absence of such instructions, the Java Virtual Machine ignores
 * this attribute.
 *
 * The {@code RuntimeInvisibleParameterAnnotations} attribute has the following
 * format:
 * <pre>
 * RuntimeInvisibleParameterAnnotations_attribute {
 *    u2 attribute_name_index;
 *    u4 attribute_length;
 *
 *    u1 num_parameters;
 *    {   u2         num_annotations;
 *        annotation annotations[num_annotations];
 *    } parameter_annotations[num_parameters];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.19">
 * VM Spec: The RuntimeInvisibleParameterAnnotations attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeInvisibleParameterAnnotations_attribute extends RuntimeParameterAnnotations {

    RuntimeInvisibleParameterAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeInvisibleParameterAnnotations";
    }

    @Override
    String getMessageKey_4_num_parameters() {
        return "msg_attr_RuntimeInvisibleParameterAnnotations__num_parameters";
    }

    @Override
    String getMessageKey_4_parameter_annotations() {
        return "msg_attr_RuntimeInvisibleParameterAnnotations_parameter_annotations";
    }

    @Override
    String getMessageKey_4_parameter_annotations__num_annotations() {
        return "msg_attr_RuntimeInvisibleParameterAnnotations_parameter_annotations__num_annotations";
    }

    @Override
    String getMessageKey_4_parameter_annotations__annotations() {
        return "msg_attr_RuntimeInvisibleParameterAnnotations_parameter_annotations__annotations";
    }
}
