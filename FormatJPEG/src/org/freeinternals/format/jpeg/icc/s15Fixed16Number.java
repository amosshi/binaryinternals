/*
 * s15Fixed16Number.java    Nov 10, 2010, 09:18
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 *
 * @author Amos Shi
 */
public class s15Fixed16Number {

    public final byte[] rawData;
    public final double value;

    public s15Fixed16Number(final PosDataInputStream input) throws IOException {
        this.rawData = new byte[4];
        this.rawData[0] = input.readByte();
        this.rawData[1] = input.readByte();
        this.rawData[2] = input.readByte();
        this.rawData[3] = input.readByte();

        StringBuilder sb = new StringBuilder(9);
        sb.append(String.format("%02X", this.rawData[0]));
        sb.append(String.format("%02X", this.rawData[1]));
        sb.append(String.format("%02X", this.rawData[2]));
        sb.append(String.format("%02X", this.rawData[3]));

        long l = Long.parseLong(sb.toString(), 16);
        if (l > 0x80000000) {
            this.value = ((((~l) << 32) >> 32) * -1) / 65536.0;
        } else {
            int p1 = Integer.parseInt(sb.substring(0, 4), 16);
            int p2 = Integer.parseInt(sb.substring(4), 16);
            this.value = p1 + p2 / 65536.0;
        }
    }

    @Override
    public String toString(){
        return String.format("%.4f", this.value);
    }
}
