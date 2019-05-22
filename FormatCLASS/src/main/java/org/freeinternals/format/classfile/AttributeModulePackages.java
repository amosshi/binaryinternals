/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * The class for the {@code ModulePackages} attribute. The
 * {@code ModulePackages} attribute has the following format:
 *
 * <pre>
 *    ModulePackages_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u2 package_count;
 *        u2 package_index[package_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 9
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.26">
 * VM Spec: The ModulePackages Attribute
 * </a>
 */
public class AttributeModulePackages extends AttributeInfo {

    /**
     * The value of the package_count item indicates the number of entries in
     * the {@link #package_index}table.
     */
    public transient final u2 package_count;

    /**
     * The value of each entry in the {@link #package_index} table must be a
     * valid index into the {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that index must be a
     * {@link ConstantPackageInfo} structure representing a package in the
     * current module.
     */
    private transient final u2[] package_index;

    AttributeModulePackages(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.package_count = new u2(posDataInputStream);
        if (this.package_count.value > 0) {
            this.package_index = new u2[this.package_count.value];
            for (int i = 0; i < this.package_count.value; i++) {
                this.package_index[i] = new u2(posDataInputStream);
            }
        } else {
            this.package_index = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }
}
