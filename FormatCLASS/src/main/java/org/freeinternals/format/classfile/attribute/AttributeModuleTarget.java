/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code ModuleTarget} attribute. The {@code ModuleTarget}
 * attribute has the following format:
 *
 * <pre>
 *    ModuleTarget_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        // index to a CONSTANT_utf8_info structure
 *        u2 os_arch_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 9
 * @see
 * <a href="https://openjdk.java.net/jeps/261"> JEP 261: Module System</a>
 */
public class AttributeModuleTarget extends AttributeInfo {

    /**
     * Index to a {@link ConstantUtf8Info} structure.
     */
    public transient final u2 os_arch_index;

    AttributeModuleTarget(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.Format_53_0, JavaSEVersion.Version_9);
        this.os_arch_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }
}
