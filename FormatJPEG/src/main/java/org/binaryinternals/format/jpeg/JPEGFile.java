/*
 * JPEGFile.java    August 21, 2010, 21:30
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// JPEG file: a sequence of markers, with the compressed data between them.
///
/// A marker whose content cannot be parsed is shown as a plain marker, so a
/// broken APP segment does not hide the rest of the file.
///
/// @author Amos Shi
public final class JPEGFile extends FileFormat {

    private static final Logger LOGGER = Logger.getLogger(JPEGFile.class.getName());

    public JPEGFile(final Path file) throws IOException {
        super(file);
        this.parse();
    }

    private void parse() throws IOException {
        final PosDataInputStream posDataInputStream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // Marker - SOI
        final int soi = posDataInputStream.readUnsignedShort();
        if (MarkerCode.SOI != soi) {
            throw new FileFormatException(
                    "File is not started with JPEG SOI. Expected value %x, current value %x.".formatted(MarkerCode.SOI, soi));
        }
        this.addFileComponent(new Marker_SOI(posDataInputStream, soi));

        // Markers & File Data
        boolean isCompressedData = false;
        while (posDataInputStream.getPos() < (this.fileByteArray.length - 1)) {
            if (MarkerCode.isValid(getMarkerCode(this.fileByteArray, posDataInputStream.getPos()))) {
                final Marker marker = MarkerParse.parse(posDataInputStream);
                this.addFileComponent(marker);
                isCompressedData = MarkerCode.isCompressedDataFollowed(marker.marker_code);
            } else {
                // Data until the next marker
                int next = posDataInputStream.getPos() + 1;
                while (next < this.fileByteArray.length - 1 && !MarkerCode.isValid(getMarkerCode(this.fileByteArray, next))) {
                    next++;
                }
                if (next >= this.fileByteArray.length - 1) {
                    next = this.fileByteArray.length;
                }
                this.addFileComponent(new FileData(posDataInputStream, next - posDataInputStream.getPos(), isCompressedData));
            }
        }
        // A last byte, too short for a marker
        if (posDataInputStream.getPos() < this.fileByteArray.length) {
            this.addFileComponent(new FileData(posDataInputStream, this.fileByteArray.length - posDataInputStream.getPos(), isCompressedData));
        }

        // Parse each Marker, on a copy since a failed one is replaced
        for (final FileComponent comp : List.copyOf(super.components.values())) {
            if (comp instanceof Marker marker && marker.getMarkerLength() > 0) {
                this.parseMarker(marker);
            }
        }
    }

    /// Parse the content of `marker`, or replace it with a plain marker if
    /// that fails.
    private void parseMarker(final Marker marker) {
        String error = marker.getError();
        if (error == null) {
            try {
                marker.parse(new PosDataInputStream(
                        new PosByteArrayInputStream(this.getFileByteArray(marker.getStartPos(), marker.getLength())),
                        marker.getStartPos()));
                return;
            } catch (IOException | RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Failed to parse marker %s at %d".formatted(marker.getMarkerName(), marker.getStartPos()), ex);
                error = "Failed to parse the marker: " + ex.getMessage();
            }
        }
        this.addFileComponent(new Marker(marker, error));
    }

    private static int getMarkerCode(final byte[] fileByteArray, final int markerOffset) {
        return ((fileByteArray[markerOffset] & 0xFF) << 8) | (fileByteArray[markerOffset + 1] & 0xFF);
    }

    @Override
    public String getContentTabName() {
        return "JPEG File";
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode root) {
        for (final FileComponent comp : super.components.values()) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(root);
            }
        }
    }
}
