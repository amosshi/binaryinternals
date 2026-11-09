/*
 * Marker_APP01.java    August 25, 2010, 23:09
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.JXMLViewer;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.jpeg.tiff.TIFF;
import org.binaryinternals.format.jpeg.xmp.XMP;

/// `APP1` application segment, used by Exif and XMP.
///
/// @author Amos Shi
public class Marker_APP01 extends Marker {

    /// Identifier of an Exif segment.
    public static final String identifier_Exif = "Exif";
    /// Identifier of an XMP segment.
    public static final String identifier_XMP = "http://ns.adobe.com/xap/";
    private String identifier;
    private TIFF tiff;
    private XMP xmp;

    Marker_APP01(final PosDataInputStream pDIS, int marker_code) throws IOException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);

        if (this.identifier.equalsIgnoreCase(Marker_APP01.identifier_Exif)) {
            final int lengthTiff = this.marker_length - 2 - 6;
            final byte[] bytesTiff = Arrays.copyOfRange(pDisMarker.getBuf(), 2 + 2 + 6, 2 + 2 + 6 + lengthTiff);
            this.tiff = new TIFF(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesTiff),
                    this.getStartPos() + 2 + 2 + 6));
        } else if (this.identifier.startsWith(Marker_APP01.identifier_XMP)) {
            final int lengthXMP = this.marker_length - 2 - this.identifier.length() - 1;
            final int startXMP = 2 + 2 + this.identifier.length() + 1;
            final byte[] bytesXMP = Arrays.copyOfRange(pDisMarker.getBuf(), startXMP, startXMP + lengthXMP);
            this.xmp = new XMP(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesXMP),
                    this.getStartPos() + 2 + 2 + this.identifier.length() + 1));
        }
    }

    // Interface Methods
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                this.getStartPos(),
                2,
                "Marker code: 0xFFE1");
        comp.setDescription(MarkerCode.getMarkerCodeDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 2,
                2,
                "length: %d".formatted(this.getMarkerLength()));
        comp.setDescription(MarkerCode.getHeaderLengthDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        if (Marker_APP01.identifier_Exif.equals(this.identifier)) {
            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 4,
                    6,
                    "identifier: %s".formatted(this.identifier));
            markerNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 10,
                    this.length - 10,
                    "TIFF");
            comp.setDescription("Exchangeable image file format for digital still cameras");
            final DefaultMutableTreeNode nodeExif = new DefaultMutableTreeNode(comp);
            markerNode.add(nodeExif);
            this.tiff.generateTreeNode(nodeExif);
        } else if (this.identifier.startsWith(Marker_APP01.identifier_XMP)) {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.getStartPos() + 4,
                    this.identifier.length() + 1,
                    "identifier: %s".formatted(this.identifier))));

            comp = new JTreeNodeFileComponent(
                    this.xmp.getStartPos(),
                    this.xmp.getLength(),
                    "XMP data");
            comp.setDetailPanel(new JXMLViewer(this.xmp.rawData));
            markerNode.add(new DefaultMutableTreeNode(comp));
        } else {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.getStartPos() + 2 + 2,
                    this.marker_length - 2,
                    "data")));
        }
    } // End of method generateTreeNode
} // End of Class

