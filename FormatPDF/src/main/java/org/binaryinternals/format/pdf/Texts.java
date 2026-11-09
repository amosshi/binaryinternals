package org.binaryinternals.format.pdf;

import java.util.Locale;
import java.util.ResourceBundle;

/// Texts used in User Interface.
///
/// @author Amos Shi
public final class Texts {

    /// Tree node text for a new line.
    public static final String NewLine = "New Line";
    /// Tree node text prefix for a signature.
    public static final String Signature = "Signature: ";
    /// Tree node text for a space.
    public static final String Space = "Space";
    /// Tree node text for a white space.
    public static final String WhiteSpace = "White Space";

    /// Message key for the PDF file header.
    public static final String PDF_FILE_HEADER = "PDF_FILE_HEADER";
    /// Message key for the PDF indirect object.
    public static final String PDF_INDIRECT_OBJECT = "PDF_INDIRECT_OBJECT";
    /// Message key for the PDF Cross-Reference Table.
    public static final String PDF_CROSS_REFERENCE_TABLE = "PDF_CROSS_REFERENCE_TABLE";
    /// Message key for the PDF Cross-Reference Table keyword.
    public static final String PDF_CROSS_REFERENCE_TABLE_KEY = "PDF_CROSS_REFERENCE_TABLE_KEY";

    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(Texts.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    private Texts() {
    }

    /// Gets a message text from the resource bundle.
    ///
    /// @param key Message key
    /// @return The message text
    public static String getString(final String key) {
        return MESSAGES.getString(key);
    }
}
