/*
 * XMP.java    Nov 08, 2010, 12:55
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.ps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
/// @see <a href="http://www.adobe.com/devnet/photoshop.html">Adobe Photoshop Developer Center</a>
public class PhotoshopImageResource extends FileComponent {

    public final byte[] rawData;
    public final String signature;
    public final List<IRB_8BIM> irb_8bim;

    public PhotoshopImageResource(final PosDataInputStream input) throws IOException {
        super.startPos = input.getPos();
        super.length = input.getBuf().length;

        this.rawData = input.getBuf();
        this.irb_8bim = new ArrayList<>(100);

        this.signature = new String(this.rawData, 0, 4, StandardCharsets.ISO_8859_1);

        if (IRB_8BIM.SIGNATURE_TEXT.equals(this.signature)) {
            while (((input.getPos() - input.getOffset()) < this.rawData.length)
                    && (input.readInt() == IRB_8BIM.SIGNATURE)) {               // 0x3842494D = "8BIM"
                this.irb_8bim.add(new IRB_8BIM(input));
            }
        }
        // else if ... (other signature)
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (IRB_8BIM.SIGNATURE_TEXT.equals(this.signature)) {
            int count = 0;
            for (IRB_8BIM irb : this.irb_8bim) {
                final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        irb.getStartPos(),
                        irb.getLength(),
                        "IRB-8BIM [%d]".formatted(count)));
                parentNode.add(node);
                count++;
                irb.generateTreeNode(node);
            }
        }
    }
}
