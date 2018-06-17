/*
 * Marker_APP00.java    August 25, 2010, 23:09
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
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class Marker_APP00 extends Marker {

    public static final String identifier_JFIF = "JFIF";
    private String identifier;
    private GenerateTreeNode compnent;

    Marker_APP00(final PosDataInputStream pDIS, int marker_code) throws IOException, FileFormatException {
        super(pDIS, marker_code);
    }

    @Override
    protected void parse(final PosDataInputStream pDisMarker) throws IOException, FileFormatException {
        super.parseInitSkip(pDisMarker);
        this.identifier = super.parseIdentifier(pDisMarker);

        if (Marker_APP00.identifier_JFIF.equals(this.identifier)) {
            this.compnent = new JFIF(pDisMarker);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
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
        comp.setDescription("Total APP0 field byte count, including the byte count value (2 bytes), but excluding the APP0 marker itself.");
        markerNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                this.identifier.length() + 1,
                String.format("identifier: %s", this.identifier));
        if (this.identifier.equals(Marker_APP00.identifier_JFIF)) {
            comp.setDescription("This zero terminated string (“JFIF”) uniquely identifies this APP0 marker. This string shall have zero parity (bit 7=0).");
        }
        markerNode.add(new DefaultMutableTreeNode(comp));

        markerNode.add(identifierNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + this.identifier.length() + 1,
                this.startPos + this.length - lastPos,
                this.identifier)));
        if (this.compnent != null) {
            this.compnent.generateTreeNode(identifierNode);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Inner Class
    private class JFIF implements GenerateTreeNode {

        private final int version_major;
        private final int version_minor;
        private final int units;
        private final int Xdensity;
        private final int Ydensity;
        private final int Xthumbnail;
        private final int Ythumbnail;
        private final FileComponenPlaceHolder RGBn;

        JFIF(final PosDataInputStream pDIS) throws IOException, FileFormatException {
            // Version
            this.version_major = pDIS.readUnsignedByte();
            this.version_minor = pDIS.readUnsignedByte();

            // Units
            this.units = pDIS.readUnsignedByte();

            // X,Y density
            this.Xdensity = pDIS.readUnsignedShort();
            this.Ydensity = pDIS.readUnsignedShort();

            // X,Y thumbnail and RGBs
            this.Xthumbnail = pDIS.readUnsignedByte();
            this.Ythumbnail = pDIS.readUnsignedByte();
            final int thumbnail_size = this.Xthumbnail * this.Ythumbnail;
            this.RGBn = new FileComponenPlaceHolder(pDIS, thumbnail_size * 3);
        }

        public void generateTreeNode(DefaultMutableTreeNode parentNode) {

            int pos = Marker_APP00.this.getStartPos();
            JTreeNodeFileComponent comp;

            // Marker_APP00 Version Major and Minor
            comp = new JTreeNodeFileComponent(
                    pos + 9,
                    1,
                    String.format("major version: %d", this.version_major));
            comp.setDescription("The most significant byte is used for major revisions.");
            parentNode.add(new DefaultMutableTreeNode(comp));
            comp = new JTreeNodeFileComponent(
                    pos + 0xA,
                    1,
                    String.format("minor version: %d", this.version_minor));
            comp.setDescription("The least significant byte for minor revisions.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            // Marker_APP00 Density Units
            comp = new JTreeNodeFileComponent(
                    pos + 0xB,
                    1,
                    String.format("units: %d - %s", this.units, this.getUnitsDesc()));
            comp.setDescription("Units for the X and Y densities.<br />"
                    + "<ul>"
                    + "<li> units = <strong>0</strong>: no units, X and Y specify the pixel aspect ratio </li>"
                    + "<li> units = <strong>1</strong>: X and Y are dots per inch </li>"
                    + "<li> units = <strong>2</strong>: X and Y are dots per cm </li>"
                    + "</ul>");
            parentNode.add(new DefaultMutableTreeNode(comp));

            // Marker_APP00 X,Y density
            comp = new JTreeNodeFileComponent(
                    pos + 0xC,
                    2,
                    String.format("X density: %d", this.Xdensity));
            comp.setDescription("Horizontal pixel density");
            parentNode.add(new DefaultMutableTreeNode(comp));
            comp = new JTreeNodeFileComponent(
                    pos + 0xE,
                    2,
                    String.format("Y density: %d", this.Ydensity));
            comp.setDescription("Vertical pixel density");
            parentNode.add(new DefaultMutableTreeNode(comp));

            // Marker_APP00 X,Y thumbnail
            comp = new JTreeNodeFileComponent(
                    pos + 0x10,
                    1,
                    String.format("X thumbnail: %d", this.Xthumbnail));
            comp.setDescription("Thumbnail horizontal pixel count");
            parentNode.add(new DefaultMutableTreeNode(comp));
            comp = new JTreeNodeFileComponent(
                    pos + 0x11,
                    1,
                    String.format("Y thumbnail: %d", this.Ythumbnail));
            comp.setDescription("Thumbnail vertical pixel count");
            parentNode.add(new DefaultMutableTreeNode(comp));

            // RGBn
            comp = new JTreeNodeFileComponent(
                    pos + 0x12,
                    this.RGBn.getLength(),
                    String.format("RGBn: length = %d", this.RGBn.getLength()));
            comp.setDescription("Packed (24-bit) RGB values for the thumbnail pixels, <br />n = Xthumbnail * Ythumbnail");
            parentNode.add(new DefaultMutableTreeNode(comp));
        }

        public String getUnitsDesc() {
            switch (this.units) {
                case 0:
                    return "no units, X and Y specify the pixel spect ratio";
                case 1:
                    return "X and Y are dots per inch";
                case 2:
                    return "X and Y are dots per cm";
                default:
                    return "Unknown";
            }
        }
    }
}
