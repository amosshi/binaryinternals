/*
 * Marker_TEM.java    August 28, 2010, 00:00
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
public class Marker_TEM extends Marker {

    Marker_TEM(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) {
    }
}
