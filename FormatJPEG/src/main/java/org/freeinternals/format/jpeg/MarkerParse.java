/*
 * MarkerParse.java    Sep 19, 2010, 20:35
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class MarkerParse {

    /**
     *
     * @param pDisFile Data Input Stream for <code>file</code> byte array
     * @return The generated proper marker instance
     * @throws IOException
     * @throws JPEGFileFormatException
     */
    public static Marker parse(final PosDataInputStream pDisFile) throws IOException, FileFormatException {
        Marker marker;

        final int marker_code = pDisFile.readUnsignedShort();
        switch (marker_code) {
            case MarkerCode.APP00:
                marker = new Marker_APP00(pDisFile, marker_code);
                break;

            case MarkerCode.APP01:
                marker = new Marker_APP01(pDisFile, marker_code);
                break;

            case MarkerCode.APP02:
                marker = new Marker_APP02(pDisFile, marker_code);
                break;

            case MarkerCode.APP13:
                marker = new Marker_APP13(pDisFile, marker_code);
                break;

            case MarkerCode.APP14:
                marker = new Marker_APP14(pDisFile, marker_code);
                break;

            case MarkerCode.DQT:
                marker = new Marker_DQT(pDisFile, marker_code);
                break;

            case MarkerCode.DRI:
                marker = new Marker_DRI(pDisFile, marker_code);
                break;

            case MarkerCode.SOF00:
            case MarkerCode.SOF01:
            case MarkerCode.SOF02:
            case MarkerCode.SOF03:

            case MarkerCode.SOF05:
            case MarkerCode.SOF06:
            case MarkerCode.SOF07:

            case MarkerCode.SOF09:
            case MarkerCode.SOF10:
            case MarkerCode.SOF11:

            case MarkerCode.SOF13:
            case MarkerCode.SOF14:
            case MarkerCode.SOF15:
                marker = new Marker_SOFnn(pDisFile, marker_code);
                break;

            case MarkerCode.SOI:
                marker = new Marker_SOI(pDisFile, marker_code);
                break;

            case MarkerCode.EOI:
                marker = new Marker_EOI(pDisFile, marker_code);
                break;

            case MarkerCode.DHT:
                marker = new Marker_DHT(pDisFile, marker_code);
                break;

            case MarkerCode.SOS:
                marker = new Marker_SOS(pDisFile, marker_code);
                break;

            case MarkerCode.COM:
                marker = new Marker_COM(pDisFile, marker_code);
                break;

            default:
                marker = new Marker(pDisFile, marker_code);
                break;
        }

        return marker;
    }
}
