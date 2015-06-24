/*
 * TIFF.java    Nov 06, 2010, 16:55
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

public class TIFF extends FileComponent {

    public static final String IFD_Number_Description = "A 2-byte count of the number of directory entries (i.e., the number of fields)";
    private byte[] tiffByteArray;
    private final TIFFHeader tiffHeader;
    ArrayList<IFDGroup> exifGroup = new ArrayList<IFDGroup>();

    public TIFF(final PosDataInputStream pDisTiff)
            throws IOException, FileFormatException {

        super.startPos = pDisTiff.getPos();
        super.length = 0; // Not appliable
        this.tiffByteArray = pDisTiff.getBuf();
        this.tiffHeader = new TIFFHeader(pDisTiff);

        IFDGroup group = new IFDGroup(this.tiffByteArray, this.tiffHeader, this.tiffHeader.offset_0ifd);
        this.exifGroup.add(group);
        while (group.next != 0) {
            group = new IFDGroup(this.tiffByteArray, this.tiffHeader, group.next);
            this.exifGroup.add(group);
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode nodeTiffHeader;

        // TIFF Header
        comp = new JTreeNodeFileComponent(
                this.startPos,
                TIFFHeader.SIZE,
                "TIFF Header");
        comp.setDescription("A TIFF file begins with an 8-byte image file header that points to an image file directory (IFD).");
        parentNode.add(nodeTiffHeader = new DefaultMutableTreeNode(comp));
        this.tiffHeader.generateTreeNode(nodeTiffHeader);

        // TIFF Data
        ConcurrentSkipListMap<Integer, RefItem> sortedMap = new ConcurrentSkipListMap<Integer, RefItem>();
        int lastEnd = this.tiffHeader.getStartPos() + TIFFHeader.SIZE;          // Absolute position
        int diff;

        for (IFDGroup group : this.exifGroup) {
            RefItem refItem = new RefItem();
            refItem.offset = group.offset;
            refItem.length = group.length;
            refItem.ifdgroup = group;
            sortedMap.put(refItem.offset, refItem);

            this.loadRefItem(group.ifd, sortedMap);
        }

        for (RefItem ref : sortedMap.values()) {
            diff = (this.startPos + ref.offset) - lastEnd;
            if (diff > 0) {
                Tool.generateTreeNode_Diff(
                        parentNode, lastEnd, diff,
                        this.tiffByteArray, this.startPos);
            }

            if (ref.ifdgroup != null) {
                ref.ifdgroup.generateTreeNode(parentNode);
            } else {
                parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.tiffHeader.getStartPos() + ref.offset,
                        ref.length,
                        String.format("Reference of tag [%s] %04X.H (%d, %s)",
                        ref.ifd.getTagSpace().toString(),
                        ref.ifd.ifd_tag,
                        ref.ifd.ifd_tag,
                        ref.ifd.getTagName()))));
            }

            lastEnd = this.startPos + ref.offset + ref.length;
        }

        // In case, there is some extra space in the end
        diff = (this.tiffHeader.getStartPos() + this.tiffByteArray.length) - lastEnd;
        if (diff > 0) {
            Tool.generateTreeNode_Diff(
                    parentNode, lastEnd, diff,
                    this.tiffByteArray, this.startPos);
        }
    }

    private void loadRefItem(IFD[] ifds, ConcurrentSkipListMap<Integer, RefItem> sortedMap) {
        if (ifds == null) {
            return;
        }

        // Add Special Logic for IFD_0201_JPEGInterchangeFormat & IFD_0202_JPEGInterchangeFormatLength
        // Now we just assume these two IFD tags should be in the same exifGroup
        long jpeg_offset = 0;
        long jpeg_length = 0;
        IFD ifd_jpeg_offset = null;

        for (IFD item : ifds) {
            if (item instanceof IFD_0201_JPEGInterchangeFormat) {
                jpeg_offset = ((IFD_0201_JPEGInterchangeFormat) item).getBitstreamOffset();
                ifd_jpeg_offset = item;
            } else if (item instanceof IFD_0202_JPEGInterchangeFormatLength) {
                jpeg_length = ((IFD_0202_JPEGInterchangeFormatLength) item).getBitstreamLength();
            }

            if (item instanceof IFD_LONG_Pointer) {
                IFD_LONG_Pointer p = (IFD_LONG_Pointer) item;

                RefItem refItem = new RefItem();
                refItem.offset = (int) p.value[0];
                refItem.length = 2 + p.ifd_number * IFD.SIZE + 4;
                refItem.ifd = p;
                sortedMap.put(refItem.offset, refItem);

                this.loadRefItem(p.ifd_sub, sortedMap);
            } else {
                if (item.isValue() == false) {
                    RefItem refItem = new RefItem();
                    refItem.offset = item.ifd_value_offset;
                    refItem.length = item.data_size;
                    refItem.ifd = item;
                    sortedMap.put(refItem.offset, refItem);
                }
            }
        }

        if (jpeg_offset > 0 && jpeg_length > 0 && ifd_jpeg_offset != null) {
            RefItem refItem = new RefItem();
            refItem.offset = (int) jpeg_offset;
            refItem.length = (int) jpeg_length;
            refItem.ifd = ifd_jpeg_offset;
            sortedMap.put(refItem.offset, refItem);
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    private class RefItem {

        int offset;
        int length;
        IFD ifd = null;
        IFDGroup ifdgroup = null;
    }
}
