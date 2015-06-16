/*
 * ShortText.java    Oct 26, 2010, 12:14
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
public class ShortText{

    private static final ResourceBundle res;

    static{
        res = ResourceBundle.getBundle(ShortText.class.getName().replace('.', '/'));
    }

    public static final String KEY_Offset_n = "Offset_n";

    public static final String KEY_Type_Rational = "Type_Rational";
    public static final String KEY_Type_SRational = "Type_SRational";

    public static final String KEY_rational_n = "rational_n";
    public static final String KEY_srational_n = "srational_n";
    public static final String KEY_Numerator = "Numerator";
    public static final String KEY_numerator_n = "numerator_n";
    public static final String KEY_Denominator = "Denominator";
    public static final String KEY_denominator_n = "denominator_n";
    public static final String KEY_Unused = "Unused";

    public static String getString(String key){
        return res.getString(key);
    }
}
