/*
 * Marker_DRI.java    August 27, 2010, 23:50
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class Marker_DRI extends Marker {

    private int Lr;
    private int Ri;

    Marker_DRI(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
        super.parseInitSkip(pDisMarker);
        this.Lr = super.marker_length;
        this.Ri = pDisMarker.readUnsignedShort();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        int lastPos;
        JTreeNodeFileComponent comp;

        // Marker code
        comp = new JTreeNodeFileComponent(
                lastPos = this.getStartPos(),
                2,
                String.format("Marker code: 0x04X", this.marker_code));
        comp.setDescription(MarkerCode.getMarkerCodeDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        // Marker Length
        comp = new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                2,
                String.format("length: %d", this.Lr));
        comp.setDescription(MarkerCode.getHeaderLengthDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        // Ri
        comp = new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                2,
                String.format("Ri: %d", this.Ri));
        comp.setDescription("Restart interval – Specifies the number of MCU in the restart interval.");
        markerNode.add(new DefaultMutableTreeNode(comp));
    }
}
