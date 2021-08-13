package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.SignatureConvertor;
import org.freeinternals.format.classfile.u2;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public abstract class ConstantRefInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 class_index;
    public final u2 name_and_type_index;

    protected ConstantRefInfo(short tag, final PosDataInputStream posDataInputStream) throws IOException {
        super(tag);

        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.class_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: class_index=[%d], name_and_type_index=[%d].",
                this.getName(), this.startPos, this.length, this.class_index.value, this.name_and_type_index.value);
    }

    /**
     * Shared {@link #toString(org.freeinternals.format.classfile.constant.CPInfo[])} method for methods.
     * 
     * @param constantPool  Constant pool of current class file.
     * @return Reader friendly string of current object
     */
    protected String toString4Method(CPInfo[] constantPool) {
        // Class
        String clazz = constantPool[this.class_index.value].toString(constantPool);

        // Name and Type
        ConstantNameAndTypeInfo nameType = (ConstantNameAndTypeInfo) constantPool[this.name_and_type_index.value];
        String nameStr = constantPool[nameType.name_index.value].toString(constantPool);
        String typeStr = constantPool[nameType.descriptor_index.value].toString(constantPool);
        String parameters;
        String returnType;

        try {
            parameters = SignatureConvertor.methodParameters2Readable(typeStr);
        } catch (FileFormatException ex) {
            parameters = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method parameters: " + typeStr, ex);
        }

        try {
            returnType = SignatureConvertor.methodReturnTypeExtractor(typeStr).toString();
        } catch (FileFormatException ex) {
            returnType = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method return type: " + typeStr, ex);
        }

        return String.format("%s.%s%s : %s", clazz, nameStr, parameters, returnType);
    }
}
