/*
 * WeaverVersion_attribute.java    19:08, August 14, 2021
 *
 * Copyright  2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute.aspectj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.attribute.attribute_info;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code org.aspectj.weaver.AjSynthetic} attribute.
 *
 * The attribute has the following format:
 *
 * <pre>
 *    WeaverVersion_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * The following classes has this attribute:
 * <pre>
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/beans/factory/aspectj/AbstractDependencyInjectionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/beans/factory/aspectj/AbstractInterfaceDrivenDependencyInjectionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/beans/factory/aspectj/AnnotationBeanConfigurerAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/beans/factory/aspectj/GenericInterfaceDrivenDependencyInjectionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/cache/aspectj/AbstractCacheAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/cache/aspectj/AnnotationCacheAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/cache/aspectj/JCacheCacheAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/scheduling/aspectj/AbstractAsyncExecutionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/scheduling/aspectj/AnnotationAsyncExecutionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/transaction/aspectj/AbstractTransactionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/transaction/aspectj/AnnotationTransactionAspect.class
 *   spring-aspects-5.2.8.RELEASE.jar/org/springframework/transaction/aspectj/JtaAnnotationTransactionAspect.class
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://github.com/eclipse/org.aspectj/blob/V1_9_7/org.aspectj.matcher/src/main/java/org/aspectj/weaver/AjAttribute.java">AjAttribute</a>
 * @see
 * <a href="https://github.com/eclipse/org.aspectj/blob/V1_9_7/org.aspectj.matcher/src/main/java/org/aspectj/weaver/AjAttribute.java#L152">AjSynthetic</a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from Source Code instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class AjSynthetic_attribute extends attribute_info {

    public static final int LENGTH = 0;
    public static final String FULLNAME = "org.aspectj.weaver.AjSynthetic";

    public AjSynthetic_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != LENGTH) {
            throw new FileFormatException(String.format("The attribute_length of %s is not %d, it is %d.", FULLNAME, LENGTH, this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        // Nothing is needed
    }
}
