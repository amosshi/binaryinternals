/*
 * Compression.java    Nov 06, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import java.util.Arrays;
import java.util.Optional;

/// The `compression method` of a local file header or a central directory
/// file header, see `APPNOTE.TXT` 4.4.5.
///
/// @author Amos Shi
public enum Compression {

    STORED(0, "Stored (no compression)"),
    SHRUNK(1, "Shrunk"),
    REDUCED_1(2, "Reduced with compression factor 1"),
    REDUCED_2(3, "Reduced with compression factor 2"),
    REDUCED_3(4, "Reduced with compression factor 3"),
    REDUCED_4(5, "Reduced with compression factor 4"),
    IMPLODED(6, "Imploded"),
    DEFLATED(8, "Deflated"),
    DEFLATE64(9, "Enhanced Deflating using Deflate64"),
    PKWARE_IMPLODED(10, "PKWARE Data Compression Library Imploding"),
    BZIP2(12, "BZIP2"),
    LZMA(14, "LZMA"),
    IBM_CMPSC(16, "IBM z/OS CMPSC Compression"),
    IBM_TERSE(18, "IBM TERSE"),
    IBM_LZ77(19, "IBM LZ77 z Architecture"),
    ZSTD_DEPRECATED(20, "Zstandard (deprecated)"),
    ZSTD(93, "Zstandard"),
    MP3(94, "MP3"),
    XZ(95, "XZ"),
    JPEG(96, "JPEG variant"),
    WAVPACK(97, "WavPack"),
    PPMD(98, "PPMd version I, Rev 1"),
    AES(99, "AE-x encryption marker");

    /// The value stored in the file.
    public final int value;
    /// Human readable name.
    public final String description;

    Compression(final int value, final String description) {
        this.value = value;
        this.description = description;
    }

    /// Find the compression method of `value`.
    ///
    /// @param value The `compression method` field value
    /// @return The compression method, or empty for unknown values
    public static Optional<Compression> of(final int value) {
        return Arrays.stream(values()).filter(m -> m.value == value).findFirst();
    }

    /// Text for the tree node of the `compression method` field.
    static String toText(final int value) {
        return "compression method = %d (%s)".formatted(
                value, of(value).map(m -> m.description).orElse("Unknown"));
    }
}
