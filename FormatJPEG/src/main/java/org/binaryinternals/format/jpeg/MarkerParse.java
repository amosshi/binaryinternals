/*
 * MarkerParse.java    Sep 19, 2010, 20:35
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class MarkerParse {

    private MarkerParse() {
    }

    /// @param pDisFile Data Input Stream for `file` byte array
    /// @return The generated proper marker instance
    /// @throws IOException Parse file failed
    /// @throws FileFormatException Parse file failed
    public static Marker parse(final PosDataInputStream pDisFile) throws IOException {
        final int markerCode = pDisFile.readUnsignedShort();
        return switch (markerCode) {
            case MarkerCode.APP00 -> new Marker_APP00(pDisFile, markerCode);
            case MarkerCode.APP01 -> new Marker_APP01(pDisFile, markerCode);
            case MarkerCode.APP02 -> new Marker_APP02(pDisFile, markerCode);
            case MarkerCode.APP13 -> new Marker_APP13(pDisFile, markerCode);
            case MarkerCode.APP14 -> new Marker_APP14(pDisFile, markerCode);
            case MarkerCode.DQT -> new Marker_DQT(pDisFile, markerCode);
            case MarkerCode.DRI -> new Marker_DRI(pDisFile, markerCode);
            case MarkerCode.SOF00, MarkerCode.SOF01, MarkerCode.SOF02, MarkerCode.SOF03,
                    MarkerCode.SOF05, MarkerCode.SOF06, MarkerCode.SOF07, MarkerCode.SOF09,
                    MarkerCode.SOF10, MarkerCode.SOF11, MarkerCode.SOF13, MarkerCode.SOF14,
                    MarkerCode.SOF15 -> new Marker_SOFnn(pDisFile, markerCode);
            case MarkerCode.SOI -> new Marker_SOI(pDisFile, markerCode);
            case MarkerCode.EOI -> new Marker_EOI(pDisFile, markerCode);
            case MarkerCode.DHT -> new Marker_DHT(pDisFile, markerCode);
            case MarkerCode.SOS -> new Marker_SOS(pDisFile, markerCode);
            case MarkerCode.COM -> new Marker_COM(pDisFile, markerCode);
            default -> new Marker(pDisFile, markerCode);
        };
    }
}
