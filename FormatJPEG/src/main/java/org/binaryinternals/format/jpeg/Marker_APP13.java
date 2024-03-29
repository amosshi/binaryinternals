/*
 * Marker_APP13.java    August 25, 2010, 23:27
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.jpeg.ps.PhotoshopImageResource;

/**
 * An APP13 marker designates a Photoshop Image Resource (PSIR) that contains IPTC metadata.
 *
 * @author Amos Shi
 */
public class Marker_APP13 extends Marker {

    public static final String IDENTIFIER_PHOTOSHOP = "Photoshop";
    private String identifier;
    private PhotoshopImageResource psir;

    Marker_APP13(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException, FileFormatException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);
        if (this.identifier.contains(Marker_APP13.IDENTIFIER_PHOTOSHOP)) {
            final int lengthPhir = this.marker_length - 2 - this.identifier.length() - 1;
            final byte[] bytesPhir = new byte[lengthPhir];
            System.arraycopy(pDisMarker.getBuf(), 2 + 2 + this.identifier.length() + 1, bytesPhir, 0, lengthPhir);
            this.psir = new PhotoshopImageResource(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesPhir),
                    this.getStartPos() + 2 + 2 + this.identifier.length() + 1));
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        int lastPos;
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode identifierNode;

        comp = new JTreeNodeFileComponent(
                lastPos = this.getStartPos(),
                2,
                String.format("Marker code: 0x04X", this.marker_code));
        comp.setDescription(MarkerCode.getMarkerCodeDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                2,
                String.format("length: %d", this.getMarkerLength()));
        comp.setDescription(MarkerCode.getHeaderLengthDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                this.identifier.length() + 1,
                String.format("identifier: %s", this.identifier))));

        if (this.identifier.contains(Marker_APP13.IDENTIFIER_PHOTOSHOP)) {
            comp = new JTreeNodeFileComponent(
                    this.psir.getStartPos(),
                    this.psir.getLength(),
                    "Photoshop Image Resource Block");
            markerNode.add(identifierNode = new DefaultMutableTreeNode(comp));
            this.psir.generateTreeNode(identifierNode);
        } else {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + this.identifier.length() + 1,
                    this.startPos + this.length - lastPos,
                    this.identifier)));
        }
    } // End of method generateTreeNode
}
