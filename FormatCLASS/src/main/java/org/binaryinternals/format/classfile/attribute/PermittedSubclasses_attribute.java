/*
 * PermittedSubclasses_attribute.java    10:38 AM, August 14, 2021
 *
 * Copyright  2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 *
 * The class for the {@code PermittedSubclasses} attribute. The
 * {@code PermittedSubclasses} attribute has the following format:
 *
 * <pre>
 *    PermittedSubclasses_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 number_of_classes;
 *        u2 classes[number_of_classes];
 *    }
 * </pre>
 *
 * Note. The code in current class the same as NestMembers_attribute. We need to
 * merge them later.
 *
 * @author Amos Shi
 * @since Java 17
 * @see <a href="https://openjdk.java.net/jeps/360"> JEP 360</a>
 * @see <a href="https://openjdk.java.net/jeps/409"> JEP 409</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class PermittedSubclasses_attribute extends ClassesCount {

    public PermittedSubclasses_attribute(u2 nameIndex, String type, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_PermittedSubclasses";
    }

    @Override
    String getMessageKey_4_classes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    String getMessageKey_4_number_of_classes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
