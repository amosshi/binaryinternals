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
    Version_11("11", 11);

    public final String name;
    public final int majorVersion;

    private JavaSEVersion(String name, int majorVersion) {
        this.name = name;
        this.majorVersion = majorVersion;
    }
}
