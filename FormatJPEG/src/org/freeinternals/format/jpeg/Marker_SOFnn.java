/*
 * Marker_SOFnn.java    September 02, 2010, 00:44
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class Marker_SOFnn extends Marker {

    private class Component {

        /** Component Identifier */
        @SuppressWarnings("PackageVisibleField")
        int C;
        /** Horizontal sampling factor */
        @SuppressWarnings("PackageVisibleField")
        int H;
        /** Vertical sampling factor  */
        @SuppressWarnings("PackageVisibleField")
        int V;
        /** Quantization table destination selector */
        @SuppressWarnings("PackageVisibleField")
        int Tq;
    }
    /** Sample precision */
    private int P;
    /** Number of lines */
    private int Y;
    /** Number of samples per line */
    private int X;
    /** Number of image components in frame */
    private int Nf;
    /** Image Components */
    private Component[] C;

    Marker_SOFnn(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException, FileFormatException {

        super.parseInitSkip(pDisMarker);

        // P,Y, X, Nf
        this.P = pDisMarker.readUnsignedByte();   // TODO - Check the values
        this.Y = pDisMarker.readUnsignedShort();  // TODO - Check the values
        this.X = pDisMarker.readUnsignedShort();  // TODO - Check the values
        this.Nf = pDisMarker.readUnsignedByte();  // TODO - Check the values

        // Components: Ci, Hi, Vi, Tqi
        byte b;
        this.C = new Component[this.Nf];
        for (int i = 0; i < this.Nf; i++) {
            this.C[i] = new Component();
            this.C[i].C = pDisMarker.readUnsignedByte();
            b = pDisMarker.readByte();
            this.C[i].H = (b & 0xF0) >> 4;
            this.C[i].V = (b & 0x0F);
            this.C[i].Tq = pDisMarker.readUnsignedByte();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        final int pos = super.getStartPos();

        // Marker code
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 0,
                2,
                String.format("Marker code: 0x%X", super.getMarker()))));

        // Marker Length
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 2,
                2,
                String.format("length: %d", super.getMarkerLength()))));

        // P, Y, X, Nf
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                String.format("P: %d", this.P))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5,
                2,
                String.format("Y: %d", this.Y))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 7,
                2,
                String.format("X: %d", this.X))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                String.format("Nf: %d", this.Nf))));

        // Components: Ci, Hi, Vi, Tqi
        if (this.Nf <= 0 || this.C == null) {
            return;
        }

        DefaultMutableTreeNode attributeNode;
        DefaultMutableTreeNode componentNode;

        markerNode.add(attributeNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                this.Nf * 3,
                "Component-specification parameters")));

        for (int i = 0; i < this.Nf; i++) {

            attributeNode.add(componentNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + 10 + 3 * i,
                    3,
                    String.format("Component-%d", i + 1))));

            if (this.C[i] != null) {
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 0,
                        1,
                        String.format("C: %d", this.C[i].C))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 1,
                        1,
                        String.format("H: %d", this.C[i].H))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 1,
                        1,
                        String.format("V: %d", this.C[i].V))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 2,
                        1,
                        String.format("Tq: %d", this.C[i].Tq))));
            }
        }
    }
}
