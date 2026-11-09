/*
 * annotation_set_ref_list.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// A list of annotation set references, the annotations of the parameters of a method.
///
/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public final class annotation_set_ref_list extends FileComponent implements GenerateTreeNodeDexFile {

    /// Size of the list in entries.
    public final Type_uint size;
    /// Elements of the list.
    public final annotation_set_ref_item[] list;

    annotation_set_ref_list(PosDataInputStreamDex stream, DexFile dex) throws IOException {
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
        return FORMAT_STRING_STRING.formatted(this.getClass().getSimpleName(), this.size);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        final DexFile dexFile = (DexFile) format;
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
                "list [%d]".formatted(this.list.length),
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
                    "%s[%d].annotations_off".formatted(annotation_set_ref_item.class.getSimpleName(), i),
                    refItem.annotations_off,
                    "msg_annotation_set_ref_item__annotations_off",
                    Icons.Offset
            );

            if (refItem.annotations_off.value == 0) {
                continue;
            }

            FileComponent fc = dexFile.data.get(refItem.annotations_off.value);
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    fc.getStartPos(),
                    fc.getLength(),
                    fc.getClass().getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_annotation_set_item")
            ));
            refItemNode.add(itemNode);
            if (fc instanceof GenerateTreeNodeDexFile item) {
                item.generateTreeNode(itemNode, dexFile);
            }
        }
    }

    /// An element of the list: the offset of an annotation set.
    public static final class annotation_set_ref_item extends FileComponent {

        /// Length of the item in bytes.
        public static final int LENGTH = Type_uint.LENGTH;
        /// Offset from the start of the file to the referenced annotation set, or `0` if there are no annotations for this element.
        public final Type_uint annotations_off;

        annotation_set_ref_item(PosDataInputStreamDex stream, DexFile dex) throws IOException {
            super.startPos = stream.getPos();
            this.annotations_off = stream.Dex_uint();
            if ((this.annotations_off.value != 0) && (!dex.data.containsKey(this.annotations_off.value))) {
                dex.parseData(this.annotations_off.value, annotation_set_item::new, stream);
            }
            super.length = stream.getPos() - super.startPos;
        }
    }
}
