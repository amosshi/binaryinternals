/*
 * TIFF.java    Nov 06, 2010, 16:55
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

public class TIFF extends FileComponent implements GenerateTreeNode {

    public static final String IFD_Number_Description = "A 2-byte count of the number of directory entries (i.e., the number of fields)";
    private final byte[] tiffByteArray;
    private final TIFFHeader tiffHeader;
    final ArrayList<IFDGroup> exifGroup = new ArrayList<>();

    public TIFF(final PosDataInputStream pDisTiff)
            throws IOException {

        super.startPos = pDisTiff.getPos();
        super.length = 0; // Not appliable
        this.tiffByteArray = pDisTiff.getBuf();
        this.tiffHeader = new TIFFHeader(pDisTiff);

        // A next IFD pointing back to a parsed one would loop for ever
        final Set<Integer> parsed = new HashSet<>();
        IFDGroup group = new IFDGroup(this.tiffByteArray, this.tiffHeader, this.tiffHeader.offset_0ifd);
        parsed.add(group.offset);
        this.exifGroup.add(group);
        while (group.next != 0 && parsed.add(group.next)) {
            group = new IFDGroup(this.tiffByteArray, this.tiffHeader, group.next);
            this.exifGroup.add(group);
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {

        // TIFF Header
        final JTreeNodeFileComponent comp = new JTreeNodeFileComponent(
                this.startPos,
                TIFFHeader.SIZE,
                "TIFF Header");
        comp.setDescription("A TIFF file begins with an 8-byte image file header that points to an image file directory (IFD).");
        final DefaultMutableTreeNode nodeTiffHeader = new DefaultMutableTreeNode(comp);
        parentNode.add(nodeTiffHeader);
        this.tiffHeader.generateTreeNode(nodeTiffHeader);

        // TIFF Data, in the order of the offset
        final SortedMap<Integer, RefItem> sortedMap = new TreeMap<>();
        for (final IFDGroup group : this.exifGroup) {
            sortedMap.put(group.offset, new RefItem(group.offset, group.length, null, group));
            loadRefItem(group.ifd, sortedMap);
        }

        int lastEnd = this.tiffHeader.getStartPos() + TIFFHeader.SIZE;          // Absolute position
        for (final RefItem ref : sortedMap.values()) {
            final int diff = (this.startPos + ref.offset()) - lastEnd;
            if (diff > 0) {
                generateTreeNodeDiff(parentNode, lastEnd, diff, this.tiffByteArray, this.startPos);
            }

            if (ref.ifdgroup() != null) {
                ref.ifdgroup().generateTreeNode(parentNode);
            } else {
                parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.tiffHeader.getStartPos() + ref.offset(),
                        ref.length(),
                        "Reference of tag [%s] %04X.H (%d, %s)".formatted(
                                ref.ifd().getTagSpace(),
                                ref.ifd().ifd_tag,
                                ref.ifd().ifd_tag,
                                ref.ifd().getTagName()))));
            }

            lastEnd = this.startPos + ref.offset() + ref.length();
        }

        // In case, there is some extra space in the end
        final int diff = (this.tiffHeader.getStartPos() + this.tiffByteArray.length) - lastEnd;
        if (diff > 0) {
            generateTreeNodeDiff(parentNode, lastEnd, diff, this.tiffByteArray, this.startPos);
        }
    }

    private static void loadRefItem(final IFD[] ifds, final SortedMap<Integer, RefItem> sortedMap) {
        if (ifds == null) {
            return;
        }

        // Add Special Logic for IFD_0201_JPEGInterchangeFormat & IFD_0202_JPEGInterchangeFormatLength
        // Now we just assume these two IFD tags should be in the same exifGroup
        long jpegOffset = 0;
        long jpegLength = 0;
        IFD ifdJpegOffset = null;

        for (final IFD item : ifds) {
            switch (item) {
                case IFD_0201_JPEGInterchangeFormat format -> {
                    jpegOffset = format.getBitstreamOffset();
                    ifdJpegOffset = item;
                }
                case IFD_0202_JPEGInterchangeFormatLength formatLength -> jpegLength = formatLength.getBitstreamLength();
                default -> {
                    // Not the JPEG thumbnail
                }
            }

            if (item instanceof IFD_LONG_Pointer p) {
                final int offset = (int) p.value[0];
                sortedMap.put(offset, new RefItem(offset, 2 + p.ifd_number * IFD.SIZE + 4, p, null));
                loadRefItem(p.ifd_sub, sortedMap);
            } else if (!item.isValue() && item.data_size > 0) {
                sortedMap.put(item.ifd_value_offset, new RefItem(item.ifd_value_offset, item.data_size, item, null));
            }
        }

        if (jpegOffset > 0 && jpegLength > 0 && ifdJpegOffset != null) {
            sortedMap.put((int) jpegOffset, new RefItem((int) jpegOffset, (int) jpegLength, ifdJpegOffset, null));
        }
    }

    /// Data referenced from the TIFF: an IFD group, or the value of an IFD
    /// entry stored at an offset.
    ///
    /// @param offset Offset within the TIFF
    /// @param length Length of the data
    /// @param ifd The IFD entry referencing the data, or `null` for an IFD group
    /// @param ifdgroup The IFD group, or `null` for an IFD entry value
    private record RefItem(int offset, int length, IFD ifd, IFDGroup ifdgroup) {
    }
}
