/*
 * IFDTag.java    Sep 06, 2010, 23:41
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.util.ResourceBundle;

/// @author Amos Shi
public class IFDTag {

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

    private IFDTag() {
    }

    public static String getTagName(int tag) {
        return switch (tag) {
            // ITFF-v6
            case IFDTag.IFD_00FE_NewSubfileType -> IFDTagMessage.getString("IFD_00FE_NewSubfileType");
            case IFDTag.IFD_00FF_SubfileType -> IFDTagMessage.getString("IFD_00FF_SubfileType");
            case IFDTag.IFD_0100_ImageWidth -> IFDTagMessage.getString("IFD_0100_ImageWidth");
            case IFDTag.IFD_0101_ImageLength -> IFDTagMessage.getString("IFD_0101_ImageLength");
            case IFDTag.IFD_0102_BitsPerSample -> IFDTagMessage.getString("IFD_0102_BitsPerSample");
            case IFDTag.IFD_0103_Compression -> IFDTagMessage.getString("IFD_0103_Compression");
            //          case IFDTag.IFD_0106_Color:
            case IFDTag.IFD_0106_PhotometricInterpretation -> IFDTagMessage.getString("IFD_0106_PhotometricInterpretation");
            case IFDTag.IFD_0107_Threshholding -> IFDTagMessage.getString("IFD_0107_Threshholding");
            case IFDTag.IFD_0108_CellWidth -> IFDTagMessage.getString("IFD_0108_CellWidth");
            case IFDTag.IFD_0109_CellLength -> IFDTagMessage.getString("IFD_0109_CellLength");
            case IFDTag.IFD_010A_FillOrder -> IFDTagMessage.getString("IFD_010A_FillOrder");
            case IFDTag.IFD_010D_DocumentName -> IFDTagMessage.getString("IFD_010D_DocumentName");
            case IFDTag.IFD_010E_ImageDescription -> IFDTagMessage.getString("IFD_010E_ImageDescription");
            case IFDTag.IFD_010F_Make -> IFDTagMessage.getString("IFD_010F_Make");
            case IFDTag.IFD_0110_Model -> IFDTagMessage.getString("IFD_0110_Model");
            case IFDTag.IFD_0111_StripOffsets -> IFDTagMessage.getString("IFD_0111_StripOffsets");
            case IFDTag.IFD_0112_Orientation -> IFDTagMessage.getString("IFD_0112_Orientation");
            case IFDTag.IFD_0115_SamplesPerPixel -> IFDTagMessage.getString("IFD_0115_SamplesPerPixel");
            case IFDTag.IFD_0116_RowsPerStrip -> IFDTagMessage.getString("IFD_0116_RowsPerStrip");
            case IFDTag.IFD_0117_StripByteCounts -> IFDTagMessage.getString("IFD_0117_StripByteCounts");
            case IFDTag.IFD_0118_MinSampleValue -> IFDTagMessage.getString("IFD_0118_MinSampleValue");
            case IFDTag.IFD_0119_MaxSampleValue -> IFDTagMessage.getString("IFD_0119_MaxSampleValue");
            case IFDTag.IFD_011A_XResolution -> IFDTagMessage.getString("IFD_011A_XResolution");
            case IFDTag.IFD_011B_YResolution -> IFDTagMessage.getString("IFD_011B_YResolution");
            case IFDTag.IFD_011C_PlanarConfiguration -> IFDTagMessage.getString("IFD_011C_PlanarConfiguration");
            case IFDTag.IFD_011D_PageName -> IFDTagMessage.getString("IFD_011D_PageName");
            case IFDTag.IFD_011E_XPosition -> IFDTagMessage.getString("IFD_011E_XPosition");
            case IFDTag.IFD_011F_YPosition -> IFDTagMessage.getString("IFD_011F_YPosition");
            case IFDTag.IFD_0120_FreeOffsets -> IFDTagMessage.getString("IFD_0120_FreeOffsets");
            case IFDTag.IFD_0121_FreeByteCounts -> IFDTagMessage.getString("IFD_0121_FreeByteCounts");
            case IFDTag.IFD_0122_GrayResponseUnit -> IFDTagMessage.getString("IFD_0122_GrayResponseUnit");
            case IFDTag.IFD_0123_GrayResponseCurve -> IFDTagMessage.getString("IFD_0123_GrayResponseCurve");
            case IFDTag.IFD_0124_T4Options -> IFDTagMessage.getString("IFD_0124_T4Options");
            case IFDTag.IFD_0125_T6Options -> IFDTagMessage.getString("IFD_0125_T6Options");
            case IFDTag.IFD_0128_ResolutionUnit -> IFDTagMessage.getString("IFD_0128_ResolutionUnit");
            case IFDTag.IFD_0129_PageNumber -> IFDTagMessage.getString("IFD_0129_PageNumber");
            case IFDTag.IFD_012D_TransferFunction -> IFDTagMessage.getString("IFD_012D_TransferFunction");
            case IFDTag.IFD_0131_Software -> IFDTagMessage.getString("IFD_0131_Software");
            case IFDTag.IFD_0132_DateTime -> IFDTagMessage.getString("IFD_0132_DateTime");
            case IFDTag.IFD_013B_Artist -> IFDTagMessage.getString("IFD_013B_Artist");
            case IFDTag.IFD_013C_HostComputer -> IFDTagMessage.getString("IFD_013C_HostComputer");
            case IFDTag.IFD_013D_Predictor -> IFDTagMessage.getString("IFD_013D_Predictor");
            case IFDTag.IFD_013E_WhitePoint -> IFDTagMessage.getString("IFD_013E_WhitePoint");
            case IFDTag.IFD_013F_PrimaryChromaticities -> IFDTagMessage.getString("IFD_013F_PrimaryChromaticities");
            case IFDTag.IFD_0140_ColorMap -> IFDTagMessage.getString("IFD_0140_ColorMap");
            case IFDTag.IFD_0141_HalftoneHints -> IFDTagMessage.getString("IFD_0141_HalftoneHints");
            case IFDTag.IFD_0142_TileWidth -> IFDTagMessage.getString("IFD_0142_TileWidth");
            case IFDTag.IFD_0143_TileLength -> IFDTagMessage.getString("IFD_0143_TileLength");
            case IFDTag.IFD_0144_TileOffsets -> IFDTagMessage.getString("IFD_0144_TileOffsets");
            case IFDTag.IFD_0145_TileByteCounts -> IFDTagMessage.getString("IFD_0145_TileByteCounts");
            case IFDTag.IFD_014C_InkSet -> IFDTagMessage.getString("IFD_014C_InkSet");
            case IFDTag.IFD_014D_InkNames -> IFDTagMessage.getString("IFD_014D_InkNames");
            case IFDTag.IFD_014E_NumberOfInks -> IFDTagMessage.getString("IFD_014E_NumberOfInks");
            case IFDTag.IFD_0150_DotRange -> IFDTagMessage.getString("IFD_0150_DotRange");
            case IFDTag.IFD_0151_TargetPrinter -> IFDTagMessage.getString("IFD_0151_TargetPrinter");
            case IFDTag.IFD_0152_ExtraSamples -> IFDTagMessage.getString("IFD_0152_ExtraSamples");
            case IFDTag.IFD_0153_SampleFormat -> IFDTagMessage.getString("IFD_0153_SampleFormat");
            case IFDTag.IFD_0154_SMinSampleValue -> IFDTagMessage.getString("IFD_0154_SMinSampleValue");
            case IFDTag.IFD_0155_SMaxSampleValue -> IFDTagMessage.getString("IFD_0155_SMaxSampleValue");
            case IFDTag.IFD_0156_TransferRange -> IFDTagMessage.getString("IFD_0156_TransferRange");
            case IFDTag.IFD_0200_JPEGProc -> IFDTagMessage.getString("IFD_0200_JPEGProc");
            case IFDTag.IFD_0201_JPEGInterchangeFormat -> IFDTagMessage.getString("IFD_0201_JPEGInterchangeFormat");
            case IFDTag.IFD_0202_JPEGInterchangeFormatLength -> IFDTagMessage.getString("IFD_0202_JPEGInterchangeFormatLength");
            case IFDTag.IFD_0203_JPEGRestartInterval -> IFDTagMessage.getString("IFD_0203_JPEGRestartInterval");
            case IFDTag.IFD_0205_JPEGLosslessPredictors -> IFDTagMessage.getString("IFD_0205_JPEGLosslessPredictors");
            case IFDTag.IFD_0206_JPEGPointTransforms -> IFDTagMessage.getString("IFD_0206_JPEGPointTransforms");
            case IFDTag.IFD_0207_JPEGQTables -> IFDTagMessage.getString("IFD_0207_JPEGQTables");
            case IFDTag.IFD_0208_JPEGDCTables -> IFDTagMessage.getString("IFD_0208_JPEGDCTables");
            case IFDTag.IFD_0209_JPEGACTables -> IFDTagMessage.getString("IFD_0209_JPEGACTables");
            case IFDTag.IFD_0211_YCbCrCoefficients -> IFDTagMessage.getString("IFD_0211_YCbCrCoefficients");
            case IFDTag.IFD_0212_YCbCrSubSampling -> IFDTagMessage.getString("IFD_0212_YCbCrSubSampling");
            case IFDTag.IFD_0213_YCbCrPositioning -> IFDTagMessage.getString("IFD_0213_YCbCrPositioning");
            case IFDTag.IFD_0214_ReferenceBlackWhite -> IFDTagMessage.getString("IFD_0214_ReferenceBlackWhite");
            case IFDTag.IFD_8298_Copyright -> IFDTagMessage.getString("IFD_8298_Copyright");
            default -> getTagNameExif(tag);
        };
    }

    /// Name of a tag defined in `CIPA DC-008-2010` (Exif).
    private static String getTagNameExif(final int tag) {
        return switch (tag) {
            case IFDTag.IFD_829A_ExposureTime -> IFDTagMessage.getString("IFD_829A_ExposureTime");
            case IFDTag.IFD_829D_FNumber -> IFDTagMessage.getString("IFD_829D_FNumber");
            case IFDTag.IFD_8769_Exif -> IFDTagMessage.getString("IFD_8769_Exif");
            case IFDTag.IFD_8822_ExposureProgram -> IFDTagMessage.getString("IFD_8822_ExposureProgram");
            case IFDTag.IFD_8824_SpectralSensitivity -> IFDTagMessage.getString("IFD_8824_SpectralSensitivity");
            case IFDTag.IFD_8825_GPS -> IFDTagMessage.getString("IFD_8825_GPS");
            case IFDTag.IFD_8827_PhotographicSensitivity -> IFDTagMessage.getString("IFD_8827_PhotographicSensitivity");
            case IFDTag.IFD_8828_OECF -> IFDTagMessage.getString("IFD_8828_OECF");
            case IFDTag.IFD_8830_SensitivityType -> IFDTagMessage.getString("IFD_8830_SensitivityType");
            case IFDTag.IFD_8831_StandardOutputSensitivity -> IFDTagMessage.getString("IFD_8831_StandardOutputSensitivity");
            case IFDTag.IFD_8832_RecommendedExposureIndex -> IFDTagMessage.getString("IFD_8832_RecommendedExposureIndex");
            case IFDTag.IFD_8833_ISOSpeed -> IFDTagMessage.getString("IFD_8833_ISOSpeed");
            case IFDTag.IFD_8834_ISOSpeedLatitudeyyy -> IFDTagMessage.getString("IFD_8834_ISOSpeedLatitudeyyy");
            case IFDTag.IFD_8835_ISOSpeedLatitudezzz -> IFDTagMessage.getString("IFD_8835_ISOSpeedLatitudezzz");
            case IFDTag.IFD_9000_ExifVersion -> IFDTagMessage.getString("IFD_9000_ExifVersion");
            case IFDTag.IFD_9003_DateTimeOriginal -> IFDTagMessage.getString("IFD_9003_DateTimeOriginal");
            case IFDTag.IFD_9004_DateTimeDigitized -> IFDTagMessage.getString("IFD_9004_DateTimeDigitized");
            case IFDTag.IFD_9101_ComponentsConfiguration -> IFDTagMessage.getString("IFD_9101_ComponentsConfiguration");
            case IFDTag.IFD_9102_CompressedBitsPerPixel -> IFDTagMessage.getString("IFD_9102_CompressedBitsPerPixel");
            case IFDTag.IFD_9201_ShutterSpeedValue -> IFDTagMessage.getString("IFD_9201_ShutterSpeedValue");
            case IFDTag.IFD_9202_ApertureValue -> IFDTagMessage.getString("IFD_9202_ApertureValue");
            case IFDTag.IFD_9203_BrightnessValue -> IFDTagMessage.getString("IFD_9203_BrightnessValue");
            case IFDTag.IFD_9204_ExposureBiasValue -> IFDTagMessage.getString("IFD_9204_ExposureBiasValue");
            case IFDTag.IFD_9205_MaxApertureValue -> IFDTagMessage.getString("IFD_9205_MaxApertureValue");
            case IFDTag.IFD_9206_SubjectDistance -> IFDTagMessage.getString("IFD_9206_SubjectDistance");
            case IFDTag.IFD_9207_MeteringMode -> IFDTagMessage.getString("IFD_9207_MeteringMode");
            case IFDTag.IFD_9208_LightSource -> IFDTagMessage.getString("IFD_9208_LightSource");
            case IFDTag.IFD_9209_Flash -> IFDTagMessage.getString("IFD_9209_Flash");
            case IFDTag.IFD_920A_FocalLength -> IFDTagMessage.getString("IFD_920A_FocalLength");
            case IFDTag.IFD_9214_SubjectArea -> IFDTagMessage.getString("IFD_9214_SubjectArea");
            case IFDTag.IFD_927C_MakerNode -> IFDTagMessage.getString("IFD_927C_MakerNode");
            case IFDTag.IFD_9286_UserComment -> IFDTagMessage.getString("IFD_9286_UserComment");
            case IFDTag.IFD_9290_SubsecTime -> IFDTagMessage.getString("IFD_9290_SubsecTime");
            case IFDTag.IFD_9291_SubsecTimeOriginal -> IFDTagMessage.getString("IFD_9291_SubsecTimeOriginal");
            case IFDTag.IFD_9292_SubsecTimeDigitized -> IFDTagMessage.getString("IFD_9292_SubsecTimeDigitized");
            case IFDTag.IFD_A000_FlashpixVersion -> IFDTagMessage.getString("IFD_A000_FlashpixVersion");
            case IFDTag.IFD_A001_ColorSpace -> IFDTagMessage.getString("IFD_A001_ColorSpace");
            case IFDTag.IFD_A002_PixelXDimension -> IFDTagMessage.getString("IFD_A002_PixelXDimension");
            case IFDTag.IFD_A003_PixelYDimension -> IFDTagMessage.getString("IFD_A003_PixelYDimension");
            case IFDTag.IFD_A004_RelatedSoundFile -> IFDTagMessage.getString("IFD_A004_RelatedSoundFile");
            case IFDTag.IFD_A005_Interoperability -> IFDTagMessage.getString("IFD_A005_Interoperability");
            case IFDTag.IFD_A20B_FlashEnergy -> IFDTagMessage.getString("IFD_A20B_FlashEnergy");
            case IFDTag.IFD_A20C_SpatialFrequencyResponse -> IFDTagMessage.getString("IFD_A20C_SpatialFrequencyResponse");
            case IFDTag.IFD_A20E_FocalPlaneXResolution -> IFDTagMessage.getString("IFD_A20E_FocalPlaneXResolution");
            case IFDTag.IFD_A20F_FocalPlaneYResolution -> IFDTagMessage.getString("IFD_A20F_FocalPlaneYResolution");
            case IFDTag.IFD_A210_FocalPlaneResolutionUnit -> IFDTagMessage.getString("IFD_A210_FocalPlaneResolutionUnit");
            case IFDTag.IFD_A214_SubjectLocation -> IFDTagMessage.getString("IFD_A214_SubjectLocation");
            case IFDTag.IFD_A215_ExposureIndex -> IFDTagMessage.getString("IFD_A215_ExposureIndex");
            case IFDTag.IFD_A217_SensingMethod -> IFDTagMessage.getString("IFD_A217_SensingMethod");
            case IFDTag.IFD_A300_FileSource -> IFDTagMessage.getString("IFD_A300_FileSource");
            case IFDTag.IFD_A301_SceneType -> IFDTagMessage.getString("IFD_A301_SceneType");
            case IFDTag.IFD_A302_CFAPattern -> IFDTagMessage.getString("IFD_A302_CFAPattern");
            case IFDTag.IFD_A401_CustomRendered -> IFDTagMessage.getString("IFD_A401_CustomRendered");
            case IFDTag.IFD_A402_ExposureMode -> IFDTagMessage.getString("IFD_A402_ExposureMode");
            case IFDTag.IFD_A403_WhiteBalance -> IFDTagMessage.getString("IFD_A403_WhiteBalance");
            case IFDTag.IFD_A404_DigitalZoomRatio -> IFDTagMessage.getString("IFD_A404_DigitalZoomRatio");
            case IFDTag.IFD_A405_FocalLengthIn35mmFilm -> IFDTagMessage.getString("IFD_A405_FocalLengthIn35mmFilm");
            case IFDTag.IFD_A406_SceneCaptureType -> IFDTagMessage.getString("IFD_A406_SceneCaptureType");
            case IFDTag.IFD_A407_GainControl -> IFDTagMessage.getString("IFD_A407_GainControl");
            case IFDTag.IFD_A408_Contrast -> IFDTagMessage.getString("IFD_A408_Contrast");
            case IFDTag.IFD_A409_Saturation -> IFDTagMessage.getString("IFD_A409_Saturation");
            case IFDTag.IFD_A40A_Sharpness -> IFDTagMessage.getString("IFD_A40A_Sharpness");
            case IFDTag.IFD_A40B_DeviceSettingDescription -> IFDTagMessage.getString("IFD_A40B_DeviceSettingDescription");
            case IFDTag.IFD_A40C_SubjectDistanceRange -> IFDTagMessage.getString("IFD_A40C_SubjectDistanceRange");
            case IFDTag.IFD_A420_ImageUniqueID -> IFDTagMessage.getString("IFD_A420_ImageUniqueID");
            case IFDTag.IFD_A430_CameraOwnerName -> IFDTagMessage.getString("IFD_A430_CameraOwnerName");
            case IFDTag.IFD_A431_BodySerialNumber -> IFDTagMessage.getString("IFD_A431_BodySerialNumber");
            case IFDTag.IFD_A432_LensSpecification -> IFDTagMessage.getString("IFD_A432_LensSpecification");
            case IFDTag.IFD_A433_LensMake -> IFDTagMessage.getString("IFD_A433_LensMake");
            case IFDTag.IFD_A434_LensModel -> IFDTagMessage.getString("IFD_A434_LensModel");
            case IFDTag.IFD_A435_LensSerialNumber -> IFDTagMessage.getString("IFD_A435_LensSerialNumber");
            case IFDTag.IFD_A500_Gamma -> IFDTagMessage.getString("IFD_A500_Gamma");
            default -> IFDTagMessage.getString("UNKNOWN_TAG");
        };
    }

    public static String getTagNameGPS(int tag) {
        return switch (tag) {
            case IFDTag.IFD_0000_GPSVersionID -> IFDTagMessage.getString("IFD_0000_GPSVersionID");
            case IFDTag.IFD_0001_GPSLatitudeRef -> IFDTagMessage.getString("IFD_0001_GPSLatitudeRef");
            case IFDTag.IFD_0002_GPSLatitude -> IFDTagMessage.getString("IFD_0002_GPSLatitude");
            case IFDTag.IFD_0003_GPSLongitudeRef -> IFDTagMessage.getString("IFD_0003_GPSLongitudeRef");
            case IFDTag.IFD_0004_GPSLongitude -> IFDTagMessage.getString("IFD_0004_GPSLongitude");
            case IFDTag.IFD_0005_GPSAltitudeRef -> IFDTagMessage.getString("IFD_0005_GPSAltitudeRef");
            case IFDTag.IFD_0006_GPSAltitude -> IFDTagMessage.getString("IFD_0006_GPSAltitude");
            case IFDTag.IFD_0007_GPSTimeStamp -> IFDTagMessage.getString("IFD_0007_GPSTimeStamp");
            case IFDTag.IFD_0008_GPSSatellites -> IFDTagMessage.getString("IFD_0008_GPSSatellites");
            case IFDTag.IFD_0009_GPSStatus -> IFDTagMessage.getString("IFD_0009_GPSStatus");
            case IFDTag.IFD_000A_GPSMeasureMode -> IFDTagMessage.getString("IFD_000A_GPSMeasureMode");
            case IFDTag.IFD_000B_GPSDOP -> IFDTagMessage.getString("IFD_000B_GPSDOP");
            case IFDTag.IFD_000C_GPSSpeedRef -> IFDTagMessage.getString("IFD_000C_GPSSpeedRef");
            case IFDTag.IFD_000D_GPSSpeed -> IFDTagMessage.getString("IFD_000D_GPSSpeed");
            case IFDTag.IFD_000E_GPSTrackRef -> IFDTagMessage.getString("IFD_000E_GPSTrackRef");
            case IFDTag.IFD_000F_GPSTrack -> IFDTagMessage.getString("IFD_000F_GPSTrack");
            case IFDTag.IFD_0010_GPSImgDirectionRef -> IFDTagMessage.getString("IFD_0010_GPSImgDirectionRef");
            case IFDTag.IFD_0011_GPSImgDirection -> IFDTagMessage.getString("IFD_0011_GPSImgDirection");
            case IFDTag.IFD_0012_GPSMapDatum -> IFDTagMessage.getString("IFD_0012_GPSMapDatum");
            case IFDTag.IFD_0013_GPSDestLatitudeRef -> IFDTagMessage.getString("IFD_0013_GPSDestLatitudeRef");
            case IFDTag.IFD_0014_GPSDestLatitude -> IFDTagMessage.getString("IFD_0014_GPSDestLatitude");
            case IFDTag.IFD_0015_GPSDestLongitudeRef -> IFDTagMessage.getString("IFD_0015_GPSDestLongitudeRef");
            case IFDTag.IFD_0016_GPSDestLongitude -> IFDTagMessage.getString("IFD_0016_GPSDestLongitude");
            case IFDTag.IFD_0017_GPSDestBearingRef -> IFDTagMessage.getString("IFD_0017_GPSDestBearingRef");
            case IFDTag.IFD_0018_GPSDestBearing -> IFDTagMessage.getString("IFD_0018_GPSDestBearing");
            case IFDTag.IFD_0019_GPSDestDistanceRef -> IFDTagMessage.getString("IFD_0019_GPSDestDistanceRef");
            case IFDTag.IFD_001A_GPSDestDistance -> IFDTagMessage.getString("IFD_001A_GPSDestDistance");
            case IFDTag.IFD_001B_GPSProcessingMethod -> IFDTagMessage.getString("IFD_001B_GPSProcessingMethod");
            case IFDTag.IFD_001C_GPSAreaInformation -> IFDTagMessage.getString("IFD_001C_GPSAreaInformation");
            case IFDTag.IFD_001D_GPSDateStamp -> IFDTagMessage.getString("IFD_001D_GPSDateStamp");
            case IFDTag.IFD_001E_GPSDifferential -> IFDTagMessage.getString("IFD_001E_GPSDifferential");
            case IFDTag.IFD_001F_GPSHPositioiningError -> IFDTagMessage.getString("IFD_001F_GPSHPositioiningError");
            default -> IFDTagMessage.getString("UNKNOWN_GPS_TAG");
        };
    }

    public static String getTagNameIntero(int tag) {
        return switch (tag) {
            case IFDTag.IFD_0001_InteroperabilityIndex -> IFDTagMessage.getString("IFD_0001_InteroperabilityIndex");
            case IFDTag.IFD_0002_InteroperabilityVersion -> IFDTagMessage.getString("IFD_0002_InteroperabilityVersion");
            default -> IFDTagMessage.getString("UNKNOWN_TAG");
        };
    }

}
