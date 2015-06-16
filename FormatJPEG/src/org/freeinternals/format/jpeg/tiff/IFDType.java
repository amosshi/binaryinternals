/*
 * IFDType.java    Sep 07, 2010, 22:43
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

/**
 *
 * @author Amos Shi
 */
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

    
    public static String getTypeName(int type) {
        final String typeName;
        switch (type) {
            case IFDType.BYTE:
                typeName = "BYTE";
                break;
            case IFDType.ASCII:
                typeName = "ASCII";
                break;
            case IFDType.SHORT:
                typeName = "SHORT";
                break;
            case IFDType.LONG:
                typeName = "LONG";
                break;
            case IFDType.RATIONAL:
                typeName = "RATIONAL";
                break;
            case IFDType.SBYTE:
                typeName = "SBYTE";
                break;
            case IFDType.UNDEFINED:
                typeName = "UNDEFINED";
                break;
            case IFDType.SSHORT:
                typeName = "SSHORT";
                break;
            case IFDType.SLONG:
                typeName = "SLONG";
                break;
            case IFDType.SRATIONAL:
                typeName = "SRATIONAL";
                break;
            case IFDType.FLOAT:
                typeName = "FLOAT";
                break;
            case IFDType.DOUBLE:
                typeName = "DOUBLE";
                break;
            default:
                typeName = "Unknown";
        }
        return typeName;
    }

    public static int getTypeLength(int type){
        final int length;
        switch (type) {
            case IFDType.BYTE:
                length = IFDType.LENGTH_BYTE;
                break;
            case IFDType.ASCII:
                length = IFDType.LENGTH_ASCII;
                break;
            case IFDType.SHORT:
                length = IFDType.LENGTH_SHORT;
                break;
            case IFDType.LONG:
                length = IFDType.LENGTH_LONG;
                break;
            case IFDType.RATIONAL:
                length = IFDType.LENGTH_RATIONAL;
                break;
            case IFDType.SBYTE:
                length = IFDType.LENGTH_SBYTE;
                break;
            case IFDType.UNDEFINED:
                length = IFDType.LENGTH_UNDEFINED;
                break;
            case IFDType.SSHORT:
                length = IFDType.LENGTH_SSHORT;
                break;
            case IFDType.SLONG:
                length = IFDType.LENGTH_SLONG;
                break;
            case IFDType.SRATIONAL:
                length = IFDType.LENGTH_SRATIONAL;
                break;
            case IFDType.FLOAT:
                length = IFDType.LENGTH_FLOAT;
                break;
            case IFDType.DOUBLE:
                length = IFDType.LENGTH_DOUBLE;
                break;
            default:
                length = -1;
        }
        return length;
    }

}
