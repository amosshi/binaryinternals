/*
 * AttributeSourceFile.java    5:26 AM, August 5, 2007
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
 *    SourceFile_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u2 sourcefile_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#79868">
 * VM Spec: The SourceFile Attribute
 * </a>
 */
public class AttributeSourceFile extends AttributeInfo {

    public transient final u2 sourcefile_index;

    AttributeSourceFile(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 2) {
            throw new FileFormatException(String.format("The attribute_length of AttributeSourceFile is not 2, it is %d.", this.attribute_length.value));
        }

        this.sourcefile_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

}
