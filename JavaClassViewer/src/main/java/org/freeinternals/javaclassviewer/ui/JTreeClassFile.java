/*
 * JTreeClassFile.java    August 7, 2007, 4:23 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.AttributeInfo;
import org.freeinternals.format.classfile.CPInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.FieldInfo;
import org.freeinternals.format.classfile.Interface;
import org.freeinternals.format.classfile.MethodInfo;

/**
 * A tree for {@link ClassFile} displaying all compoents in the class file.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile
 */
public class JTreeClassFile extends JTree {

    private static final long serialVersionUID = 4876543219876500000L;
    private static final int STARTPOS_CONSTANT_POOL = 10;
    private final ClassFile classFile;
    DefaultMutableTreeNode root = null;

    /**
     * Creates a tree for {@link ClassFile}.
     *
     * @param classFile The class file to be shown
     */
    public JTreeClassFile(final ClassFile classFile) {
        this.classFile = classFile;
        this.generateTreeNodes();
        this.setModel(new DefaultTreeModel(this.root));
    }

    private void generateTreeNodes() {
        this.root = new DefaultMutableTreeNode(new JTreeNodeFileComponent(0, this.classFile.getByteArraySize(), "Class File"));
        this.root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(0, 4, "magic")));

        this.generateTreeNodeClsssFileVersion();
        this.generateConstantPool();
        this.generateClassDeclaration();
        this.generateFields();
        this.generateMethods();
        this.generateAttributes();
    }

    private void generateTreeNodeClsssFileVersion() {
        final DefaultMutableTreeNode minor_version = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.minor_version.getStartPos(),
                this.classFile.minor_version.getLength(),
                "minor_version: " + this.classFile.minor_version.value.value
        ));
        this.root.add(minor_version);

        final DefaultMutableTreeNode major_version = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.major_version.getStartPos(),
                this.classFile.major_version.getLength(),
                "major_version: " + this.classFile.major_version.getValue()
        ));
        this.root.add(major_version);
    }

    private void generateConstantPool() {
        final int cpCount = this.classFile.constant_pool_count.getValue();
        final DefaultMutableTreeNode constant_pool_count = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.constant_pool_count.getStartPos(),
                this.classFile.constant_pool_count.getLength(),
                "constant_pool_count: " + cpCount
        ));
        this.root.add(constant_pool_count);

        final CPInfo[] cp = this.classFile.getConstantPool();
        final DefaultMutableTreeNode constant_pool = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                STARTPOS_CONSTANT_POOL,
                cp[cpCount - 1].getStartPos() + cp[cpCount - 1].getLength() - STARTPOS_CONSTANT_POOL,
                "constant_pool[" + cpCount + "]"
        ));
        this.root.add(constant_pool);

        DefaultMutableTreeNode cp_info;
        for (int i = 1; i < cpCount; i++) {
            if (cp[i] != null) {
                cp_info = new DefaultMutableTreeNode(new JTreeNodeFileComponent(cp[i].getStartPos(), cp[i].getLength(), i + ". " + cp[i].getName()));
                new JTreeCPInfo(this.classFile).generateTreeNode(cp_info, cp[i]);
            } else {
                cp_info = new DefaultMutableTreeNode(new JTreeNodeFileComponent(0, 0, i + ". [Empty Item]"));
            }

            constant_pool.add(cp_info);
        }
    }

    private void generateClassDeclaration() {

        final StringBuilder sb = new StringBuilder();
        final DefaultMutableTreeNode access_flags = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.access_flags.getStartPos(),
                this.classFile.access_flags.getLength(),
                "access_flags: " + this.classFile.access_flags.getModifiers()
        ));
        this.root.add(access_flags);

        sb.append("this_class: ");
        sb.append(this.classFile.this_class.getValue());
        sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.this_class.getValue())));
        final DefaultMutableTreeNode this_class = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.this_class.getStartPos(),
                this.classFile.this_class.getLength(),
                sb.toString()
        ));
        this.root.add(this_class);

        sb.setLength(0);
        sb.append("super_class: ");
        sb.append(this.classFile.super_class.getValue());
        sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.super_class.getValue())));
        final DefaultMutableTreeNode super_class = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.super_class.getStartPos(),
                this.classFile.super_class.getLength(),
                sb.toString()
        ));
        this.root.add(super_class);

        final int interfaceCount = this.classFile.interfaces_count.getValue();
        final DefaultMutableTreeNode interfaces_count = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.interfaces_count.getStartPos(),
                this.classFile.interfaces_count.getLength(),
                "interfaces_count: " + interfaceCount
        ));
        this.root.add(interfaces_count);

        if (interfaceCount > 0) {
            final Interface[] interfaces = this.classFile.getInterfaces();

            final DefaultMutableTreeNode interfacesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    interfaces[0].getStartPos(),
                    interfaces[interfaceCount - 1].getStartPos() + interfaces[interfaceCount - 1].getLength() - interfaces[0].getStartPos(),
                    "interfaces[" + interfaceCount + "]"
            ));
            this.root.add(interfacesNode);

            DefaultMutableTreeNode interfaceNode;
            for (int i = 0; i < interfaceCount; i++) {
                sb.setLength(0);
                sb.append(i);
                sb.append(". ");
                sb.append(interfaces[i].getValue());
                sb.append(String.format(" - [%s]", this.classFile.getCPDescription(interfaces[i].getValue())));

                interfaceNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        interfaces[i].getStartPos(),
                        interfaces[i].getLength(),
                        sb.toString()
                ));
                interfacesNode.add(interfaceNode);
            }
        }
    }

    private void generateFields() {
        final int fieldCount = this.classFile.fields_count.getValue();
        final DefaultMutableTreeNode fields_count = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.fields_count.getStartPos(),
                this.classFile.fields_count.getLength(),
                "fields_count: " + fieldCount
        ));
        this.root.add(fields_count);

        if (fieldCount > 0) {
            final FieldInfo[] fields = this.classFile.getFields();
            final DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    fields[0].getStartPos(),
                    fields[fieldCount - 1].getStartPos() + fields[fieldCount - 1].getLength() - fields[0].getStartPos(),
                    "fields[" + fieldCount + "]"
            ));
            this.root.add(fieldsNode);

            DefaultMutableTreeNode fieldNode;
            for (int i = 0; i < fieldCount; i++) {
                fieldNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        fields[i].getStartPos(),
                        fields[i].getLength(),
                        String.format("field %d: %s", i + 1, fields[i].getDeclaration())
                ));
                this.generateField(fieldNode, fields[i], this.classFile);
                fieldsNode.add(fieldNode);
            }
        }
    }

    private void generateField(final DefaultMutableTreeNode rootNode, final FieldInfo field_info, final ClassFile classFile) {
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

    private void generateMethods() {
        final int methodCount = this.classFile.methods_count.getValue();
        final DefaultMutableTreeNode methods_count = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.methods_count.getStartPos(),
                this.classFile.methods_count.getLength(),
                "methods_count: " + methodCount
        ));
        this.root.add(methods_count);

        if (methodCount > 0) {
            final MethodInfo[] methods = this.classFile.getMethods();
            final DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    methods[0].getStartPos(),
                    methods[methodCount - 1].getStartPos() + methods[methodCount - 1].getLength() - methods[0].getStartPos(),
                    "methods[" + methodCount + "]"
            ));
            this.root.add(methodsNode);

            DefaultMutableTreeNode methodNode;
            for (int i = 0; i < methodCount; i++) {
                methodNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        methods[i].getStartPos(),
                        methods[i].getLength(),
                        String.format("method %d: %s", i + 1, methods[i].getDeclaration())
                ));
                this.generateMethod(methodNode, methods[i], this.classFile);
                methodsNode.add(methodNode);
            }
        }
    }

    public void generateMethod(final DefaultMutableTreeNode rootNode, final MethodInfo method_info, final ClassFile classFile) {
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

    private void generateAttributes() {
        final int attrCount = this.classFile.attributes_count.getValue();
        final DefaultMutableTreeNode attrs_count = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.attributes_count.getStartPos(),
                this.classFile.attributes_count.getLength(),
                "attributes_count: " + attrCount
        ));
        this.root.add(attrs_count);

        if (attrCount > 0) {
            final AttributeInfo[] attrs = this.classFile.getAttributes();
            final DefaultMutableTreeNode attrsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    attrs[0].getStartPos(),
                    attrs[attrCount - 1].getStartPos() + attrs[attrCount - 1].getLength() - attrs[0].getStartPos(),
                    "attributes[" + attrCount + "]"
            ));
            this.root.add(attrsNode);

            DefaultMutableTreeNode attrNode;
            for (int i = 0; i < attrCount; i++) {
                attrNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attrs[i].getStartPos(),
                        attrs[i].getLength(),
                        (i + 1) + ". " + attrs[i].getName()
                ));
                new JTreeAttribute(this.classFile).generateTreeNode(attrNode, attrs[i]);
                attrsNode.add(attrNode);
            }
        }
    }
}
