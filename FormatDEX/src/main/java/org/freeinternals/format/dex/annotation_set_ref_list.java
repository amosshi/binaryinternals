/*
 * annotation_set_ref_list.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

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
public class annotation_set_ref_list extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_uint size;
    public final annotation_set_ref_item[] list;

    annotation_set_ref_list(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
        super.startPos = stream.getPos();

        this.size = stream.Dex_uint();
        if (this.size.value > 0) {
            this.list = new annotation_set_ref_item[this.size.intValue()];
            for (int i = 0; i < this.size.value; i++) {
                this.list[i] = new annotation_set_ref_item(stream, dex);
            }
        } else {
            this.list = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public String toString() {
        return String.format(FORMAT_STRING_STRING, this.getClass().getSimpleName(), this.size);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile)format;
        int floatPos = super.startPos;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "size",
                this.size,
                "msg_annotation_set_ref_list__size",
                Icons.Size);
        floatPos += Type_uint.LENGTH;

        if (this.list == null) {
            return;
        }

        DefaultMutableTreeNode listNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                annotation_set_ref_item.LENGTH * this.list.length,
                String.format("list [%d]", this.list.length),
                Icons.Data,
                MESSAGES.getString("msg_annotation_set_ref_list__list")
        ));
        parentNode.add(listNode);

        for (int i = 0; i < this.list.length; i++) {
            // Since annotation_set_ref_item has only 1 field, so we do not use child node
            annotation_set_ref_item refItem = this.list[i];
            DefaultMutableTreeNode refItemNode = addNode(listNode,
                    refItem.getStartPos(),
                    refItem.getLength(),
                    String.format("%s[%d].annotations_off", annotation_set_ref_item.class.getSimpleName(), i),
                    refItem.annotations_off,
                    "msg_annotation_set_ref_item__annotations_off",
                    Icons.Offset
            );

            if (refItem.annotations_off.value == 0) {
                continue;
            }

            FileComponent fc = dexFile.data.get(refItem.annotations_off.value);
            //annotation_set_item item = (annotation_set_item)
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    fc.getStartPos(),
                    fc.getLength(),
                    fc.getClass().getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_annotation_set_item")
            ));
            refItemNode.add(itemNode);
            if (fc instanceof GenerateTreeNodeDexFile) {
                ((GenerateTreeNodeDexFile) fc).generateTreeNode(itemNode, dexFile);
            }
        }
    }

    public static class annotation_set_ref_item extends FileComponent {

        public static final int LENGTH = Type_uint.LENGTH;
        public final Type_uint annotations_off;

        annotation_set_ref_item(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.annotations_off = stream.Dex_uint();
            if ((this.annotations_off.value != 0) && (!dex.data.keySet().contains(this.annotations_off.value))) {
                dex.parseData(this.annotations_off.value, annotation_set_item.class, stream);
            }
            super.length = stream.getPos() - super.startPos;
        }
    }
}
