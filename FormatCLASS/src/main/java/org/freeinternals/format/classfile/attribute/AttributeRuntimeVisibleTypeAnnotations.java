/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The RuntimeVisibleTypeAnnotations attribute records run-time visible
 * annotations on types used in the declaration of the corresponding class,
 * field, or method, or in an expression in the corresponding method body. The
 * RuntimeVisibleTypeAnnotations attribute also records run-time visible
 * annotations on type parameter declarations of generic classes, interfaces,
 * methods, and constructors. The Java Virtual Machine must make these
 * annotations available so they can be returned by the appropriate reflective
 * APIs.
 *
 * The RuntimeVisibleTypeAnnotations attribute has the following format:
 * <pre>
 *   RuntimeVisibleTypeAnnotations_attribute {
 *     u2              attribute_name_index;
 *     u4              attribute_length;
 *
 *     u2              num_annotations;
 *     type_annotation annotations[num_annotations];
 *   }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 8.0
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20">
 * VM Spec: The RuntimeVisibleTypeAnnotations Attribute
 * </a>
 */
public class AttributeRuntimeVisibleTypeAnnotations extends AttributeRuntimeTypeAnnotations {

    public AttributeRuntimeVisibleTypeAnnotations(u2 nameIndex, String name, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, name, posDataInputStream, ClassFile.Version.Format_52_0, JavaSEVersion.Version_8);
    }

}
