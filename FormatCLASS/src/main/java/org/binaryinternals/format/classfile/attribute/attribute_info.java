/*
 * attribute_info.java    4:02 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.JavaSEVersion;
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

/**
 * Super class for attributes in class file. All attributes have the following
 * format:
 *
 * <pre>
 *    attribute_info {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u1 info[attribute_length];
 *    }
 * </pre>
 *
 * The contents in {@code info} is determined by {@code attribute_name_index}.
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7">
 * VM Spec: Attributes
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public abstract class attribute_info extends FileComponent implements GenerateTreeNodeClassFile {

    private static final Logger LOG = Logger.getLogger(attribute_info.class.getName());

    /**
     * Non-standard attributes. All of the attributes which are not defined in
     * the VM Spec.
     */
    public static final String UNRECOGNIZED = "[Un-Recognized] ";

    /**
     * Name of the attribute. It is one of the enum names in
     * {@link AttributeTypes}.
     */
    public final String name;

    /**
     * The {@link #attribute_name_index} must be a valid unsigned 16-bit index
     * into the
     * {@link org.binaryinternals.format.classfile.ClassFile#constant_pool} of the
     * {@link ClassFile}, the {@link ClassFile#constant_pool} entry at
     * {@link #attribute_name_index} must be a
     * <code>CONSTANT_Utf8_info structure</code>
     * ({@link org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info})
     * representing the name of the attribute.
     */
    public final u2 attribute_name_index;

    /**
     * The value of the {@link #attribute_length} item indicates the length of
     * the subsequent information in bytes. The length does not include the
     * initial six bytes that contain the {@link #attribute_name_index} and
     * {@link #attribute_length} items.
     */
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

    /**
     * Parse one JVM attribute.This method is not 'public' since it is supposed
     * to be called inside this library only.
     *
     * @param posDataInputStream Input Stream for the class file
     * @param cp Constant Pool item
     * @return Parsed result
     * @throws IOException Input Stream read fail
     * @throws FileFormatException Class file format error
     *
     * <pre>
     * java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
     * </pre>
     */
    @SuppressWarnings("java:S3776")
    public static attribute_info parse(final PosDataInputStream posDataInputStream, final cp_info[] cp) throws IOException, FileFormatException {
        attribute_info attr = null;

        final u2 attrNameIndex = new u2(posDataInputStream);
        if (cp_info.ConstantType.CONSTANT_Utf8.tag == cp[attrNameIndex.value].tag.value) {
            final String type = ((CONSTANT_Utf8_info) cp[attrNameIndex.value]).getValue();

            boolean matched = false;
            for (AttributeTypes attrType : AttributeTypes.values()) {
                if (attrType.getName().equals(type) && attrType.clazz != null) {
                    // There is only 1 constructor in the JVM Attributes
                    Constructor<?> cons = attrType.clazz.getDeclaredConstructors()[0];

                    try {
                        switch (cons.getParameterCount()) {
                            case 3:
                                matched = true;
                                attr = (attribute_info) cons.newInstance(attrNameIndex, type, posDataInputStream);
                                break;
                            case 4:
                                matched = true;
                                attr = (attribute_info) cons.newInstance(attrNameIndex, type, posDataInputStream, cp);
                                break;
                            default:
                                LOG.log(Level.SEVERE, "Coding Problem: unrecognized contructor paramter count found = {0} / {1}", new Object[]{attrType.clazz.getName(), cons.getParameterCount()});
                                break;
                        }
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        LOG.log(Level.SEVERE, "Failed to parse the JVM Attribute {0}, {1}", new Object[]{attrType.clazz.getName(), ex.toString()});
                    }
                }
            }
            if (!matched) {
                LOG.log(Level.WARNING, "Un-recognized Attribute Found !!! Type = {0}", type);
                attr = new Unrecognized_attribute(attrNameIndex, UNRECOGNIZED + type, posDataInputStream);
            }
        } else {
            throw new FileFormatException(String.format("Attribute name_index is not CONSTANT_Utf8. Constant index = %d, type = %d, position = 0x%X", attrNameIndex.value, cp[attrNameIndex.value].tag.value, posDataInputStream.getPos()));
        }

        return attr;
    }

    /**
     * Verify the current class file input stream position is correct.
     *
     * @param endPos Current position
     * @throws FileFormatException Invalid class file format
     */
    protected void checkSize(final int endPos) throws FileFormatException {
        if (this.startPos + this.length != endPos) {
            throw new FileFormatException(String.format("Attribute analysis failed. type='%s', startPos=%d, length=%d, endPos=%d", this.getName(), this.startPos, this.length, endPos));
        }
    }

    /**
     * The get the Message key which contains the corresponding description of current attribute.
     *
     * @return Message key
     */
    public abstract String getMessageKey();

    /**
     * Get the {@link #name} of the attribute, if {@link #name} is
     * <code>null</code>, it will return an empty string.
     *
     * Attributes are used in the {@link ClassFile}, {@link field_info},
     * {@link method_info}, and {@link Code_attribute} structures of the class
     * file format.
     *
     * @return A string of the attribute name
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7">
     * VM Spec: Attributes
     * </a>
     */
    public String getName() {
        return this.name;
    }

    public void generateTreeNodeCommon(final DefaultMutableTreeNode parentNode, final ClassFile classFile) {
        int startPosMoving = this.getStartPos();
        this.addNode(parentNode,
                startPosMoving,
                u2.LENGTH,
                "attribute_name_index",
                String.format(TEXT_CPINDEX_VALUE, this.attribute_name_index.value, "name", this.getName()),
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

    /**
     * Attributes in Java <code>classfile</code>.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7">
     * VM Spec: Attributes </a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum AttributeTypes {

        /**
         * The name for {@code ConstantValue} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.2">
         * VM Spec: The ConstantValue Attribute
         * </a>
         */
        ConstantValue(ConstantValue_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code Code} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.3">
         * VM Spec: The Code Attribute
         * </a>
         */
        Code(Code_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code StackMapTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.4">
         * VM Spec: The StackMapTable Attribute
         * </a>
         */
        StackMapTable(StackMapTable_attribute.class, ClassFile.FormatVersion.FORMAT_50, JavaSEVersion.VERSION_6),
        /**
         * The name for {@code Exceptions} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.5">
         * VM Spec: The Exceptions Attribute
         * </a>
         */
        Exceptions(Exceptions_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code InnerClasses} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.6">
         * VM Spec: The InnerClasses Attribute
         * </a>
         */
        InnerClasses(InnerClasses_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_1),
        /**
         * The name for {@code EnclosingMethod} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.7">
         * VM Spec: The EnclosingMethod Attribute
         * </a>
         */
        EnclosingMethod(EnclosingMethod_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code Synthetic} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.8">
         * VM Spec: The Synthetic Attribute
         * </a>
         */
        Synthetic(Synthetic_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_1),
        /**
         * The name for {@code Signature} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.9">
         * VM Spec: The Signature Attribute
         * </a>
         */
        Signature(Signature_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code SourceFile} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.10">
         * VM Spec: The SourceFile Attribute
         * </a>
         */
        SourceFile(SourceFile_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code SourceDebugExtension} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.11">
         * VM Spec: The SourceDebugExtension Attribute
         * </a>
         */
        SourceDebugExtension(SourceDebugExtension_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code LineNumberTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.12">
         * VM Spec: The LineNumberTable Attribute
         * </a>
         */
        LineNumberTable(LineNumberTable_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code LocalVariableTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.13">
         * VM Spec: The LocalVariableTable Attribute
         * </a>
         */
        LocalVariableTable(LocalVariableTable_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2),
        /**
         * The name for {@code LocalVariableTypeTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.14">
         * VM Spec: The LocalVariableTypeTable Attribute
         * </a>
         */
        LocalVariableTypeTable(LocalVariableTypeTable_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code Deprecated} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.15">
         * VM Spec: The Deprecated Attribute
         * </a>
         */
        Deprecated(Deprecated_attribute.class, ClassFile.FormatVersion.FORMAT_45_3, JavaSEVersion.VERSION_1_1),
        /**
         * The name for {@code RuntimeVisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.16">
         * VM Spec: The RuntimeVisibleAnnotations Attribute
         * </a>
         */
        RuntimeVisibleAnnotations(RuntimeVisibleAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code RuntimeInvisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.17">
         * VM Spec: The RuntimeInvisibleAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleAnnotations(RuntimeInvisibleAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code RuntimeVisibleParameterAnnotations } attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.18">
         * VM Spec: The RuntimeVisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeVisibleParameterAnnotations(RuntimeVisibleParameterAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code RuntimeInvisibleParameterAnnotations} attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.19">
         * VM Spec: The RuntimeInvisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleParameterAnnotations(RuntimeInvisibleParameterAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code RuntimeVisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.20">
         * VM Spec: The RuntimeVisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeVisibleTypeAnnotations(RuntimeVisibleTypeAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_52, JavaSEVersion.VERSION_8),
        /**
         * The name for {@code RuntimeInvisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.21">
         * VM Spec: The RuntimeInvisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleTypeAnnotations(RuntimeInvisibleTypeAnnotations_attribute.class, ClassFile.FormatVersion.FORMAT_52, JavaSEVersion.VERSION_8),
        /**
         * The name for {@code AnnotationDefault} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.22">
         * VM Spec: The AnnotationDefault Attribute
         * </a>
         */
        AnnotationDefault(AnnotationDefault_attribute.class, ClassFile.FormatVersion.FORMAT_49, JavaSEVersion.VERSION_5_0),
        /**
         * The name for {@code BootstrapMethods} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.23">
         * VM Spec: The BootstrapMethods Attribute
         * </a>
         */
        BootstrapMethods(BootstrapMethods_attribute.class, ClassFile.FormatVersion.FORMAT_51, JavaSEVersion.VERSION_7),
        /**
         * The name for {@code MethodParameters} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.24">
         * VM Spec: The MethodParameters Attribute
         * </a>
         */
        MethodParameters(MethodParameters_attribute.class, ClassFile.FormatVersion.FORMAT_52, JavaSEVersion.VERSION_8),
        /**
         * The name for {@code Module} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.25">
         * VM Spec: The Module Attribute
         * </a>
         */
        Module(Module_attribute.class, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9),
        /**
         * The name for {@code ModulePackages} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.26">
         * VM Spec: The ModuModulePackages Attribute
         * </a>
         */
        ModulePackages(ModulePackages_attribute.class, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9),
        /**
         * The name for {@code ModuleMainClass} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.27">
         * VM Spec: The ModuleMainClass Attribute
         * </a>
         */
        ModuleMainClass(ModuleMainClass_attribute.class, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9),
        /**
         * The name for {@code ModuleHashes} attribute type. This is a OpenJDK
         * specific attribute and do not exist in Oracle JDK.
         *
         * @see
         * <a href="http://mail.openjdk.java.net/pipermail/jigsaw-dev/2017-February/011262.html">
         * OpenJDK specific attribute specifications</a>
         */
        ModuleHashes(ModuleHashes_attribute.class, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9),
        /**
         * The name for {@code ModuleTarget} attribute type. This is a OpenJDK
         * specific attribute and do not exist in Oracle JDK.
         *
         * @see
         * <a href="https://openjdk.java.net/jeps/261"> JEP 261: Module
         * System</a>
         */
        ModuleTarget(ModuleTarget_attribute.class, ClassFile.FormatVersion.FORMAT_53, JavaSEVersion.VERSION_9),
        /**
         * The name for {@code NestHost} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.28">
         * VM Spec: The NestHost Attribute
         * </a>
         */
        NestHost(NestHost_attribute.class, ClassFile.FormatVersion.FORMAT_55, JavaSEVersion.VERSION_11),
        /**
         * The name for {@code NestMembers} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.29">
         * VM Spec: The NestMembers Attribute
         * </a>
         */
        NestMembers(NestMembers_attribute.class, ClassFile.FormatVersion.FORMAT_55, JavaSEVersion.VERSION_11),
        /**
         * The name for {@code Bridge} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        Bridge(Bridge_attribute.class, ClassFile.FormatVersion.FORMAT_48, JavaSEVersion.VERSION_1_4),
        /**
         * The name for {@code Record} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.30">
         * VM Spec: The Record Attribute
         * </a>
         */
        Record(Record_attribute.class, ClassFile.FormatVersion.FORMAT_60, JavaSEVersion.VERSION_16),
        /**
         * The name for {@code PermittedSubclasses} attribute type.
         *
         * @see <a href="https://openjdk.java.net/jeps/360"> JEP 360</a>
         * @see <a href="https://openjdk.java.net/jeps/409"> JEP 409</a>
         */
        PermittedSubclasses(PermittedSubclasses_attribute.class, ClassFile.FormatVersion.FORMAT_61, JavaSEVersion.VERSION_17),
        /**
         * The name for {@code ModuleResolution} attribute type.
         *
         * This attribute is found in the following openJDK 17 .class files:
         * <pre>
         *   openjdk-17/jmods/jdk.incubator.foreign.jmod/classes/module-info.class
         *   openjdk-17/jmods/jdk.incubator.vector.jmod/classes/module-info.class
         * </pre>
         */
        ModuleResolution(ModuleResolution_attribute.class, ClassFile.FormatVersion.FORMAT_61, JavaSEVersion.VERSION_17),
        /**
         * The name for {@code Scala} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        Scala(Scala_attribute.class),
        /**
         * The name for {@code ScalaSig} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        ScalaSig(ScalaSig_attribute.class),
        /**
         * The name for {@code org.aspectj.weaver.WeaverVersion} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        WeaverVersion(WeaverVersion_attribute.class, WeaverVersion_attribute.FULLNAME),
        /**
         * The name for {@code org.aspectj.weaver.AjSynthetic} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        AjSynthetic(AjSynthetic_attribute.class, AjSynthetic_attribute.FULLNAME),
        /**
         * The name for {@code org.aspectj.weaver.AjSynthetic} attribute type.
         * This is a none-JVM-Spec attribute.
         */
        MethodDeclarationLineNumber(MethodDeclarationLineNumber_attribute.class, MethodDeclarationLineNumber_attribute.FULLNAME);

        /**
         * The Java class representing to the attributes.
         *
         * If {@link #clazz} is null, which means it is not implemented yet.
         */
        final Class<?> clazz;

        public final String fullname;

        /**
         * Class file format.
         */
        public final ClassFile.FormatVersion format;

        /**
         * Java SE platform version.
         */
        public final JavaSEVersion javaSE;

        AttributeTypes(Class<?> clazz, ClassFile.FormatVersion format, JavaSEVersion javaSE, String name) {
            this.clazz = clazz;
            this.format = format;
            this.javaSE = javaSE;
            this.fullname = name;
        }

        AttributeTypes(Class<?> clazz, ClassFile.FormatVersion format, JavaSEVersion javaSE) {
            this(clazz, format, javaSE, null);
        }

        AttributeTypes(Class<?> clazz, String name) {
            this(clazz, null, null, name);
        }

        AttributeTypes(Class<?> clazz) {
            this(clazz, null, null, null);
        }

        /**
         * Getter for {@link #clazz}.
         *
         * @return Value of {@link #clazz}
         */
        @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "We need it")
        public Class<?> getClassType() {
            return this.clazz;
        }

        /**
         * Get the attribute full name used in <code>.class</code> file.
         *
         * @return Attribute full name used in <code>.class</code> file
         */
        public String getName(){
            return (this.fullname == null) ? this.name() : this.fullname;
        }
    }
}
