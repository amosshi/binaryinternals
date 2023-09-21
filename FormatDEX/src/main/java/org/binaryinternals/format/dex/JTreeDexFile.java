/*
 * JTreeDexFile.java    June 15, 2015, 23:45
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

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

    JTreeDexFile() {
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        DexFile dexFile = (DexFile)format;
        this.generate_magic(parentNode, dexFile);
        dexFile.header.generateTreeNode(parentNode, dexFile);
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
                DexFile.DEX_FILE_MAGIC1.length + DexFile.DEX_FILE_MAGIC2.length,
                "magic"));
        parentNode.add(magicNode);

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC1.length,
                "magic 1: " + new String(dexFile.magic1, StandardCharsets.UTF_8),
                Icons.Magic,
                GenerateTreeNodeDexFile.MESSAGES.getString("msg_dex_file_magic1")
        )));
        startPos += DexFile.DEX_FILE_MAGIC1.length;

        magicNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                DexFile.DEX_FILE_MAGIC2.length,
                "magic 2: " + new String(dexFile.magic2, StandardCharsets.UTF_8),
                Icons.Magic,
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
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    String.format("string_id_item[%,d] : %s", i, left(dexFile.get_string_ids_string(i)))
            ));
            node.add(itemNode);
            item.generateTreeNode(itemNode, dexFile);
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
                    String.format("proto_id_item[%,d] : %s", i, item.get_shorty(dexFile))));
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
                    String.format("class_def_item[%,d] %s", i, item.get_class_jls(dexFile))));
            item.generateTreeNode(itemNode, dexFile);
            node.add(itemNode);
        }
    }

    private void generate_data(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        if (dexFile.data.size() < 1) {
            return;
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                dexFile.header.data_off.intValue(),
                dexFile.header.data_size.intValue(),
                String.format("data [%,d]", dexFile.data.size())));
        parentNode.add(node);

        dexFile.data.entrySet().stream().map(item -> item.getValue()).forEachOrdered(comp -> {
            int startPos = comp.getStartPos();

            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    comp.getLength(),
                    Type_uint.toString(startPos) + " - " + comp.getClass().getSimpleName()));
            node.add(itemNode);

            if (comp instanceof GenerateTreeNode) {
                ((GenerateTreeNode) comp).generateTreeNode(itemNode);
            } else if (comp instanceof GenerateTreeNodeDexFile) {
                ((GenerateTreeNodeDexFile) comp).generateTreeNode(itemNode, dexFile);
            } else {
                // This should never happen, or else it is a coding logic error
                LOGGER.severe(String.format("FileComponent is not added to the tree: position=0x%X type=%s", comp.getStartPos(), comp.getClass().getName()));
            }
        });
    }
}
