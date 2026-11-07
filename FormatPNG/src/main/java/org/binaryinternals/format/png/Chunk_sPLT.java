/*
 * Chunk_sPLT.java    May 02, 2011, 11:33
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// This chunk can be used to suggest a reduced palette to be used when the
/// display device is not capable of displaying the full range of colors present
/// in the image.
///
/// If present, it provides a recommended set of colors, with alpha and
/// frequency information, that can be used to construct a reduced palette to
/// which the PNG image can be quantized.
///
/// This chunk contains a null-terminated text string that names the palette and
/// a one-byte sample depth, followed by a series of palette entries, each a
/// six-byte or ten-byte series containing five unsigned integers:
///
/// ```
/// Palette name:    1-79 bytes (character string)
/// Null terminator: 1 byte
/// Sample depth:    1 byte
/// Red:             1 or 2 bytes
/// Green:           1 or 2 bytes
/// Blue:            1 or 2 bytes
/// Alpha:           1 or 2 bytes
/// Frequency:       2 bytes
/// ...etc...
/// ```
///
/// There can be any number of entries; a decoder determines the number of
/// entries from the remaining chunk length after the sample depth byte. It is
/// an error if this remaining length is not divisible by 6 (if the `sPLT`
/// sample depth is 8) or by 10 (if the `sPLT` sample depth is 16). Entries must
/// appear in decreasing order of frequency. There is no requirement that the
/// entries all be used by the image, nor that they all be different.
///
/// @author Amos Shi
public final class Chunk_sPLT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "sPLT";
    /// Size of [SuggestedPaletteEntry#Frequency()].
    private static final int FREQUENCY_SIZE = 2;

    /// The palette name can be any convenient name for referring to the palette
    /// (for example, "256 color including Macintosh default", "256 color
    /// including Windows-3.1 default", "Optimal 512").
    ///
    /// It may help applications or people to choose the appropriate suggested
    /// palette when more than one appears in a PNG file. The palette name is
    /// case-sensitive and subject to the same restrictions as a text keyword: it
    /// must contain only printable Latin-1 \[ISO/IEC-8859-1\] characters (33-126
    /// and 161-255) and spaces (32), but no leading, trailing, or consecutive
    /// spaces.
    public final String PaletteName;
    /// The sPLT sample depth must be 8 or 16.
    public final int SampleDepth;
    /// The palette entries, or `null` when there is none.
    public final SuggestedPaletteEntry[] SuggestedPalette;

    Chunk_sPLT(final PosDataInputStream stream) throws IOException {
        super(stream);

        final PosDataInputStream chunkDataStream = this.getChunkDataStream();
        this.PaletteName = chunkDataStream.readASCII();
        this.SampleDepth = chunkDataStream.read();
        if (this.SampleDepth != 8 && this.SampleDepth != 16) {
            throw new FileFormatException(
                    "Invalid 'Sample depth' for sPLT chunk. value = %d, expected value = 8 or 16".formatted(this.SampleDepth));
        }

        final int sampleSize = this.getSampleSize();
        final int entryLength = this.getEntryLength();
        final int plteLength = this.Length - this.PaletteName.length() - 1 - 1;
        if (plteLength % entryLength != 0) {
            throw new FileFormatException(
                    "Invalid sPLT chunk since palette length is not divisible by %d. value = %d".formatted(entryLength, plteLength));
        }

        final int plteCount = plteLength / entryLength;
        if (plteCount > 0) {
            this.SuggestedPalette = new SuggestedPaletteEntry[plteCount];
            for (int i = 0; i < plteCount; i++) {
                this.SuggestedPalette[i] = new SuggestedPaletteEntry(
                        readSample(chunkDataStream, sampleSize),
                        readSample(chunkDataStream, sampleSize),
                        readSample(chunkDataStream, sampleSize),
                        readSample(chunkDataStream, sampleSize),
                        chunkDataStream.readUnsignedShort());
            }
        } else {
            this.SuggestedPalette = null;
        }
    }

    /// Size of one red, green, blue or alpha sample: 1 byte for sample depth 8,
    /// 2 bytes for sample depth 16.
    private int getSampleSize() {
        return this.SampleDepth / Byte.SIZE;
    }

    /// Size of one palette entry: 6 bytes for sample depth 8, 10 bytes for
    /// sample depth 16.
    private int getEntryLength() {
        return 4 * this.getSampleSize() + FREQUENCY_SIZE;
    }

    private static int readSample(final PosDataInputStream stream, final int sampleSize) throws IOException {
        return sampleSize == 1 ? stream.read() : stream.readUnsignedShort();
    }

    @Override
    protected void generateTreeNodeChunkData(final DefaultMutableTreeNode parent) {
        int start = this.getDataStartPos();

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.PaletteName.length(),
                "Palette name = %s".formatted(this.PaletteName))));
        start += this.PaletteName.length();
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Null terminator")));
        start += 1;
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                1,
                "Sample depth = %d".formatted(this.SampleDepth))));
        start += 1;

        if (this.SuggestedPalette == null) {
            return;
        }

        final int sampleSize = this.getSampleSize();
        final int entryLength = this.getEntryLength();
        for (int i = 0; i < this.SuggestedPalette.length; i++) {
            final SuggestedPaletteEntry entry = this.SuggestedPalette[i];
            final DefaultMutableTreeNode entryNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    entryLength,
                    "Suggested Palette entry [%d] = %s".formatted(i, entry)));
            parent.add(entryNode);

            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    sampleSize,
                    "Red = %03d".formatted(entry.Red()))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + sampleSize,
                    sampleSize,
                    "Green = %03d".formatted(entry.Green()))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 2 * sampleSize,
                    sampleSize,
                    "Blue = %03d".formatted(entry.Blue()))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 3 * sampleSize,
                    sampleSize,
                    "Alpha = %03d".formatted(entry.Alpha()))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start + 4 * sampleSize,
                    FREQUENCY_SIZE,
                    "Frequency = %03d".formatted(entry.Frequency()))));
            start += entryLength;
        }
    }

    /// One suggested palette entry.
    ///
    /// @param Red The red, green, blue, and alpha samples are either one or two
    ///        bytes each, depending on the `sPLT` sample depth, regardless of the
    ///        image bit depth.
    /// @param Green Refer to `Red`.
    /// @param Blue Refer to `Red`.
    /// @param Alpha The color samples are not premultiplied by alpha, nor are
    ///        they precomposited against any background. An alpha value of 0
    ///        means fully transparent, while an alpha value of 255 (when the
    ///        `sPLT` sample depth is 8) or 65535 (when the `sPLT` sample depth is
    ///        16) means fully opaque. The palette samples have the same gamma and
    ///        chromaticity values as those of the PNG image.
    /// @param Frequency Each frequency value is proportional to the fraction of
    ///        pixels in the image that are closest to that palette entry in RGBA
    ///        space, before the image has been composited against any
    ///        background. The exact scale factor is chosen by the encoder, but
    ///        should be chosen so that the range of individual values reasonably
    ///        fills the range 0 to 65535. It is acceptable to artificially
    ///        inflate the frequencies for "important" colors such as those in a
    ///        company logo or in the facial features of a portrait. Zero is a
    ///        valid frequency meaning the color is "least important" or that it
    ///        is rarely if ever used. But when all of the frequencies are zero,
    ///        they are meaningless (nothing may be inferred about the actual
    ///        frequencies of the colors).
    public record SuggestedPaletteEntry(int Red, int Green, int Blue, int Alpha, int Frequency) {

        @Override
        public String toString() {
            return "RGB: (%03d, %03d, %03d), Alpha = %03d, Frequency = %03d".formatted(
                    this.Red, this.Green, this.Blue,
                    this.Alpha, this.Frequency);
        }
    }
}
