/*
 * TreeNodeGenerator.java    June 15, 2015, 23:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.ui.dex;

import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.dex.ClassDefItem;
import org.freeinternals.format.dex.DexFile;
import org.freeinternals.format.dex.Dex_ubyte;
import org.freeinternals.format.dex.Dex_uint;
import org.freeinternals.format.dex.Dex_ushort;
import org.freeinternals.format.dex.FieldIdItem;
import org.freeinternals.format.dex.HeaderItem;
import org.freeinternals.format.dex.HeaderItem.Endian;
import org.freeinternals.format.dex.MethodIdItem;
import org.freeinternals.format.dex.ProtoIdItem;
import org.freeinternals.format.dex.StringDataItem;
import org.freeinternals.format.dex.StringIdItem;
import org.freeinternals.format.dex.TypeIdItem;

/**
 *
 * @author Amos Shi
 */
public class TreeNodeGenerator {
    private static final String MESSAGE_CLASS_IDX = "class_idx";
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
        this.generateMethodIds();
        this.generateClassDefs();
        this.generateData();
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value) {
        
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                len,
                name + ": " + value.toString()));
        parentNode.add(node);
        return node;
    }

    private void generateMagic() {
        int startPos = 0;

        DefaultMutableTreeNode magicNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.size() + DexFile.DEX_FILE_MAGIC2.size(),
                "magic"));
        this.rootNode.add(magicNode);

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.size(),
                "magic 1")));
        startPos += DexFile.DEX_FILE_MAGIC1.size();

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC2.size(),
                "magic 2")));
    }

    private void generateHeaderItem() {
        final HeaderItem header = this.dexFile.header;
        DefaultMutableTreeNode nodeTemp;
        int startPos = header.getStartPos();

        DefaultMutableTreeNode headerNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                header.getLength(),
                "header_item"));
        this.rootNode.add(headerNode);

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "checksum", header.checksum);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                header.signature.length,
                "signature: " + Dex_ubyte.toString(header.signature))));
        startPos += header.signature.length;

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "file_size", header.file_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "header_size", header.header_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        headerNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                Dex_uint.LENGTH,
                "endian_tag: " + header.endian_tag.toString() + " / " + Endian.toString(header.endian_tag.intValue()))));
        startPos += Dex_uint.LENGTH;

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "link_size", header.link_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "link_off", header.link_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "map_off", header.map_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "string_ids_size", header.string_ids_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "string_ids_off", header.string_ids_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "type_ids_size", header.type_ids_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "type_ids_off", header.type_ids_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "proto_ids_size", header.proto_ids_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "proto_ids_off", header.proto_ids_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "field_ids_size", header.field_ids_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "field_ids_off", header.field_ids_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "method_ids_size", header.method_ids_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "method_ids_off", header.method_ids_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "class_defs_size", header.class_defs_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "class_defs_off", header.class_defs_off);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        nodeTemp = this.addNode(headerNode, startPos, Dex_uint.LENGTH, "data_size", header.data_size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

        this.addNode(headerNode, startPos, Dex_uint.LENGTH, "data_off", header.data_off);
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
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "string_id_item[" + String.format("%,d", i) + "].string_data_off: " + item.string_data_off.toString()));
            node.add(itemNode);

            FileComponent fc = this.dexFile.data.get(item.string_data_off.value);
            if (fc instanceof StringDataItem) {
                this.generateData(itemNode, (StringDataItem) fc);
            } else {
                itemNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        0,
                        0,
                        "ERROR: No String Item Found")));
            }
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
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "type_id_item[" + String.format("%,d", i) + "].descriptor_idx: " + item.descriptor_idx.toString()));
            node.add(itemNode);
            itemNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    0,
                    0,
                    this.dexFile.getString(item.descriptor_idx.intValue()))));
        }
    }

    private void generateProtoIds() {
        if (this.dexFile.proto_ids == null || this.dexFile.proto_ids.length < 1) {
            return;
        }

        int size = this.dexFile.proto_ids.length;
        DefaultMutableTreeNode nodeTemp;

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

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "shorty_idx", item.shorty_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();
            this.addNode(nodeTemp, 0, 0, "Value", this.dexFile.getString(item.shorty_idx.intValue()));

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "return_type_idx", item.return_type_idx);
            this.addNode(nodeTemp, 0, 0, "Value", this.dexFile.getTypeDescriptor(item.return_type_idx.intValue()));
        }
    }

    private void generateFieldIds() {
        if (this.dexFile.field_ids == null || this.dexFile.field_ids.length < 1) {
            return;
        }

        int size = this.dexFile.field_ids.length;
        DefaultMutableTreeNode nodeTemp;

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

            nodeTemp = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, "type_idx", item.type_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            this.addNode(itemNode, startPos, Dex_uint.LENGTH, "name_idx", item.name_idx);
        }
    }

    private void generateMethodIds() {
        if (this.dexFile.method_ids == null || this.dexFile.method_ids.length < 1) {
            return;
        }

        int size = this.dexFile.method_ids.length;
        DefaultMutableTreeNode nodeTemp;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.dexFile.header.method_ids_off.intValue(),
                size * MethodIdItem.LENGTH,
                "method_ids"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            MethodIdItem item = this.dexFile.method_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "method_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_ushort.LENGTH, "proto_idx", item.proto_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            this.addNode(itemNode, startPos, Dex_uint.LENGTH, "name_idx", item.name_idx);
        }
    }

    private void generateClassDefs() {
        if (this.dexFile.class_defs == null || this.dexFile.class_defs.length < 1) {
            return;
        }

        int size = this.dexFile.class_defs.length;
        DefaultMutableTreeNode nodeTemp;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.dexFile.header.class_defs_off.intValue(),
                size * ClassDefItem.LENGTH,
                "class_defs"));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            ClassDefItem item = this.dexFile.class_defs[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "class_def_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "access_flags", item.access_flags);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "superclass_idx", item.superclass_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "interfaces_off", item.interfaces_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "source_file_idx", item.source_file_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "annotations_off", item.annotations_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = this.addNode(itemNode, startPos, Dex_uint.LENGTH, "class_data_off", item.class_data_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            this.addNode(itemNode, startPos, Dex_uint.LENGTH, "static_values_off", item.static_values_off);
        }
    }

    private void generateData() {
        if (this.dexFile.data.size() < 1) {
            return;
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.dexFile.header.data_off.intValue(),
                this.dexFile.header.data_size.intValue(),
                "data"));
        this.rootNode.add(node);

        for (Map.Entry<Long, FileComponent> item : this.dexFile.data.entrySet()) {
            FileComponent fc = item.getValue();
            int startPos = fc.getStartPos();

            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    fc.getLength(),
                    Dex_uint.toString(startPos) + " - " + fc.getClass().getSimpleName()));
            node.add(itemNode);

            if (fc instanceof StringDataItem) {
                this.generateData(itemNode, (StringDataItem) fc);
            }
        }
    }

    private void generateData(DefaultMutableTreeNode parentNode, StringDataItem item) {
        DefaultMutableTreeNode nodeTemp;
        int startPos = item.getStartPos();
        int utf16Size = item.utf16_size.value;

        nodeTemp = this.addNode(parentNode, startPos, item.utf16_size.length, "utf16_size", utf16Size);
        startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();
        if (utf16Size > 0) {
            this.addNode(parentNode, startPos, item.data.length, "data", item.getString());
        }
    }

}
