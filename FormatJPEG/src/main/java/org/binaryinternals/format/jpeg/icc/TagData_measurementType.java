/*
 * TagData_measurementType.java    Nov 27, 2010, 17:07
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// `measurementType`: the measurement conditions.
///
/// @author Amos Shi
public class TagData_measurementType extends TagData {

    /// Length of the tag data in bytes.
    public static final int LENGTH = 36;

    /// Standard observer encodings, ICC.1 `measurementType`.
    private static final String[] STANDARD_OBSERVERS = {
        "Unknown", "CIE 1931 standard colorimetric observer", "CIE 1964 standard colorimetric observer"};
    /// Measurement geometry encodings, ICC.1 `measurementType`.
    private static final String[] MEASUREMENT_GEOMETRIES = {"Unknown", "0°:45° or 45°:0°", "0°:d or d:0°"};
    /// Standard illuminant encodings, ICC.1 `measurementType`.
    private static final String[] STANDARD_ILLUMINANTS = {"Unknown", "D50", "D65", "D93", "F2", "D55", "A", "Equi-Power (E)", "F8"};
    /// Encoded value for the standard observer.
    public final long standardObserver;
    /// nCIEXYZ tristimulus values for the measurement backing.
    public final XYZNumber measurementBacking;
    /// Encoded value for the measurement geometry.
    public final long measurementGeometry;
    /// Encoded value for the measurement flare.
    public final long measurementFlare;
    /// Encoded value for the standard illuminant.
    public final long standardIlluminant;

    /// Reads the tag data.
    ///
    /// @param input Input stream of the file
    /// @throws IOException Parse file failed
    public TagData_measurementType(final PosDataInputStream input) throws IOException {
        super(input);
        if (super.length != LENGTH) {
            throw new FileFormatException("measurementType length is %d, expected %d.".formatted(super.length, LENGTH));
        }

        this.standardObserver = input.readUnsignedInt();
        this.measurementBacking = new XYZNumber(input);
        this.measurementGeometry = input.readUnsignedInt();
        this.measurementFlare = input.readUnsignedInt();
        this.standardIlluminant = input.readUnsignedInt();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.length > 8) {
            this.generateTreeNode_TagDataType(parentNode);

            JTreeNodeFileComponent comp;

            comp = new JTreeNodeFileComponent(
                    this.startPos + 8,
                    4,
                    "standard observer = %d (%s)".formatted(this.standardObserver, encoding(STANDARD_OBSERVERS, this.standardObserver)));
            comp.setDescription("Standard observer.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    startPos + 12,
                    XYZNumber.LENGTH,
                    "measurement backing");
            comp.setDescription("XYZ tristimulus values for measurement backing.");
            final DefaultMutableTreeNode xyzNode = new DefaultMutableTreeNode(comp);
            parentNode.add(xyzNode);
            this.measurementBacking.generateTreeNode(xyzNode, this.startPos + 12);

            comp = new JTreeNodeFileComponent(
                    this.startPos + 24,
                    4,
                    "measurement geometry = %d (%s)".formatted(this.measurementGeometry,
                            encoding(MEASUREMENT_GEOMETRIES, this.measurementGeometry)));
            comp.setDescription("Measurement geometry.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 28,
                    4,
                    "measurement flare = %.2f%% (0x%08X)".formatted(this.measurementFlare * 100.0 / 0x10000, this.measurementFlare));
            comp.setDescription("Measurement flare, a <code>u16Fixed16Number</code>: <code>0x00010000</code> is 100%.");
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    this.startPos + 32,
                    4,
                    "standard illuminant = %d (%s)".formatted(this.standardIlluminant,
                            encoding(STANDARD_ILLUMINANTS, this.standardIlluminant)));
            comp.setDescription("Standard illuminant.");
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            // For error case
            super.generateTreeNode(parentNode);
        }
    }

    /// Name of an encoded value, or `Unrecognized` when it is not in `names`.
    private static String encoding(final String[] names, final long value) {
        return (value >= 0 && value < names.length) ? names[(int) value] : "Unrecognized";
    }
}
