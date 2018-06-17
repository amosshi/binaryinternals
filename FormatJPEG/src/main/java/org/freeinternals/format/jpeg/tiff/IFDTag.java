/*
 * IFDTag.java    Sep 06, 2010, 23:41
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.jpeg.tiff;

import java.util.ResourceBundle;

/**
 *
 * @author Amos Shi
 */
public class IFDTag {

    ////////////////////////////////////////////////////////////////////////////
    // ITFF-v6

    public static final int IFD_00FE_NewSubfileType = 0x00FE;
    public static final int IFD_00FF_SubfileType = 0x00FF;
    public static final int IFD_0100_ImageWidth = 0x0100;                       // TIFF-v6: Page 18, 34
    public static final int IFD_0101_ImageLength = 0x0101;                      // TIFF-v6: Page 18, 34
    public static final int IFD_0102_BitsPerSample = 0x0102;                    // TIFF-v6: Page 22, 29
    public static final int IFD_0103_Compression = 0x0103;                      // TIFF-v6: Page 17, 30, 49, 104
    public static final int IFD_0106_Color = 0x0106;                            // TIFF-v6: Page 17, 111
    public static final int IFD_0106_PhotometricInterpretation = 0x0106;        // TIFF-v6: Page 37, 90, 111
    public static final int IFD_0107_Threshholding = 0x0107;
    public static final int IFD_0108_CellWidth = 0x0108;
    public static final int IFD_0109_CellLength = 0x0109;
    public static final int IFD_010A_FillOrder = 0x010A;
    public static final int IFD_010D_DocumentName = 0x010D;
    public static final int IFD_010E_ImageDescription = 0x010E;
    public static final int IFD_010F_Make = 0x010F;
    public static final int IFD_0110_Model = 0x0110;
    public static final int IFD_0111_StripOffsets = 0x0111;                     // TIFF-v6: Page 19, 40
    public static final int IFD_0112_Orientation = 0x0112;
    public static final int IFD_0115_SamplesPerPixel = 0x0115;                  // TIFF-v6: Page 24, 39
    public static final int IFD_0116_RowsPerStrip = 0x0116;                     // TIFF-v6: Page 19, 39
    public static final int IFD_0117_StripByteCounts = 0x0117;                  // TIFF-v6: Page 19, 40
    public static final int IFD_0118_MinSampleValue = 0x0118;
    public static final int IFD_0119_MaxSampleValue = 0x0119;
    public static final int IFD_011A_XResolution = 0x011A;                      // TIFF-v6: Page 19, 41
    public static final int IFD_011B_YResolution = 0x011B;                      // TIFF-v6: Page 19, 41
    public static final int IFD_011C_PlanarConfiguration = 0x011C;
    public static final int IFD_011D_PageName = 0x011D;
    public static final int IFD_011E_XPosition = 0x011E;
    public static final int IFD_011F_YPosition = 0x011F;
    public static final int IFD_0120_FreeOffsets = 0x0120;
    public static final int IFD_0121_FreeByteCounts = 0x0121;
    public static final int IFD_0122_GrayResponseUnit = 0x0122;
    public static final int IFD_0123_GrayResponseCurve = 0x0123;
    public static final int IFD_0124_T4Options = 0x0124;
    public static final int IFD_0125_T6Options = 0x0125;
    public static final int IFD_0128_ResolutionUnit = 0x0128;                   // TIFF-v6: Page 18, 38
    public static final int IFD_0129_PageNumber = 0x0129;
    public static final int IFD_012D_TransferFunction = 0x012D;
    public static final int IFD_0131_Software = 0x0131;
    public static final int IFD_0132_DateTime = 0x0132;
    public static final int IFD_013B_Artist = 0x013B;
    public static final int IFD_013C_HostComputer = 0x013C;
    public static final int IFD_013D_Predictor = 0x013D;
    public static final int IFD_013E_WhitePoint = 0x013E;
    public static final int IFD_013F_PrimaryChromaticities = 0x013F;
    public static final int IFD_0140_ColorMap = 0x0140;                         // TIFF-v6: Page 23, 29
    public static final int IFD_0141_HalftoneHints = 0x0141;
    public static final int IFD_0142_TileWidth = 0x0142;
    public static final int IFD_0143_TileLength = 0x0143;
    public static final int IFD_0144_TileOffsets = 0x0144;
    public static final int IFD_0145_TileByteCounts = 0x0145;
    public static final int IFD_014C_InkSet = 0x014C;
    public static final int IFD_014D_InkNames = 0x014D;
    public static final int IFD_014E_NumberOfInks = 0x014E;
    public static final int IFD_0150_DotRange = 0x0150;
    public static final int IFD_0151_TargetPrinter = 0x0151;
    public static final int IFD_0152_ExtraSamples = 0x0152;                     // TIFF-v6: Page 31, 77
    public static final int IFD_0153_SampleFormat = 0x0153;
    public static final int IFD_0154_SMinSampleValue = 0x0154;
    public static final int IFD_0155_SMaxSampleValue = 0x0155;
    public static final int IFD_0156_TransferRange = 0x0156;
    public static final int IFD_0200_JPEGProc = 0x0200;
    public static final int IFD_0201_JPEGInterchangeFormat = 0x0201;
    public static final int IFD_0202_JPEGInterchangeFormatLength = 0x0202;
    public static final int IFD_0203_JPEGRestartInterval = 0x0203;
    public static final int IFD_0205_JPEGLosslessPredictors = 0x0205;
    public static final int IFD_0206_JPEGPointTransforms = 0x0206;
    public static final int IFD_0207_JPEGQTables = 0x0207;
    public static final int IFD_0208_JPEGDCTables = 0x0208;
    public static final int IFD_0209_JPEGACTables = 0x0209;
    public static final int IFD_0211_YCbCrCoefficients = 0x0211;
    public static final int IFD_0212_YCbCrSubSampling = 0x0212;
    public static final int IFD_0213_YCbCrPositioning = 0x0213;
    public static final int IFD_0214_ReferenceBlackWhite = 0x0214;
    public static final int IFD_8298_Copyright = 0x8298;

    ////////////////////////////////////////////////////////////////////////////
    // CIPA DC-008-2010

    public static final int IFD_829A_ExposureTime = 0x829A;
    public static final int IFD_829D_FNumber = 0x829D;
    public static final int IFD_8769_Exif = 0x8769;
    public static final int IFD_8822_ExposureProgram = 0x8822;
    public static final int IFD_8824_SpectralSensitivity = 0x8824;
    public static final int IFD_8825_GPS = 0x8825;                              // GPS Info IFD Pointer
    public static final int IFD_8827_PhotographicSensitivity = 0x8827;
    public static final int IFD_8828_OECF = 0x8828;
    public static final int IFD_8830_SensitivityType = 0x8830;
    public static final int IFD_8831_StandardOutputSensitivity = 0x8831;
    public static final int IFD_8832_RecommendedExposureIndex = 0x8832;
    public static final int IFD_8833_ISOSpeed = 0x8833;
    public static final int IFD_8834_ISOSpeedLatitudeyyy = 0x8834;
    public static final int IFD_8835_ISOSpeedLatitudezzz = 0x8835;
    public static final int IFD_9000_ExifVersion = 0x9000;
    public static final int IFD_9003_DateTimeOriginal = 0x9003;
    public static final int IFD_9004_DateTimeDigitized = 0x9004;
    public static final int IFD_9101_ComponentsConfiguration = 0x9101;
    public static final int IFD_9102_CompressedBitsPerPixel = 0x9102;
    public static final int IFD_9201_ShutterSpeedValue = 0x9201;
    public static final int IFD_9202_ApertureValue = 0x9202;
    public static final int IFD_9203_BrightnessValue = 0x9203;
    public static final int IFD_9204_ExposureBiasValue = 0x9204;
    public static final int IFD_9205_MaxApertureValue = 0x9205;
    public static final int IFD_9206_SubjectDistance = 0x9206;
    public static final int IFD_9207_MeteringMode = 0x9207;
    public static final int IFD_9208_LightSource = 0x9208;
    public static final int IFD_9209_Flash = 0x9209;
    public static final int IFD_920A_FocalLength = 0x920A;
    public static final int IFD_9214_SubjectArea = 0x9214;
    public static final int IFD_927C_MakerNode = 0x927C;
    public static final int IFD_9286_UserComment = 0x9286;
    public static final int IFD_9290_SubsecTime = 0x9290;
    public static final int IFD_9291_SubsecTimeOriginal = 0x9291;
    public static final int IFD_9292_SubsecTimeDigitized = 0x9292;
    public static final int IFD_A000_FlashpixVersion = 0xA000;
    public static final int IFD_A001_ColorSpace = 0xA001;
    public static final int IFD_A002_PixelXDimension = 0xA002;
    public static final int IFD_A003_PixelYDimension = 0xA003;
    public static final int IFD_A004_RelatedSoundFile = 0xA004;
    public static final int IFD_A005_Interoperability = 0xA005;                 // Interoperability IFD Pointer
    public static final int IFD_A20B_FlashEnergy = 0xA20B;
    public static final int IFD_A20C_SpatialFrequencyResponse = 0xA20C;
    public static final int IFD_A20E_FocalPlaneXResolution = 0xA20E;
    public static final int IFD_A20F_FocalPlaneYResolution = 0xA20F;
    public static final int IFD_A210_FocalPlaneResolutionUnit = 0xA210;
    public static final int IFD_A214_SubjectLocation = 0xA214;
    public static final int IFD_A215_ExposureIndex = 0xA215;
    public static final int IFD_A217_SensingMethod = 0xA217;
    public static final int IFD_A300_FileSource = 0xA300;
    public static final int IFD_A301_SceneType = 0xA301;
    public static final int IFD_A302_CFAPattern = 0xA302;
    public static final int IFD_A401_CustomRendered = 0xA401;
    public static final int IFD_A402_ExposureMode = 0xA402;
    public static final int IFD_A403_WhiteBalance = 0xA403;
    public static final int IFD_A404_DigitalZoomRatio = 0xA404;
    public static final int IFD_A405_FocalLengthIn35mmFilm = 0xA405;
    public static final int IFD_A406_SceneCaptureType = 0xA406;
    public static final int IFD_A407_GainControl = 0xA407;
    public static final int IFD_A408_Contrast = 0xA408;
    public static final int IFD_A409_Saturation = 0xA409;
    public static final int IFD_A40A_Sharpness = 0xA40A;
    public static final int IFD_A40B_DeviceSettingDescription = 0xA40B;
    public static final int IFD_A40C_SubjectDistanceRange = 0xA40C;
    public static final int IFD_A420_ImageUniqueID = 0xA420;
    public static final int IFD_A430_CameraOwnerName = 0xA430;
    public static final int IFD_A431_BodySerialNumber = 0xA431;
    public static final int IFD_A432_LensSpecification = 0xA432;
    public static final int IFD_A433_LensMake = 0xA433;
    public static final int IFD_A434_LensModel = 0xA434;
    public static final int IFD_A435_LensSerialNumber = 0xA435;
    public static final int IFD_A500_Gamma = 0xA500;

    // GPS Private IFD

    public static final int IFD_0000_GPSVersionID = 0x0000;
    public static final int IFD_0001_GPSLatitudeRef = 0x0001;
    public static final int IFD_0002_GPSLatitude = 0x0002;
    public static final int IFD_0003_GPSLongitudeRef = 0x0003;
    public static final int IFD_0004_GPSLongitude = 0x0004;
    public static final int IFD_0005_GPSAltitudeRef = 0x0005;
    public static final int IFD_0006_GPSAltitude = 0x0006;
    public static final int IFD_0007_GPSTimeStamp = 0x0007;
    public static final int IFD_0008_GPSSatellites = 0x0008;
    public static final int IFD_0009_GPSStatus = 0x0009;
    public static final int IFD_000A_GPSMeasureMode = 0x000A;
    public static final int IFD_000B_GPSDOP = 0x000B;
    public static final int IFD_000C_GPSSpeedRef = 0x000C;
    public static final int IFD_000D_GPSSpeed = 0x000D;
    public static final int IFD_000E_GPSTrackRef = 0x000E;
    public static final int IFD_000F_GPSTrack = 0x000F;
    public static final int IFD_0010_GPSImgDirectionRef = 0x0010;
    public static final int IFD_0011_GPSImgDirection = 0x0011;
    public static final int IFD_0012_GPSMapDatum = 0x0012;
    public static final int IFD_0013_GPSDestLatitudeRef = 0x0013;
    public static final int IFD_0014_GPSDestLatitude = 0x0014;
    public static final int IFD_0015_GPSDestLongitudeRef = 0x0015;
    public static final int IFD_0016_GPSDestLongitude = 0x0016;
    public static final int IFD_0017_GPSDestBearingRef = 0x0017;
    public static final int IFD_0018_GPSDestBearing = 0x0018;
    public static final int IFD_0019_GPSDestDistanceRef = 0x0019;
    public static final int IFD_001A_GPSDestDistance = 0x001A;
    public static final int IFD_001B_GPSProcessingMethod = 0x001B;
    public static final int IFD_001C_GPSAreaInformation = 0x001C;
    public static final int IFD_001D_GPSDateStamp = 0x001D;
    public static final int IFD_001E_GPSDifferential = 0x001E;
    public static final int IFD_001F_GPSHPositioiningError = 0x001F;

    // Interoperability Private IFD

    public static final int IFD_0001_InteroperabilityIndex = 0x0001;
    public static final int IFD_0002_InteroperabilityVersion = 0x0002;
    private static final ResourceBundle IFDTagMessage = ResourceBundle.getBundle(IFDTag.class.getName().replace('.', '/'));

    public static String getTagName(int tag){
        final String name;
        switch(tag){

            ////////////////////////////////////////////////////////////////////
            // ITFF-v6

            case IFDTag.IFD_00FE_NewSubfileType:
                name = IFDTagMessage.getString("IFD_00FE_NewSubfileType");
                break;

            case IFDTag.IFD_00FF_SubfileType:
                name = IFDTagMessage.getString("IFD_00FF_SubfileType");
                break;

            case IFDTag.IFD_0100_ImageWidth:
                name = IFDTagMessage.getString("IFD_0100_ImageWidth");
                break;

            case IFDTag.IFD_0101_ImageLength:
                name = IFDTagMessage.getString("IFD_0101_ImageLength");
                break;

            case IFDTag.IFD_0102_BitsPerSample:
                name = IFDTagMessage.getString("IFD_0102_BitsPerSample");
                break;

            case IFDTag.IFD_0103_Compression:
                name = IFDTagMessage.getString("IFD_0103_Compression");
                break;

//          case IFDTag.IFD_0106_Color:
            case IFDTag.IFD_0106_PhotometricInterpretation:
                name = IFDTagMessage.getString("IFD_0106_PhotometricInterpretation");
                break;

            case IFDTag.IFD_0107_Threshholding:
                name = IFDTagMessage.getString("IFD_0107_Threshholding");
                break;

            case IFDTag.IFD_0108_CellWidth:
                name = IFDTagMessage.getString("IFD_0108_CellWidth");
                break;

            case IFDTag.IFD_0109_CellLength:
                name = IFDTagMessage.getString("IFD_0109_CellLength");
                break;

            case IFDTag.IFD_010A_FillOrder:
                name = IFDTagMessage.getString("IFD_010A_FillOrder");
                break;

            case IFDTag.IFD_010D_DocumentName:
                name = IFDTagMessage.getString("IFD_010D_DocumentName");
                break;

            case IFDTag.IFD_010E_ImageDescription:
                name = IFDTagMessage.getString("IFD_010E_ImageDescription");
                break;

            case IFDTag.IFD_010F_Make:
                name = IFDTagMessage.getString("IFD_010F_Make");
                break;

            case IFDTag.IFD_0110_Model:
                name = IFDTagMessage.getString("IFD_0110_Model");
                break;

            case IFDTag.IFD_0111_StripOffsets:
                name = IFDTagMessage.getString("IFD_0111_StripOffsets");
                break;

            case IFDTag.IFD_0112_Orientation:
                name = IFDTagMessage.getString("IFD_0112_Orientation");
                break;

            case IFDTag.IFD_0115_SamplesPerPixel:
                name = IFDTagMessage.getString("IFD_0115_SamplesPerPixel");
                break;

            case IFDTag.IFD_0116_RowsPerStrip:
                name = IFDTagMessage.getString("IFD_0116_RowsPerStrip");
                break;

            case IFDTag.IFD_0117_StripByteCounts:
                name = IFDTagMessage.getString("IFD_0117_StripByteCounts");
                break;

            case IFDTag.IFD_0118_MinSampleValue:
                name = IFDTagMessage.getString("IFD_0118_MinSampleValue");
                break;

            case IFDTag.IFD_0119_MaxSampleValue:
                name = IFDTagMessage.getString("IFD_0119_MaxSampleValue");
                break;

            case IFDTag.IFD_011A_XResolution:
                name = IFDTagMessage.getString("IFD_011A_XResolution");
                break;

            case IFDTag.IFD_011B_YResolution:
                name = IFDTagMessage.getString("IFD_011B_YResolution");
                break;

            case IFDTag.IFD_011C_PlanarConfiguration:
                name = IFDTagMessage.getString("IFD_011C_PlanarConfiguration");
                break;

            case IFDTag.IFD_011D_PageName:
                name = IFDTagMessage.getString("IFD_011D_PageName");
                break;

            case IFDTag.IFD_011E_XPosition:
                name = IFDTagMessage.getString("IFD_011E_XPosition");
                break;

            case IFDTag.IFD_011F_YPosition:
                name = IFDTagMessage.getString("IFD_011F_YPosition");
                break;

            case IFDTag.IFD_0120_FreeOffsets:
                name = IFDTagMessage.getString("IFD_0120_FreeOffsets");
                break;

            case IFDTag.IFD_0121_FreeByteCounts:
                name = IFDTagMessage.getString("IFD_0121_FreeByteCounts");
                break;

            case IFDTag.IFD_0122_GrayResponseUnit:
                name = IFDTagMessage.getString("IFD_0122_GrayResponseUnit");
                break;

            case IFDTag.IFD_0123_GrayResponseCurve:
                name = IFDTagMessage.getString("IFD_0123_GrayResponseCurve");
                break;

            case IFDTag.IFD_0124_T4Options:
                name = IFDTagMessage.getString("IFD_0124_T4Options");
                break;

            case IFDTag.IFD_0125_T6Options:
                name = IFDTagMessage.getString("IFD_0125_T6Options");
                break;

            case IFDTag.IFD_0128_ResolutionUnit:
                name = IFDTagMessage.getString("IFD_0128_ResolutionUnit");
                break;

            case IFDTag.IFD_0129_PageNumber:
                name = IFDTagMessage.getString("IFD_0129_PageNumber");
                break;

            case IFDTag.IFD_012D_TransferFunction:
                name = IFDTagMessage.getString("IFD_012D_TransferFunction");
                break;

            case IFDTag.IFD_0131_Software:
                name = IFDTagMessage.getString("IFD_0131_Software");
                break;

            case IFDTag.IFD_0132_DateTime:
                name = IFDTagMessage.getString("IFD_0132_DateTime");
                break;

            case IFDTag.IFD_013B_Artist:
                name = IFDTagMessage.getString("IFD_013B_Artist");
                break;

            case IFDTag.IFD_013C_HostComputer:
                name = IFDTagMessage.getString("IFD_013C_HostComputer");
                break;

            case IFDTag.IFD_013D_Predictor:
                name = IFDTagMessage.getString("IFD_013D_Predictor");
                break;

            case IFDTag.IFD_013E_WhitePoint:
                name = IFDTagMessage.getString("IFD_013E_WhitePoint");
                break;

            case IFDTag.IFD_013F_PrimaryChromaticities:
                name = IFDTagMessage.getString("IFD_013F_PrimaryChromaticities");
                break;

            case IFDTag.IFD_0140_ColorMap:
                name = IFDTagMessage.getString("IFD_0140_ColorMap");
                break;

            case IFDTag.IFD_0141_HalftoneHints:
                name = IFDTagMessage.getString("IFD_0141_HalftoneHints");
                break;

            case IFDTag.IFD_0142_TileWidth:
                name = IFDTagMessage.getString("IFD_0142_TileWidth");
                break;

            case IFDTag.IFD_0143_TileLength:
                name = IFDTagMessage.getString("IFD_0143_TileLength");
                break;

            case IFDTag.IFD_0144_TileOffsets:
                name = IFDTagMessage.getString("IFD_0144_TileOffsets");
                break;

            case IFDTag.IFD_0145_TileByteCounts:
                name = IFDTagMessage.getString("IFD_0145_TileByteCounts");
                break;

            case IFDTag.IFD_014C_InkSet:
                name = IFDTagMessage.getString("IFD_014C_InkSet");
                break;

            case IFDTag.IFD_014D_InkNames:
                name = IFDTagMessage.getString("IFD_014D_InkNames");
                break;

            case IFDTag.IFD_014E_NumberOfInks:
                name = IFDTagMessage.getString("IFD_014E_NumberOfInks");
                break;

            case IFDTag.IFD_0150_DotRange:
                name = IFDTagMessage.getString("IFD_0150_DotRange");
                break;

            case IFDTag.IFD_0151_TargetPrinter:
                name = IFDTagMessage.getString("IFD_0151_TargetPrinter");
                break;

            case IFDTag.IFD_0152_ExtraSamples:
                name = IFDTagMessage.getString("IFD_0152_ExtraSamples");
                break;

            case IFDTag.IFD_0153_SampleFormat:
                name = IFDTagMessage.getString("IFD_0153_SampleFormat");
                break;

            case IFDTag.IFD_0154_SMinSampleValue:
                name = IFDTagMessage.getString("IFD_0154_SMinSampleValue");
                break;

            case IFDTag.IFD_0155_SMaxSampleValue:
                name = IFDTagMessage.getString("IFD_0155_SMaxSampleValue");
                break;

            case IFDTag.IFD_0156_TransferRange:
                name = IFDTagMessage.getString("IFD_0156_TransferRange");
                break;

            case IFDTag.IFD_0200_JPEGProc:
                name = IFDTagMessage.getString("IFD_0200_JPEGProc");
                break;

            case IFDTag.IFD_0201_JPEGInterchangeFormat:
                name = IFDTagMessage.getString("IFD_0201_JPEGInterchangeFormat");
                break;

            case IFDTag.IFD_0202_JPEGInterchangeFormatLength:
                name = IFDTagMessage.getString("IFD_0202_JPEGInterchangeFormatLength");
                break;

            case IFDTag.IFD_0203_JPEGRestartInterval:
                name = IFDTagMessage.getString("IFD_0203_JPEGRestartInterval");
                break;

            case IFDTag.IFD_0205_JPEGLosslessPredictors:
                name = IFDTagMessage.getString("IFD_0205_JPEGLosslessPredictors");
                break;

            case IFDTag.IFD_0206_JPEGPointTransforms:
                name = IFDTagMessage.getString("IFD_0206_JPEGPointTransforms");
                break;

            case IFDTag.IFD_0207_JPEGQTables:
                name = IFDTagMessage.getString("IFD_0207_JPEGQTables");
                break;

            case IFDTag.IFD_0208_JPEGDCTables:
                name = IFDTagMessage.getString("IFD_0208_JPEGDCTables");
                break;

            case IFDTag.IFD_0209_JPEGACTables:
                name = IFDTagMessage.getString("IFD_0209_JPEGACTables");
                break;

            case IFDTag.IFD_0211_YCbCrCoefficients:
                name = IFDTagMessage.getString("IFD_0211_YCbCrCoefficients");
                break;

            case IFDTag.IFD_0212_YCbCrSubSampling:
                name = IFDTagMessage.getString("IFD_0212_YCbCrSubSampling");
                break;

            case IFDTag.IFD_0213_YCbCrPositioning:
                name = IFDTagMessage.getString("IFD_0213_YCbCrPositioning");
                break;

            case IFDTag.IFD_0214_ReferenceBlackWhite:
                name = IFDTagMessage.getString("IFD_0214_ReferenceBlackWhite");
                break;

            case IFDTag.IFD_8298_Copyright:
                name = IFDTagMessage.getString("IFD_8298_Copyright");
                break;

            ////////////////////////////////////////////////////////////////////
            // CIPA DC-008-2010

            case IFDTag.IFD_829A_ExposureTime:
                name = IFDTagMessage.getString("IFD_829A_ExposureTime");
                break;

            case IFDTag.IFD_829D_FNumber:
                name = IFDTagMessage.getString("IFD_829D_FNumber");
                break;

            case IFDTag.IFD_8769_Exif:
                name = IFDTagMessage.getString("IFD_8769_Exif");
                break;

            case IFDTag.IFD_8822_ExposureProgram:
                name = IFDTagMessage.getString("IFD_8822_ExposureProgram");
                break;

            case IFDTag.IFD_8824_SpectralSensitivity:
                name = IFDTagMessage.getString("IFD_8824_SpectralSensitivity");
                break;

            case IFDTag.IFD_8825_GPS:
                name = IFDTagMessage.getString("IFD_8825_GPS");
                break;

            case IFDTag.IFD_8827_PhotographicSensitivity:
                name = IFDTagMessage.getString("IFD_8827_PhotographicSensitivity");
                break;

            case IFDTag.IFD_8828_OECF:
                name = IFDTagMessage.getString("IFD_8828_OECF");
                break;

            case IFDTag.IFD_8830_SensitivityType:
                name = IFDTagMessage.getString("IFD_8830_SensitivityType");
                break;

            case IFDTag.IFD_8831_StandardOutputSensitivity:
                name = IFDTagMessage.getString("IFD_8831_StandardOutputSensitivity");
                break;

            case IFDTag.IFD_8832_RecommendedExposureIndex:
                name = IFDTagMessage.getString("IFD_8832_RecommendedExposureIndex");
                break;

            case IFDTag.IFD_8833_ISOSpeed:
                name = IFDTagMessage.getString("IFD_8833_ISOSpeed");
                break;

            case IFDTag.IFD_8834_ISOSpeedLatitudeyyy:
                name = IFDTagMessage.getString("IFD_8834_ISOSpeedLatitudeyyy");
                break;

            case IFDTag.IFD_8835_ISOSpeedLatitudezzz:
                name = IFDTagMessage.getString("IFD_8835_ISOSpeedLatitudezzz");
                break;

            case IFDTag.IFD_9000_ExifVersion:
                name = IFDTagMessage.getString("IFD_9000_ExifVersion");
                break;

            case IFDTag.IFD_9003_DateTimeOriginal:
                name = IFDTagMessage.getString("IFD_9003_DateTimeOriginal");
                break;

            case IFDTag.IFD_9004_DateTimeDigitized:
                name = IFDTagMessage.getString("IFD_9004_DateTimeDigitized");
                break;

            case IFDTag.IFD_9101_ComponentsConfiguration:
                name = IFDTagMessage.getString("IFD_9101_ComponentsConfiguration");
                break;

            case IFDTag.IFD_9102_CompressedBitsPerPixel:
                name = IFDTagMessage.getString("IFD_9102_CompressedBitsPerPixel");
                break;

            case IFDTag.IFD_9201_ShutterSpeedValue:
                name = IFDTagMessage.getString("IFD_9201_ShutterSpeedValue");
                break;

            case IFDTag.IFD_9202_ApertureValue:
                name = IFDTagMessage.getString("IFD_9202_ApertureValue");
                break;

            case IFDTag.IFD_9203_BrightnessValue:
                name = IFDTagMessage.getString("IFD_9203_BrightnessValue");
                break;

            case IFDTag.IFD_9204_ExposureBiasValue:
                name = IFDTagMessage.getString("IFD_9204_ExposureBiasValue");
                break;

            case IFDTag.IFD_9205_MaxApertureValue:
                name = IFDTagMessage.getString("IFD_9205_MaxApertureValue");
                break;

            case IFDTag.IFD_9206_SubjectDistance:
                name = IFDTagMessage.getString("IFD_9206_SubjectDistance");
                break;

            case IFDTag.IFD_9207_MeteringMode:
                name = IFDTagMessage.getString("IFD_9207_MeteringMode");
                break;

            case IFDTag.IFD_9208_LightSource:
                name = IFDTagMessage.getString("IFD_9208_LightSource");
                break;

            case IFDTag.IFD_9209_Flash:
                name = IFDTagMessage.getString("IFD_9209_Flash");
                break;

            case IFDTag.IFD_920A_FocalLength:
                name = IFDTagMessage.getString("IFD_920A_FocalLength");
                break;

            case IFDTag.IFD_9214_SubjectArea:
                name = IFDTagMessage.getString("IFD_9214_SubjectArea");
                break;

            case IFDTag.IFD_927C_MakerNode:
                name = IFDTagMessage.getString("IFD_927C_MakerNode");
                break;

            case IFDTag.IFD_9286_UserComment:
                name = IFDTagMessage.getString("IFD_9286_UserComment");
                break;

            case IFDTag.IFD_9290_SubsecTime:
                name = IFDTagMessage.getString("IFD_9290_SubsecTime");
                break;

            case IFDTag.IFD_9291_SubsecTimeOriginal:
                name = IFDTagMessage.getString("IFD_9291_SubsecTimeOriginal");
                break;

            case IFDTag.IFD_9292_SubsecTimeDigitized:
                name = IFDTagMessage.getString("IFD_9292_SubsecTimeDigitized");
                break;

            case IFDTag.IFD_A000_FlashpixVersion:
                name = IFDTagMessage.getString("IFD_A000_FlashpixVersion");
                break;

            case IFDTag.IFD_A001_ColorSpace:
                name = IFDTagMessage.getString("IFD_A001_ColorSpace");
                break;

            case IFDTag.IFD_A002_PixelXDimension:
                name = IFDTagMessage.getString("IFD_A002_PixelXDimension");
                break;

            case IFDTag.IFD_A003_PixelYDimension:
                name = IFDTagMessage.getString("IFD_A003_PixelYDimension");
                break;

            case IFDTag.IFD_A004_RelatedSoundFile:
                name = IFDTagMessage.getString("IFD_A004_RelatedSoundFile");
                break;

            case IFDTag.IFD_A005_Interoperability:
                name = IFDTagMessage.getString("IFD_A005_Interoperability");
                break;

            case IFDTag.IFD_A20B_FlashEnergy:
                name = IFDTagMessage.getString("IFD_A20B_FlashEnergy");
                break;

            case IFDTag.IFD_A20C_SpatialFrequencyResponse:
                name = IFDTagMessage.getString("IFD_A20C_SpatialFrequencyResponse");
                break;

            case IFDTag.IFD_A20E_FocalPlaneXResolution:
                name = IFDTagMessage.getString("IFD_A20E_FocalPlaneXResolution");
                break;

            case IFDTag.IFD_A20F_FocalPlaneYResolution:
                name = IFDTagMessage.getString("IFD_A20F_FocalPlaneYResolution");
                break;

            case IFDTag.IFD_A210_FocalPlaneResolutionUnit:
                name = IFDTagMessage.getString("IFD_A210_FocalPlaneResolutionUnit");
                break;

            case IFDTag.IFD_A214_SubjectLocation:
                name = IFDTagMessage.getString("IFD_A214_SubjectLocation");
                break;

            case IFDTag.IFD_A215_ExposureIndex:
                name = IFDTagMessage.getString("IFD_A215_ExposureIndex");
                break;

            case IFDTag.IFD_A217_SensingMethod:
                name = IFDTagMessage.getString("IFD_A217_SensingMethod");
                break;

            case IFDTag.IFD_A300_FileSource:
                name = IFDTagMessage.getString("IFD_A300_FileSource");
                break;

            case IFDTag.IFD_A301_SceneType:
                name = IFDTagMessage.getString("IFD_A301_SceneType");
                break;

            case IFDTag.IFD_A302_CFAPattern:
                name = IFDTagMessage.getString("IFD_A302_CFAPattern");
                break;

            case IFDTag.IFD_A401_CustomRendered:
                name = IFDTagMessage.getString("IFD_A401_CustomRendered");
                break;

            case IFDTag.IFD_A402_ExposureMode:
                name = IFDTagMessage.getString("IFD_A402_ExposureMode");
                break;

            case IFDTag.IFD_A403_WhiteBalance:
                name = IFDTagMessage.getString("IFD_A403_WhiteBalance");
                break;

            case IFDTag.IFD_A404_DigitalZoomRatio:
                name = IFDTagMessage.getString("IFD_A404_DigitalZoomRatio");
                break;

            case IFDTag.IFD_A405_FocalLengthIn35mmFilm:
                name = IFDTagMessage.getString("IFD_A405_FocalLengthIn35mmFilm");
                break;

            case IFDTag.IFD_A406_SceneCaptureType:
                name = IFDTagMessage.getString("IFD_A406_SceneCaptureType");
                break;

            case IFDTag.IFD_A407_GainControl:
                name = IFDTagMessage.getString("IFD_A407_GainControl");
                break;

            case IFDTag.IFD_A408_Contrast:
                name = IFDTagMessage.getString("IFD_A408_Contrast");
                break;

            case IFDTag.IFD_A409_Saturation:
                name = IFDTagMessage.getString("IFD_A409_Saturation");
                break;

            case IFDTag.IFD_A40A_Sharpness:
                name = IFDTagMessage.getString("IFD_A40A_Sharpness");
                break;

            case IFDTag.IFD_A40B_DeviceSettingDescription:
                name = IFDTagMessage.getString("IFD_A40B_DeviceSettingDescription");
                break;

            case IFDTag.IFD_A40C_SubjectDistanceRange:
                name = IFDTagMessage.getString("IFD_A40C_SubjectDistanceRange");
                break;

            case IFDTag.IFD_A420_ImageUniqueID:
                name = IFDTagMessage.getString("IFD_A420_ImageUniqueID");
                break;

            case IFDTag.IFD_A430_CameraOwnerName:
                name = IFDTagMessage.getString("IFD_A430_CameraOwnerName");
                break;

            case IFDTag.IFD_A431_BodySerialNumber:
                name = IFDTagMessage.getString("IFD_A431_BodySerialNumber");
                break;

            case IFDTag.IFD_A432_LensSpecification:
                name = IFDTagMessage.getString("IFD_A432_LensSpecification");
                break;

            case IFDTag.IFD_A433_LensMake:
                name = IFDTagMessage.getString("IFD_A433_LensMake");
                break;

            case IFDTag.IFD_A434_LensModel:
                name = IFDTagMessage.getString("IFD_A434_LensModel");
                break;

            case IFDTag.IFD_A435_LensSerialNumber:
                name = IFDTagMessage.getString("IFD_A435_LensSerialNumber");
                break;

            case IFDTag.IFD_A500_Gamma:
                name = IFDTagMessage.getString("IFD_A500_Gamma");
                break;

            default:
                name = IFDTagMessage.getString("UNKNOWN_TAG");
        }

        return name;
    }

    
    public static String getTagNameGPS(int tag){
        final String name;
        switch(tag){
            case IFDTag.IFD_0000_GPSVersionID:
                name = IFDTagMessage.getString("IFD_0000_GPSVersionID");
                break;
            case IFDTag.IFD_0001_GPSLatitudeRef:
                name = IFDTagMessage.getString("IFD_0001_GPSLatitudeRef");
                break;
            case IFDTag.IFD_0002_GPSLatitude:
                name = IFDTagMessage.getString("IFD_0002_GPSLatitude");
                break;
            case IFDTag.IFD_0003_GPSLongitudeRef:
                name = IFDTagMessage.getString("IFD_0003_GPSLongitudeRef");
                break;
            case IFDTag.IFD_0004_GPSLongitude:
                name = IFDTagMessage.getString("IFD_0004_GPSLongitude");
                break;
            case IFDTag.IFD_0005_GPSAltitudeRef:
                name = IFDTagMessage.getString("IFD_0005_GPSAltitudeRef");
                break;
            case IFDTag.IFD_0006_GPSAltitude:
                name = IFDTagMessage.getString("IFD_0006_GPSAltitude");
                break;
            case IFDTag.IFD_0007_GPSTimeStamp:
                name = IFDTagMessage.getString("IFD_0007_GPSTimeStamp");
                break;
            case IFDTag.IFD_0008_GPSSatellites:
                name = IFDTagMessage.getString("IFD_0008_GPSSatellites");
                break;
            case IFDTag.IFD_0009_GPSStatus:
                name = IFDTagMessage.getString("IFD_0009_GPSStatus");
                break;
            case IFDTag.IFD_000A_GPSMeasureMode:
                name = IFDTagMessage.getString("IFD_000A_GPSMeasureMode");
                break;
            case IFDTag.IFD_000B_GPSDOP:
                name = IFDTagMessage.getString("IFD_000B_GPSDOP");
                break;
            case IFDTag.IFD_000C_GPSSpeedRef:
                name = IFDTagMessage.getString("IFD_000C_GPSSpeedRef");
                break;
            case IFDTag.IFD_000D_GPSSpeed:
                name = IFDTagMessage.getString("IFD_000D_GPSSpeed");
                break;
            case IFDTag.IFD_000E_GPSTrackRef:
                name = IFDTagMessage.getString("IFD_000E_GPSTrackRef");
                break;
            case IFDTag.IFD_000F_GPSTrack:
                name = IFDTagMessage.getString("IFD_000F_GPSTrack");
                break;
            case IFDTag.IFD_0010_GPSImgDirectionRef:
                name = IFDTagMessage.getString("IFD_0010_GPSImgDirectionRef");
                break;
            case IFDTag.IFD_0011_GPSImgDirection:
                name = IFDTagMessage.getString("IFD_0011_GPSImgDirection");
                break;
            case IFDTag.IFD_0012_GPSMapDatum:
                name = IFDTagMessage.getString("IFD_0012_GPSMapDatum");
                break;
            case IFDTag.IFD_0013_GPSDestLatitudeRef:
                name = IFDTagMessage.getString("IFD_0013_GPSDestLatitudeRef");
                break;
            case IFDTag.IFD_0014_GPSDestLatitude:
                name = IFDTagMessage.getString("IFD_0014_GPSDestLatitude");
                break;
            case IFDTag.IFD_0015_GPSDestLongitudeRef:
                name = IFDTagMessage.getString("IFD_0015_GPSDestLongitudeRef");
                break;
            case IFDTag.IFD_0016_GPSDestLongitude:
                name = IFDTagMessage.getString("IFD_0016_GPSDestLongitude");
                break;
            case IFDTag.IFD_0017_GPSDestBearingRef:
                name = IFDTagMessage.getString("IFD_0017_GPSDestBearingRef");
                break;
            case IFDTag.IFD_0018_GPSDestBearing:
                name = IFDTagMessage.getString("IFD_0018_GPSDestBearing");
                break;
            case IFDTag.IFD_0019_GPSDestDistanceRef:
                name = IFDTagMessage.getString("IFD_0019_GPSDestDistanceRef");
                break;
            case IFDTag.IFD_001A_GPSDestDistance:
                name = IFDTagMessage.getString("IFD_001A_GPSDestDistance");
                break;
            case IFDTag.IFD_001B_GPSProcessingMethod:
                name = IFDTagMessage.getString("IFD_001B_GPSProcessingMethod");
                break;
            case IFDTag.IFD_001C_GPSAreaInformation:
                name = IFDTagMessage.getString("IFD_001C_GPSAreaInformation");
                break;
            case IFDTag.IFD_001D_GPSDateStamp:
                name = IFDTagMessage.getString("IFD_001D_GPSDateStamp");
                break;
            case IFDTag.IFD_001E_GPSDifferential:
                name = IFDTagMessage.getString("IFD_001E_GPSDifferential");
                break;
            case IFDTag.IFD_001F_GPSHPositioiningError:
                name = IFDTagMessage.getString("IFD_001F_GPSHPositioiningError");
                break;
            default:
                name = IFDTagMessage.getString("UNKNOWN_GPS_TAG");
        }

        return name;
    }


    public static String getTagNameIntero(int tag){
        final String name;
        switch(tag){
            case IFDTag.IFD_0001_InteroperabilityIndex:
                name = IFDTagMessage.getString("IFD_0001_InteroperabilityIndex");
                break;
            case IFDTag.IFD_0002_InteroperabilityVersion:
                name = IFDTagMessage.getString("IFD_0002_InteroperabilityVersion");
                break;
            default:
                name = IFDTagMessage.getString("UNKNOWN_TAG");
        }

        return name;
    }


}
