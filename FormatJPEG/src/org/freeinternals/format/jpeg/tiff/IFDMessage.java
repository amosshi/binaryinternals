/*
 * IFDMessage.java    Oct 07, 2010, 11:24
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.util.ResourceBundle;

/**
 *
 * @author Amos shi
 */
public class IFDMessage{

    private static final ResourceBundle res;

    static{
        res = ResourceBundle.getBundle(IFDMessage.class.getName().replace('.', '/'));
    }

    public static final String KEY_IFD = "IFD";
    public static final String KEY_IFD_Type = "IFD_Type";
    public static final String KEY_IFD_Tag = "IFD_Tag";
    public static final String KEY_IFD_Count = "IFD_Count";
    public static final String KEY_IFD_Offset = "IFD_Offset";
    public static final String KEY_IFD_Value = "IFD_Value";
    public static final String KEY_IFD_Value_Ref = "IFD_Value_Ref";

    public static final String KEY_IFD_0106_Description = "IFD_0106_Description";
    public static final String KEY_IFD_010F_Description = "IFD_010F_Description";
    public static final String KEY_IFD_0110_Description = "IFD_0110_Description";
    public static final String KEY_IFD_0112_Description = "IFD_0112_Description";
    public static final String KEY_IFD_011A_Description = "IFD_011A_Description";
    public static final String KEY_IFD_011B_Description = "IFD_011B_Description";
    public static final String KEY_IFD_0128_Description = "IFD_0128_Description";
    public static final String KEY_IFD_0131_Description = "IFD_0131_Description";
    public static final String KEY_IFD_0132_Description = "IFD_0132_Description";
    public static final String KEY_IFD_013E_Description = "IFD_013E_Description";
    public static final String KEY_IFD_013F_Description = "IFD_013F_Description";
    public static final String KEY_IFD_0201_Description = "IFD_0201_Description";
    public static final String KEY_IFD_0202_Description = "IFD_0202_Description";
    public static final String KEY_IFD_0211_Description = "IFD_0211_Description";
    public static final String KEY_IFD_0213_Description = "IFD_0213_Description";
    public static final String KEY_IFD_0214_Description = "IFD_0214_Description";
    public static final String KEY_IFD_829A_Description = "IFD_829A_Description";
    public static final String KEY_IFD_829D_Description = "IFD_829D_Description";
    public static final String KEY_IFD_8769_Exif_Category_A = "IFD_8769_Exif_Category_A";
    public static final String KEY_IFD_8769_Exif_Category_B = "IFD_8769_Exif_Category_B";
    public static final String KEY_IFD_8769_Exif_Category_C = "IFD_8769_Exif_Category_C";
    public static final String KEY_IFD_8769_Exif_Category_D = "IFD_8769_Exif_Category_D";
    public static final String KEY_IFD_8769_Exif_Category_E = "IFD_8769_Exif_Category_E";
    public static final String KEY_IFD_8769_Exif_Category_F = "IFD_8769_Exif_Category_F";
    public static final String KEY_IFD_8769_Exif_Category_G = "IFD_8769_Exif_Category_G";
    public static final String KEY_IFD_8769_Exif_FormatVersion = "IFD_8769_Exif_FormatVersion";
    public static final String KEY_IFD_8822_Description = "IFD_8822_Description";
    public static final String KEY_IFD_8825_GPS_Category_A = "IFD_8825_GPS_Category_A";
    public static final String KEY_IFD_8827_Description = "IFD_8827_Description";
    public static final String KEY_IFD_9000_Description = "IFD_9000_Description";
    public static final String KEY_IFD_9003_Description = "IFD_9003_Description";
    public static final String KEY_IFD_9004_Description = "IFD_9004_Description";
    public static final String KEY_IFD_9101_Description = "IFD_9101_Description";
    public static final String KEY_IFD_9102_Description = "IFD_9102_Description";
    public static final String KEY_IFD_9201_Description = "IFD_9201_Description";
    public static final String KEY_IFD_9202_Description = "IFD_9202_Description";
    public static final String KEY_IFD_9204_Description = "IFD_9204_Description";
    public static final String KEY_IFD_9205_Description = "IFD_9205_Description";
    public static final String KEY_IFD_9207_Description = "IFD_9207_Description";
    public static final String KEY_IFD_9208_Description = "IFD_9208_Description";
    public static final String KEY_IFD_9209_Description = "IFD_9209_Description";
    public static final String KEY_IFD_920A_Description = "IFD_920A_Description";
    public static final String KEY_IFD_927C_Description = "IFD_927C_Description";
    public static final String KEY_IFD_9286_Description = "IFD_9286_Description";
    public static final String KEY_IFD_9290_Description = "IFD_9290_Description";
    public static final String KEY_IFD_9291_Description = "IFD_9291_Description";
    public static final String KEY_IFD_9292_Description = "IFD_9292_Description";
    public static final String KEY_IFD_A000_Description = "IFD_A000_Description";
    public static final String KEY_IFD_A001_Description = "IFD_A001_Description";
    public static final String KEY_IFD_A002_Description = "IFD_A002_Description";
    public static final String KEY_IFD_A003_Description = "IFD_A003_Description";
    public static final String KEY_IFD_A005_Intero_Category_A = "IFD_A005_Intero_Category_A";
    public static final String KEY_IFD_A20E_Description = "IFD_A20E_Description";
    public static final String KEY_IFD_A20F_Description = "IFD_A20F_Description";
    public static final String KEY_IFD_A210_Description = "IFD_A210_Description";
    public static final String KEY_IFD_A215_Description = "IFD_A215_Description";
    public static final String KEY_IFD_A217_Description = "IFD_A217_Description";
    public static final String KEY_IFD_A300_Description = "IFD_A300_Description";
    public static final String KEY_IFD_A301_Description = "IFD_A301_Description";
    public static final String KEY_IFD_A302_Description = "IFD_A302_Description";
    public static final String KEY_IFD_A401_Description = "IFD_A401_Description";
    public static final String KEY_IFD_A402_Description = "IFD_A402_Description";
    public static final String KEY_IFD_A403_Description = "IFD_A403_Description";
    public static final String KEY_IFD_A404_Description = "IFD_A404_Description";
    public static final String KEY_IFD_A405_Description = "IFD_A405_Description";
    public static final String KEY_IFD_A406_Description = "IFD_A406_Description";
    public static final String KEY_IFD_A407_Description = "IFD_A407_Description";
    public static final String KEY_IFD_A408_Description = "IFD_A408_Description";
    public static final String KEY_IFD_A409_Description = "IFD_A409_Description";
    public static final String KEY_IFD_A40A_Description = "IFD_A40A_Description";
    public static final String KEY_IFD_A40C_Description = "IFD_A40C_Description";
    public static final String KEY_IFD_A500_Description = "IFD_A500_Description";

    public static final String KEY_IFD_GPS_0000_Description = "IFD_GPS_0000_Description";
    public static final String KEY_IFD_ItO_0001_Description = "IFD_ItO_0001_Description";

    public static String getString(String key){
        return res.getString(key);
    }
}
