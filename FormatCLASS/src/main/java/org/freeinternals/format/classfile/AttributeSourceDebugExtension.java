/*
 * AttributeSourceDebugExtension.java    11:00 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The {@code SourceDebugExtension} attribute is an optional attribute in the
 * {@code attributes} table of a {@code ClassFile} structure. There can be no
 * more than one {@code SourceDebugExtension} attribute in the
 * {@code attributes} table of a given {@code ClassFile} structure.
 *
 * The {@code SourceDebugExtension} attribute has the following format:
 * <pre>
 * SourceDebugExtension_attribute {
 *   u2 attribute_name_index;
 *   u4 attribute_length;
 *   u1 debug_extension[attribute_length];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.9">
 * VM Spec: The Signature Attribute
 * </a>
 */
// TODO - This Attribute is not tested - since no test case found
public class AttributeSourceDebugExtension extends AttributeInfo {

    /**
     * The {@link #debug_extension} array holds extended debugging information
     * which has no semantic effect on the Java Virtual Machine. The information
     * is represented using a modified UTF-8 string ({@link ConstantUtf8Info})
     * with no terminating zero byte.
     */
    public transient final byte[] debug_extension;

    AttributeSourceDebugExtension(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream)
            throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (super.attribute_length.value > 0) {
            this.debug_extension = new byte[super.attribute_length.value];
            final int bytesRead = posDataInputStream.read(this.debug_extension);
            if (bytesRead != super.attribute_length.value) {
                throw new FileFormatException("Read bytes for SourceDebugExtension error.");
            }
        } else {
            this.debug_extension = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }
    
    /**
     * Get the {@link #debug_extension} as String.
     * 
     * @return The string of {@link #debug_extension}
     */
    public String getDebugExtesionString(){
        return new String(this.debug_extension);
    }
}
