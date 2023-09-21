/*
 * WeaverVersion_attribute.java    19:08, August 14, 2021
 *
 * Copyright  2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute.aspectj;

import java.io.IOException;
import java.time.Instant;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.attribute.attribute_info;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code org.aspectj.weaver.WeaverVersion} attribute.
 *
 * The attribute has the following format:
 *
 * <pre>
 *    WeaverVersion_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        short major_version;
 *        short minor_version;
 *        long version_time;
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
 * <a href="https://github.com/eclipse/org.aspectj/blob/V1_9_7/org.aspectj.matcher/src/main/java/org/aspectj/weaver/AjAttribute.java">AjAttribute</a>
 * @see
 * <a href="https://github.com/eclipse/org.aspectj/blob/V1_9_7/org.aspectj.matcher/src/main/java/org/aspectj/weaver/AjAttribute.java#L293">WeaverVersionInfo.write()</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class WeaverVersion_attribute extends attribute_info {

    public static final int LENGTH = 12;
    public static final String FULLNAME = "org.aspectj.weaver.WeaverVersion";

    public final short major_version;
    public final short minor_version;
    public final long version_time;

    public WeaverVersion_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != LENGTH) {
            throw new FileFormatException(String.format("The attribute_length of %s is not %d, it is %d.", FULLNAME, LENGTH, this.attribute_length.value));
        }
        this.major_version = posDataInputStream.readShort();
        this.minor_version = posDataInputStream.readShort();
        this.version_time = posDataInputStream.readLong();

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int floatPos = super.startPos + 6;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_SHORT,
                "major_version: " + this.major_version
        )));
        floatPos += PosDataInputStream.LENGTH_SHORT;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_SHORT,
                "minor_version: " + this.minor_version
        )));
        floatPos += PosDataInputStream.LENGTH_SHORT;

        String verionTimeStr = (this.version_time != -1) ? Instant.ofEpochMilli(this.version_time).toString() : "";
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                PosDataInputStream.LENGTH_LONG,
                String.format("minor_version: %d %s", this.version_time, verionTimeStr)
        )));
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_NoneJVM";
    }
}
