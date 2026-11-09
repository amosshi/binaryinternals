/*
 * Marker_SOS.java    August 27, 2010, 23:48
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// `SOS` marker: start of scan.
///
/// @author Amos Shi
public class Marker_SOS extends Marker {

    /// Scan component-specification parameters.
    ///
    /// @param cs Scan component selector, `Cs`
    /// @param td DC entropy coding table destination selector, `Td`
    /// @param ta AC entropy coding table destination selector, `Ta`
    private record Parameter(int cs, int td, int ta) {
    }

    /// Number of image components in scan.
    private int Ns;

    /// Scan component-specification parameters.
    private Parameter[] parameter;

    /// Start of spectral or predictor selection.
    private int Ss;

    /// End of spectral selection.
    private int Se;

    /// Successive approximation bit position high.
    private int Ah;

    /// Successive approximation bit position low or point transform.
    private int Al;

    Marker_SOS(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException {

        super.parseInitSkip(pDisMarker);

        // Ns
        this.Ns = pDisMarker.readUnsignedByte();

        // ITU-T T.81, B.2.3: Scan header syntax
        if (this.Ns < 1 || this.Ns > 4) {
            throw new FileFormatException("SOS number of image components Ns is %d, expected 1 - 4.".formatted(this.Ns));
        }
        if (super.marker_length != 6 + 2 * this.Ns) {
            throw new FileFormatException("SOS length Ls is %d, expected 6 + 2 * Ns = %d.".formatted(
                    super.marker_length, 6 + 2 * this.Ns));
        }

        // Scan component-specification parameters: Cs, Td, Ta
        this.parameter = new Parameter[this.Ns];
        for (int i = 0; i < this.Ns; i++) {
            final int cs = pDisMarker.readUnsignedByte();
            final int tables = pDisMarker.readUnsignedByte();
            this.parameter[i] = new Parameter(cs, (tables & 0xF0) >> 4, tables & 0x0F);
        }

        // Ss, Se, Ah, Al
        this.Ss = pDisMarker.readUnsignedByte();
        this.Se = pDisMarker.readUnsignedByte();

        final byte b = pDisMarker.readByte();
        this.Ah = (b & 0xF0) >> 4;
        this.Al = b & 0x0F;
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

        // Ls
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 2,
                2,
                "Ls (Scan header length): %d".formatted(super.getMarkerLength()))));

        // Ns
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                "Ns: %d".formatted(this.Ns))));

        // Scan component-specification parameters: Cs, Td, Ta
        if (this.Ns > 0) {

            final DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + 5,
                    this.Ns * 2,
                    "Scan component-specification parameters"));
            markerNode.add(attributeNode);

            for (int i = 0; i < this.Ns; i++) {
                final DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2 * i,
                        2,
                        "Parameter-%d".formatted(i + 1)));
                attributeNode.add(parameterNode);

                // Cs, Td, Ta
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2 * i + 0,
                        1,
                        "Cs: %d".formatted(this.parameter[i].cs()))));
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2 * i + 1,
                        1,
                        "Td: %d".formatted(this.parameter[i].td()))));
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2 * i + 1,
                        1,
                        "Ta: %d".formatted(this.parameter[i].ta()))));
            }
        }

        // Ss, Se, Ah, Al
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns * 2 + 0,
                1,
                "Ss: %d".formatted(this.Ss))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns * 2 + 1,
                1,
                "Se: %d".formatted(this.Se))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns * 2 + 2,
                1,
                "Ah: %d".formatted(this.Ah))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns * 2 + 2,
                1,
                "Al: %d".formatted(this.Al))));
    }
}
