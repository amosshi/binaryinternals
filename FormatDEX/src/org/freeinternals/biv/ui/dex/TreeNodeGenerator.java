/*
 * TreeNodeGenerator.java    June 15, 2015, 23:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.ui.dex;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.dex.DexFile;
import org.freeinternals.format.dex.Dex_ubyte;
import org.freeinternals.format.dex.Dex_uint;
import org.freeinternals.format.dex.Dex_ushort;
import org.freeinternals.format.dex.FieldIdItem;
import org.freeinternals.format.dex.HeaderItem;
import org.freeinternals.format.dex.HeaderItem.Endian;
import org.freeinternals.format.dex.ProtoIdItem;
import org.freeinternals.format.dex.StringIdItem;
import org.freeinternals.format.dex.TypeIdItem;

/**
 *
 * @author Amos Shi
 */
public class TreeNodeGenerator {

    private final DexFile dexFile;
    private final DefaultMutableTreeNode rootNode;

    public static void generate(DexFile dexFile, DefaultMutableTreeNode parentNode) {
        new TreeNodeGenerator(dexFile, parentNode).start();
    }

    TreeNodeGenerator(DexFile dexFile, DefaultMutableTreeNode parentNode) {
        this.dexFile = dexFile;
        this.rootNode = parentNode;
    }

    private void start() {
        this.generateMagic();
        this.generateHeaderItem();
        this.generateStringIds();
        this.generateTypeIds();
        this.generateProtoIds();
        this.generateFieldIds();

    }

    private int addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                len,
                name + ": " + value.toString())));
        return startPos + len;
    }

    private void generateMagic() {
        int startPos = 0;

        DefaultMutableTreeNode magicNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.length + DexFile.DEX_FILE_MAGIC2.length,
                "magic"));
        this.rootNode.add(magicNode);

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.length,
                "magic 1")));
        startPos += DexFile.DEX_FILE_MAGIC1.length;

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC2.length,
                "magic 2")));
    }

    private void generateHeaderItem() {
        final HeaderItem header = this.dexFile.header;
        int startPos = header.getStartPos();

        DefaultMutableTreeNode headerNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                header.getLength(),
                "header_item"));
        this.rootNode.add(headerNode);

        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "checksum", header.checksum);

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                header.signature.length,
                "signature: " + Dex_ubyte.toString(header.signature))));
        startPos += header.signature.length;

        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "file_size", header.file_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "header_size", header.header_size);

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                Dex_uint.LENGTH,
                "endian_tag: " + header.endian_tag.toString() + " / " + Endian.toString(header.endian_tag.intValue()))));
        startPos += Dex_uint.LENGTH;

        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "link_size", header.link_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "link_off", header.link_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "map_off", header.map_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "string_ids_size", header.string_ids_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "string_ids_off", header.string_ids_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "type_ids_size", header.type_ids_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "type_ids_off", header.type_ids_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "proto_ids_size", header.proto_ids_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "proto_ids_off 	", header.proto_ids_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "field_ids_size", header.field_ids_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "field_ids_off", header.field_ids_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "method_ids_size", header.method_ids_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "method_ids_off", header.method_ids_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "class_defs_size", header.class_defs_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "class_defs_off", header.class_defs_off);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "data_size", header.data_size);
        startPos = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "data_off", header.data_off);
    }

    private void generateStringIds() {
        if (this.dexFile.string_ids == null || this.dexFile.string_ids.length < 1) {
            return;
        }

        int startPos = this.dexFile.header.string_ids_off.intValue();
        int size = this.dexFile.string_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                size * Dex_uint.LENGTH,
                "string_ids"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            StringIdItem item = this.dexFile.string_ids[i];
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "string_id_item[" + String.format("%,d", i) + "].string_data_off: " + item.string_data_off.toString())));
        }
    }

    private void generateTypeIds() {
        if (this.dexFile.type_ids == null || this.dexFile.type_ids.length < 1) {
            return;
        }

        int startPos = this.dexFile.header.type_ids_off.intValue();
        int size = this.dexFile.type_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                size * Dex_uint.LENGTH,
                "type_ids"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            TypeIdItem item = this.dexFile.type_ids[i];
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "type_id_item[" + String.format("%,d", i) + "].descriptor_idx: " + item.descriptor_idx.toString())));
        }
    }

    private void generateProtoIds() {
        if (this.dexFile.proto_ids == null || this.dexFile.proto_ids.length < 1) {
            return;
        }

        int size = this.dexFile.proto_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.dexFile.header.proto_ids_off.intValue(),
                size * ProtoIdItem.LENGTH,
                "proto_ids"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            ProtoIdItem item = this.dexFile.proto_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "proto_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            startPos = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "shorty_idx", item.shorty_idx);
            startPos = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "return_type_idx", item.return_type_idx);
            startPos = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "parameters_off", item.parameters_off);
        }
    }

    private void generateFieldIds() {
        if (this.dexFile.field_ids == null || this.dexFile.field_ids.length < 1) {
            return;
        }

        int size = this.dexFile.field_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.dexFile.header.field_ids_off.intValue(),
                size * FieldIdItem.LENGTH,
                "field_ids"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            FieldIdItem item = this.dexFile.field_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "proto_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            startPos = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, "class_idx", item.class_idx);
            startPos = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, "type_idx", item.type_idx);
            startPos = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "name_idx", item.name_idx);
        }        
    }
}
