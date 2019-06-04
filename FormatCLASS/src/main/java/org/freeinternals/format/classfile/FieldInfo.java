/*
 * FieldInfo.java    3:57 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.attribute.AttributeInfo;
import org.freeinternals.format.classfile.constant.CPInfo;

/**
 * {@code Field} of a class or interface. The {@code Field} structure has the
 * following format:
 *
 * <pre>
 *    field_info {
 *        u2 access_flags;
 *        u2 name_index;
 *        u2 descriptor_index;
 *        u2 attributes_count;
 *        attribute_info attributes[attributes_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.5">
 * VM Spec: Fields
 * </a>
 */
public class FieldInfo extends FileComponent {

    public transient final u2 access_flags;
    public transient final u2 name_index;
    public transient final u2 descriptor_index;
    public transient final u2 attributes_count;
    public transient final AttributeInfo[] attributes;
    private String declaration;

    FieldInfo(final PosDataInputStream posDataInputStream, final CPInfo[] cp) throws IOException, FileFormatException {
        this.startPos = posDataInputStream.getPos();
        this.length = -1;

        this.access_flags = new u2(posDataInputStream);
        this.name_index = new u2(posDataInputStream);
        this.descriptor_index = new u2(posDataInputStream);
        this.attributes_count = new u2(posDataInputStream);

        final int attrCount = this.attributes_count.value;
        if (attrCount > 0) {
            this.attributes = new AttributeInfo[attrCount];
            for (int i = 0; i < attrCount; i++) {
                this.attributes[i] = AttributeInfo.parse(posDataInputStream, cp);
            }
        } else {
            this.attributes = null;
        }

        this.calculateLength();
    }

    private void calculateLength() {
        this.length = 8;

        for (int i = 0; i < this.attributes_count.value; i++) {
            this.length += this.attributes[i].getLength();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get raw data
    /**
     * Get the value of {@code attributes}[{@code index}].
     *
     * @param index Index of the field attribute(s)
     * @return The value of {@code attributes}[{@code index}]
     */
    public AttributeInfo getAttribute(final int index) {
        AttributeInfo info = null;
        if (this.attributes != null) {
            info = this.attributes[index];
        }
        return info;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get extracted data
    /**
     * Generate the modifier string from the {@link #access_flags} value.
     *
     * @return A string for modifier
     */
    public String getModifiers() {
        return AccessFlag.getFieldModifier(this.access_flags.value);
    }

    /**
     * Set the declaration string.
     *
     * @param declaration Human readable declaration string
     */
    final void setDeclaration(final String declaration) {
        this.declaration = declaration;
    }

    /**
     * Get the declaration of the field. The declaration is generated by
     * {@code access_flags}, {@code name_index} and {@code descriptor_index}.
     *
     * @return {@code Field} declaration
     */
    public String getDeclaration() {
        return this.declaration;
    }
}
