/**
 * Chunk_hIST.java    May 02, 2011, 11:33
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 * This chunk can be used to suggest a reduced palette to be used when the 
 * display device is not capable of displaying the full range of colors present
 * in the image.
 * <p>
 * If present, it provides a recommended set of colors, with alpha and frequency
 * information, that can be used to construct a reduced palette to which the 
 * PNG image can be quantized.
 * </p>
 * <p>
 * This chunk contains a null-terminated text string that names the palette and
 * a one-byte sample depth, followed by a series of palette entries, each a 
 * six-byte or ten-byte series containing five unsigned integers:
 * </p>
 * <pre>
 * Palette name:    1-79 bytes (character string)
 * Null terminator: 1 byte
 * Sample depth:    1 byte
 * Red:             1 or 2 bytes
 * Green:           1 or 2 bytes
 * Blue:            1 or 2 bytes
 * Alpha:           1 or 2 bytes
 * Frequency:       2 bytes
 * ...etc...
 * </pre>
 * <p>
 * There can be any number of entries; a decoder determines the number of 
 * entries from the remaining chunk length after the sample depth byte.
 * It is an error if this remaining length is not divisible by 6 (if the 
 * <span>sPLT</span> sample depth is 8) or by 10 (if the <span>sPLT</span> 
 * sample depth is 16).
 * Entries must appear in decreasing order of frequency.
 * There is no requirement that the entries all be used by the image, nor that
 * they all be different.
 * </p>
 *
 * @author Amos Shi
 */
public class Chunk_sPLT extends Chunk {

    public static final String CHUNK_TYPE_NAME = "sPLT";
    /**
     * The palette name can be any convenient name for referring to the palette
     * (for example, "256 color including Macintosh default", "256 color
     * including Windows-3.1 default", "Optimal 512").
     * <p>
     * It may help applications or people to choose the appropriate suggested
     * palette when more than one appears in a PNG file.
     * The palette name is case-sensitive and subject to the same restrictions
     * as a text keyword: it must contain only printable Latin-1
     * [ISO/IEC-8859-1] characters (33-126 and 161-255) and spaces (32), but no
     * leading, trailing, or consecutive spaces.
     * </p>
     */
    public final String PaletteName;
    /**
     * The sPLT sample depth must be 8 or 16.
     */
    public final int SampleDepth;
    public final SuggestedPaletteEntry[] SuggestedPalette;

    /**
     * Get Chunk Type in binary format.
     */
    static byte[] GetChunkType() {
        return CHUNK_TYPE_NAME.getBytes();
    }

    public Chunk_sPLT(PosDataInputStream stream, PNGFile png) throws IOException, FileFormatException {
        super(stream, png);

        PosDataInputStream chunkDataStream = super.getChunkDataStream();
        this.PaletteName = chunkDataStream.readASCII();
        this.SampleDepth = chunkDataStream.read();
        if (this.SampleDepth != 8 && this.SampleDepth != 16) {
            throw new FileFormatException(String.format(
                    "Invalid 'Sample depth' for sPLT chunk. value = %d, expected value = 8 or 16", this.SampleDepth));
        }

        int plteLength = this.Length - this.PaletteName.length() - 1 - 1;
        int plteCount;
        switch (this.SampleDepth) {
            case 8:
                if (plteLength % 6 != 0) {
                    throw new FileFormatException(String.format(
                            "Invalid sPLT chunk since palette lenth is not divisible by 6. value = %d", plteLength));
                }
                plteCount = plteLength / 6;
                if (plteCount > 0) {
                    this.SuggestedPalette = new SuggestedPaletteEntry[plteCount];
                    for (int i = 0; i < plteCount; i++) {
                        this.SuggestedPalette[i] = new SuggestedPaletteEntry(
                                chunkDataStream.read(),
                                chunkDataStream.read(),
                                chunkDataStream.read(),
                                chunkDataStream.read(),
                                chunkDataStream.readUnsignedShort());
                    }
                } else {
                    this.SuggestedPalette = null;
                }
                break;
            case 16:
                if (plteLength % 10 != 0) {
                    throw new FileFormatException(String.format(
                            "Invalid sPLT chunk since palette lenth is not divisible by 10. value = %d", plteLength));
                }
                plteCount = plteLength / 10;
                if (plteCount > 0) {
                    this.SuggestedPalette = new SuggestedPaletteEntry[plteCount];
                    for (int i = 0; i < plteCount; i++) {
                        this.SuggestedPalette[i] = new SuggestedPaletteEntry(
                                chunkDataStream.readUnsignedShort(),
                                chunkDataStream.readUnsignedShort(),
                                chunkDataStream.readUnsignedShort(),
                                chunkDataStream.readUnsignedShort(),
                                chunkDataStream.readUnsignedShort());
                    }
                } else {
                    this.SuggestedPalette = null;
                }
                break;
            default:
                this.SuggestedPalette = null;
                break;
        }
    }

    @Override
    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        int start = this.startPos + 4 + 4;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                this.PaletteName.length(),
                String.format("Palette name = %s", this.PaletteName))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += this.PaletteName.length(),
                1,
                "Null terminator")));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start += 1,
                1,
                String.format("Sample depth = %d", this.SampleDepth))));
        start += 1;

        if (this.SuggestedPalette == null) {
            return;
        }

        DefaultMutableTreeNode entryNode;
        int i = 0;
        int entryLength = 0, entrySize = 0;
        for (SuggestedPaletteEntry entry : this.SuggestedPalette) {
            switch (this.SampleDepth) {
                case 8:
                    entryLength = 6;
                    entrySize = 1;
                    break;
                case 16:
                    entryLength = 10;
                    entrySize = 2;
                    break;
            }

            parent.add(entryNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    entryLength,
                    String.format("Suggested Palette entry [%d] = %s", i, entry.toString()))));

            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start,
                    entrySize,
                    String.format("Red = %03d", entry.Red))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += entrySize,
                    entrySize,
                    String.format("Green = %03d", entry.Green))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += entrySize,
                    entrySize,
                    String.format("Blue = %03d", entry.Blue))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += entrySize,
                    entrySize,
                    String.format("Alpha = %03d", entry.Alpha))));
            entryNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start += entrySize,
                    2,
                    String.format("Frequency = %03d", entry.Frequency))));
            start += 2;

            i++;
        }

    }

    public class SuggestedPaletteEntry {

        /**
         * The red, green, blue, and alpha samples are either one or two bytes
         * each, depending on the <span>sPLT</span> sample depth, regardless of
         * the image bit depth.
         */
        public final int Red;
        /**
         * Refer to {@link #Red}.
         *
         * @see #Red
         */
        public final int Green;
        /**
         * Refer to {@link #Red}.
         *
         * @see #Red
         */
        public final int Blue;
        /**
         * The color samples are not premultiplied by alpha, nor are they
         * precomposited against any background.
         * <p>
         * An alpha value of 0 means fully transparent, while an alpha value of
         * 255 (when the <span>sPLT</span> sample depth is 8) or 65535 (when the
         * <span>sPLT</span> sample depth is 16) means fully opaque.
         * The palette samples have the same gamma and chromaticity values as
         * those of the PNG image.
         * </p>
         *
         * @see #Red
         */
        public final int Alpha;
        /**
         * Each frequency value is proportional to the fraction of pixels in
         * the image that are closest to that palette entry in RGBA space,
         * before the image has been composited against any background.
         * <p>
         * The exact scale factor is chosen by the encoder, but should be chosen
         * so that the range of individual values reasonably fills the range 0
         * to 65535.
         * It is acceptable to artificially inflate the frequencies for
         * "important" colors such as those in a company logo or in the facial
         * features of a portrait.
         * Zero is a valid frequency meaning the color is "least important" or
         * that it is rarely if ever used. But when all of the frequencies are
         * zero, they are meaningless (nothing may be inferred about the actual
         * frequencies of the colors).
         * </p>
         */
        public final int Frequency;

        public SuggestedPaletteEntry(int r, int g, int b, int a, int f) {
            this.Red = r;
            this.Green = g;
            this.Blue = b;
            this.Alpha = a;
            this.Frequency = f;
        }

        @Override
        public String toString() {
            return String.format("RGB: (%03d, %03d, %03d), Alpha = %03d, Frequency = %03d",
                    this.Red, this.Green, this.Blue,
                    this.Alpha, this.Frequency);
        }
    }
}
