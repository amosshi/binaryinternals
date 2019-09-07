/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 *
 * @author Amos Shi
 */
public class ConstantRefInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final u2 class_index;
    public final u2 name_and_type_index;
    private final String name;

    protected ConstantRefInfo(short tag, final PosDataInputStream posDataInputStream, String name, ClassFile.Version version, JavaSEVersion javaSE) throws IOException {
        super(tag, false, version, javaSE);

        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.class_index = new u2(posDataInputStream);
        this.name_and_type_index = new u2(posDataInputStream);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: class_index=[%d], name_and_type_index=[%d].",
                this.getName(), this.startPos, this.length, this.class_index.value, this.name_and_type_index.value);
    }
    
    @Override
    public String toString(CPInfo[] constant_pool) {
        return null;
    }

}
