/*
 * Header.java    Nov 09, 2010, 21:41
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
public class Header extends FileComponent implements GenerateTreeNode {

    public static final int LENGTH = 128;
    public final long ProfileSize;                                              //  0 -  3
    public final String CMMType;                                                //  4 -  7
    public final String ProfileVersion;                                         //  8 - 11
    public final String ProfileDeviceClass;                                     // 12 - 15
    public final String DataColourSpace;                                        // 16 - 19
    public final String ProfileConnectionSpace;                                 // 20 - 23
    public final dateTimeNumber DateTime;                                       // 24 - 35
    public final String ProfileFileSignature;                                   // 36 - 39
    public final String PrimaryPlatform;                                        // 40 - 43
    public final long ProfileFlags;                                             // 44 - 47
    public final String DeviceManufacturer;                                     // 48 - 51
    public final String DeviceModel;                                            // 52 - 55
    public final long[] DeviceAttributes;                                       // 56 - 63
    public final long RenderingIntent;                                          // 64 - 67
    public final XYZNumber ProfileConnectionSpaceIlluminant;                    // 68 - 79
    public final String ProfileCreator;                                         // 80 - 83
    public final byte[] ProfileID = new byte[16];                               // 84 - 99
    public final byte[] Reserved = new byte[28];                                // 100-127

    public Header(final PosDataInputStream input) throws IOException {
        StringBuilder sb;

        super.startPos = input.getPos();
        super.length = Header.LENGTH;

        this.ProfileSize = input.readUnsignedInt();                             // 0 - 3
        this.CMMType = input.readASCII(4);                                      // 4 - 7

        // 8 - 11
        sb = new StringBuilder(8);
        sb.append(input.readByte());
        sb.append('.');
        sb.append(input.readByte());
        sb.append('.');
        sb.append(input.readByte());
        sb.append('.');
        sb.append(input.readByte());
        this.ProfileVersion = sb.toString();

        this.ProfileDeviceClass = input.readASCII(4);                           // 12 - 15
        this.DataColourSpace = input.readASCII(4);                              // 16 - 19
        this.ProfileConnectionSpace = input.readASCII(4);                       // 20 - 23
        this.DateTime = new dateTimeNumber(input);                              // 24 - 35
        this.ProfileFileSignature = input.readASCII(4);                         // 36 - 39
        this.PrimaryPlatform = input.readASCII(4);                              // 40 - 43
        this.ProfileFlags = input.readUnsignedInt();                            // 44 - 47 - TODO: Analysis known flags
        this.DeviceManufacturer = input.readASCII(4);                           // 48 - 51
        this.DeviceModel = input.readASCII(4);                                  // 52 - 55

        // 56 - 63 - TODO: Analysis known flags
        this.DeviceAttributes = new long[2];
        this.DeviceAttributes[0] = input.readUnsignedInt();
        this.DeviceAttributes[1] = input.readUnsignedInt();

        this.RenderingIntent = input.readUnsignedInt();                         // 64 - 67 - TODO: Analysis known flags
        this.ProfileConnectionSpaceIlluminant = new XYZNumber(input);           // 68 - 79
        this.ProfileCreator = input.readASCII(4);                               // 80 - 83
        input.readFully(this.ProfileID);                                        // 84 - 99
        input.readFully(this.Reserved);                                         // 100-127
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        // 0 - 3
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 0,
                4,
                "Profile size = %d".formatted(this.ProfileSize));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 4 - 7
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                4,
                "CMM Type = %s".formatted(this.CMMType));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 8 - 11
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 8,
                4,
                "Profile version = %s".formatted(this.ProfileVersion));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 12 - 15
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 12,
                4,
                "Profile/Device class = %s".formatted(this.ProfileDeviceClass));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 16 - 19
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 16,
                4,
                "Data colour space = %s".formatted(this.DataColourSpace));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 20 - 23
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 20,
                4,
                "Profile connection space = %s".formatted(this.ProfileConnectionSpace));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 24 - 35
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 24,
                12,
                "Date and Time = %s".formatted(this.DateTime.toString()));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 36 - 39
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 36,
                4,
                "Profile file signature = %s".formatted(this.ProfileFileSignature));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 40 - 43
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 40,
                4,
                "Primary platform = %s".formatted(this.PrimaryPlatform));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 44 - 47
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 44,
                4,
                "Profile flags = %s".formatted(Long.toHexString(this.ProfileFlags)));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 48 - 51
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 48,
                4,
                "Device manufacturer = %s".formatted(this.DeviceManufacturer));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 52 - 55
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 52,
                4,
                "Device model = %s".formatted(this.DeviceModel));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 56 - 63
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 56,
                8,
                "Device attributes");
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 64 - 67
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 64,
                4,
                "Rendering intent");
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 68 - 79
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 68,
                12,
                "Profile connection space illuminant");
        parentNode.add(node = new DefaultMutableTreeNode(comp));
        this.ProfileConnectionSpaceIlluminant.generateTreeNode(node, this.getStartPos() + 68);

        // 80 - 83
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 80,
                4,
                "Device model = %s".formatted(this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 84 - 99
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 80,
                4,
                "Profile ID = %s".formatted(this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 100 - 127
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 80,
                4,
                "Reserved = %s".formatted(this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));
    }
}
