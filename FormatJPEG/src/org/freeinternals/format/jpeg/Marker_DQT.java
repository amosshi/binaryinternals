/*
 * Marker_DQT.java    August 25, 2010, 23:30
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponenPlaceHolder;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class Marker_DQT extends Marker {

    private int Pq;
    private int Tq;
    private FileComponenPlaceHolder Qk;

    Marker_DQT(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }


    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException, FileFormatException {

        super.parseInitSkip(pDisMarker);

        // Pq & Tq
        final byte b = pDisMarker.readByte();
        this.Pq = (b & 0xF0) >> 4;    // TODO - Check value more precisely
        this.Tq = (b & 0x0F);

        if (this.Pq != 0 && this.Pq != 1) {
            throw new FileFormatException(String.format(
                    "DQT Pq value unrecognizable: expected value is 0 or 1; current value is %d.",
                    this.Pq));
        }
        if (this.Tq != 0 && this.Tq != 1 && this.Tq != 2 && this.Tq != 3) {
            throw new FileFormatException(String.format(
                    "DQT Tq value unrecognizable: expected value is 0, 1, 2 or 3; current value is %d.",
                    this.Pq));
        }

        // Qk

        // This should be corret, but in testing, we find that when Pq is 0, it is still has 16-bit Pq.
        //this.Qk = new FileComponenPlaceHolder(pDisMarker, (this.Pq + 1) * 64);
        this.Qk = new FileComponenPlaceHolder(pDisMarker, this.length - 1 - 2 - 2);
    }

    ///////////////////////////////////////////////////////////////////////////
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
                String.format("length: %d", super.getMarkerLength()))));

        // Pq
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                1,
                String.format("Pq: %d", this.Pq))));

        // Tq
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                1,
                String.format("Tq: %d", this.Tq))));

        // Qk
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 5,
                this.Qk.getLength(),
                String.format("Qk: length = %d", this.Qk.getLength()))));
    }
}
