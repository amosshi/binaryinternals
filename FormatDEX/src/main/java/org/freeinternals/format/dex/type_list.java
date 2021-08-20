/*
 * type_list.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
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
public class type_list extends FileComponent implements GenerateTreeNode {

    public final Type_uint size;
    public final type_item[] list;

    type_list(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uint();
        if (this.size.value > 0) {
            DexFile.check_uint("type_list.size", this.size, stream.getPos());
            this.list = new type_item[(int) this.size.value];
            for (int i = 0; i < this.size.value; i++) {
                this.list[i] = new type_item(stream);
            }
        } else {
            this.list = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public String toString() {
        return "TODO";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class type_item extends FileComponent implements GenerateTreeNode {

        /**
         * Item Size In Bytes.
         *
         * @see map_list.TypeCodes#TYPE_TYPE_LIST
         */
        public static final int ITEM_SIZE = 2;

        public final Type_ushort type_idx;

        type_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.type_idx = stream.Dex_ushort();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
