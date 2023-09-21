package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u1;
import org.binaryinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleParameterAnnotations Attribute.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from JVM spec instead
 * java:S101 - Class names should comply with a naming convention  --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention  --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116"})
public abstract class RuntimeParameterAnnotations extends attribute_info {

    public final u1 num_parameters;
    public final parameter_annotations[] parameter_annotations;

    RuntimeParameterAnnotations(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.num_parameters = new u1(posDataInputStream);
        if (this.num_parameters.value > 0) {
            this.parameter_annotations = new parameter_annotations[this.num_parameters.value];
            for (int i = 0; i < this.num_parameters.value; i++) {
                this.parameter_annotations[i] = new parameter_annotations(posDataInputStream);
            }
        } else {
            this.parameter_annotations = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        int startPosMoving = super.startPos;

        this.addNode(parentNode,
                startPosMoving + 6, 1,
                "num_parameters", this.num_parameters.value,
                this.getMessageKey_4_num_parameters(), Icons.Counter
        );

        if (this.parameter_annotations != null && this.parameter_annotations.length > 0) {
            DefaultMutableTreeNode parameterAnnotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 7,
                    this.getLength() - 7,
                    String.format("parameter_annotations [%d]", this.parameter_annotations.length),
                    MESSAGES.getString(this.getMessageKey_4_parameter_annotations())
            ));
            parentNode.add(parameterAnnotationsNode);

            for (int i = 0; i < this.parameter_annotations.length; i++) {
                DefaultMutableTreeNode parameterAnnotation = this.addNode(parameterAnnotationsNode,
                        this.parameter_annotations[i].getStartPos(),
                        this.parameter_annotations[i].getLength(),
                        String.valueOf(i + 1),
                        "parameter_annotation",
                        this.getMessageKey_4_parameter_annotations(),
                        Icons.Annotations
                );
                this.parameter_annotations[i].generateTreeNode(parameterAnnotation, fileFormat);
            }
        }
    }

    /**
     * Get message key for {@link #num_parameters}.
     *
     * @return Message key for {@link #num_parameters}
     *
     * @see RuntimeVisibleParameterAnnotations_attribute
     * @see RuntimeInvisibleParameterAnnotations_attribute
     */
    abstract String getMessageKey_4_num_parameters();

    /**
     * Get message key for {@link #parameter_annotations}.
     *
     * @return Message key for {@link #parameter_annotations}
     *
     * @see RuntimeVisibleParameterAnnotations_attribute
     * @see RuntimeInvisibleParameterAnnotations_attribute
     */
    abstract String getMessageKey_4_parameter_annotations();

    /**
     * Get message key for {@link parameter_annotations#num_annotations}.
     *
     * @return Message key for {@link parameter_annotations#num_annotations}
     *
     * @see RuntimeVisibleParameterAnnotations_attribute
     * @see RuntimeInvisibleParameterAnnotations_attribute
     */
    abstract String getMessageKey_4_parameter_annotations__num_annotations();

    /**
     * Get message key for {@link parameter_annotations#annotations}0.
     *
     * @return Message key for {@link parameter_annotations#annotations}
     *
     * @see RuntimeVisibleParameterAnnotations_attribute
     * @see RuntimeInvisibleParameterAnnotations_attribute
     */
    abstract String getMessageKey_4_parameter_annotations__annotations();

    public class parameter_annotations extends FileComponent implements GenerateTreeNodeClassFile {

        public final u2 num_annotations;
        public final Annotation[] annotations;

        private parameter_annotations(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            this.startPos = posDataInputStream.getPos();

            this.num_annotations = new u2(posDataInputStream);
            if (this.num_annotations.value > 0) {
                this.annotations = new Annotation[this.num_annotations.value];
                for (int i = 0; i < this.num_annotations.value; i++) {
                    this.annotations[i] = new Annotation(posDataInputStream);
                }
            } else {
                this.annotations = null;
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "num_annotations", RuntimeParameterAnnotations.this.getMessageKey_4_parameter_annotations__num_annotations(),
                    "msg_version", Icons.Counter
            );

            if (this.annotations != null && this.annotations.length > 0) {
                DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + 2,
                        this.getLength() - 2,
                        String.format("annotations [%d]", this.annotations.length),
                        MESSAGES.getString(RuntimeParameterAnnotations.this.getMessageKey_4_parameter_annotations__annotations())
                ));
                parentNode.add(annotationsNode);

                for (int i = 0; i < this.annotations.length; i++) {
                    DefaultMutableTreeNode annotationNode = this.addNode(annotationsNode,
                            this.annotations[i].getStartPos(),
                            this.annotations[i].getLength(),
                            String.valueOf(i + 1),
                            classFile.getCPDescription(this.annotations[i].type_index.value),
                            RuntimeParameterAnnotations.this.getMessageKey_4_parameter_annotations__annotations(),
                            Icons.Annotations
                    );
                    this.annotations[i].generateTreeNode(annotationNode, fileFormat);
                }
            }
        } // End generateTreeNode()
    }
}
