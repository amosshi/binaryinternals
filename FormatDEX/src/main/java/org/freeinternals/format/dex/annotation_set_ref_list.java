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
public class annotation_set_ref_list extends FileComponent implements GenerateTreeNodeDexFile{

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
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "size",
                this.size,
                "msg_annotation_set_ref_list__size",
                UITool.icon4Size());
        //floatPos += Type_uint.LENGTH;

        if (this.list != null) {
            // TODO
        }
    }


    public static class annotation_set_ref_item extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uint annotations_off;

        annotation_set_ref_item(PosDataInputStreamDex stream, DexFile dex) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.annotations_off = stream.Dex_uint();
            if ((this.annotations_off.value != 0) && (!dex.data.keySet().contains(this.annotations_off.value))) {
                dex.parseData(this.annotations_off.value, annotation_set_item.class, stream);
            }
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            //
        }

    }

}
