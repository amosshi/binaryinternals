package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class AttributeRuntimeAnnotations extends AttributeInfo {

    public transient final u2 num_annotations;
    private transient final Annotation[] annotations;

    AttributeRuntimeAnnotations(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream)
            throws java.io.IOException, FileFormatException {
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
}
