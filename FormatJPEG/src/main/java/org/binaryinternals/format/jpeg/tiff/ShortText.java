/*
 * ShortText.java    Oct 26, 2010, 12:14
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.util.ResourceBundle;

/// Short texts of the TIFF tree nodes, from the `ShortText` resource bundle.
///
/// @author Amos shi
public class ShortText {

    private static final ResourceBundle res;

    static {
        res = ResourceBundle.getBundle(ShortText.class.getName().replace('.', '/'));
    }

    /// Message key of the text: Offset with an index.
    public static final String KEY_Offset_n = "Offset_n";

    /// Message key of the text: The `RATIONAL` type.
    public static final String KEY_Type_Rational = "Type_Rational";
    /// Message key of the text: The `SRATIONAL` type.
    public static final String KEY_Type_SRational = "Type_SRational";

    /// Message key of the text: `RATIONAL` value with an index.
    public static final String KEY_rational_n = "rational_n";
    /// Message key of the text: `SRATIONAL` value with an index.
    public static final String KEY_srational_n = "srational_n";
    /// Message key of the text: Numerator.
    public static final String KEY_Numerator = "Numerator";
    /// Message key of the text: Numerator with an index.
    public static final String KEY_numerator_n = "numerator_n";
    /// Message key of the text: Denominator.
    public static final String KEY_Denominator = "Denominator";
    /// Message key of the text: Denominator with an index.
    public static final String KEY_denominator_n = "denominator_n";
    /// Message key of the text: Unused bytes.
    public static final String KEY_Unused = "Unused";

    private ShortText() {
    }

    /// Gets a text from the resource bundle.
    ///
    /// @param key Message key
    /// @return The text
    public static String getString(String key) {
        return res.getString(key);
    }
}
