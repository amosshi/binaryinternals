/*
 * IFDParse.java    Sep 09, 2010, 12:16
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;


/**
 *
 * @author Amos Shi
 */
public class IFDParse {

    public static IFD parse(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        switch (tag) {
            case IFDTag.IFD_0106_PhotometricInterpretation:
                return new IFD_0106_PhotometricInterpretation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_010E_ImageDescription:
                return new IFD_010E_ImageDescription(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_010F_Make:
                return new IFD_010F_Make(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0110_Model:
                return new IFD_0110_Model(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0112_Orientation:
                return new IFD_0112_Orientation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_011A_XResolution:
                return new IFD_011A_XResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_011B_YResolution:
                return new IFD_011B_YResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0128_ResolutionUnit:
                return new IFD_0128_ResolutionUnit(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0131_Software:
                return new IFD_0131_Software(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0132_DateTime:
                return new IFD_0132_DateTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_013E_WhitePoint:
                return new IFD_013E_WhitePoint(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_013F_PrimaryChromaticities:
                return new IFD_013F_PrimaryChromaticities(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0201_JPEGInterchangeFormat:
                return new IFD_0201_JPEGInterchangeFormat(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0202_JPEGInterchangeFormatLength:
                return new IFD_0202_JPEGInterchangeFormatLength(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0211_YCbCrCoefficients:
                return new IFD_0211_YCbCrCoefficients(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0213_YCbCrPositioning:
                return new IFD_0213_YCbCrPositioning(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0214_ReferenceBlackWhite:
                return new IFD_0214_ReferenceBlackWhite(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

            case IFDTag.IFD_829A_ExposureTime:
                return new IFD_829A_ExposureTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_829D_FNumber:
                return new IFD_829D_FNumber(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8769_Exif:
                return new IFD_8769_Exif(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8822_ExposureProgram:
                return new IFD_8822_ExposureProgram(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8825_GPS:
                return new IFD_8825_GPS(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_8827_PhotographicSensitivity:
                return new IFD_8827_PhotographicSensitivity(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9000_ExifVersion:
                return new IFD_9000_ExifVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9003_DateTimeOriginal:
                return new IFD_9003_DateTimeOriginal(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9004_DateTimeDigitized:
                return new IFD_9004_DateTimeDigitized(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9101_ComponentsConfiguration:
                return new IFD_9101_ComponentsConfiguration(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9102_CompressedBitsPerPixel:
                return new IFD_9102_CompressedBitsPerPixel(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9201_ShutterSpeedValue:
                return new IFD_9201_ShutterSpeedValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9202_ApertureValue:
                return new IFD_9202_ApertureValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9204_ExposureBiasValue:
                return new IFD_9204_ExposureBiasValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9205_MaxApertureValue:
                return new IFD_9205_MaxApertureValue(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9207_MeteringMode:
                return new IFD_9207_MeteringMode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9208_LightSource:
                return new IFD_9208_LightSource(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9209_Flash:
                return new IFD_9209_Flash(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_920A_FocalLength:
                return new IFD_920A_FocalLength(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_927C_MakerNode:
                return new IFD_927C_MakerNode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9286_UserComment:
                return new IFD_9286_UserComment(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9290_SubsecTime:
                return new IFD_9290_SubsecTime(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9291_SubsecTimeOriginal:
                return new IFD_9291_SubsecTimeOriginal(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_9292_SubsecTimeDigitized:
                return new IFD_9292_SubsecTimeDigitized(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A000_FlashpixVersion:
                return new IFD_A000_FlashpixVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A001_ColorSpace:
                return new IFD_A001_ColorSpace(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A002_PixelXDimension:                           // Short or Long
                return new IFD_A002_PixelXDimension(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A003_PixelYDimension:                           // Short or Long
                return new IFD_A003_PixelYDimension(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A005_Interoperability:
                return new IFD_A005_Interoperability(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A20E_FocalPlaneXResolution:
                return new IFD_A20E_FocalPlaneXResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A20F_FocalPlaneYResolution:
                return new IFD_A20F_FocalPlaneYResolution(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A210_FocalPlaneResolutionUnit:
                return new IFD_A210_FocalPlaneResolutionUnit(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A215_ExposureIndex:
                return new IFD_A215_ExposureIndex(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A217_SensingMethod:
                return new IFD_A217_SensingMethod(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A300_FileSource:
                return new IFD_A300_FileSource(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A301_SceneType:
                return new IFD_A301_SceneType(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A302_CFAPattern:
                return new IFD_A302_CFAPattern(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A401_CustomRendered:
                return new IFD_A401_CustomRendered(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A402_ExposureMode:
                return new IFD_A402_ExposureMode(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A403_WhiteBalance:
                return new IFD_A403_WhiteBalance(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A404_DigitalZoomRatio:
                return new IFD_A404_DigitalZoomRatio(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A405_FocalLengthIn35mmFilm:
                return new IFD_A405_FocalLengthIn35mmFilm(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A406_SceneCaptureType:
                return new IFD_A406_SceneCaptureType(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A407_GainControl:
                return new IFD_A407_GainControl(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A408_Contrast:
                return new IFD_A408_Contrast(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A409_Saturation:
                return new IFD_A409_Saturation(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A40A_Sharpness:
                return new IFD_A40A_Sharpness(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A40C_SubjectDistanceRange:
                return new IFD_A40C_SubjectDistanceRange(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_A500_Gamma:
                return new IFD_A500_Gamma(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

            default:
                if (type == IFDType.SHORT) {
                    return new IFD_SHORT(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.LONG) {
                    return new IFD_LONG(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.ASCII) {
                    return new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.RATIONAL) {
                    return new IFD_RATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.UNDEFINED) {
                    return new IFD_UNDEFINED(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.SRATIONAL) {
                    return new IFD_SRATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else {
                    return new IFD(pDIS, byteOrder, tag, type, startPosTiff);
                }
        }
    }

    static IFD parseGPS(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        switch (tag) {
            case IFDTag.IFD_0000_GPSVersionID:
                return new IFD_0000_GPSVersionID(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

            default:
                if (type == IFDType.ASCII) {
                    return new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.BYTE) {
                    return new IFD_BYTE(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.RATIONAL) {
                    return new IFD_RATIONAL(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.SHORT) {
                    return new IFD_SHORT(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else if (type == IFDType.UNDEFINED) {
                    return new IFD_UNDEFINED(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else {
                    return new IFD(pDIS, byteOrder, tag, type, startPosTiff);
                }
        }

    }

    static IFD parseIntero(final PosDataInputStream pDIS, int byteOrder, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        final int tag = readUnsignedShort(pDIS, byteOrder);
        final int type = readUnsignedShort(pDIS, byteOrder);

        switch (tag) {
            case IFDTag.IFD_0001_InteroperabilityIndex:
                return new IFD_0001_InteroperabilityIndex(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            case IFDTag.IFD_0002_InteroperabilityVersion:
                return new IFD_0002_InteroperabilityVersion(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
            default:
                if (type == IFDType.ASCII) {
                    return new IFD_ASCII(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
                } else {
                    return new IFD(pDIS, byteOrder, tag, type, startPosTiff);
                }
        }
    }

    public static int readUnsignedShort(final PosDataInputStream pDIS, int byteOrder) throws IOException {
        if (byteOrder == TIFFHeader.BYTEORDER_BIGENDIAN) {
            return pDIS.readUnsignedShort();
        } else {
            return pDIS.readUnsignedShort_LittleEndian();
        }
    }

    public static int readInt(final PosDataInputStream pDIS, int byteOrder) throws IOException {
        if (byteOrder == TIFFHeader.BYTEORDER_BIGENDIAN) {
            return pDIS.readInt();
        } else {
            return pDIS.readInt_LittleEndian();
        }
    }
}
