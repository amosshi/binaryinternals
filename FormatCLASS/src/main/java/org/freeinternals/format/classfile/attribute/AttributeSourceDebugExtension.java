/*
 * AttributeSourceDebugExtension.java    11:00 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
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
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.11">
 * VM Spec: The Signature Attribute
 * </a>
 */
// TODO - This Attribute is not tested - since no test case found
public class AttributeSourceDebugExtension extends AttributeInfo {

    /**
     * The {@link #debug_extension} array holds extended debugging information
     * which has no semantic effect on the Java Virtual Machine. The information
     * is represented using a modified UTF-8 string ({@link org.freeinternals.format.classfile.constant.ConstantUtf8Info})
     * with no terminating zero byte.
     */
    public transient final byte[] debug_extension;

    AttributeSourceDebugExtension(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_49_0, JavaSEVersion.Version_5_0);

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
        return new String(this.debug_extension);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        if (this.debug_extension != null && this.debug_extension.length > 0) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 6,
                    this.debug_extension.length,
                    String.format("debug_extension: %s", this.getDebugExtesionString())
            )));
        }
    }
}
