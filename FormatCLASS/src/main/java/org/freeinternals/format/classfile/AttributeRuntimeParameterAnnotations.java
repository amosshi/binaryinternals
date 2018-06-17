package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * The Runtime(In)VisibleParameterAnnotations Attribute.
 * 
 * @author Amos Shi
 */
public class AttributeRuntimeParameterAnnotations extends AttributeInfo {

    public transient final u1 num_parameters;
    public transient final ParameterAnnotation[] parameter_annotations;

    AttributeRuntimeParameterAnnotations(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream)
            throws IOException, FileFormatException {

        super(nameIndex, type, posDataInputStream);

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

    public static class ParameterAnnotation extends FileComponent {

        public transient final u2 num_annotations;
        public transient final Annotation[] annotations;

        private ParameterAnnotation(final PosDataInputStream posDataInputStream)
                throws IOException, FileFormatException {
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
