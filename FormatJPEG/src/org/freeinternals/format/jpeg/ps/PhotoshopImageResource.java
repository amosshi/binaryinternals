/*
 * XMP.java    Nov 08, 2010, 12:55
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.ps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 * @see <a href="http://www.adobe.com/devnet/photoshop.html">Adobe Photoshop Developer Center</a>
 */
public class PhotoshopImageResource extends FileComponent {

    public final byte[] rawData;
    public final String signature;
    public final List<IRB_8BIM> irb_8bim;

    public PhotoshopImageResource(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.rawData = input.getBuf();
        this.irb_8bim = new ArrayList<IRB_8BIM>(100);

        StringBuilder sb = new StringBuilder(5);
        sb.append((char)this.rawData[0]);
        sb.append((char)this.rawData[1]);
        sb.append((char)this.rawData[2]);
        sb.append((char)this.rawData[3]);
        this.signature = sb.toString();

        if (IRB_8BIM.SIGNATURE_TEXT.equals(this.signature)) {
            while (((input.getPos() - input.getOffset()) < this.rawData.length)
                    && (input.readInt() == IRB_8BIM.SIGNATURE)) {               // 0x3842494D = "8BIM"
                this.irb_8bim.add(new IRB_8BIM(input));
            }
        }
        // else if ... (other signature)
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int count;
        DefaultMutableTreeNode node;

        if (IRB_8BIM.SIGNATURE_TEXT.equals(this.signature)) {
            count = 0;
            for (IRB_8BIM irb : this.irb_8bim) {
                parentNode.add(node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        irb.getStartPos(),
                        irb.getLength(),
                        String.format("IRB-8BIM [%d]", count))));
                count++;
                irb.generateTreeNode(node);
            }
        }
    }
}
