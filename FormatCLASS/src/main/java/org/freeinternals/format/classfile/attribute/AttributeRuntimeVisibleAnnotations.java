/*
 * AttributeCode.java    09:24 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.16">
 * VM Spec: The RuntimeVisibleAnnotations Attribute
 * </a>
 */
public class AttributeRuntimeVisibleAnnotations extends AttributeRuntimeAnnotations {

    AttributeRuntimeVisibleAnnotations(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_49_0, JavaSEVersion.Version_5_0);
    }
}
