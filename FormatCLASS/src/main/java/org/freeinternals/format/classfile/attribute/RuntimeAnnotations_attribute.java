package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleAnnotations Attribute.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public abstract class RuntimeAnnotations_attribute extends attribute_info {

    public final u2 num_annotations;
    private final Annotation[] annotations;

    RuntimeAnnotations_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.num_annotations = new u2(posDataInputStream);
        if (this.num_annotations.value > 0) {
            this.annotations = new Annotation[this.num_annotations.value];
            for (int i = 0; i < this.num_annotations.value; i++) {
                this.annotations[i] = new Annotation(posDataInputStream);
            }
        } else {
            this.annotations = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * Get the value of {@code annotations}[{@code index}].
     *
     * @param index Index of the annotation
     * @return The value of {@code annotations}[{@code index}]
     */
    public Annotation getAnnotation(final int index) {
        Annotation a = null;
        if (this.annotations != null && this.annotations.length > index) {
            a = this.annotations[index];
        }
        return a;
    }

    // 4.7.16. The RuntimeVisibleAnnotations Attribute
    // 4.7.17. The RuntimeInvisibleAnnotations Attribute
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        ClassFile classFile = (ClassFile) format;
        final int startPosMoving = super.startPos;

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving + 6,
                2,
                "num_annotations: " + this.num_annotations.value)));

        if (this.num_annotations.value > 0) {
            DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    String.format("annotations[%d]", this.num_annotations.value)
            ));
            parentNode.add(annotationsNode);

            for (int i = 0; i < this.num_annotations.value; i++) {
                Annotation a = this.getAnnotation(i);
                DefaultMutableTreeNode annotationNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        a.getStartPos(),
                        a.getLength(),
                        String.format("annotation %d: %s", i + 1, classFile.getCPDescription(a.type_index.value))
                ));
                annotationsNode.add(annotationNode);
                Annotation.generateSubnode(annotationNode, a, classFile);
            }
        }
    }
}
