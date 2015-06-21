/*
 * AccessFlags.java    9:27 PM, August 7, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Access Flag of a {@code class} or {@code interface}.
 * It is the {@code access_flags} in {@code ClassFile} structure.
 * <p>
 * The access flag is a mask combination of the following flags:
 *
 * <pre>
 *    Flag Name      Value     Interpretation
 *    ACC_PUBLIC     0x0001    Declared public; may be accessed from outside its package.
 *    ACC_FINAL      0x0010    Declared final; no subclasses allowed.
 *    ACC_SUPER      0x0020    Treat superclass methods specially when invoked by the invokespecial instruction.
 *    ACC_INTERFACE  0x0200    Is an interface, not a class.
 *    ACC_ABSTRACT   0x0400    Declared abstract; may not be instantiated.
 *    ACC_SYNTHETIC  0x1000    Declared synthetic; not present in the source code.
 *    ACC_ANNOTATION 0x2000    Declared as an annotation type.
 *    ACC_ENUM       0x4000    Declared as an enum type
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile#access_flags
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#74353">
 * VM Spec: The ClassFile Structure
 * </a>
 */
public class AccessFlags extends U2ClassComponent {

    AccessFlags(final PosDataInputStream posDis)
            throws IOException {
        super(posDis);
    }

    /**
     * Generate the modifier of a {@code class} or {@code interface} from the access flag value.
     *
     * @return A string for modifier
     */
    public String getModifiers() {
        return AccessFlag.getClassModifier(super.value.value);
    }

    /**
     * Display a string for the raw format of the internal values of this access flag object.
     *
     * @return A string representing this object
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(25);

        sb.append(String.format("%04X  (", super.value.value));

        if ((this.value.value & AccessFlag.ACC_PUBLIC.value) > 0) {
            sb.append("public ");
        }
        if ((this.value.value & AccessFlag.ACC_FINAL.value) > 0) {
            sb.append("final ");
        }
        if ((this.value.value & AccessFlag.ACC_SUPER.value) > 0) {
            sb.append("super ");
        }
        if ((this.value.value & AccessFlag.ACC_INTERFACE.value) > 0) {
            sb.append("interface ");
        }
        if ((this.value.value & AccessFlag.ACC_ABSTRACT.value) > 0) {
            sb.append("abstract ");
        }

        sb.append(")");

        return sb.toString();
    }
}
