/*
 * JTreeAttribute.java   August 15, 2007, 5:58 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.Annotation;
import org.freeinternals.format.classfile.Annotation.ElementValue.TagEnum;
import org.freeinternals.format.classfile.AttributeAnnotationDefault;
import org.freeinternals.format.classfile.AttributeBootstrapMethods;
import org.freeinternals.format.classfile.AttributeBootstrapMethods.BootstrapMethod;
import org.freeinternals.format.classfile.AttributeCode;
import org.freeinternals.format.classfile.AttributeCode.ExceptionTable;
import org.freeinternals.format.classfile.AttributeConstantValue;
import org.freeinternals.format.classfile.AttributeDeprecated;
import org.freeinternals.format.classfile.AttributeEnclosingMethod;
import org.freeinternals.format.classfile.AttributeExceptions;
import org.freeinternals.format.classfile.AttributeInfo;
import org.freeinternals.format.classfile.AttributeInnerClasses;
import org.freeinternals.format.classfile.AttributeLineNumberTable;
import org.freeinternals.format.classfile.AttributeLocalVariableTable;
import org.freeinternals.format.classfile.AttributeSourceFile;
import org.freeinternals.format.classfile.AttributeSynthetic;
import org.freeinternals.format.classfile.AttributeExtended;
import org.freeinternals.format.classfile.AttributeLocalVariableTypeTable;
import org.freeinternals.format.classfile.AttributeLocalVariableTypeTable.LocalVariableTypeTable;
import org.freeinternals.format.classfile.AttributeRuntimeAnnotations;
import org.freeinternals.format.classfile.AttributeRuntimeParameterAnnotations;
import org.freeinternals.format.classfile.AttributeRuntimeTypeAnnotations;
import org.freeinternals.format.classfile.AttributeSignature;
import org.freeinternals.format.classfile.AttributeSourceDebugExtension;
import org.freeinternals.format.classfile.AttributeStackMapTable;
import org.freeinternals.format.classfile.AttributeStackMapTable.StackMapFrame;
import org.freeinternals.format.classfile.AttributeStackMapTable.VerificationTypeInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
class JTreeAttribute {

    public static final String ATTRIBUTE_CODE_NODE = "code";
    private final ClassFile classFile;

    JTreeAttribute(final ClassFile classFile) {
        this.classFile = classFile;
    }

    public void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeInfo attribute_info)
            throws InvalidTreeNodeException {

        if (attribute_info == null) {
            return;
        }

        final int startPos = attribute_info.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "attribute_name_index: " + attribute_info.attribute_name_index.value + ", name=" + attribute_info.getName())));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                4,
                "attribute_length: " + attribute_info.attribute_length.value)));

        if (attribute_info instanceof AttributeConstantValue) {
            // 4.7.2. The ConstantValue Attribute
            this.generateTreeNode(rootNode, (AttributeConstantValue) attribute_info);
        } else if (attribute_info instanceof AttributeCode) {
            // 4.7.3. The Code Attribute
            this.generateTreeNode(rootNode, (AttributeCode) attribute_info);
        } else if (attribute_info instanceof AttributeStackMapTable) {
            // 4.7.4. The StackMapTable Attribute
            this.generateTreeNode(rootNode, (AttributeStackMapTable) attribute_info);
        } else if (attribute_info instanceof AttributeExceptions) {
            // 4.7.5. The Exceptions Attribute
            this.generateTreeNode(rootNode, (AttributeExceptions) attribute_info);
        } else if (attribute_info instanceof AttributeInnerClasses) {
            // 4.7.6. The InnerClasses Attribute
            this.generateTreeNode(rootNode, (AttributeInnerClasses) attribute_info);
        } else if (attribute_info instanceof AttributeEnclosingMethod) {
            // 4.7.7. The EnclosingMethod Attribute
            this.generateTreeNode(rootNode, (AttributeEnclosingMethod) attribute_info);
        } else if (attribute_info instanceof AttributeSynthetic) {
            // 4.7.8. The Synthetic Attribute
            this.generateTreeNode(rootNode, (AttributeSynthetic) attribute_info);
        } else if (attribute_info instanceof AttributeSignature) {
            // 4.7.9. The Signature Attribute
            this.generateTreeNode(rootNode, (AttributeSignature) attribute_info);
        } else if (attribute_info instanceof AttributeSourceFile) {
            // 4.7.10. The SourceFile Attribute
            this.generateTreeNode(rootNode, (AttributeSourceFile) attribute_info);
        } else if (attribute_info instanceof AttributeSourceDebugExtension) {
            // 4.7.11. The SourceDebugExtension Attribute
            this.generateTreeNode(rootNode, (AttributeSourceDebugExtension) attribute_info);
        } else if (attribute_info instanceof AttributeLineNumberTable) {
            // 4.7.12. The LineNumberTable Attribute
            this.generateTreeNode(rootNode, (AttributeLineNumberTable) attribute_info);
        } else if (attribute_info instanceof AttributeLocalVariableTable) {
            // 4.7.13. The LocalVariableTable Attribute
            this.generateTreeNode(rootNode, (AttributeLocalVariableTable) attribute_info);
        } else if (attribute_info instanceof AttributeLocalVariableTypeTable) {
            // 4.7.14. The LocalVariableTypeTable Attribute
            this.generateTreeNode(rootNode, (AttributeLocalVariableTypeTable) attribute_info);
        } else if (attribute_info instanceof AttributeDeprecated) {
            // 4.7.15. The Deprecated Attribute
            this.generateTreeNode(rootNode, (AttributeDeprecated) attribute_info);
        } else if (attribute_info instanceof AttributeRuntimeAnnotations) {
            // 4.7.16. The RuntimeVisibleAnnotations Attribute
            // 4.7.17. The RuntimeInvisibleAnnotations Attribute
            this.generateTreeNode(rootNode, (AttributeRuntimeAnnotations) attribute_info);
        } else if (attribute_info instanceof AttributeRuntimeParameterAnnotations) {
            // 4.7.18. The RuntimeVisibleParameterAnnotations Attribute
            // 4.7.19. The RuntimeInvisibleParameterAnnotations Attribute
            this.generateTreeNode(rootNode, (AttributeRuntimeParameterAnnotations) attribute_info);
        } else if (attribute_info instanceof AttributeRuntimeTypeAnnotations) {
            // 4.7.20. The RuntimeVisibleTypeAnnotations Attribute
            // 4.7.21. The RuntimeInvisibleTypeAnnotations Attribute
            this.generateTreeNode(rootNode, (AttributeRuntimeTypeAnnotations) attribute_info);
        } else if (attribute_info instanceof AttributeAnnotationDefault) {
            // 4.7.22. The AnnotationDefault Attribute
            this.generateTreeNode(rootNode, (AttributeAnnotationDefault) attribute_info);
        } else if (attribute_info instanceof AttributeBootstrapMethods) {
            // 4.7.23. The BootstrapMethods Attribute
            this.generateTreeNode(rootNode, (AttributeBootstrapMethods) attribute_info);

            // 4.7.24. The MethodParameters Attribute
        } else {
            // This is not a standard attribute type defined in the JVM Spec
            this.generateTreeNode(rootNode, (AttributeExtended) attribute_info);
        }
    }

    // 4.7.2. The ConstantValue Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeConstantValue constantValue)
            throws InvalidTreeNodeException {
        int index = constantValue.constantvalue_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                constantValue.getStartPos() + 6,
                2,
                "constantvalue_index: " + index + " - " + this.classFile.getCPDescription(index)
        )));
    }

    // 4.7.3. The Code Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeCode code)
            throws InvalidTreeNodeException {
        int i;
        final int startPos = code.getStartPos();
        final int codeLength = code.code_length.value;
        DefaultMutableTreeNode treeNodeExceptionTable;
        DefaultMutableTreeNode treeNodeExceptionTableItem;
        final int attrCount = code.attributes_count.value;
        DefaultMutableTreeNode treeNodeAttribute;
        DefaultMutableTreeNode treeNodeAttributeItem;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "max_stack: " + code.max_stack.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 8,
                2,
                "max_locals: " + code.max_locals.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 10,
                4,
                "code_length: " + code.code_length.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 14,
                codeLength,
                ATTRIBUTE_CODE_NODE)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 14 + codeLength,
                2,
                "exception_table_length: " + code.exception_table_length.value)));

        // Add exception table
        if (code.exception_table_length.value > 0) {
            treeNodeExceptionTable = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 14 + codeLength + 2,
                    ExceptionTable.LENGTH * code.exception_table_length.value,
                    "exception_table"));

            AttributeCode.ExceptionTable et;
            for (i = 0; i < code.exception_table_length.value; i++) {
                et = code.getExceptionTable(i);

                treeNodeExceptionTableItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        et.getStartPos(),
                        et.getLength(),
                        String.format("[%d]", i)));
                this.generate(treeNodeExceptionTableItem, et);
                treeNodeExceptionTable.add(treeNodeExceptionTableItem);
            }

            rootNode.add(treeNodeExceptionTable);
        }

        // Add attributes
        final int attrStartPos = startPos + 14 + codeLength + 2 + code.exception_table_length.value * ExceptionTable.LENGTH;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                attrStartPos,
                2,
                "attributes_count: " + attrCount)));
        if (attrCount > 0) {
            int attrLength = 0;
            for (AttributeInfo codeAttr : code.attributes) {
                attrLength += codeAttr.getLength();
            }
            treeNodeAttribute = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    attrStartPos + 2,
                    attrLength,
                    "attributes"));

            for (i = 0; i < attrCount; i++) {
                AttributeInfo attr = code.getAttribute(i);
                treeNodeAttributeItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        attr.getStartPos(),
                        attr.getLength(),
                        i + ". " + attr.getName()
                ));
                this.generateTreeNode(treeNodeAttributeItem, attr);
                treeNodeAttribute.add(treeNodeAttributeItem);
            }

            rootNode.add(treeNodeAttribute);
        }

    }

    // 4.7.4. The StackMapTable Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeStackMapTable smt)
            throws InvalidTreeNodeException {

        final int startPos = smt.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "number_of_entries: " + smt.number_of_entries.value
        )));

        if (smt.number_of_entries.value > 0) {

            DefaultMutableTreeNode entries = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    smt.getLength() - 8,
                    "entries"
            ));
            rootNode.add(entries);

            for (int i = 0; i < smt.number_of_entries.value; i++) {
                DefaultMutableTreeNode entry = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        smt.entries[i].getStartPos(),
                        smt.entries[i].getLength(),
                        "entry " + i
                ));
                entries.add(entry);
                this.generate(entry, smt.entries[i]);
            }
        }
    }

    // 4.7.4 - StackMapFrame
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final StackMapFrame smf)
            throws InvalidTreeNodeException {

        int startPos = smf.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                1,
                "frame_type: " + smf.frame_type.value + " - " + StackMapFrame.FrameTypeEnum.getUnionName(smf.frame_type.value)
        )));
        startPos += 1;

        if (smf.union_same_locals_1_stack_item_frame != null) {

            DefaultMutableTreeNode stacks = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    smf.union_same_locals_1_stack_item_frame.stack[0].getLength(),
                    "stack[1]"
            ));
            rootNode.add(stacks);

            DefaultMutableTreeNode stack = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    smf.union_same_locals_1_stack_item_frame.stack[0].getStartPos(),
                    smf.union_same_locals_1_stack_item_frame.stack[0].getLength(),
                    "stack 0"
            ));
            stacks.add(stack);
            this.generate(stack, smf.union_same_locals_1_stack_item_frame.stack[0]);

        } else if (smf.union_same_locals_1_stack_item_frame_extended != null) {
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset_delta: " + smf.union_same_locals_1_stack_item_frame_extended.offset_delta.value
            )));
            startPos += 2;

            DefaultMutableTreeNode stacks = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    smf.union_same_locals_1_stack_item_frame_extended.stack[0].getLength(),
                    "stack[1]"
            ));
            rootNode.add(stacks);

            DefaultMutableTreeNode stack = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    smf.union_same_locals_1_stack_item_frame_extended.stack[0].getStartPos(),
                    smf.union_same_locals_1_stack_item_frame_extended.stack[0].getLength(),
                    "stack 0"
            ));
            stacks.add(stack);
            this.generate(stack, smf.union_same_locals_1_stack_item_frame_extended.stack[0]);

        } else if (smf.union_chop_frame != null) {

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset_delta: " + smf.union_chop_frame.offset_delta.value
            )));
            startPos += 2;

        } else if (smf.union_same_frame_extended != null) {

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset_delta: " + smf.union_same_frame_extended.offset_delta.value
            )));
            startPos += 2;

        } else if (smf.union_append_frame != null) {

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset_delta: " + smf.union_append_frame.offset_delta.value
            )));
            startPos += 2;

            int size_locals = 0;
            if (smf.union_append_frame.locals.length > 0) {
                for (VerificationTypeInfo local : smf.union_append_frame.locals) {
                    size_locals += local.getLength();
                }
                DefaultMutableTreeNode locals = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos,
                        size_locals,
                        "locals[" + smf.union_append_frame.locals.length + "]"
                ));
                startPos += size_locals;
                rootNode.add(locals);

                for (int i = 0; i < smf.union_append_frame.locals.length; i++) {
                    DefaultMutableTreeNode local = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            smf.union_append_frame.locals[i].getStartPos(),
                            smf.union_append_frame.locals[i].getLength(),
                            "local " + (i + 1)
                    ));
                    locals.add(local);
                    this.generate(local, smf.union_append_frame.locals[i]);
                }
            }

        } else if (smf.union_full_frame != null) {

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset_delta: " + smf.union_full_frame.offset_delta.value
            )));
            startPos += 2;

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "number_of_locals: " + smf.union_full_frame.number_of_locals.value
            )));
            startPos += 2;

            int size_locals = 0;
            if (smf.union_full_frame.number_of_locals.value > 0) {
                for (int i = 0; i < smf.union_full_frame.number_of_locals.value; i++) {
                    size_locals += smf.union_full_frame.locals[i].getLength();
                }
                DefaultMutableTreeNode locals = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos,
                        size_locals,
                        "locals[" + smf.union_full_frame.number_of_locals.value + "]"
                ));
                startPos += size_locals;
                rootNode.add(locals);

                for (int i = 0; i < smf.union_full_frame.locals.length; i++) {
                    DefaultMutableTreeNode local = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            smf.union_full_frame.locals[i].getStartPos(),
                            smf.union_full_frame.locals[i].getLength(),
                            "local " + (i + 1)
                    ));
                    locals.add(local);
                    this.generate(local, smf.union_full_frame.locals[i]);
                }
            }

            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "number_of_stack_items: " + smf.union_full_frame.number_of_stack_items.value
            )));
            startPos += 2;

            int size_stack = 0;
            if (smf.union_full_frame.number_of_stack_items.value > 0) {
                for (int i = 0; i < smf.union_full_frame.number_of_stack_items.value; i++) {
                    size_stack += smf.union_full_frame.stack[i].getLength();
                }

                DefaultMutableTreeNode stacks = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos,
                        size_stack,
                        "stack[" + smf.union_full_frame.number_of_stack_items.value + "]"
                ));
                rootNode.add(stacks);

                for (int i = 0; i < smf.union_full_frame.stack.length; i++) {
                    DefaultMutableTreeNode stack = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            smf.union_full_frame.stack[i].getStartPos(),
                            smf.union_full_frame.stack[i].getLength(),
                            "stack " + (i + 1)
                    ));
                    stacks.add(stack);
                    this.generate(stack, smf.union_full_frame.stack[i]);
                }
            }
        } // End union_full_frame
    }

    // 4.7.4 - VerificationTypeInfo
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final VerificationTypeInfo vti)
            throws InvalidTreeNodeException {

        int startPos = vti.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                1,
                "tag: " + vti.tag.value + " - " + VerificationTypeInfo.TagEnum.getTagName(vti.tag.value)
        )));
        startPos += 1;

        if (vti.union_Object_variable_info != null) {
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "cpool_index: " + vti.union_Object_variable_info.cpool_index.value + " - " + this.classFile.getCPDescription(vti.union_Object_variable_info.cpool_index.value)
            )));
        } else if (vti.union_Uninitialized_variable_info != null) {
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    2,
                    "offset: " + vti.union_Uninitialized_variable_info.offset.value
            )));
        }
    }

    // 4.7.5. The Exceptions Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeExceptions exceptions)
            throws InvalidTreeNodeException {
        int i;
        final int startPos = exceptions.getStartPos();
        final int numOfExceptions = exceptions.number_of_exceptions.value;
        DefaultMutableTreeNode treeNodeExceptions;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "number_of_exceptions: " + numOfExceptions
        )));
        if (numOfExceptions > 0) {
            treeNodeExceptions = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    numOfExceptions * 2,
                    "exceptions"));

            for (i = 0; i < numOfExceptions; i++) {
                int cp_index = exceptions.getExceptionIndexTableItem(i);
                treeNodeExceptions.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos + 10 + i * 2,
                        2,
                        String.format("exception_index_table[%d]: cp_index=%d - %s", i, cp_index, this.classFile.getCPDescription(cp_index))
                )));
            }

            rootNode.add(treeNodeExceptions);
        }
    }

    // 4.7.6. The InnerClasses Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeInnerClasses innerClasses)
            throws InvalidTreeNodeException {
        int i;
        final int startPos = innerClasses.getStartPos();
        final int numOfClasses = innerClasses.number_of_classes.value;
        DefaultMutableTreeNode treeNodeInnerClass;
        DefaultMutableTreeNode treeNodeInnerClassItem;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "number_of_classes: " + numOfClasses)));
        if (numOfClasses > 0) {
            treeNodeInnerClass = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    innerClasses.getClass(numOfClasses - 1).getStartPos() + innerClasses.getClass(numOfClasses - 1).getLength() - (startPos + 8),
                    "inner classes"));

            AttributeInnerClasses.Class cls;
            for (i = 0; i < numOfClasses; i++) {
                cls = innerClasses.getClass(i);

                treeNodeInnerClassItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        cls.getStartPos(),
                        cls.getLength(),
                        String.format("inner class %d", i + 1)));
                this.generate(treeNodeInnerClassItem, cls);
                treeNodeInnerClass.add(treeNodeInnerClassItem);
            }

            rootNode.add(treeNodeInnerClass);
        }
    }

    // 4.7.7. The EnclosingMethod Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeEnclosingMethod em)
            throws InvalidTreeNodeException {

        int startPos = em.getStartPos() + 6;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "class_index: " + em.class_index.value + " - " + this.classFile.getCPDescription(em.class_index.value)
        )));
        startPos += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "method_index: " + em.method_index.value + " - " + this.classFile.getCPDescription(em.method_index.value)
        )));
    }

    // 4.7.8. The Synthetic Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeSynthetic synthetic)
            throws InvalidTreeNodeException {
        // Nothing to add
    }

    // 4.7.9. The Signature Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeSignature signature)
            throws InvalidTreeNodeException {

        final int startPos = signature.getStartPos();
        final int sigIndex = signature.signature_index.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "signature_index: " + sigIndex + " - " + this.classFile.getCPDescription(sigIndex)
        )));
    }

    // 4.7.10. The SourceFile Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeSourceFile sourceFile)
            throws InvalidTreeNodeException {
        int cp_index = sourceFile.sourcefile_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                sourceFile.getStartPos() + 6,
                2,
                String.format("sourcefile_index: %d [%s]", cp_index, this.classFile.getCPDescription(cp_index)))));
    }

    // 4.7.11. The SourceDebugExtension Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeSourceDebugExtension sde)
            throws InvalidTreeNodeException {

        if (sde.debug_extension != null && sde.debug_extension.length > 0) {
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    sde.getStartPos() + 6,
                    sde.debug_extension.length,
                    String.format("debug_extension: %s", sde.getDebugExtesionString())
            )));
        }
    }

    // 4.7.12. The LineNumberTable Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeLineNumberTable lineNumberTable)
            throws InvalidTreeNodeException {
        final int startPos = lineNumberTable.getStartPos();
        final int length = lineNumberTable.line_number_table_length.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "line_number_table_length: " + length)));

        if (length > 0) {
            final DefaultMutableTreeNode treeNodeLnt = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    length * 4,
                    "line_number_table"));

            DefaultMutableTreeNode treeNodeLntItem;
            AttributeLineNumberTable.LineNumberTable lnt;
            for (int i = 0; i < length; i++) {
                lnt = lineNumberTable.getLineNumberTable(i);

                treeNodeLntItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        lnt.getStartPos(),
                        lnt.getLength(),
                        String.format("[row %d]", i)));
                this.generate(treeNodeLntItem, lnt);
                treeNodeLnt.add(treeNodeLntItem);
            }

            rootNode.add(treeNodeLnt);
        }
    }

    // 4.7.13. The LocalVariableTable Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeLocalVariableTable localVariableTable)
            throws InvalidTreeNodeException {
        final int startPos = localVariableTable.getStartPos();
        final int length = localVariableTable.local_variable_table_length.value;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "local_variable_table_length: " + length)));

        if (length > 0) {
            final DefaultMutableTreeNode treeNodeLvt = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    length * AttributeLocalVariableTable.LocalVariableTable.LENGTH,
                    "local_variable_table"));

            DefaultMutableTreeNode treeNodeLvtItem;
            AttributeLocalVariableTable.LocalVariableTable lvt;
            for (int i = 0; i < length; i++) {
                lvt = localVariableTable.getLocalVariableTable(i);

                treeNodeLvtItem = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        lvt.getStartPos(),
                        lvt.getLength(),
                        String.format("[%05d]", i)));
                this.generate(treeNodeLvtItem, lvt);
                treeNodeLvt.add(treeNodeLvtItem);
            }

            rootNode.add(treeNodeLvt);
        }
    }

    // 4.7.15. The Deprecated Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeDeprecated deprecated)
            throws InvalidTreeNodeException {
        // Nothing to add
    }

    // 4.7.14. The LocalVariableTypeTable Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeLocalVariableTypeTable lvtt)
            throws InvalidTreeNodeException {

        final int startPos = lvtt.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "local_variable_type_table_length: " + lvtt.local_variable_type_table_length.value
        )));

        if (lvtt.local_variable_type_table_length.value > 0) {
            DefaultMutableTreeNode lvtt_nodes = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    lvtt.local_variable_type_table_length.value * LocalVariableTypeTable.LENGTH,
                    "local_variable_type_table"
            ));
            rootNode.add(lvtt_nodes);

            for (int i = 0; i < lvtt.local_variable_type_table_length.value; i++) {
                LocalVariableTypeTable item = lvtt.local_variable_type_table[i];
                int item_startPos = item.getStartPos();
                DefaultMutableTreeNode lvtt_node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos,
                        item.getLength(),
                        "local_variable_type_table " + (i + 1)
                ));
                lvtt_nodes.add(lvtt_node);

                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos,
                        2,
                        "start_pc: " + item.start_pc.value
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 2,
                        2,
                        "length: " + item.length.value
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 4,
                        2,
                        "signature_index: " + item.signature_index.value + " - " + this.classFile.getCPDescription(item.signature_index.value)
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 6,
                        2,
                        "name_index: " + item.name_index.value + " - " + this.classFile.getCPDescription(item.name_index.value)
                )));
                lvtt_node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        item_startPos + 8,
                        2,
                        "index: " + item.index.value
                )));
            }
        }
    }

    // 4.7.16. The RuntimeVisibleAnnotations Attribute
    // 4.7.17. The RuntimeInvisibleAnnotations Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeAnnotations ra)
            throws InvalidTreeNodeException {

        final int startPos = ra.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "num_annotations: " + ra.num_annotations.value)));

        if (ra.num_annotations.value > 0) {
            DefaultMutableTreeNode annotations = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    ra.getLength() - 8,
                    "annotations"));
            rootNode.add(annotations);

            for (int i = 0; i < ra.num_annotations.value; i++) {
                Annotation a = ra.getAnnotation(i);
                DefaultMutableTreeNode annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        a.getStartPos(),
                        a.getLength(),
                        String.format("annotation %d", i + 1)));
                annotations.add(annotation);
                generate(annotation, a);
            }
        }
    }

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final Annotation a)
            throws InvalidTreeNodeException {
        this.generate(rootNode, a, a.getStartPos());
    }

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final Annotation a,
            int currentPos)
            throws InvalidTreeNodeException {

        // int currentPos = a.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                u2.LENGTH,
                "type_index: " + a.type_index.value + " - " + this.classFile.getCPDescription(a.type_index.value)
        )));
        currentPos += u2.LENGTH;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                u2.LENGTH,
                "num_element_value_pairs: " + a.num_element_value_pairs.value
        )));
        currentPos += u2.LENGTH;

        if (a.num_element_value_pairs.value > 0) {
            DefaultMutableTreeNode element_value_pairs = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    currentPos,
                    a.getStartPos() + a.getLength() - currentPos,
                    "element_value_pairs"));
            rootNode.add(element_value_pairs);

            for (int i = 0; i < a.num_element_value_pairs.value; i++) {
                Annotation.ElementValuePair pair = a.getElementvaluePair(i);
                DefaultMutableTreeNode element_value_pair = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pair.getStartPos(),
                        pair.getLength(),
                        String.format("element_value_pair %d", i + 1)));
                element_value_pairs.add(element_value_pair);
                generate(element_value_pair, pair);
            }
        }
    }

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final Annotation.ElementValuePair pair)
            throws InvalidTreeNodeException {

        int startPos = pair.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "element_name_index: " + pair.element_name_index.value + " - " + this.classFile.getCPDescription(pair.element_name_index.value)
        )));
        DefaultMutableTreeNode value = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                pair.getLength() - 2,
                "value"));
        rootNode.add(value);

        generate(value, pair.value);

    }

    // 4.7.16, 4.7.17:  The RuntimeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final Annotation.ElementValue elementValue)
            throws InvalidTreeNodeException {

        int startPos = elementValue.getStartPos();
        char tag = elementValue.tag;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                1,
                "tag: " + tag + " - " + TagEnum.getType(tag)
        )));
        startPos += 1;

        if (elementValue.union_const_value_index != null) {
            int constValueIndex = elementValue.union_const_value_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "const_value_index: " + constValueIndex + " - " + classFile.getCPDescription(constValueIndex)
            )));
        } else if (elementValue.union_enum_const_value != null) {
            int cp_index = elementValue.union_enum_const_value.type_name_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "type_name_index: " + cp_index + " - " + classFile.getCPDescription(cp_index)
            )));
            startPos += u2.LENGTH;

            cp_index = elementValue.union_enum_const_value.const_name_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "const_name_index: " + cp_index + " - " + classFile.getCPDescription(cp_index)
            )));
        } else if (elementValue.union_class_info_index != null) {
            int classInfoIndex = elementValue.union_class_info_index.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "class_info_index: " + classInfoIndex + " - " + classFile.getCPDescription(classInfoIndex)
            )));
        } else if (elementValue.union_annotation_value != null) {
            this.generate(rootNode, elementValue.union_annotation_value);
        } else if (elementValue.union_array_value != null) {
            int num_values = elementValue.union_array_value.num_values.value;
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "num_values: " + num_values
            )));
            startPos += u2.LENGTH;

            if (elementValue.union_array_value.values != null
                    && elementValue.union_array_value.values.length > 0) {
                DefaultMutableTreeNode values = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos,
                        elementValue.getLength() - 3,
                        "values: " + num_values
                ));
                rootNode.add(values);

                for (int i = 0; i < elementValue.union_array_value.values.length; i++) {
                    DefaultMutableTreeNode value = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            elementValue.union_array_value.values[i].getStartPos(),
                            elementValue.union_array_value.values[i].getLength(),
                            "value " + (i + 1)
                    ));
                    values.add(value);
                    this.generate(value, elementValue.union_array_value.values[i]);
                }
            }
        }
    }

    // 4.7.18. The RuntimeVisibleParameterAnnotations Attribute
    // 4.7.19. The RuntimeInvisibleParameterAnnotations Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeParameterAnnotations rpa)
            throws InvalidTreeNodeException {

        int startPos = rpa.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                1,
                "num_parameters: " + rpa.num_parameters.value)));

        if (rpa.parameter_annotations != null && rpa.parameter_annotations.length > 0) {
            DefaultMutableTreeNode parameter_annotations = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 7,
                    rpa.getLength() - 7,
                    "parameter_annotations"));
            rootNode.add(parameter_annotations);

            for (int i = 0; i < rpa.parameter_annotations.length; i++) {
                DefaultMutableTreeNode parameter_annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        rpa.parameter_annotations[i].getStartPos(),
                        rpa.parameter_annotations[i].getLength(),
                        String.format("parameter_annotation %d", i + 1)));
                parameter_annotations.add(parameter_annotation);
                this.generate(parameter_annotation, rpa.parameter_annotations[i]);
            }
        }
    }

    // 4.7.18, 4.7.19:  The RuntimeParameterAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeParameterAnnotations.ParameterAnnotation pa)
            throws InvalidTreeNodeException {

        int startPos = pa.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "num_annotations: " + pa.num_annotations.value
        )));

        if (pa.annotations != null && pa.annotations.length > 0) {
            DefaultMutableTreeNode annotations = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 2,
                    pa.getLength() - 2,
                    "annotations"));
            rootNode.add(annotations);

            for (int i = 0; i < pa.annotations.length; i++) {
                DefaultMutableTreeNode annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pa.annotations[i].getStartPos(),
                        pa.annotations[i].getLength(),
                        String.format("annotation %d", i + 1)
                ));
                annotations.add(annotation);
                generate(annotation, pa.annotations[i]);
            }
        }
    }

    // 4.7.20. The RuntimeVisibleTypeAnnotations Attribute
    // 4.7.21. The RuntimeInvisibleTypeAnnotations Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeTypeAnnotations rta)
            throws InvalidTreeNodeException {

        int startPos = rta.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                u2.LENGTH,
                "num_annotations: " + rta.num_annotations.value)));

        if (rta.annotations != null && rta.annotations.length > 0) {
            DefaultMutableTreeNode annotations = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    rta.getLength() - 8,
                    "annotations"));
            rootNode.add(annotations);

            for (int i = 0; i < rta.annotations.length; i++) {
                DefaultMutableTreeNode annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        rta.annotations[i].getStartPos(),
                        rta.annotations[i].getLength(),
                        String.format("type_annotation %d", i + 1)));
                annotations.add(annotation);
                this.generate(annotation, rta.annotations[i]);
            }
        }
    }

    // 4.7.20, 4.7.21:  The RuntimeTypeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeTypeAnnotations.TypeAnnotation ta)
            throws InvalidTreeNodeException {

        int startPos = ta.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u1.LENGTH,
                "target_type: 0x" + String.format("%02X", ta.target_type.value)
        )));
        startPos += u1.LENGTH;
        // TODO - Refactor as JTreeNodeFileComponent to set description

        if (ta.union_type_parameter_target != null) {
            DefaultMutableTreeNode type_parameter_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_type_parameter_target.getLength(),
                    "type_parameter_target"));
            rootNode.add(type_parameter_target);
            type_parameter_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "type_parameter_index: " + ta.union_type_parameter_target.type_parameter_index.value
            )));
            startPos += u1.LENGTH;

        } else if (ta.union_supertype_target != null) {
            DefaultMutableTreeNode supertype_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_supertype_target.getLength(),
                    "supertype_target"));
            rootNode.add(supertype_target);
            supertype_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "supertype_index: " + ta.union_supertype_target.supertype_index.value
            )));
            startPos += u2.LENGTH;

        } else if (ta.union_type_parameter_bound_target != null) {
            DefaultMutableTreeNode type_parameter_bound_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_type_parameter_bound_target.getLength(),
                    "type_parameter_bound_target"));
            rootNode.add(type_parameter_bound_target);
            type_parameter_bound_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "type_parameter_index: " + ta.union_type_parameter_bound_target.type_parameter_index.value
            )));
            startPos += u1.LENGTH;
            type_parameter_bound_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "bound_index: " + ta.union_type_parameter_bound_target.bound_index.value
            )));
            startPos += u1.LENGTH;

        } else if (ta.union_empty_target != null) {
            // Do nothing since it is empty
        } else if (ta.union_method_formal_parameter_target != null) {
            DefaultMutableTreeNode formal_parameter_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_method_formal_parameter_target.getLength(),
                    "formal_parameter_target"));
            rootNode.add(formal_parameter_target);
            formal_parameter_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "formal_parameter_index: " + ta.union_method_formal_parameter_target.formal_parameter_index.value
            )));
            startPos += u1.LENGTH;

        } else if (ta.union_throws_target != null) {
            DefaultMutableTreeNode throws_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_throws_target.getLength(),
                    "throws_target"));
            rootNode.add(throws_target);
            throws_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "throws_type_index: " + ta.union_throws_target.throws_type_index.value
            )));
            startPos += u2.LENGTH;

        } else if (ta.union_localvar_target != null) {
            DefaultMutableTreeNode localvar_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_localvar_target.getLength(),
                    "localvar_target"));
            startPos += ta.union_localvar_target.getLength();
            rootNode.add(localvar_target);
            this.generate(localvar_target, ta.union_localvar_target);

        } else if (ta.union_catch_target != null) {
            DefaultMutableTreeNode catch_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_catch_target.getLength(),
                    "catch_target"));
            rootNode.add(catch_target);
            catch_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "exception_table_index: " + ta.union_catch_target.exception_table_index.value
            )));
            startPos += u2.LENGTH;

        } else if (ta.union_offset_target != null) {
            DefaultMutableTreeNode offset_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_offset_target.getLength(),
                    "offset_target"));
            rootNode.add(offset_target);
            offset_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "offset: " + ta.union_offset_target.offset.value
            )));
            startPos += u2.LENGTH;

        } else if (ta.union_type_argument_target != null) {
            DefaultMutableTreeNode type_argument_target = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    ta.union_type_argument_target.getLength(),
                    "type_argument_target"));
            rootNode.add(type_argument_target);
            type_argument_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "offset: " + ta.union_type_argument_target.offset.value
            )));
            startPos += u2.LENGTH;
            type_argument_target.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "type_argument_index: " + ta.union_type_argument_target.type_argument_index.value
            )));
            startPos += u1.LENGTH;
        }

        // target_path
        DefaultMutableTreeNode target_path = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                ta.target_path.getLength(),
                "target_path"));
        startPos += ta.target_path.getLength();
        rootNode.add(target_path);
        this.generate(target_path, ta.target_path);

        // Annotation
        this.generate(rootNode, ta, startPos);
    }

    // 4.7.20. localvar_target
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeTypeAnnotations.TypeAnnotation.LocalvarTarget lt)
            throws InvalidTreeNodeException {

        int startPos = lt.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "table_length: " + lt.table_length.value
        )));
        startPos += u2.LENGTH;

        if (lt.table == null || lt.table.length < 1) {
            return;
        }

        DefaultMutableTreeNode tables = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                lt.getLength() - u2.LENGTH,
                String.format("table[%d]", lt.table.length)
        ));
        rootNode.add(tables);
        for (int i = 0; i < lt.table.length; i++) {
            startPos = lt.table[i].getStartPos();
            DefaultMutableTreeNode table = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    lt.table[i].getLength(),
                    String.format("table[%d]", i + 1)
            ));
            tables.add(table);

            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "start_pc: " + lt.table[i].start_pc.value
            )));
            startPos += u2.LENGTH;
            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "length: " + lt.table[i].length.value
            )));
            startPos += u2.LENGTH;
            table.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u2.LENGTH,
                    "index: " + lt.table[i].index.value
            )));
        }

    }

    // 4.7.20, 4.7.21:  The RuntimeTypeAnnotations Attribute
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeRuntimeTypeAnnotations.TypeAnnotation.TypePath tp)
            throws InvalidTreeNodeException {

        int startPos = tp.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u1.LENGTH,
                "path_length: " + tp.path_length.value
        )));
        startPos += u1.LENGTH;

        if (tp.path == null || tp.path.length < 1) {
            return;
        }

        DefaultMutableTreeNode paths = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                tp.getLength() - u1.LENGTH,
                String.format("path[%d]", tp.path.length)
        ));
        rootNode.add(paths);
        for (int i = 0; i < tp.path.length; i++) {
            startPos = tp.path[i].getStartPos();
            DefaultMutableTreeNode path = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    tp.path[i].getLength(),
                    String.format("path[%d]", i + 1)
            ));
            paths.add(path);

            path.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "type_path_kind: " + tp.path[i].type_path_kind.value
            )));
            startPos += u1.LENGTH;
            path.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    u1.LENGTH,
                    "type_argument_index: " + tp.path[i].type_argument_index.value
            )));
        }
    }

    // 4.7.22. The AnnotationDefault Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeAnnotationDefault ad)
            throws InvalidTreeNodeException {

        DefaultMutableTreeNode default_value = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                ad.getStartPos() + 6,
                ad.getLength() - 6,
                "default_value"
        ));
        rootNode.add(default_value);
        this.generate(default_value, ad.default_value);
    }

    // 4.7.23. The BootstrapMethods Attribute
    private void generateTreeNode(
            final DefaultMutableTreeNode rootNode,
            final AttributeBootstrapMethods abm)
            throws InvalidTreeNodeException {

        int startPos = abm.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "num_bootstrap_methods: " + abm.num_bootstrap_methods.value
        )));

        if (abm.bootstrap_methods != null && abm.bootstrap_methods.length > 0) {
            DefaultMutableTreeNode bootstrap_methods = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos + 8,
                    abm.getLength() - 8,
                    "bootstrap_methods"));
            rootNode.add(bootstrap_methods);

            for (int i = 0; i < abm.bootstrap_methods.length; i++) {
                BootstrapMethod m = abm.bootstrap_methods[i];
                DefaultMutableTreeNode bootstrap_method = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        m.getStartPos(),
                        m.getLength(),
                        String.format("bootstrap_method %d", i + 1)));
                bootstrap_methods.add(bootstrap_method);
                this.generate(bootstrap_method, m);
            }
        }
    }

    // 4.7.23 BootstrapMethods
    private void generate(
            final DefaultMutableTreeNode rootNode,
            final BootstrapMethod m)
            throws InvalidTreeNodeException {

        int startPos = m.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "bootstrap_method_ref: " + m.bootstrap_method_ref.value + " - " + this.classFile.getCPDescription(m.bootstrap_method_ref.value)
        )));
        startPos += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                u2.LENGTH,
                "num_bootstrap_arguments: " + m.num_bootstrap_arguments.value
        )));
        startPos += u2.LENGTH;

        if (m.bootstrap_arguments != null && m.bootstrap_arguments.length > 0) {
            DefaultMutableTreeNode bootstrap_arguments = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPos,
                    m.bootstrap_arguments.length * u2.LENGTH,
                    "bootstrap_arguments"));
            rootNode.add(bootstrap_arguments);

            for (int i = 0; i < m.bootstrap_arguments.length; i++) {
                DefaultMutableTreeNode bootstrap_method = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPos,
                        u2.LENGTH,
                        "argument " + (i + 1) + ": " + m.bootstrap_arguments[i].value + " - " + this.classFile.getCPDescription(m.bootstrap_arguments[i].value)
                ));
                startPos += u2.LENGTH;
                bootstrap_arguments.add(bootstrap_method);
            }
        }
    }

    // Other Unknown Attributes
    private void generateTreeNode(final DefaultMutableTreeNode rootNode, final AttributeExtended unknown)
            throws InvalidTreeNodeException {
        if (unknown.attribute_length.value > 0) {
            rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    unknown.getStartPos() + 6,
                    unknown.attribute_length.value,
                    "raw data"
            )));
        }
    }

    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeCode.ExceptionTable et)
            throws InvalidTreeNodeException {
        if (et == null) {
            return;
        }

        final int startPos = et.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "start_pc: " + et.start_pc.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "end_pc: " + et.end_pc.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                "handler_pc: " + et.handler_pc.value)));
        int catch_type = et.catch_type.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "catch_type: " + catch_type + " - " + this.classFile.getCPDescription(catch_type)
        )));
    }

    private void generate(final DefaultMutableTreeNode rootNode, final AttributeInnerClasses.Class innerClass)
            throws InvalidTreeNodeException {
        final int startPos = innerClass.getStartPos();

        int cp_index = innerClass.inner_class_info_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "inner_class_info_index: " + cp_index + " - " + this.classFile.getCPDescription(cp_index)
        )));

        cp_index = innerClass.outer_class_info_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "outer_class_info_index: " + cp_index + " - " + this.classFile.getCPDescription(cp_index)
        )));

        cp_index = innerClass.inner_name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                "inner_name_index: " + cp_index + " - " + this.classFile.getCPDescription(cp_index)
        )));

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                "inner_class_access_flags: " + innerClass.inner_class_access_flags.value + " - " + innerClass.getModifiers()
        )));
    }

    private void generate(final DefaultMutableTreeNode rootNode, final AttributeLineNumberTable.LineNumberTable lnt)
            throws InvalidTreeNodeException {
        if (lnt == null) {
            return;
        }

        final int startPos = lnt.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "start_pc: " + lnt.start_pc.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "line_number: " + lnt.line_number.value)));
    }

    private void generate(
            final DefaultMutableTreeNode rootNode,
            final AttributeLocalVariableTable.LocalVariableTable lvt)
            throws InvalidTreeNodeException {
        if (lvt == null) {
            return;
        }

        final int startPos = lvt.getStartPos();
        int cp_index;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos,
                2,
                "start_pc: " + lvt.start_pc.value)));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 2,
                2,
                "length: " + lvt.length.value)));
        cp_index = lvt.name_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 4,
                2,
                String.format("name_index: %d - %s", cp_index, this.classFile.getCPDescription(cp_index)))));
        cp_index = lvt.descriptor_index.value;
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 6,
                2,
                String.format("descriptor_index: %d - %s", cp_index, this.classFile.getCPDescription(cp_index)))));
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPos + 8,
                2,
                "index: " + lvt.index.value)));
    }
}
