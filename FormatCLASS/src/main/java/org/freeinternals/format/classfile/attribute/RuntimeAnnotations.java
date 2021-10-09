package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The Runtime(In)VisibleAnnotations Attribute.
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
public abstract class RuntimeAnnotations extends attribute_info {

    public final u2 num_annotations;
    private final Annotation[] annotations;

    RuntimeAnnotations(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        final ClassFile classFile = (ClassFile) format;
        final int startPosMoving = super.startPos;

        this.addNode(parentNode,
                startPosMoving + 6, u2.LENGTH,
                "num_annotations", this.num_annotations.value,
                this.getMessageKey_4_num_annotations(), Icons.Counter
        );

        if (this.num_annotations.value > 0) {
            DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving + 8,
                    this.getLength() - 8,
                    String.format("annotations [%d]", this.num_annotations.value),
                    MESSAGES.getString(this.getMessageKey_4_annotations())
            ));
            parentNode.add(annotationsNode);

            for (int i = 0; i < this.num_annotations.value; i++) {
                Annotation a = this.annotations[i];
                DefaultMutableTreeNode annotationNode = this.addNode(annotationsNode,
                        a.getStartPos(),
                        a.getLength(),
                        String.valueOf(i + 1),
                        classFile.getCPDescription(a.type_index.value),
                        this.getMessageKey_4_annotations(),
                        Icons.Annotations
                );
                a.generateTreeNode(annotationNode, format);
            }
        }
    }

    /**
     * Get message key for {@link #annotations}.
     *
     * @return Message key for {@link #annotations}
     *
     * @see RuntimeVisibleAnnotations_attribute
     * @see RuntimeInvisibleAnnotations_attribute
     */
    abstract String getMessageKey_4_annotations();

    /**
     * Get message key for {@link #num_annotations}.
     *
     * @return Message key for {@link #num_annotations}
     *
     * @see RuntimeVisibleAnnotations_attribute
     * @see RuntimeInvisibleAnnotations_attribute
     */
    abstract String getMessageKey_4_num_annotations();
}
