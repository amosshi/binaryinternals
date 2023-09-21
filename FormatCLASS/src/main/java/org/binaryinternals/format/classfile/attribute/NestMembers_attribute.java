/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.29">
 * VM Spec: The NestMembers Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class NestMembers_attribute extends ClassesCount {

    public NestMembers_attribute(u2 nameIndex, String type, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_NestMembers";
    }

    @Override
    String getMessageKey_4_classes() {
        return "msg_attr_NestMembers__classes";
    }

    @Override
    String getMessageKey_4_number_of_classes() {
        return "msg_attr_NestMembers__number_of_classes";
    }
}
