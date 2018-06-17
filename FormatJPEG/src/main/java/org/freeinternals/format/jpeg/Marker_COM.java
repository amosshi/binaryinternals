/*
 * Marker_COM.java    August 25, 2010, 23:30
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
public class Marker_COM extends Marker {

    /** Comment byte. */
    private FileComponenPlaceHolder Cm;


    Marker_COM(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }


    @Override
    protected void parse(final PosDataInputStream pDisMarker)
            throws IOException, FileFormatException {

        super.parseInitSkip(pDisMarker);

        this.Cm = new FileComponenPlaceHolder(pDisMarker, super.getMarkerLength() - 2);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);

        // Marker_APP00 Marker
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos(),
                2,
                "Marker code: 0xFFE0")));

        // Marker_APP00 Length
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 2,
                2,
                String.format("length: %d", this.getMarkerLength()))));

        // Comment
        markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                this.Cm.getLength(),
                String.format("Cm: length = %d", this.Cm.getLength()))));

    }
}
