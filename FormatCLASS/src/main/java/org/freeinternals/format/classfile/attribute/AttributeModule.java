/*
 * AttributeModule.java    May 17, 2019
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code Module} attribute.
 *
 * The {@code Module} attribute is a variable-length attribute in the attributes
 * table of a {@code ClassFile} structure. The {@code Module} attribute
 * indicates the modules required by a module; the packages exported and opened
 * by a module; and the services used and provided by a module.
 *
 * The {@code Module} attribute has the following format:
 *
 * <pre>
 * Module_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *
 *     u2 module_name_index;
 *     u2 module_flags;
 *     u2 module_version_index;
 *
 *     u2 requires_count;
 *     {   u2 requires_index;
 *         u2 requires_flags;
 *         u2 requires_version_index;
 *     } requires[requires_count];
 *
 *     u2 exports_count;
 *     {   u2 exports_index;
 *         u2 exports_flags;
 *         u2 exports_to_count;
 *         u2 exports_to_index[exports_to_count];
 *     } exports[exports_count];
 *
 *     u2 opens_count;
 *     {   u2 opens_index;
 *         u2 opens_flags;
 *         u2 opens_to_count;
 *         u2 opens_to_index[opens_to_count];
 *     } opens[opens_count];
 *
 *     u2 uses_count;
 *     u2 uses_index[uses_count];
 *
 *     u2 provides_count;
 *     {   u2 provides_index;
 *         u2 provides_with_count;
 *         u2 provides_with_index[provides_with_count];
 *     } provides[provides_count];
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 9
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.25">VM
 * Spec: The Module Attribute</a>
 */
public class AttributeModule extends AttributeInfo {

    public transient final u2 module_name_index;
    public transient final u2 module_flags;
    public transient final u2 module_version_index;

    public transient final u2 requires_count;
    public final transient Requires[] requires;

    public transient final u2 exports_count;
    public final transient Exports[] exports;

    public transient final u2 opens_count;
    public final transient Opens[] opens;

    public transient final u2 uses_count;
    public final transient u2[] uses_index;

    public transient final u2 provides_count;
    public final transient Provides[] provides;

    AttributeModule(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.module_name_index = new u2(posDataInputStream);
        this.module_flags = new u2(posDataInputStream);
        this.module_version_index = new u2(posDataInputStream);

        // Requires
        this.requires_count = new u2(posDataInputStream);
        if (this.requires_count.value > 0) {
            this.requires = new Requires[this.requires_count.value];
            for (int i = 0; i < this.requires_count.value; i++) {
                this.requires[i] = new Requires(posDataInputStream);
            }
        } else {
            this.requires = null;
        }

        // Exports
        this.exports_count = new u2(posDataInputStream);
        if (this.exports_count.value > 0) {
            this.exports = new Exports[this.exports_count.value];
            for (int i = 0; i < this.exports_count.value; i++) {
                this.exports[i] = new Exports(posDataInputStream);
            }
        } else {
            this.exports = null;
        }

        // Opens
        this.opens_count = new u2(posDataInputStream);
        if (this.opens_count.value > 0) {
            this.opens = new Opens[this.opens_count.value];
            for (int i = 0; i < this.opens_count.value; i++) {
                this.opens[i] = new Opens(posDataInputStream);
            }
        } else {
            this.opens = null;
        }

        // Uses
        this.uses_count = new u2(posDataInputStream);
        if (this.uses_count.value > 0) {
            this.uses_index = new u2[this.uses_count.value];
            for (int i = 0; i < this.uses_count.value; i++) {
                this.uses_index[i] = new u2(posDataInputStream);
            }
        } else {
            this.uses_index = null;
        }

        // Provides
        this.provides_count = new u2(posDataInputStream);
        if (this.provides_count.value > 0) {
            this.provides = new Provides[this.provides_count.value];
            for (int i = 0; i < this.provides_count.value; i++) {
                this.provides[i] = new Provides(posDataInputStream);
            }
        } else {
            this.provides = null;
        }

        // Check Length        
        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * The {@code requires} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final class Requires extends FileComponent {

        public static final int LENGTH = 6;
        public transient final u2 requires_index;
        public transient final u2 requires_flags;
        public transient final u2 requires_version_index;

        private Requires(final PosDataInputStream posDataInputStream)
                throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.requires_index = new u2(posDataInputStream);
            this.requires_flags = new u2(posDataInputStream);
            this.requires_version_index = new u2(posDataInputStream);
        }
    }

    /**
     * The {@code exports} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final class Exports extends FileComponent {

        public transient final u2 exports_index;
        public transient final u2 exports_flags;
        public transient final u2 exports_to_count;
        public transient final u2[] exports_to_index;

        private Exports(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();

            this.exports_index = new u2(posDataInputStream);
            this.exports_flags = new u2(posDataInputStream);
            this.exports_to_count = new u2(posDataInputStream);
            if (this.exports_to_count.value > 0) {
                this.exports_to_index = new u2[this.exports_to_count.value];
                for (int i = 0; i < this.exports_to_count.value; i++) {
                    this.exports_to_index[i] = new u2(posDataInputStream);
                }
            } else {
                this.exports_to_index = null;
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }
    }

    /**
     * The {@code opens} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final class Opens extends FileComponent {

        public transient final u2 opens_index;
        public transient final u2 opens_flags;
        public transient final u2 opens_to_count;
        public transient final u2[] opens_to_index;

        private Opens(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();

            this.opens_index = new u2(posDataInputStream);
            this.opens_flags = new u2(posDataInputStream);
            this.opens_to_count = new u2(posDataInputStream);
            if (this.opens_to_count.value > 0) {
                this.opens_to_index = new u2[this.opens_to_count.value];
                for (int i = 0; i < this.opens_to_count.value; i++) {
                    this.opens_to_index[i] = new u2(posDataInputStream);
                }
            } else {
                this.opens_to_index = null;
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }
    }

    /**
     * The {@code provides} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final class Provides extends FileComponent {

        public transient final u2 provides_index;
        public transient final u2 provides_with_count;
        public transient final u2[] provides_with_index;

        private Provides(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();

            this.provides_index = new u2(posDataInputStream);
            this.provides_with_count = new u2(posDataInputStream);
            if (this.provides_with_count.value > 0) {
                this.provides_with_index = new u2[this.provides_with_count.value];
                for (int i = 0; i < this.provides_with_count.value; i++) {
                    this.provides_with_index[i] = new u2(posDataInputStream);
                }
            } else {
                this.provides_with_index = null;
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }
    }

}
