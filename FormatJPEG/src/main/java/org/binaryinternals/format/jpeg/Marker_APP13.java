/*
 * Marker_APP13.java    August 25, 2010, 23:27
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.jpeg.ps.PhotoshopImageResource;

/// An APP13 marker designates a Photoshop Image Resource (PSIR) that contains IPTC metadata.
///
/// @author Amos Shi
public class Marker_APP13 extends Marker {

    /// Identifier of a Photoshop image resources segment.
    public static final String IDENTIFIER_PHOTOSHOP = "Photoshop";
    private String identifier;
    private PhotoshopImageResource psir;

    Marker_APP13(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);
        if (this.identifier.contains(Marker_APP13.IDENTIFIER_PHOTOSHOP)) {
            final int lengthPhir = this.marker_length - 2 - this.identifier.length() - 1;
            final int startPhir = 2 + 2 + this.identifier.length() + 1;
            final byte[] bytesPhir = Arrays.copyOfRange(pDisMarker.getBuf(), startPhir, startPhir + lengthPhir);
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

        comp = new JTreeNodeFileComponent(
                lastPos = this.getStartPos(),
                2,
                "Marker code: 0x%04X".formatted(this.marker_code));
        comp.setDescription(MarkerCode.getMarkerCodeDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                2,
                "length: %d".formatted(this.getMarkerLength()));
        comp.setDescription(MarkerCode.getHeaderLengthDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                this.identifier.length() + 1,
                "identifier: %s".formatted(this.identifier))));

        if (this.identifier.contains(Marker_APP13.IDENTIFIER_PHOTOSHOP)) {
            comp = new JTreeNodeFileComponent(
                    this.psir.getStartPos(),
                    this.psir.getLength(),
                    "Photoshop Image Resource Block");
            final DefaultMutableTreeNode identifierNode = new DefaultMutableTreeNode(comp);
            markerNode.add(identifierNode);
            this.psir.generateTreeNode(identifierNode);
        } else {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + this.identifier.length() + 1,
                    this.startPos + this.length - lastPos,
                    this.identifier)));
        }
    } // End of method generateTreeNode
}
