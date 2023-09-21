/*
 * JavaSEVersion.java    June 4, 2019
 *
 * Copyright  2019, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.time.LocalDate;
import java.time.Month;

/**
 * Java SE platform version.
 *
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">The
 * ClassFile Structure</a>
 * @author Amos Shi
 */
public enum JavaSEVersion {

    VERSION_1_0_2("1.0.2", 1, false, LocalDate.of(1996, Month.MAY, 1)),
    VERSION_1_1("1.1", 1, false, LocalDate.of(1997, Month.FEBRUARY, 1)),
    VERSION_1_2("1.2", 1, false, LocalDate.of(1998, Month.DECEMBER, 1)),
    VERSION_1_3("1.3", 1, false, LocalDate.of(2000, Month.MAY, 1)),
    VERSION_1_4("1.4", 1, false, LocalDate.of(2002, Month.FEBRUARY, 1)),
    VERSION_5_0("5.0", 5, false, LocalDate.of(2004, Month.SEPTEMBER, 1)),
    VERSION_6("6", 6, false, LocalDate.of(2006, Month.DECEMBER, 1)),
    VERSION_7("7", 7, false, LocalDate.of(2011, Month.JULY, 1)),
    VERSION_8("8", 8, false, LocalDate.of(2014, Month.MARCH, 1)),
    VERSION_9("9", 9, false, LocalDate.of(2017, Month.SEPTEMBER, 1)),
    VERSION_10("10", 10, false, LocalDate.of(2018, Month.MARCH, 1)),
    VERSION_11("11", 11, true, LocalDate.of(2018, Month.SEPTEMBER, 1)),
    VERSION_12("12", 12, false, LocalDate.of(2019, Month.MARCH, 1)),
    VERSION_13("13", 13, false, LocalDate.of(2019, Month.SEPTEMBER, 1)),
    VERSION_14("14", 14, false, LocalDate.of(2020, Month.MARCH, 1)),
    VERSION_15("15", 15, false, LocalDate.of(2020, Month.SEPTEMBER, 1)),
    VERSION_16("16", 16, false, LocalDate.of(2021, Month.MARCH, 1)),
    VERSION_17("17", 17, true, LocalDate.of(2021, Month.SEPTEMBER, 1)),
    VERSION_18("18", 18, false, LocalDate.of(2022, Month.MARCH, 1));

    /**
     * Version name.
     */
    public final String versionName;

    /**
     * Major version number.
     */
    public final int versionNumber;

    /**
     * Released Year and Month.
     */
    public final LocalDate released;

    /**
     * Is Long-Term-Support (LTS) release or not.
     */
    public final boolean isLTS;

    private JavaSEVersion(String name, int versionNumber, boolean lts, LocalDate released) {
        this.versionName = name;
        this.versionNumber = versionNumber;
        this.isLTS = lts;
        this.released = released;
    }
}
