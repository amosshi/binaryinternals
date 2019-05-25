/*
 * attribute_info.java    4:02 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.constant.CPInfo;
import org.freeinternals.format.classfile.constant.ConstantUtf8Info;
import org.freeinternals.format.classfile.u2;
import org.freeinternals.format.classfile.u4;

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
 * @since JDK 6.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7">
 * VM Spec: Attributes
 * </a>
 */
public class AttributeInfo extends FileComponent {

    private static final Logger LOG = Logger.getLogger(AttributeInfo.class.getName());

    /**
     * Non-standard attributes. All of the attributes which are not defined in
     * the VM Spec.
     */
    public static final String UNRECOGNIZED = "[Un-Recognized] ";
    /**
     * Name of the attribute. It is one of the enum names in
     * {@link AttributeTypes}.
     */
    public transient final String name;
    /**
     * The {@link #attribute_name_index} must be a valid unsigned 16-bit index
     * into the {@link ClassFile#constant_pool} of the {@link ClassFile}, the
     * {@link ClassFile#constant_pool} entry at {@link #attribute_name_index}
     * must be a <code>CONSTANT_Utf8_info structure</code>
     * ({@link ConstantUtf8Info}) representing the name of the attribute.
     */
    public transient final u2 attribute_name_index;
    /**
     * The value of the {@link #attribute_length} item indicates the length of
     * the subsequent information in bytes. The length does not include the
     * initial six bytes that contain the {@link #attribute_name_index} and
     * {@link #attribute_length} items.
     */
    public transient final u4 attribute_length;

    AttributeInfo(final u2 nameIndex, final String name, final PosDataInputStream posDataInputStream) throws IOException {
        super.startPos = posDataInputStream.getPos() - 2;

        this.name = name;
        this.attribute_name_index = nameIndex;
        this.attribute_length = new u4(posDataInputStream);

        super.length = this.attribute_length.value + u2.LENGTH + u4.LENGTH;
    }

    /**
     * Parse one JVM attribute.
     *
     * This method is not 'public' since it is supposed to be called inside this
     * library only.
     */
    public static AttributeInfo parse(final PosDataInputStream posDataInputStream, final CPInfo[] cp) throws IOException, FileFormatException {
        AttributeInfo attr = null;

        final u2 attrNameIndex = new u2(posDataInputStream);
        if (CPInfo.ConstantType.CONSTANT_Utf8.tag == cp[attrNameIndex.value].tag.value) {
            final String type = ((ConstantUtf8Info) cp[attrNameIndex.value]).getValue();

            boolean matched = false;
            for (AttributeTypes attrType : AttributeTypes.values()) {
                if (attrType.name().equals(type) && attrType.clazz != null) {
                    // There is only 1 constructor in the JVM Attributes
                    Constructor cons = attrType.clazz.getDeclaredConstructors()[0];

                    try {
                        switch (cons.getParameterCount()) {
                            case 3:
                                matched = true;
                                attr = (AttributeInfo) cons.newInstance(attrNameIndex, type, posDataInputStream);
                                break;
                            case 4:
                                matched = true;
                                attr = (AttributeInfo) cons.newInstance(attrNameIndex, type, posDataInputStream, cp);
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
            if (matched == false) {
                LOG.log(Level.WARNING, "Un-recognized Attribute Found !!! Type = {0}", type);
                attr = new AttributeUnrecognized(attrNameIndex, UNRECOGNIZED + type, posDataInputStream);
            }
        } else {
            throw new FileFormatException(String.format("Attribute name_index is not CONSTANT_Utf8. Constant index = %d, type = %d.", attrNameIndex.value, cp[attrNameIndex.value].tag.value));
        }

        return attr;
    }

    /**
     * Verify the current class file input stream position is correct.
     *
     * @param endPos Current position
     * @throws org.freeinternals.format.FileFormatException Invalid class file
     * format
     */
    protected void checkSize(final int endPos) throws FileFormatException {
        if (this.startPos + this.length != endPos) {
            throw new FileFormatException(String.format("Attribute analysis failed. type='%s', startPos=%d, length=%d, endPos=%d", this.getName(), this.startPos, this.length, endPos));
        }
    }

    /**
     * Get the {@link #name} of the attribute, if {@link #name} is
     * <code>null</code>, it will return an empty string.
     *
     * Attributes are used in the {@code ClassFile}
     * ({@link ClassFile}), {@code field_info} ({@link FieldInfo}),
     * {@code method_info} ({@link MethodInfo}), and {@code Code_attribute}
     * structures of the class file format.
     *
     * @return A string of the attribute name
     * @see
     * <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#43817">
     * VM Spec: Attributes
     * </a>
     */
    public String getName() {
        return (this.name != null) ? this.name : "";
    }

    /**
     * Attributes in Java <code>classfile</code>.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html">
     * VM Spec: Attributes </a>
     */
    public enum AttributeTypes {

        /**
         * The name for {@code ConstantValue} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.2">
         * VM Spec: The ConstantValue Attribute
         * </a>
         */
        ConstantValue(AttributeConstantValue.class),
        /**
         * The name for {@code Code} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.3">
         * VM Spec: The Code Attribute
         * </a>
         */
        Code(AttributeCode.class),
        /**
         * The name for {@code StackMapTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.4">
         * VM Spec: The StackMapTable Attribute
         * </a>
         */
        StackMapTable(AttributeStackMapTable.class),
        /**
         * The name for {@code Exceptions} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.5">
         * VM Spec: The Exceptions Attribute
         * </a>
         */
        Exceptions(AttributeExceptions.class),
        /**
         * The name for {@code InnerClasses} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.6">
         * VM Spec: The InnerClasses Attribute
         * </a>
         */
        InnerClasses(AttributeInnerClasses.class),
        /**
         * The name for {@code EnclosingMethod} attribute type.
         *
         * TO DO
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.7">
         * VM Spec: The EnclosingMethod Attribute
         * </a>
         */
        EnclosingMethod(AttributeEnclosingMethod.class),
        /**
         * The name for {@code Synthetic} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.8">
         * VM Spec: The Synthetic Attribute
         * </a>
         */
        Synthetic(AttributeSynthetic.class),
        /**
         * The name for {@code Signature} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.9">
         * VM Spec: The Signature Attribute
         * </a>
         */
        Signature(AttributeSignature.class),
        /**
         * The name for {@code SourceFile} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.10">
         * VM Spec: The SourceFile Attribute
         * </a>
         */
        SourceFile(AttributeSourceFile.class),
        /**
         * The name for {@code SourceDebugExtension} attribute type.
         *
         * TO DO
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.11">
         * VM Spec: The SourceDebugExtension Attribute
         * </a>
         */
        SourceDebugExtension(AttributeSourceDebugExtension.class),
        /**
         * The name for {@code LineNumberTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.12">
         * VM Spec: The LineNumberTable Attribute
         * </a>
         */
        LineNumberTable(AttributeLineNumberTable.class),
        /**
         * The name for {@code LocalVariableTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.13">
         * VM Spec: The LocalVariableTable Attribute
         * </a>
         */
        LocalVariableTable(AttributeLocalVariableTable.class),
        /**
         * The name for {@code LocalVariableTypeTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.14">
         * VM Spec: The LocalVariableTypeTable Attribute
         * </a>
         */
        LocalVariableTypeTable(AttributeLocalVariableTypeTable.class),
        /**
         * The name for {@code Deprecated} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.15">
         * VM Spec: The Deprecated Attribute
         * </a>
         */
        Deprecated(AttributeDeprecated.class),
        /**
         * The name for {@code RuntimeVisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.16">
         * VM Spec: The RuntimeVisibleAnnotations Attribute
         * </a>
         */
        RuntimeVisibleAnnotations(AttributeRuntimeVisibleAnnotations.class),
        /**
         * The name for {@code RuntimeInvisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.17">
         * VM Spec: The RuntimeInvisibleAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleAnnotations(AttributeRuntimeInvisibleAnnotations.class),
        /**
         * The name for {@code RuntimeVisibleParameterAnnotations } attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.18">
         * VM Spec: The RuntimeVisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeVisibleParameterAnnotations(AttributeRuntimeVisibleParameterAnnotations.class),
        /**
         * The name for {@code RuntimeInvisibleParameterAnnotations} attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.19">
         * VM Spec: The RuntimeInvisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleParameterAnnotations(AttributeRuntimeInvisibleParameterAnnotations.class),
        /**
         * The name for {@code RuntimeVisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.20">
         * VM Spec: The RuntimeVisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeVisibleTypeAnnotations(AttributeRuntimeVisibleTypeAnnotations.class),
        /**
         * The name for {@code RuntimeInvisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.21">
         * VM Spec: The RuntimeInvisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleTypeAnnotations(AttributeRuntimeInvisibleTypeAnnotations.class),
        /**
         * The name for {@code AnnotationDefault} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.22">
         * VM Spec: The AnnotationDefault Attribute
         * </a>
         */
        AnnotationDefault(AttributeAnnotationDefault.class),
        /**
         * The name for {@code BootstrapMethods} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.23">
         * VM Spec: The BootstrapMethods Attribute
         * </a>
         */
        BootstrapMethods(AttributeBootstrapMethods.class),
        /**
         * The name for {@code MethodParameters} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.24">
         * VM Spec: The MethodParameters Attribute
         * </a>
         */
        MethodParameters(AttributeMethodParameters.class),
        /**
         * The name for {@code Module} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.25">
         * VM Spec: The Module Attribute
         * </a>
         */
        Module(AttributeModule.class),
        /**
         * The name for {@code ModulePackages} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.26">
         * VM Spec: The ModuModulePackages Attribute
         * </a>
         */
        ModulePackages(AttributeModulePackages.class),
        /**
         * The name for {@code ModuleMainClass} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.27">
         * VM Spec: The ModuleMainClass Attribute
         * </a>
         */
        ModuleMainClass(AttributeModuleMainClass.class),
        /**
         * The name for {@code ModuleHashes} attribute type. This is a OpenJDK
         * specific attribute and do not exist in Oracle JDK.
         *
         * @see
         * <a href="http://mail.openjdk.java.net/pipermail/jigsaw-dev/2017-February/011262.html">OpenJDK
         * specific attribute specifications</a>
         */
        ModuleHashes(AttributeModuleHashes.class),
        /**
         * The name for {@code ModuleTarget} attribute type. This is a OpenJDK
         * specific attribute and do not exist in Oracle JDK.
         *
         * @see
         * <a href="https://openjdk.java.net/jeps/261"> JEP 261: Module
         * System</a>
         */
        ModuleTarget(AttributeModuleTarget.class),
        /**
         * The name for {@code NestHost} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.28">
         * VM Spec: The NestHost Attribute
         * </a>
         */
        NestHost(AttributeNestHost.class),
        /**
         * The name for {@code NestMembers} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.29">
         * VM Spec: The NestMembers Attribute
         * </a>
         */
        NestMembers(AttributeNestMembers.class);

        /**
         * The Java class representing to the attributes.
         *
         * If {@link #clazz} is null, which means it is not implemented yet.
         */
        final Class<?> clazz;

        AttributeTypes(Class clazz) {
            this.clazz = clazz;
        }

        /**
         * Getter for {@link #clazz}.
         *
         * @return Value of {@link #clazz}
         */
        public Class<?> getClassType() {
            return this.clazz;
        }
    }
}
