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
import org.freeinternals.format.classfile.AttributeInfo;
import org.freeinternals.format.classfile.AbstractCPInfo;
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
    private static final int STARTPOS_constant_pool = 10;
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
        try {
            this.root = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(0, this.classFile.getByteArraySize(), "Class File"));

            final DefaultMutableTreeNode magic = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(0, 4, "magic"));
            this.root.add(magic);

            this.generateTreeNodeClsssFileVersion();
            this.generateConstantPool();
            this.generateClassDeclaration();
            this.generateFields();
            this.generateMethods();
            this.generateAttributes();
        } catch (InvalidTreeNodeException itne) {
            Logger.getLogger(JTreeClassFile.class.getName()).log(Level.SEVERE, null, itne);
        }
    }

    private void generateTreeNodeClsssFileVersion()
            throws InvalidTreeNodeException {
        final DefaultMutableTreeNode minor_version = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.minor_version.getStartPos(),
                this.classFile.minor_version.getLength(),
                "minor_version: " + this.classFile.minor_version.value.value));
        this.root.add(minor_version);

        final DefaultMutableTreeNode major_version = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.major_version.getStartPos(),
                this.classFile.major_version.getLength(),
                "major_version: " + this.classFile.major_version.getValue()));
        this.root.add(major_version);
    }

    private void generateConstantPool()
            throws InvalidTreeNodeException {
        final int cpCount = this.classFile.constant_pool_count.getValue();
        final DefaultMutableTreeNode constant_pool_count = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.constant_pool_count.getStartPos(),
                this.classFile.constant_pool_count.getLength(),
                "constant_pool_count: " + cpCount));
        this.root.add(constant_pool_count);

        final AbstractCPInfo[] cp = this.classFile.getConstantPool();
        final DefaultMutableTreeNode constant_pool = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                STARTPOS_constant_pool,
                cp[cpCount - 1].getStartPos() + cp[cpCount - 1].getLength() - STARTPOS_constant_pool,
                "constant_pool"));
        this.root.add(constant_pool);

        DefaultMutableTreeNode cp_info;
        for (int i = 1; i < cpCount; i++) {
            if (cp[i] != null) {
                cp_info = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(cp[i].getStartPos(), cp[i].getLength(), i + ". " + cp[i].getName()));
                new JTreeCPInfo(this.classFile).generateTreeNode(cp_info, cp[i]);
            } else {
                cp_info = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(0, 0, i + ". [Empty Item]"));
            }

            constant_pool.add(cp_info);
        }
    }

    private void generateClassDeclaration()
            throws InvalidTreeNodeException {

        final StringBuilder sb = new StringBuilder();

        final DefaultMutableTreeNode access_flags = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.access_flags.getStartPos(),
                this.classFile.access_flags.getLength(),
                "access_flags: " + this.classFile.access_flags.getModifiers()));
        this.root.add(access_flags);

        sb.append("this_class: ");
        sb.append(this.classFile.this_class.getValue());
        sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.this_class.getValue())));
        final DefaultMutableTreeNode this_class = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.this_class.getStartPos(),
                this.classFile.this_class.getLength(),
                sb.toString()));
        this.root.add(this_class);

        sb.setLength(0);
        sb.append("super_class: ");
        sb.append(this.classFile.super_class.getValue());
        sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.super_class.getValue())));
        final DefaultMutableTreeNode super_class = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.super_class.getStartPos(),
                this.classFile.super_class.getLength(),
                sb.toString()));
        this.root.add(super_class);

        final int interfaceCount = this.classFile.interfaces_count.getValue();
        final DefaultMutableTreeNode interfaces_count = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.interfaces_count.getStartPos(),
                this.classFile.interfaces_count.getLength(),
                "interfaces_count: " + interfaceCount));
        this.root.add(interfaces_count);

        if (interfaceCount > 0) {
            final Interface[] interfaces = this.classFile.getInterfaces();

            final DefaultMutableTreeNode interfacesNode = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(
                    interfaces[0].getStartPos(),
                    interfaces[interfaceCount - 1].getStartPos() + interfaces[interfaceCount - 1].getLength() - interfaces[0].getStartPos(),
                    "interfaces"));
            this.root.add(interfacesNode);

            DefaultMutableTreeNode interfaceNode;
            for (int i = 0; i < interfaceCount; i++) {
                sb.setLength(0);
                sb.append(i);
                sb.append(". ");
                sb.append(interfaces[i].getValue());
                sb.append(String.format(" - [%s]", this.classFile.getCPDescription(interfaces[i].getValue())));

                interfaceNode = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(
                        interfaces[i].getStartPos(),
                        interfaces[i].getLength(),
                        sb.toString()));
                interfacesNode.add(interfaceNode);
            }
        }
    }

    private void generateFields()
            throws InvalidTreeNodeException {
        final int fieldCount = this.classFile.fields_count.getValue();
        final DefaultMutableTreeNode fields_count = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.fields_count.getStartPos(),
                this.classFile.fields_count.getLength(),
                "fields_count: " + fieldCount));
        this.root.add(fields_count);

        if (fieldCount > 0) {
            final FieldInfo[] fields = this.classFile.getFields();
            final DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(
                    fields[0].getStartPos(),
                    fields[fieldCount - 1].getStartPos() + fields[fieldCount - 1].getLength() - fields[0].getStartPos(),
                    "fields"));
            this.root.add(fieldsNode);

            DefaultMutableTreeNode fieldNode;
            for (int i = 0; i < fieldCount; i++) {
                fieldNode = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(
                        fields[i].getStartPos(),
                        fields[i].getLength(),
                        String.format("field %d: %s", i + 1, fields[i].getDeclaration())));
                JTreeField.generateTreeNode(fieldNode, fields[i], classFile);
                fieldsNode.add(fieldNode);
            }
        }
    }

    private void generateMethods()
            throws InvalidTreeNodeException {
        final int methodCount = this.classFile.methods_count.getValue();
        final DefaultMutableTreeNode methods_count = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.methods_count.getStartPos(),
                this.classFile.methods_count.getLength(),
                "methods_count: " + methodCount));
        this.root.add(methods_count);

        if (methodCount > 0) {
            final MethodInfo[] methods = this.classFile.getMethods();
            final DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(
                    methods[0].getStartPos(),
                    methods[methodCount - 1].getStartPos() + methods[methodCount - 1].getLength() - methods[0].getStartPos(),
                    "methods"));
            this.root.add(methodsNode);

            DefaultMutableTreeNode methodNode;
            for (int i = 0; i < methodCount; i++) {
                methodNode = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(
                        methods[i].getStartPos(),
                        methods[i].getLength(),
                        String.format("method %d: %s", i + 1, methods[i].getDeclaration())));
                JTreeMethod.generateTreeNode(methodNode, methods[i], this.classFile);
                methodsNode.add(methodNode);
            }
        }
    }

    private void generateAttributes()
            throws InvalidTreeNodeException {
        final int attrCount = this.classFile.attributes_count.getValue();
        final DefaultMutableTreeNode attrs_count = new DefaultMutableTreeNode(
                new JTreeNodeClassComponent(
                this.classFile.attributes_count.getStartPos(),
                this.classFile.attributes_count.getLength(),
                "attributes_count: " + attrCount));
        this.root.add(attrs_count);

        if (attrCount > 0) {
            final AttributeInfo[] attrs = this.classFile.getAttributes();
            final DefaultMutableTreeNode attrsNode = new DefaultMutableTreeNode(
                    new JTreeNodeClassComponent(
                    attrs[0].getStartPos(),
                    attrs[attrCount - 1].getStartPos() + attrs[attrCount - 1].getLength() - attrs[0].getStartPos(),
                    "attributes"));
            this.root.add(attrsNode);

            DefaultMutableTreeNode attrNode;
            for (int i = 0; i < attrCount; i++) {
                attrNode = new DefaultMutableTreeNode(
                        new JTreeNodeClassComponent(
                        attrs[i].getStartPos(),
                        attrs[i].getLength(),
                        i + ". " + attrs[i].getName()));
                new JTreeAttribute(this.classFile).generateTreeNode(attrNode, attrs[i]);
                attrsNode.add(attrNode);
            }
        }
    }
}
