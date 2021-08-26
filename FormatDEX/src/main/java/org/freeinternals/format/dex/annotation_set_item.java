/*
 * annotation_set_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
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
public class annotation_set_item extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_uint size;
    public final annotation_off_item[] entries;

    annotation_set_item(PosDataInputStreamDex stream, DexFile dexFile) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uint();
        if (this.size.value > 0) {
            this.entries = new annotation_off_item[this.size.intValue()];
            for (int i = 0; i < this.size.value; i++) {
                this.entries[i] = new annotation_off_item(stream, dexFile);
            }
        } else {
            this.entries = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public String toString() {
        return String.format(FORMAT_STRING_STRING, this.getClass().getSimpleName(), this.size);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "size",
                this.size,
                "msg_annotation_set_item__size",
                UITool.icon4Size());
        floatPos += Type_uint.LENGTH;

        if (this.entries == null) {
            return;
        }

        DefaultMutableTreeNode entriesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                annotation_off_item.LENGTH * this.entries.length,
                String.format("entries [%d]", this.entries.length),
                UITool.icon4Data(),
                MESSAGES.getString("msg_annotation_set_item__entries")
        ));
        parentNode.add(entriesNode);

        for (int i = 0; i < this.entries.length; i++) {
            // Since annotation_off_item has only 1 field, so we do not use child node
            annotation_off_item offItem = this.entries[i];
            DefaultMutableTreeNode offItemNode = addNode(entriesNode,
                    offItem.getStartPos(),
                    offItem.getLength(),
                    String.format("%s[%d].annotation_off", annotation_off_item.class.getSimpleName(), i),
                    offItem.annotation_off,
                    "msg_annotation_off_item__annotation_off",
                    UITool.icon4Offset()
            );

            annotation_item item = (annotation_item) dexFile.data.get(offItem.annotation_off.value);
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    annotation_item.class.getSimpleName(),
                    UITool.icon4Shortcut(),
                    MESSAGES.getString("msg_annotation_item")
            ));
            offItemNode.add(itemNode);
            item.generateTreeNode(itemNode, dexFile);
        }
    }

    public static class annotation_off_item extends FileComponent {

        public static final int LENGTH = Type_uint.LENGTH;
        public final Type_uint annotation_off;

        annotation_off_item(PosDataInputStreamDex stream, DexFile dexFile) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.annotation_off = stream.Dex_uint();
            super.length = stream.getPos() - super.startPos;

            dexFile.parseData(this.annotation_off.value, annotation_item.class, stream);
        }
    }

    public static class annotation_item extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_ubyte visibility;
        public final encoded_annotation annotation;

        annotation_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.visibility = stream.Dex_ubyte();
            this.annotation = new encoded_annotation(stream);
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            int floatPos = super.startPos;
            addNode(parentNode,
                    floatPos,
                    Type_ubyte.LENGTH,
                    "size",
                    String.format(FORMAT_STRING_STRING, this.visibility, Visibility.toString(this.visibility.value)),
                    "msg_annotation_item__visibility", //
                    UITool.icon4Data() // Icon to be changed
            );
            floatPos += Type_ubyte.LENGTH;

            DefaultMutableTreeNode annoNode = addNode(parentNode,
                    floatPos,
                    this.annotation.getLength(),
                    "annotation",
                    this.annotation.toString(dexFile),
                    "msg_annotation_item__annotation",
                    UITool.icon4Annotations()
            );
            this.annotation.generateTreeNode(annoNode, dexFile);
        }

        public enum Visibility {
            VISIBILITY_BUILD(0x00, "msg_annotation_item__visibility_build"),
            VISIBILITY_RUNTIME(0x01, "msg_annotation_item__visibility_runtime"),
            VISIBILITY_SYSTEM(0x02, "msg_annotation_item__visibility_system");

            public final int value;
            public final String description;

            Visibility(int v, String d) {
                this.value = v;
                this.description = MESSAGES.getString(d);
            }

            @Override
            public String toString() {
                return String.format("%d - %s", this.value, this.description);
            }

            public static String toString(int v) {
                for (Visibility item : Visibility.values()) {
                    if (item.value == v) {
                        return item.toString();
                    }
                }

                return String.format("%d - %s", v, MESSAGES.getString("msg_common_unrecognized"));
            }
        }
    }
}
