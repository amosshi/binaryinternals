/*
 * class_data_item.java    June 23, 2015, 06:20
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
public class class_data_item extends FileComponent implements GenerateTreeNode {

    public final Type_uleb128 static_fields_size;
    public final Type_uleb128 instance_fields_size;
    public final Type_uleb128 direct_methods_size;
    public final Type_uleb128 virtual_methods_size;

    public final encoded_field[] static_fields;
    public final encoded_field[] instance_fields;
    public final encoded_method[] direct_methods;
    public final encoded_method[] virtual_methods;

    class_data_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.static_fields_size = stream.Dex_uleb128();
        this.instance_fields_size = stream.Dex_uleb128();
        this.direct_methods_size = stream.Dex_uleb128();
        this.virtual_methods_size = stream.Dex_uleb128();

        if (this.static_fields_size.value > 0) {
            this.static_fields = new encoded_field[this.static_fields_size.value];
            for (int i = 0; i < this.static_fields_size.value; i++) {
                this.static_fields[i] = new encoded_field(stream);
            }
        } else {
            this.static_fields = null;
        }

        if (this.instance_fields_size.value > 0) {
            this.instance_fields = new encoded_field[this.instance_fields_size.value];
            for (int i = 0; i < this.instance_fields_size.value; i++) {
                this.instance_fields[i] = new encoded_field(stream);
            }
        } else {
            this.instance_fields = null;
        }

        if (this.direct_methods_size.value > 0) {
            this.direct_methods = new encoded_method[this.direct_methods_size.value];
            for (int i = 0; i < this.direct_methods_size.value; i++) {
                this.direct_methods[i] = new encoded_method(stream);
            }
        } else {
            this.direct_methods = null;
        }

        if (this.virtual_methods_size.value > 0) {
            this.virtual_methods = new encoded_method[this.virtual_methods_size.value];
            for (int i = 0; i < this.virtual_methods_size.value; i++) {
                this.virtual_methods[i] = new encoded_method(stream);
            }
        } else {
            this.virtual_methods = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class encoded_field extends FileComponent implements GenerateTreeNode {

        public final Type_uleb128 field_idx_diff;
        public final Type_uleb128 access_flags;

        encoded_field(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.field_idx_diff = stream.Dex_uleb128();
            this.access_flags = stream.Dex_uleb128();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class encoded_method extends FileComponent implements GenerateTreeNode {

        public final Type_uleb128 method_idx_diff;
        public final Type_uleb128 access_flags;
        public final Type_uleb128 code_off;

        encoded_method(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.method_idx_diff = stream.Dex_uleb128();
            this.access_flags = stream.Dex_uleb128();
            this.code_off = stream.Dex_uleb128();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
