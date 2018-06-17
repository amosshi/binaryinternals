/*
 * Marker_APP01.java    August 25, 2010, 23:09
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
import org.freeinternals.commonlib.ui.JXMLViewer;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.jpeg.tiff.TIFF;
import org.freeinternals.format.jpeg.xmp.XMP;

/**
 *
 * @author Amos Shi
 */
public class Marker_APP01 extends Marker {

    public static final String identifier_Exif = "Exif";
    public static final String identifier_XMP = "http://ns.adobe.com/xap/";
    private String identifier;
    private TIFF tiff;
    private XMP xmp;

    Marker_APP01(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException, FileFormatException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);

        if (this.identifier.equalsIgnoreCase(Marker_APP01.identifier_Exif)) {
            // TODO - Clean the following code, call method 
            //        pDisMarker.getPartialStream(startPos, length) - This method test failed June.19.2013
            final int lengthTiff = this.marker_length - 2 - 6;
            final byte[] bytesTiff = new byte[lengthTiff];
            System.arraycopy(pDisMarker.getBuf(), 2 + 2 + 6, bytesTiff, 0, lengthTiff);
            this.tiff = new TIFF(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesTiff),
                    this.getStartPos() + 2 + 2 + 6));
            
//            pDisMarker.getPartialStream(this.getStartPos() + 2 + 2 + 6, lengthTiff);
//            this.tiff = new TIFF(pDisMarker.getPartialStream(
//                    this.getStartPos() + 2 + 2 + 6,
//                    lengthTiff));            
        } else if (this.identifier.startsWith(Marker_APP01.identifier_XMP)) {
            final int lengthXMP = this.marker_length - 2 - this.identifier.length() - 1;
            final byte[] bytesXMP = new byte[lengthXMP];
            System.arraycopy(pDisMarker.getBuf(), 2 + 2 + this.identifier.length() + 1, bytesXMP, 0, lengthXMP);
            this.xmp = new XMP(new PosDataInputStream(
                    new PosByteArrayInputStream(bytesXMP),
                    this.getStartPos() + 2 + 2 + this.identifier.length() + 1));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode markerNode = this.generateTreeNode_Marker(parentNode);
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode nodeExif;

        comp = new JTreeNodeFileComponent(
                this.getStartPos(),
                2,
                "Marker code: 0xFFE1");
        comp.setDescription(MarkerCode.getMarkerCodeDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 2,
                2,
                String.format("length: %d", this.getMarkerLength()));
        comp.setDescription(MarkerCode.getHeaderLengthDescription());
        markerNode.add(new DefaultMutableTreeNode(comp));

        if (Marker_APP01.identifier_Exif.equals(this.identifier)) {
            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 4,
                    6,
                    String.format("identifier: %s", this.identifier));
            markerNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 10,
                    this.length - 10,
                    "TIFF");
            comp.setDescription("Exchangeable image file format for digital still cameras");
            markerNode.add(nodeExif = new DefaultMutableTreeNode(comp));
            this.tiff.generateTreeNode(nodeExif);
        } else if (this.identifier.startsWith(Marker_APP01.identifier_XMP)) {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.getStartPos() + 4,
                    this.identifier.length() + 1,
                    String.format("identifier: %s", this.identifier))));

            comp = new JTreeNodeFileComponent(
                    this.xmp.getStartPos(),
                    this.xmp.getLength(),
                    "XMP data");
            comp.setDetailPanel(new JXMLViewer(new PosDataInputStream(new PosByteArrayInputStream(this.xmp.rawData))));
            markerNode.add(new DefaultMutableTreeNode(comp));
        } else {
            markerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.getStartPos() + 2 + 2,
                    this.marker_length - 2,
                    "data")));
        }
    } // End of method generateTreeNode
} // End of Class

