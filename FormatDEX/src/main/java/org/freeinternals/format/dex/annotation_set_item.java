/*
 * AnnotationSetList.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;

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

    annotation_set_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uint();
        if (this.size.value > 0) {
            this.entries = new annotation_off_item[this.size.intValue()];
            for (int i = 0; i < this.size.value; i++) {
                this.entries[i] = new annotation_off_item(stream);
            }
        } else {
            this.entries = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        // TODO add
    }

    public static class annotation_off_item extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uint annotation_off;

        annotation_off_item(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.annotation_off = stream.Dex_uint();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            // to add
        }
    }

    public static class annotation_item extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_ubyte visibility;

        annotation_item(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.visibility = stream.Dex_ubyte();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            // to add
        }
    }

}
