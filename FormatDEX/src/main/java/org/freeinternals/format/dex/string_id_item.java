/*
 * string_id_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;

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
public class string_id_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * @see map_list.TypeCodes#TYPE_STRING_ID_ITEM
     */
    public static final int ITEM_SIZE = 0x04;

    /**
     * offset from the start of the file to the string data for this item. The
     * offset should be to a location in the data section, and the data should
     * be in the format specified by "string_data_item" below. There is no
     * alignment requirement for the offset.
     */
    public final Type_uint string_data_off;

    string_id_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.string_data_off = stream.Dex_uint();
        super.length = Type_uint.LENGTH;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode, final FileFormat format) {
        DexFile dexFile = (DexFile)format;
        DefaultMutableTreeNode offsetNode = addNode(parentNode, this.startPos, Type_uint.LENGTH, "string_data_off", string_data_off, "msg_string_id_item__string_data_off", UITool.icon4Offset());

        string_data_item item = (string_data_item) dexFile.data.get(this.string_data_off.value);
        DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                item.getStartPos(),
                item.getLength(),
                "string_data_item",
                UITool.icon4Shortcut(),
                GenerateTreeNodeDexFile.MESSAGES.getString("msg_string_data_item")
        ));
        offsetNode.add(dataNode);
        item.generateTreeNode(dataNode, format);
    }
}
