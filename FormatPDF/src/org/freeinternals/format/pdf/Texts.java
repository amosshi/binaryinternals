package org.freeinternals.format.pdf;

import java.util.ResourceBundle;

/**
 * Texts used in User Interface.
 *
 * @author Amos Shi
 */
public class Texts {

    public static final String NewLine = "New Line";
    public static final String Signature = "Signature: ";
    public static final String Space = "Space";
    private static final ResourceBundle res;

    static {
        res = ResourceBundle.getBundle(Texts.class.getName().replace('.', '/'));
    }

    public static String getString(String key) {
        return res.getString(key);
    }
    public static final String PDF_FILE_HEADER = "PDF_FILE_HEADER";
    public static final String PDF_INDIRECT_OBJECT = "PDF_INDIRECT_OBJECT";
    public static final String PDF_CROSS_REFERENCE_TABLE = "PDF_CROSS_REFERENCE_TABLE";
    public static final String PDF_CROSS_REFERENCE_TABLE_KEY = "PDF_CROSS_REFERENCE_TABLE_KEY";
}
