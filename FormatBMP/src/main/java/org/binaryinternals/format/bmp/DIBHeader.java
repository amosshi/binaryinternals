/*
 * DIBHeader.java    Nov 29, 2010, 12:28
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.bmp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// The DIB (Device Independent Bitmap) header, which tells the image
/// dimensions and pixel format. Its size tells which version it is.
///
/// @author Amos Shi
public final class DIBHeader extends FileComponent implements GenerateTreeNode {

    /// The size of this header in bytes.
    public final int size;
    /// The header, typed by its version.
    public final Header data;

    DIBHeader(final PosDataInputStream input) throws IOException {
        this.startPos = input.getPos();

        this.size = input.readIntInLittleEndian();
        this.length = this.size;
        this.data = switch (this.size) {
            case BITMAPCOREHEADER.SIZE -> new BITMAPCOREHEADER(input);
            case BITMAPINFOHEADER.SIZE -> new BITMAPINFOHEADER(input);
            case BITMAPV2INFOHEADER.SIZE -> new BITMAPV2INFOHEADER(input);
            case BITMAPV3INFOHEADER.SIZE -> new BITMAPV3INFOHEADER(input);
            case BITMAPCOREHEADER2.SIZE -> new BITMAPCOREHEADER2(input);
            case BITMAPV4HEADER.SIZE -> new BITMAPV4HEADER(input);
            case BITMAPV5HEADER.SIZE -> new BITMAPV5HEADER(input);
            default -> throw new FileFormatException(
                    "Un-recognized DIB Header length encountered (%d).".formatted(this.size));
        };

        // Skip the fields not parsed, like the color masks of BITMAPV2INFOHEADER
        // and BITMAPV3INFOHEADER, so the color table is read from the right place
        final long unread = (long) this.startPos + this.size - input.getPos();
        if (unread > 0) {
            input.skipNBytes(unread);
        }
    }

    /// Get the number of bits-per-pixel.
    ///
    /// @return the bits-per-pixel value
    public int getBitCount() {
        return switch (this.data) {
            case BITMAPCOREHEADER core -> core.BitCount;
            case BITMAPINFOHEADER info -> info.BitCount;
        };
    }

    /// The size of the image in bytes.
    ///
    /// If it is `BITMAPINFOHEADER`, we return the `SizeImage` value, or calculate
    /// it when it is 0, which is allowed for uncompressed bitmaps. If it is
    /// `BITMAPCOREHEADER`, we calculate the value on the fly.
    ///
    /// @return the image size in bytes
    /// @see <a href="http://doxygen.reactos.org/d6/dea/bmpdecode_8c_source.html">BMP Decode source code from www.reactos.org </a>
    public long calcImageSize() {
        return switch (this.data) {
            case BITMAPCOREHEADER core -> this.getRowSize() * Math.abs((long) core.Height);
            // Attention: BITMAPCOREHEADER2 is based on BITMAPINFOHEADER, instead of BITMAPCOREHEADER.
            case BITMAPCOREHEADER2 core2 -> this.getRowSize() * Math.abs((long) core2.Height);
            case BITMAPINFOHEADER info -> info.SizeImage == 0 && this.isUncompressed()
                    ? this.getRowSize() * Math.abs((long) info.Height)
                    : info.SizeImage;
        };
    }

    /// The size of one row of pixels in bytes. Each row is padded to a multiple
    /// of 4 bytes.
    ///
    /// @return the row size in bytes
    public long getRowSize() {
        return (((long) this.getWidth() * this.getBitCount() + 31) / 32) * 4;
    }

    public int getWidth() {
        return switch (this.data) {
            case BITMAPCOREHEADER core -> core.Width;
            case BITMAPINFOHEADER info -> info.Width;
        };
    }

    /// The height in pixels. It is negative for a top-down bitmap, whose first
    /// row is the top of the image.
    ///
    /// @return the height
    public int getHeight() {
        return switch (this.data) {
            case BITMAPCOREHEADER core -> core.Height;
            case BITMAPINFOHEADER info -> info.Height;
        };
    }

    public int getCompressionMethod() {
        return switch (this.data) {
            // If it is BITMAPCOREHEADER, it is the same as not compressed.
            case BITMAPCOREHEADER _ -> CompressionMethod.BI_RGB.value;
            case BITMAPINFOHEADER info -> info.Compression;
        };
    }

    /// The pixels are stored as they are, without RLE, JPEG or PNG compression.
    private boolean isUncompressed() {
        final int compression = this.getCompressionMethod();
        return compression == CompressionMethod.BI_RGB.value || compression == CompressionMethod.BI_BITFIELDS.value;
    }

    /// The number of colors in the color table, or 0 for the default of
    /// 2<sup>n</sup> colors.
    ///
    /// @return the number of colors
    public long getColorsUsed() {
        return switch (this.data) {
            case BITMAPCOREHEADER _ -> 0;
            case BITMAPINFOHEADER info -> info.ColorsUsed;
        };
    }

    public long getICCProfileData() {
        return this.data instanceof BITMAPV5HEADER v5 ? v5.ICCProfileData : -1;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "DIB Header (%s) [0x%08X, %d]".formatted(this.data.getClass().getSimpleName(), this.startPos, this.length),
                "Bitmap Information Header."));
        parentNode.add(node);

        this.data.generateTreeNode(node);
    }

    /// Add one field to the tree.
    private static void addField(final DefaultMutableTreeNode node, final int pos, final int len,
            final String text, final String description) {
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(pos, len, text, description)));
    }

    /// The DIB header versions.
    public sealed interface Header extends GenerateTreeNode permits BITMAPCOREHEADER, BITMAPINFOHEADER {
    }

    /// Or named `OS21XBITMAPHEADER`.
    ///
    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd183372%28VS.85%29.aspx">BITMAPCOREHEADER Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public static final class BITMAPCOREHEADER extends FileComponent implements Header {

        public static final int SIZE = 12;
        public final int Width;
        public final int Height;
        public final int Planes;
        public final int BitCount;

        BITMAPCOREHEADER(final PosDataInputStream input) throws IOException {
            super(input.getPos() - 4, SIZE);

            this.Width = input.readUnsignedShortInLittleEndian();
            this.Height = input.readUnsignedShortInLittleEndian();
            this.Planes = input.readUnsignedShortInLittleEndian();
            this.BitCount = input.readUnsignedShortInLittleEndian();
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            addField(node, this.startPos, 4, "size = %d".formatted(SIZE), "The size of this header in bytes.");
            addField(node, this.startPos + 4, 2, "width = %d".formatted(this.Width), null);
            addField(node, this.startPos + 6, 2, "height = %d".formatted(this.Height), null);
            addField(node, this.startPos + 8, 2, "planes = %d".formatted(this.Planes), null);
            addField(node, this.startPos + 10, 2, "bits per pixel = %d".formatted(this.BitCount), null);
        }
    }

    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd183376%28VS.85%29.aspx">BITMAPINFOHEADER Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public static sealed class BITMAPINFOHEADER extends FileComponent implements Header
            permits BITMAPV2INFOHEADER, BITMAPV3INFOHEADER, BITMAPCOREHEADER2, BITMAPV4HEADER {

        /// The number of bytes required by the structure.
        public static final int SIZE = 40;
        /// The width of the bitmap, in pixels.
        public final int Width;
        /// The height of the bitmap, in pixels. Negative for a top-down bitmap.
        public final int Height;
        /// The number of planes for the target device. This value must be set to 1.
        public final int Planes;
        /// The number of bits-per-pixel.
        public final int BitCount;
        /// The type of compression for a compressed bottom-up bitmap (top-down DIBs cannot be compressed).
        public final int Compression;
        /// The size, in bytes, of the image.
        public final long SizeImage;
        /// The horizontal resolution, in pixels-per-meter, of the target device for the bitmap.
        public final int XPelsPerMeter;
        /// The vertical resolution, in pixels-per-meter, of the target device for the bitmap.
        public final int YPelsPerMeter;
        /// The number of color indexes in the color table that are actually used by the bitmap.
        public final long ColorsUsed;
        /// The number of color indexes that are required for displaying the bitmap.
        public final long ColorsImportant;

        BITMAPINFOHEADER(final PosDataInputStream input) throws IOException {
            super(input.getPos() - 4, SIZE);

            this.Width = input.readIntInLittleEndian();
            this.Height = input.readIntInLittleEndian();
            this.Planes = input.readUnsignedShortInLittleEndian();
            this.BitCount = input.readUnsignedShortInLittleEndian();
            this.Compression = input.readIntInLittleEndian();
            this.SizeImage = input.readUnsignedIntInLittleEndian();
            this.XPelsPerMeter = input.readIntInLittleEndian();
            this.YPelsPerMeter = input.readIntInLittleEndian();
            this.ColorsUsed = input.readUnsignedIntInLittleEndian();
            this.ColorsImportant = input.readUnsignedIntInLittleEndian();
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            addField(node, this.startPos, 4,
                    "size = %d".formatted(this.length),
                    "The size of this header in bytes.");
            addField(node, this.startPos + 4, 4,
                    "bitmap width = %d".formatted(this.Width),
                    "The bitmap width in pixels (signed integer).");
            addField(node, this.startPos + 8, 4,
                    "bitmap height = %d".formatted(this.Height),
                    "The bitmap height in pixels (signed integer). A negative height is a top-down bitmap.");
            addField(node, this.startPos + 12, 2,
                    "color planes = %d".formatted(this.Planes),
                    "The number of color planes being used. Must be set to 1.");
            addField(node, this.startPos + 14, 2,
                    "bits per pixel = %d".formatted(this.BitCount),
                    "The number of bits per pixel, which is the color depth of the image. Typical values are 1, 4, 8, 16, 24 and 32.");
            addField(node, this.startPos + 16, 4,
                    "compression method = %d%s".formatted(this.Compression,
                            CompressionMethod.of(this.Compression).map(m -> " (" + m.name() + ")").orElse("")),
                    "The compression method being used. See the next table for a list of possible values.");
            addField(node, this.startPos + 20, 4,
                    "image size = %d".formatted(this.SizeImage),
                    "The image size. This is the size of the raw bitmap data (see below), and should not be confused with the file size.");
            addField(node, this.startPos + 24, 4,
                    "horizontal resolution = %d".formatted(this.XPelsPerMeter),
                    "The horizontal resolution of the image. (pixel per meter, signed integer).");
            addField(node, this.startPos + 28, 4,
                    "vertical resolution = %d".formatted(this.YPelsPerMeter),
                    "The vertical resolution of the image. (pixel per meter, signed integer).");
            addField(node, this.startPos + 32, 4,
                    "colors = %d".formatted(this.ColorsUsed),
                    "The number of colors in the color palette, or 0 to default to 2<sup>n</sup>.");
            addField(node, this.startPos + 36, 4,
                    "important colors = %d".formatted(this.ColorsImportant),
                    "The number of important colors used, or 0 when every color is important; generally ignored.");
        }
    }

    /// Adds the RGB color masks to `BITMAPINFOHEADER`.
    @SuppressWarnings("PublicInnerClass")
    public static final class BITMAPV2INFOHEADER extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 52;

        BITMAPV2INFOHEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            super.generateTreeNode(node);
            addField(node, this.startPos + BITMAPINFOHEADER.SIZE, SIZE - BITMAPINFOHEADER.SIZE,
                    "BITMAPV2INFOHEADER - Added data", null);
        }
    }

    /// Adds the RGBA color masks to `BITMAPINFOHEADER`.
    @SuppressWarnings("PublicInnerClass")
    public static final class BITMAPV3INFOHEADER extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 56;

        BITMAPV3INFOHEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            super.generateTreeNode(node);
            addField(node, this.startPos + BITMAPINFOHEADER.SIZE, SIZE - BITMAPINFOHEADER.SIZE,
                    "BITMAPV3INFOHEADER - Added data", null);
        }
    }

    /// @see <a href="http://doxygen.reactos.org/d7/d02/structBITMAPCOREHEADER2.html">BITMAPCOREHEADER2 Structure Reference</a>
    @SuppressWarnings("PublicInnerClass")
    public static final class BITMAPCOREHEADER2 extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 64;
        public final int ResUnit;
        public final int Reserved;
        public final int Orientation;
        public final int Halftoning;
        public final long HalftoneSize1;
        public final long HalftoneSize2;
        public final long ColorSpace;
        public final long AppData;

        BITMAPCOREHEADER2(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;

            this.ResUnit = input.readUnsignedShortInLittleEndian();
            this.Reserved = input.readUnsignedShortInLittleEndian();
            this.Orientation = input.readUnsignedShortInLittleEndian();
            this.Halftoning = input.readUnsignedShortInLittleEndian();
            this.HalftoneSize1 = input.readUnsignedIntInLittleEndian();
            this.HalftoneSize2 = input.readUnsignedIntInLittleEndian();
            this.ColorSpace = input.readUnsignedIntInLittleEndian();
            this.AppData = input.readUnsignedIntInLittleEndian();
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            final int start = this.startPos + BITMAPINFOHEADER.SIZE;
            addField(node, start, 2, "ResUnit = %d".formatted(this.ResUnit), null);
            addField(node, start + 2, 2, "Reserved = %d".formatted(this.Reserved), null);
            addField(node, start + 4, 2, "Orientation = %d".formatted(this.Orientation), null);
            addField(node, start + 6, 2, "Halftoning = %d".formatted(this.Halftoning), null);
            addField(node, start + 8, 4, "HalftoneSize1 = %d".formatted(this.HalftoneSize1), null);
            addField(node, start + 12, 4, "HalftoneSize2 = %d".formatted(this.HalftoneSize2), null);
            addField(node, start + 16, 4, "ColorSpace = %d".formatted(this.ColorSpace), null);
            addField(node, start + 20, 4, "AppData = %d".formatted(this.AppData), null);
        }
    }

    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd183380%28VS.85%29.aspx">BITMAPV4HEADER Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public static sealed class BITMAPV4HEADER extends BITMAPINFOHEADER permits BITMAPV5HEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 108;
        /// Color mask that specifies the red component of each pixel, valid only if `Compression` is set to `BI_BITFIELDS`.
        public final long RedMask;
        /// Color mask that specifies the green component of each pixel, valid only if `Compression` is set to `BI_BITFIELDS`.
        public final long GreenMask;
        /// Color mask that specifies the blue component of each pixel, valid only if `Compression` is set to `BI_BITFIELDS`.
        public final long BlueMask;
        /// Color mask that specifies the alpha component of each pixel.
        public final long AlphaMask;
        /// The color space of the DIB.
        public final int ColorSpaceType;
        /// Specifies the x, y, and z coordinates of the three colors that correspond to the red, green, and blue endpoints for the logical color space associated with the bitmap.
        public final CIEXYZTRIPLE Endpoints;
        /// Tone response curve for red.
        public final long GammaRed;
        /// Tone response curve for green.
        public final long GammaGreen;
        /// Tone response curve for blue.
        public final long GammaBlue;

        BITMAPV4HEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;

            this.RedMask = input.readUnsignedIntInLittleEndian();
            this.GreenMask = input.readUnsignedIntInLittleEndian();
            this.BlueMask = input.readUnsignedIntInLittleEndian();
            this.AlphaMask = input.readUnsignedIntInLittleEndian();
            this.ColorSpaceType = input.readIntInLittleEndian();
            this.Endpoints = CIEXYZTRIPLE.read(input);
            this.GammaRed = input.readUnsignedIntInLittleEndian();
            this.GammaGreen = input.readUnsignedIntInLittleEndian();
            this.GammaBlue = input.readUnsignedIntInLittleEndian();
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            final int start = this.startPos + BITMAPINFOHEADER.SIZE;
            addField(node, start, 4, "RedMask = %d".formatted(this.RedMask), null);
            addField(node, start + 4, 4, "GreenMask = %d".formatted(this.GreenMask), null);
            addField(node, start + 8, 4, "BlueMask = %d".formatted(this.BlueMask), null);
            addField(node, start + 12, 4, "AlphaMask = %d".formatted(this.AlphaMask), null);
            addField(node, start + 16, 4, "ColorSpaceType = %d".formatted(this.ColorSpaceType), null);
            addField(node, start + 20, CIEXYZTRIPLE.SIZE, "Endpoints", null);
            addField(node, start + 20 + CIEXYZTRIPLE.SIZE, 4, "GammaRed = %d".formatted(this.GammaRed), null);
            addField(node, start + 20 + CIEXYZTRIPLE.SIZE + 4, 4, "GammaGreen = %d".formatted(this.GammaGreen), null);
            addField(node, start + 20 + CIEXYZTRIPLE.SIZE + 8, 4, "GammaBlue = %d".formatted(this.GammaBlue), null);
        }
    }

    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd183381%28VS.85%29.aspx">BITMAPV5HEADER Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public static final class BITMAPV5HEADER extends BITMAPV4HEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 124;
        /// Rendering intent for bitmap.
        public final long Intent;
        /// The offset, in bytes, from the beginning of the BITMAPV5HEADER structure to the start of the profile data.
        public final long ICCProfileData;
        /// Size, in bytes, of embedded profile data.
        public final long ICCProfileSize;
        /// This member has been reserved.
        public final long Reserved;

        BITMAPV5HEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;

            this.Intent = input.readUnsignedIntInLittleEndian();
            this.ICCProfileData = input.readUnsignedIntInLittleEndian();
            this.ICCProfileSize = input.readUnsignedIntInLittleEndian();
            this.Reserved = input.readUnsignedIntInLittleEndian();
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            final int start = this.startPos + BITMAPV4HEADER.SIZE;
            addField(node, start, 4, "Intent = %d".formatted(this.Intent), null);
            addField(node, start + 4, 4, "ICC Profile Data = %d".formatted(this.ICCProfileData), null);
            addField(node, start + 8, 4, "ICC Profile Size = %d".formatted(this.ICCProfileSize), null);
            addField(node, start + 12, 4, "Reserved = %d".formatted(this.Reserved), null);
        }
    }

    /// @param x The x coordinate in fix point.
    /// @param y The y coordinate in fix point.
    /// @param z The z coordinate in fix point.
    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd371828%28v=VS.85%29.aspx">CIEXYZ Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public record CIEXYZ(long x, long y, long z) {

        static CIEXYZ read(final PosDataInputStream input) throws IOException {
            return new CIEXYZ(
                    input.readUnsignedIntInLittleEndian(),
                    input.readUnsignedIntInLittleEndian(),
                    input.readUnsignedIntInLittleEndian());
        }
    }

    /// @param red The xyz coordinates of red endpoint.
    /// @param green The xyz coordinates of green endpoint.
    /// @param blue The xyz coordinates of blue endpoint.
    /// @see <a href="http://msdn.microsoft.com/en-us/library/dd371833%28v=VS.85%29.aspx">CIEXYZTRIPLE Structure</a>
    @SuppressWarnings("PublicInnerClass")
    public record CIEXYZTRIPLE(CIEXYZ red, CIEXYZ green, CIEXYZ blue) {

        public static final int SIZE = 36;

        static CIEXYZTRIPLE read(final PosDataInputStream input) throws IOException {
            return new CIEXYZTRIPLE(CIEXYZ.read(input), CIEXYZ.read(input), CIEXYZ.read(input));
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public enum CompressionMethod {

        /// No compress method used. This is the most common case.
        BI_RGB(0),
        /// RLE 8-bit/pixel. Can be used only with 8-bit/pixel bitmaps.
        BI_RLE8(1),
        /// RLE 4-bit/pixel. Can be used only with 4-bit/pixel bitmaps.
        BI_RLE4(2),
        /// Bit field or Huffman 1D compression for BITMAPCOREHEADER2.
        /// Pixel format defined by bit masks or Huffman 1D compressed bitmap
        /// for BITMAPCOREHEADER2
        BI_BITFIELDS(3),
        /// JPEG or RLE-24 compression for BITMAPCOREHEADER2.
        /// The bitmap contains a JPEG image or RLE-24 compressed bitmap for
        /// BITMAPCOREHEADER2
        BI_JPEG(4),
        /// The bitmap contains a PNG image.
        BI_PNG(5);
        /// Value of a compression method.
        public final int value;

        CompressionMethod(final int i) {
            this.value = i;
        }

        /// Find the [CompressionMethod] by its [#value].
        ///
        /// @param v Value to look up
        /// @return The matching constant, or empty if not recognized
        public static Optional<CompressionMethod> of(final int v) {
            return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
        }
    }
}
