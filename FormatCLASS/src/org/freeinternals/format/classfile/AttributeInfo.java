/*
 * attribute_info.java    4:02 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * Super class for attributes in class file. All attributes have the following
 * format:
 *
 * <pre>
 *    attribute_info {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *        u1 info[attribute_length];
 *    }
 * </pre>
 *
 * The contents in {@code info} is determined by {@code attribute_name_index}.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7">
 * VM Spec: Attributes
 * </a>
 */
public class AttributeInfo extends FileComponent {

    private static final Logger Log = Logger.getLogger(AttributeInfo.class.getName());

    /**
     * Non-standard attributes. All of the attributes which are not defined in
     * the VM Spec.
     */
    public static final String Extended = "[Extended Attr.] ";
    /**
     * Name of the attribute. It is one of the enum names in
     * {@link AttributeName}.
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

    AttributeInfo(
            final u2 nameIndex,
            final String name,
            final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {
        super.startPos = posDataInputStream.getPos() - 2;

        this.name = name;
        this.attribute_name_index = new u2(nameIndex.value);
        this.attribute_length = new u4(posDataInputStream.readInt());

        super.length = this.attribute_length.value + 6;
    }

    static AttributeInfo parse(
            final PosDataInputStream posDataInputStream,
            final CPInfo[] cp)
            throws IOException, FileFormatException {
        AttributeInfo attr;

        final u2 attrNameIndex = new u2(posDataInputStream.readUnsignedShort());
        if (CPInfo.ConstantType.CONSTANT_Utf8.tag == cp[attrNameIndex.value].tag.value) {
            final String type = ((ConstantUtf8Info) cp[attrNameIndex.value]).getValue();

            if (AttributeName.ConstantValue.name().equals(type)) {
                // 4.7.2. The ConstantValue Attribute
                attr = new AttributeConstantValue(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.Code.name().equals(type)) {
                // 4.7.3. The Code Attribute 
                attr = new AttributeCode(attrNameIndex, type, posDataInputStream, cp);
            } else if (AttributeName.StackMapTable.name().equals(type)) {
                // 4.7.4. The StackMapTable Attribute
                attr = new AttributeStackMapTable(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.Exceptions.name().equals(type)) {
                // 4.7.5. The Exceptions Attribute 
                attr = new AttributeExceptions(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.InnerClasses.name().equals(type)) {
                // 4.7.6. The InnerClasses Attribute 
                attr = new AttributeInnerClasses(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.EnclosingMethod.name().equals(type)) {
                // 4.7.7. The EnclosingMethod Attribute
                attr = new AttributeEnclosingMethod(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.Synthetic.name().equals(type)) {
                // 4.7.8. The Synthetic Attribute 
                attr = new AttributeSynthetic(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.Signature.name().equals(type)) {
                // 4.7.9. The Signature Attribute 
                attr = new AttributeSignature(attrNameIndex, type, posDataInputStream, cp);
            } else if (AttributeName.SourceFile.name().equals(type)) {
                // 4.7.10. The SourceFile Attribute 
                attr = new AttributeSourceFile(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.SourceDebugExtension.name().equals(type)) {
                // 4.7.11. The SourceDebugExtension Attribute
                attr = new AttributeSourceDebugExtension(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.LineNumberTable.name().equals(type)) {
                // 4.7.12. The LineNumberTable Attribute 
                attr = new AttributeLineNumberTable(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.LocalVariableTable.name().equals(type)) {
                // 4.7.13. The LocalVariableTable Attribute
                attr = new AttributeLocalVariableTable(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.LocalVariableTypeTable.name().equals(type)) {
                // 4.7.14. The LocalVariableTypeTable Attribute 
                attr = new AttributeLocalVariableTypeTable(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.Deprecated.name().equals(type)) {
                // 4.7.15. The Deprecated Attribute
                attr = new AttributeDeprecated(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.RuntimeVisibleAnnotations.name().equals(type)) {
                // 4.7.16. The RuntimeVisibleAnnotations Attribute 
                attr = new AttributeRuntimeVisibleAnnotations(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.RuntimeInvisibleAnnotations.name().equals(type)) {
                // 4.7.17. The RuntimeInvisibleAnnotations Attribute 
                attr = new AttributeRuntimeInvisibleAnnotations(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.RuntimeVisibleParameterAnnotations.name().equals(type)) {
                // 4.7.18. The RuntimeVisibleParameterAnnotations Attribute 
                attr = new AttributeRuntimeVisibleParameterAnnotations(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.RuntimeInvisibleParameterAnnotations.name().equals(type)) {
                // 4.7.19. The RuntimeInvisibleParameterAnnotations Attribute 
                attr = new AttributeRuntimeInvisibleParameterAnnotations(attrNameIndex, type, posDataInputStream);
                // 4.7.20. The RuntimeVisibleTypeAnnotations Attribute
                // 4.7.21. The RuntimeInvisibleTypeAnnotations Attribute
            } else if (AttributeName.AnnotationDefault.name().equals(type)) {
                // 4.7.22. The AnnotationDefault Attribute
                attr = new AttributeAnnotationDefault(attrNameIndex, type, posDataInputStream);
            } else if (AttributeName.BootstrapMethods.name().equals(type)) {
                // 4.7.23. The BootstrapMethods Attribute
                attr = new AttributeBootstrapMethods(attrNameIndex, type, posDataInputStream);
                // 4.7.24. The MethodParameters Attribute
            } else {
                Log.log(Level.INFO, "Un-recognized Attribute Found !!! Type = {0}", type);
                System.out.println( "Un-recognized Attribute Found !!! Type = " + type);    // We keep this in case no logger settings exist
                attr = new AttributeExtended(attrNameIndex, Extended + type, posDataInputStream);
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
    protected void checkSize(final int endPos)
            throws FileFormatException {
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

    public enum AttributeName {

        /**
         * The name for {@code ConstantValue} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.2">
         * VM Spec: The ConstantValue Attribute
         * </a>
         */
        ConstantValue,
        /**
         * The name for {@code Code} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3">
         * VM Spec: The Code Attribute
         * </a>
         */
        Code,
        /**
         * The name for {@code StackMapTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.4">
         * VM Spec: The StackMapTable Attribute
         * </a>
         */
        StackMapTable,
        /**
         * The name for {@code Exceptions} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.5">
         * VM Spec: The Exceptions Attribute
         * </a>
         */
        Exceptions,
        /**
         * The name for {@code InnerClasses} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.6">
         * VM Spec: The InnerClasses Attribute
         * </a>
         */
        InnerClasses,
        /**
         * The name for {@code EnclosingMethod} attribute type.
         *
         * TO DO
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.7">
         * VM Spec: The EnclosingMethod Attribute
         * </a>
         */
        EnclosingMethod,
        /**
         * The name for {@code Synthetic} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.8">
         * VM Spec: The Synthetic Attribute
         * </a>
         */
        Synthetic,
        /**
         * The name for {@code Signature} attribute type.
         *
         * TO DO
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.9">
         * VM Spec: The Signature Attribute
         * </a>
         */
        Signature,
        /**
         * The name for {@code SourceFile} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.10">
         * VM Spec: The SourceFile Attribute
         * </a>
         */
        SourceFile,
        /**
         * The name for {@code SourceDebugExtension} attribute type.
         *
         * TO DO
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.11">
         * VM Spec: The SourceDebugExtension Attribute
         * </a>
         */
        SourceDebugExtension,
        /**
         * The name for {@code LineNumberTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.12">
         * VM Spec: The LineNumberTable Attribute
         * </a>
         */
        LineNumberTable,
        /**
         * The name for {@code LocalVariableTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.13">
         * VM Spec: The LocalVariableTable Attribute
         * </a>
         */
        LocalVariableTable,
        /**
         * The name for {@code LocalVariableTypeTable} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.14">
         * VM Spec: The LocalVariableTypeTable Attribute
         * </a>
         */
        LocalVariableTypeTable,
        /**
         * The name for {@code Deprecated} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.15">
         * VM Spec: The Deprecated Attribute
         * </a>
         */
        Deprecated,
        /**
         * The name for {@code RuntimeVisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.16">
         * VM Spec: The RuntimeVisibleAnnotations Attribute
         * </a>
         */
        RuntimeVisibleAnnotations,
        /**
         * The name for {@code RuntimeInvisibleAnnotations } attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.17">
         * VM Spec: The RuntimeInvisibleAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleAnnotations,
        /**
         * The name for {@code RuntimeVisibleParameterAnnotations } attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.18">
         * VM Spec: The RuntimeVisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeVisibleParameterAnnotations,
        /**
         * The name for {@code RuntimeInvisibleParameterAnnotations} attribute
         * type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.19">
         * VM Spec: The RuntimeInvisibleParameterAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleParameterAnnotations,
        /**
         * The name for {@code RuntimeVisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20">
         * VM Spec: The RuntimeVisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeVisibleTypeAnnotations,
        /**
         * The name for {@code RuntimeInvisibleTypeAnnotations} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.21">
         * VM Spec: The RuntimeInvisibleTypeAnnotations Attribute
         * </a>
         */
        RuntimeInvisibleTypeAnnotations,
        /**
         * The name for {@code AnnotationDefault} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.22">
         * VM Spec: The AnnotationDefault Attribute
         * </a>
         */
        AnnotationDefault,
        /**
         * The name for {@code BootstrapMethods} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.23">
         * VM Spec: The BootstrapMethods Attribute
         * </a>
         */
        BootstrapMethods,
        /**
         * The name for {@code MethodParameters} attribute type.
         *
         * @see
         * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.24">
         * VM Spec: The MethodParameters Attribute
         * </a>
         */
        MethodParameters;
    }
}
