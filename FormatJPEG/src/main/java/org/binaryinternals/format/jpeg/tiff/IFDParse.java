/*
 * IFDParse.java    Sep 09, 2010, 12:16
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;


/// @author Amos Shi
public class IFDParse {

    private IFDParse() {
    }

    public static IFD parse(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        return switch (tag) {
            case IFDTag.IFD_0106_PhotometricInterpretation -> new IFD_0106_PhotometricInterpretation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_010E_ImageDescription -> new IFD_010E_ImageDescription(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_010F_Make -> new IFD_010F_Make(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0110_Model -> new IFD_0110_Model(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0112_Orientation -> new IFD_0112_Orientation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_011A_XResolution -> new IFD_011A_XResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_011B_YResolution -> new IFD_011B_YResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0128_ResolutionUnit -> new IFD_0128_ResolutionUnit(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0131_Software -> new IFD_0131_Software(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0132_DateTime -> new IFD_0132_DateTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_013E_WhitePoint -> new IFD_013E_WhitePoint(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_013F_PrimaryChromaticities -> new IFD_013F_PrimaryChromaticities(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0201_JPEGInterchangeFormat -> new IFD_0201_JPEGInterchangeFormat(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0202_JPEGInterchangeFormatLength -> new IFD_0202_JPEGInterchangeFormatLength(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0211_YCbCrCoefficients -> new IFD_0211_YCbCrCoefficients(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0213_YCbCrPositioning -> new IFD_0213_YCbCrPositioning(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0214_ReferenceBlackWhite -> new IFD_0214_ReferenceBlackWhite(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_829A_ExposureTime -> new IFD_829A_ExposureTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_829D_FNumber -> new IFD_829D_FNumber(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8769_Exif -> new IFD_8769_Exif(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8822_ExposureProgram -> new IFD_8822_ExposureProgram(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8825_GPS -> new IFD_8825_GPS(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8827_PhotographicSensitivity -> new IFD_8827_PhotographicSensitivity(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9000_ExifVersion -> new IFD_9000_ExifVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9003_DateTimeOriginal -> new IFD_9003_DateTimeOriginal(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9004_DateTimeDigitized -> new IFD_9004_DateTimeDigitized(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9101_ComponentsConfiguration -> new IFD_9101_ComponentsConfiguration(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9102_CompressedBitsPerPixel -> new IFD_9102_CompressedBitsPerPixel(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9201_ShutterSpeedValue -> new IFD_9201_ShutterSpeedValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9202_ApertureValue -> new IFD_9202_ApertureValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9204_ExposureBiasValue -> new IFD_9204_ExposureBiasValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9205_MaxApertureValue -> new IFD_9205_MaxApertureValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9207_MeteringMode -> new IFD_9207_MeteringMode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9208_LightSource -> new IFD_9208_LightSource(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9209_Flash -> new IFD_9209_Flash(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_920A_FocalLength -> new IFD_920A_FocalLength(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_927C_MakerNode -> new IFD_927C_MakerNode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9286_UserComment -> new IFD_9286_UserComment(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9290_SubsecTime -> new IFD_9290_SubsecTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9291_SubsecTimeOriginal -> new IFD_9291_SubsecTimeOriginal(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9292_SubsecTimeDigitized -> new IFD_9292_SubsecTimeDigitized(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A000_FlashpixVersion -> new IFD_A000_FlashpixVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A001_ColorSpace -> new IFD_A001_ColorSpace(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A002_PixelXDimension -> new IFD_A002_PixelXDimension(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);   // Short or Long
            case IFDTag.IFD_A003_PixelYDimension -> new IFD_A003_PixelYDimension(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);   // Short or Long
            case IFDTag.IFD_A005_Interoperability -> new IFD_A005_Interoperability(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A20E_FocalPlaneXResolution -> new IFD_A20E_FocalPlaneXResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A20F_FocalPlaneYResolution -> new IFD_A20F_FocalPlaneYResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A210_FocalPlaneResolutionUnit -> new IFD_A210_FocalPlaneResolutionUnit(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A215_ExposureIndex -> new IFD_A215_ExposureIndex(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A217_SensingMethod -> new IFD_A217_SensingMethod(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A300_FileSource -> new IFD_A300_FileSource(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A301_SceneType -> new IFD_A301_SceneType(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A302_CFAPattern -> new IFD_A302_CFAPattern(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A401_CustomRendered -> new IFD_A401_CustomRendered(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A402_ExposureMode -> new IFD_A402_ExposureMode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A403_WhiteBalance -> new IFD_A403_WhiteBalance(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A404_DigitalZoomRatio -> new IFD_A404_DigitalZoomRatio(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A405_FocalLengthIn35mmFilm -> new IFD_A405_FocalLengthIn35mmFilm(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A406_SceneCaptureType -> new IFD_A406_SceneCaptureType(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A407_GainControl -> new IFD_A407_GainControl(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A408_Contrast -> new IFD_A408_Contrast(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A409_Saturation -> new IFD_A409_Saturation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A40A_Sharpness -> new IFD_A40A_Sharpness(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A40C_SubjectDistanceRange -> new IFD_A40C_SubjectDistanceRange(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A500_Gamma -> new IFD_A500_Gamma(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            default -> switch (type) {
                case IFDType.SHORT -> new IFD_SHORT(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.LONG -> new IFD_LONG(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.ASCII -> new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.RATIONAL -> new IFD_RATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.UNDEFINED -> new IFD_UNDEFINED(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.SRATIONAL -> new IFD_SRATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                default -> new IFD(pDIS, byteOrder, tag, type, startPosTiff);
            };
        };
    }

    static IFD parseGPS(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        return switch (tag) {
            case IFDTag.IFD_0000_GPSVersionID -> new IFD_0000_GPSVersionID(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            default -> switch (type) {
                case IFDType.ASCII -> new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.BYTE -> new IFD_BYTE(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.RATIONAL -> new IFD_RATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.SHORT -> new IFD_SHORT(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                case IFDType.UNDEFINED -> new IFD_UNDEFINED(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                default -> new IFD(pDIS, byteOrder, tag, type, startPosTiff);
            };
        };

    }

    static IFD parseIntero(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        return switch (tag) {
            case IFDTag.IFD_0001_InteroperabilityIndex -> new IFD_0001_InteroperabilityIndex(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0002_InteroperabilityVersion -> new IFD_0002_InteroperabilityVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            default -> switch (type) {
                case IFDType.ASCII -> new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                default -> new IFD(pDIS, byteOrder, tag, type, startPosTiff);
            };
        };
    }

    public static int readUnsignedShort(final PosDataInputStream pDIS, int byteOrder) throws IOException {
        return pDIS.readUnsignedShort(TIFFHeader.toByteOrder(byteOrder));
    }

    public static int readInt(final PosDataInputStream pDIS, int byteOrder) throws IOException {
        return pDIS.readInt(TIFFHeader.toByteOrder(byteOrder));
    }

    static long readUnsignedInt(final PosDataInputStream pDIS, int byteOrder) throws IOException {
        return pDIS.readUnsignedInt(TIFFHeader.toByteOrder(byteOrder));
    }

    static PosDataInputStream getTiffOffsetReader(final byte[] tiffByteArray, int valueOffset, int dataSize) throws IOException {
        final PosDataInputStream reader = new PosDataInputStream(new PosByteArrayInputStream(tiffByteArray));

        // The offset is unsigned, a negative int is beyond any TIFF data held in memory
        if (valueOffset < 0 || (long) valueOffset + dataSize > tiffByteArray.length) {
            throw new FileFormatException("TIFF IFD: the data [%d, %d bytes] is beyond the TIFF data length %d".formatted(
                    Integer.toUnsignedLong(valueOffset), dataSize, tiffByteArray.length));
        }

        reader.skipNBytes(valueOffset);
        return reader;
    }
}
