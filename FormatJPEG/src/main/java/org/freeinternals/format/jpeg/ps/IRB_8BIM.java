/*
 * XMP.java    Nov 08, 2010, 12:58
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.ps;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class IRB_8BIM extends FileComponent {

    public static final int SIGNATURE = 0x3842494D;
    public static final String SIGNATURE_TEXT = "8BIM";
    public final int identifier;
    public final String name;
    private final boolean name_skip1byte;
    public final int size;
    public final byte[] data;
    private final boolean data_skip1byte;

    IRB_8BIM(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos() - 4;

        // identifier
        this.identifier = input.readUnsignedShort();

        // name
        int nameLen = input.readUnsignedShort();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < nameLen; i++) {
            sb.append((char) input.readByte());
        }
        this.name = sb.toString();

        // size
        this.size = input.readInt();

        // skip for name
        this.data_skip1byte = ((this.size & 1) != 0);
        if (this.data_skip1byte) {
            input.skip(1);
        }

        // data
        if (this.size > 0) {
            this.data = new byte[this.size];
            for (int i = 0; i < this.size; i++) {
                this.data[i] = input.readByte();
            }
        } else {
            this.data = null;
        }

        // skip for name
        this.name_skip1byte = ((this.size & 1) != 0);  // true for odd value; false for even
        if (this.name_skip1byte) {
            input.skip(1);
        }

        // total length
        super.length = input.getPos() - super.startPos;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int lastPos;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = this.startPos,
                4,
                "Signature = '8BIM'")));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + 4,
                2,
                String.format("Resource Unique Identifier = 0x%04X", this.identifier))));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + 2,
                2 + this.name.length(),
                String.format("Name = '%s'", this.name))));
        if (this.name_skip1byte) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos = lastPos + 2 + this.name.length(),
                    1,
                    "skip for even")));
        }

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + ((this.name_skip1byte) ? 1 : 2 + this.name.length()),
                4,
                String.format("Size = %d", this.size))));

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                lastPos = lastPos + 4,
                this.size,
                "Resource Data")));
        if (this.data_skip1byte) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    lastPos + this.size,
                    1,
                    "skip for even")));
        }
    }
}
