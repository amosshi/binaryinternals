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
import org.freeinternals.format.FileFormatException;

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
 *
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html">
 * VM Spec: The ClassFile Structure </a>
 */
public class ClassFile {

    private final byte[] classByteArray;
    private PosDataInputStream posDataInputStream;
    /**
     * Magic number of {@code class} file.
     */
    public static final int MAGIC = 0xCAFEBABE;
    private u4 magic;
    // Class file Version
    public MinorVersion minor_version;
    public MajorVersion major_version;
    // Constant pool
    public CPCount constant_pool_count;
    private AbstractCPInfo[] constant_pool;
    // Class Declaration
    public AccessFlags access_flags;
    public ThisClass this_class;
    public SuperClass super_class;
    public InterfaceCount interfaces_count;
    private Interface[] interfaces;
    // Field
    public FieldCount fields_count;
    private FieldInfo[] fields;
    // Method
    public MethodCount methods_count;
    private MethodInfo[] methods;
    // Attribute
    public AttributeCount attributes_count;
    private AttributeInfo[] attributes;

    /**
     * Creates a new instance of ClassFile from byte array.
     *
     * @param classByteArray Byte array of a class file
     * @throws java.io.IOException Error happened when reading the byte array
     * @throws org.freeinternals.format.FileFormatException Invalid class file
     * format
     */
    public ClassFile(final byte[] classByteArray)
            throws java.io.IOException, FileFormatException {
        this.classByteArray = classByteArray.clone();
        final ClassFile.Parser parser = new Parser();
        parser.parse();
        this.analysisDeclarations();
    }

    private void analysisDeclarations()
            throws FileFormatException {

        // Analysis field declarations
        if (this.fields_count.getValue() > 0) {
            String type;
            for (FieldInfo field : fields) {
                try {
                    type = SignatureConvertor.signature2Type(this.getConstantUtf8Value(field.descriptor_index.value));
                } catch (SignatureException se) {
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
                    mtdReturnType = SignatureConvertor.parseMethodReturnType(this.getConstantUtf8Value(method.descriptor_index.value));
                } catch (SignatureException se) {
                    mtdReturnType = String.format("[Unexpected method return type: %s]", this.getConstantUtf8Value(method.descriptor_index.value));
                }
                try {
                    mtdParameters = SignatureConvertor.parseMethodParameters(this.getConstantUtf8Value(method.descriptor_index.value));
                } catch (SignatureException se) {
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
     * Get a UTF-8 text from the constant pool.
     *
     * @param cpIndex Constant Pool object Index
     * @return The UTF-8 text
     * @throws org.freeinternals.format.FileFormatException Invalid class file
     * format
     */
    public String getConstantUtf8Value(final int cpIndex)
            throws FileFormatException {
        String returnValue = null;

        if ((cpIndex == 0) || (cpIndex >= this.constant_pool_count.value.value)) {
            throw new FileFormatException(String.format(
                    "Constant Pool index is out of range. CP index cannot be zero, and should be less than CP count (=%d). CP index = %d.",
                    this.constant_pool_count.value.value,
                    cpIndex));
        }

        if (this.constant_pool[cpIndex].tag.value == AbstractCPInfo.CONSTANT_Utf8) {
            final ConstantUtf8Info utf8Info = (ConstantUtf8Info) this.constant_pool[cpIndex];
            returnValue = utf8Info.getValue();
        } else {
            throw new FileFormatException(String.format(
                    "Unexpected constant pool type: Utf8(%d) expected, but it is '%d'.",
                    AbstractCPInfo.CONSTANT_Utf8,
                    this.constant_pool[cpIndex].tag.value));
        }

        return returnValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get raw data
    /**
     * Get the byte array of current class.
     *
     * @return Byte array of the class
     */
    public byte[] getClassByteArray() {
        return this.classByteArray;
    }

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
     * Get the length of the class byte array.
     *
     * @return Length of class byte array
     */
    public int getByteArraySize() {
        return this.classByteArray.length;
    }

    /**
     * Get the {@code constant_pool[]} of the {@code ClassFile} structure.
     *
     * @return The {@code constant_pool[]}
     */
    public AbstractCPInfo[] getConstantPool() {
        return this.constant_pool;
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
        if (index >= this.constant_pool_count.getValue()) {
            return null;
        }

        // Special index: empty
        if (index == 0) {
            return null;
        }

        return new CPDescr().getCPDescr(index);
    }

    /**
     * Get the {@code interfaces}[] of the {@code ClassFile} structure.
     *
     * @return The {@code interfaces}[]
     */
    public Interface[] getInterfaces() {
        return this.interfaces;
    }

    /**
     * Get the {@code fields}[] of the {@code ClassFile} structure.
     *
     * @return The {@code fields}[]
     */
    public FieldInfo[] getFields() {
        return this.fields;
    }

    /**
     * Get the {@code methods}[] of the {@code ClassFile} structure.
     *
     * @return The {@code methods}[]
     */
    public MethodInfo[] getMethods() {
        return this.methods;
    }

    /**
     * Get the {@code attributes}[] of the {@code ClassFile} structure.
     *
     * @return The {@code attributes}[]
     */
    public AttributeInfo[] getAttributes() {
        return this.attributes;
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
    private class Parser {

        Parser() {
        }

        public void parse()
                throws FileFormatException, IOException {
            final PosByteArrayInputStream posByteArrayInputStream = new PosByteArrayInputStream(classByteArray);
            ClassFile.this.posDataInputStream = new PosDataInputStream(posByteArrayInputStream);

            ClassFile.this.magic = new u4(ClassFile.this.posDataInputStream.readInt());
            if (ClassFile.this.magic.value != ClassFile.MAGIC) {
                throw new FileFormatException("The magic number of the byte array is not 0xCAFEBABE");
            }

            this.parseClassFileVersion();
            this.parseConstantPool();
            this.parseClassDeclaration();
            this.parseFields();
            this.parseMethods();
            this.parseAttributes();
        }

        private void parseClassFileVersion()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.minor_version = new MinorVersion(ClassFile.this.posDataInputStream);
            ClassFile.this.major_version = new MajorVersion(ClassFile.this.posDataInputStream);
        }

        private void parseConstantPool()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.constant_pool_count = new CPCount(ClassFile.this.posDataInputStream);
            final int cp_count = ClassFile.this.constant_pool_count.getValue();

            ClassFile.this.constant_pool = new AbstractCPInfo[cp_count];
            short tag;
            for (int i = 1; i < cp_count; i++) {
                tag = (short) ClassFile.this.posDataInputStream.readUnsignedByte();

                switch (tag) {
                    case AbstractCPInfo.CONSTANT_Utf8:
                        ClassFile.this.constant_pool[i] = new ConstantUtf8Info(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_Integer:
                        ClassFile.this.constant_pool[i] = new ConstantIntegerInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_Float:
                        ClassFile.this.constant_pool[i] = new ConstantFloatInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_Long:
                        ClassFile.this.constant_pool[i] = new ConstantLongInfo(ClassFile.this.posDataInputStream);
                        i++;
                        break;

                    case AbstractCPInfo.CONSTANT_Double:
                        ClassFile.this.constant_pool[i] = new ConstantDoubleInfo(ClassFile.this.posDataInputStream);
                        i++;
                        break;

                    case AbstractCPInfo.CONSTANT_Class:
                        ClassFile.this.constant_pool[i] = new ConstantClassInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_String:
                        ClassFile.this.constant_pool[i] = new ConstantStringInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_Fieldref:
                        ClassFile.this.constant_pool[i] = new ConstantFieldrefInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_Methodref:
                        ClassFile.this.constant_pool[i] = new ConstantMethodrefInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_InterfaceMethodref:
                        ClassFile.this.constant_pool[i] = new ConstantInterfaceMethodrefInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_NameAndType:
                        ClassFile.this.constant_pool[i] = new ConstantNameAndTypeInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_MethodHandle:
                        ClassFile.this.constant_pool[i] = new ConstantMethodHandleInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_MethodType:
                        ClassFile.this.constant_pool[i] = new ConstantMethodTypeInfo(ClassFile.this.posDataInputStream);
                        break;

                    case AbstractCPInfo.CONSTANT_InvokeDynamic:
                        ClassFile.this.constant_pool[i] = new ConstantInvokeDynamicInfo(ClassFile.this.posDataInputStream);
                        break;

                    default:
                        throw new FileFormatException(
                                String.format("Unreconizable constant pool type found. Constant pool tag: [%d]; class file offset: [%d].", tag, ClassFile.this.posDataInputStream.getPos() - 1));
                }
            }
        }

        private void parseClassDeclaration()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.access_flags = new AccessFlags(ClassFile.this.posDataInputStream);
            ClassFile.this.this_class = new ThisClass(ClassFile.this.posDataInputStream);
            ClassFile.this.super_class = new SuperClass(ClassFile.this.posDataInputStream);

            ClassFile.this.interfaces_count = new InterfaceCount(ClassFile.this.posDataInputStream);
            if (ClassFile.this.interfaces_count.getValue() > 0) {
                ClassFile.this.interfaces = new Interface[ClassFile.this.interfaces_count.getValue()];
                for (int i = 0; i < ClassFile.this.interfaces_count.getValue(); i++) {
                    ClassFile.this.interfaces[i] = new Interface(ClassFile.this.posDataInputStream);
                }
            }
        }

        private void parseFields()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.fields_count = new FieldCount(ClassFile.this.posDataInputStream);
            final int fieldCount = ClassFile.this.fields_count.getValue();
            if (fieldCount > 0) {
                ClassFile.this.fields = new FieldInfo[fieldCount];
                for (int i = 0; i < fieldCount; i++) {
                    ClassFile.this.fields[i] = new FieldInfo(ClassFile.this.posDataInputStream, ClassFile.this.constant_pool);
                }
            }
        }

        private void parseMethods()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.methods_count = new MethodCount(ClassFile.this.posDataInputStream);
            final int methodCount = ClassFile.this.methods_count.getValue();

            if (methodCount > 0) {
                ClassFile.this.methods = new MethodInfo[methodCount];
                for (int i = 0; i < methodCount; i++) {
                    ClassFile.this.methods[i] = new MethodInfo(ClassFile.this.posDataInputStream, ClassFile.this.constant_pool);
                }
            }
        }

        private void parseAttributes()
                throws java.io.IOException, FileFormatException {
            ClassFile.this.attributes_count = new AttributeCount(ClassFile.this.posDataInputStream);
            final int attributeCount = ClassFile.this.attributes_count.getValue();
            if (attributeCount > 0) {
                ClassFile.this.attributes = new AttributeInfo[attributeCount];
                for (int i = 0; i < attributeCount; i++) {
                    ClassFile.this.attributes[i] = AttributeInfo.parse(ClassFile.this.posDataInputStream, ClassFile.this.constant_pool);
                }
            }
        }
    }

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

    private class CPDescr {

        CPDescr() {
        }

        public String getCPDescr(final int index) {
            final StringBuilder sb = new StringBuilder(40);

            if (index >= 0 && index < ClassFile.this.constant_pool.length) {
                if (ClassFile.this.constant_pool[index] != null) {
                    switch (ClassFile.this.constant_pool[index].tag.value) {
                        case AbstractCPInfo.CONSTANT_Utf8:
                            sb.append("Utf8: ");
                            sb.append(this.getDescr_Utf8((ConstantUtf8Info) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Integer:
                            sb.append("Integer: ");
                            sb.append(this.getDescr_Integer((ConstantIntegerInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Float:
                            sb.append("Float: ");
                            sb.append(this.getDescr_Float((ConstantFloatInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Long:
                            sb.append("Long: ");
                            sb.append(this.getDescr_Long((ConstantLongInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Double:
                            sb.append("Double: ");
                            sb.append(this.getDescr_Double((ConstantDoubleInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Class:
                            sb.append("Class: ");
                            sb.append(this.getDescr_Class((ConstantClassInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_String:
                            sb.append("String: ");
                            sb.append(this.getDescr_String((ConstantStringInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Fieldref:
                            sb.append("Fieldref: ");
                            sb.append(this.getDescr_Fieldref((ConstantFieldrefInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_Methodref:
                            sb.append("Methodref: ");
                            sb.append(this.getDescr_Methodref((ConstantMethodrefInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_InterfaceMethodref:
                            sb.append("InterfaceMethodref: ");
                            sb.append(this.getDescr_InterfaceMethodref((ConstantInterfaceMethodrefInfo) ClassFile.this.constant_pool[index]));
                            break;
                        case AbstractCPInfo.CONSTANT_NameAndType:
                            sb.append("NameAndType: ");
                            sb.append(this.getDescr_NameAndType(
                                    (ConstantNameAndTypeInfo) ClassFile.this.constant_pool[index],
                                    ClassFile.Descr_NameAndType.RAW));
                            break;
                        case AbstractCPInfo.CONSTANT_MethodHandle:
                        case AbstractCPInfo.CONSTANT_MethodType:
                        case AbstractCPInfo.CONSTANT_InvokeDynamic:
                            sb.append(ClassFile.this.constant_pool[index].getDescription());
                            break;
                        default:
                            sb.append("!!! Un-supported CP type.");
                            break;
                    }
                }
            } else {
                sb.append("ERROR - Index (" + index + ") out of bound of constant_pool (" + ClassFile.this.constant_pool.length + ")");
            }

            return sb.toString();
        }

        private String getDescr_Utf8(final ConstantUtf8Info info) {
            return info.getValue();
        }

        private String getDescr_Integer(final ConstantIntegerInfo info) {
            return String.valueOf(info.integerValue);
        }

        private String getDescr_Float(final ConstantFloatInfo info) {
            return String.valueOf(info.floatValue);
        }

        private String getDescr_Long(final ConstantLongInfo info) {
            return String.valueOf(info.longValue);
        }

        private String getDescr_Double(final ConstantDoubleInfo info) {
            return String.valueOf(info.doubleValue);
        }

        private String getDescr_Class(final ConstantClassInfo info) {
            // The value of the name_index item must be a valid index into the constant_pool table. 
            // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure 
            // representing a valid fully qualified class or interface name encoded in internal form.
            return SignatureConvertor.parseClassSignature(this.getDescr_Utf8(
                    (ConstantUtf8Info) ClassFile.this.constant_pool[info.name_index.value]));
        }

        private String getDescr_String(final ConstantStringInfo info) {
            // The value of the string_index item must be a valid index into the constant_pool table. 
            // The constant_pool entry at that index must be a CONSTANT_Utf8_info (.4.7) structure 
            // representing the sequence of characters to which the String object is to be initialized.
            return SignatureConvertor.parseClassSignature(this.getDescr_Utf8(
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
                        sb.append(SignatureConvertor.signature2Type(type));
                    } catch (SignatureException ex) {
                        Logger.getLogger(ClassFile.class.getName()).log(Level.SEVERE, null, ex);

                        sb.append(type);
                        sb.append(" !!! Un-recognized type");
                    }
                    break;

                case METHOD:
                    final StringBuilder sb_mtd = new StringBuilder();
                    try {
                        sb_mtd.append("parameter = ");
                        sb_mtd.append(SignatureConvertor.parseMethodParameters(type));
                        sb_mtd.append(", returns = ");
                        sb_mtd.append(SignatureConvertor.parseMethodReturnType(type));

                        sb.append(sb_mtd);
                    } catch (SignatureException ex) {
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
