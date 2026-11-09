/*
 * TagType.java    Nov 21, 2010, 00:26
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/// ICC tag type signatures, see `ICC.1:2004-10` (profile version 4.2.0.0) section 10.
///
/// @author Amos Shi
public class TagType {

    /// Tag type `chromaticityType`, signature `chrm`, see section 10.2.
    public static final int chromaticityType = 0x6368726D;                      // ’chrm’, ICC 4.2.0.0 - 10.2
    /// Tag type `colorantOrderType`, signature `clro`, see section 10.3.
    public static final int colorantOrderType = 0x636C726F;                     // ‘clro’, ICC 4.2.0.0 - 10.3
    /// Tag type `colorantTableType`, signature `clrt`, see section 10.4.
    public static final int colorantTableType = 0x636C7274;                     // ‘clrt’, ICC 4.2.0.0 - 10.4
    /// Tag type `curveType`, signature `curv`, see section 10.5.
    public static final int curveType = 0x63757276;                             // ‘curv’, ICC 4.2.0.0 - 10.5
    /// Tag type `dataType`, signature `data`, see section 10.6.
    public static final int dataType = 0x64617461;                              // ‘data’, ICC 4.2.0.0 - 10.6
    /// Tag type `dateTimeType`, signature `dtim`, see section 10.7.
    public static final int dateTimeType = 0x6474696D;                          // ‘dtim’, ICC 4.2.0.0 - 10.7
    /// Tag type `lut16Type`, signature `mft2`, see section 10.8.
    public static final int lut16Type = 0x6D667432;                             // ‘mft2’, ICC 4.2.0.0 - 10.8
    /// Tag type `lut8Type`, signature `mft1`, see section 10.9.
    public static final int lut8Type = 0x6D667431;                              // ‘mft1’, ICC 4.2.0.0 - 10.9
    /// Tag type `lutAtoBType`, signature `mAB`, see section 10.10.
    public static final int lutAtoBType = 0x6D414220;                           // ‘mAB ’, ICC 4.2.0.0 - 10.10
    /// Tag type `lutBtoAType`, signature `mBA`, see section 10.11.
    public static final int lutBtoAType = 0x6D424120;                           // ‘mBA ’, ICC 4.2.0.0 - 10.11
    /// Tag type `measurementType`, signature `meas`, see section 10.12.
    public static final int measurementType = 0x6D656173;                       // ‘meas’, ICC 4.2.0.0 - 10.12
    /// Tag type `multiLocalizedUnicodeType`, signature `mluc`, see section 10.13.
    public static final int multiLocalizedUnicodeType = 0x6D6C7563;             // ‘mluc’, ICC 4.2.0.0 - 10.13
    /// Tag type `namedColor2Type`, signature `ncl2`, see section 10.14.
    public static final int namedColor2Type = 0x6E636C32;                       // ‘ncl2’, ICC 4.2.0.0 - 10.14
    /// Tag type `parametricCurveType`, signature `para`, see section 10.15.
    public static final int parametricCurveType = 0x70617261;                   // ‘para’, ICC 4.2.0.0 - 10.15
    /// Tag type `profileSequenceDescType`, signature `pseq`, see section 10.16.
    public static final int profileSequenceDescType = 0x70736571;               // ‘pseq’, ICC 4.2.0.0 - 10.16
    /// Tag type `responseCurveSet16Type`, signature `rcs2`, see section 10.17.
    public static final int responseCurveSet16Type = 0x72637332;                // ‘rcs2’, ICC 4.2.0.0 - 10.17
    /// Tag type `s15Fixed16ArrayType`, signature `sf32`, see section 10.18.
    public static final int s15Fixed16ArrayType = 0x73663332;                   // ‘sf32’, ICC 4.2.0.0 - 10.18
    /// Tag type `signatureType`, signature `sig`, see section 10.19.
    public static final int signatureType = 0x73696720;                         // ‘sig ’, ICC 4.2.0.0 - 10.19
    /// Tag type `textType`, signature `text`, see section 10.20.
    public static final int textType = 0x74657874;                              // ‘text’, ICC 4.2.0.0 - 10.20
    /// Tag type `u16Fixed16ArrayType`, signature `uf32`, see section 10.21.
    public static final int u16Fixed16ArrayType = 0x75663332;                   // ‘uf32’, ICC 4.2.0.0 - 10.21
    /// Tag type `uInt16ArrayType`, signature `ui16`, see section 10.22.
    public static final int uInt16ArrayType = 0x75693136;                       // ‘ui16’, ICC 4.2.0.0 - 10.22
    /// Tag type `uInt32ArrayType`, signature `ui32`, see section 10.23.
    public static final int uInt32ArrayType = 0x75693332;                       // ‘ui32’, ICC 4.2.0.0 - 10.23
    /// Tag type `uInt64ArrayType`, signature `ui64`, see section 10.24.
    public static final int uInt64ArrayType = 0x75693634;                       // ‘ui64’, ICC 4.2.0.0 - 10.24
    /// Tag type `uInt8ArrayType`, signature `ui08`, see section 10.25.
    public static final int uInt8ArrayType = 0x75693038;                        // ‘ui08’, ICC 4.2.0.0 - 10.25
    /// Tag type `viewingConditionsType`, signature `view`, see section 10.26.
    public static final int viewingConditionsType = 0x76696577;                 // ‘view’, ICC 4.2.0.0 - 10.26
    /// Tag type `XYZType`, signature `XYZ`, see section 10.27.
    public static final int XYZType = 0x58595A20;                               // ‘XYZ ’, ICC 4.2.0.0 - 10.27

    private TagType() {
    }

    /// Converts a type signature number to its 4-character text.
    ///
    /// @param sig Type Signature value in number
    /// @return Type Signature string
    public static String getTypeSignature(int sig) {
        return new String(ByteBuffer.allocate(Integer.BYTES).putInt(sig).array(), StandardCharsets.ISO_8859_1);
    }
}
