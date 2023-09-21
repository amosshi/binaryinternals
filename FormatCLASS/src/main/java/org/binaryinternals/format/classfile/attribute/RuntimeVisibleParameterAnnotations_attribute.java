/*
 * AttributeRuntimeVisibleParameterAnnotations.java    11:25 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 * The {@code RuntimeVisibleParameterAnnotations} attribute is a variable-length
 * attribute in the {@code attributes} table of the {@code method_info}
 * structure. The {@code RuntimeVisibleParameterAnnotations} attribute records
 * run-time-visible Java programming language annotations on the parameters of
 * the corresponding method.
 *
 * The {@code RuntimeVisibleParameterAnnotations} attribute has the following
 * format:
 * <pre>
 * RuntimeVisibleParameterAnnotations_attribute {
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
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.18">
 * VM Spec: The RuntimeVisibleParameterAnnotations attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeVisibleParameterAnnotations_attribute extends RuntimeParameterAnnotations {

    RuntimeVisibleParameterAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeVisibleParameterAnnotations";
    }

    @Override
    String getMessageKey_4_num_parameters() {
        return "msg_attr_RuntimeVisibleParameterAnnotations__num_parameters";
    }

    @Override
    String getMessageKey_4_parameter_annotations() {
        return "msg_attr_RuntimeVisibleParameterAnnotations_parameter_annotations";
    }

    @Override
    String getMessageKey_4_parameter_annotations__num_annotations() {
        return "msg_attr_RuntimeVisibleParameterAnnotations_parameter_annotations__num_annotations";
    }

    @Override
    String getMessageKey_4_parameter_annotations__annotations() {
        return "msg_attr_RuntimeVisibleParameterAnnotations_parameter_annotations__annotations";
    }
}
