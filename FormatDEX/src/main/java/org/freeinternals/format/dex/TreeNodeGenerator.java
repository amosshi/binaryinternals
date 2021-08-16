/*
 * TreeNodeGenerator.java    June 15, 2015, 23:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public class TreeNodeGenerator {
    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(TreeNodeGenerator.class.getPackageName() + ".MessagesBundle", Locale.ROOT);
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
        this.dexFile.header.generateTreeNode(this.rootNode);
        this.generateStringIds();
        this.generateTypeIds();
        this.generateProtoIds();
        this.generateFieldIds();
        this.generateMethodIds();
        this.generateClassDefs();
        this.generateData();
    }

    protected static DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value) {
        return addNode(parentNode, startPos, len, name, value, null, null);
    }

    protected static DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value, Icon icon) {
        return addNode(parentNode, startPos, len, name, value, null, icon);
    }

    protected static DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value, String msgkey) {
        return addNode(parentNode, startPos, len, name, value, msgkey, null);
    }
    protected static DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, int startPos, int len, String name, Object value, String msgkey, Icon icon) {
        JTreeNodeFileComponent fileComp = new JTreeNodeFileComponent(
                startPos,
                len,
                name + ": " + value.toString()
        );
        if (msgkey != null) {
            fileComp.setDescription(MESSAGES.getString(msgkey));
        }
        if (icon != null) {
            fileComp.setIcon(icon);
        }
        
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileComp);
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
                "magic 1: " + new String(this.dexFile.magic1, StandardCharsets.UTF_8))));
        startPos += DexFile.DEX_FILE_MAGIC1.size();

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC2.size(),
                "magic 2: " + new String(this.dexFile.magic2, StandardCharsets.UTF_8))));
    }

    private void generateStringIds() {
        if (this.dexFile.string_ids == null || this.dexFile.string_ids.length < 1) {
            return;
        }

        int startPos = this.dexFile.header.string_ids_off.intValue();
        int size = this.dexFile.string_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                size * Type_uint.LENGTH,
                String.format("string_ids [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            string_id_item item = this.dexFile.string_ids[i];
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "string_id_item[" + String.format("%,d", i) + "].string_data_off: " + item.string_data_off.toString()));
            node.add(itemNode);

            FileComponent fc = this.dexFile.data.get(item.string_data_off.value);
            if (fc instanceof StringDataItem) {
                ((StringDataItem) fc).generateTreeNode(itemNode);
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
                size * Type_uint.LENGTH,
                String.format("type_ids [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            type_id_item item = this.dexFile.type_ids[i];
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
                size * proto_id_item.LENGTH,
                String.format("proto_ids [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            proto_id_item item = this.dexFile.proto_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "proto_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "shorty_idx", item.shorty_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();
            addNode(nodeTemp, 0, 0, "Value", this.dexFile.getString(item.shorty_idx.intValue()));

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "return_type_idx", item.return_type_idx);
            addNode(nodeTemp, 0, 0, "Value", this.dexFile.getTypeDescriptor(item.return_type_idx.intValue()));
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
                size * field_id_item.LENGTH,
                String.format("field_ids [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            field_id_item item = this.dexFile.field_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "proto_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = addNode(itemNode, startPos, Type_ushort.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_ushort.LENGTH, "type_idx", item.type_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            addNode(itemNode, startPos, Type_uint.LENGTH, "name_idx", item.name_idx);
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
                size * method_id_item.LENGTH,
                String.format("method_ids [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            method_id_item item = this.dexFile.method_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "method_id_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = addNode(itemNode, startPos, Type_ushort.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_ushort.LENGTH, "proto_idx", item.proto_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            addNode(itemNode, startPos, Type_uint.LENGTH, "name_idx", item.name_idx);
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
                size * class_def_item.LENGTH,
                String.format("class_defs [%,d]", size)));
        this.rootNode.add(node);

        for (int i = 0; i < size; i++) {
            class_def_item item = this.dexFile.class_defs[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "class_def_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "access_flags", item.access_flags);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "superclass_idx", item.superclass_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "interfaces_off", item.interfaces_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "source_file_idx", item.source_file_idx);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "annotations_off", item.annotations_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "class_data_off", item.class_data_off);
            startPos = ((JTreeNodeFileComponent)nodeTemp.getUserObject()).getLastPosPlus1();

            addNode(itemNode, startPos, Type_uint.LENGTH, "static_values_off", item.static_values_off);
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
                    Type_uint.toString(startPos) + " - " + fc.getClass().getSimpleName()));
            node.add(itemNode);

            if (fc instanceof StringDataItem) {
                ((StringDataItem) fc).generateTreeNode(itemNode);
            }
        }
    }
}
