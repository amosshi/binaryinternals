/*
 * FileComponenPlaceHolder.java    10:32 PM, August 9, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.io.IOException;

/**
 * Super class for all simple components which have only one 16-bit (@link FileComponentU2) field in a {@code class} file.
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class FileComponenPlaceHolder extends FileComponent {

    FileComponenPlaceHolder() {
        super();
    }

    public FileComponenPlaceHolder(final PosDataInputStream posDataInputStream, int length)
            throws IOException {
        this();
        super.startPos = posDataInputStream.getPos();

        if (length > 0) {
            this.length = length;
            posDataInputStream.skipBytes(length);
        } else {
            this.length = 0;
        }
    }
}
