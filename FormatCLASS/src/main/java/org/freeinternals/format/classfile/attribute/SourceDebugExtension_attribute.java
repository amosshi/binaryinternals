/*
 * AttributeSourceDebugExtension.java    11:00 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.freeinternals.format.classfile.u2;

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
 *
 *   u1 debug_extension[attribute_length];
 * }
 * </pre>
 *
 * Note. TODO - This Attribute is not tested - since no test case found
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.11">
 * VM Spec: The Signature Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class SourceDebugExtension_attribute extends attribute_info {

    /**
     * The {@link #debug_extension} array holds extended debugging information
     * which has no semantic effect on the Java Virtual Machine.
     *
     * The information is represented using a modified UTF-8 string
     * ({@link CONSTANT_Utf8_info}) with no terminating zero byte.
     */
    public final byte[] debug_extension;

    SourceDebugExtension_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
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
    public String getDebugExtesionString() {
        return new String(this.debug_extension, StandardCharsets.UTF_8);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        if (this.debug_extension != null && this.debug_extension.length > 0) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 6,
                    this.debug_extension.length,
                    String.format("debug_extension: %s", this.getDebugExtesionString())
            )));
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_SourceDebugExtension";
    }
}
