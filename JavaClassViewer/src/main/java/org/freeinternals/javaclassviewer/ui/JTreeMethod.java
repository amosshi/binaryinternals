/*
 * JTreeMethod.java    August 15, 2007, 6:03 PM
 *
 * Copyright 2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.AttributeInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.MethodInfo;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
final class JTreeMethod {

    private JTreeMethod() {
    }

    public static void generateTreeNode(final DefaultMutableTreeNode rootNode, final MethodInfo method_info, final ClassFile classFile) throws InvalidTreeNodeException {
        if (method_info == null) {
            return;
        }

        final int startPos = method_info.getStartPos();
        final int attributesCount = method_info.attributes_count.value;
        int cp_index;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "access_flags: " + method_info.access_flags.value + ", " + method_info.getModifiers()
        )));
        cp_index = method_info.name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                String.format("name_index: %d - %s", cp_index, classFile.getCPDescription(cp_index))
        )));
        cp_index = method_info.descriptor_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                String.format("descriptor_index: %d - %s", cp_index, classFile.getCPDescription(cp_index))
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "attributes_count: " + attributesCount)));

        if (attributesCount > 0) {
            final AttributeInfo lastAttr = method_info.getAttribute(attributesCount - 1);
            final DefaultMutableTreeNode treeNodeAttr = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    lastAttr.getStartPos() + lastAttr.getLength() - startPos - 8,
                    "attributes[" + attributesCount + "]"
            ));

            DefaultMutableTreeNode treeNodeAttrItem;
            AttributeInfo attr;
            for (int i = 0; i < attributesCount; i++) {
                attr = method_info.getAttribute(i);
                treeNodeAttrItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        String.format("%d. %s", i + 1, attr.getName())
                ));
                new JTreeAttribute(classFile).generateTreeNode(treeNodeAttrItem, attr);
                treeNodeAttr.add(treeNodeAttrItem);
            }
            rootNode.add(treeNodeAttr);
        }
    }
}
