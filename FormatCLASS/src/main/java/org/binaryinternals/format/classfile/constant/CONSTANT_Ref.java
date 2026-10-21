package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.SignatureConvertor;
import org.binaryinternals.format.classfile.u2;

/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public abstract class CONSTANT_Ref extends cp_info {

    public static final int LENGTH = 5;
    public final u2 class_index;
    public final u2 name_and_type_index;

    protected CONSTANT_Ref(final short tag, final PosDataInputStream posDataInputStream) throws IOException {
        super(tag, posDataInputStream.getPos() - 1, LENGTH);

        this.class_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
    }

    @Override
    public String getDescription() {
        return "%s: Start Position: [%d], length: [%d], value: class_index=[%d], name_and_type_index=[%d].".formatted(
                this.getName(), this.startPos, this.length, this.class_index.value, this.name_and_type_index.value);
    }

    /// Shared {@link #toString(cp_info[])} method for methods.
    ///
    /// @param constantPool Constant pool of current class file.
    /// @return Reader friendly string of current object
    protected String toString4Method(cp_info[] constantPool) {
        // Class
        String clazz = constantPool[this.class_index.value].toString(constantPool);

        // Name and Type
        CONSTANT_NameAndType_info nameType = (CONSTANT_NameAndType_info) constantPool[this.name_and_type_index.value];
        String nameStr = constantPool[nameType.name_index.value].toString(constantPool);
        String typeStr = constantPool[nameType.descriptor_index.value].toString(constantPool);
        String parameters;
        String returnType;

        try {
            parameters = SignatureConvertor.methodParameters(typeStr);
        } catch (FileFormatException ex) {
            parameters = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method parameters: " + typeStr, ex);
        }

        try {
            returnType = SignatureConvertor.methodReturnType(typeStr);
        } catch (FileFormatException ex) {
            returnType = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method return type: " + typeStr, ex);
        }

        return "%s.%s%s : %s".formatted(clazz, nameStr, parameters, returnType);
    }

    protected void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile, String classPerfix) {
        final int classIndex = this.class_index.value;
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "class_index",
                TEXT_CPINDEX_VALUE.formatted(classIndex, classPerfix, classFile.getCPDescription(classIndex)),
                "msg_const_ref_class_index",
                Icons.Offset);

        final int ntIndex = this.name_and_type_index.value;
        this.addNode(parentNode,
                super.startPos + 3,
                2,
                "name_and_type_index",
                TEXT_CPINDEX_PUREVALUE.formatted(ntIndex, classFile.getCPDescription(ntIndex)),
                "msg_const_ref_name_and_type_index",
                Icons.Offset);
    }
}
