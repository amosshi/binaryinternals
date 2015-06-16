/*
 * Header.java    Nov 09, 2010, 21:41
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.icc;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
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
                String.format("Profile size = %d", this.ProfileSize));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 4 - 7
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 4,
                4,
                String.format("CMM Type = %s", this.CMMType));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 8 - 11
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 8,
                4,
                String.format("Profile version = %s", this.ProfileVersion));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 12 - 15
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 12,
                4,
                String.format("Profile/Device class = %s", this.ProfileDeviceClass));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 16 - 19
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 16,
                4,
                String.format("Data colour space = %s", this.DataColourSpace));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 20 - 23
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 20,
                4,
                String.format("Profile connection space = %s", this.ProfileConnectionSpace));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 24 - 35
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 24,
                12,
                String.format("Date and Time = %s", this.DateTime.toString()));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 36 - 39
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 36,
                4,
                String.format("Profile file signature = %s", this.ProfileFileSignature));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 40 - 43
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 40,
                4,
                String.format("Primary platform = %s", this.PrimaryPlatform));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 44 - 47
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 44,
                4,
                String.format("Profile flags = %s", Long.toHexString(this.ProfileFlags)));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 48 - 51
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 48,
                4,
                String.format("Device manufacturer = %s", this.DeviceManufacturer));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 52 - 55
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 52,
                4,
                String.format("Device model = %s", this.DeviceModel));
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
                String.format("Device model = %s", this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 84 - 99
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 80,
                4,
                String.format("Profile ID = %s", this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // 100 - 127
        comp = new JTreeNodeFileComponent(
                this.getStartPos() + 80,
                4,
                String.format("Reserved = %s", this.ProfileCreator));
        parentNode.add(new DefaultMutableTreeNode(comp));
    }
}
