/*
 * AttributeRuntimeVisibleParameterAnnotations.java    11:25 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

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
 * @since JDK 8.0
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.18">
 * VM Spec: The RuntimeVisibleParameterAnnotations attribute
 * </a>
 */
public class AttributeRuntimeVisibleParameterAnnotations extends AttributeRuntimeParameterAnnotations {

    AttributeRuntimeVisibleParameterAnnotations(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_49_0, JavaSEVersion.Version_5_0);
    }
}
