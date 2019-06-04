/*
 * AttributeDeprecated.java    5:37 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code Deprecated} attribute. The {@code Deprecated}
 * attribute has the following format:
 *
 * <pre>
 *    Deprecated_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see
 * <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#78232">
 * VM Spec: The Deprecated Attribute
 * </a>
 */
public class AttributeDeprecated extends AttributeInfo {

    AttributeDeprecated(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_45_3, JavaSEVersion.Version_1_1);

        if (this.attribute_length.value != 0) {
            throw new FileFormatException(String.format("The attribute_length of AttributeDeprecated is not 0, it is %d.", this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }
}
