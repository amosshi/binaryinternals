/*
 * IFDMessage.java    Oct 07, 2010, 11:24
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.util.Locale;
import java.util.ResourceBundle;

/// @author Amos shi
public class IFDMessage {

    private static final ResourceBundle res;

    static {
        res = ResourceBundle.getBundle(IFDMessage.class.getName().replace('.', '/'), Locale.ROOT);
    }

    public static final String KEY_IFD = "IFD";
    public static final String KEY_IFD_Type = "IFD_Type";
    public static final String KEY_IFD_Tag = "IFD_Tag";
    public static final String KEY_IFD_Count = "IFD_Count";
    public static final String KEY_IFD_Offset = "IFD_Offset";
    public static final String KEY_IFD_Value = "IFD_Value";
    public static final String KEY_IFD_Value_Ref = "IFD_Value_Ref";

    public static final String KEY_IFD_8769_Exif_Category_A = "IFD_8769_Exif_Category_A";
    public static final String KEY_IFD_8769_Exif_Category_B = "IFD_8769_Exif_Category_B";
    public static final String KEY_IFD_8769_Exif_Category_C = "IFD_8769_Exif_Category_C";
    public static final String KEY_IFD_8769_Exif_Category_D = "IFD_8769_Exif_Category_D";
    public static final String KEY_IFD_8769_Exif_Category_E = "IFD_8769_Exif_Category_E";
    public static final String KEY_IFD_8769_Exif_Category_F = "IFD_8769_Exif_Category_F";
    public static final String KEY_IFD_8769_Exif_Category_G = "IFD_8769_Exif_Category_G";
    public static final String KEY_IFD_8769_Exif_FormatVersion = "IFD_8769_Exif_FormatVersion";
    public static final String KEY_IFD_8825_GPS_Category_A = "IFD_8825_GPS_Category_A";
    public static final String KEY_IFD_A005_Intero_Category_A = "IFD_A005_Intero_Category_A";

    public static final String KEY_IFD_GPS_0000_Description = "IFD_GPS_0000_Description";
    public static final String KEY_IFD_ItO_0001_Description = "IFD_ItO_0001_Description";

    /// Format of the description key of a common tag, with the tag in 4 upper case hex digits,
    /// like `IFD_A409_Description`.
    ///
    /// @see IFD#getDescription()
    public static final String KEY_FORMAT_IFD_DESCRIPTION = "IFD_%04X_Description";

    private IFDMessage() {
    }

    public static String getString(String key) {
        return res.getString(key);
    }
}
