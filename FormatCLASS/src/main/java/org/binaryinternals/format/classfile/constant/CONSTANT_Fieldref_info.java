/*
 * ConstantFieldrefInfo.java    4:31 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.SignatureConvertor;

/**
 * The class for the {@code CONSTANT_Fieldref_info} structure in constant pool.
 * The {@code CONSTANT_Fieldref_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Fieldref_info {
 *        u1 tag;
 *        u2 class_index;
 *        u2 name_and_type_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.4.2">
 * VM Spec: The CONSTANT_Fieldref_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_Fieldref_info extends CONSTANT_Ref {

    CONSTANT_Fieldref_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Fieldref.tag, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_ref";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Fieldref.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        // Class
        String clazz = constantPool[this.class_index.value].toString(constantPool);

        // Name and Type
        CONSTANT_NameAndType_info nameType = (CONSTANT_NameAndType_info) constantPool[this.name_and_type_index.value];
        String name = constantPool[nameType.name_index.value].toString(constantPool);
        String type = constantPool[nameType.descriptor_index.value].toString(constantPool);
        String typeDesc;

        try {
            typeDesc = SignatureConvertor.fieldDescriptorExtractor(type).toString();
        } catch (FileFormatException ex) {
            typeDesc = type + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the field type: " + type, ex);
        }

        return String.format("%s.%s : %s", clazz, name, typeDesc);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        super.generateTreeNode(parentNode,
                (ClassFile) format,
                "class name"
        );
    }
}
