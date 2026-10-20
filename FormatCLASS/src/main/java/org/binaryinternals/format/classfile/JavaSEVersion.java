/*
 * JavaSEVersion.java    June 4, 2019
 *
 * Copyright  2019, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.lang.reflect.ClassFileFormatVersion;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Optional;

/// Java SE platform version.
///
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">The
/// ClassFile Structure</a>
/// @author Amos Shi
public enum JavaSEVersion {

    VERSION_1_0_2("1.0.2", 1, false, LocalDate.of(1996, Month.MAY, 1), ClassFileFormatVersion.RELEASE_0),
    VERSION_1_1("1.1", 1, false, LocalDate.of(1997, Month.FEBRUARY, 1), ClassFileFormatVersion.RELEASE_1),
    VERSION_1_2("1.2", 1, false, LocalDate.of(1998, Month.DECEMBER, 1), ClassFileFormatVersion.RELEASE_2),
    VERSION_1_3("1.3", 1, false, LocalDate.of(2000, Month.MAY, 1), ClassFileFormatVersion.RELEASE_3),
    VERSION_1_4("1.4", 1, false, LocalDate.of(2002, Month.FEBRUARY, 1), ClassFileFormatVersion.RELEASE_4),
    VERSION_5_0("5.0", 5, false, LocalDate.of(2004, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_5),
    VERSION_6("6", 6, false, LocalDate.of(2006, Month.DECEMBER, 1), ClassFileFormatVersion.RELEASE_6),
    VERSION_7("7", 7, false, LocalDate.of(2011, Month.JULY, 1), ClassFileFormatVersion.RELEASE_7),
    VERSION_8("8", 8, false, LocalDate.of(2014, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_8),
    VERSION_9("9", 9, false, LocalDate.of(2017, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_9),
    VERSION_10("10", 10, false, LocalDate.of(2018, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_10),
    VERSION_11("11", 11, true, LocalDate.of(2018, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_11),
    VERSION_12("12", 12, false, LocalDate.of(2019, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_12),
    VERSION_13("13", 13, false, LocalDate.of(2019, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_13),
    VERSION_14("14", 14, false, LocalDate.of(2020, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_14),
    VERSION_15("15", 15, false, LocalDate.of(2020, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_15),
    VERSION_16("16", 16, false, LocalDate.of(2021, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_16),
    VERSION_17("17", 17, true, LocalDate.of(2021, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_17),
    VERSION_18("18", 18, false, LocalDate.of(2022, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_18),
    VERSION_19("19", 19, false, LocalDate.of(2022, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_19),
    VERSION_20("20", 20, false, LocalDate.of(2023, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_20),
    VERSION_21("21", 21, true, LocalDate.of(2023, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_21),
    VERSION_22("22", 22, false, LocalDate.of(2024, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_22),
    VERSION_23("23", 23, false, LocalDate.of(2024, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_23),
    VERSION_24("24", 24, false, LocalDate.of(2025, Month.MARCH, 1), ClassFileFormatVersion.RELEASE_24),
    VERSION_25("25", 25, true, LocalDate.of(2025, Month.SEPTEMBER, 1), ClassFileFormatVersion.RELEASE_25);
    /// Version name.
    public final String versionName;

    /// Major version number.
    public final int versionNumber;

    /// Released Year and Month.
    public final LocalDate released;

    /// Is Long-Term-Support (LTS) release or not.
    public final boolean isLTS;

    /// The class file format version of this release.
    public final ClassFileFormatVersion classFileFormat;

    private JavaSEVersion(String name, int versionNumber, boolean lts, LocalDate released, ClassFileFormatVersion format) {
        this.versionName = name;
        this.versionNumber = versionNumber;
        this.isLTS = lts;
        this.released = released;
        this.classFileFormat = format;
    }

    /// Get the Java SE release of a class file format version.
    ///
    /// @param format Class file format version
    /// @return The Java SE release, or empty when `format` is newer than
    /// this enum
    public static Optional<JavaSEVersion> of(final ClassFileFormatVersion format) {
        return Arrays.stream(values()).filter(v -> v.classFileFormat == format).findFirst();
    }
}
