/*
 * JTreeDexFile.java    June 15, 2015, 23:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * </pre>
 */
@SuppressWarnings("java:S100")
public class JTreeDexFile implements GenerateTreeNodeDexFile {

    private static final Logger LOGGER = Logger.getLogger(JTreeDexFile.class.getName());
    private static final String MESSAGE_CLASS_IDX = "class_idx";

    JTreeDexFile() {
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
            fileComp.setDescription(GenerateTreeNodeDexFile.MESSAGES.getString(msgkey));
        }
        fileComp.setIcon(icon);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileComp);
        parentNode.add(node);
        return node;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        this.generate_magic(parentNode, dexFile);
        dexFile.header.generateTreeNode(parentNode);
        this.generate_string_ids(parentNode, dexFile);
        this.generate_type_ids(parentNode, dexFile);
        this.generate_proto_ids(parentNode, dexFile);
        this.generate_field_ids(parentNode, dexFile);
        this.generate_method_ids(parentNode, dexFile);
        this.generate_class_defs(parentNode, dexFile);
        this.generate_data(parentNode, dexFile);
    }

    private void generate_magic(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int startPos = 0;

        DefaultMutableTreeNode magicNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.size() + DexFile.DEX_FILE_MAGIC2.size(),
                "magic"));
        parentNode.add(magicNode);

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.size(),
                "magic 1: " + new String(dexFile.magic1, StandardCharsets.UTF_8),
                UITool.icon4Magic(),
                GenerateTreeNodeDexFile.MESSAGES.getString("msg_dex_file_magic1")
        )));
        startPos += DexFile.DEX_FILE_MAGIC1.size();

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC2.size(),
                "magic 2: " + new String(dexFile.magic2, StandardCharsets.UTF_8),
                UITool.icon4Magic(),
                GenerateTreeNodeDexFile.MESSAGES.getString("msg_dex_file_magic2")
        )));
    }

    private void generate_string_ids(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.string_ids == null || dexFile.string_ids.length < 1) {
            return;
        }

        int startPos = dexFile.header.string_ids_off.intValue();
        int size = dexFile.string_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                size * Type_uint.LENGTH,
                String.format("string_ids [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            string_id_item item = dexFile.string_ids[i];
            FileComponent fc = dexFile.data.get(item.string_data_off.value);
            string_data_item strData = null;
            String strDataValue = "";
            if (fc instanceof string_data_item) {
                strData = (string_data_item) fc;
                strDataValue = strData.getString();
            } else {
                LOGGER.log(Level.SEVERE, String.format("This case should never happen. The string_ids did not point to string_data_item: index=%d offset=0x%08X", i, fc.getStartPos()));
            }

            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    String.format("string_id_item[%,d] : %s", i, UITool.left(strDataValue))
            ));
            node.add(itemNode);
            item.generateTreeNode(itemNode);

            if (strData != null) {
                DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        strData.getStartPos(),
                        strData.getLength(),
                        "string_data_item",
                        UITool.icon4Shortcut(),
                        GenerateTreeNodeDexFile.MESSAGES.getString("msg_string_data_item")
                ));
                itemNode.add(dataNode);
                strData.generateTreeNode(dataNode);
            }
        }
    }

    private void generate_type_ids(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.type_ids == null || dexFile.type_ids.length < 1) {
            return;
        }

        int startPos = dexFile.header.type_ids_off.intValue();
        int size = dexFile.type_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                size * Type_uint.LENGTH,
                String.format("type_ids [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            type_id_item item = dexFile.type_ids[i];
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    String.format("type_id_item[%,d] : %s", i, item.get_descriptor_jls(dexFile))
            ));
            node.add(itemNode);
            item.generateTreeNode(itemNode, dexFile);
        }
    }

    private void generate_proto_ids(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.proto_ids == null || dexFile.proto_ids.length < 1) {
            return;
        }

        int size = dexFile.proto_ids.length;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.proto_ids_off.intValue(),
                size * proto_id_item.ITEM_SIZE,
                String.format("proto_ids [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            proto_id_item item = dexFile.proto_ids[i];
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    "proto_id_item[" + String.format("%,d", i) + "]"));
            item.generateTreeNode(itemNode, dexFile);
            node.add(itemNode);
        }
    }

    private void generate_field_ids(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.field_ids == null || dexFile.field_ids.length < 1) {
            return;
        }

        int size = dexFile.field_ids.length;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.field_ids_off.intValue(),
                size * field_id_item.ITEM_SIZE,
                String.format("field_ids [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            field_id_item item = dexFile.field_ids[i];
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    String.format("field_id_item[%,d] : %s", i, item.toString(dexFile))));
            item.generateTreeNode(itemNode, dexFile);
            node.add(itemNode);
        }
    }

    private void generate_method_ids(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.method_ids == null || dexFile.method_ids.length < 1) {
            return;
        }

        int size = dexFile.method_ids.length;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.method_ids_off.intValue(),
                size * method_id_item.ITEM_SIZE,
                String.format("method_ids [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            method_id_item item = dexFile.method_ids[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    String.format("method_id_item[%,d] : %s", i, item.toString(dexFile))));
            item.generateTreeNode(itemNode, dexFile);
            node.add(itemNode);
        }
    }

    private void generate_class_defs(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.class_defs == null || dexFile.class_defs.length < 1) {
            return;
        }

        int size = dexFile.class_defs.length;
        DefaultMutableTreeNode nodeTemp;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.class_defs_off.intValue(),
                size * class_def_item.ITEM_SIZE,
                String.format("class_defs [%,d]", size)));
        parentNode.add(node);

        for (int i = 0; i < size; i++) {
            class_def_item item = dexFile.class_defs[i];
            int startPos = item.getStartPos();
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    item.getLength(),
                    "class_def_item[" + String.format("%,d", i) + "]"));
            node.add(itemNode);

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, MESSAGE_CLASS_IDX, item.class_idx);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "access_flags", item.access_flags);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "superclass_idx", item.superclass_idx);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "interfaces_off", item.interfaces_off);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "source_file_idx", item.source_file_idx);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "annotations_off", item.annotations_off);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            nodeTemp = addNode(itemNode, startPos, Type_uint.LENGTH, "class_data_off", item.class_data_off);
            startPos = ((JTreeNodeFileComponent) nodeTemp.getUserObject()).getLastPosPlus1();

            addNode(itemNode, startPos, Type_uint.LENGTH, "static_values_off", item.static_values_off);
        }
    }

    private void generate_data(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.data.size() < 1) {
            return;
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.data_off.intValue(),
                dexFile.header.data_size.intValue(),
                "data"));
        parentNode.add(node);

        for (Map.Entry<Long, FileComponent> item : dexFile.data.entrySet()) {
            FileComponent fc = item.getValue();
            int startPos = fc.getStartPos();

            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    fc.getLength(),
                    Type_uint.toString(startPos) + " - " + fc.getClass().getSimpleName()));
            node.add(itemNode);

            if (fc instanceof string_data_item) {
                ((string_data_item) fc).generateTreeNode(itemNode);
            }
        }
    }
}
