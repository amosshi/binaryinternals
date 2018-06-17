/*
 * TagType.java    Nov 21, 2010, 00:26
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

/**
 *
 * @author Amos Shi
 */
public class TagType {

    public static final int chromaticityType = 0x6368726D;                      // ’chrm’, ICC 4.2.0.0 - 10.2
    public static final int colorantOrderType = 0x636C726F;                     // ‘clro’, ICC 4.2.0.0 - 10.3
    public static final int colorantTableType = 0x636c7274;                     // ‘clrt’, ICC 4.2.0.0 - 10.4
    public static final int curveType = 0x63757276;                             // ‘curv’, ICC 4.2.0.0 - 10.5
    public static final int dataType = 0x64617461;                              // ‘data’, ICC 4.2.0.0 - 10.6
    public static final int dateTimeType = 0x6474696D;                          // ‘dtim’, ICC 4.2.0.0 - 10.7
    public static final int lut16Type = 0x6D667432;                             // ‘mft2’, ICC 4.2.0.0 - 10.8
    public static final int lut8Type = 0x6D667431;                              // ‘mft1’, ICC 4.2.0.0 - 10.9
    public static final int lutAtoBType = 0x6D414220;                           // ‘mAB ’, ICC 4.2.0.0 - 10.10
    public static final int lutBtoAType = 0x6D424120;                           // ‘mBA ’, ICC 4.2.0.0 - 10.11
    public static final int measurementType = 0x6D656173;                       // ‘meas’, ICC 4.2.0.0 - 10.12
    public static final int multiLocalizedUnicodeType = 0x6D6C7563;             // ‘mluc’, ICC 4.2.0.0 - 10.13
    public static final int namedColor2Type = 0x6E636C32;                       // ‘ncl2’, ICC 4.2.0.0 - 10.14
    public static final int parametricCurveType = 0x70617261;                   // ‘para’, ICC 4.2.0.0 - 10.15
    public static final int profileSequenceDescType = 0x70736571;               // ‘pseq’, ICC 4.2.0.0 - 10.16
    public static final int responseCurveSet16Type = 0x72637332;                // ‘rcs2’, ICC 4.2.0.0 - 10.17
    public static final int s15Fixed16ArrayType = 0x73663332;                   // ‘sf32’, ICC 4.2.0.0 - 10.18
    public static final int signatureType = 0x73696720;                         // ‘sig ’, ICC 4.2.0.0 - 10.19
    public static final int textType = 0x74657874;                              // ‘text’, ICC 4.2.0.0 - 10.20
    public static final int u16Fixed16ArrayType = 0x75663332;                   // ‘uf32’, ICC 4.2.0.0 - 10.21
    public static final int uInt16ArrayType = 0x75693136;                       // ‘ui16’, ICC 4.2.0.0 - 10.22
    public static final int uInt32ArrayType = 0x75693332;                       // ‘ui32’, ICC 4.2.0.0 - 10.23
    public static final int uInt64ArrayType = 0x75693634;                       // ‘ui64’, ICC 4.2.0.0 - 10.24
    public static final int uInt8ArrayType = 0x75693038;                        // ‘ui08’, ICC 4.2.0.0 - 10.25
    public static final int viewingConditionsType = 0x76696577;                 // ‘view’, ICC 4.2.0.0 - 10.26
    public static final int XYZType = 0x58595A20;                               // ‘XYZ ’, ICC 4.2.0.0 - 10.27

    /**
     *
     * @param sig
     * @return
     */
    public static String getTypeSignature(int sig) {
        StringBuilder sb = new StringBuilder(5);
        sb.append((char) ((sig & 0xFF000000) >> 24));
        sb.append((char) (((sig & 0x00FF0000) << 8) >> 24));
        sb.append((char) (((sig & 0x0000FF00) << 16) >> 24));
        sb.append((char) (((sig & 0x000000FF) << 24) >> 24));
        return sb.toString();
    }
}
