/*
 * Marker_SOS.java    August 27, 2010, 23:48
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
public class Marker_SOS extends Marker {

    private class Parameter {

        /** Scan component selector */
        @SuppressWarnings("PackageVisibleField")
        int Cs;
        /** DC entropy coding table destination selector. */
        @SuppressWarnings("PackageVisibleField")
        int Td;
        /** AC entropy coding table destination selector.  */
        @SuppressWarnings("PackageVisibleField")
        int Ta;
    }

    /** Number of image components in scan. */
    private int Ns;

    /** Scan component-specification parameters. */
    private Parameter[] parameter;

    /** Start of spectral or predictor selection. */
    private int Ss;

    /** End of spectral selection. */
    private int Se;

    /** Successive approximation bit position high. */
    private int Ah;

    /** Successive approximation bit position low or point transform. */
    private int Al;

    Marker_SOS(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }


    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException, FileFormatException {

        super.parseInitSkip(pDisMarker);

        byte b;

        // Ns
        this.Ns = pDisMarker.readUnsignedByte();        // TODO - Check this value

        // Scan component-specification parameters: Cs, Td, Ta
        this.parameter = new Parameter[this.Ns];
        for (int i = 0; i < this.Ns; i++) {
            this.parameter[i] = new Parameter();
            this.parameter[i].Cs = pDisMarker.readUnsignedByte();

            b = pDisMarker.readByte();
            this.parameter[i].Td = (b & 0xF0) >> 4;
            this.parameter[i].Ta = b & 0x0F;
        }
        // TODO - Now check the value Ls(marker length) matchs
        //super.verify();

        // Ss, Se, Ah, Al
        this.Ss = pDisMarker.readUnsignedByte();
        this.Se = pDisMarker.readUnsignedByte();

        b = pDisMarker.readByte();
        this.Ah = (b & 0xF0) >> 4;
        this.Al = b & 0x0F;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        DefaultMutableTreeNode attributeNode;
        final int pos = super.getStartPos();

        // Marker code
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 0,
                2,
                String.format("Marker code: 0x%X", super.getMarker()))));

        // Ls
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 2,
                2,
                String.format("Ls (Scan header length): %d", super.getMarkerLength()))));

        // Ns
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                String.format("Ns: %d", this.Ns))));

        // Scan component-specification parameters: Cs, Td, Ta
        if (this.Ns > 0) {
            DefaultMutableTreeNode parameterNode;

            markerNode.add(attributeNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + 5,
                    this.Ns * 2,
                    "Scan component-specification parameters")));

            for (int i = 0; i < this.Ns; i++) {
                parameterNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2*i,
                        2,
                        String.format("Parameter-%d", i + 1)));
                attributeNode.add(parameterNode);

                // Cs, Td, Ta
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2*i + 0,
                        1,
                        String.format("Cs: %d", this.parameter[i].Cs))));
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2*i + 1,
                        1,
                        String.format("Td: %d", this.parameter[i].Td))));
                parameterNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos + 5 + 2*i + 1,
                        1,
                        String.format("Ta: %d", this.parameter[i].Ta))));
            }
        }

        // Ss, Se, Ah, Al
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns*2 + 0,
                1,
                String.format("Ss: %d", this.Ss))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns*2 + 1,
                1,
                String.format("Se: %d", this.Se))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns*2 + 2,
                1,
                String.format("Ah: %d", this.Ah))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 5 + this.Ns*2 + 2,
                1,
                String.format("Al: %d", this.Al))));
    }
}
