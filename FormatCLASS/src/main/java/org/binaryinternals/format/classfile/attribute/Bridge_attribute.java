/*
 * Scala_attribute.java    00:02, August 15, 2021
 *
 * Copyright  2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code Bridge} attribute.
 *
 * The {@code Bridge} attribute has the following format:
 * <pre>
 *    Bridge_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * The following classes has this attribute:
 * <pre>
 *   apache-hive-3.1.2-bin/lib/hive-druid-handler-3.1.2.jar/org/skife/jdbi/org/antlr/runtime/UnbufferedTokenStream.class
 *   netbeans-12.0/ide/modules/ext/antlr-runtime-3.4.jar/org/antlr/runtime/UnbufferedTokenStream.class
 *   sonarqube-8.4.2.36762/extensions/plugins/sonar-scm-svn-plugin-1.10.0.1917.jar/org/antlr/runtime/UnbufferedTokenStream.class
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.4
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Bridge_attribute extends attribute_info {

    public static final int LENGTH = 0;

    Bridge_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != LENGTH) {
            throw new FileFormatException(String.format("The attribute_length of Deprecated is not %d, it is %d.", LENGTH, this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        // Nothing to add
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Bridge";
    }
}
