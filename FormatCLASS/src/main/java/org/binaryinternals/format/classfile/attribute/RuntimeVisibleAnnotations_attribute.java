/*
 * RuntimeVisibleAnnotations_attribute.java    09:24 AM, April 28, 2014
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
 * The class for the {@code RuntimeVisibleAnnotations} attribute. The
 * {@code RuntimeVisibleAnnotations} attribute has the following format:
 *
 * <pre>
 * RuntimeVisibleAnnotations_attribute {
 *   u2         attribute_name_index;
 *   u4         attribute_length;
 *
 *   u2         num_annotations;
 *   annotation annotations[num_annotations];
 * }
 *
 * annotation {
 *   u2 type_index;
 *   u2 num_element_value_pairs;
 *   {   u2            element_name_index;
 *       element_value value;
 *   } element_value_pairs[num_element_value_pairs];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16">
 * VM Spec: The RuntimeVisibleAnnotations Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeVisibleAnnotations_attribute extends RuntimeAnnotations {

    RuntimeVisibleAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeVisibleAnnotations";
    }

    @Override
    String getMessageKey_4_annotations() {
        return "msg_attr_RuntimeVisibleAnnotations__annotations";
    }

    @Override
    String getMessageKey_4_num_annotations() {
        return "msg_attr_RuntimeVisibleAnnotations__num_annotations";
    }
}
