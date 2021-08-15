/*
 * JTreeClassFile.java    August 7, 2007, 4:23 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.ui.HTMLKit;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.attribute.attribute_info;
import org.freeinternals.format.classfile.constant.cp_info;

/**
 * A tree for {@link ClassFile} displaying all components in the class file.
 *
 * @author Amos Shi
 * @see ClassFile
 */
public class JTreeClassFile {

    private static final String HTML_LI = "<li>%s</li>";
    private static final String HTML_OL_BEGIN = "<ol>";
    private static final String HTML_OL_END = "</ol>";
    private static final String MESSAGE_ACCESS_FLAGS = "access_flags: ";
    private static final String MESSAGE_ATTR_COUNT = "attributes_count: ";

    static final String CP_PREFIX = "constant_pool[";
    static final String FIELDS_PREFIX = "fields[";
    static final String METHODS_PERFIX = "methods[";
    private final ClassFile classFile;
    DefaultMutableTreeNode root;

    /**
     * Creates a tree for {@link ClassFile}.
     *
     * @param classFile The class file to be shown
     */
    JTreeClassFile(final ClassFile classFile) {
        this.classFile = classFile;
    }

    void generateTreeNodes(final DefaultMutableTreeNode rootNode) {
        this.root = rootNode;
        this.root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(0, u4.LENGTH, "magic", GenerateClassfileTreeNode.MESSAGES.getString("msg_magic"))));

        this.generateTreeNodeClsssFileVersion();
        this.generateConstantPool();
        this.generateClassDeclaration();
        this.generateFields();
        this.generateMethods();
        this.generateAttributes();
    }

    private void generateTreeNodeClsssFileVersion() {
        int startPos = 4;

        this.root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "minor_version: " + this.classFile.minor_version.value,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_version")
        )));
        startPos += u2.LENGTH;

        this.root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "major_version: " + this.classFile.major_version.value,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_version")
        )));
    }

    private void generateConstantPool() {
        // MAGIC(4) + Minor Version + Major Version
        int startPos = 4 + u2.LENGTH + u2.LENGTH;

        final int cpCount = this.classFile.constant_pool_count.value;
        this.root.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "constant_pool_count: " + cpCount,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_constant_pool_count")
        )));
        startPos += u2.LENGTH;

        final cp_info[] cp = this.classFile.constant_pool;
        final DefaultMutableTreeNode constantPool = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                cp[cpCount - 1].getStartPos() + cp[cpCount - 1].getLength() - startPos,
                CP_PREFIX + cpCount + "]",
                GenerateClassfileTreeNode.MESSAGES.getString("msg_constant_pool_table")
        ));
        this.root.add(constantPool);

        DefaultMutableTreeNode cpInfoNode;
        for (int i = 1; i < cpCount; i++) {
            if (cp[i] != null) {
                cpInfoNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(cp[i].getStartPos(), cp[i].getLength(), i + ". " + cp[i].getName()));
                cpInfoNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        cp[i].getStartPos(),
                        1,
                        "tag: " + cp[i].tag.value,
                        GenerateClassfileTreeNode.MESSAGES.getString("msg_cp_tag")
                )));
                cp[i].generateTreeNode(cpInfoNode, this.classFile);
            } else {
                cpInfoNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(0, 0, i + ". [Empty Item]",
                        GenerateClassfileTreeNode.MESSAGES.getString("msg_cp_empty")));
            }

            constantPool.add(cpInfoNode);
        }
    }

    private void generateClassDeclaration() {

        final StringBuilder sb = new StringBuilder();
        final DefaultMutableTreeNode accessFlags = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.access_flags.getStartPos(),
                this.classFile.access_flags.getLength(),
                MESSAGE_ACCESS_FLAGS + BytesTool.getBinaryString(this.classFile.access_flags.value.value) + " " + this.classFile.getModifiers(),
                GenerateClassfileTreeNode.MESSAGES.getString("msg_access_flags")
        ));
        this.root.add(accessFlags);

        sb.append("this_class: ");
        sb.append(this.classFile.this_class.getValue());
        sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.this_class.getValue())));
        final DefaultMutableTreeNode thisClass = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.this_class.getStartPos(),
                this.classFile.this_class.getLength(),
                sb.toString(),
                GenerateClassfileTreeNode.MESSAGES.getString("msg_this_class")
        ));
        this.root.add(thisClass);

        final int superClassValue = this.classFile.super_class.getValue();
        sb.setLength(0);
        sb.append("super_class: ");
        sb.append(superClassValue);
        // Note. for module-info.class, it do NOT have super class.
        if (superClassValue > 0) {
            sb.append(String.format(" - %s", this.classFile.getCPDescription(this.classFile.super_class.getValue())));
        }
        final DefaultMutableTreeNode superClass = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.super_class.getStartPos(),
                this.classFile.super_class.getLength(),
                sb.toString(),
                GenerateClassfileTreeNode.MESSAGES.getString("msg_super_class")
        ));
        this.root.add(superClass);

        final int interfaceCount = this.classFile.interfaces_count.getValue();
        final DefaultMutableTreeNode interfacesCount = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.interfaces_count.getStartPos(),
                this.classFile.interfaces_count.getLength(),
                "interfaces_count: " + interfaceCount,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_interfaces_count")
        ));
        this.root.add(interfacesCount);

        if (this.classFile.interfaces_count.getValue() > 0) {
            final U2ClassComponent[] interfaces = this.classFile.interfaces;

            final DefaultMutableTreeNode interfacesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    interfaces[0].getStartPos(),
                    interfaces[interfaceCount - 1].getStartPos() + interfaces[interfaceCount - 1].getLength() - interfaces[0].getStartPos(),
                    "interfaces[" + interfaceCount + "]",
                    GenerateClassfileTreeNode.MESSAGES.getString("msg_interfaces_table")
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
        final DefaultMutableTreeNode fieldsCount = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.fields_count.getStartPos(),
                this.classFile.fields_count.getLength(),
                "fields_count: " + fieldCount,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_fields_count")
        ));
        this.root.add(fieldsCount);

        if (fieldCount > 0) {
            final field_info[] fields = this.classFile.fields;
            final DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    fields[0].getStartPos(),
                    fields[fieldCount - 1].getStartPos() + fields[fieldCount - 1].getLength() - fields[0].getStartPos(),
                    FIELDS_PREFIX + fieldCount + "]",
                    GenerateClassfileTreeNode.MESSAGES.getString("msg_fields_table")
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

    private void generateField(final DefaultMutableTreeNode rootNode, final field_info fieldInfo, final ClassFile classFile) {
        if (fieldInfo == null) {
            return;
        }

        final int startPos = fieldInfo.getStartPos();
        final int attributesCount = fieldInfo.attributes_count.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                MESSAGE_ACCESS_FLAGS + BytesTool.getBinaryString(fieldInfo.access_flags.value) + " " + fieldInfo.getModifiers()
        )));
        int nameIndex = fieldInfo.name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "name_index: " + nameIndex + " - " + classFile.getCPDescription(nameIndex)
        )));
        int descriptorIndex = fieldInfo.descriptor_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                "descriptor_index: " + descriptorIndex + " - " + classFile.getCPDescription(descriptorIndex)
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                MESSAGE_ATTR_COUNT + attributesCount
        )));

        if (attributesCount > 0) {
            final attribute_info lastAttr = fieldInfo.getAttribute(attributesCount - 1);
            final DefaultMutableTreeNode treeNodeAttr = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    lastAttr.getStartPos() + lastAttr.getLength() - startPos - 8,
                    "attributes"
            ));

            DefaultMutableTreeNode treeNodeAttrItem;
            attribute_info attr;
            for (int i = 0; i < attributesCount; i++) {
                attr = fieldInfo.getAttribute(i);

                treeNodeAttrItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        String.format("%d. %s", i + 1, attr.getName()
                        )));
                attribute_info.generateTreeNode(treeNodeAttrItem, attr, this.classFile);

                treeNodeAttr.add(treeNodeAttrItem);
            }
            rootNode.add(treeNodeAttr);
        }
    }

    private void generateMethods() {
        final int methodCount = this.classFile.methods_count.getValue();
        final DefaultMutableTreeNode methodsCount = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.methods_count.getStartPos(),
                this.classFile.methods_count.getLength(),
                "methods_count: " + methodCount,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_methods_count")
        ));
        this.root.add(methodsCount);

        if (methodCount > 0) {
            final method_info[] methods = this.classFile.methods;
            final DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    methods[0].getStartPos(),
                    methods[methodCount - 1].getStartPos() + methods[methodCount - 1].getLength() - methods[0].getStartPos(),
                    METHODS_PERFIX + methodCount + "]",
                    GenerateClassfileTreeNode.MESSAGES.getString("msg_methods_table")
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

    private void generateMethod(final DefaultMutableTreeNode rootNode, final method_info methodInfo, final ClassFile classFile) {
        if (methodInfo == null) {
            return;
        }

        final int startPos = methodInfo.getStartPos();
        final int attributesCount = methodInfo.attributes_count.value;
        int cpIndex;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                MESSAGE_ACCESS_FLAGS + BytesTool.getBinaryString(methodInfo.access_flags.value) + " " + methodInfo.getModifiers()
        )));
        cpIndex = methodInfo.name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                String.format("name_index: %d - %s", cpIndex, classFile.getCPDescription(cpIndex))
        )));
        cpIndex = methodInfo.descriptor_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                String.format("descriptor_index: %d - %s", cpIndex, classFile.getCPDescription(cpIndex))
        )));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                MESSAGE_ATTR_COUNT + attributesCount)));

        if (attributesCount > 0) {
            final attribute_info lastAttr = methodInfo.getAttribute(attributesCount - 1);
            final DefaultMutableTreeNode treeNodeAttr = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    lastAttr.getStartPos() + lastAttr.getLength() - startPos - 8,
                    "attributes[" + attributesCount + "]"
            ));

            DefaultMutableTreeNode treeNodeAttrItem;
            attribute_info attr;
            for (int i = 0; i < attributesCount; i++) {
                attr = methodInfo.getAttribute(i);
                treeNodeAttrItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        String.format("%d. %s", i + 1, attr.getName())
                ));
                attribute_info.generateTreeNode(treeNodeAttrItem, attr, this.classFile);

                treeNodeAttr.add(treeNodeAttrItem);
            }
            rootNode.add(treeNodeAttr);
        }
    }

    private void generateAttributes() {
        final int attrCount = this.classFile.attributes_count.getValue();
        final DefaultMutableTreeNode attrsCount = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.classFile.attributes_count.getStartPos(),
                this.classFile.attributes_count.getLength(),
                MESSAGE_ATTR_COUNT + attrCount,
                GenerateClassfileTreeNode.MESSAGES.getString("msg_attributes_count")
        ));
        this.root.add(attrsCount);

        if (attrCount > 0) {
            final attribute_info[] attrs = this.classFile.attributes;
            final DefaultMutableTreeNode attrsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    attrs[0].getStartPos(),
                    attrs[attrCount - 1].getStartPos() + attrs[attrCount - 1].getLength() - attrs[0].getStartPos(),
                    "attributes[" + attrCount + "]",
                    GenerateClassfileTreeNode.MESSAGES.getString("msg_attributes_table")
            ));
            this.root.add(attrsNode);

            DefaultMutableTreeNode attrNode;
            for (int i = 0; i < attrCount; i++) {
                attribute_info attr = attrs[i];
                attrNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        (i + 1) + ". " + attr.getName()
                ));
                attribute_info.generateTreeNode(attrNode, attr, this.classFile);

                attrsNode.add(attrNode);
            }
        }
    }

    StringBuilder generateOpcodeParseResult(byte[] opcodeData) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.START);

        int cpindexCounter = 0;

        // The Extracted Code
        sb.append("<pre>");
        sb.append(BytesTool.getByteDataHexView(opcodeData));
        sb.append('\n');
        List<Opcode.InstructionParsed> codeResult = Opcode.parseCode(opcodeData);
        for (Opcode.InstructionParsed iResult : codeResult) {
            sb.append(iResult.toString(this.classFile));
            sb.append('\n');
            if (iResult.getCpindex() != null) {
                cpindexCounter++;
            }
        }
        sb.append("</pre>");

        // The Reference Object
        if (cpindexCounter > 0) {
            sb.append(HTML_OL_BEGIN);
            codeResult.stream().filter(iResult -> (iResult.getCpindex() != null)).forEachOrdered(
                    (Opcode.InstructionParsed iResult) -> sb.append(String.format(HTML_LI, HTMLKit.escapeFilter(
                            this.classFile.getCPDescription(iResult.getCpindex()))))
            );
            sb.append(HTML_OL_END);
        }

        sb.append(HTMLKit.END);
        return sb;
    }

    StringBuilder generateReport2CP() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.START);

        int count;

        // Constant Pool
        count = this.classFile.constant_pool_count.value;
        sb.append(String.format("Constant Pool Count: %d", count));
        sb.append(HTMLKit.NEW_LINE);
        if (count > 0) {
            cp_info[] cpInfoList = this.classFile.constant_pool;

            // Constant Pool - by Type
            sb.append("Constant Pool - Class");
            this.generateReport4CPType(sb, cpInfoList, count, cp_info.ConstantType.CONSTANT_Class.tag);
            sb.append("Constant Pool - Field");
            this.generateReport4CPType(sb, cpInfoList, count, cp_info.ConstantType.CONSTANT_Fieldref.tag);
            sb.append("Constant Pool - Method");
            this.generateReport4CPType(sb, cpInfoList, count, cp_info.ConstantType.CONSTANT_Methodref.tag);

            // Constant Pool Object List
            sb.append("Constant Pool Object List");
            sb.append(HTMLKit.NEW_LINE);
            sb.append(HTML_OL_BEGIN);
            for (cp_info cpItem : this.classFile.constant_pool) {
                String cpitemString = (cpItem == null) ? "(empty)" : cpItem.toString(this.classFile.constant_pool);
                sb.append(String.format(HTML_LI, HTMLKit.escapeFilter(cpitemString)));
            }
            sb.append(HTML_OL_END);
        }

        sb.append(HTMLKit.END);
        return sb;
    }

    private void generateReport4CPType(StringBuilder sb, cp_info[] cpInfoList, int count, short tag) {
        sb.append(HTMLKit.NEW_LINE);
        sb.append("<ul>");
        for (int i = 1; i < count; i++) {
            if (cpInfoList[i] != null && cpInfoList[i].tag.value == tag) {
                sb.append(String.format("<li>%d. %s</li>", i,
                        HTMLKit.escapeFilter(this.classFile.getCPDescription(i))));
            }
        }
        sb.append("</ul>");
    }

    StringBuilder generateReport2Fields() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.START);

        // Fields
        int count = this.classFile.fields_count.getValue();
        sb.append(String.format("Field Count: %d", count));
        sb.append(HTMLKit.NEW_LINE);
        if (count > 0) {
            sb.append(HTML_OL_BEGIN);
            for (field_info field : this.classFile.fields) {
                sb.append(String.format(HTML_LI, HTMLKit.escapeFilter(field.getDeclaration())));
            }
            sb.append(HTML_OL_END);
        }
        sb.append(HTMLKit.NEW_LINE);

        sb.append(HTMLKit.END);
        return sb;
    }

    StringBuilder generateReport2Methods() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.START);

        // Methods
        int count = this.classFile.methods_count.getValue();
        sb.append(String.format("Method Count: %d", count));
        sb.append(HTMLKit.NEW_LINE);
        if (count > 0) {
            sb.append(HTML_OL_BEGIN);
            for (method_info method : this.classFile.methods) {
                sb.append(String.format(HTML_LI, HTMLKit.escapeFilter(method.getDeclaration())));
            }
            sb.append(HTML_OL_END);
        }
        sb.append(HTMLKit.NEW_LINE);

        sb.append(HTMLKit.END);
        return sb;
    }
}
