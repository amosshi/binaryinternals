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

    VERSION_1_0_2("1.0.2", 1, false),
    VERSION_1_1("1.1", 1, false),
    VERSION_5_0("5.0", 5, false),
    VERSION_6("6", 6, false),
    VERSION_7("7", 7, false),
    VERSION_8("8", 8, false),
    VERSION_9("9", 9, false),
    VERSION_11("11", 11, true),
    VERSION_12("12", 12, false),
    VERSION_13("13", 13, false),
    VERSION_14("14", 14, false),
    VERSION_15("15", 15, false),
    VERSION_16("16", 16, false),
    VERSION_17("17", 17, true);

    /**
     * Version name.
     */
    public final String versionName;

    /**
     * Major version number.
     */
    public final int majorVersion;

    /**
     * Is Long-Term-Support (LTS) release or not.
     */
    public final boolean isLTS;

    private JavaSEVersion(String name, int majorVersion, boolean lts) {
        this.versionName = name;
        this.majorVersion = majorVersion;
        this.isLTS = lts;
    }
}
