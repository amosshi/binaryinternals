/*
 * IFD_0132_DateTime.java    Sep 11, 2010, 23:48
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class IFD_0132_DateTime extends IFD_ASCII {

    public static final int COUNT = 20;
    /// The EXIF date and time format, `YYYY:MM:DD HH:MM:SS`.
    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("uuuu:MM:dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
    /// Length of the date and time text, without the NUL terminator.
    private static final int TEXT_LENGTH = 19;

    /// The date and time, or `null` when the value is not a valid date, like
    /// the blanks EXIF allows for an unknown date.
    public final LocalDateTime datetime;

    public IFD_0132_DateTime(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
        this.datetime = parse(this.value);
    }

    /// Parse the `YYYY:MM:DD HH:MM:SS` text of a date and time tag.
    ///
    /// @param value The tag value, with the NUL terminator
    /// @return The date and time, or `null` if it is not a valid date
    static LocalDateTime parse(final String value) {
        if (value.length() < TEXT_LENGTH) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.substring(0, TEXT_LENGTH), FORMAT);
        } catch (DateTimeParseException _) {
            return null;
        }
    }

    /// Text of a date and time tag: the parsed date, or the raw text if it is
    /// not a valid date.
    static String toText(final LocalDateTime datetime, final String value) {
        return (datetime != null) ? FORMAT.format(datetime) : value.substring(0, Math.min(value.length(), TEXT_LENGTH));
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_ASCII(
                parentNode,
                toText(this.datetime, this.value),
                IFDMessage.getString(IFDMessage.KEY_IFD_0132_Description));
    }
}
