/*
 * Scala_attribute.java    19:06, August 14, 2021
 *
 * Copyright  2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute.scala;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.attribute.attribute_info;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code Scala} attribute.
 * 
 * We found this attribute in Scala generated .class files, like:
 * <pre>
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/AssemblyBuilderFactory$.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILGenerator$$anonfun$Emit$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILGenerator$.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILGenerator$ExceptionStack.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anon$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$addPrimitive$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$caseILGenerator$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$caseOpCode$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$caseTypeBuilder$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$hasControlChars$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$pad$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printAssemblyBoilerplate$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printAttributes$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printHeader$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printSignature$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printSignature$2.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printTypeParams$1$$anonfun$apply$mcVI$sp$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$$anonfun$printTypeParams$1.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/ILPrinterVisitor$.class
 *   scala-2.9.3/lib/scala-compiler.jar/ch/epfl/lamp/compiler/msil/emit/Label$.class
 * </pre>
 * 
 * Per current findings, this attribute's length is always <code>0</code>.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Scala_attribute extends attribute_info {

    public Scala_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 0) {
            throw new FileFormatException(String.format("The attribute_length of Scala is not 0, it is %d.", this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        // Nothing to add
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_NoneJVM";
    }
}
