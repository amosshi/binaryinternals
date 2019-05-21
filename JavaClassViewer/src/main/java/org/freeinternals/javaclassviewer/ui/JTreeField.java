/*
 * JTreeField.java    August 15, 2007, 6:00 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.AttributeInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.FieldInfo;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
final class JTreeField {

    private JTreeField() {
    }

    public static void generateTreeNode(final DefaultMutableTreeNode rootNode, final FieldInfo field_info, final ClassFile classFile) throws InvalidTreeNodeException {
        if (field_info == null) {
            return;
        }

        final int startPos = field_info.getStartPos();
        final int attributesCount = field_info.attributes_count.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "access_flags: " + field_info.access_flags.value + ", " + field_info.getModifiers()
        )));
        int name_index = field_info.name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "name_index: " + name_index + " - " + classFile.getCPDescription(name_index)
        )));
        int descriptor_index = field_info.descriptor_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                "descriptor_index: " + descriptor_index + " - " + classFile.getCPDescription(descriptor_index)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "attributes_count: " + attributesCount
        )));

        if (attributesCount > 0) {
            final AttributeInfo lastAttr = field_info.getAttribute(attributesCount - 1);
            final DefaultMutableTreeNode treeNodeAttr = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    lastAttr.getStartPos() + lastAttr.getLength() - startPos - 8,
                    "attributes"
            ));

            DefaultMutableTreeNode treeNodeAttrItem;
            AttributeInfo attr;
            for (int i = 0; i < attributesCount; i++) {
                attr = field_info.getAttribute(i);

                treeNodeAttrItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        String.format("%d. %s", i + 1, attr.getName()
                        )));
                new JTreeAttribute(classFile).generateTreeNode(treeNodeAttrItem, attr);
                treeNodeAttr.add(treeNodeAttrItem);
            }
            rootNode.add(treeNodeAttr);
        }
    }
}
