/*
 * Marker_SOF02.java    August 27, 2010, 23:31
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
public class Marker_RSTm extends Marker {

    Marker_RSTm(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) {
    }
}
