/*
 * attribute_info.java    4:02 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.lang.classfile.Attributes;
import java.lang.reflect.ClassFileFormatVersion;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.attribute.aspectj.AjSynthetic_attribute;
import org.binaryinternals.format.classfile.attribute.aspectj.MethodDeclarationLineNumber_attribute;
import org.binaryinternals.format.classfile.attribute.aspectj.WeaverVersion_attribute;
import org.binaryinternals.format.classfile.attribute.scala.ScalaSig_attribute;
import org.binaryinternals.format.classfile.attribute.scala.Scala_attribute;
import org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.binaryinternals.format.classfile.constant.cp_info;
import org.binaryinternals.format.classfile.field_info;
import org.binaryinternals.format.classfile.method_info;
import org.binaryinternals.format.classfile.u2;
import org.binaryinternals.format.classfile.u4;

/// Super class for attributes in class file. All attributes have the following
/// format:
///
/// ```
///    attribute_info {
///        u2 attribute_name_index;
///        u4 attribute_length;
///
///        u1 info[attribute_length];
///    }
/// ```
///
/// The contents in `info` is determined by `attribute_name_index`.
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7">
/// VM Spec: Attributes
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public abstract class attribute_info extends FileComponent implements GenerateTreeNodeClassFile {

    private static final Logger LOG = Logger.getLogger(attribute_info.class.getName());

    /// Non-standard attributes. All of the attributes which are not defined in
    /// the VM Spec.
    public static final String UNRECOGNIZED = "[Un-Recognized] ";

    /// Name of the attribute. It is one of the enum names in
    /// {@link AttributeTypes}.
    public final String name;

    /// The {@link #attribute_name_index} must be a valid unsigned 16-bit index
    /// into the
    /// {@link org.binaryinternals.format.classfile.ClassFile#constant_pool} of the
    /// {@link ClassFile}, the {@link ClassFile#constant_pool} entry at
    /// {@link #attribute_name_index} must be a
    /// `CONSTANT_Utf8_info structure`
    /// ({@link org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info})
    /// representing the name of the attribute.
    public final u2 attribute_name_index;

    /// The value of the {@link #attribute_length} item indicates the length of
    /// the subsequent information in bytes. The length does not include the
    /// initial six bytes that contain the {@link #attribute_name_index} and
    /// {@link #attribute_length} items.
    public final u4 attribute_length;

    @SuppressWarnings("java:S5993")
    public attribute_info(final u2 nameIndex, final String name, final PosDataInputStream posDataInputStream) throws IOException {
        super.startPos = posDataInputStream.getPos() - 2;

        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("The attribute name annot be none. location=0x" + Integer.toHexString(posDataInputStream.getPos()));
        }
        this.name = name;
        this.attribute_name_index = nameIndex;
        this.attribute_length = new u4(posDataInputStream);

        super.length = this.attribute_length.value + u2.LENGTH + u4.LENGTH;
    }

    /// Parse one JVM attribute.This method is not 'public' since it is supposed
    /// to be called inside this library only.
    ///
    /// @param posDataInputStream Input Stream for the class file
    /// @param cp Constant Pool item
    /// @return Parsed result
    /// @throws IOException Input Stream read fail
    /// @throws FileFormatException Class file format error
    public static attribute_info parse(final PosDataInputStream posDataInputStream, final cp_info[] cp) throws IOException, FileFormatException {
        final int attrNameIndexPos = posDataInputStream.getPos();
        final u2 attrNameIndex = new u2(posDataInputStream);
        if (attrNameIndex.value >= cp.length || !(cp[attrNameIndex.value] instanceof CONSTANT_Utf8_info nameInfo)) {
            throw new FileFormatException("Attribute name_index is not CONSTANT_Utf8. Constant index = %d, position = 0x%X".formatted(
                    attrNameIndex.value, attrNameIndexPos));
        }

        final String type = nameInfo.getValue();
        final Optional<AttributeTypes> attrType = AttributeTypes.of(type);
        if (attrType.isEmpty()) {
            LOG.log(Level.WARNING, "Un-recognized Attribute Found !!! Type = {0}", type);
            return new Unrecognized_attribute(attrNameIndex, UNRECOGNIZED + type, posDataInputStream);
        }

        return switch (attrType.get()) {
                case ConstantValue -> new ConstantValue_attribute(attrNameIndex, type, posDataInputStream);
                case Code -> new Code_attribute(attrNameIndex, type, posDataInputStream, cp);
                case StackMapTable -> new StackMapTable_attribute(attrNameIndex, type, posDataInputStream);
                case Exceptions -> new Exceptions_attribute(attrNameIndex, type, posDataInputStream);
                case InnerClasses -> new InnerClasses_attribute(attrNameIndex, type, posDataInputStream);
                case EnclosingMethod -> new EnclosingMethod_attribute(attrNameIndex, type, posDataInputStream);
                case Synthetic -> new Synthetic_attribute(attrNameIndex, type, posDataInputStream);
                case Signature -> new Signature_attribute(attrNameIndex, type, posDataInputStream, cp);
                case SourceFile -> new SourceFile_attribute(attrNameIndex, type, posDataInputStream);
                case SourceDebugExtension -> new SourceDebugExtension_attribute(attrNameIndex, type, posDataInputStream);
                case LineNumberTable -> new LineNumberTable_attribute(attrNameIndex, type, posDataInputStream);
                case LocalVariableTable -> new LocalVariableTable_attribute(attrNameIndex, type, posDataInputStream);
                case LocalVariableTypeTable -> new LocalVariableTypeTable_attribute(attrNameIndex, type, posDataInputStream);
                case Deprecated -> new Deprecated_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeVisibleAnnotations -> new RuntimeVisibleAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeInvisibleAnnotations -> new RuntimeInvisibleAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeVisibleParameterAnnotations -> new RuntimeVisibleParameterAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeInvisibleParameterAnnotations -> new RuntimeInvisibleParameterAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeVisibleTypeAnnotations -> new RuntimeVisibleTypeAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case RuntimeInvisibleTypeAnnotations -> new RuntimeInvisibleTypeAnnotations_attribute(attrNameIndex, type, posDataInputStream);
                case AnnotationDefault -> new AnnotationDefault_attribute(attrNameIndex, type, posDataInputStream);
                case BootstrapMethods -> new BootstrapMethods_attribute(attrNameIndex, type, posDataInputStream);
                case MethodParameters -> new MethodParameters_attribute(attrNameIndex, type, posDataInputStream);
                case Module -> new Module_attribute(attrNameIndex, type, posDataInputStream);
                case ModulePackages -> new ModulePackages_attribute(attrNameIndex, type, posDataInputStream);
                case ModuleMainClass -> new ModuleMainClass_attribute(attrNameIndex, type, posDataInputStream);
                case ModuleHashes -> new ModuleHashes_attribute(attrNameIndex, type, posDataInputStream);
                case ModuleTarget -> new ModuleTarget_attribute(attrNameIndex, type, posDataInputStream);
                case NestHost -> new NestHost_attribute(attrNameIndex, type, posDataInputStream);
                case NestMembers -> new NestMembers_attribute(attrNameIndex, type, posDataInputStream);
                case Bridge -> new Bridge_attribute(attrNameIndex, type, posDataInputStream);
                case Record -> new Record_attribute(attrNameIndex, type, posDataInputStream, cp);
                case PermittedSubclasses -> new PermittedSubclasses_attribute(attrNameIndex, type, posDataInputStream);
                case ModuleResolution -> new ModuleResolution_attribute(attrNameIndex, type, posDataInputStream);
                case Scala -> new Scala_attribute(attrNameIndex, type, posDataInputStream);
                case ScalaSig -> new ScalaSig_attribute(attrNameIndex, type, posDataInputStream);
                case WeaverVersion -> new WeaverVersion_attribute(attrNameIndex, type, posDataInputStream);
                case AjSynthetic -> new AjSynthetic_attribute(attrNameIndex, type, posDataInputStream);
                case MethodDeclarationLineNumber -> new MethodDeclarationLineNumber_attribute(attrNameIndex, type, posDataInputStream);
                default -> new Unrecognized_attribute(attrNameIndex, UNRECOGNIZED + type, posDataInputStream);
        };
    }

    /// Verify the current class file input stream position is correct.
    ///
    /// @param endPos Current position
    /// @throws FileFormatException Invalid class file format
    protected void checkSize(final int endPos) throws FileFormatException {
        checkAttributeSize(this.name, this.startPos, this.length, endPos);
    }

    protected static void checkAttributeSize(
            final String name,
            final int startPos,
            final int length,
            final int endPos) throws FileFormatException {
        if (startPos + length != endPos) {
            throw new FileFormatException("Attribute analysis failed. type='%s', startPos=%d, length=%d, endPos=%d".formatted(name, startPos, length, endPos));
        }
    }

    /// The get the Message key which contains the corresponding description of current attribute.
    ///
    /// @return Message key
    public abstract String getMessageKey();

    /// Get the {@link #name} of the attribute, if {@link #name} is
    /// `null`, it will return an empty string.
    ///
    /// Attributes are used in the {@link ClassFile}, {@link field_info},
    /// {@link method_info}, and {@link Code_attribute} structures of the class
    /// file format.
    ///
    /// @return A string of the attribute name
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7">
    /// VM Spec: Attributes
    /// </a>
    public String getName() {
        return this.name;
    }

    public void generateTreeNodeCommon(final DefaultMutableTreeNode parentNode, final ClassFile classFile) {
        int startPosMoving = this.getStartPos();
        this.addNode(parentNode,
                startPosMoving,
                u2.LENGTH,
                "attribute_name_index",
                TEXT_CPINDEX_VALUE.formatted(this.attribute_name_index.value, "name", this.getName()),
                "msg_attribute_info__attribute_name_index",
                Icons.Name
        );

        startPosMoving += u2.LENGTH;
        this.addNode(parentNode,
                startPosMoving,
                u4.LENGTH,
                "attribute_length",
                this.attribute_length.value,
                "msg_attribute_info__attribute_length",
                Icons.Length
        );

        this.generateTreeNode(parentNode, classFile);
    }

    /// Attributes in Java `classfile`.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7">
    /// VM Spec: Attributes </a>
    ///
    /// ```
    /// java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
    /// ```
    @SuppressWarnings("java:S115")
    public enum AttributeTypes {

        /// The name for `ConstantValue` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.2">
        /// VM Spec: The ConstantValue Attribute
        /// </a>
        ConstantValue(ConstantValue_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_CONSTANT_VALUE),
        /// The name for `Code` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.3">
        /// VM Spec: The Code Attribute
        /// </a>
        Code(Code_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_CODE),
        /// The name for `StackMapTable` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.4">
        /// VM Spec: The StackMapTable Attribute
        /// </a>
        StackMapTable(StackMapTable_attribute.class, ClassFileFormatVersion.RELEASE_6, Attributes.NAME_STACK_MAP_TABLE),
        /// The name for `Exceptions` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.5">
        /// VM Spec: The Exceptions Attribute
        /// </a>
        Exceptions(Exceptions_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_EXCEPTIONS),
        /// The name for `InnerClasses` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.6">
        /// VM Spec: The InnerClasses Attribute
        /// </a>
        InnerClasses(InnerClasses_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_INNER_CLASSES),
        /// The name for `EnclosingMethod` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.7">
        /// VM Spec: The EnclosingMethod Attribute
        /// </a>
        EnclosingMethod(EnclosingMethod_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_ENCLOSING_METHOD),
        /// The name for `Synthetic` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.8">
        /// VM Spec: The Synthetic Attribute
        /// </a>
        Synthetic(Synthetic_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_SYNTHETIC),
        /// The name for `Signature` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.9">
        /// VM Spec: The Signature Attribute
        /// </a>
        Signature(Signature_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_SIGNATURE),
        /// The name for `SourceFile` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.10">
        /// VM Spec: The SourceFile Attribute
        /// </a>
        SourceFile(SourceFile_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_SOURCE_FILE),
        /// The name for `SourceDebugExtension` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.11">
        /// VM Spec: The SourceDebugExtension Attribute
        /// </a>
        SourceDebugExtension(SourceDebugExtension_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_SOURCE_DEBUG_EXTENSION),
        /// The name for `LineNumberTable` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.12">
        /// VM Spec: The LineNumberTable Attribute
        /// </a>
        LineNumberTable(LineNumberTable_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_LINE_NUMBER_TABLE),
        /// The name for `LocalVariableTable` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.13">
        /// VM Spec: The LocalVariableTable Attribute
        /// </a>
        LocalVariableTable(LocalVariableTable_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_LOCAL_VARIABLE_TABLE),
        /// The name for `LocalVariableTypeTable` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.14">
        /// VM Spec: The LocalVariableTypeTable Attribute
        /// </a>
        LocalVariableTypeTable(LocalVariableTypeTable_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_LOCAL_VARIABLE_TYPE_TABLE),
        /// The name for `Deprecated` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.15">
        /// VM Spec: The Deprecated Attribute
        /// </a>
        Deprecated(Deprecated_attribute.class, ClassFileFormatVersion.RELEASE_1, Attributes.NAME_DEPRECATED),
        /// The name for `RuntimeVisibleAnnotations ` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.16">
        /// VM Spec: The RuntimeVisibleAnnotations Attribute
        /// </a>
        RuntimeVisibleAnnotations(RuntimeVisibleAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_RUNTIME_VISIBLE_ANNOTATIONS),
        /// The name for `RuntimeInvisibleAnnotations ` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.17">
        /// VM Spec: The RuntimeInvisibleAnnotations Attribute
        /// </a>
        RuntimeInvisibleAnnotations(RuntimeInvisibleAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_RUNTIME_INVISIBLE_ANNOTATIONS),
        /// The name for `RuntimeVisibleParameterAnnotations ` attribute
        /// type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.18">
        /// VM Spec: The RuntimeVisibleParameterAnnotations Attribute
        /// </a>
        RuntimeVisibleParameterAnnotations(RuntimeVisibleParameterAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS),
        /// The name for `RuntimeInvisibleParameterAnnotations` attribute
        /// type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.19">
        /// VM Spec: The RuntimeInvisibleParameterAnnotations Attribute
        /// </a>
        RuntimeInvisibleParameterAnnotations(RuntimeInvisibleParameterAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS),
        /// The name for `RuntimeVisibleTypeAnnotations` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.20">
        /// VM Spec: The RuntimeVisibleTypeAnnotations Attribute
        /// </a>
        RuntimeVisibleTypeAnnotations(RuntimeVisibleTypeAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_8, Attributes.NAME_RUNTIME_VISIBLE_TYPE_ANNOTATIONS),
        /// The name for `RuntimeInvisibleTypeAnnotations` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.21">
        /// VM Spec: The RuntimeInvisibleTypeAnnotations Attribute
        /// </a>
        RuntimeInvisibleTypeAnnotations(RuntimeInvisibleTypeAnnotations_attribute.class, ClassFileFormatVersion.RELEASE_8, Attributes.NAME_RUNTIME_INVISIBLE_TYPE_ANNOTATIONS),
        /// The name for `AnnotationDefault` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.22">
        /// VM Spec: The AnnotationDefault Attribute
        /// </a>
        AnnotationDefault(AnnotationDefault_attribute.class, ClassFileFormatVersion.RELEASE_5, Attributes.NAME_ANNOTATION_DEFAULT),
        /// The name for `BootstrapMethods` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.23">
        /// VM Spec: The BootstrapMethods Attribute
        /// </a>
        BootstrapMethods(BootstrapMethods_attribute.class, ClassFileFormatVersion.RELEASE_7, Attributes.NAME_BOOTSTRAP_METHODS),
        /// The name for `MethodParameters` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.24">
        /// VM Spec: The MethodParameters Attribute
        /// </a>
        MethodParameters(MethodParameters_attribute.class, ClassFileFormatVersion.RELEASE_8, Attributes.NAME_METHOD_PARAMETERS),
        /// The name for `Module` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.25">
        /// VM Spec: The Module Attribute
        /// </a>
        Module(Module_attribute.class, ClassFileFormatVersion.RELEASE_9, Attributes.NAME_MODULE),
        /// The name for `ModulePackages` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.26">
        /// VM Spec: The ModuModulePackages Attribute
        /// </a>
        ModulePackages(ModulePackages_attribute.class, ClassFileFormatVersion.RELEASE_9, Attributes.NAME_MODULE_PACKAGES),
        /// The name for `ModuleMainClass` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.27">
        /// VM Spec: The ModuleMainClass Attribute
        /// </a>
        ModuleMainClass(ModuleMainClass_attribute.class, ClassFileFormatVersion.RELEASE_9, Attributes.NAME_MODULE_MAIN_CLASS),
        /// The name for `ModuleHashes` attribute type. This is a OpenJDK
        /// specific attribute and do not exist in Oracle JDK.
        ///
        /// @see
        /// <a href="http://mail.openjdk.java.net/pipermail/jigsaw-dev/2017-February/011262.html">
        /// OpenJDK specific attribute specifications</a>
        ModuleHashes(ModuleHashes_attribute.class, ClassFileFormatVersion.RELEASE_9, Attributes.NAME_MODULE_HASHES),
        /// The name for `ModuleTarget` attribute type. This is a OpenJDK
        /// specific attribute and do not exist in Oracle JDK.
        ///
        /// @see
        /// <a href="https://openjdk.java.net/jeps/261"> JEP 261: Module
        /// System</a>
        ModuleTarget(ModuleTarget_attribute.class, ClassFileFormatVersion.RELEASE_9, Attributes.NAME_MODULE_TARGET),
        /// The name for `NestHost` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.28">
        /// VM Spec: The NestHost Attribute
        /// </a>
        NestHost(NestHost_attribute.class, ClassFileFormatVersion.RELEASE_11, Attributes.NAME_NEST_HOST),
        /// The name for `NestMembers` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.29">
        /// VM Spec: The NestMembers Attribute
        /// </a>
        NestMembers(NestMembers_attribute.class, ClassFileFormatVersion.RELEASE_11, Attributes.NAME_NEST_MEMBERS),
        /// The name for `Bridge` attribute type.
        /// This is a none-JVM-Spec attribute.
        Bridge(Bridge_attribute.class, ClassFileFormatVersion.RELEASE_4),
        /// The name for `Record` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.30">
        /// VM Spec: The Record Attribute
        /// </a>
        Record(Record_attribute.class, ClassFileFormatVersion.RELEASE_16, Attributes.NAME_RECORD),
        /// The name for `PermittedSubclasses` attribute type.
        ///
        /// @see
        /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.31">
        /// VM Spec: The PermittedSubclasses Attribute
        /// </a>
        /// @see <a href="https://openjdk.java.net/jeps/360"> JEP 360</a>
        /// @see <a href="https://openjdk.java.net/jeps/409"> JEP 409</a>
        PermittedSubclasses(PermittedSubclasses_attribute.class, ClassFileFormatVersion.RELEASE_17, Attributes.NAME_PERMITTED_SUBCLASSES),
        /// The name for `ModuleResolution` attribute type.
        ///
        /// This attribute is found in the following openJDK 17 .class files:
        /// ```
        ///   openjdk-17/jmods/jdk.incubator.foreign.jmod/classes/module-info.class
        ///   openjdk-17/jmods/jdk.incubator.vector.jmod/classes/module-info.class
        /// ```
        ModuleResolution(ModuleResolution_attribute.class, ClassFileFormatVersion.RELEASE_17, Attributes.NAME_MODULE_RESOLUTION),
        /// The name for `Scala` attribute type.
        /// This is a none-JVM-Spec attribute.
        Scala(Scala_attribute.class),
        /// The name for `ScalaSig` attribute type.
        /// This is a none-JVM-Spec attribute.
        ScalaSig(ScalaSig_attribute.class),
        /// The name for `org.aspectj.weaver.WeaverVersion` attribute type.
        /// This is a none-JVM-Spec attribute.
        WeaverVersion(WeaverVersion_attribute.class, WeaverVersion_attribute.FULLNAME),
        /// The name for `org.aspectj.weaver.AjSynthetic` attribute type.
        /// This is a none-JVM-Spec attribute.
        AjSynthetic(AjSynthetic_attribute.class, AjSynthetic_attribute.FULLNAME),
        /// The name for `org.aspectj.weaver.AjSynthetic` attribute type.
        /// This is a none-JVM-Spec attribute.
        MethodDeclarationLineNumber(MethodDeclarationLineNumber_attribute.class, MethodDeclarationLineNumber_attribute.FULLNAME);

        /// The Java class representing to the attributes.
        ///
        /// If {@link #clazz} is null, which means it is not implemented yet.
        final Class<?> clazz;

        public final String fullname;

        /// Class file format.
        public final ClassFileFormatVersion format;

        AttributeTypes(Class<?> clazz, ClassFileFormatVersion format, String name) {
            this.clazz = clazz;
            this.format = format;
            this.fullname = name;
        }

        AttributeTypes(Class<?> clazz, ClassFileFormatVersion format) {
            this(clazz, format, null);
        }

        AttributeTypes(Class<?> clazz, String name) {
            this(clazz, null, name);
        }

        AttributeTypes(Class<?> clazz) {
            this(clazz, null, null);
        }

        private static final Map<String, AttributeTypes> BY_NAME = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(AttributeTypes::getName, Function.identity()));

        /// Get the attribute type of an attribute name used in `.class`
        /// files.
        ///
        /// @param name The attribute name
        /// @return The attribute type, or empty for an unrecognized attribute
        public static Optional<AttributeTypes> of(final String name) {
            return Optional.ofNullable(BY_NAME.get(name));
        }

        /// Getter for {@link #clazz}.
        ///
        /// @return Value of {@link #clazz}
        public Class<?> getClassType() {
            return this.clazz;
        }

        /// Get the attribute full name used in `.class` file.
        ///
        /// @return Attribute full name used in `.class` file
        public String getName(){
            return (this.fullname == null) ? this.name() : this.fullname;
        }
    }
}
