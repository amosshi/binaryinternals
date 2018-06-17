/*
 * Marker_DHT.java    August 27, 2010, 23:26
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
public class Marker_DHT extends Marker {

    /** Table class. 0 = DC table or lossless table; 1 = AC table. */
    private int Tc;                      // TODO - Set all attributes as final
    /** Huffman table destination identifier. */
    private int Th;
    /** Number of Huffman codes of length <code>i</code>. */
    private int[] Li = new int[16];
    /** Value associated with each Huffman code.
     *  Specifies, for each <code>i</code>, the value associated with each 
     * Huffman code of length <code>i</code>.
     * The meaning of each value is determined by the Huffman coding model.
     * The Vi,j’s are the elements of the list <code>HUFFVAL</code>.
     */
    private FileComponenPlaceHolder Vij;


    Marker_DHT(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException, FileFormatException {

        super.parseInitSkip(pDisMarker);

        // Tc, Th
        byte b = pDisMarker.readByte();
        this.Tc = (b & 0xF0) >> 4;    // TODO: Check this value
        this.Th = (b & 0x0F);         // TODO: Check this value

        // Li
        for (int i = 0; i < 16; i++) {
            this.Li[i] = pDisMarker.readUnsignedByte();
        }

        // Vij
        this.Vij = new FileComponenPlaceHolder(pDisMarker, super.marker_length - 19);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods

    /**
     *
     * @param parentNode
     */
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

        // Tc, Th
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                String.format("Tc: %d", this.Tc))));
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos + 4,
                1,
                String.format("Th: %d", this.Th))));

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
