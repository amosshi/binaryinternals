/*
 * IFDType.java    Sep 07, 2010, 22:43
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

/// TIFF field types, see `TIFF 6.0` section 2, and their sizes in bytes.
///
/// @author Amos Shi
public class IFDType {

    /// Not a valid type.
    public static final int UnexpectedType = 0;
    /// Type 1, `BYTE`: 8-bit unsigned integer.
    public static final int BYTE = 1;
    /// Type 2, `ASCII`: 8-bit byte that contains a 7-bit ASCII code; the last byte must be NUL.
    public static final int ASCII = 2;
    /// Type 3, `SHORT`: 16-bit (2-byte) unsigned integer.
    public static final int SHORT = 3;
    /// Type 4, `LONG`: 32-bit (4-byte) unsigned integer.
    public static final int LONG = 4;
    /// Type 5, `RATIONAL`: Two LONGs: the numerator and the denominator of a fraction.
    public static final int RATIONAL = 5;
    /// Type 6, `SBYTE`: 8-bit signed (twos-complement) integer.
    public static final int SBYTE = 6;
    /// Type 7, `UNDEFINED`: 8-bit byte that may contain anything, depending on the definition of the field.
    public static final int UNDEFINED = 7;
    /// Type 8, `SSHORT`: 16-bit (2-byte) signed (twos-complement) integer.
    public static final int SSHORT = 8;
    /// Type 9, `SLONG`: 32-bit (4-byte) signed (twos-complement) integer.
    public static final int SLONG = 9;
    /// Type 10, `SRATIONAL`: Two SLONGs: the numerator and the denominator of a fraction.
    public static final int SRATIONAL = 10;
    /// Type 11, `FLOAT`: Single precision (4-byte) IEEE format.
    public static final int FLOAT = 11;
    /// Type 12, `DOUBLE`: Double precision (8-byte) IEEE format.
    public static final int DOUBLE = 12;

    /// Size of type [#BYTE] in bytes.
    public static final int LENGTH_BYTE = 1;
    /// Size of type [#ASCII] in bytes.
    public static final int LENGTH_ASCII = 1;
    /// Size of type [#SHORT] in bytes.
    public static final int LENGTH_SHORT = 2;
    /// Size of type [#LONG] in bytes.
    public static final int LENGTH_LONG = 4;
    /// Size of type [#RATIONAL] in bytes.
    public static final int LENGTH_RATIONAL = 8;
    /// Size of type [#SBYTE] in bytes.
    public static final int LENGTH_SBYTE = 1;
    /// Size of type [#UNDEFINED] in bytes.
    public static final int LENGTH_UNDEFINED = 1;
    /// Size of type [#SSHORT] in bytes.
    public static final int LENGTH_SSHORT = 2;
    /// Size of type [#SLONG] in bytes.
    public static final int LENGTH_SLONG = 4;
    /// Size of type [#SRATIONAL] in bytes.
    public static final int LENGTH_SRATIONAL = 8;
    /// Size of type [#FLOAT] in bytes.
    public static final int LENGTH_FLOAT = 4;
    /// Size of type [#DOUBLE] in bytes.
    public static final int LENGTH_DOUBLE = 8;

    private IFDType() {
    }

    /// Gets the name of a field type.
    ///
    /// @param type The field type
    /// @return The type name, or `Unknown`
    public static String getTypeName(int type) {
        return switch (type) {
            case IFDType.BYTE -> "BYTE";
            case IFDType.ASCII -> "ASCII";
            case IFDType.SHORT -> "SHORT";
            case IFDType.LONG -> "LONG";
            case IFDType.RATIONAL -> "RATIONAL";
            case IFDType.SBYTE -> "SBYTE";
            case IFDType.UNDEFINED -> "UNDEFINED";
            case IFDType.SSHORT -> "SSHORT";
            case IFDType.SLONG -> "SLONG";
            case IFDType.SRATIONAL -> "SRATIONAL";
            case IFDType.FLOAT -> "FLOAT";
            case IFDType.DOUBLE -> "DOUBLE";
            default -> "Unknown";
        };
    }

    /// Gets the size of a field type in bytes.
    ///
    /// @param type The field type
    /// @return The size in bytes, or `-1` for an unknown type
    public static int getTypeLength(int type) {
        return switch (type) {
            case IFDType.BYTE -> IFDType.LENGTH_BYTE;
            case IFDType.ASCII -> IFDType.LENGTH_ASCII;
            case IFDType.SHORT -> IFDType.LENGTH_SHORT;
            case IFDType.LONG -> IFDType.LENGTH_LONG;
            case IFDType.RATIONAL -> IFDType.LENGTH_RATIONAL;
            case IFDType.SBYTE -> IFDType.LENGTH_SBYTE;
            case IFDType.UNDEFINED -> IFDType.LENGTH_UNDEFINED;
            case IFDType.SSHORT -> IFDType.LENGTH_SSHORT;
            case IFDType.SLONG -> IFDType.LENGTH_SLONG;
            case IFDType.SRATIONAL -> IFDType.LENGTH_SRATIONAL;
            case IFDType.FLOAT -> IFDType.LENGTH_FLOAT;
            case IFDType.DOUBLE -> IFDType.LENGTH_DOUBLE;
            default -> -1;
        };
    }

}
