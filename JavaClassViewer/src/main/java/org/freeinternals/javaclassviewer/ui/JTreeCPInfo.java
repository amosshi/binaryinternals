/*
 * JTreeCPInfo.java    August 15, 2007, 4:12 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.CPInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.ConstantClassInfo;
import org.freeinternals.format.classfile.ConstantDoubleInfo;
import org.freeinternals.format.classfile.ConstantFieldrefInfo;
import org.freeinternals.format.classfile.ConstantFloatInfo;
import org.freeinternals.format.classfile.ConstantIntegerInfo;
import org.freeinternals.format.classfile.ConstantInterfaceMethodrefInfo;
import org.freeinternals.format.classfile.ConstantInvokeDynamicInfo;
import org.freeinternals.format.classfile.ConstantLongInfo;
import org.freeinternals.format.classfile.ConstantMethodHandleInfo;
import org.freeinternals.format.classfile.ConstantMethodTypeInfo;
import org.freeinternals.format.classfile.ConstantMethodrefInfo;
import org.freeinternals.format.classfile.ConstantNameAndTypeInfo;
import org.freeinternals.format.classfile.ConstantStringInfo;
import org.freeinternals.format.classfile.ConstantUtf8Info;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
final class JTreeCPInfo {

    private final ClassFile classFile;

    JTreeCPInfo(final ClassFile classFile) {
        this.classFile = classFile;
    }

    public void generateTreeNode(final DefaultMutableTreeNode rootNode, final CPInfo cp_info)
            throws InvalidTreeNodeException {
        if (cp_info == null) {
            return;
        }

        final short tag = cp_info.tag.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                cp_info.getStartPos(),
                1,
                "tag: " + tag)));

        if (cp_info instanceof ConstantUtf8Info) {
            this.generateTreeNode(rootNode, (ConstantUtf8Info) cp_info);
        } else if (cp_info instanceof ConstantIntegerInfo) {
            this.generateTreeNode(rootNode, (ConstantIntegerInfo) cp_info);
        } else if (cp_info instanceof ConstantFloatInfo) {
            this.generateTreeNode(rootNode, (ConstantFloatInfo) cp_info);
        } else if (cp_info instanceof ConstantLongInfo) {
            this.generateTreeNode(rootNode, (ConstantLongInfo) cp_info);
        } else if (cp_info instanceof ConstantDoubleInfo) {
            this.generateTreeNode(rootNode, (ConstantDoubleInfo) cp_info);
        } else if (cp_info instanceof ConstantClassInfo) {
            this.generateTreeNode(rootNode, (ConstantClassInfo) cp_info);
        } else if (cp_info instanceof ConstantStringInfo) {
            this.generateTreeNode(rootNode, (ConstantStringInfo) cp_info);
        } else if (cp_info instanceof ConstantFieldrefInfo) {
            this.generateTreeNode(rootNode, (ConstantFieldrefInfo) cp_info);
        } else if (cp_info instanceof ConstantMethodrefInfo) {
            this.generateTreeNode(rootNode, (ConstantMethodrefInfo) cp_info);
        } else if (cp_info instanceof ConstantInterfaceMethodrefInfo) {
            this.generateTreeNode(rootNode, (ConstantInterfaceMethodrefInfo) cp_info);
        } else if (cp_info instanceof ConstantNameAndTypeInfo) {
            this.generateTreeNode(rootNode, (ConstantNameAndTypeInfo) cp_info);
        } else if (cp_info instanceof ConstantMethodHandleInfo) {
            this.generateTreeNode(rootNode, (ConstantMethodHandleInfo) cp_info);
        } else if (cp_info instanceof ConstantMethodTypeInfo) {
            this.generateTreeNode(rootNode, (ConstantMethodTypeInfo) cp_info);
        } else if (cp_info instanceof ConstantInvokeDynamicInfo) {
            this.generateTreeNode(rootNode, (ConstantInvokeDynamicInfo) cp_info);
        } else {
            System.out.print("Un-recognized cp_info, tag = " + tag);
            // TODO: Add exception
        }
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantUtf8Info utf8Info)
            throws InvalidTreeNodeException {
        final int startPos = utf8Info.getStartPos();
        final int bytesLength = utf8Info.length_utf8.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "length: " + bytesLength)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                bytesLength,
                "bytes: " + utf8Info.getValue())));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantIntegerInfo integerInfo)
            throws InvalidTreeNodeException {
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                integerInfo.getStartPos() + 1,
                4,
                "bytes: " + integerInfo.integerValue)));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantFloatInfo floatInfo)
            throws InvalidTreeNodeException {
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatInfo.getStartPos() + 1,
                4,
                "bytes: " + floatInfo.floatValue)));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantLongInfo longInfo)
            throws InvalidTreeNodeException {
        final int startPos = longInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                4,
                "high_bytes - value: " + longInfo.longValue
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 5,
                4,
                "low_bytes"
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantDoubleInfo doubleInfo)
            throws InvalidTreeNodeException {
        final int startPos = doubleInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                4,
                "high_bytes - value: " + doubleInfo.doubleValue
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 5,
                4,
                "low_bytes"
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantClassInfo classInfo)
            throws InvalidTreeNodeException {
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                classInfo.getStartPos() + 1,
                2,
                "name_index: " + classInfo.name_index.value + " - " + this.classFile.getCPDescription(classInfo.name_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantStringInfo stringInfo)
            throws InvalidTreeNodeException {
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                stringInfo.getStartPos() + 1,
                2,
                "string_index: " + stringInfo.string_index.value + " - " + this.classFile.getCPDescription(stringInfo.string_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantFieldrefInfo fieldrefInfo)
            throws InvalidTreeNodeException {
        final int startPos = fieldrefInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "class_index: " + fieldrefInfo.class_index.value + " - " + this.classFile.getCPDescription(fieldrefInfo.class_index.value)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + fieldrefInfo.name_and_type_index.value + " - " + this.classFile.getCPDescription(fieldrefInfo.name_and_type_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantMethodrefInfo methodrefInfo)
            throws InvalidTreeNodeException {
        final int startPos = methodrefInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "class_index: " + methodrefInfo.class_index.value + " - " + this.classFile.getCPDescription(methodrefInfo.class_index.value)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + methodrefInfo.name_and_type_index.value + " - " + this.classFile.getCPDescription(methodrefInfo.name_and_type_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantInterfaceMethodrefInfo interfaceMethodrefInfo)
            throws InvalidTreeNodeException {
        final int startPos = interfaceMethodrefInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "class_index: " + interfaceMethodrefInfo.class_index.value + " - " + this.classFile.getCPDescription(interfaceMethodrefInfo.class_index.value)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + interfaceMethodrefInfo.name_and_type_index.value + " - " + this.classFile.getCPDescription(interfaceMethodrefInfo.name_and_type_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantNameAndTypeInfo nameAndTypeInfo)
            throws InvalidTreeNodeException {
        final int startPos = nameAndTypeInfo.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "name_index: " + nameAndTypeInfo.name_index.value + " - " + this.classFile.getCPDescription(nameAndTypeInfo.name_index.value)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "descriptor_index: " + nameAndTypeInfo.descriptor_index.value + " - " + this.classFile.getCPDescription(nameAndTypeInfo.descriptor_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantMethodHandleInfo info)
            throws InvalidTreeNodeException {
        final int startPos = info.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                1,
                "reference_kind: " + info.reference_kind.value + " - " + ConstantMethodHandleInfo.ReferenceKind.name(info.reference_kind.value)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "reference_index: " + info.reference_index.value + " - " + this.classFile.getCPDescription(info.reference_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantMethodTypeInfo info)
            throws InvalidTreeNodeException {
        final int startPos = info.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "descriptor_index: " + info.descriptor_index.value + " - " + this.classFile.getCPDescription(info.descriptor_index.value)
        )));
    }

    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final ConstantInvokeDynamicInfo info)
            throws InvalidTreeNodeException {
        final int startPos = info.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 1,
                2,
                "bootstrap_method_attr_index: " + info.bootstrap_method_attr_index.value
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 3,
                2,
                "name_and_type_index: " + info.name_and_type_index.value + " - " + this.classFile.getCPDescription(info.name_and_type_index.value)
        )));
    }
}
