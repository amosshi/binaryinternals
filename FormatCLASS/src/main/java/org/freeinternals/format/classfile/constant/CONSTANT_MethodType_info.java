/*
 * ConstantMethodTypeInfo.java    12:04 AM, April 28, 2014
 *
 * Copyright 2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.SignatureConvertor;
import org.freeinternals.format.classfile.u2;

/**
 * The {@code CONSTANT_MethodHandle_info} structure is used to represent a
 * method handle.
 *
 * <pre>
 *    CONSTANT_MethodType_info {
 *        u1 tag;
 * 
 *        u2 descriptor_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.9">
 * VM Spec: The CONSTANT_MethodType_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_MethodType_info extends cp_info {

    public static final int LENGTH = 3;
    /**
     * The value of the {@code descriptor_index} item must be a valid index into
     * the {@code constant_pool} table. The {@code constant_pool} entry at that
     * index must be a {@code CONSTANT_Utf8_info} structure representing a
     * method descriptor.
     */
    public final u2 descriptor_index;

    CONSTANT_MethodType_info(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(cp_info.ConstantType.CONSTANT_MethodType.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        this.descriptor_index = new u2(posDataInputStream);
        super.length = LENGTH;
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], descriptor_index: [%d]. ",
                this.getName(), this.startPos, super.length, this.descriptor_index.value);
    }
    
    @Override
    public String getMessageKey() {
        return "msg_const_methodtype";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_MethodType.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        String descriptor = constantPool[this.descriptor_index.value].toString(constantPool);
        String parameters;
        String returnType;

        try {
            parameters = SignatureConvertor.methodParameters2Readable(descriptor);
        } catch (FileFormatException ex) {
            parameters = descriptor + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method parameters: " + descriptor, ex);
        }

        try {
            returnType = SignatureConvertor.methodReturnTypeExtractor(descriptor).toString();
        } catch (FileFormatException ex) {
            returnType = descriptor + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method return type: " + descriptor, ex);
        }

        return String.format("%s : %s", parameters, returnType);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int cpIndex = this.descriptor_index.value;
        this.addNode(parentNode,
                startPos + 1,
                2,
                "descriptor_index",
                String.format(TEXT_CPINDEX_VALUE, cpIndex, "descriptor", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_const_methodtype_descriptor_index",
                Icons.Descriptor
        );
    }
}
