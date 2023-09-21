/*
 * WeaverVersion_attribute.java    19:08, August 14, 2021
 *
 * Copyright  2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute.scala;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.attribute.attribute_info;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code org.aspectj.weaver.ScalaSig} attribute.
 *
 * The attribute has the following format:
 *
 * <pre>
 *    ScalaSig_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        byte major_version;
 *        byte minor_version;
 *        byte version_time;
 *    }
 * </pre>
 *
 * The following classes has this attribute:
 * <pre>
 *   spring-framework-5.2.8.RELEASE/libs/spring-aspects-5.2.8.RELEASE.jar/org/springframework/beans/factory/aspectj/AbstractDependencyInjectionAspect.class
 *   spring-framework-5.2.8.RELEASE/libs/spring-aspects-5.2.8.RELEASE.jar/org/springframework/cache/aspectj/AbstractCacheAspect.class
 *   spring-framework-5.2.8.RELEASE/libs/spring-aspects-5.2.8.RELEASE.jar/org/springframework/cache/aspectj/JCacheCacheAspect.class
 *   spring-framework-5.2.8.RELEASE/libs/spring-aspects-5.2.8.RELEASE.jar/org/springframework/scheduling/aspectj/AbstractAsyncExecutionAspect.class
 *   spring-framework-5.2.8.RELEASE/libs/spring-aspects-5.2.8.RELEASE.jar/org/springframework/transaction/aspectj/AbstractTransactionAspect.class
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://www.scala-lang.org/old/sid/10">SID # 10 (draft) - Storage of
 * pickled Scala signatures in class file</a>
 * @see
 * <a href="https://www.scala-lang.org/old/sites/default/files/sids/dubochet/Mon,%202010-05-31,%2015:25/Storage%20of%20pickled%20Scala%20signatures%20in%20class%20files.pdf">Storage
 * of pickled Scala signatures in class files</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class ScalaSig_attribute extends attribute_info {

    public static final int LENGTH = 3;
    public static final String FULLNAME = "org.aspectj.weaver.ScalaSig";

    /**
     * The major version number.
     */
    public final int major_version;
    /**
     * The minor version number.
     */
    public final int minor_version;
    /**
     * The number of entries, which is always equal to <code>0</code> (as the
     * actual entries are defined in the <code>ScalaSignature</code>
     * annotation).
     */
    public final int number_of_entries;

    public ScalaSig_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != LENGTH) {
            throw new FileFormatException(String.format("The attribute_length of %s is not %d, it is %d.", FULLNAME, LENGTH, this.attribute_length.value));
        }
        this.major_version = posDataInputStream.readByte();
        this.minor_version = posDataInputStream.readByte();
        this.number_of_entries = posDataInputStream.readByte();

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int floatPos = super.startPos + 6;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_BYTE,
                "major_version: " + this.major_version,
                Icons.Versions
        )));
        floatPos += PosDataInputStream.LENGTH_BYTE;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_BYTE,
                "minor_version: " + this.minor_version,
                Icons.Versions
        )));
        floatPos += PosDataInputStream.LENGTH_BYTE;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_BYTE,
                "number_of_entries: " + this.number_of_entries,
                Icons.Counter
        )));
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_NoneJVM";
    }
}
