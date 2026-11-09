/*
 * Marker_DHT.java    August 27, 2010, 23:26
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponentPlaceHolder;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// `DHT` marker: define Huffman table(s).
///
/// @author Amos Shi
public class Marker_DHT extends Marker {

    /// Table class. 0 = DC table or lossless table; 1 = AC table.
    private int Tc;
    /// Huffman table destination identifier.
    private int Th;
    /// Number of Huffman codes of length `i`.
    private final int[] Li = new int[16];
    /// Value associated with each Huffman code. Specifies, for each
    /// `i`, the value associated with each Huffman code of length
    /// `i`. The meaning of each value is determined by the Huffman
    /// coding model. The Vi,j’s are the elements of the list
    /// `HUFFVAL`.
    private FileComponentPlaceHolder Vij;

    Marker_DHT(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException {

        super.parseInitSkip(pDisMarker);

        // Tc, Th
        byte b = pDisMarker.readByte();
        this.Tc = (b & 0xF0) >> 4;    // Table class: 0 = DC table, 1 = AC table
        this.Th = (b & 0x0F);         // Huffman table destination identifier: 0 - 3

        // Li
        for (int i = 0; i < 16; i++) {
            this.Li[i] = pDisMarker.readUnsignedByte();
        }

        // Vij
        this.Vij = new FileComponentPlaceHolder(pDisMarker, super.marker_length - 19);
    }

    /// {@inheritDoc}
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        final int pos = super.getStartPos();

        // Marker code
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 0,
                2,
                "Marker code: 0x%X".formatted(super.getMarker()))));

        // Marker Length
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 2,
                2,
                "length: %d".formatted(super.getMarkerLength()))));

        // Tc, Th
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                "Tc: %d".formatted(this.Tc))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                "Th: %d".formatted(this.Th))));

        // Li
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5,
                16,
                "Li: L1 - L16")));

        // Vij
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 21,
                this.Vij.getLength(),
                "Vij: Symbol-length assignment parameters")));

    }

}
