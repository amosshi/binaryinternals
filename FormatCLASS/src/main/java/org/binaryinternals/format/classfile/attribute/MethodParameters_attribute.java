/*
 * AttributeMethodParameters.java    5:37 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.AccessFlag;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u1;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code MethodParameters} attribute. The
 * {@code MethodParameters} attribute has the following format:
 *
 * Parameter names aren’t compiled into the byte code by default. Hence we have
 * to add a compiler switch to enable named parameters:
 * <pre>
 *   javac -parameters
 * </pre>
 *
 * The MethodParameters attribute has the following format:
 * <pre>
 *    MethodParameters_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u1 parameters_count;
 *        {   u2 name_index;
 *            u2 access_flags;
 *        } parameters[parameters_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 8
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.24">
 * VM Spec: The MethodParameters Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class MethodParameters_attribute extends attribute_info {

    /**
     * The value of the parameters_count item indicates the number of parameter
     * descriptors in the method descriptor referenced by the descriptor_index
     * of the attribute's enclosing method_info structure.
     */
    public final u1 parameters_count;
    public final Parameter[] parameters;

    MethodParameters_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.parameters_count = new u1(posDataInputStream, true);
        if (this.parameters_count.value > 0) {
            this.parameters = new Parameter[this.parameters_count.value];
            for (int i = 0; i < this.parameters_count.value; i++) {
                this.parameters[i] = new Parameter(posDataInputStream);
            }
        } else {
            this.parameters = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos + 6;

        this.addNode(parentNode,
                startPosMoving, u1.LENGTH,
                "parameters_count",
                this.parameters_count.value,
                "msg_attr_MethodParameters__parameters_count",
                Icons.Counter
        );
        startPosMoving += u1.LENGTH;

        if (this.parameters == null || this.parameters.length < 1) {
            return;
        }

        DefaultMutableTreeNode parametersNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                this.parameters.length * Parameter.LENGTH,
                String.format("parameters [%d]", this.parameters.length),
                MESSAGES.getString("msg_attr_MethodParameters__parameters_count")
        ));
        parentNode.add(parametersNode);

        for (int i = 0; i < this.parameters.length; i++) {
            DefaultMutableTreeNode parameterNode = this.addNode(parametersNode,
                    startPosMoving,
                    Parameter.LENGTH,
                    String.format("paramter %d", i + 1),
                    ((ClassFile) classFile).getCPDescription(this.parameters[i].name_index.value),
                    "msg_attr_parameters",
                    Icons.Parameter
            );
            this.parameters[i].generateTreeNode(parameterNode, classFile);
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_MethodParameters";
    }

    public static final class Parameter extends FileComponent implements GenerateTreeNodeClassFile {

        public static final int LENGTH = u2.LENGTH + u2.LENGTH;

        /**
         * The value of the name_index item must either be zero or a valid index
         * into the constant_pool table. If the value of the name_index item is
         * zero, then this parameters element indicates a formal parameter with
         * no name. If the value of the name_index item is nonzero, the
         * constant_pool entry at that index must be a CONSTANT_Utf8_info
         * structure representing a valid unqualified name denoting a formal
         * parameter.
         */
        public final u2 name_index;
        /**
         * The value of the access_flags item is as follows: 0x0010 (ACC_FINAL),
         * 0x1000 (ACC_SYNTHETIC), 0x8000 (ACC_MANDATED).
         *
         * @see AccessFlag#ForMethodParameters
         */
        public final u2 access_flags;

        private Parameter(final PosDataInputStream posDataInputStream) throws IOException {
            super.startPos = posDataInputStream.getPos();
            this.name_index = new u2(posDataInputStream);
            this.access_flags = new u2(posDataInputStream);
            super.length = posDataInputStream.getPos() - super.startPos;
        }

        public String getAccessFlagsText() {
            return AccessFlag.getMethodParametersModifier(this.access_flags.value);
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int nameCpIndex = this.name_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "name_index",
                    String.format(TEXT_CPINDEX_VALUE, nameCpIndex, "name", classFile.getCPDescription(nameCpIndex)),
                    "msg_attr_parameters__name_index",
                    Icons.Name
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "access_flags",
                    BytesTool.getBinaryString(this.access_flags.value) + " - " + this.getAccessFlagsText(),
                    "msg_attr_parameters__access_flags",
                    Icons.AccessFlag
            );
        }
    }
}
