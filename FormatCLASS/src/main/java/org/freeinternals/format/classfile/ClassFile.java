/*
 * ClassFile.java    2:58 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.attribute.AttributeInfo;
import org.freeinternals.format.classfile.constant.CPInfo;
import org.freeinternals.format.classfile.constant.CPInfo.ConstantType;
import org.freeinternals.format.classfile.constant.ConstantClassInfo;
import org.freeinternals.format.classfile.constant.ConstantDoubleInfo;
import org.freeinternals.format.classfile.constant.ConstantDynamicInfo;
import org.freeinternals.format.classfile.constant.ConstantFieldrefInfo;
import org.freeinternals.format.classfile.constant.ConstantFloatInfo;
import org.freeinternals.format.classfile.constant.ConstantIntegerInfo;
import org.freeinternals.format.classfile.constant.ConstantInterfaceMethodrefInfo;
import org.freeinternals.format.classfile.constant.ConstantInvokeDynamicInfo;
import org.freeinternals.format.classfile.constant.ConstantLongInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodHandleInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodTypeInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodrefInfo;
import org.freeinternals.format.classfile.constant.ConstantModuleInfo;
import org.freeinternals.format.classfile.constant.ConstantNameAndTypeInfo;
import org.freeinternals.format.classfile.constant.ConstantPackageInfo;
import org.freeinternals.format.classfile.constant.ConstantStringInfo;
import org.freeinternals.format.classfile.constant.ConstantUtf8Info;

/**
 * Represents a {@code class} file. A {@code class} file structure has the
 * following format:
 *
 * <pre>
 *    ClassFile {
 *        u4 magic;
 *        u2 minor_version;
 *        u2 major_version;
 *        u2 constant_pool_count;
 *        cp_info constant_pool[constant_pool_count-1];
 *        u2 access_flags;
 *        u2 this_class;
 *        u2 super_class;
 *        u2 interfaces_count;
 *        u2 interfaces[interfaces_count];
 *        u2 fields_count;
 *        field_info fields[fields_count];
 *        u2 methods_count;
 *        method_info methods[methods_count];
 *        u2 attributes_count;
 *        attribute_info attributes[attributes_count];
 *    }
 * </pre>
 *
 * The {@code ClassFile} object is constructed from the class byte array.
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
 * VM Spec: The ClassFile Structure </a>
 */
public class ClassFile {

    public final byte[] classByteArray;

    /**
     * Magic number of {@code class} file.
     */
    public static final int MAGIC = 0xCAFEBABE;
    public final u4 magic;

    //
    // Class file Version
    //
    /**
     * Minor version of a {@code class} file. It is the {@code minor_version} in
     * {@code ClassFile} structure.
     *
     * @see ClassFile#getVersion()
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 minor_version;

    /**
     * Major version of a {@code class} file. It is the {@code major_version} in
     * {@code ClassFile} structure.
     *
     * <pre>
     * The Java virtual machine implementation of Sun's JDK release 1.0.2 supports
     * class file format versions 45.0 through 45.3 inclusive. Sun's JDK releases
     * 1.1.X can support class file formats of versions in the range 45.0 through
     * 45.65535 inclusive. Implementations of version 1.2 of the Java 2 platform
     * can support class file formats of versions in the range 45.0 through 46.0
     * inclusive.
     * </pre>
     *
     * @see ClassFile#getVersion()
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 major_version;

    //
    // Constant pool
    //
    /**
     * Constant Pool Count of a {@code class} or {@code interface}. It is the
     * {@code constant_pool_count} in {@code ClassFile} structure.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 constant_pool_count;
    public final CPInfo[] constant_pool;

    //
    // Class Declaration
    //

    /**
     * A mask of flags used to denote access permissions to and properties of this class or interface.
     * 
     * @see AccessFlag#ACC_PUBLIC
     * @see AccessFlag#ACC_FINAL
     * @see AccessFlag#ACC_SUPER
     * @see AccessFlag#ACC_INTERFACE
     * @see AccessFlag#ACC_ABSTRACT
     * @see AccessFlag#ACC_SYNTHETIC
     * @see AccessFlag#ACC_ANNOTATION
     * @see AccessFlag#ACC_ENUM
     * @see AccessFlag#ACC_MODULE
     */
    public final U2ClassComponent access_flags;

    /**
     * {@code This} class of a {@code class} or {@code interface}. It is the
     * {@code this_class} in {@code ClassFile} structure.
     *
     * @see ClassFile#getThisClassName()
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent this_class;

    /**
     * Super class of a {@code class} or {@code interface}. It is the
     * {@code super_class} in {@code ClassFile} structure.
     *
     * @see ClassFile#getSuperClassName()
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent super_class;

    /**
     * Interfaces count of a {@code class} or {@code interface}. It is the
     * {@code interfaces_count} in {@code ClassFile} structure.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent interfaces_count;
    public final U2ClassComponent[] interfaces;

    //
    // Field
    //
    /**
     * Fields Count of a {@code class} or {@code interface}. It is the
     * {@code fields_count} in {@code ClassFile} structure.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent fields_count;
    public final FieldInfo[] fields;

    //
    // Method
    //
    /**
     * Methods Count of a {@code class} or {@code interface}. It is the
     * {@code methods_count} in {@code ClassFile} structure.
     *
     * @see ClassFile#methods_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent methods_count;
    public final MethodInfo[] methods;

    //
    // Attribute
    //
    /**
     * Attributes count of a {@code class} or {@code interface}. It is the
     * {@code attributes_count} in {@code ClassFile} structure.
     *
     * @see ClassFile#attributes_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent attributes_count;
    public final AttributeInfo[] attributes;

    /**
     * Creates a new instance of ClassFile from byte array.
     *
     * @param classByteArray Byte array of a class file
     * @throws IOException Error happened when reading the byte array
     * @throws FileFormatException Invalid class file format
     */
    public ClassFile(final byte[] classByteArray) throws IOException, FileFormatException {
        this.classByteArray = classByteArray.clone();

        //
        // Parse the Classfile byte by byte
        //
        PosDataInputStream posDataInputStream = new PosDataInputStream(new PosByteArrayInputStream(classByteArray));

        // magic number
        this.magic = new u4(posDataInputStream);
        if (this.magic.value != ClassFile.MAGIC) {
            throw new FileFormatException("The magic number of the byte array is not 0xCAFEBABE");
        }

        // Classfile version
        this.minor_version = new u2(posDataInputStream);
        this.major_version = new u2(posDataInputStream);

        // Constant Pool
        this.constant_pool_count = new u2(posDataInputStream);
        this.constant_pool = new CPInfo[this.constant_pool_count.value];
        for (int i = 1; i < this.constant_pool_count.value; i++) {
            short tag = (short) posDataInputStream.readUnsignedByte();

            this.constant_pool[i] = ConstantType.parse(tag, posDataInputStream);
            if (tag == CPInfo.ConstantType.CONSTANT_Long.tag || tag == CPInfo.ConstantType.CONSTANT_Double.tag) {
                // Long/Double type occupies two Constant Pool index
                i++;
            }
        }

        // Class Declaration
        this.access_flags = new U2ClassComponent(posDataInputStream);
        this.this_class = new U2ClassComponent(posDataInputStream);
        this.super_class = new U2ClassComponent(posDataInputStream);
        this.interfaces_count = new U2ClassComponent(posDataInputStream);
        if (this.interfaces_count.getValue() > 0) {
            this.interfaces = new U2ClassComponent[this.interfaces_count.getValue()];
            for (int i = 0; i < this.interfaces_count.getValue(); i++) {
                this.interfaces[i] = new U2ClassComponent(posDataInputStream);
            }
        } else {
            this.interfaces = null;
        }

        // Fields
        this.fields_count = new U2ClassComponent(posDataInputStream);
        final int fieldCount = this.fields_count.getValue();
        if (fieldCount > 0) {
            this.fields = new FieldInfo[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                this.fields[i] = new FieldInfo(posDataInputStream, this.constant_pool);
            }
        } else {
            this.fields = null;
        }

        // Methods
        this.methods_count = new U2ClassComponent(posDataInputStream);
        final int methodCount = this.methods_count.getValue();

        if (methodCount > 0) {
            this.methods = new MethodInfo[methodCount];
            for (int i = 0; i < methodCount; i++) {
                this.methods[i] = new MethodInfo(posDataInputStream, this.constant_pool);
            }
        } else {
            this.methods = null;
        }

        // Attributes
        this.attributes_count = new U2ClassComponent(posDataInputStream);
        final int attributeCount = this.attributes_count.getValue();
        if (attributeCount > 0) {
            this.attributes = new AttributeInfo[attributeCount];
            for (int i = 0; i < attributeCount; i++) {
                this.attributes[i] = AttributeInfo.parse(posDataInputStream, this.constant_pool);
            }
        } else {
            this.attributes = null;
        }
    }

    /**
     * Get the text of {@link #this_class}, which is the class name.
     *
     * @return Corresponding text of {@link #this_class}
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getThisClassName() throws FileFormatException {
        return this.getConstantClassInfoName(this.this_class.getValue());
    }

    /**
     * Generate the modifier of a {@code class} or {@code interface} from the
     * access flag value.
     *
     * @return A string for modifier
     */
    public String getModifiers() {
        return AccessFlag.getClassModifier(this.access_flags.value.value);
    }
    
    /**
     * Get the text of {@link #super_class}, which is the super class name.
     *
     * @return Corresponding text of {@link #super_class}
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getSuperClassName() throws FileFormatException {
        int superClass = this.super_class.getValue();
        if (superClass == 0) {
            // java.lang.Object.class OR module-info.class
            return "";
        } else {
            return this.getConstantClassInfoName(superClass);
        }
    }

    /**
     * Get <code>CONSTANT_Class_info</code> name from the
     * {@link #constant_pool}.
     *
     * @param cpIndex {@link #constant_pool} item index
     * @return Get <code>CONSTANT_Class_info</code> name at <code>cpIndex</code>
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getConstantClassInfoName(int cpIndex) throws FileFormatException {
        String name = null;
        if ((cpIndex >= 0 && cpIndex < this.constant_pool.length)
                && (this.constant_pool[cpIndex] instanceof ConstantClassInfo)) {
            ConstantClassInfo clsInfo = (ConstantClassInfo) this.constant_pool[cpIndex];
            name = getConstantUtf8Value(clsInfo.name_index.value, this.constant_pool);
        } else {
            throw new FileFormatException(String.format("Constant Pool index (value = %d) is out of range, or it is not a CONSTANT_Class_info. ", cpIndex));
        }

        return name;
    }

    /**
     * Get <code>CONSTANT_Utf8_info</code> text from the
     * {@link #constant_pool}..
     *
     * @param cpIndex Constant Pool object Index for
     * <code>CONSTANT_Utf8_info</code>
     * @return The UTF-8 text
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid class file
     * format
     */
    static String getConstantUtf8Value(final int cpIndex, final CPInfo[] cpInfo) throws FileFormatException {
        String returnValue = null;

        if ((cpIndex == 0) || (cpIndex >= cpInfo.length)) {
            throw new FileFormatException(String.format(
                    "Constant Pool index is out of range. CP index cannot be zero, and should be less than CP count (=%d). CP index = %d.",
                    cpInfo.length,
                    cpIndex));
        }

        if (cpInfo[cpIndex].tag.value == CPInfo.ConstantType.CONSTANT_Utf8.tag) {
            final ConstantUtf8Info utf8Info = (ConstantUtf8Info) cpInfo[cpIndex];
            returnValue = utf8Info.getValue();
        } else {
            throw new FileFormatException(String.format(
                    "Unexpected constant pool type: Utf8(%d) expected, but it is '%d'.",
                    CPInfo.ConstantType.CONSTANT_Utf8.tag,
                    cpInfo[cpIndex].tag.value));
        }

        return returnValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get raw data
    /**
     * Get part of the class byte array. The array begins at the specified
     * {@code startIndex} and extends to the byte at
     * {@code startIndex}+{@code length}.
     *
     * @param startIndex The start index
     * @param length The length of the array
     * @return Part of the class byte array
     */
    public byte[] getClassByteArray(final int startIndex, final int length) {
        if ((startIndex < 0) || (length < 1)) {
            throw new IllegalArgumentException("startIndex or length is not valid. startIndex = " + startIndex + ", length = " + length);
        }
        if (startIndex + length - 1 > this.classByteArray.length) {
            throw new ArrayIndexOutOfBoundsException("The last item index is bigger than class byte array size.");
        }

        byte[] data = new byte[length];
        System.arraycopy(this.classByteArray, startIndex, data, 0, length);
        return data;
    }

    /**
     * Returns a string of the constant pool item at the specified
     * {@code index}.
     *
     * @param index Index in the constant pool
     * @return String of the constant pool item at {@code index}
     */
    public String getCPDescription(final int index) {
        // Invalid index
        if (index >= this.constant_pool_count.value) {
            throw new IllegalArgumentException(String.format("Index is bigger than constant pool size. size=%d, index=%d", this.constant_pool_count.value, index));
        }

        // Special index: empty
        if (index == 0) {
            throw new IllegalArgumentException("Constant Pool Index cannot be zero. index=" + index);
        }
        
        CPInfo cp = this.constant_pool[index];
        if (cp == null) {
            // For Double, Long type, each item take two indexs, so there could be some index contains nothing.
            throw new IllegalArgumentException("Nothing exist at the Constant Pool Index. index=" + index);
        }

        return cp.toString(this.constant_pool);
    }

    /**
     * Get the Java Class file version.
     *
     * @return The Java class file version.
     */
    public Version getVersion() {
        return Version.valueOf(this.major_version.value, this.minor_version.value);
    }

    @Override
    public String toString() {
        return "Class contains "
                + this.fields_count.value.value + " field(s) and "
                + this.methods_count.value.value + " method(s)";
    }

    /**
     * Version numbers of a class file. Together, a major and a minor version
     * number determine the version of the class file format. If a class file
     * has major version number M and minor version number m, we denote the
     * version of its class file format as M.m.
     *
     * @author Amos Shi
     */
    public enum Version {

        /**
         * For 45.3, it could be both {@link JavaSEVersion#Version_1_1} or
         * {@link JavaSEVersion#Version_1_0_2}. We simply use the
         * {@link JavaSEVersion#Version_1_1} which is the newer one.
         */
        Format_45_3(45, 3, JavaSEVersion.Version_1_1),
        Format_49_0(49, 0, JavaSEVersion.Version_5_0),
        Format_50_0(50, 0, JavaSEVersion.Version_6),
        Format_51_0(51, 0, JavaSEVersion.Version_7),
        Format_52_0(52, 0, JavaSEVersion.Version_8),
        Format_53_0(53, 0, JavaSEVersion.Version_9),
        Format_55_0(55, 0, JavaSEVersion.Version_11);

        public final int major_version;
        public final int minor_version;
        public final JavaSEVersion java_se;

        private Version(int major, int minor, JavaSEVersion javaSE) {
            this.major_version = major;
            this.minor_version = minor;
            this.java_se = javaSE;
        }

        public static Version valueOf(int major, int minor) {
            for (Version v : Version.values()) {
                if (v.major_version == major && v.minor_version == minor) {
                    return v;
                }
            }

            throw new IllegalArgumentException("The provided version number is not recognized");
        }

        /**
         * Get the class file version as a string.
         *
         * @return Version as String.
         */
        public String getVersion() {
            return String.format("%d.%d", this.major_version, this.minor_version);
        }
    }

}
