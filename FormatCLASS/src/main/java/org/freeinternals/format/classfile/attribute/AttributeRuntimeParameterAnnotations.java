package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u1;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleParameterAnnotations Attribute.
 *
 * @author Amos Shi
 */
public class AttributeRuntimeParameterAnnotations extends AttributeInfo {

    public transient final u1 num_parameters;
    public transient final ParameterAnnotation[] parameter_annotations;

    AttributeRuntimeParameterAnnotations(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream, ClassFile.Version format, JavaSEVersion javaSE) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, format, javaSE);

        this.num_parameters = new u1(posDataInputStream);
        if (this.num_parameters.value > 0) {
            this.parameter_annotations = new ParameterAnnotation[this.num_parameters.value];
            for (int i = 0; i < this.num_parameters.value; i++) {
                this.parameter_annotations[i] = new ParameterAnnotation(posDataInputStream);
            }
        } else {
            this.parameter_annotations = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    // 4.7.18. The RuntimeVisibleParameterAnnotations Attribute
    // 4.7.19. The RuntimeInvisibleParameterAnnotations Attribute
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        int startPosMoving = super.startPos;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                1,
                "num_parameters: " + this.num_parameters.value
        )));

        if (this.parameter_annotations != null && this.parameter_annotations.length > 0) {
            DefaultMutableTreeNode parameter_annotations_node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 7,
                    this.getLength() - 7,
                    "parameter_annotations"
            ));
            parentNode.add(parameter_annotations_node);

            for (int i = 0; i < this.parameter_annotations.length; i++) {
                DefaultMutableTreeNode parameter_annotation = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.parameter_annotations[i].getStartPos(),
                        this.parameter_annotations[i].getLength(),
                        String.format("parameter_annotation %d", i + 1)
                ));
                parameter_annotations_node.add(parameter_annotation);
                this.generateSubnode(parameter_annotation, this.parameter_annotations[i], classFile);
            }
        }
    }
    

    // 4.7.18, 4.7.19:  The RuntimeParameterAnnotations Attribute
    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeRuntimeParameterAnnotations.ParameterAnnotation pa, ClassFile classFile) {

        int startPosMoving = pa.getStartPos();
        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                2,
                "num_annotations: " + pa.num_annotations.value
        )));

        if (pa.annotations != null && pa.annotations.length > 0) {
            DefaultMutableTreeNode annotations = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 2,
                    pa.getLength() - 2,
                    "annotations[" + pa.annotations.length + "]"
            ));
            rootNode.add(annotations);

            for (int i = 0; i < pa.annotations.length; i++) {
                DefaultMutableTreeNode annotationNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pa.annotations[i].getStartPos(),
                        pa.annotations[i].getLength(),
                        String.format("annotation %d", i + 1)
                ));
                annotations.add(annotationNode);
                Annotation.generateSubnode(annotationNode, pa.annotations[i], classFile);
            }
        }
    }
    

    public static class ParameterAnnotation extends FileComponent {

        public transient final u2 num_annotations;
        public transient final Annotation[] annotations;

        private ParameterAnnotation(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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
    }
}
