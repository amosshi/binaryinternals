/*
 * CPInfo.java    3:44 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import java.lang.classfile.constantpool.PoolEntry;
import java.lang.reflect.ClassFileFormatVersion;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u1;

/// The super class for constant pool items in class file. All constant pool
/// items have the following format:
///
/// ```
///    cp_info {
///        u1 tag;
///        u1 info[];
///    }
/// ```
///
/// The contents in `info` is determined by `tag`.
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4">
/// VM Spec: The Constant Pool
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings("java:S101")
public abstract class cp_info extends FileComponent implements GenerateTreeNodeClassFile {

    /// Warning message for un-recognized type.
    protected static final String UNRECOGNIZED_TYPE = " !!! Un-recognized type";

    /// Each item in the {@link ClassFile#constant_pool} table must begin with a
    /// 1-byte {@link #tag} indicating the kind of `cp_info` entry.
    ///
    /// The value come from the enum field {@link ConstantType#tag}.
    public final u1 tag;

    cp_info(short tag) {
        this.tag = new u1(tag);
    }

    /// Creates a constant pool item of a fixed length.
    ///
    /// @param tag Constant pool tag
    /// @param startPos Start position of the item, which is the position of the tag
    /// @param length Length of the item, including the tag
    cp_info(short tag, int startPos, int length) {
        super(startPos, length);
        this.tag = new u1(tag);
    }

    /// Get a detailed, technical description of the constant pool item.
    ///
    /// @return Detailed, technical description of the item
    public abstract String getDescription();

    /// Get the name of current constant pool item.
    ///
    /// @return Name of the constant pool item
    public abstract String getName();

    /// The get the Message key which contains the corresponding description of current attribute.
    ///
    /// @return Message key
    public abstract String getMessageKey();

    /// Get a human reader friendly of current constant pool item.
    ///
    /// @param constantPool Constant Pool items needed
    /// @return Reader friendly string
    public abstract String toString(cp_info[] constantPool);

    /// Constant pool tags.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4">
    /// VM Spec: The Constant Pool
    /// </a>
    ///
    /// ```
    /// java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
    /// ```
    @SuppressWarnings("java:S115")
    public enum ConstantType {

        /// The value for constant type `CONSTANT_Utf8`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.7">
        /// VM Spec: The CONSTANT_Utf8_info Structure
        /// </a>
        CONSTANT_Utf8(PoolEntry.TAG_UTF8, ClassFileFormatVersion.RELEASE_1, false),
        /// The value for constant type `CONSTANT_Integer`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.4">
        /// VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
        /// </a>
        CONSTANT_Integer(PoolEntry.TAG_INTEGER, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_Float`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.4">
        /// VM Spec: The CONSTANT_Integer_info and CONSTANT_Float_info Structures
        /// </a>
        CONSTANT_Float(PoolEntry.TAG_FLOAT, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_Long`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.5">
        /// VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
        /// </a>
        CONSTANT_Long(PoolEntry.TAG_LONG, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_Double`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.5">
        /// VM Spec: The CONSTANT_Long_info and CONSTANT_Double_info Structures
        /// </a>
        CONSTANT_Double(PoolEntry.TAG_DOUBLE, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_Class`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.1">
        /// VM Spec: The CONSTANT_Class_info Structure
        /// </a>
        CONSTANT_Class(PoolEntry.TAG_CLASS, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_String`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.3">
        /// VM Spec: The CONSTANT_String_info Structure
        /// </a>
        CONSTANT_String(PoolEntry.TAG_STRING, ClassFileFormatVersion.RELEASE_1, true),
        /// The value for constant type `CONSTANT_Fieldref`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.2">
        /// VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
        /// CONSTANT_InterfaceMethodref_info Structures
        /// </a>
        CONSTANT_Fieldref(PoolEntry.TAG_FIELDREF, ClassFileFormatVersion.RELEASE_1, false),
        /// The value for constant type `CONSTANT_Methodref`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.2">
        /// VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
        /// CONSTANT_InterfaceMethodref_info Structures
        /// </a>
        CONSTANT_Methodref(PoolEntry.TAG_METHODREF, ClassFileFormatVersion.RELEASE_1, false),
        /// The value for constant type `CONSTANT_InterfaceMethodref`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.2">
        /// VM Spec: The CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and
        /// CONSTANT_InterfaceMethodref_info Structures
        /// </a>
        CONSTANT_InterfaceMethodref(PoolEntry.TAG_INTERFACE_METHODREF, ClassFileFormatVersion.RELEASE_1, false),
        /// The value for constant type `CONSTANT_NameAndType`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.6">
        /// VM Spec: The CONSTANT_NameAndType_info Structure
        /// </a>
        CONSTANT_NameAndType(PoolEntry.TAG_NAME_AND_TYPE, ClassFileFormatVersion.RELEASE_1, false),
        /// The value for constant type `CONSTANT_MethodHandle`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.8">
        /// VM Spec: The CONSTANT_MethodHandle_info Structure
        /// </a>
        CONSTANT_MethodHandle(PoolEntry.TAG_METHOD_HANDLE, ClassFileFormatVersion.RELEASE_7, true),
        /// The value for constant type `CONSTANT_MethodType`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.9">
        /// VM Spec: The CONSTANT_MethodType_info Structure
        /// </a>
        CONSTANT_MethodType(PoolEntry.TAG_METHOD_TYPE, ClassFileFormatVersion.RELEASE_7, true),
        /// The value for constant type `CONSTANT_Dynamic`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.10">
        /// VM Spec: The CONSTANT_Dynamic_info and CONSTANT_InvokeDynamic_info
        /// Structures
        /// </a>
        CONSTANT_Dynamic(PoolEntry.TAG_DYNAMIC, ClassFileFormatVersion.RELEASE_11, true),
        /// The value for constant type `CONSTANT_InvokeDynamic`.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.10">
        /// VM Spec: The CONSTANT_InvokeDynamic Structure
        /// </a>
        CONSTANT_InvokeDynamic(PoolEntry.TAG_INVOKE_DYNAMIC, ClassFileFormatVersion.RELEASE_7, false),
        /// The CONSTANT_Module_info structure is used to represent a module.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.11">
        /// VM Spec: The CONSTANT_Module_info Structure
        /// </a>
        CONSTANT_Module(PoolEntry.TAG_MODULE, ClassFileFormatVersion.RELEASE_9, false),
        /// The CONSTANT_Package_info structure is used to represent a package
        /// exported or opened by a module.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.12">
        /// VM Spec: The CONSTANT_Package_info Structure
        /// </a>
        CONSTANT_Package(PoolEntry.TAG_PACKAGE, ClassFileFormatVersion.RELEASE_9, false);

        /// Value of tag.
        public final short tag;

        /// Class file format.
        public final ClassFileFormatVersion format;

        /// Some entries in the constant_pool table are loadable because they
        /// represent entities that can be pushed onto the stack at run time to
        /// enable further computation.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4-310">
        /// VM Spec: Table 4.4-C. Loadable constant pool tags
        /// </a>
        public final boolean loadable;

        private ConstantType(int tag, ClassFileFormatVersion format, boolean loadable) {
            this.tag = (short) tag;
            this.format = format;
            this.loadable = loadable;
        }

        /// Get the {@link ConstantType} based on its internal {@link #tag}
        /// value.
        ///
        /// @param tag Internal {@link #tag} value
        /// @return Corresponding {@link ConstantType}
        public static ConstantType valueOf(int tag) {
            for (ConstantType item : ConstantType.values()) {
                if (item.tag == tag) {
                    return item;
                }
            }

            throw new IllegalArgumentException("Invalid tag value: " + tag);
        }

        /// Parse a constant pool item.
        ///
        /// @param tag Constant pool item tag, indicating the type of the item
        /// @param posDataInputStream Class file byte stream
        /// @return Parsed constant pool info
        /// @throws IOException Failed to read the class file
        /// @throws FileFormatException An invalid class file format encountered
        public static cp_info parse(final int tag, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            final int tagPos = posDataInputStream.getPos() - 1;
            try {
                return switch (tag) {
                    case PoolEntry.TAG_UTF8 -> new CONSTANT_Utf8_info(posDataInputStream);
                    case PoolEntry.TAG_INTEGER -> new CONSTANT_Integer_info(posDataInputStream);
                    case PoolEntry.TAG_FLOAT -> new CONSTANT_Float_info(posDataInputStream);
                    case PoolEntry.TAG_LONG -> new CONSTANT_Long_info(posDataInputStream);
                    case PoolEntry.TAG_DOUBLE -> new CONSTANT_Double_info(posDataInputStream);
                    case PoolEntry.TAG_CLASS -> new CONSTANT_Class_info(posDataInputStream);
                    case PoolEntry.TAG_STRING -> new CONSTANT_String_info(posDataInputStream);
                    case PoolEntry.TAG_FIELDREF -> new CONSTANT_Fieldref_info(posDataInputStream);
                    case PoolEntry.TAG_METHODREF -> new CONSTANT_Methodref_info(posDataInputStream);
                    case PoolEntry.TAG_INTERFACE_METHODREF -> new CONSTANT_InterfaceMethodref_info(posDataInputStream);
                    case PoolEntry.TAG_NAME_AND_TYPE -> new CONSTANT_NameAndType_info(posDataInputStream);
                    case PoolEntry.TAG_METHOD_HANDLE -> new CONSTANT_MethodHandle_info(posDataInputStream);
                    case PoolEntry.TAG_METHOD_TYPE -> new CONSTANT_MethodType_info(posDataInputStream);
                    case PoolEntry.TAG_DYNAMIC -> new CONSTANT_Dynamic_info(posDataInputStream);
                    case PoolEntry.TAG_INVOKE_DYNAMIC -> new CONSTANT_InvokeDynamic_info(posDataInputStream);
                    case PoolEntry.TAG_MODULE -> new CONSTANT_Module_info(posDataInputStream);
                    case PoolEntry.TAG_PACKAGE -> new CONSTANT_Package_info(posDataInputStream);
                    default -> throw new FileFormatException(
                            "Unrecognizable constant pool type found. Constant pool tag: [%d]; class file offset: [%d].".formatted(tag, tagPos));
                };
            } catch (RuntimeException ex) {
                throw new FileFormatException("Failed to parse the constant pool tag %d at position %d".formatted(tag, tagPos), ex);
            }
        }
    }
}
