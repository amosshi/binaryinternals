/*
 * AttributeSynthetic.java    5:25 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The class for the {@code SourceFile} attribute.
 * The {@code SourceFile} attribute has the following format:
 *
 * <pre>
 *    Synthetic_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#80128">
 * VM Spec: The Synthetic Attribute
 * </a>
 */
public class AttributeSynthetic extends AttributeInfo {

    AttributeSynthetic(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 0) {
            throw new FileFormatException(String.format("The attribute_length of AttributeSynthetic is not 0, it is %d.", this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }
}
