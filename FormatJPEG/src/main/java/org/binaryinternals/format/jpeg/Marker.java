/*
 * MarkerCode.java    August 27, 2010, 22:49
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Super class of the JPEG markers: a marker code, and the segment length for the markers with a segment.
///
/// @author Amos Shi
public class Marker extends FileComponent implements GenerateTreeNode {

    private static final int IDENTIFIER_LENGTH_MAX = 100;
    /// The marker code, see [MarkerCode].
    @SuppressWarnings("ProtectedField")
    protected final int marker_code;
    /// The segment length, the length field included; `0` for the standalone markers.
    @SuppressWarnings("ProtectedField")
    protected final int marker_length;
    /// Why the marker content cannot be parsed, or `null`.
    private String error;

    Marker(final PosDataInputStream pDIS, int markerCode) throws IOException {

        // The marker is not valid
        if (!MarkerCode.isValid(markerCode)) {
            throw new FileFormatException("Non-valid marker found. marker code = %X (%d).".formatted(markerCode, markerCode));
        }

        // Start Position of this Marker
        super.startPos = pDIS.getPos() - MarkerCode.MARKER_CODE_BYTES_COUNT;

        // Marker Code
        this.marker_code = markerCode;

        // Marker Length
        if (MarkerCode.isLengthAvailable(markerCode)) {
            final int remaining = pDIS.getBuf().length - (pDIS.getPos() - pDIS.getOffset());
            if (remaining < MarkerCode.MARKER_LENGTH_BYTES_COUNT) {
                this.marker_length = 0;
                pDIS.skipNBytes(remaining);
                super.length = MarkerCode.MARKER_CODE_BYTES_COUNT + remaining;
                this.error = "The file ends inside the marker length";
                return;
            }

            // The length counts itself, but not the marker code
            this.marker_length = pDIS.readUnsignedShort();
            final int dataLength = Math.clamp(this.marker_length - MarkerCode.MARKER_LENGTH_BYTES_COUNT, 0,
                    remaining - MarkerCode.MARKER_LENGTH_BYTES_COUNT);
            pDIS.skipNBytes(dataLength);
            super.length = MarkerCode.MARKER_CODE_BYTES_COUNT + MarkerCode.MARKER_LENGTH_BYTES_COUNT + dataLength;
            if (this.marker_length < MarkerCode.MARKER_LENGTH_BYTES_COUNT) {
                this.error = "Invalid marker length %d, at least 2 is expected".formatted(this.marker_length);
            } else if (dataLength < this.marker_length - MarkerCode.MARKER_LENGTH_BYTES_COUNT) {
                this.error = "The file ends inside the marker, %d bytes are missing".formatted(
                        this.marker_length - MarkerCode.MARKER_LENGTH_BYTES_COUNT - dataLength);
            }
        } else {
            this.marker_length = 0;
            super.length = MarkerCode.MARKER_CODE_BYTES_COUNT;
        }
    }

    /// A generic view of `other`, used when `other` failed to parse.
    ///
    /// @param other The marker
    /// @param error Why `other` could not be parsed
    Marker(final Marker other, final String error) {
        super(other.startPos, other.length);
        this.marker_code = other.marker_code;
        this.marker_length = other.marker_length;
        this.error = error;
    }

    /// Why the marker content cannot be parsed, or `null`.
    ///
    /// @return The error, or `null` if the marker is fine
    String getError() {
        return this.error;
    }

    /// Parses the marker segment content, nothing by default.
    ///
    /// @param pDisMarker Input stream of the marker segment
    /// @throws IOException if the stream cannot be read
    protected void parse(final PosDataInputStream pDisMarker) throws IOException {
    }

    /// Skips the marker code and the segment length.
    ///
    /// @param pDisMarker Input stream of the marker segment
    /// @throws IOException if the stream cannot be read
    protected void parseInitSkip(final PosDataInputStream pDisMarker) throws IOException {
        // Skip the marker code and length field
        long skip = MarkerCode.MARKER_CODE_BYTES_COUNT + MarkerCode.MARKER_LENGTH_BYTES_COUNT;
        pDisMarker.skipNBytes(skip);
    }

    /// Reads the identifier of an application segment: a NUL terminated ASCII text.
    ///
    /// @param pDisMarker Input stream of the marker segment, positioned at the identifier
    /// @return The identifier, without the NUL
    /// @throws IOException if the stream cannot be read
    protected String parseIdentifier(final PosDataInputStream pDisMarker) throws IOException {

        // Parse the Identifier, an '\000' ended ASCII string
        final StringBuilder sb = new StringBuilder(IDENTIFIER_LENGTH_MAX);
        while (sb.length() <= IDENTIFIER_LENGTH_MAX) {
            byte b = pDisMarker.readByte();
            if (b == 0) {
                break;
            }
            sb.append((char) Byte.toUnsignedInt(b));
        }
        return sb.toString();
    }

    // Get each field
    /// Gets the marker code.
    ///
    /// @return The marker code
    public int getMarker() {
        return this.marker_code;
    }

    /// Gets the marker name.
    ///
    /// @return The marker name
    public String getMarkerName() {
        return MarkerCode.getMarkerName(this.marker_code);
    }

    /// Gets the marker description.
    ///
    /// @return The marker description
    public String getMarkerDescription() {
        return MarkerCode.getMarkerDescription(this.marker_code);
    }

    /// Gets the segment length.
    ///
    /// @return The segment length, the length field included
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
                    "Marker code = 0x%X".formatted(this.getMarker()));
            comp.setDescription(MarkerCode.getMarkerCodeDescription());
            markerNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.getStartPos() + 2,
                    2,
                    "length = %d".formatted(this.getMarkerLength()));
            markerNode.add(new DefaultMutableTreeNode(comp));

            final int dataLength = this.getLength() - MarkerCode.MARKER_CODE_BYTES_COUNT - MarkerCode.MARKER_LENGTH_BYTES_COUNT;
            if (dataLength > 0 || this.error != null) {
                comp = new JTreeNodeFileComponent(
                        this.getStartPos() + 4,
                        Math.max(dataLength, 0),
                        "marker data");
                if (this.error != null) {
                    comp.setDescription(this.error);
                }
                markerNode.add(new DefaultMutableTreeNode(comp));
            }
        } else {
            this.generateTreeNode_Marker(parentNode);
        }
    }

    DefaultMutableTreeNode generateTreeNode_Marker(DefaultMutableTreeNode root) {

        JTreeNodeFileComponent node = new JTreeNodeFileComponent(
                this.getStartPos(),
                this.getLength(),
                this.getMarkerName());
        node.setDescription(this.getMarkerDescription());
        final DefaultMutableTreeNode markerNode = new DefaultMutableTreeNode(node);
        root.add(markerNode);

        return markerNode;
    }
}
