/*
 * ClassFile.java    2:58 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.classfile.attribute.Code_attribute;
import org.freeinternals.format.classfile.attribute.attribute_info;
import org.freeinternals.format.classfile.constant.CONSTANT_Class_info;
import org.freeinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.freeinternals.format.classfile.constant.cp_info;
import org.freeinternals.format.classfile.constant.cp_info.ConstantType;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html">
 * VM Spec: The ClassFile Structure </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class ClassFile extends FileFormat {

    /**
     * Magic number of {@code class} file.
     */
    public static final int FORMAT_MAGIC_NUMBER = 0xCAFEBABE;

    public final u4 magic;

    //
    // Class file Version
    //
    /**
     * Minor version of a {@code class} file. It is the {@code minor_version} in
     * {@code ClassFile} structure.
     *
     * @see ClassFile#getVersion()
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final u2 constant_pool_count;
    public final cp_info[] constant_pool;

    //
    // Class Declaration
    //
    /**
     * A mask of flags used to denote access permissions to and properties of
     * this class or interface.
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent super_class;

    /**
     * Interfaces count of a {@code class} or {@code interface}. It is the
     * {@code interfaces_count} in {@code ClassFile} structure.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
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
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent fields_count;
    public final field_info[] fields;

    //
    // Method
    //
    /**
     * Methods Count of a {@code class} or {@code interface}. It is the
     * {@code methods_count} in {@code ClassFile} structure.
     *
     * @see ClassFile#methods_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent methods_count;
    public final method_info[] methods;

    //
    // Attribute
    //
    /**
     * Attributes count of a {@code class} or {@code interface}. It is the
     * {@code attributes_count} in {@code ClassFile} structure.
     *
     * @see ClassFile#attributes_count
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1">
     * VM Spec: The ClassFile Structure
     * </a>
     */
    public final U2ClassComponent attributes_count;
    public final attribute_info[] attributes;

    /**
     * Creates a new instance of ClassFile from byte array.
     *
     * @param classFile Java class file
     * @throws IOException Error happened when reading the byte array
     * @throws FileFormatException Invalid class file format
     */
    public ClassFile(final File classFile) throws IOException, FileFormatException {
        this(BytesTool.readFileAsBytes(classFile), classFile.getName(), classFile.getCanonicalPath());
    }

    public ClassFile(final byte[] classFileBytes) throws IOException, FileFormatException {
        this(classFileBytes, null, null);
    }

    // java:S127 - "for" loop stop conditions should be invariant --- No we need it because Long/Double type occupies two Constant Pool index
    // java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
    @SuppressWarnings({"java:S127", "java:S3776"})
    public ClassFile(final byte[] classFileBytes, final String fileName, final String filePath) throws IOException, FileFormatException {
        super(classFileBytes, fileName, filePath);

        //
        // Parse the Classfile byte by byte
        //
        PosDataInputStream posDataInputStream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // magic number
        this.magic = new u4(posDataInputStream);
        if (this.magic.value != ClassFile.FORMAT_MAGIC_NUMBER) {
            throw new FileFormatException("The magic number of the byte array is not 0xCAFEBABE");
        }

        // Classfile version
        this.minor_version = new u2(posDataInputStream);
        this.major_version = new u2(posDataInputStream);

        // Constant Pool
        this.constant_pool_count = new u2(posDataInputStream);
        this.constant_pool = new cp_info[this.constant_pool_count.value];
        for (int i = 1; i < this.constant_pool_count.value; i++) {
            short tag = (short) posDataInputStream.readUnsignedByte();

            this.constant_pool[i] = ConstantType.parse(tag, posDataInputStream);
            if (tag == cp_info.ConstantType.CONSTANT_Long.tag || tag == cp_info.ConstantType.CONSTANT_Double.tag) {
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
            this.fields = new field_info[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                this.fields[i] = new field_info(posDataInputStream, this.constant_pool);
            }
        } else {
            this.fields = null;
        }

        // Methods
        this.methods_count = new U2ClassComponent(posDataInputStream);
        final int methodCount = this.methods_count.getValue();

        if (methodCount > 0) {
            this.methods = new method_info[methodCount];
            for (int i = 0; i < methodCount; i++) {
                this.methods[i] = new method_info(posDataInputStream, this.constant_pool);
            }
        } else {
            this.methods = null;
        }

        // Attributes
        this.attributes_count = new U2ClassComponent(posDataInputStream);
        final int attributeCount = this.attributes_count.getValue();
        if (attributeCount > 0) {
            this.attributes = new attribute_info[attributeCount];
            for (int i = 0; i < attributeCount; i++) {
                this.attributes[i] = attribute_info.parse(posDataInputStream, this.constant_pool);
            }
        } else {
            this.attributes = null;
        }
    }

    /**
     * Get the text of {@link #this_class}, which is the class name.
     *
     * @return Corresponding text of {@link #this_class}
     * @throws FileFormatException Invalid {@link #constant_pool} item found
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
     * @throws FileFormatException Invalid {@link #constant_pool} item found
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
     * @throws FileFormatException Invalid {@link #constant_pool} item found
     */
    public String getConstantClassInfoName(int cpIndex) throws FileFormatException {
        String name = null;
        if ((cpIndex >= 0 && cpIndex < this.constant_pool.length)
                && (this.constant_pool[cpIndex] instanceof CONSTANT_Class_info)) {
            CONSTANT_Class_info clsInfo = (CONSTANT_Class_info) this.constant_pool[cpIndex];
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
     * @param cpInfo Constant pool items
     * @return The UTF-8 text
     * @throws FileFormatException Invalid class file format
     */
    public static String getConstantUtf8Value(final int cpIndex, final cp_info[] cpInfo) throws FileFormatException {
        String returnValue = null;

        if ((cpIndex == 0) || (cpIndex >= cpInfo.length)) {
            throw new FileFormatException(String.format(
                    "Constant Pool index is out of range. CP index cannot be zero, and should be less than CP count (=%d). CP index = %d.",
                    cpInfo.length,
                    cpIndex));
        }

        if (cpInfo[cpIndex].tag.value == cp_info.ConstantType.CONSTANT_Utf8.tag) {
            final CONSTANT_Utf8_info utf8Info = (CONSTANT_Utf8_info) cpInfo[cpIndex];
            returnValue = utf8Info.getValue();
        } else {
            throw new FileFormatException(String.format("Unexpected constant pool type: Utf8(%d) expected, but it is '%d'.",
                    cp_info.ConstantType.CONSTANT_Utf8.tag,
                    cpInfo[cpIndex].tag.value));
        }

        return returnValue;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Get raw data
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

        cp_info cp = this.constant_pool[index];
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
    public FormatVersion getVersion() {
        return FormatVersion.valueOf(this.major_version.value, this.minor_version.value);
    }

    @Override
    public String toString() {
        return "Class contains "
                + this.fields_count.value.value + " field(s) and "
                + this.methods_count.value.value + " method(s)";
    }

    @Override
    public String getContentTabName() {
        return "JVM Class File";
    }

    @Override
    public Icon getIcon() {
        return UITool.icon4Java();
    }

    // Lazy creation of JTreeClassFile
    private JTreeClassFile jtreeAdapter;
    private JTreeClassFile getJTreeAdapter() {
        if (this.jtreeAdapter == null) {
            this.jtreeAdapter = new JTreeClassFile();
        }
        return this.jtreeAdapter;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.getJTreeAdapter().generateTreeNode(parentNode, this);
    }

    @Override
    public void treeSelectionChanged(final JTreeNodeFileComponent tnfc, final JTabbedPane tabs) {
        super.treeSelectionChanged(tnfc, tabs);

        if (Code_attribute.ATTRIBUTE_CODE_NODE.equals(tnfc.getText())) {
            final byte[] data = this.getFileByteArray(tnfc.getStartPos(), tnfc.getLength());
            StringBuilder sb = this.getJTreeAdapter().generateOpcodeParseResult(data);
            JTextPane pane = super.tabAddTextPane(tabs, "Opcode");
            pane.setText(sb.toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.CP_PREFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Constant Pool");
            pane.setText(this.getJTreeAdapter().generateReport2CP().toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.FIELDS_PREFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Fields");
            pane.setText(this.getJTreeAdapter().generateReport2Fields().toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.METHODS_PERFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Methods");
            pane.setText(this.getJTreeAdapter().generateReport2Methods().toString());
        }
    }

    /**
     * Class file Format Version numbers. Together, a major and a minor version
     * number determine the version of the class file format. If a class file
     * has major version number M and minor version number m, we denote the
     * version of its class file format as M.m.
     *
     * @author Amos Shi
     */
    public enum FormatVersion {

        /**
         * For 45.3, it could be both {@link JavaSEVersion#VERSION_1_1} or
         * {@link JavaSEVersion#VERSION_1_0_2}. We simply use the
         * {@link JavaSEVersion#VERSION_1_1} which is the newer one.
         */
        FORMAT_45_3(45, 3, JavaSEVersion.VERSION_1_1),
        FORMAT_46(46, 0, JavaSEVersion.VERSION_1_2),
        FORMAT_47(47, 0, JavaSEVersion.VERSION_1_3),
        FORMAT_48(48, 0, JavaSEVersion.VERSION_1_4),
        FORMAT_49(49, 0, JavaSEVersion.VERSION_5_0),
        FORMAT_50(50, 0, JavaSEVersion.VERSION_6),
        FORMAT_51(51, 0, JavaSEVersion.VERSION_7),
        FORMAT_52(52, 0, JavaSEVersion.VERSION_8),
        FORMAT_53(53, 0, JavaSEVersion.VERSION_9),
        FORMAT_54(54, 0, JavaSEVersion.VERSION_10),
        FORMAT_55(55, 0, JavaSEVersion.VERSION_11),
        FORMAT_56(56, 0, JavaSEVersion.VERSION_12),
        FORMAT_57(57, 0, JavaSEVersion.VERSION_13),
        FORMAT_58(58, 0, JavaSEVersion.VERSION_14),
        FORMAT_59(59, 0, JavaSEVersion.VERSION_15),
        FORMAT_60(60, 0, JavaSEVersion.VERSION_16),
        FORMAT_61(61, 0, JavaSEVersion.VERSION_17);

        public final int major_version;
        public final int minor_version;
        public final JavaSEVersion java_se;

        private FormatVersion(int major, int minor, JavaSEVersion javaSE) {
            this.major_version = major;
            this.minor_version = minor;
            this.java_se = javaSE;
        }

        public static FormatVersion valueOf(int major, int minor) {
            for (FormatVersion v : FormatVersion.values()) {
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
