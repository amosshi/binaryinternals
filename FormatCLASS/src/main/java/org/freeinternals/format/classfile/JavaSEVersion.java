/*
 * JavaSEVersion.java    June 4, 2019
 *
 * Copyright  2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

/**
 * Java SE platform version.
 *
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">The
 * ClassFile Structure</a>
 * @author Amos Shi
 */
public enum JavaSEVersion {

    Version_1_0_2("1.0.2", 1),
    Version_1_1("1.1", 1),
    Version_5_0("5.0", 5),
    Version_6("6", 6),
    Version_7("7", 7),
    Version_8("8", 8),
    Version_9("9", 9),
    Version_11("11", 11),
    Version_12("12", 12),
    Version_13("13", 13),
    Version_14("14", 14),
    Version_15("15", 15);

    public final String name;
    public final int majorVersion;

    private JavaSEVersion(String name, int majorVersion) {
        this.name = name;
        this.majorVersion = majorVersion;
    }
}
