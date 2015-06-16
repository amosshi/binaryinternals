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
 * @since JDK 6.0
 * @see
 * <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#2877">
 * VM Spec: Fields
 * </a>
 */
public class FieldInfo extends FileComponent {

    public transient final u2 access_flags;
    public transient final u2 name_index;
    public transient final u2 descriptor_index;
    public transient final u2 attributes_count;
    public transient final AttributeInfo[] attributes;
    /**
     * Value for access flag {@code ACC_PUBLIC} for a {@code Field}.
     */
    public static final int ACC_PUBLIC = 0x0001;
    /**
     * Value for access flag {@code ACC_PRIVATE} for a {@code Field}.
     */
    public static final int ACC_PRIVATE = 0x0002;
    /**
     * Value for access flag {@code ACC_PROTECTED} for a {@code Field}.
     */
    public static final int ACC_PROTECTED = 0x0004;
    /**
     * Value for access flag {@code ACC_STATIC} for a {@code Field}.
     */
    public static final int ACC_STATIC = 0x0008;
    /**
     * Value for access flag {@code ACC_FINAL} for a {@code Field}.
     */
    public static final int ACC_FINAL = 0x0010;
    /**
     * Value for access flag {@code ACC_VOLATILE} for a {@code Field}.
     */
    public static final int ACC_VOLATILE = 0x0040;
    /**
     * Value for access flag {@code ACC_TRANSIENT} for a {@code Field}.
     */
    public static final int ACC_TRANSIENT = 0x0080;
    private String declaration;

    FieldInfo(final PosDataInputStream posDataInputStream, final AbstractCPInfo[] cp)
            throws IOException, FileFormatException {
        this.startPos = posDataInputStream.getPos();
        this.length = -1;

        this.access_flags = new u2(posDataInputStream.readUnsignedShort());
        this.name_index = new u2(posDataInputStream.readUnsignedShort());
        this.descriptor_index = new u2(posDataInputStream.readUnsignedShort());
        this.attributes_count = new u2(posDataInputStream.readUnsignedShort());

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
     * Generate the modifier of a {@code Field} from the {@code access_flags}
     * value.
     *
     * @return A string for modifier
     */
    public String getModifiers() {
        final StringBuilder sb = new StringBuilder(20);
        Boolean isTheFirstModifier = true;

        if ((this.access_flags.value & FieldInfo.ACC_PUBLIC) > 0) {
            sb.append("public");
            isTheFirstModifier = false;
        } else if ((this.access_flags.value & FieldInfo.ACC_PRIVATE) > 0) {
            sb.append("private");
            isTheFirstModifier = false;
        } else if ((this.access_flags.value & FieldInfo.ACC_PROTECTED) > 0) {
            sb.append("protected");
            isTheFirstModifier = false;
        }

        if ((this.access_flags.value & FieldInfo.ACC_STATIC) > 0) {
            if (isTheFirstModifier == false) {
                sb.append(' ');
            }
            sb.append("static");
            isTheFirstModifier = false;
        }

        if ((this.access_flags.value & FieldInfo.ACC_FINAL) > 0) {
            if (isTheFirstModifier == false) {
                sb.append(' ');
            }
            sb.append("final");
            isTheFirstModifier = false;
        } else if ((this.access_flags.value & FieldInfo.ACC_VOLATILE) > 0) {
            if (isTheFirstModifier == false) {
                sb.append(' ');
            }
            sb.append("volatile");
            isTheFirstModifier = false;
        }

        if ((this.access_flags.value & FieldInfo.ACC_TRANSIENT) > 0) {
            if (isTheFirstModifier == false) {
                sb.append(' ');
            }
            sb.append("transient");
            isTheFirstModifier = false;
        }

        return sb.toString();
    }

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
