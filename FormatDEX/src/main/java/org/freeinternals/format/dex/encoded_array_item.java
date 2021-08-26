/*
 * encoded_array_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class encoded_array_item extends FileComponent implements GenerateTreeNodeDexFile {
    
    public final encoded_array value;

    encoded_array_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.value = new encoded_array(stream);
        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        DefaultMutableTreeNode valueNode = addNode(parentNode, super.startPos, this.value.getLength(), "value", this.value, "msg_encoded_array_item__value", UITool.icon4Data());
        this.value.generateTreeNode(valueNode, dexFile);
    }
}
