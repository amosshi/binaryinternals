/*
 * TypeIdItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.GenerateTreeNode;

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
public class type_id_item extends FileComponent implements GenerateTreeNode {

    /**
     * index into the string_ids list for the descriptor string of this type.
     * The string must conform to the syntax for TypeDescriptor, defined above.
     */
    public Type_uint descriptor_idx;

    type_id_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.descriptor_idx = stream.Dex_uint();
        super.length = Type_uint.LENGTH;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
