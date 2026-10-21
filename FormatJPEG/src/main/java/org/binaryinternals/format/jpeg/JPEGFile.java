/*
 * JPEGFile.java    August 21, 2010, 21:30
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 *
 * @author Amos Shi
 */
public class JPEGFile extends FileFormat {

    public JPEGFile(final Path file) throws IOException {
        super(file);
        this.parse();
    }

    private void parse() throws IOException {
        Marker marker;
        PosDataInputStream posDataInputStream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // Marker - SOI
        final int soi = posDataInputStream.readUnsignedShort();
        if (MarkerCode.SOI != soi) {
            throw new FileFormatException(String.format(
                    "File is not started with JPEG SOI. Expected value %x, current value %x.",
                    MarkerCode.SOI, soi));
        }
        final Marker soiMarker = new Marker_SOI(posDataInputStream, soi);
        super.components.put(Long.valueOf(soiMarker.getStartPos()), soiMarker);

        // Markers & File Data
        boolean isCompressedData = false;
        boolean isNextMarkerFound;
        int i;
        int length;
        while (posDataInputStream.getPos() < (this.fileByteArray.length - 1)) {
            if (MarkerCode.isValid(getMarkerCode(this.fileByteArray, posDataInputStream.getPos()))) {
                marker = MarkerParse.parse(posDataInputStream);
                super.components.put(Long.valueOf(marker.getStartPos()), marker);
                isCompressedData = MarkerCode.isCompressedDataFollowed(marker.marker_code);
            } else {
                // find next marker
                isNextMarkerFound = false;
                for (i = posDataInputStream.getPos() + 1; i < this.fileByteArray.length - 1; i++) {
                    if (MarkerCode.isValid(getMarkerCode(this.fileByteArray, i))) {
                        isNextMarkerFound = true;
                        break;
                    }
                }
                // Determine the length
                if (isNextMarkerFound) {
                    length = i - posDataInputStream.getPos();
                } else {
                    length = this.fileByteArray.length - posDataInputStream.getPos();
                }
                // Add one component
                final FileData fileData = new FileData(posDataInputStream, length, isCompressedData);
                super.components.put(Long.valueOf(fileData.getStartPos()), fileData);
            }
        }

        // Parse each Marker
        final Iterator<FileComponent> iteratorMarker = super.components.values().iterator();
        FileComponent comp;
        byte[] markerByteArray;
        int markerByteArraySize;

        while (iteratorMarker.hasNext()) {
            comp = iteratorMarker.next();
            if (comp instanceof Marker) {
                marker = (Marker) comp;
                if (marker.getMarkerLength() > 0) {
                    markerByteArraySize = marker.getLength() + MarkerCode.MARKER_CODE_BYTES_COUNT;
                    markerByteArray = new byte[markerByteArraySize];
                    System.arraycopy(this.fileByteArray, marker.getStartPos(), markerByteArray, 0, markerByteArraySize);
                    try {
                        marker.parse(new PosDataInputStream(new PosByteArrayInputStream(markerByteArray), marker.getStartPos()));
                    } catch (IOException ex) {
                        System.out.println("JPEGFile.parse() - " + marker.getMarkerName() + " - " + ex.toString());
                    }
                }
            }
        }
    } // End method parse

    private static int getMarkerCode(byte[] fileByteArray, int markerOffset) {
        return ((fileByteArray[markerOffset] & 0x000000FF) << 8)
                + (fileByteArray[markerOffset + 1] & 0x000000FF);
    }

    @Override
    public String getContentTabName() {
        return "JPEG File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode root) {
        final Iterator<FileComponent> iterator = super.components.values().iterator();

        while (iterator.hasNext()) {
            FileComponent comp = iterator.next();
            if (comp instanceof GenerateTreeNode) {
                GenerateTreeNode generator = (GenerateTreeNode) comp;
                generator.generateTreeNode(root);
            }
        } // End while
    }
}
