/*
 * Marker_DQT.java    August 25, 2010, 23:30
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponentPlaceHolder;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class Marker_DQT extends Marker {

    private int Pq;
    private int Tq;
    private FileComponentPlaceHolder Qk;

    Marker_DQT(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException {

        super.parseInitSkip(pDisMarker);

        // Pq & Tq
        final byte b = pDisMarker.readByte();
        this.Pq = (b & 0xF0) >> 4;    // TODO - Check value more precisely
        this.Tq = (b & 0x0F);

        if (this.Pq != 0 && this.Pq != 1) {
            throw new FileFormatException("DQT Pq value unrecognizable: expected value is 0 or 1; current value is %d.".formatted(
                    this.Pq));
        }
        if (this.Tq != 0 && this.Tq != 1 && this.Tq != 2 && this.Tq != 3) {
            throw new FileFormatException("DQT Tq value unrecognizable: expected value is 0, 1, 2 or 3; current value is %d.".formatted(
                    this.Pq));
        }

        // Qk

        // The spec gives (Pq + 1) * 64 bytes, but files were found with 16-bit
        // values when Pq is 0, so the rest of the marker is taken instead.
        this.Qk = new FileComponentPlaceHolder(pDisMarker, this.length - 1 - 2 - 2);
    }

    // Interface Methods

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);

        // Marker code
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos(),
                2,
                "Marker code: 0xFFDB")));

        // Marker Length
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 2,
                2,
                "length: %d".formatted(super.getMarkerLength()))));

        // Pq
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                1,
                "Pq: %d".formatted(this.Pq))));

        // Tq
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                1,
                "Tq: %d".formatted(this.Tq))));

        // Qk
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 5,
                this.Qk.getLength(),
                "Qk: length = %d".formatted(this.Qk.getLength()))));
    }
}
