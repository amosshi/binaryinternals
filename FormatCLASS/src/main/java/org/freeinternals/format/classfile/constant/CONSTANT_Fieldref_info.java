/*
 * ConstantFieldrefInfo.java    4:31 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.SignatureConvertor;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
 * VM Spec: The CONSTANT_Fieldref_info Structure
 * </a>
 */
public class CONSTANT_Fieldref_info extends CONSTANT_Ref_info {

    CONSTANT_Fieldref_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Fieldref.tag, posDataInputStream);
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
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                2,
                "class_index: " + this.class_index.value + " - " + classFile.getCPDescription(this.class_index.value)
        )));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 3,
                2,
                "name_and_type_index: " + this.name_and_type_index.value + " - " + classFile.getCPDescription(this.name_and_type_index.value)
        )));
    }
}
