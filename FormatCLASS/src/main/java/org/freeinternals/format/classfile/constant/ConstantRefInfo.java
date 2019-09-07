/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.SignatureConvertor;
import org.freeinternals.format.classfile.u2;

/**
 *
 * @author Amos Shi
 */
public abstract class ConstantRefInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 class_index;
    public final u2 name_and_type_index;

    protected ConstantRefInfo(short tag, final PosDataInputStream posDataInputStream, ClassFile.Version version, JavaSEVersion javaSE) throws IOException {
        super(tag, false, version, javaSE);

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
     * @param constant_pool  Constant pool of current class file.
     * @return Reader friendly string of current object
     */
    protected String toString4Method(CPInfo[] constant_pool) {
        // Class
        String clazz = constant_pool[this.class_index.value].toString(constant_pool);

        // Name and Type
        ConstantNameAndTypeInfo nameType = (ConstantNameAndTypeInfo) constant_pool[this.name_and_type_index.value];
        String nameStr = constant_pool[nameType.name_index.value].toString(constant_pool);
        String typeStr = constant_pool[nameType.descriptor_index.value].toString(constant_pool);
        String parameters, returnType;

        try {
            parameters = SignatureConvertor.MethodParameters2Readable(typeStr);
        } catch (FileFormatException ex) {
            parameters = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method parameters: " + typeStr, ex);
        }

        try {
            returnType = SignatureConvertor.MethodReturnTypeExtractor(typeStr).toString();
        } catch (FileFormatException ex) {
            returnType = typeStr + UNRECOGNIZED_TYPE;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to parse the method return type: " + typeStr, ex);
        }

        return String.format("%s.%s%s : %s", clazz, nameStr, parameters, returnType);
    }
}
