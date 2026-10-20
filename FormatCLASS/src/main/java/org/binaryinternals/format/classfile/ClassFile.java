/*
 * ClassFile.java    2:58 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassHierarchyResolver;
import java.lang.constant.ClassDesc;
import java.lang.reflect.ClassFileFormatVersion;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.attribute.Code_attribute;
import org.binaryinternals.format.classfile.attribute.attribute_info;
import org.binaryinternals.format.classfile.constant.CONSTANT_Class_info;
import org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.binaryinternals.format.classfile.constant.cp_info;
import org.binaryinternals.format.classfile.constant.cp_info.ConstantType;

/// Represents a `class` file. A `class` file structure has the
/// following format:
///
/// ```
///    ClassFile {
///        u4 magic;
///        u2 minor_version;
///        u2 major_version;
///        u2 constant_pool_count;
///        cp_info constant_pool[constant_pool_count-1];
///        u2 access_flags;
///        u2 this_class;
///        u2 super_class;
///        u2 interfaces_count;
///        u2 interfaces[interfaces_count];
///        u2 fields_count;
///        field_info fields[fields_count];
///        u2 methods_count;
///        method_info methods[methods_count];
///        u2 attributes_count;
///        attribute_info attributes[attributes_count];
///    }
/// ```
///
/// The `ClassFile` object is constructed from the class byte array.
///
/// @author Amos Shi
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html">
/// VM Spec: The ClassFile Structure </a>
///
/// ```
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings("java:S116")
public class ClassFile extends FileFormat {

    /// Magic number of `class` file.
    public static final int FORMAT_MAGIC_NUMBER = 0xCAFEBABE;

    public final u4 magic;

    //
    // Class file Version
    //
    /// Minor version of a `class` file. It is the `minor_version` in
    /// `ClassFile` structure.
    ///
    /// @see ClassFile#getVersion()
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final u2 minor_version;

    /// Major version of a `class` file. It is the `major_version` in
    /// `ClassFile` structure.
    ///
    /// ```
    /// The Java virtual machine implementation of Sun's JDK release 1.0.2 supports
    /// class file format versions 45.0 through 45.3 inclusive. Sun's JDK releases
    /// 1.1.X can support class file formats of versions in the range 45.0 through
    /// 45.65535 inclusive. Implementations of version 1.2 of the Java 2 platform
    /// can support class file formats of versions in the range 45.0 through 46.0
    /// inclusive.
    /// ```
    ///
    /// @see ClassFile#getVersion()
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final u2 major_version;

    //
    // Constant pool
    //
    /// Constant Pool Count of a `class` or `interface`. It is the
    /// `constant_pool_count` in `ClassFile` structure.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final u2 constant_pool_count;
    public final cp_info[] constant_pool;

    //
    // Class Declaration
    //
    /// A mask of flags used to denote access permissions to and properties of
    /// this class or interface.
    ///
    /// @see AccessFlag#ACC_PUBLIC
    /// @see AccessFlag#ACC_FINAL
    /// @see AccessFlag#ACC_SUPER
    /// @see AccessFlag#ACC_INTERFACE
    /// @see AccessFlag#ACC_ABSTRACT
    /// @see AccessFlag#ACC_SYNTHETIC
    /// @see AccessFlag#ACC_ANNOTATION
    /// @see AccessFlag#ACC_ENUM
    /// @see AccessFlag#ACC_MODULE
    public final U2ClassComponent access_flags;

    /// `This` class of a `class` or `interface`. It is the
    /// `this_class` in `ClassFile` structure.
    ///
    /// @see ClassFile#getThisClassName()
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent this_class;

    /// Super class of a `class` or `interface`. It is the
    /// `super_class` in `ClassFile` structure.
    ///
    /// @see ClassFile#getSuperClassName()
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent super_class;

    /// Interfaces count of a `class` or `interface`. It is the
    /// `interfaces_count` in `ClassFile` structure.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent interfaces_count;
    public final U2ClassComponent[] interfaces;

    //
    // Field
    //
    /// Fields Count of a `class` or `interface`. It is the
    /// `fields_count` in `ClassFile` structure.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent fields_count;
    public final field_info[] fields;

    //
    // Method
    //
    /// Methods Count of a `class` or `interface`. It is the
    /// `methods_count` in `ClassFile` structure.
    ///
    /// @see ClassFile#methods_count
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent methods_count;
    public final method_info[] methods;

    //
    // Attribute
    //
    /// Attributes count of a `class` or `interface`. It is the
    /// `attributes_count` in `ClassFile` structure.
    ///
    /// @see ClassFile#attributes_count
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.1">
    /// VM Spec: The ClassFile Structure
    /// </a>
    public final U2ClassComponent attributes_count;
    public final attribute_info[] attributes;

    /// Creates a new instance of ClassFile from byte array.
    ///
    /// @param classFile Java class file
    /// @throws IOException Error happened when reading the byte array
    /// @throws FileFormatException Invalid class file format
    public ClassFile(final Path classFile) throws IOException, FileFormatException {
        this(Files.readAllBytes(classFile),
                String.valueOf(classFile.getFileName()),
                classFile.toRealPath().toString());
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
        this.interfaces = new U2ClassComponent[this.interfaces_count.getValue()];
        for (int i = 0; i < this.interfaces.length; i++) {
            this.interfaces[i] = new U2ClassComponent(posDataInputStream);
        }

        // Fields
        this.fields_count = new U2ClassComponent(posDataInputStream);
        this.fields = new field_info[this.fields_count.getValue()];
        for (int i = 0; i < this.fields.length; i++) {
            this.fields[i] = new field_info(posDataInputStream, this.constant_pool);
        }

        // Methods
        this.methods_count = new U2ClassComponent(posDataInputStream);
        this.methods = new method_info[this.methods_count.getValue()];
        for (int i = 0; i < this.methods.length; i++) {
            this.methods[i] = new method_info(posDataInputStream, this.constant_pool);
        }

        // Attributes
        this.attributes_count = new U2ClassComponent(posDataInputStream);
        this.attributes = new attribute_info[this.attributes_count.getValue()];
        for (int i = 0; i < this.attributes.length; i++) {
            this.attributes[i] = attribute_info.parse(posDataInputStream, this.constant_pool);
        }
    }

    /// Get the text of {@link #this_class}, which is the class name.
    ///
    /// @return Corresponding text of {@link #this_class}
    /// @throws FileFormatException Invalid {@link #constant_pool} item found
    public String getThisClassName() throws FileFormatException {
        return this.getConstantClassInfoName(this.this_class.getValue());
    }

    /// Generate the modifier of a `class` or `interface` from the
    /// access flag value.
    ///
    /// @return A string for modifier
    public String getModifiers() {
        return AccessFlag.getClassModifier(this.access_flags.value.value);
    }

    /// Get the text of {@link #super_class}, which is the super class name.
    ///
    /// @return Corresponding text of {@link #super_class}
    /// @throws FileFormatException Invalid {@link #constant_pool} item found
    public String getSuperClassName() throws FileFormatException {
        int superClass = this.super_class.getValue();
        if (superClass == 0) {
            // java.lang.Object.class OR module-info.class
            return "";
        } else {
            return this.getConstantClassInfoName(superClass);
        }
    }

    /// Get `CONSTANT_Class_info` name from the
    /// {@link #constant_pool}.
    ///
    /// @param cpIndex {@link #constant_pool} item index
    /// @return Get `CONSTANT_Class_info` name at `cpIndex`
    /// @throws FileFormatException Invalid {@link #constant_pool} item found
    public String getConstantClassInfoName(int cpIndex) throws FileFormatException {
        if (cpIndex >= 0 && cpIndex < this.constant_pool.length
                && this.constant_pool[cpIndex] instanceof CONSTANT_Class_info clsInfo) {
            return getConstantUtf8Value(clsInfo.name_index.value, this.constant_pool);
        }
        throw new FileFormatException("Constant Pool index (value = %d) is out of range, or it is not a CONSTANT_Class_info.".formatted(cpIndex));
    }

    /// Get `CONSTANT_Utf8_info` text from the
    /// {@link #constant_pool}..
    ///
    /// @param cpIndex Constant Pool object Index for
    /// `CONSTANT_Utf8_info`
    /// @param cpInfo Constant pool items
    /// @return The UTF-8 text
    /// @throws FileFormatException Invalid class file format
    public static String getConstantUtf8Value(final int cpIndex, final cp_info[] cpInfo) throws FileFormatException {
        if ((cpIndex == 0) || (cpIndex >= cpInfo.length)) {
            throw new FileFormatException(
                    "Constant Pool index is out of range. CP index cannot be zero, and should be less than CP count (=%d). CP index = %d.".formatted(
                    cpInfo.length,
                    cpIndex));
        }

        if (cpInfo[cpIndex] instanceof CONSTANT_Utf8_info utf8Info) {
            return utf8Info.getValue();
        }
        throw new FileFormatException("Unexpected constant pool type: Utf8(%d) expected, but it is '%s'.".formatted(
                cp_info.ConstantType.CONSTANT_Utf8.tag,
                (cpInfo[cpIndex] == null) ? "empty" : cpInfo[cpIndex].tag.value));
    }

    // ----------------------------------------------------------------------
    // Get raw data
    /// Returns a string of the constant pool item at the specified
    /// `index`.
    ///
    /// @param index Index in the constant pool
    /// @return String of the constant pool item at `index`
    public String getCPDescription(final int index) {
        // Invalid index
        if (index >= this.constant_pool_count.value) {
            throw new IllegalArgumentException("Index is bigger than constant pool size. size=%d, index=%d".formatted(this.constant_pool_count.value, index));
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

    /// Get the class file format version.
    ///
    /// @return The class file format version, or empty when the
    /// {@link #major_version} is not known by the running JDK
    public Optional<ClassFileFormatVersion> getVersion() {
        try {
            return Optional.of(ClassFileFormatVersion.fromMajor(this.major_version.value));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    /// Whether the class file depends on the preview features of its Java SE
    /// release, that is the {@link #minor_version} is `65535` and the
    /// {@link #major_version} is 56 (Java SE 12) or above.
    ///
    /// @return `true` if the class file uses preview features
    public boolean isPreview() {
        return this.major_version.value >= ClassFileFormatVersion.RELEASE_12.major()
                && this.minor_version.value == java.lang.classfile.ClassFile.PREVIEW_MINOR_VERSION;
    }

    /// Get the Java SE release of the class file, for example
    /// `Java SE 25, preview features`.
    ///
    /// @return The Java SE release text
    public String getJavaSEText() {
        final Optional<ClassFileFormatVersion> version = this.getVersion();
        if (version.isEmpty()) {
            return "unknown Java SE version";
        }

        final String release = JavaSEVersion.of(version.get())
                .map(v -> v.versionName)
                .orElseGet(() -> String.valueOf(version.get().runtimeVersion().feature()));
        return "Java SE " + release + (this.isPreview() ? ", preview features" : "");
    }

    /// Get the class file version text, for example
    /// `69.0 (Java SE 25)`.
    ///
    /// @return The version text
    public String getVersionText() {
        return "%d.%d (%s)".formatted(this.major_version.value, this.minor_version.value, this.getJavaSEText());
    }

    /// Verify the class file with the JVM verifier of the JDK Class-File API.
    ///
    ///
    /// Classes outside the JDK are looked up under the class path root of
    /// {@link #filePath}, for example `/out` for
    /// `/out/com/acme/Foo.class`.
    ///
    ///
    /// @return The verification result
    public Verification verify() {
        final ClassHierarchyResolver jdk = ClassHierarchyResolver.defaultResolver();
        final ClassHierarchyResolver resolver = this.classPathRoot()
                .map(root -> jdk.orElse(ClassHierarchyResolver.ofResourceParsing(desc -> openClass(root, desc))))
                .orElse(jdk);

        List<VerifyError> errors;
        try {
            errors = java.lang.classfile.ClassFile.of(java.lang.classfile.ClassFile.ClassHierarchyResolverOption.of(resolver))
                    .verify(this.fileByteArray);
        } catch (IllegalArgumentException ex) {
            errors = List.of(new VerifyError(ex.getMessage()));
        }

        final List<String> messages = errors.stream()
                .map(e -> (e.getMessage() == null || e.getMessage().isEmpty()) ? e.toString() : e.getMessage())
                .toList();
        final List<String> unresolved = messages.stream()
                .filter(m -> m.contains(Verification.UNRESOLVED_CLASS))
                .distinct()
                .toList();
        final List<String> failures = messages.stream()
                .filter(m -> !m.contains(Verification.UNRESOLVED_CLASS))
                .toList();
        return new Verification(failures, unresolved);
    }

    /// The result of {@link #verify()}.
    ///
    /// @param errors Verification errors
    /// @param unresolved Messages for the classes which could not be found, so
    /// the verification of the code using them is incomplete
    public record Verification(List<String> errors, List<String> unresolved) {

        static final String UNRESOLVED_CLASS = "Could not resolve class";

        /// Creates the result, with immutable copies of the lists.
        public Verification {
            errors = List.copyOf(errors);
            unresolved = List.copyOf(unresolved);
        }

        /// Get a summary, for example `passed` or `2 error(s)`.
        ///
        /// @return Summary text
        public String summary() {
            if (!this.errors.isEmpty()) {
                return this.errors.size() + " error(s)";
            }
            return this.unresolved.isEmpty() ? "passed" : "incomplete, %d class(es) not found".formatted(this.unresolved.size());
        }
    }

    /// The class path root of {@link #filePath}, found by removing one
    /// directory for each package level of {@link #this_class}.
    private Optional<Path> classPathRoot() {
        try {
            final Path file = Path.of(this.filePath);
            final String internalName = this.getConstantClassInfoName(this.this_class.getValue());
            Path root = file.getParent();
            for (int i = 0; i < internalName.chars().filter(c -> c == SignatureConvertor.BINARY_NAME_SEPARATOR).count() && root != null; i++) {
                root = root.getParent();
            }
            if (root != null && Files.isRegularFile(file) && Files.isSameFile(file, root.resolve(internalName + ".class"))) {
                return Optional.of(root);
            }
        } catch (InvalidPathException | IOException | FileFormatException _) {
            // In-memory bytes, or this_class is invalid: no class path
        }
        return Optional.empty();
    }

    private static InputStream openClass(final Path root, final ClassDesc desc) {
        final String descriptor = desc.descriptorString();
        try {
            return Files.newInputStream(root.resolve(descriptor.substring(1, descriptor.length() - 1) + ".class"));
        } catch (IOException | InvalidPathException _) {
            return null;
        }
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
    public Optional<Icons> getIcon() {
        return Optional.of(Icons.Java);
    }

    private final JTreeClassFile jtreeAdapter = new JTreeClassFile();

    private JTreeClassFile getJTreeAdapter() {
        return this.jtreeAdapter;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.getJTreeAdapter().generateTreeNode(parentNode, this);
    }

    @Override
    public void treeSelectionChanged(final JTreeNodeFileComponent tnfc, final JTabbedPane tabs) {
        super.treeSelectionChanged(tnfc, tabs);

        if (Code_attribute.ATTRIBUTE_CODE_TEXT.equals(tnfc.getText())) {
            final byte[] data = this.getFileByteArray(tnfc.getStartPos(), tnfc.getLength());
            StringBuilder sb = this.getJTreeAdapter().generateOpcodeParseResult(data);
            JTextPane pane = super.tabAddTextPane(tabs, "Opcode");
            pane.setText(sb.toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.TEXT_CP_PREFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Constant Pool");
            pane.setText(this.getJTreeAdapter().generateReport2CP().toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.TEXT_FIELDS_PREFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Fields");
            pane.setText(this.getJTreeAdapter().generateReport2Fields().toString());
        } else if (tnfc.getText().startsWith(GenerateTreeNodeClassFile.TEXT_METHODS_PERFIX)) {
            JTextPane pane = super.tabAddTextPane(tabs, "Methods");
            pane.setText(this.getJTreeAdapter().generateReport2Methods().toString());
        }
    }

}
