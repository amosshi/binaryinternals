/*
 * IFDType.java    Sep 07, 2010, 22:43
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

/// @author Amos Shi
public class IFDType {

    public static final int UnexpectedType = 0;
    public static final int BYTE = 1;
    public static final int ASCII = 2;
    public static final int SHORT = 3;
    public static final int LONG = 4;
    public static final int RATIONAL = 5;
    public static final int SBYTE = 6;
    public static final int UNDEFINED = 7;
    public static final int SSHORT = 8;
    public static final int SLONG = 9;
    public static final int SRATIONAL = 10;
    public static final int FLOAT = 11;
    public static final int DOUBLE = 12;

    public static final int LENGTH_BYTE = 1;
    public static final int LENGTH_ASCII = 1;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;
    public static final int LENGTH_RATIONAL = 8;
    public static final int LENGTH_SBYTE = 1;
    public static final int LENGTH_UNDEFINED = 1;
    public static final int LENGTH_SSHORT = 2;
    public static final int LENGTH_SLONG = 4;
    public static final int LENGTH_SRATIONAL = 8;
    public static final int LENGTH_FLOAT = 4;
    public static final int LENGTH_DOUBLE = 8;

    private IFDType() {
    }

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
