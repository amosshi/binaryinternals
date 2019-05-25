/*
 * ClassFile.java    2:58 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.format.classfile.constant.CPInfo;
import org.freeinternals.format.classfile.constant.ConstantStringInfo;
import org.freeinternals.format.classfile.constant.ConstantClassInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodHandleInfo;
import org.freeinternals.format.classfile.constant.ConstantInterfaceMethodrefInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodrefInfo;
import org.freeinternals.format.classfile.constant.ConstantMethodTypeInfo;
import org.freeinternals.format.classfile.constant.ConstantInvokeDynamicInfo;
import org.freeinternals.format.classfile.constant.ConstantNameAndTypeInfo;
import org.freeinternals.format.classfile.constant.ConstantDoubleInfo;
import org.freeinternals.format.classfile.constant.ConstantModuleInfo;
import org.freeinternals.format.classfile.constant.ConstantFloatInfo;
import org.freeinternals.format.classfile.constant.ConstantUtf8Info;
import org.freeinternals.format.classfile.constant.ConstantFieldrefInfo;
import org.freeinternals.format.classfile.constant.ConstantLongInfo;
import org.freeinternals.format.classfile.constant.ConstantPackageInfo;
import org.freeinternals.format.classfile.constant.ConstantDynamicInfo;
import org.freeinternals.format.classfile.constant.ConstantIntegerInfo;
import org.freeinternals.format.classfile.attribute.AttributeInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.constant.CPInfo.ConstantType;

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
 * @since JDK 6.0
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/">
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
     * @author Amos Shi
     * @see ClassFile#minor_version
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4">
     * VM Spec: The ClassFile Structure
     * </a>
     *
     * TODO - Make it final
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
     * @author Amos Shi
     * @since JDK 6.0
     * @see ClassFile#major_version
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 major_version;

    // Constant pool
    /**
     * Constant Pool Count of a {@code class} or {@code interface}. It is the
     * {@code constant_pool_count} in {@code ClassFile} structure.
     *
     * @author Amos Shi
     * @see ClassFile#constant_pool_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 constant_pool_count;
    public final CPInfo[] constant_pool;

    //
    // Class Declaration
    //
    public final AccessFlags access_flags;

    /**
     * {@code This} class of a {@code class} or {@code interface}. It is the
     * {@code this_class} in {@code ClassFile} structure.
     *
     * @author Amos Shi
     * @see ClassFile#this_class
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent this_class;

    /**
     * Super class of a {@code class} or {@code interface}. It is the
     * {@code super_class} in {@code ClassFile} structure.
     *
     * @author Amos Shi
     * @since JDK 6.0
     * @see ClassFile#super_class
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent super_class;

    /**
     * Interfaces count of a {@code class} or {@code interface}. It is the
     * {@code interfaces_count} in {@code ClassFile} structure.
     *
     * @author Amos Shi
     * @since JDK 6.0
     * @see ClassFile#interfaces_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
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
     * @author Amos Shi
     * @since JDK 6.0
     * @see ClassFile#fields_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
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
     * @author Amos Shi
     * @see ClassFile#methods_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
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
     * @author Amos Shi
     * @see ClassFile#attributes_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent attributes_count;
    public final AttributeInfo[] attributes;

    /**
     * Creates a new instance of ClassFile from byte array.
     *
     * @param classByteArray Byte array of a class file
     * @throws java.io.IOException Error happened when reading the byte array
     * @throws org.freeinternals.format.FileFormatException Invalid class file
     * format
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
        this.access_flags = new AccessFlags(posDataInputStream);
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

        // Set the Declarations
        // TODO - Refactor - Delete this method
        this.analysisDeclarations();
    }

    private void analysisDeclarations()
            throws FileFormatException {

        // Analysis field declarations
        if (this.fields_count.getValue() > 0) {
            String type;
            for (FieldInfo field : fields) {
                try {
                    type = SignatureConvertor.FieldDescriptorExtractor(this.getConstantUtf8Value(field.descriptor_index.value)).toString();
                } catch (FileFormatException se) {
                    type = "[Unexpected signature type]: " + this.getConstantUtf8Value(field.descriptor_index.value);
                }
                field.setDeclaration(String.format("%s %s %s",
                        field.getModifiers(),
                        type,
                        this.getConstantUtf8Value(field.name_index.value)));
            }
        }

        // Analysis method declarations
        if (this.methods_count.getValue() > 0) {
            String mtdReturnType;
            String mtdParameters;
            for (MethodInfo method : methods) {
                try {
                    mtdReturnType = SignatureConvertor.MethodReturnTypeExtractor(this.getConstantUtf8Value(method.descriptor_index.value)).toString();
                } catch (FileFormatException se) {
                    mtdReturnType = String.format("[Unexpected method return type: %s]", this.getConstantUtf8Value(method.descriptor_index.value));
                }
                try {
                    mtdParameters = SignatureConvertor.MethodParameters2Readable(this.getConstantUtf8Value(method.descriptor_index.value));
                } catch (FileFormatException se) {
                    mtdParameters = String.format("[Unexpected method parameters: %s]", this.getConstantUtf8Value(method.descriptor_index.value));
                }

                method.setDeclaration(String.format("%s %s %s %s",
                        method.getModifiers(),
                        mtdReturnType,
                        this.getConstantUtf8Value(method.name_index.value),
                        mtdParameters));
            }
        }
    }

    /**
     * Get the text of {@link #this_class}, which is the class name.
     *
     * @return Corresponding text of {@link #this_class}
     * @throws org.freeinternals.format.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getThisClassName() throws FileFormatException {
        return this.getConstantClassInfoName(this.this_class.getValue());
    }

    /**
     * Get the text of {@link #super_class}, which is the super class name.
     *
     * @return Corresponding text of {@link #super_class}
     * @throws org.freeinternals.format.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getSuperClassName() throws FileFormatException {
        int superClass = this.super_class.getValue();
        if (superClass == 0) {
            // java.lang.Object.class
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
     * @throws org.freeinternals.format.FileFormatException Invalid
     * {@link #constant_pool} item found
     */
    public String getConstantClassInfoName(int cpIndex) throws FileFormatException {
        String name = null;
        if ((cpIndex >= 0 && cpIndex < this.constant_pool.length)
                && (this.constant_pool[cpIndex] instanceof ConstantClassInfo)) {
            ConstantClassInfo clsInfo = (ConstantClassInfo) this.constant_pool[cpIndex];
            name = this.getConstantUtf8Value(clsInfo.name_index.value);
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
     * @throws org.freeinternals.format.FileFormatException Invalid class file
     * format
     */
    public String getConstantUtf8Value(final int cpIndex)
            throws FileFormatException {
        String returnValue = null;

        if ((cpIndex == 0) || (cpIndex >= this.constant_pool_count.value)) {
            throw new FileFormatException(String.format(
                    "Constant Pool index is out of range. CP index cannot be zero, and should be less than CP count (=%d). CP index = %d.",
                    this.constant_pool_count.value,
                    cpIndex));
        }

        if (this.constant_pool[cpIndex].tag.value == CPInfo.ConstantType.CONSTANT_Utf8.tag) {
            final ConstantUtf8Info utf8Info = (ConstantUtf8Info) this.constant_pool[cpIndex];
            returnValue = utf8Info.getValue();
        } else {
            throw new FileFormatException(String.format(
                    "Unexpected constant pool type: Utf8(%d) expected, but it is '%d'.",
                    CPInfo.ConstantType.CONSTANT_Utf8.tag,
                    this.constant_pool[cpIndex].tag.value));
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
            return null;
        }

        // Special index: empty
        if (index == 0) {
            return null;
        }

        return new CPDescr().getCPDescr(index);
    }

    @Override
    public String toString() {
        return "Class contains "
                + this.fields_count.value.value + " field(s) and "
                + this.methods_count.value.value + " method(s)";
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get extracted data
    ///////////////////////////////////////////////////////////////////////////
    // Internal Classes
    private static enum Descr_NameAndType {

        RAW(1), FIELD(2), METHOD(3);
        private final int enum_value;

        Descr_NameAndType(final int value) {
            this.enum_value = value;
        }

        public int value() {
            return this.enum_value;
        }
    }

    // TODO - Refactor this method into each Constant type
    private class CPDescr {

        public String getCPDescr(final int index) {
            final StringBuilder sb = new StringBuilder(40);

            if (index >= 0 && index < ClassFile.this.constant_pool.length) {
                CPInfo cp_info = ClassFile.this.constant_pool[index];
                if (cp_info != null) {
                    sb.append(cp_info.getName()).append(": ");
                    if (cp_info instanceof ConstantUtf8Info) {
                        sb.append(this.getDescr_Utf8((ConstantUtf8Info) cp_info));
                    } else if (cp_info instanceof ConstantIntegerInfo) {
                        sb.append(String.valueOf(((ConstantIntegerInfo) cp_info).integerValue));
                    } else if (cp_info instanceof ConstantFloatInfo) {
                        sb.append(String.valueOf(((ConstantFloatInfo) cp_info).floatValue));
                    } else if (cp_info instanceof ConstantLongInfo) {
                        sb.append(String.valueOf(((ConstantLongInfo) cp_info).longValue));
                    } else if (cp_info instanceof ConstantDoubleInfo) {
                        sb.append(String.valueOf(((ConstantDoubleInfo) cp_info).doubleValue));
                    } else if (cp_info instanceof ConstantClassInfo) {
                        sb.append(this.getDescr_Class((ConstantClassInfo) cp_info));
                    } else if (cp_info instanceof ConstantStringInfo) {
                        sb.append(this.getDescr_String((ConstantStringInfo) cp_info));
                    } else if (cp_info instanceof ConstantFieldrefInfo) {
                        sb.append(this.getDescr_Fieldref((ConstantFieldrefInfo) cp_info));
                    } else if (cp_info instanceof ConstantMethodrefInfo) {
                        sb.append(this.getDescr_Methodref((ConstantMethodrefInfo) cp_info));
                    } else if (cp_info instanceof ConstantInterfaceMethodrefInfo) {
                        sb.append(this.getDescr_InterfaceMethodref((ConstantInterfaceMethodrefInfo) cp_info));
                    } else if (cp_info instanceof ConstantNameAndTypeInfo) {
                        sb.append(this.getDescr_NameAndType(
                                (ConstantNameAndTypeInfo) cp_info,
                                ClassFile.Descr_NameAndType.RAW));
                    } else if (cp_info instanceof ConstantMethodTypeInfo) {
                        ConstantMethodTypeInfo mti = (ConstantMethodTypeInfo) cp_info;
                        sb.append(this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[mti.descriptor_index.value]));
                    } else if (cp_info instanceof ConstantDynamicInfo) {
                        sb.append("bootstrap_method_attr_index = ").append(((ConstantDynamicInfo) cp_info).bootstrap_method_attr_index.value);
                        sb.append(", name_and_type_index = ").append(this.getCPDescr(((ConstantDynamicInfo) cp_info).name_and_type_index.value));
                    } else if (cp_info instanceof ConstantInvokeDynamicInfo) {
                        sb.append("bootstrap_method_attr_index = ").append(((ConstantInvokeDynamicInfo) cp_info).bootstrap_method_attr_index.value);
                        sb.append(", name_and_type_index = ").append(this.getCPDescr(((ConstantInvokeDynamicInfo) cp_info).name_and_type_index.value));
                    } else if (cp_info instanceof ConstantMethodHandleInfo) {
                        sb.append("reference_kind = ").append(ConstantMethodHandleInfo.ReferenceKind.name(((ConstantMethodHandleInfo) cp_info).reference_kind.value));
                        sb.append(", reference_index = ").append(this.getCPDescr(((ConstantMethodHandleInfo) cp_info).reference_index.value));
                    } else if (cp_info instanceof ConstantModuleInfo) {
                        sb.append(this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[((ConstantModuleInfo) cp_info).name_index.value]));
                    } else if (cp_info instanceof ConstantPackageInfo) {
                        sb.append(this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[((ConstantPackageInfo) cp_info).name_index.value]));
                    } else {
                        sb.append("!!! Un-recognized CP type.");
                    }
                } // End if
            } else {
                sb.append("ERROR - Index (" + index + ") out of bound of constant_pool (" + ClassFile.this.constant_pool.length + ")");
            }

            return sb.toString();
        }

        private String getDescr_Utf8(final ConstantUtf8Info info) {
            return info.getValue();
        }

        private String getDescr_Class(final ConstantClassInfo info) {
            // The value of the name_index item must be a valid index into the constant_pool table.
            // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure
            // representing a valid fully qualified class or interface name encoded in internal form.
            return SignatureConvertor.ParseClassSignature(this.getDescr_Utf8(
                    (ConstantUtf8Info) ClassFile.this.constant_pool[info.name_index.value]));
        }

        private String getDescr_String(final ConstantStringInfo info) {
            // The value of the string_index item must be a valid index into the constant_pool table.
            // The constant_pool entry at that index must be a CONSTANT_Utf8_info (.4.7) structure
            // representing the sequence of characters to which the String object is to be initialized.
            return SignatureConvertor.ParseClassSignature(this.getDescr_Utf8(
                    (ConstantUtf8Info) ClassFile.this.constant_pool[info.string_index.value]));
        }

        private String getDescr_Fieldref(final ConstantFieldrefInfo info) {
            return this.getDescr_ref(
                    info.class_index.value,
                    info.name_and_type_index.value,
                    ClassFile.Descr_NameAndType.FIELD);
        }

        private String getDescr_Methodref(final ConstantMethodrefInfo info) {
            return this.getDescr_ref(
                    info.class_index.value,
                    info.name_and_type_index.value,
                    ClassFile.Descr_NameAndType.METHOD);
        }

        private String getDescr_InterfaceMethodref(final ConstantInterfaceMethodrefInfo info) {
            return this.getDescr_ref(
                    info.class_index.value,
                    info.name_and_type_index.value,
                    ClassFile.Descr_NameAndType.METHOD);
        }

        private String getDescr_ref(final int classindex, final int natindex, final ClassFile.Descr_NameAndType type) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getDescr_Class((ConstantClassInfo) ClassFile.this.constant_pool[classindex]));
            sb.append(".");
            sb.append(this.getDescr_NameAndType((ConstantNameAndTypeInfo) ClassFile.this.constant_pool[natindex], type));

            return sb.toString();
        }

        private String getDescr_NameAndType(final ConstantNameAndTypeInfo info, final ClassFile.Descr_NameAndType format) {
            final StringBuilder sb = new StringBuilder();
            String type;

            sb.append(this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[info.name_index.value]));
            sb.append(", ");
            type = this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[info.descriptor_index.value]);

            switch (format) {
                case RAW:
                    sb.append(type);
                    break;

                case FIELD:
                    try {
                        sb.append("type = ");
                        sb.append(SignatureConvertor.FieldDescriptorExtractor(type).toString());
                    } catch (FileFormatException ex) {
                        Logger.getLogger(ClassFile.class.getName()).log(Level.SEVERE, null, ex);

                        sb.append(type);
                        sb.append(" !!! Un-recognized type");
                    }
                    break;

                case METHOD:
                    final StringBuilder sb_mtd = new StringBuilder();
                    try {
                        sb_mtd.append("parameter = ");
                        sb_mtd.append(SignatureConvertor.MethodParameters2Readable(type));
                        sb_mtd.append(", returns = ");
                        sb_mtd.append(SignatureConvertor.MethodReturnTypeExtractor(type).toString());

                        sb.append(sb_mtd);
                    } catch (FileFormatException ex) {
                        Logger.getLogger(ClassFile.class.getName()).log(Level.SEVERE, null, ex);

                        sb.append(type);
                        sb.append(" !!! Un-recognized type");
                    }
                    break;
                default:
                    break;
            }

            return sb.toString();
        }
    }
}
