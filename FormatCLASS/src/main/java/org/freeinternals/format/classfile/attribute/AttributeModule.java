/*
 * AttributeModule.java    May 17, 2019
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.AccessFlag;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.25">
 * VM Spec: The Module Attribute</a>
 */
public class AttributeModule extends AttributeInfo {
    private final String MESSAGE_ETI = "exports_to_index[";

    public final u2 module_name_index;
    public final u2 module_flags;
    public final u2 module_version_index;

    public final u2 requires_count;
    public final Requires[] requires;

    public final u2 exports_count;
    public final Exports[] exports;

    public final u2 opens_count;
    public final Opens[] opens;

    public final u2 uses_count;
    public final u2[] uses_index;

    public final u2 provides_count;
    public final Provides[] provides;

    @SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high --> No, it is not high
    AttributeModule(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.FORMAT_53_0, JavaSEVersion.VERSION_9);

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
     * Get extracted {@link #module_flags}.
     *
     * @return Extracted {@link #module_flags}
     */
    public String getModuleFlags() {
        return AccessFlag.getModifier(this.module_flags.value, AccessFlag.ForModule);
    }

    @Override
    @SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high --> No, it is not high
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final ClassFile classFile) {

        int startPosMoving = super.startPos + 6;

        // module_name_index
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "module_name_index: " + this.module_name_index.value + " - " + classFile.getCPDescription(this.module_name_index.value)
        )));
        startPosMoving += u2.LENGTH;

        // module_flags
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "module_flags: " + this.module_flags.value + " " + this.getModuleFlags()
        )));
        startPosMoving += u2.LENGTH;

        // module_version_index
        String module_version = (this.module_version_index.value == 0)
                ? "no version information"
                : classFile.getCPDescription(this.module_version_index.value);

        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "module_version_index: " + this.module_version_index.value + " - " + module_version
        )));
        startPosMoving += u2.LENGTH;

        //
        // requires
        //
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "requires_count: " + this.requires_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.requires_count.value > 0) {
            final DefaultMutableTreeNode requiresNode = new DefaultMutableTreeNode(
                    new JTreeNodeFileComponent(
                            startPosMoving,
                            AttributeModule.Requires.LENGTH * this.requires_count.value,
                            "requires[" + this.requires_count.value + "]"
                    ));
            parentNode.add(requiresNode);

            DefaultMutableTreeNode requireNode;
            for (int i = 0; i < this.requires.length; i++) {
                requireNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.requires[i].getStartPos(),
                        this.requires[i].getLength(),
                        "require [" + i + "]"
                ));
                this.generateSubnode(requireNode, this.requires[i], classFile);
                requiresNode.add(requireNode);
            }

            // Update the new startPos
            AttributeModule.Requires requireLastItem = this.requires[this.requires.length - 1];
            startPosMoving = requireLastItem.getStartPos() + requireLastItem.getLength();
        }

        //
        // exports
        //
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "exports_count: " + this.exports_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.exports_count.value > 0) {
            AttributeModule.Exports exporstLastItem = this.exports[this.exports_count.value - 1];
            final DefaultMutableTreeNode exportsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    exporstLastItem.getStartPos() + exporstLastItem.getLength() - startPosMoving,
                    "exports[" + this.exports_count.value + "]"
            ));
            parentNode.add(exportsNode);

            for (int i = 0; i < this.exports.length; i++) {
                DefaultMutableTreeNode exportNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.exports[i].getStartPos(),
                        this.exports[i].getLength(),
                        "export [" + i + "]"
                ));
                this.generateSubnode(exportNode, this.exports[i], classFile);
                exportsNode.add(exportNode);
            }

            // Update the new startPos
            startPosMoving = exporstLastItem.getStartPos() + exporstLastItem.getLength();
        }

        //
        // opens
        //
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "opens_count: " + this.opens_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.opens_count.value > 0) {
            AttributeModule.Opens opensLastItem = this.opens[this.opens_count.value - 1];
            final DefaultMutableTreeNode opensNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    opensLastItem.getStartPos() + opensLastItem.getLength() - startPosMoving,
                    "opens[" + this.opens_count.value + "]"
            ));
            parentNode.add(opensNode);

            for (int i = 0; i < this.opens.length; i++) {
                DefaultMutableTreeNode openNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.opens[i].getStartPos(),
                        this.opens[i].getLength(),
                        "open [" + i + "]"
                ));
                this.generateSubnode(openNode, this.opens[i], classFile);
                opensNode.add(openNode);
            }

            // Update the new startPos
            startPosMoving = opensLastItem.getStartPos() + opensLastItem.getLength();
        }

        //
        // uses
        //
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "uses_count: " + this.uses_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.uses_count.value > 0) {
            final int uses_count_length = u2.LENGTH * this.uses_count.value;
            final DefaultMutableTreeNode usesCountNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    uses_count_length,
                    "uses_index[" + this.uses_count.value + "]"
            ));
            parentNode.add(usesCountNode);

            for (int i = 0; i < this.uses_index.length; i++) {
                usesCountNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        "uses_index [" + i + "]: " + this.uses_index[i].value + " - " + classFile.getCPDescription(this.uses_index[i].value)
                )));
            }

            startPosMoving += uses_count_length;
        }

        //
        // provides
        //
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "provides_count: " + this.provides_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (this.provides_count.value > 0) {
            AttributeModule.Provides provideLastItem = this.provides[this.provides_count.value - 1];
            final DefaultMutableTreeNode providesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    provideLastItem.getStartPos() + provideLastItem.getLength() - startPosMoving,
                    "provides[" + this.provides_count.value + "]"
            ));
            parentNode.add(providesNode);

            for (int i = 0; i < this.provides.length; i++) {
                DefaultMutableTreeNode provideNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        this.provides[i].getStartPos(),
                        this.provides[i].getLength(),
                        "provide [" + i + "]"
                ));
                this.generateSubnode(provideNode, this.provides[i], classFile);
                providesNode.add(provideNode);
            }
        }
    }

    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeModule.Provides provide, final ClassFile classFile) {
        int startPosMoving = provide.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "provides_index: " + provide.provides_index.value + " - " + classFile.getCPDescription(provide.provides_index.value)
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "provides_with_count: " + provide.provides_with_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (provide.provides_with_count.value > 0) {
            final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * provide.provides_with_count.value,
                    MESSAGE_ETI + provide.provides_with_count.value + "]"
            ));
            rootNode.add(exportsToIndexNode);

            for (int i = 0; i < provide.provides_with_index.length; i++) {
                exportsToIndexNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        MESSAGE_ETI + i + "]: " + provide.provides_with_index[i].value + " - " + classFile.getCPDescription(provide.provides_with_index[i].value)
                )));
            }
        }
    }

    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeModule.Opens open, final ClassFile classFile) {
        int startPosMoving = open.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "opens_index: " + open.opens_index.value + " - " + classFile.getCPDescription(open.opens_index.value)
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "opens_flags: " + open.opens_flags.value + " " + open.getOpenFlags()
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "opens_to_count: " + open.opens_to_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (open.opens_to_count.value > 0) {
            final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * open.opens_to_count.value,
                    MESSAGE_ETI + open.opens_to_count.value + "]"
            ));
            rootNode.add(exportsToIndexNode);

            for (int i = 0; i < open.opens_to_index.length; i++) {
                exportsToIndexNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        MESSAGE_ETI + i + "]: " + open.opens_to_index[i].value + " - " + classFile.getCPDescription(open.opens_to_index[i].value)
                )));
            }
        }
    }

    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeModule.Exports export, final ClassFile classFile) {
        int startPosMoving = export.getStartPos();

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "exports_index: " + export.exports_index.value + " - " + classFile.getCPDescription(export.exports_index.value)
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "exports_flags: " + export.exports_flags.value + " " + export.getExportFlags()
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "exports_to_count: " + export.exports_to_count.value
        )));
        startPosMoving += u2.LENGTH;

        if (export.exports_to_count.value > 0) {
            final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    u2.LENGTH * export.exports_to_count.value,
                    MESSAGE_ETI + export.exports_to_count.value + "]"
            ));
            rootNode.add(exportsToIndexNode);

            for (int i = 0; i < export.exports_to_index.length; i++) {
                exportsToIndexNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        MESSAGE_ETI + i + "]: " + export.exports_to_index[i].value + " - " + classFile.getCPDescription(export.exports_to_index[i].value)
                )));
            }
        }
    }

    private void generateSubnode(final DefaultMutableTreeNode rootNode, final AttributeModule.Requires require, final ClassFile classFile) {
        int startPosMoving = require.getStartPos();

        // requires_version_index
        String requires_version_index = (require.requires_version_index.value == 0)
                ? "no version information"
                : classFile.getCPDescription(require.requires_version_index.value);

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "requires_index: " + require.requires_index.value + " - " + classFile.getCPDescription(require.requires_index.value)
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "requires_flags: " + require.requires_flags.value + " " + require.getRequiresFlags()
        )));
        startPosMoving += u2.LENGTH;

        rootNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                startPosMoving,
                u2.LENGTH,
                "requires_version_index: " + require.requires_version_index.value + " - " + requires_version_index
        )));
    }

    /**
     * The {@code requires} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final static class Requires extends FileComponent {

        public static final int LENGTH = 6;
        public final u2 requires_index;
        public final u2 requires_flags;
        public final u2 requires_version_index;

        private Requires(final PosDataInputStream posDataInputStream)
                throws IOException {
            this.startPos = posDataInputStream.getPos();
            this.length = LENGTH;

            this.requires_index = new u2(posDataInputStream);
            this.requires_flags = new u2(posDataInputStream);
            this.requires_version_index = new u2(posDataInputStream);
        }

        /**
         * Get extracted {@link #requires_flags}.
         *
         * @return Extracted {@link #requires_flags}
         */
        public String getRequiresFlags() {
            return AccessFlag.getModifier(this.requires_flags.value, AccessFlag.ForModuleRequires);
        }
    }

    /**
     * The {@code exports} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final static class Exports extends FileComponent {

        public final u2 exports_index;
        public final u2 exports_flags;
        public final u2 exports_to_count;
        public final u2[] exports_to_index;

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

        /**
         * Get extracted {@link #exports_flags}.
         *
         * @return Extracted {@link #exports_flags}
         */
        public String getExportFlags() {
            return AccessFlag.getModifier(this.exports_flags.value, AccessFlag.ForModuleExports);
        }
    }

    /**
     * The {@code opens} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final static class Opens extends FileComponent {

        public final u2 opens_index;
        public final u2 opens_flags;
        public final u2 opens_to_count;
        public final u2[] opens_to_index;

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

        /**
         * Get extracted {@link #opens_flags}.
         *
         * @return Extracted {@link #opens_flags}
         */
        public String getOpenFlags() {
            return AccessFlag.getModifier(this.opens_flags.value, AccessFlag.ForModuleOpens);
        }
    }

    /**
     * The {@code provides} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public final static class Provides extends FileComponent {

        public final u2 provides_index;
        public final u2 provides_with_count;
        public final u2[] provides_with_index;

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
