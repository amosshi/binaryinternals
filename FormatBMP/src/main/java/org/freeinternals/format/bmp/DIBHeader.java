/**
 * DIBHeader.java    Nov 29, 2010, 12:28
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.bmp;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class DIBHeader extends FileComponent implements GenerateTreeNode {

    /** The size of this header in bytes. */
    public final int size;
    public final Object data;

    DIBHeader(final PosDataInputStream input) throws IOException {
        this.startPos = input.getPos();

        this.size = input.readInt_LittleEndian();
        this.length = this.size;
        switch (this.size) {
            case 12:
                this.data = new BITMAPCOREHEADER(input);
                break;
            case 40:
                this.data = new BITMAPINFOHEADER(input);
                break;
            case 52:
                this.data = new BITMAPV2INFOHEADER(input);
                break;
            case 56:
                this.data = new BITMAPV3INFOHEADER(input);
                break;
            case 64:
                this.data = new BITMAPCOREHEADER2(input);
                break;
            case 108:
                this.data = new BITMAPV4HEADER(input);
                break;
            case 124:
                this.data = new BITMAPV5HEADER(input);
                break;
            default:
                this.data = null;
                throw new IllegalArgumentException(String.format(
                        "Un-recognized DIB Header length encountered (%d).", this.length));
        }
    }

    /**
     * Get the number of bits-per-pixel.
     *
     * @return the bits-per-pixel value
     */
    public int getBitCount() {
        if (this.data instanceof BITMAPCOREHEADER) {
            return ((BITMAPCOREHEADER) this.data).BitCount;
        } else if (this.data instanceof BITMAPINFOHEADER) {
            return ((BITMAPINFOHEADER) this.data).BitCount;
        } else {
            return -1;
        }
    }

    ;

    /**
     * The size of the image in bytes.
     * If it is BITMAPINFOHEADER, we return the <code>SizeImage</code> value;
     * if it is BITMAPCOREHEADER, we calculate the value on the fly.
     *
     * @return the image size in bytes
     * @see <a href="http://doxygen.reactos.org/d6/dea/bmpdecode_8c_source.html">BMP Decode source code from www.reactos.org </a>
     */
    public long calcImageSize() {
        if (this.data instanceof BITMAPCOREHEADER) {
            // The <code>ceil</code> method needs a double paramter, int parameter will cause error because it is not float.
            BITMAPCOREHEADER core = (BITMAPCOREHEADER) this.data;
            return ((long)(Math.ceil(((double)core.Width) * core.BitCount / 32) * 4)) * core.Height;
        } else if (this.data instanceof BITMAPCOREHEADER2) {
            // Attention: BITMAPCOREHEADER2 is based on BITMAPINFOHEADER, instead of BITMAPCOREHEADER.
            BITMAPCOREHEADER2 core2 = (BITMAPCOREHEADER2) this.data;
            return ((long)(Math.ceil(((double)core2.Width) * core2.BitCount / 32) * 4)) * core2.Height;
        } else if (this.data instanceof BITMAPINFOHEADER) {
            return ((BITMAPINFOHEADER) this.data).SizeImage;
        } else {
            return -1;
        }
    }

    public int getWidth() {
        if (this.data instanceof BITMAPCOREHEADER) {
            return ((BITMAPCOREHEADER) this.data).Width;
        } else if (this.data instanceof BITMAPINFOHEADER) {
            return ((BITMAPINFOHEADER) this.data).Width;
        } else {
            return -1;
        }
    }

    public int getHeight() {
        if (this.data instanceof BITMAPCOREHEADER) {
            return ((BITMAPCOREHEADER) this.data).Height;
        } else if (this.data instanceof BITMAPINFOHEADER) {
            return ((BITMAPINFOHEADER) this.data).Height;
        } else {
            return -1;
        }
    }

    public int getCompressionMethod() {
        if (this.data instanceof BITMAPINFOHEADER) {
            return ((BITMAPINFOHEADER) this.data).Compression;
        } else {
            // If it is BITMAPCOREHEADER, it is the same as not compressed.
            return CompressionMethod.BI_RGB.value;
        }
    }

    public long getICCProfileData() {
        if (this.data instanceof BITMAPV5HEADER) {
            return ((BITMAPV5HEADER) this.data).ICCProfileData;
        } else {
            return -1;
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("DIB Header (%s) [0x%08X, %d]", this.data.getClass().getSimpleName(), this.startPos, this.length));
        comp.setDescription("Bitmap Information Header.");
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        if (this.data instanceof GenerateTreeNode) {
            ((GenerateTreeNode) this.data).generateTreeNode(node);
        }
    }

    /**
     * Or named <code>OS21XBITMAPHEADER</code>.
     *
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd183372%28VS.85%29.aspx">BITMAPCOREHEADER Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class BITMAPCOREHEADER extends FileComponent implements GenerateTreeNode {

        public static final int SIZE = 12;
        public final int Width;
        public final int Height;
        public final int Planes;
        public final int BitCount;

        BITMAPCOREHEADER(final PosDataInputStream input) throws IOException {
            this.startPos = input.getPos() - 4;
            this.length = SIZE;

            this.Width = input.readUnsignedShort_LittleEndian();
            this.Height = input.readUnsignedShort_LittleEndian();
            this.Planes = input.readUnsignedShort_LittleEndian();
            this.BitCount = input.readUnsignedShort_LittleEndian();
        }

        public void generateTreeNode(DefaultMutableTreeNode node) {
            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos,
                    4,
                    String.format("size = %d", SIZE));
            comp.setDescription("The size of this header in bytes.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 4,
                    2,
                    String.format("width = %d", this.Width));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 6,
                    2,
                    String.format("height = %d", this.Height));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    2,
                    String.format("planes = %d", this.Planes));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 10,
                    2,
                    String.format("bits per pixel = %d", this.BitCount));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    };

    /**
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd183376%28VS.85%29.aspx">BITMAPINFOHEADER Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class BITMAPINFOHEADER extends FileComponent implements GenerateTreeNode {

        /** The number of bytes required by the structure. */
        public static final int SIZE = 40;
        /** The width of the bitmap, in pixels. */
        public final int Width;
        /** The height of the bitmap, in pixels. */
        public final int Height;
        /** The number of planes for the target device. This value must be set to 1. */
        public final int Planes;
        /** The number of bits-per-pixel. */
        public final int BitCount;
        /** The type of compression for a compressed bottom-up bitmap (top-down DIBs cannot be compressed). */
        public final int Compression;
        /** The size, in bytes, of the image. */
        public final long SizeImage;
        /** The horizontal resolution, in pixels-per-meter, of the target device for the bitmap. */
        public final int XPelsPerMeter;
        /** The vertical resolution, in pixels-per-meter, of the target device for the bitmap. */
        public final int YPelsPerMeter;
        /** The number of color indexes in the color table that are actually used by the bitmap. */
        public final long ColorsUsed;
        /** The number of color indexes that are required for displaying the bitmap. */
        public final long ColorsImportant;

        BITMAPINFOHEADER(final PosDataInputStream input) throws IOException {
            this.startPos = input.getPos() - 4;
            this.length = SIZE;

            this.Width = input.readInt_LittleEndian();
            this.Height = input.readInt_LittleEndian();
            this.Planes = input.readUnsignedShort_LittleEndian();
            this.BitCount = input.readUnsignedShort_LittleEndian();
            this.Compression = input.readInt_LittleEndian();
            this.SizeImage = input.readUnsignedInt_LittleEndian();
            this.XPelsPerMeter = input.readInt_LittleEndian();
            this.YPelsPerMeter = input.readInt_LittleEndian();
            this.ColorsUsed = input.readUnsignedInt_LittleEndian();
            this.ColorsImportant = input.readUnsignedInt_LittleEndian();
        }

        public void generateTreeNode(DefaultMutableTreeNode node) {
            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos,
                    4,
                    String.format("size = %d", this.length));
            comp.setDescription("The size of this header in bytes.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 4,
                    4,
                    String.format("bitmap width = %d", this.Width));
            comp.setDescription("The bitmap width in pixels (signed integer).");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    String.format("bitmap height = %d", this.Height));
            comp.setDescription("The bitmap width in pixels (signed integer).");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 12,
                    2,
                    String.format("color planes = %d", this.Planes));
            comp.setDescription("The number of color planes being used. Must be set to 1.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 14,
                    2,
                    String.format("bits per pixel = %d", this.BitCount));
            comp.setDescription("The number of bits per pixel, which is the color depth of the image. Typical values are 1, 4, 8, 16, 24 and 32.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 16,
                    4,
                    String.format("compression method = %d", this.Compression));
            comp.setDescription("The compression method being used. See the next table for a list of possible values.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 20,
                    4,
                    String.format("image size = %d", this.SizeImage));
            comp.setDescription("The image size. This is the size of the raw bitmap data (see below), and should not be confused with the file size.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 24,
                    4,
                    String.format("horizontal resolution = %d", this.XPelsPerMeter));
            comp.setDescription("The horizontal resolution of the image. (pixel per meter, signed integer).");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 28,
                    4,
                    String.format("vertical resolution = %d", this.YPelsPerMeter));
            comp.setDescription("The vertical resolution of the image. (pixel per meter, signed integer).");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 32,
                    4,
                    String.format("colors = %d", this.ColorsUsed));
            comp.setDescription("The number of colors in the color palette, or 0 to default to 2<sup>n</sup>.");
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 36,
                    4,
                    String.format("important colors = %d", this.ColorsImportant));
            comp.setDescription("The number of important colors used, or 0 when every color is important; generally ignored.");
            node.add(new DefaultMutableTreeNode(comp));
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public class BITMAPV2INFOHEADER extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 52;

        BITMAPV2INFOHEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = BITMAPV2INFOHEADER.SIZE;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE,
                    SIZE - BITMAPINFOHEADER.SIZE,
                    "BITMAPV2INFOHEADER - Added data");
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    };

    @SuppressWarnings("PublicInnerClass")
    public class BITMAPV3INFOHEADER extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 56;

        BITMAPV3INFOHEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = BITMAPV3INFOHEADER.SIZE;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE,
                    SIZE - BITMAPINFOHEADER.SIZE,
                    "BITMAPV2INFOHEADER - Added data");
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    };

    /**
     * @see <a href="http://doxygen.reactos.org/d7/d02/structBITMAPCOREHEADER2.html">BITMAPCOREHEADER2 Structure Reference</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class BITMAPCOREHEADER2 extends BITMAPINFOHEADER {

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
            this.length = BITMAPCOREHEADER2.SIZE;

            this.ResUnit = input.readUnsignedShort_LittleEndian();
            this.Reserved = input.readUnsignedShort_LittleEndian();
            this.Orientation = input.readUnsignedShort_LittleEndian();
            this.Halftoning = input.readUnsignedShort_LittleEndian();
            this.HalftoneSize1 = input.readUnsignedInt_LittleEndian();
            this.HalftoneSize2 = input.readUnsignedInt_LittleEndian();
            this.ColorSpace = input.readUnsignedInt_LittleEndian();
            this.AppData = input.readUnsignedInt_LittleEndian();
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE,
                    2,
                    String.format("ResUnit = %d", ResUnit));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 2,
                    2,
                    String.format("Reserved = %d", Reserved));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 4,
                    2,
                    String.format("Orientation = %d", Orientation));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 6,
                    2,
                    String.format("Halftoning = %d", Halftoning));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 8,
                    4,
                    String.format("HalftoneSize1 = %d", HalftoneSize1));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 12,
                    4,
                    String.format("HalftoneSize2 = %d", HalftoneSize2));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 16,
                    4,
                    String.format("ColorSpace = %d", ColorSpace));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 20,
                    4,
                    String.format("AppData = %d", AppData));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    };

    /**
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd183380%28VS.85%29.aspx">BITMAPV4HEADER Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class BITMAPV4HEADER extends BITMAPINFOHEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 108;
        /** Color mask that specifies the red component of each pixel, valid only if <code>Compression</code> is set to <code>BI_BITFIELDS</code>. */
        public final long RedMask;
        /** Color mask that specifies the green component of each pixel, valid only if <code>Compression</code> is set to <code>BI_BITFIELDS</code>. */
        public final long GreenMask;
        /** Color mask that specifies the blue component of each pixel, valid only if <code>Compression</code> is set to <code>BI_BITFIELDS</code>. */
        public final long BlueMask;
        /** Color mask that specifies the alpha component of each pixel. */
        public final long AlphaMask;
        /** The color space of the DIB. */
        public final int ColorSpaceType;
        /** Specifies the x, y, and z coordinates of the three colors that correspond to the red, green, and blue endpoints for the logical color space associated with the bitmap. */
        public final CIEXYZTRIPLE Endpoints;
        /** Tone response curve for red. */
        public final long GammaRed;
        /** Tone response curve for green. */
        public final long GammaGreen;
        /** Tone response curve for blue. */
        public final long GammaBlue;

        BITMAPV4HEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = BITMAPV4HEADER.SIZE;

            this.RedMask = input.readUnsignedInt_LittleEndian();
            this.GreenMask = input.readUnsignedInt_LittleEndian();
            this.BlueMask = input.readUnsignedInt_LittleEndian();
            this.AlphaMask = input.readUnsignedInt_LittleEndian();
            this.ColorSpaceType = input.readInt_LittleEndian();
            this.Endpoints = new CIEXYZTRIPLE(input);
            this.GammaRed = input.readUnsignedInt_LittleEndian();
            this.GammaGreen = input.readUnsignedInt_LittleEndian();
            this.GammaBlue = input.readUnsignedInt_LittleEndian();
        }

        /**
         * 
         * @param node
         */
        @Override
        public void generateTreeNode(DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE,
                    4,
                    String.format("RedMask = %d", RedMask));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 4,
                    4,
                    String.format("GreenMask = %d", GreenMask));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 8,
                    4,
                    String.format("BlueMask = %d", BlueMask));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 12,
                    4,
                    String.format("AlphaMask = %d", AlphaMask));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 16,
                    4,
                    String.format("ColorSpaceType = %d", ColorSpaceType));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 20,
                    CIEXYZTRIPLE.SIZE,
                    "Endpoints");
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 20 + CIEXYZTRIPLE.SIZE,
                    4,
                    String.format("GammaRed = %d", GammaRed));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 20 + CIEXYZTRIPLE.SIZE + 4,
                    4,
                    String.format("GammaGreen = %d", GammaGreen));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPINFOHEADER.SIZE + 20 + CIEXYZTRIPLE.SIZE + 8,
                    4,
                    String.format("GammaBlue = %d", GammaBlue));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    }

    /**
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd183381%28VS.85%29.aspx">BITMAPV5HEADER Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class BITMAPV5HEADER extends BITMAPV4HEADER {

        @SuppressWarnings("FieldNameHidesFieldInSuperclass")
        public static final int SIZE = 124;
        /** Rendering intent for bitmap. */
        public final long Intent;
        /** The offset, in bytes, from the beginning of the BITMAPV5HEADER structure to the start of the profile data. */
        public final long ICCProfileData;
        /** Size, in bytes, of embedded profile data. */
        public final long ICCProfileSize;
        /** This member has been reserved. */
        public final long Reserved;

        BITMAPV5HEADER(final PosDataInputStream input) throws IOException {
            super(input);
            this.length = SIZE;

            this.Intent = input.readUnsignedInt_LittleEndian();
            this.ICCProfileData = input.readUnsignedInt_LittleEndian();
            this.ICCProfileSize = input.readUnsignedInt_LittleEndian();
            this.Reserved = input.readUnsignedInt_LittleEndian();
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode node) {
            super.generateTreeNode(node);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPV4HEADER.SIZE,
                    4,
                    String.format("Intent = %d", Intent));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPV4HEADER.SIZE + 4,
                    4,
                    String.format("ICC Profile Data = %d", ICCProfileData));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPV4HEADER.SIZE + 8,
                    4,
                    String.format("ICC Profile Size = %d", ICCProfileSize));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + BITMAPV4HEADER.SIZE + 12,
                    4,
                    String.format("Reserved = %d", Reserved));
            comp.setDescription(null);
            node.add(new DefaultMutableTreeNode(comp));
        }
    }

    /**
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd371828%28v=VS.85%29.aspx">CIEXYZ Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class CIEXYZ {

        /** The x coordinate in fix point. */
        public final long x;
        /** The y coordinate in fix point. */
        public final long y;
        /** The z coordinate in fix point. */
        public final long z;

        CIEXYZ(final PosDataInputStream input) throws IOException {
            this.x = input.readUnsignedInt_LittleEndian();
            this.y = input.readUnsignedInt_LittleEndian();
            this.z = input.readUnsignedInt_LittleEndian();
        }
    }

    /**
     * @see <a href="http://msdn.microsoft.com/en-us/library/dd371833%28v=VS.85%29.aspx">CIEXYZTRIPLE Structure</a>
     */
    @SuppressWarnings("PublicInnerClass")
    public class CIEXYZTRIPLE {

        public static final int SIZE = 36;
        /** The xyz coordinates of red endpoint. */
        public final CIEXYZ Red;
        /** The xyz coordinates of green endpoint. */
        public final CIEXYZ Green;
        /** The xyz coordinates of blue endpoint. */
        public final CIEXYZ Blue;

        CIEXYZTRIPLE(final PosDataInputStream input) throws IOException {
            this.Red = new CIEXYZ(input);
            this.Green = new CIEXYZ(input);
            this.Blue = new CIEXYZ(input);
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public enum CompressionMethod {

        /** No compress method used. This is the most common case. */
        BI_RGB(0),
        /** RLE 8-bit/pixel. Can be used only with 8-bit/pixel bitmaps. */
        BI_RLE8(1),
        /** RLE 4-bit/pixel. Can be used only with 4-bit/pixel bitmaps. */
        BI_RLE4(2),
        /** Bit field or Huffman 1D compression for BITMAPCOREHEADER2.
         *  Pixel format defined by bit masks or Huffman 1D compressed bitmap
         *  for BITMAPCOREHEADER2
         */
        BI_BITFIELDS(3),
        /** JPEG or RLE-24 compression for BITMAPCOREHEADER2.
         *  The bitmap contains a JPEG image or RLE-24 compressed bitmap for
         *  BITMAPCOREHEADER2
         */
        BI_JPEG(4),
        /** The bitmap contains a PNG image. */
        BI_PNG(5);
        /** Value of a compression method. */
        public final int value;

        CompressionMethod(int i) {
            this.value = i;
        }
    }
}
