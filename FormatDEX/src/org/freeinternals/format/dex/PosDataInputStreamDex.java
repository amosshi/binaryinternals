/*
 * PosDataInputStreamDex.java    June 17, 2015, 21:29
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 *
 * @author Amos Shi
 */
public class PosDataInputStreamDex extends PosDataInputStream {

    /**
     * Endian of the {@link DexFile}. The default value is little-endian
     * {@link HeaderItem.Endian#ENDIAN_CONSTANT}, as the DEX format
     * specification said.
     */
    protected HeaderItem.Endian endian = HeaderItem.Endian.ENDIAN_CONSTANT;

    public PosDataInputStreamDex(PosByteArrayInputStream in) {
        super(in);
    }

    protected void setEndian(HeaderItem.Endian e) {
        this.endian = e;
    }

    /**
     * Read a {@link dexbyte} from the input stream.
     *
     * @return a {@link dexbyte}
     * @throws java.io.IOException I/O error
     */
    public dexbyte dexbyte() throws IOException {
        return new dexbyte(this.readByte());
    }

    /**
     * Read a {@link dexubyte} from the input stream.
     *
     * @return a {@link dexubyte}
     * @throws java.io.IOException I/O error
     */
    public dexubyte dexubyte() throws IOException {
        return new dexubyte(this.readUnsignedByte());
    }

    /**
     * Read a {@link dexshort} from the input stream.
     *
     * @return a {@link dexshort}
     * @throws java.io.IOException I/O Error
     */
    public dexshort dexshort() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexshort(this.readShort_LittleEndian());
        } else {
            return new dexshort(this.readShort());
        }
    }

    /**
     * Read a {@link dexushort} from the input stream.
     *
     * @return a {@link dexushort}
     * @throws java.io.IOException I/O Error
     */
    public dexushort dexushort() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexushort(this.readUnsignedShort_LittleEndian());
        } else {
            return new dexushort(this.readUnsignedShort());
        }
    }

    /**
     * Read a {@link dexint} from the input stream.
     *
     * @return a {@link dexint}
     * @throws java.io.IOException I/O Error
     */
    public dexint dexint() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexint(this.readInt_LittleEndian());
        } else {
            return new dexint(this.readInt());
        }
    }

    /**
     * Read a {@link dexuint} from the input stream.
     *
     * @return a {@link dexuint}
     * @throws java.io.IOException I/O Error
     */
    public dexuint dexuint() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexuint(this.readUnsignedInt_LittleEndian());
        } else {
            return new dexuint(this.readUnsignedInt());
        }
    }

    /**
     * Read a {@link dexlong} from the input stream.
     *
     * @return a {@link dexlong}
     * @throws java.io.IOException I/O Error
     */
    public dexlong dexlong() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexlong(this.readLong_LittleEndian());
        } else {
            return new dexlong(this.readLong());
        }
    }

    /**
     * Read a {@link dexulong} from the input stream.
     *
     * @return a {@link dexulong}
     * @throws java.io.IOException I/O Error
     */
    public dexulong dexulong() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new dexulong(this.readUnsignedLong_LittleEndian());
        } else {
            return new dexulong(this.readUnsignedLong());
        }
    }

    /**
     * Read a {@link dexsleb128} from the input stream.
     *
     * @return a {@link dexsleb128}
     */
    public dexsleb128 dexsleb128() {
        return null;
    }

    /**
     * Read a {@link dexuleb128} from the input stream.
     *
     * @return a {@link dexuleb128}
     */
    public dexuleb128 dexuleb128() {
        return null;
    }

    /**
     * Read a {@link dexuleb128p1} from the input stream.
     *
     * @return a {@link dexuleb128p1}
     */
    public dexuleb128p1 dexuleb128p1() {
        return null;
    }
}
