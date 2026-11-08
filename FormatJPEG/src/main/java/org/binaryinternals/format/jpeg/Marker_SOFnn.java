/*
 * Marker_SOFnn.java    September 02, 2010, 00:44
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class Marker_SOFnn extends Marker {

    /// Frame component-specification parameters.
    ///
    /// @param c Component identifier, `C`
    /// @param h Horizontal sampling factor, `H`
    /// @param v Vertical sampling factor, `V`
    /// @param tq Quantization table destination selector, `Tq`
    private record Component(int c, int h, int v, int tq) {
    }
    /// Sample precision
    private int P;
    /// Number of lines
    private int Y;
    /// Number of samples per line
    private int X;
    /// Number of image components in frame
    private int Nf;
    /// Image Components
    private Component[] C;

    Marker_SOFnn(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException {

        super.parseInitSkip(pDisMarker);

        // P,Y, X, Nf
        this.P = pDisMarker.readUnsignedByte();   // TODO - Check the values
        this.Y = pDisMarker.readUnsignedShort();  // TODO - Check the values
        this.X = pDisMarker.readUnsignedShort();  // TODO - Check the values
        this.Nf = pDisMarker.readUnsignedByte();  // TODO - Check the values

        // Components: Ci, Hi, Vi, Tqi
        this.C = new Component[this.Nf];
        for (int i = 0; i < this.Nf; i++) {
            final int c = pDisMarker.readUnsignedByte();
            final int b = pDisMarker.readUnsignedByte();
            this.C[i] = new Component(c, (b & 0xF0) >> 4, b & 0x0F, pDisMarker.readUnsignedByte());
        }
    }

    // Interface Methods
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

        // P, Y, X, Nf
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                "P: %d".formatted(this.P))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5,
                2,
                "Y: %d".formatted(this.Y))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 7,
                2,
                "X: %d".formatted(this.X))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 9,
                1,
                "Nf: %d".formatted(this.Nf))));

        // Components: Ci, Hi, Vi, Tqi
        if (this.Nf <= 0 || this.C == null) {
            return;
        }

        final DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 10,
                this.Nf * 3,
                "Component-specification parameters"));
        markerNode.add(attributeNode);

        for (int i = 0; i < this.Nf; i++) {

            final DefaultMutableTreeNode componentNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + 10 + 3 * i,
                    3,
                    "Component-%d".formatted(i + 1)));
            attributeNode.add(componentNode);

            if (this.C[i] != null) {
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 0,
                        1,
                        "C: %d".formatted(this.C[i].c()))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 1,
                        1,
                        "H: %d".formatted(this.C[i].h()))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 1,
                        1,
                        "V: %d".formatted(this.C[i].v()))));
                componentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 10 + 3 * i + 2,
                        1,
                        "Tq: %d".formatted(this.C[i].tq()))));
            }
        }
    }
}
