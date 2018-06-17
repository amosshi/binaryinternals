/**
 * Chunk.java    Apr 21, 2011, 22:46
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.png;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 * @see <a href="http://www.LIBPNG.org">PNG (Portable Network Graphics) Specification</a>
 */
public class Chunk extends FileComponent implements GenerateTreeNode {

    /**
     * A 4-byte unsigned integer giving the number of bytes in the chunk's data
     * field.
     * <p>
     * The length counts <strong>only</strong> the data field, 
     * <strong>not</strong> itself, the chunk type code, or the CRC.  
     * Zero is a valid length. 
     * Although encoders and decoders should treat the length as unsigned, 
     * its value must not exceed <code>2<sup>31</sup>-1</code> bytes.
     * </p>
     */
    public final int Length;
    /**
     * A 4-byte chunk type code.
     * <p>
     * For convenience in description and in examining PNG files, type codes
     * are restricted to consist of uppercase and lowercase ASCII letters
     * (A-Z and a-z, or 65-90 and 97-122 decimal).
     * However, encoders and decoders must treat the codes as fixed binary
     * values, not character strings.
     * For example, it would not be correct to represent the type code
     * <code>IDAT</code> by the EBCDIC equivalents of those letters.
     * Additional naming conventions for chunk types are discussed in the next
     * section.
     * </p>
     */
    public final byte[] ChunkType = new byte[4];
    /**
     * The data bytes appropriate to the chunk type, if any.
     * <p>
     * This field can be <code>null</code> for zero length.
     * </p>
     */
    public final byte[] ChunkData;
    /**
     * A 4-byte CRC (Cyclic Redundancy Check) calculated on the preceding bytes
     * in the chunk, including the chunk type code and chunk data fields,
     * but <strong>not</strong> including the length field.
     * <p>
     * The CRC is always present, even for chunks containing no data.
     * </p>
     */
    public final byte[] CRC = new byte[4];

    Chunk(PosDataInputStream stream, PNGFile png) throws IOException {
        this.startPos = stream.getPos();

        this.Length = stream.readInt();  // Chunk Length
        this.length = this.Length + 12;

        // Chunk Type
        stream.read(this.ChunkType);

        // Chunk Data
        if (this.Length > 0) {
            this.ChunkData = new byte[this.Length];
            stream.read(this.ChunkData);
        } else {
            this.ChunkData = null;
        }

        // CRC
        stream.read(this.CRC);
    }

    public String getChunkTypeName() {
        StringBuilder sb = new StringBuilder(this.ChunkType.length);
        sb.append((char) this.ChunkType[0]);
        sb.append((char) this.ChunkType[1]);
        sb.append((char) this.ChunkType[2]);
        sb.append((char) this.ChunkType[3]);
        return sb.toString();
    }

    protected final void checkLength(String chunkTypeName, int lengthExpected) throws FileFormatException {
        if (this.Length != lengthExpected) {
            throw new FileFormatException(String.format(
                    "Chunk (%s): chunk data length must be %d. current value = %d",
                    chunkTypeName,
                    lengthExpected,
                    this.Length));
        }
    }

    protected PosDataInputStream getChunkDataStream() {
        if (this.ChunkData != null) {
            return new PosDataInputStream(
                    new PosByteArrayInputStream(this.ChunkData),
                    this.startPos + 4 + 4);
        } else {
            return null;
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parent) {
        int start = this.startPos;
        DefaultMutableTreeNode chunkDataNode;

        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start,
                4,
                String.format("Length = %d", this.Length))));
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start = start + 4,
                4,
                String.format("Chunk Type = %s", this.getChunkTypeName()))));

        start = start + 4;            // Or else when Lengh == 0, the 4 is not added
        if (this.Length > 0) {
            parent.add(chunkDataNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    start, // start = start + 4
                    this.Length,
                    "Chunk Data")));
            this.generateTreeNodeChunkData(chunkDataNode);
        }
        parent.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                start + this.Length,
                4,
                "CRC")));
    }

    protected void generateTreeNodeChunkData(DefaultMutableTreeNode parent) {
        System.out.println("Care me!");
    }
}
