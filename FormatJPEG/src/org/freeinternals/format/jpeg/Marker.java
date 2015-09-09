/*
 * MarkerCode.java    August 27, 2010, 22:49
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class Marker extends FileComponent implements GenerateTreeNode {

    private static final int IDENTIFIER_LENGTH_MAX = 100;
    @SuppressWarnings("ProtectedField")
    protected final int marker_code;
    @SuppressWarnings("ProtectedField")
    protected int marker_length;

    Marker(final PosDataInputStream pDIS, int markerCode) throws IOException, FileFormatException {

        // The marker is not valid
        if (MarkerCode.isValid(markerCode) == false) {
            throw new FileFormatException(String.format(
                    "Non-valid marker found. marker code = %X (%d).",
                    markerCode,
                    markerCode));
        }

        // Start Position of this Marker
        super.startPos = pDIS.getPos() - 2;

        // Marker Code
        this.marker_code = markerCode;

        // Marker Length
        if (MarkerCode.isLengthAvailable(markerCode)) {
            this.marker_length = pDIS.readUnsignedShort();
            super.length = this.marker_length + 2;
            pDIS.skip(this.marker_length - 2);
        } else {
            this.marker_length = 0;
            super.length = MarkerCode.MARKER_CODE_BYTES_COUNT;
        }
    }

    protected void parse(final PosDataInputStream pDisMarker) throws IOException, FileFormatException {
    }

    protected void parseInitSkip(final PosDataInputStream pDisMarker) throws IOException{
        // Skip the marker code and length field
        pDisMarker.skip(MarkerCode.MARKER_CODE_BYTES_COUNT);
        pDisMarker.skip(MarkerCode.MARKER_LENGTH_BYTES_COUNT);
    }

    protected String parseIdentifier(final PosDataInputStream pDisMarker) throws IOException{

        // Parse the Identifier, an '\000' ended ASCII string
        final StringBuffer sb = new StringBuffer(IDENTIFIER_LENGTH_MAX);
        while (sb.length() <= IDENTIFIER_LENGTH_MAX) {
            byte b = pDisMarker.readByte();
            if (b == 0) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get each field
    public int getMarker() {
        return this.marker_code;
    }

    public String getMarkerName() {
        return MarkerCode.getMarkerName(this.marker_code);
    }

    public String getMarkerDescription() {
        return MarkerCode.getMarkerDescription(this.marker_code);
    }

    public int getMarkerLength() {
        return this.marker_length;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (MarkerCode.isLengthAvailable(this.marker_code)) {
            DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.getStartPos(),
                    2,
                    String.format("Marker code = 0x%X", this.getMarker()));
            comp.setDescription(MarkerCode.getMarkerCodeDescription());
            markerNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 2,
                    2,
                    String.format("length = %d", this.getMarkerLength()));
            markerNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 4,
                    this.getMarkerLength() - 2,
                    "marker data");
            markerNode.add(new DefaultMutableTreeNode(comp));
        } else {
            this.generateTreeNode_Marker(parentNode);
        }
    }

    DefaultMutableTreeNode generateTreeNode_Marker(DefaultMutableTreeNode root) {
        DefaultMutableTreeNode markerNode;

        JTreeNodeFileComponent node = new JTreeNodeFileComponent(
                this.getStartPos(),
                this.getLength(),
                this.getMarkerName());
        node.setDescription(this.getMarkerDescription());
        root.add(markerNode = new DefaultMutableTreeNode(node));

        return markerNode;
    }
}
