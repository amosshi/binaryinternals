/*
 * Marker_APP02.java    August 25, 2010, 23:09
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.jpeg.icc.ICCProfile;

/**
 *
 * @author Amos Shi
 */
public class Marker_APP02 extends Marker {

    public static final String identifier_ICC_PROFILE = "ICC_PROFILE";
    private String identifier;
    private ICCProfile icc;

    Marker_APP02(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);

        if (Marker_APP02.identifier_ICC_PROFILE.equals(this.identifier)) {
            int lengthICC = this.marker_length - 2 - this.identifier.length() - 1;
            lengthICC = lengthICC - 2;
            final byte[] bytesICC = new byte[lengthICC];
            System.arraycopy(pDisMarker.getBuf(), 2 + 2 + this.identifier.length() + 1 + 2, bytesICC, 0, lengthICC);
            this.icc = new ICCProfile(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesICC),
                    this.getStartPos() + 2 + 2 + this.identifier.length() + 1 + 2));
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

        if (Marker_APP02.identifier_ICC_PROFILE.equals(this.identifier)) {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + this.identifier.length() + 1,
                    2,
                    "????")));
            
            markerNode.add(identifierNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + 2,
                    this.startPos + this.length - lastPos,
                    this.identifier)));
            this.icc.generateTreeNode(identifierNode);
        } else {
            markerNode.add(identifierNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + this.identifier.length() + 1,
                    this.startPos + this.length - lastPos,
                    this.identifier)));
        }

    } // End of method generateTreeNode
}
