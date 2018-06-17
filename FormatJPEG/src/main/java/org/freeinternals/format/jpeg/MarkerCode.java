/*
 * MarkerCode.java    Oct 02, 2010, 21:39
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

/**
 *
 * @author Amos Shi
 */
public class MarkerCode {

    public static final int MARKER_CODE_BYTES_COUNT = 2;
    public static final int MARKER_LENGTH_BYTES_COUNT = 2;
    public static final int min = 0xFF00;
    public static final int TEMP = 0xFF01;                      // Standalone, For temporary private use in arithmetic coding
    public static final int RES_min = 0xFF02;                   // Reserved
    public static final int RES_max = 0xFFBF;                   // Reserved
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
    public static final int max = 0xFFFF;

    /**
     * The marker code value is in valid value space or not.
     *
     * @param code Marker code value
     * @return <code>true</code> when in valid value space, else <code>false</code>
     */
    public static boolean isValid(int code) {
        return (code > min && code != max);
    }

    /**
     * The marker structure is specified in <code>ITU</code> <code>T.81</code> or not.
     *
     * @param code Marker code value
     * @return <code>true</code> when the structure is specified, else <code>false</code>
     */
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

    /**
     * The marker is stands alone or not. <br/>
     * When a marker is stands alone, it is not the start of a marker segment.
     *
     * @param code Marker code value
     * @return <code>true</code> when standalone, else <code>false</code>
     */
    public static boolean isStandalone(int code) {
        boolean result = false;
        if (code == TEMP) {
            result = true;
        } else if (code >= RST0 && code <= EOI) {
            result = true;
        }

        return result;
    }

    /**
     * Whether a two byte length indicator is followed the marker code or not.
     *
     * @param code Marker code value
     * @return <code>true</code> when length indicator is available, else <code>false</code>
     */
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

    /**
     * Whether compressed data is followed the marker.
     *
     * @param code Marker code value
     * @return <code>true</code> when compressed data is followed, else <code>false</code>
     */
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
        } else if (markerCode >= MarkerCode.RES_min && markerCode <= MarkerCode.RES_max) {
            name = "RES";
        } else if (markerCode >= MarkerCode.SOF00 && markerCode <= MarkerCode.COM) {
            switch (markerCode) {
                case MarkerCode.SOF00:
                    name = "SOF00";
                    break;
                case MarkerCode.SOF01:
                    name = "SOF01";
                    break;
                case MarkerCode.SOF02:
                    name = "SOF02";
                    break;
                case MarkerCode.SOF03:
                    name = "SOF03";
                    break;
                case MarkerCode.DHT:
                    name = "DHT";
                    break;
                case MarkerCode.SOF05:
                    name = "SOF05";
                    break;
                case MarkerCode.SOF06:
                    name = "SOF06";
                    break;
                case MarkerCode.SOF07:
                    name = "SOF07";
                    break;
                case MarkerCode.JPG:
                    name = "JPG";
                    break;
                case MarkerCode.SOF09:
                    name = "SOF09";
                    break;
                case MarkerCode.SOF10:
                    name = "SOF10";
                    break;
                case MarkerCode.SOF11:
                    name = "SOF11";
                    break;
                case MarkerCode.DAC:
                    name = "DAC";
                    break;
                case MarkerCode.SOF13:
                    name = "SOF13";
                    break;
                case MarkerCode.SOF14:
                    name = "SOF14";
                    break;
                case MarkerCode.SOF15:
                    name = "SOF15";
                    break;
                case MarkerCode.RST0:
                    name = "RST0";
                    break;
                case MarkerCode.RST1:
                    name = "RST1";
                    break;
                case MarkerCode.RST2:
                    name = "RST2";
                    break;
                case MarkerCode.RST3:
                    name = "RST3";
                    break;
                case MarkerCode.RST4:
                    name = "RST4";
                    break;
                case MarkerCode.RST5:
                    name = "RST5";
                    break;
                case MarkerCode.RST6:
                    name = "RST6";
                    break;
                case MarkerCode.RST7:
                    name = "RST7";
                    break;
                case MarkerCode.SOI:
                    name = "SOI";
                    break;
                case MarkerCode.EOI:
                    name = "EOI";
                    break;
                case MarkerCode.SOS:
                    name = "SOS";
                    break;
                case MarkerCode.DQT:
                    name = "DQT";
                    break;
                case MarkerCode.DNL:
                    name = "DNL";
                    break;
                case MarkerCode.DRI:
                    name = "DRI";
                    break;
                case MarkerCode.DHP:
                    name = "DHP";
                    break;
                case MarkerCode.EXP:
                    name = "EXP";
                    break;
                case MarkerCode.APP00:
                    name = "APP00";
                    break;
                case MarkerCode.APP01:
                    name = "APP01";
                    break;
                case MarkerCode.APP02:
                    name = "APP02";
                    break;
                case MarkerCode.APP03:
                    name = "APP03";
                    break;
                case MarkerCode.APP04:
                    name = "APP04";
                    break;
                case MarkerCode.APP05:
                    name = "APP05";
                    break;
                case MarkerCode.APP06:
                    name = "APP06";
                    break;
                case MarkerCode.APP07:
                    name = "APP07";
                    break;
                case MarkerCode.APP08:
                    name = "APP08";
                    break;
                case MarkerCode.APP09:
                    name = "APP09";
                    break;
                case MarkerCode.APP10:
                    name = "APP10";
                    break;
                case MarkerCode.APP11:
                    name = "APP11";
                    break;
                case MarkerCode.APP12:
                    name = "APP12";
                    break;
                case MarkerCode.APP13:
                    name = "APP13";
                    break;
                case MarkerCode.APP14:
                    name = "APP14";
                    break;
                case MarkerCode.APP15:
                    name = "APP15";
                    break;
                case MarkerCode.JPG00:
                    name = "SOF00";
                    break;
                case MarkerCode.JPG01:
                    name = "SOF01";
                    break;
                case MarkerCode.JPG02:
                    name = "SOF02";
                    break;
                case MarkerCode.JPG03:
                    name = "SOF03";
                    break;
                case MarkerCode.JPG04:
                    name = "SOF04";
                    break;
                case MarkerCode.JPG05:
                    name = "SOF05";
                    break;
                case MarkerCode.JPG06:
                    name = "SOF06";
                    break;
                case MarkerCode.JPG07:
                    name = "SOF07";
                    break;
                case MarkerCode.JPG08:
                    name = "SOF08";
                    break;
                case MarkerCode.JPG09:
                    name = "SOF09";
                    break;
                case MarkerCode.JPG10:
                    name = "SOF10";
                    break;
                case MarkerCode.JPG11:
                    name = "SOF11";
                    break;
                case MarkerCode.JPG12:
                    name = "SOF12";
                    break;
                case MarkerCode.JPG13:
                    name = "SOF13";
                    break;
                case MarkerCode.COM:
                    name = "COM";
                    break;
            }
        }

        return name;
    }

    public static String getMarkerDescription(int markerCode) {
        String desc = "Error";

        if (markerCode == MarkerCode.min) {
            desc = "MIN";
        } else if (markerCode == MarkerCode.TEMP) {
            desc = "[<strong>Reserved markers</strong>] For temporary private use in arithmetic coding";
        } else if (markerCode >= MarkerCode.RES_min && markerCode <= MarkerCode.RES_max) {
            desc = "[<strong>Reserved markers</strong>] Reserved";
        } else if (markerCode >= MarkerCode.SOF00 && markerCode <= MarkerCode.COM) {
            switch (markerCode) {
                case MarkerCode.SOF00:
                    desc = "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Baseline DCT";
                    break;
                case MarkerCode.SOF01:
                    desc = "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Extended sequential DCT";
                    break;
                case MarkerCode.SOF02:
                    desc = "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Progressive DCT";
                    break;
                case MarkerCode.SOF03:
                    desc = "[<strong>Start Of Frame markers, non-differential, Huffman coding</strong>] Lossless (sequential)";
                    break;
                case MarkerCode.DHT:
                    desc = "[<strong>Huffman table specification</strong>] Define Huffman table(s)";
                    break;
                case MarkerCode.SOF05:
                    desc = "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential sequential DCT";
                    break;
                case MarkerCode.SOF06:
                    desc = "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential progressive DCT";
                    break;
                case MarkerCode.SOF07:
                    desc = "[<strong>Start Of Frame markers, differential, Huffman coding</strong>] Differential lossless (sequential)";
                    break;
                case MarkerCode.JPG:
                    desc = "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.SOF09:
                    desc = "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Extended sequential DCT";
                    break;
                case MarkerCode.SOF10:
                    desc = "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Progressive DCT";
                    break;
                case MarkerCode.SOF11:
                    desc = "[<strong>Start Of Frame markers, non-differential, arithmetic coding</strong>] Lossless (sequential)";
                    break;
                case MarkerCode.DAC:
                    desc = "[<strong>Arithmetic coding conditioning specification</strong>] Define arithmetic coding conditioning(s)";
                    break;
                case MarkerCode.SOF13:
                    desc = "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential sequential DCT";
                    break;
                case MarkerCode.SOF14:
                    desc = "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential progressive DCT";
                    break;
                case MarkerCode.SOF15:
                    desc = "[<strong>Start Of Frame markers, differential, arithmetic coding</strong>] Differential lossless (sequential)";
                    break;
                case MarkerCode.RST0:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “0”";
                    break;
                case MarkerCode.RST1:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “1”";
                    break;
                case MarkerCode.RST2:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “2”";
                    break;
                case MarkerCode.RST3:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “3”";
                    break;
                case MarkerCode.RST4:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “4”";
                    break;
                case MarkerCode.RST5:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “5”";
                    break;
                case MarkerCode.RST6:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “6”";
                    break;
                case MarkerCode.RST7:
                    desc = "[<strong>Restart interval termination</strong>] Restart with modulo 8 count “7”";
                    break;
                case MarkerCode.SOI:
                    desc = "[<strong>Other markers</strong>] Start of image";
                    break;
                case MarkerCode.EOI:
                    desc = "[<strong>Other markers</strong>] End of image";
                    break;
                case MarkerCode.SOS:
                    desc = "[<strong>Other markers</strong>] Start of scan";
                    break;
                case MarkerCode.DQT:
                    desc = "[<strong>Other markers</strong>] Define quantization table(s)";
                    break;
                case MarkerCode.DNL:
                    desc = "[<strong>Other markers</strong>] Define number of lines";
                    break;
                case MarkerCode.DRI:
                    desc = "[<strong>Other markers</strong>] Define restart interval";
                    break;
                case MarkerCode.DHP:
                    desc = "[<strong>Other markers</strong>] Define hierarchical progression";
                    break;
                case MarkerCode.EXP:
                    desc = "[<strong>Other markers</strong>] Expand reference component(s)";
                    break;
                case MarkerCode.APP00:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP01:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP02:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP03:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP04:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP05:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP06:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP07:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP08:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP09:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP10:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP11:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP12:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP13:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP14:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.APP15:
                    desc = "[<strong>Other markers</strong>] Reserved for application segments";
                    break;
                case MarkerCode.JPG00:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG01:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG02:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG03:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG04:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG05:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG06:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG07:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG08:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG09:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG10:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG11:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG12:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.JPG13:
                    desc = "[<strong>Other markers</strong>] Reserved for JPEG extensions";
                    break;
                case MarkerCode.COM:
                    desc = "[<strong>Other markers</strong>] Comment";
                    break;
            }
        } else if (markerCode == MarkerCode.max) {
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
