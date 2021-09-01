/*
 * CPInfo.java    3:44 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;

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
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public abstract class cp_info extends FileComponent implements GenerateTreeNodeClassFile {

    /**
     * Warning message for un-recognized type.
     */
    protected static final String UNRECOGNIZED_TYPE = " !!! Un-recognized type";

    /**
     * Each item in the {@link ClassFile#constant_pool} table must begin with a
     * 1-byte {@link #tag} indicating the kind of <code>cp_info</code> entry.
     *
     * The value come from the enum field {@link ConstantType#tag}.
     */
    public final u1 tag;

    cp_info(short tag) {
        this.tag = new u1(tag);
    }

    /**
     * Get a detailed, technical description of the constant pool item.
     *
     * @return Detailed, technical description of the item
     */
    public abstract String getDescription();

    /**
     * Get the name of current constant pool item.
     *
     * @return Name of the constant pool item
     */
    public abstract String getName();

    /**
     * The get the Message key which contains the corresponding description of current attribute.
     *
     * @return Message key
     */
    public abstract String getMessageKey();

    /**
     * Get a human reader friendly of current constant pool item.
     *
     * @param constantPool Constant Pool items needed
     * @return Reader friendly string
     */
    public abstract String toString(cp_info[] constantPool);

    /**
     * Constant pool tags.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4">
     * VM Spec: The Constant Pool
     * </a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum ConstantType {

        /**
         * The value for constant type {@code CONSTANT_Utf8}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.7">
         * VM Spec: The CONSTANT_Utf8_info Structure
         * </a>
         */
        CONSTANT_Utf8(1, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, false, CONSTANT_Utf8_info.class),
        /**
         * The value for constant type {@code CONSTANT_Integer}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Integer(3, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_Integer_info.class),
        /**
         * The value for constant type {@code CONSTANT_Float}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.4">
         * VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
         * </a>
         */
        CONSTANT_Float(4, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_Float_info.class),
        /**
         * The value for constant type {@code CONSTANT_Long}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Long(5, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_Long_info.class),
        /**
         * The value for constant type {@code CONSTANT_Double}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.5">
         * VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
         * </a>
         */
        CONSTANT_Double(6, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_Double_info.class),
        /**
         * The value for constant type {@code CONSTANT_Class}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.1">
         * VM Spec: The CONSTANT_Class_info Structure
         * </a>
         */
        CONSTANT_Class(7, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_Class_info.class),
        /**
         * The value for constant type {@code CONSTANT_String}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.3">
         * VM Spec: The CONSTANT_String_info Structure
         * </a>
         */
        CONSTANT_String(8, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, true, CONSTANT_String_info.class),
        /**
         * The value for constant type {@code CONSTANT_Fieldref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Fieldref(9, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, false, CONSTANT_Fieldref_info.class),
        /**
         * The value for constant type {@code CONSTANT_Methodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_Methodref(10, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, false, CONSTANT_Methodref_info.class),
        /**
         * The value for constant type {@code CONSTANT_InterfaceMethodref}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.2">
         * VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
         * CONSTANT_InterfaceMethodref_info Structures
         * </a>
         */
        CONSTANT_InterfaceMethodref(11, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, false, CONSTANT_InterfaceMethodref_info.class),
        /**
         * The value for constant type {@code CONSTANT_NameAndType}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.6">
         * VM Spec: The CONSTANT_NameAndType_info Structure
         * </a>
         */
        CONSTANT_NameAndType(12, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2, false, CONSTANT_NameAndType_info.class),
        /**
         * The value for constant type {@code CONSTANT_MethodHandle}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.8">
         * VM Spec: The CONSTANT_MethodHandle_info Structure
         * </a>
         */
        CONSTANT_MethodHandle(15, ClassFile.FormatVersion.FORMAT_51, JavaSEVersion.VERSION_7, true, CONSTANT_MethodHandle_info.class),
        /**
         * The value for constant type {@code CONSTANT_MethodType}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.9">
         * VM Spec: The CONSTANT_MethodType_info Structure
         * </a>
         */
        CONSTANT_MethodType(16, ClassFile.FormatVersion.FORMAT_51, JavaSEVersion.VERSION_7, true, CONSTANT_MethodType_info.class),
        /**
         * The value for constant type {@code CONSTANT_Dynamic}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.10">
         * VM Spec: The CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info
         * Structures
         * </a>
         */
        CONSTANT_Dynamic(17, ClassFile.FormatVersion.FORMAT_55, JavaSEVersion.VERSION_11, true, CONSTANT_Dynamic_info.class),
        /**
         * The value for constant type {@code CONSTANT_InvokeDynamic}.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.10">
         * VM Spec: The CONSTANT_InvokeDynamic Structure
         * </a>
         */
        CONSTANT_InvokeDynamic(18, ClassFile.FormatVersion.FORMAT_51, JavaSEVersion.VERSION_7, false, CONSTANT_InvokeDynamic_info.class),
        /**
         * The CONSTANT_Module_info structure is used to represent a module.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.11">
         * VM Spec: The CONSTANT_Module_info Structure
         * </a>
         */
        CONSTANT_Module(19, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9, false, CONSTANT_Module_info.class),
        /**
         * The CONSTANT_Package_info structure is used to represent a package
         * exported or opened by a module.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.12">
         * VM Spec: The CONSTANT_Package_info Structure
         * </a>
         */
        CONSTANT_Package(20, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9, false, CONSTANT_Package_info.class);

        private static final Logger Log = Logger.getLogger(ConstantType.class.getName());

        /**
         * Value of tag.
         */
        public final short tag;

        /**
         * Class file format.
         */
        public final ClassFile.FormatVersion format;

        /**
         * Java SE platform version.
         */
        public final JavaSEVersion javaSE;

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
         * The Java class representing to the attributes.
         */
        final Class<?> clazz;

        private ConstantType(int tag, ClassFile.FormatVersion format, JavaSEVersion javaSE, boolean loadable, Class<?> clz) {
            this.tag = (short) tag;
            this.format = format;
            this.javaSE = javaSE;
            this.loadable = loadable;
            this.clazz = clz;
        }

        /**
         * Get the {@link ConstantType} based on its internal {@link #tag}
         * value.
         *
         * @param tag Internal {@link #tag} value
         * @return Corresponding {@link ConstantType}
         */
        public static ConstantType valueOf(int tag) {
            for (ConstantType item : ConstantType.values()) {
                if (item.tag == tag) {
                    return item;
                }
            }

            throw new IllegalArgumentException("Invalid tag value: " + tag);
        }

        /**
         * Parse a constant pool item.
         *
         * @param tag Constant pool item tag, indicating the type of the item
         * @param posDataInputStream Class file byte stream
         * @return Parsed constant pool info
         * @throws FileFormatException An invalid class file format encountered
         */
        public static cp_info parse(int tag, final PosDataInputStream posDataInputStream) throws FileFormatException {
            cp_info cpInfo = null;

            for (ConstantType item : ConstantType.values()) {
                if (item.tag == tag) {
                    try {
                        // There is only 1 constructor in the JVM Attributes
                        cpInfo = (cp_info) item.clazz.getDeclaredConstructors()[0].newInstance(posDataInputStream);
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
