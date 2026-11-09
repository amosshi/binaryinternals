/*
 * Marker_APP14.java    August 25, 2010, 23:27
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// `APP14` application segment, used by Adobe.
///
/// @author Amos Shi
public class Marker_APP14 extends Marker {

    private String identifier;

    Marker_APP14(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);
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
        final DefaultMutableTreeNode identifierNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + this.identifier.length() + 1,
                this.startPos + this.length - lastPos,
                this.identifier));
        markerNode.add(identifierNode);
    } // End of method generateTreeNode
}
