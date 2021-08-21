/*
 * AnnotationsDirectoryItem.java    June 23, 2015, 06:20
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
public class annotations_directory_item extends FileComponent implements GenerateTreeNodeDexFile{
    public final Type_uint class_annotations_off;
    public final Type_uint fields_size;
    public final Type_uint annotated_methods_size;
    public final Type_uint annotated_parameters_size;
    public final field_annotation[] field_annotations;
    public final method_annotation[] method_annotations;
    public final parameter_annotation[] parameter_annotations;

    annotations_directory_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.class_annotations_off = stream.Dex_uint();
        this.fields_size = stream.Dex_uint();
        this.annotated_methods_size = stream.Dex_uint();
        this.annotated_parameters_size = stream.Dex_uint();

        // to change
        this.field_annotations = null;
        this.method_annotations = null;
        this.parameter_annotations = null;

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        // to add
    }

    public static class field_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            // to add
        }
    }

    public static class method_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            // to add
        }
    }

    public static class parameter_annotation extends FileComponent implements GenerateTreeNodeDexFile {

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
            // to add
        }
    }
}
