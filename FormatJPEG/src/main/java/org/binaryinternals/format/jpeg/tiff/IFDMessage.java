/*
 * IFDMessage.java    Oct 07, 2010, 11:24
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.util.Locale;
import java.util.ResourceBundle;

/// Texts of the TIFF IFD tree nodes, from the `IFDMessage` resource bundle.
///
/// @author Amos shi
public class IFDMessage {

    private static final ResourceBundle res;

    static {
        res = ResourceBundle.getBundle(IFDMessage.class.getName().replace('.', '/'), Locale.ROOT);
    }

    /// Message key of the IFD entry.
    public static final String KEY_IFD = "IFD";
    /// Message key of the IFD entry type field.
    public static final String KEY_IFD_Type = "IFD_Type";
    /// Message key of the IFD entry tag field.
    public static final String KEY_IFD_Tag = "IFD_Tag";
    /// Message key of the IFD entry count field.
    public static final String KEY_IFD_Count = "IFD_Count";
    /// Message key of the IFD entry value offset field.
    public static final String KEY_IFD_Offset = "IFD_Offset";
    /// Message key of the IFD entry value, stored in the value offset field.
    public static final String KEY_IFD_Value = "IFD_Value";
    /// Message key of the IFD entry value, stored at the value offset.
    public static final String KEY_IFD_Value_Ref = "IFD_Value_Ref";

    /// Message key of the Exif tags category A: tags relating to version.
    public static final String KEY_IFD_8769_Exif_Category_A = "IFD_8769_Exif_Category_A";
    /// Message key of the Exif tags category B: tags relating to image data characteristics.
    public static final String KEY_IFD_8769_Exif_Category_B = "IFD_8769_Exif_Category_B";
    /// Message key of the Exif tags category C: tags relating to image configuration.
    public static final String KEY_IFD_8769_Exif_Category_C = "IFD_8769_Exif_Category_C";
    /// Message key of the Exif tags category D: tags relating to user information.
    public static final String KEY_IFD_8769_Exif_Category_D = "IFD_8769_Exif_Category_D";
    /// Message key of the Exif tags category E: tags relating to related file information.
    public static final String KEY_IFD_8769_Exif_Category_E = "IFD_8769_Exif_Category_E";
    /// Message key of the Exif tags category F: tags relating to date and time.
    public static final String KEY_IFD_8769_Exif_Category_F = "IFD_8769_Exif_Category_F";
    /// Message key of the Exif tags category G: tags relating to picture-taking conditions.
    public static final String KEY_IFD_8769_Exif_Category_G = "IFD_8769_Exif_Category_G";
    /// Message key of the Exif format version.
    public static final String KEY_IFD_8769_Exif_FormatVersion = "IFD_8769_Exif_FormatVersion";
    /// Message key of the GPS tags category A.
    public static final String KEY_IFD_8825_GPS_Category_A = "IFD_8825_GPS_Category_A";
    /// Message key of the Interoperability tags category A.
    public static final String KEY_IFD_A005_Intero_Category_A = "IFD_A005_Intero_Category_A";

    /// Message key of the description of the GPS tag `GPSVersionID`.
    public static final String KEY_IFD_GPS_0000_Description = "IFD_GPS_0000_Description";
    /// Message key of the description of the Interoperability tag `InteroperabilityIndex`.
    public static final String KEY_IFD_ItO_0001_Description = "IFD_ItO_0001_Description";

    /// Format of the description key of a common tag, with the tag in 4 upper case hex digits,
    /// like `IFD_A409_Description`.
    ///
    /// @see IFD#getDescription()
    public static final String KEY_FORMAT_IFD_DESCRIPTION = "IFD_%04X_Description";

    private IFDMessage() {
    }

    /// Gets a message text from the resource bundle.
    ///
    /// @param key Message key
    /// @return The message text
    public static String getString(String key) {
        return res.getString(key);
    }
}
