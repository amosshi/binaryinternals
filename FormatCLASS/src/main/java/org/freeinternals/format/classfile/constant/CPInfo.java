/*
 * CPInfo.java    3:44 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u1;

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
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4">
 * VM Spec: The Constant Pool
 * </a>
 */
public abstract class CPInfo extends FileComponent {

    /**
     * Each item in the {@link ClassFile#constant_pool} table must begin with a
     * 1-byte {@link #tag} indicating the kind of <code>cp_info</code> entry.
     *
     * The value come from the enum field {@link ConstantType#tag}.
     */
    public final transient u1 tag;

    /**
     * Some entries in the constant_pool table are loadable because they
     * represent entities that can be pushed onto the stack at run time to
     * enable further computation.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4-310">
     * VM Spec: Table 4.4-C. Loadable constant pool tags
     * </a>
     */
    public final boolean loadable;

    /**
     * Class file format.
     */
    public final ClassFile.Version format;

    /**
     * Java SE platform version.
     */
    public final JavaSEVersion javaSE;

    CPInfo(short tag, boolean loadable, ClassFile.Version format, JavaSEVersion javaSE) {
        this.tag = new u1(tag);

        this.loadable = loadable;
        this.format = format;
        this.javaSE = javaSE;
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4">
     * VM Spec: The Constant Pool
     * </a>
     */
    public enum ConstantType {

        /**
         * The value for constant type {@code CONSTANT_Utf8}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.7">
         * VM Spec: The CONSTANT_Utf8_info Structure
         * </a>
         */
        CONSTANT_Utf8(1, ConstantUtf8Info.class),
        /**
         * The value for constant type {@code CONSTANT_Integer}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Integer(3, ConstantIntegerInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Float}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Float(4, ConstantFloatInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Long}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Long(5, ConstantLongInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Double}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Double(6, ConstantDoubleInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Class}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.1">
         * VM Spec: The CONSTANT_Class_info Structure
         * </a>
         */
        CONSTANT_Class(7, ConstantClassInfo.class),
        /**
         * The value for constant type {@code CONSTANT_String}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.3">
         * VM Spec: The CONSTANT_String_info Structure
         * </a>
         */
        CONSTANT_String(8, ConstantStringInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Fieldref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Fieldref(9, ConstantFieldrefInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Methodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Methodref(10, ConstantMethodrefInfo.class),
        /**
         * The value for constant type {@code CONSTANT_InterfaceMethodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_InterfaceMethodref(11, ConstantInterfaceMethodrefInfo.class),
        /**
         * The value for constant type {@code CONSTANT_NameAndType}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.6">
         * VM Spec: The CONSTANT_NameAndType_info Structure
         * </a>
         */
        CONSTANT_NameAndType(12, ConstantNameAndTypeInfo.class),
        /**
         * The value for constant type {@code CONSTANT_MethodHandle}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.8">
         * VM Spec: The CONSTANT_MethodHandle_info Structure
         * </a>
         */
        CONSTANT_MethodHandle(15, ConstantMethodHandleInfo.class),
        /**
         * The value for constant type {@code CONSTANT_MethodType}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.9">
         * VM Spec: The CONSTANT_MethodType_info Structure
         * </a>
         */
        CONSTANT_MethodType(16, ConstantMethodTypeInfo.class),
        /**
         * The value for constant type {@code CONSTANT_Dynamic}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.10">
         * VM Spec: The CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info
         * Structures
         * </a>
         */
        CONSTANT_Dynamic(17, ConstantDynamicInfo.class),
        /**
         * The value for constant type {@code CONSTANT_InvokeDynamic}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.10">
         * VM Spec: The CONSTANT_InvokeDynamic Structure
         * </a>
         */
        CONSTANT_InvokeDynamic(18, ConstantInvokeDynamicInfo.class),
        /**
         * The CONSTANT_Module_info structure is used to represent a module.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.11">
         * VM Spec: The CONSTANT_Module_info Structure
         * </a>
         */
        CONSTANT_Module(19, ConstantModuleInfo.class),
        /**
         * The CONSTANT_Package_info structure is used to represent a package
         * exported or opened by a module.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.12">
         * VM Spec: The CONSTANT_Package_info Structure
         * </a>
         */
        CONSTANT_Package(20, ConstantPackageInfo.class);

        private static final Logger Log = Logger.getLogger(ConstantType.class.getName());

        /**
         * Value of tag.
         */
        public final short tag;

        /**
         * The Java class representing to the attributes.
         */
        final Class<?> clazz;

        private ConstantType(int tag, Class<?> clz) {
            this.tag = (short) tag;
            this.clazz = clz;
        }

        /**
         * Get the {@link ConstantType} name based on its internal {@link #tag}
         * value.
         *
         * @param tag Internal {@link #tag} value
         * @return {@link ConstantType} name
         */
        public static String name(int tag) {
            String result = "Un-recognized";
            for (ConstantType item : ConstantType.values()) {
                if (item.tag == tag) {
                    result = item.name();
                    break;
                }
            }
            return result;
        }

        /**
         * Parse a constant pool item.
         */
        public static CPInfo parse(int tag, final PosDataInputStream posDataInputStream) throws FileFormatException {
            CPInfo cpInfo = null;

            for (ConstantType item : ConstantType.values()) {
                if (item.tag == tag) {
                    // There is only 1 constructor in the JVM Attributes
                    Constructor cons = item.clazz.getDeclaredConstructors()[0];
                    try {
                        cpInfo = (CPInfo) cons.newInstance(posDataInputStream);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Log.log(Level.SEVERE, "Failed to parse the JVM Constant Tag at position {0}, {1}", new Object[]{posDataInputStream.getPos() - 1, ex.toString()});
                    }
                }
            }

            if (cpInfo == null) {
                throw new FileFormatException(String.format("Unreconizable constant pool type found. Constant pool tag: [%d]; class file offset: [%d].", tag, posDataInputStream.getPos() - 1));
            } else {
                return cpInfo;
            }
        }
    }
}
