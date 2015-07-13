/*
 * CPInfo.java    3:44 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.FileComponent;

/**
 * The super class for constant pool items in class file. All constant pool
 * items have the following format:
 *
 * <pre>
 *    cp_info {
 *        u1 tag;
 *        u1 info[];
 *    }
 * </pre>
 *
 * The contents in {@code info} is determined by {@code tag}.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4">
 * VM Spec: The Constant Pool
 * </a>
 */
public abstract class CPInfo extends FileComponent {

    /**
     * Each item in the {@link ClassFile#constant_pool} table must begin with a
     * 1-byte {@link #tag} indicating the kind of <code>cp_info</code> entry.
     *
     * The value come from the enum field {@link ConstantType#tag}.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4">
     * VM Spec: The Constant Pool
     * </a>
     */
    public final transient u1 tag;

    CPInfo(short tag) {
        this.tag = new u1(tag);
    }

    /**
     * Get the name of current constant pool item.
     *
     * @return Name of the constant pool item
     */
    public abstract String getName();

    /**
     * Get a detailed, technical description of the constant pool item.
     *
     * @return Detailed, technical description of the item
     */
    public abstract String getDescription();

    /**
     * Constant pool tags.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4">
     * VM Spec: The Constant Pool
     * </a>
     */
    public enum ConstantType {

        /**
         * The value for constant type {@code CONSTANT_Utf8}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7">
         * VM Spec: The CONSTANT_Utf8_info Structure
         * </a>
         */
        CONSTANT_Utf8(1),
        /**
         * The value for constant type {@code CONSTANT_Integer}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Integer(3),
        /**
         * The value for constant type {@code CONSTANT_Float}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Float(4),
        /**
         * The value for constant type {@code CONSTANT_Long}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Long(5),
        /**
         * The value for constant type {@code CONSTANT_Double}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Double(6),
        /**
         * The value for constant type {@code CONSTANT_Class}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.1">
         * VM Spec: The CONSTANT_Class_info Structure
         * </a>
         */
        CONSTANT_Class(7),
        /**
         * The value for constant type {@code CONSTANT_String}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.3">
         * VM Spec: The CONSTANT_String_info Structure
         * </a>
         */
        CONSTANT_String(8),
        /**
         * The value for constant type {@code CONSTANT_Fieldref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Fieldref(9),
        /**
         * The value for constant type {@code CONSTANT_Methodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Methodref(10),
        /**
         * The value for constant type {@code CONSTANT_InterfaceMethodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_InterfaceMethodref(11),
        /**
         * The value for constant type {@code CONSTANT_NameAndType}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.6">
         * VM Spec: The CONSTANT_NameAndType_info Structure
         * </a>
         */
        CONSTANT_NameAndType(12),
        /**
         * The value for constant type {@code CONSTANT_MethodHandle}.
         *
         * @see
         * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.8">
         * VM Spec: The CONSTANT_MethodHandle_info Structure
         * </a>
         */
        CONSTANT_MethodHandle(15),
        /**
         * The value for constant type {@code CONSTANT_MethodType}.
         *
         * @see
         * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.9">
         * VM Spec: The CONSTANT_MethodType_info Structure
         * </a>
         */
        CONSTANT_MethodType(16),
        /**
         * The value for constant type {@code CONSTANT_InvokeDynamic}.
         *
         * @see
         * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.10">
         * VM Spec: The CONSTANT_InvokeDynamic Structure
         * </a>
         */
        CONSTANT_InvokeDynamic(18);

        /**
         * Value of tag.
         */
        public final short tag;

        private ConstantType(int s) {
            this.tag = (short) s;
        }
    }
}
