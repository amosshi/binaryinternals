/*
 * AttributeExtended.java    12:37 AM, August 11, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/// The class for the Unrecognized attribute. Non-standard attribute, all of the
/// attributes which are not defined in the VM Spec will be represented by this
/// class.
///
/// ```
///    attribute_info {
///        u2 attribute_name_index;
///        u4 attribute_length;
///
///        u1 info[attribute_length];
///    }
/// ```
///
/// The `info` is the raw byte array data.
///
///
/// @author Amos Shi
/// @since Java 1.0.2
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings("java:S101")
public class Unrecognized_attribute extends attribute_info {

    private static final Logger LOG = Logger.getLogger(Unrecognized_attribute.class.getName());
    private byte[] rawData;

    Unrecognized_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        LOG.log(Level.WARNING, "Un-recognized Attribute Found !!! Type = {0}", type);

        if (this.attribute_length.value > 0) {
            this.rawData = new byte[this.attribute_length.value];
            int readBytes = posDataInputStream.read(this.rawData);
            if (readBytes != this.attribute_length.value) {
                throw new IOException("Failed to read %d bytes, actual bytes red %d".formatted(this.attribute_length.value, readBytes));
            }
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /// Get the value of `info`, in raw data format.
    ///
    /// @return The value of `info`
    public byte[] getRawData() {
        return this.rawData.clone();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        if (this.attribute_length.value > 0) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.getStartPos() + 6,
                    this.attribute_length.value,
                    "raw data"
            )));
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Unrecognized";
    }
}
