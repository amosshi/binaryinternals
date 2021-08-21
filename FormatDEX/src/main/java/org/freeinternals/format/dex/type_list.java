/*
 * type_list.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.classfile.SignatureConvertor;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public class type_list extends FileComponent implements GenerateTreeNodeDexFile {

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
    public void generateTreeNode(final DefaultMutableTreeNode parentNode, final DexFile dexFile) {
        int floatPos = this.getStartPos();

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "size",
                this.size,
                "msg_type_list__size",
                UITool.icon4Size()
        );

        // Since type_item has only 1 field, so we do not use child node
        if (this.list != null && this.list.length > 0) {
            for (int i = 0; i < this.list.length; i++) {
                type_item item = this.list[i];
                addNode(parentNode,
                        item.getStartPos(),
                        item.getLength(),
                        String.format("type_item[%d].type_idx", i),
                        item.get_type_jls(dexFile),
                        "msg_type_item__type_idx",
                        UITool.icon4Index()
                );
            }
        }
    }

    /**
     * String format of current {@link type_list}.
     *
     * @param dexFile Current {@link DexFile}
     * @return String format of type list
     */
    public String toString(DexFile dexFile) {
        if (this.size.value < 1) {
            return "";
        } else {
            List<String> items = new ArrayList<>();
            for (type_item item : this.list) {
                items.add(item.get_type_jls(dexFile).toString());
            }
            return String.join(", ", items);
        }
    }

    /**
     * Elements of the {@link type_list}.
     */
    public static class type_item extends FileComponent {

        /**
         * Item Size In Bytes.
         *
         * @see map_list.TypeCodes#TYPE_TYPE_LIST
         */
        public static final int ITEM_SIZE = 2;

        public final Type_ushort type_idx;
        private String type = null;
        private SignatureConvertor.SignatureResult type_jls = null;

        type_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.type_idx = stream.Dex_ushort();
            super.length = stream.getPos() - super.startPos;
        }

        /**
         * Get {@link #type} text.
         *
         * @param dexFile Current {@link DexFile}
         * @return {@link #type} text
         * @see #type
         */
        public String get_type(DexFile dexFile) {
            if (this.type == null) {
                this.type = dexFile.get_type_ids_string(type_idx.value);
            }
            return this.type;
        }

        /**
         * Get {@link #type} text in Java Language Specification format.
         *
         * @param dexFile Current {@link DexFile}
         * @return {@link #type} parse result for JLS format
         * @see #type
         */
        public SignatureConvertor.SignatureResult get_type_jls(DexFile dexFile) {
            if (this.type_jls == null) {
                this.type_jls = dexFile.type_ids[type_idx.value].get_descriptor_jls(dexFile);
            }
            return this.type_jls;
        }
    }
}
