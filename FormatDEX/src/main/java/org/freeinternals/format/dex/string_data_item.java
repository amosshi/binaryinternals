/*
 * StringDataItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.TreeNodeGenerator.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from Dex Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class string_data_item extends FileComponent implements GenerateTreeNode {

    /**
     * size of this string, in UTF-16 code units (which is the "string length"
     * in many systems). That is, this is the decoded length of the string. (The
     * encoded length is implied by the position of the 0 byte.)
     */
    public Type_uleb128 utf16_size;

    /**
     * a series of MUTF-8 code units (a.k.a. octets, a.k.a. bytes) followed by a
     * byte of value 0. See "MUTF-8 (Modified UTF-8) Encoding" above for details
     * and discussion about the data format.
     */
    public byte[] data;

    string_data_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.utf16_size = stream.Dex_uleb128();
        if (this.utf16_size.value > 0) {
            this.data = new byte[this.utf16_size.value];
            int bytesRead = stream.read(this.data);
            if (bytesRead != this.data.length) {
                throw new IOException("Cannot read correct number of bytes for string_data_item. expected bytes = "
                        + this.data.length + ", atual bytes read = " + bytesRead);
            }
        } else {
            this.data = null;
        }
        super.length = stream.getPos() - super.startPos;
    }

    /**
     * Get the {@link #data} as a String, using platform's default charset.
     *
     * @return String for the content
     */
    public String getString() {
        return new String(this.data, StandardCharsets.UTF_8);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        DefaultMutableTreeNode nodeTemp;
        int floatPos = this.getStartPos();
        int utf16Size = this.utf16_size.value;

        nodeTemp = addNode(parentNode, floatPos, this.utf16_size.length, "utf16_size", utf16Size, "msg_string_data_item__utf16_size", UITool.icon4Shortcut());
        floatPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();
        if (utf16Size > 0) {
            addNode(parentNode, floatPos, this.data.length, "data", this.getString(), "msg_string_data_item__data", UITool.icon4Shortcut());
        }
    }
}
