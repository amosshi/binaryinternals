package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleParameterAnnotations Attribute.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public abstract class RuntimeParameterAnnotations_attribute extends attribute_info {

    public final u1 num_parameters;
    public final parameter_annotations[] parameter_annotations;

    RuntimeParameterAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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

    // 4.7.18. The RuntimeVisibleParameterAnnotations Attribute
    // 4.7.19. The RuntimeInvisibleParameterAnnotations Attribute
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                1,
                "num_parameters: " + this.num_parameters.value
        )));

        if (this.parameter_annotations != null && this.parameter_annotations.length > 0) {
            DefaultMutableTreeNode parameterAnnotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 7,
                    this.getLength() - 7,
                    "parameter_annotations"
            ));
            parentNode.add(parameterAnnotationsNode);

            for (int i = 0; i < this.parameter_annotations.length; i++) {
                DefaultMutableTreeNode parameterAnnotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.parameter_annotations[i].getStartPos(),
                        this.parameter_annotations[i].getLength(),
                        String.format("parameter_annotation %d", i + 1)
                ));
                parameterAnnotationsNode.add(parameterAnnotation);
                this.parameter_annotations[i].generateTreeNode(parentNode, classFile);
            }
        }
    }

    public static class parameter_annotations extends FileComponent implements GenerateTreeNodeClassFile {

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

        /**
         * Get the value of {@code annotations}[{@code index}].
         *
         * @param index Index of the annotation
         * @return The value of {@code annotations}[{@code index}]
         */
        public Annotation getAnnotation(final int index) {
            Annotation a = null;
            if (this.annotations != null && index < this.annotations.length) {
                a = this.annotations[index];
            }
            return a;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            int startPosMoving = this.getStartPos();
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    2,
                    "num_annotations: " + this.num_annotations.value
            )));

            if (this.annotations != null && this.annotations.length > 0) {
                DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + 2,
                        this.getLength() - 2,
                        "annotations[" + this.annotations.length + "]"
                ));
                parentNode.add(annotationsNode);

                for (int i = 0; i < this.annotations.length; i++) {
                    DefaultMutableTreeNode annotationNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            this.annotations[i].getStartPos(),
                            this.annotations[i].getLength(),
                            String.format("annotation %d", i + 1)
                    ));
                    annotationsNode.add(annotationNode);
                    this.annotations[i].generateTreeNode(annotationNode, fileFormat);
                }
            }
        }
    }
}
