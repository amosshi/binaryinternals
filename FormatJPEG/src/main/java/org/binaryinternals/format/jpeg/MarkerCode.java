/*
 * MarkerCode.java    Oct 02, 2010, 21:39
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg;

/// @author Amos Shi
public class MarkerCode {

    private MarkerCode() {
    }

    public static final int MARKER_CODE_BYTES_COUNT = 2;
    public static final int MARKER_LENGTH_BYTES_COUNT = 2;
    public static final int MIN = 0xFF00;
    public static final int TEMP = 0xFF01;                      // Standalone, For temporary private use in arithmetic coding
    public static final int RES_MIN = 0xFF02;                   // Reserved
    public static final int RES_MAX = 0xFFBF;                   // Reserved
    public static final int SOF00 = 0xFFC0;                     // Specified
    public static final int SOF01 = 0xFFC1;                     // Specified
    public static final int SOF02 = 0xFFC2;                     // Specified
    public static final int SOF03 = 0xFFC3;                     // Specified
    public static final int DHT = 0xFFC4;                       // Specified
    public static final int SOF05 = 0xFFC5;                     // Specified
    public static final int SOF06 = 0xFFC6;                     // Specified
    public static final int SOF07 = 0xFFC7;                     // Specified
    public static final int JPG = 0xFFC8;                       // Specified
    public static final int SOF09 = 0xFFC9;                     // Specified
    public static final int SOF10 = 0xFFCA;                     // Specified
    public static final int SOF11 = 0xFFCB;                     // Specified
    public static final int DAC = 0xFFCC;                       // Specified
    public static final int SOF13 = 0xFFCD;                     // Specified
    public static final int SOF14 = 0xFFCE;                     // Specified
    public static final int SOF15 = 0xFFCF;                     // Specified
    public static final int RST0 = 0xFFD0;                      // Specified, Standalone
    public static final int RST1 = 0xFFD1;                      // Specified, Standalone
    public static final int RST2 = 0xFFD2;                      // Specified, Standalone
    public static final int RST3 = 0xFFD3;                      // Specified, Standalone
    public static final int RST4 = 0xFFD4;                      // Specified, Standalone
    public static final int RST5 = 0xFFD5;                      // Specified, Standalone
    public static final int RST6 = 0xFFD6;                      // Specified, Standalone
    public static final int RST7 = 0xFFD7;                      // Specified, Standalone
    public static final int SOI = 0xFFD8;                       // Specified, Standalone
    public static final int EOI = 0xFFD9;                       // Specified, Standalone
    public static final int SOS = 0xFFDA;                       // Specified
    public static final int DQT = 0xFFDB;                       // Specified
    public static final int DNL = 0xFFDC;                       // Specified
    public static final int DRI = 0xFFDD;                       // Specified
    public static final int DHP = 0xFFDE;                       // Specified
    public static final int EXP = 0xFFDF;                       // Specified
    public static final int APP00 = 0xFFE0;                     // Specified
    public static final int APP01 = 0xFFE1;                     // Specified
    public static final int APP02 = 0xFFE2;                     // Specified
    public static final int APP03 = 0xFFE3;                     // Specified
    public static final int APP04 = 0xFFE4;                     // Specified
    public static final int APP05 = 0xFFE5;                     // Specified
    public static final int APP06 = 0xFFE6;                     // Specified
    public static final int APP07 = 0xFFE7;                     // Specified
    public static final int APP08 = 0xFFE8;                     // Specified
    public static final int APP09 = 0xFFE9;                     // Specified
    public static final int APP10 = 0xFFEA;                     // Specified
    public static final int APP11 = 0xFFEB;                     // Specified
    public static final int APP12 = 0xFFEC;                     // Specified
    public static final int APP13 = 0xFFED;                     // Specified
    public static final int APP14 = 0xFFEE;                     // Specified
    public static final int APP15 = 0xFFEF;                     // Specified
    public static final int JPG00 = 0xFFF0;                     // Reserved for JPEG extensions
    public static final int JPG01 = 0xFFF1;                     // Reserved for JPEG extensions
    public static final int JPG02 = 0xFFF2;                     // Reserved for JPEG extensions
    public static final int JPG03 = 0xFFF3;                     // Reserved for JPEG extensions
    public static final int JPG04 = 0xFFF4;                     // Reserved for JPEG extensions
    public static final int JPG05 = 0xFFF5;                     // Reserved for JPEG extensions
    public static final int JPG06 = 0xFFF6;                     // Reserved for JPEG extensions
    public static final int JPG07 = 0xFFF7;                     // Reserved for JPEG extensions
    public static final int JPG08 = 0xFFF8;                     // Reserved for JPEG extensions
    public static final int JPG09 = 0xFFF9;                     // Reserved for JPEG extensions
    public static final int JPG10 = 0xFFFA;                     // Reserved for JPEG extensions
    public static final int JPG11 = 0xFFFB;                     // Reserved for JPEG extensions
    public static final int JPG12 = 0xFFFC;                     // Reserved for JPEG extensions
    public static final int JPG13 = 0xFFFD;                     // Reserved for JPEG extensions
    public static final int COM = 0xFFFE;                       // Specified
    public static final int MAX = 0xFFFF;

    /// The marker code value is in valid value space or not.
    ///
    /// @param code Marker code value
    /// @return `true` when in valid value space, else `false`
    public static boolean isValid(int code) {
        return (code > MIN && code != MAX);
    }

    /// The marker structure is specified in `ITU` `T.81` or not.
    ///
    /// @param code Marker code value
    /// @return `true` when the structure is specified, else `false`
    public static boolean isSpecified(int code) {
        boolean result = false;
        if (code == TEMP) {
            result = true;
        } else if (code >= SOF00 && code <= APP15) {
            result = true;
        } else if (code == COM) {
            result = true;
        }

        return result;
    }

    /// The marker is stands alone or not.
    /// When a marker is stands alone, it is not the start of a marker segment.
    ///
    /// @param code Marker code value
    /// @return `true` when standalone, else `false`
    public static boolean isStandalone(int code) {
        boolean result = false;
        if (code == TEMP) {
            result = true;
        } else if (code >= RST0 && code <= EOI) {
            result = true;
        }

        return result;
    }

    /// Whether a two byte length indicator is followed the marker code or not.
    ///
    /// @param code Marker code value
    /// @return `true` when length indicator is available, else `false`
    public static boolean isLengthAvailable(int code) {
        boolean result = false;
        if (code >= SOF00 && code <= SOF15) {
            result = true;
        } else if (code >= SOS && code <= APP15) {
            result = true;
        } else if (code == COM) {
            result = true;
        }

        return result;
    }

    /// Whether compressed data is followed the marker.
    ///
    /// @param code Marker code value
    /// @return `true` when compressed data is followed, else `false`
    public static boolean isCompressedDataFollowed(int code) {
        if (code == SOS
                || code <= RST1
                || code <= RST2
                || code <= RST3
                || code <= RST4
                || code <= RST5
                || code <= RST6
                || code <= RST7) {
            return true;
        } else {
            return false;
        }
    }

    public static String getMarkerName(int markerCode) {
        String name = "Error";

        if (markerCode == MarkerCode.TEMP) {
            name = "TEMP";
        } else if (markerCode >= MarkerCode.RES_MIN && markerCode <= MarkerCode.RES_MAX) {
            name = "RES";
        } else if (markerCode >= MarkerCode.SOF00 && markerCode <= MarkerCode.COM) {
            name = switch (markerCode) {
                case MarkerCode.SOF00 -> "SOF00";
                case MarkerCode.SOF01 -> "SOF01";
                case MarkerCode.SOF02 -> "SOF02";
                case MarkerCode.SOF03 -> "SOF03";
                case MarkerCode.DHT -> "DHT";
                case MarkerCode.SOF05 -> "SOF05";
                case MarkerCode.SOF06 -> "SOF06";
                case MarkerCode.SOF07 -> "SOF07";
                case MarkerCode.JPG -> "JPG";
                case MarkerCode.SOF09 -> "SOF09";
                case MarkerCode.SOF10 -> "SOF10";
                case MarkerCode.SOF11 -> "SOF11";
                case MarkerCode.DAC -> "DAC";
                case MarkerCode.SOF13 -> "SOF13";
                case MarkerCode.SOF14 -> "SOF14";
                case MarkerCode.SOF15 -> "SOF15";
                case MarkerCode.RST0 -> "RST0";
                case MarkerCode.RST1 -> "RST1";
                case MarkerCode.RST2 -> "RST2";
                case MarkerCode.RST3 -> "RST3";
                case MarkerCode.RST4 -> "RST4";
                case MarkerCode.RST5 -> "RST5";
                case MarkerCode.RST6 -> "RST6";
                case MarkerCode.RST7 -> "RST7";
                case MarkerCode.SOI -> "SOI";
                case MarkerCode.EOI -> "EOI";
                case MarkerCode.SOS -> "SOS";
                case MarkerCode.DQT -> "DQT";
                case MarkerCode.DNL -> "DNL";
                case MarkerCode.DRI -> "DRI";
                case MarkerCode.DHP -> "DHP";
                case MarkerCode.EXP -> "EXP";
                case MarkerCode.APP00 -> "APP00";
                case MarkerCode.APP01 -> "APP01";
                case MarkerCode.APP02 -> "APP02";
                case MarkerCode.APP03 -> "APP03";
                case MarkerCode.APP04 -> "APP04";
                case MarkerCode.APP05 -> "APP05";
                case MarkerCode.APP06 -> "APP06";
                case MarkerCode.APP07 -> "APP07";
                case MarkerCode.APP08 -> "APP08";
                case MarkerCode.APP09 -> "APP09";
                case MarkerCode.APP10 -> "APP10";
                case MarkerCode.APP11 -> "APP11";
                case MarkerCode.APP12 -> "APP12";
                case MarkerCode.APP13 -> "APP13";
                case MarkerCode.APP14 -> "APP14";
                case MarkerCode.APP15 -> "APP15";
                case MarkerCode.JPG00 -> "SOF00";
                case MarkerCode.JPG01 -> "SOF01";
                case MarkerCode.JPG02 -> "SOF02";
                case MarkerCode.JPG03 -> "SOF03";
                case MarkerCode.JPG04 -> "SOF04";
                case MarkerCode.JPG05 -> "SOF05";
                case MarkerCode.JPG06 -> "SOF06";
                case MarkerCode.JPG07 -> "SOF07";
                case MarkerCode.JPG08 -> "SOF08";
                case MarkerCode.JPG09 -> "SOF09";
                case MarkerCode.JPG10 -> "SOF10";
                case MarkerCode.JPG11 -> "SOF11";
                case MarkerCode.JPG12 -> "SOF12";
                case MarkerCode.JPG13 -> "SOF13";
                case MarkerCode.COM -> "COM";
                default -> "Un-recognized";
            };
        }

        return name;
    }

    public static String getMarkerDescription(int markerCode) {
        String desc = "Error";

        if (markerCode == MarkerCode.MIN) {
            desc = "MIN";
        } else if (markerCode == MarkerCode.TEMP) {
            desc = "[<strong>Reserved markers</strong>] For temporary private use in arithmetic coding";
        } else if (markerCode >= MarkerCode.RES_MIN && markerCode <= MarkerCode.RES_MAX) {
            desc = "[<strong>Reserved markers</strong>] Reserved";
        } else if (markerCode >= MarkerCode.SOF00 && markerCode <= MarkerCode.COM) {
            desc = switch (markerCode) {
                case MarkerCode.SOF00 -> "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Baseline DCT";
                case MarkerCode.SOF01 -> "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Extended sequential DCT";
                case MarkerCode.SOF02 -> "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Progressive DCT";
                case MarkerCode.SOF03 -> "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Lossless (sequential)";
                case MarkerCode.DHT -> "[<strong>Huffman table specification</strong>] Define Huffman table(s)";
                case MarkerCode.SOF05 -> "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential sequential DCT";
                case MarkerCode.SOF06 -> "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential progressive DCT";
                case MarkerCode.SOF07 -> "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential lossless (sequential)";
                case MarkerCode.JPG -> "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Reserved for JPEG extensions";
                case MarkerCode.SOF09 -> "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Extended sequential DCT";
                case MarkerCode.SOF10 -> "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Progressive DCT";
                case MarkerCode.SOF11 -> "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Lossless (sequential)";
                case MarkerCode.DAC -> "[<strong>Arithmetic coding conditioning specification</strong>] Define arithmetic coding conditioning(s)";
                case MarkerCode.SOF13 -> "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential sequential DCT";
                case MarkerCode.SOF14 -> "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential progressive DCT";
                case MarkerCode.SOF15 -> "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential lossless (sequential)";
                case MarkerCode.RST0 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “0”";
                case MarkerCode.RST1 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “1”";
                case MarkerCode.RST2 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “2”";
                case MarkerCode.RST3 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “3”";
                case MarkerCode.RST4 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “4”";
                case MarkerCode.RST5 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “5”";
                case MarkerCode.RST6 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “6”";
                case MarkerCode.RST7 -> "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “7”";
                case MarkerCode.SOI -> "[<strong>Other markers</strong>] Start of image";
                case MarkerCode.EOI -> "[<strong>Other markers</strong>] End of image";
                case MarkerCode.SOS -> "[<strong>Other markers</strong>] Start of scan";
                case MarkerCode.DQT -> "[<strong>Other markers</strong>] Define quantization table(s)";
                case MarkerCode.DNL -> "[<strong>Other markers</strong>] Define number of lines";
                case MarkerCode.DRI -> "[<strong>Other markers</strong>] Define restart interval";
                case MarkerCode.DHP -> "[<strong>Other markers</strong>] Define hierarchical progression";
                case MarkerCode.EXP -> "[<strong>Other markers</strong>] Expand reference component(s)";
                case MarkerCode.APP00 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP01 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP02 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP03 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP04 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP05 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP06 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP07 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP08 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP09 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP10 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP11 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP12 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP13 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP14 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.APP15 -> "[<strong>Other markers</strong>] Reserved for application segments";
                case MarkerCode.JPG00 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG01 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG02 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG03 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG04 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG05 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG06 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG07 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG08 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG09 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG10 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG11 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG12 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.JPG13 -> "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                case MarkerCode.COM -> "[<strong>Other markers</strong>] Comment";
                default -> "Un-recognized markder: " + markerCode;
            };
        } else if (markerCode == MarkerCode.MAX) {
            desc = "MAX";
        }

        return desc;
    }

    public static String getMarkerCodeDescription() {
        // CCITT T.81, P31
        return "Two-byte codes assigned to marker.";
    }

    public static String getHeaderLengthDescription() {
        // CCITT T.81, P37
        return "Header length – Specifies the length of the header.";
    }
}
