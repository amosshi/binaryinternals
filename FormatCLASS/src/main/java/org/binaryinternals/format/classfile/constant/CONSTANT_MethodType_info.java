/*
 * ConstantMethodTypeInfo.java    12:04 AM, April 28, 2014
 *
 * Copyright 2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.SignatureConvertor;
import org.binaryinternals.format.classfile.u2;

/// The `CONSTANT_MethodHandle_info` structure is used to represent a
/// method handle.
///
/// ```
///    CONSTANT_MethodType_info {
///        u1 tag;
///
///        u2 descriptor_index;
///    }
/// ```
///
/// @author Amos Shi
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.9">
/// VM Spec: The CONSTANT_MethodType_info Structure
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_MethodType_info extends cp_info {

    public static final int LENGTH = 3;
    /// The value of the `descriptor_index` item must be a valid index into
    /// the `constant_pool` table. The `constant_pool` entry at that
    /// index must be a `CONSTANT_Utf8_info` structure representing a
    /// method descriptor.
    public final u2 descriptor_index;

    CONSTANT_MethodType_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_MethodType.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        this.descriptor_index = new u2(posDataInputStream);
        super.length = LENGTH;
    }

    @Override
    public String getDescription() {
        return "%s: Start Position: [%d], length: [%d], descriptor_index: [%d]. ".formatted(
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
            parameters = SignatureConvertor.methodParameters(descriptor);
        } catch (FileFormatException ex) {
            parameters = descriptor + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method parameters: " + descriptor, ex);
        }

        try {
            returnType = SignatureConvertor.methodReturnType(descriptor);
        } catch (FileFormatException ex) {
            returnType = descriptor + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method return type: " + descriptor, ex);
        }

        return "%s : %s".formatted(parameters, returnType);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int cpIndex = this.descriptor_index.value;
        this.addNode(parentNode,
                startPos + 1,
                2,
                "descriptor_index",
                TEXT_CPINDEX_VALUE.formatted(cpIndex, "descriptor", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_const_methodtype_descriptor_index",
                Icons.Descriptor
        );
    }
}
