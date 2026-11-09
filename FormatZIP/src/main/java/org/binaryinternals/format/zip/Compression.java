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

    /// Method 0: the file is stored, no compression.
    STORED(0, "Stored (no compression)"),
    /// Method 1: Shrunk.
    SHRUNK(1, "Shrunk"),
    /// Method 2: Reduced with compression factor 1.
    REDUCED_1(2, "Reduced with compression factor 1"),
    /// Method 3: Reduced with compression factor 2.
    REDUCED_2(3, "Reduced with compression factor 2"),
    /// Method 4: Reduced with compression factor 3.
    REDUCED_3(4, "Reduced with compression factor 3"),
    /// Method 5: Reduced with compression factor 4.
    REDUCED_4(5, "Reduced with compression factor 4"),
    /// Method 6: Imploded.
    IMPLODED(6, "Imploded"),
    /// Method 8: Deflated, the most common one.
    DEFLATED(8, "Deflated"),
    /// Method 9: Enhanced Deflating using Deflate64.
    DEFLATE64(9, "Enhanced Deflating using Deflate64"),
    /// Method 10: PKWARE Data Compression Library Imploding.
    PKWARE_IMPLODED(10, "PKWARE Data Compression Library Imploding"),
    /// Method 12: BZIP2.
    BZIP2(12, "BZIP2"),
    /// Method 14: LZMA.
    LZMA(14, "LZMA"),
    /// Method 16: IBM z/OS CMPSC Compression.
    IBM_CMPSC(16, "IBM z/OS CMPSC Compression"),
    /// Method 18: IBM TERSE.
    IBM_TERSE(18, "IBM TERSE"),
    /// Method 19: IBM LZ77 z Architecture.
    IBM_LZ77(19, "IBM LZ77 z Architecture"),
    /// Method 20: Zstandard, deprecated; use method 93 instead.
    ZSTD_DEPRECATED(20, "Zstandard (deprecated)"),
    /// Method 93: Zstandard.
    ZSTD(93, "Zstandard"),
    /// Method 94: MP3.
    MP3(94, "MP3"),
    /// Method 95: XZ.
    XZ(95, "XZ"),
    /// Method 96: JPEG variant.
    JPEG(96, "JPEG variant"),
    /// Method 97: WavPack.
    WAVPACK(97, "WavPack"),
    /// Method 98: PPMd version I, Rev 1.
    PPMD(98, "PPMd version I, Rev 1"),
    /// Method 99: AE-x encryption marker, the actual method is in the AES extra field.
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
