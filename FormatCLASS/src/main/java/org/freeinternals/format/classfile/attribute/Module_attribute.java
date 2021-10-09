/*
 * AttributeModule.java    May 17, 2019
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.AccessFlag;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.GenerateTreeNodeClassFile;
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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.25">
 * VM Spec: The Module Attribute</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class Module_attribute extends attribute_info {

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

    /**
     * <pre>
     * java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
     * </pre>
     */
    @SuppressWarnings("java:S3776")
    Module_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
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
     * Get extracted {@link #module_flags}.
     *
     * @return Extracted {@link #module_flags}
     */
    public String getModuleFlags() {
        return AccessFlag.getModifier(this.module_flags.value, AccessFlag.ForModule);
    }

    /**
     * @param parentNode Parent JTree node
     * @param format Current class file object
     *
     * <pre>
     * java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
     * </pre>
     */
    @Override
    @SuppressWarnings("java:S3776")
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat format) {
        ClassFile classFile = (ClassFile) format;
        int startPosMoving = super.startPos + 6;

        // module_name_index
        int moduleNameCpIndex = this.module_name_index.value;
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "module_name_index",
                String.format(TEXT_CPINDEX_VALUE, moduleNameCpIndex, "module name", classFile.getCPDescription(moduleNameCpIndex)),
                "msg_attr_module_name_index",
                Icons.Module
        );
        startPosMoving += u2.LENGTH;

        // module_flags
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "module_flags",
                BytesTool.getBinaryString(this.module_flags.value) + " - " + this.getModuleFlags(),
                "msg_attr_module_flags",
                Icons.AccessFlag
        );
        startPosMoving += u2.LENGTH;

        // module_version_index
        String moduleVersion = (this.module_version_index.value == 0)
                ? "no version information"
                : classFile.getCPDescription(this.module_version_index.value);

        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "module_version_index",
                this.module_version_index.value + " - " + moduleVersion,
                "msg_attr_module_version_index",
                Icons.Versions
        );
        startPosMoving += u2.LENGTH;

        //
        // requires
        //
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "requires_count", this.requires_count.value,
                "msg_attr_requires_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.requires_count.value > 0) {
            final DefaultMutableTreeNode requiresNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    Module_attribute.Requires.LENGTH * this.requires_count.value,
                    String.format("requires [%d]", this.requires_count.value),
                    MESSAGES.getString("msg_attr_requires")
            ));
            parentNode.add(requiresNode);

            for (int i = 0; i < this.requires.length; i++) {
                DefaultMutableTreeNode requireNode = this.addNode(requiresNode,
                        this.requires[i].getStartPos(),
                        this.requires[i].getLength(),
                        String.format("requires %d", i + 1),
                        classFile.getCPDescription(this.requires[i].requires_index.value),
                        "msg_attr_requires",
                        Icons.Data
                );
                this.requires[i].generateTreeNode(requireNode, format);
            }

            // Update the new startPos
            Module_attribute.Requires requireLastItem = this.requires[this.requires.length - 1];
            startPosMoving = requireLastItem.getStartPos() + requireLastItem.getLength();
        }

        //
        // exports
        //
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "exports_count", this.exports_count.value,
                "msg_attr_exports_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.exports_count.value > 0) {
            Module_attribute.Exports exporstLastItem = this.exports[this.exports_count.value - 1];
            final DefaultMutableTreeNode exportsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    exporstLastItem.getStartPos() + exporstLastItem.getLength() - startPosMoving,
                    String.format("exports [%d]", this.exports_count.value),
                    MESSAGES.getString("msg_attr_exports")
            ));
            parentNode.add(exportsNode);

            for (int i = 0; i < this.exports.length; i++) {
                DefaultMutableTreeNode exportNode = this.addNode(exportsNode,
                        this.exports[i].getStartPos(),
                        this.exports[i].getLength(),
                        String.format("export %d", i + 1), classFile.getCPDescription(this.exports[i].exports_index.value),
                        "msg_attr_exports", Icons.Data
                );
                this.exports[i].generateTreeNode(exportNode, format);
            }

            // Update the new startPos
            startPosMoving = exporstLastItem.getStartPos() + exporstLastItem.getLength();
        }

        //
        // opens
        //
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "opens_count", this.opens_count.value,
                "msg_attr_opens_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.opens_count.value > 0) {
            Module_attribute.Opens opensLastItem = this.opens[this.opens_count.value - 1];
            final DefaultMutableTreeNode opensNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    opensLastItem.getStartPos() + opensLastItem.getLength() - startPosMoving,
                    String.format("opens [%d]", this.opens_count.value),
                    MESSAGES.getString("msg_attr_opens")
            ));
            parentNode.add(opensNode);

            for (int i = 0; i < this.opens.length; i++) {
                DefaultMutableTreeNode openNode = this.addNode(opensNode,
                        this.opens[i].getStartPos(),
                        this.opens[i].getLength(),
                        String.format("open %d", i + 1), classFile.getCPDescription(this.opens[i].opens_index.value),
                        "msg_attr_opens", Icons.Data
                );
                this.opens[i].generateTreeNode(openNode, format);
            }

            // Update the new startPos
            startPosMoving = opensLastItem.getStartPos() + opensLastItem.getLength();
        }

        //
        // uses
        //
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "uses_count", this.uses_count.value,
                "msg_attr_uses_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.uses_count.value > 0) {
            final int uses_count_length = u2.LENGTH * this.uses_count.value;
            final DefaultMutableTreeNode usesCountNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    uses_count_length,
                    String.format("uses_index [%d]", this.uses_count.value),
                    MESSAGES.getString("msg_attr_uses_index")
            ));
            parentNode.add(usesCountNode);

            for (int i = 0; i < this.uses_index.length; i++) {
                int cpIndex = this.uses_index[i].value;
                this.addNode(usesCountNode,
                        startPosMoving + i * u2.LENGTH,
                        u2.LENGTH,
                        String.format("uses_index %d", i + 1),
                        String.format(TEXT_CPINDEX_VALUE, cpIndex, "uses class", classFile.getCPDescription(cpIndex)),
                        "msg_attr_uses_index",
                        Icons.Class
                );
            }

            startPosMoving += uses_count_length;
        }

        //
        // provides
        //
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "provides_count", this.provides_count.value,
                "msg_attr_provides_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.provides_count.value > 0) {
            Module_attribute.Provides provideLastItem = this.provides[this.provides_count.value - 1];
            final DefaultMutableTreeNode providesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    provideLastItem.getStartPos() + provideLastItem.getLength() - startPosMoving,
                    String.format("provides [%d]", this.provides_count.value),
                    MESSAGES.getString("msg_attr_provides")
            ));
            parentNode.add(providesNode);

            for (int i = 0; i < this.provides.length; i++) {
                DefaultMutableTreeNode provideNode = this.addNode(providesNode,
                        this.provides[i].getStartPos(),
                        this.provides[i].getLength(),
                        String.format("provide %d", i + 1), classFile.getCPDescription(this.provides[i].provides_index.value),
                        "msg_attr_provides", Icons.Data
                );
                this.provides[i].generateTreeNode(provideNode, format);
            }
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Module";
    }

    /**
     * The {@code requires} structure in {@code Module} attribute.
     *
     * @author Amos Shi
     * @since Java 9
     * @see Module
     */
    public static final class Requires extends FileComponent implements GenerateTreeNodeClassFile {

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

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.requires_index.value;
            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "requires_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "requires module", classFile.getCPDescription(cpIndex)),
                    "msg_attr_requires_index",
                    Icons.Name
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "requires_flags",
                    BytesTool.getBinaryString(this.requires_flags.value) + " " + this.getRequiresFlags(),
                    "msg_attr_requires_flags",
                    Icons.AccessFlag
            );
            startPosMoving += u2.LENGTH;

            // requires_version_index
            String requiresVersionIndex = (this.requires_version_index.value == 0)
                    ? "no version information"
                    : classFile.getCPDescription(this.requires_version_index.value);
            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "requires_version_index",
                    this.requires_version_index.value + " - " + requiresVersionIndex,
                    "msg_attr_requires_version_index",
                    Icons.Versions
            );
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
    public static final class Exports extends FileComponent implements GenerateTreeNodeClassFile {

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

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.exports_index.value;
            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "exports_index",
                    String.format(TEXT_CPINDEX_VALUE, cpIndex, "exports package", classFile.getCPDescription(cpIndex)),
                    "msg_attr_exports_index",
                    Icons.Package
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "exports_flags",
                    BytesTool.getBinaryString(this.exports_flags.value) + " " + this.getExportFlags(),
                    "msg_attr_exports_flags",
                    Icons.AccessFlag
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving,
                    u2.LENGTH,
                    "exports_to_count",
                    this.exports_to_count.value,
                    "msg_attr_exports_to_count",
                    Icons.Counter
            );
            startPosMoving += u2.LENGTH;

            if (this.exports_to_count.value > 0) {
                final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH * this.exports_to_count.value,
                        String.format("exports_to_index [%d]", this.exports_to_count.value),
                        MESSAGES.getString("msg_attr_exports_to_index")
                ));
                parentNode.add(exportsToIndexNode);

                for (int i = 0; i < this.exports_to_index.length; i++) {
                    cpIndex = this.exports_to_index[i].value;
                    this.addNode(exportsToIndexNode,
                            startPosMoving + i * u2.LENGTH,
                            u2.LENGTH,
                            String.format("exports_to_index %d", i + 1),
                            String.format(TEXT_CPINDEX_VALUE, cpIndex, "exports to index", classFile.getCPDescription(cpIndex)),
                            "msg_attr_exports_to_index",
                            Icons.Module
                    );
                }
            }
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
    public static final class Opens extends FileComponent implements GenerateTreeNodeClassFile {

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

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.opens_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "opens_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "opens package", classFile.getCPDescription(cpIndex)),
                    "msg_attr_opens_index", Icons.Package
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "opens_flags", BytesTool.getBinaryString(this.opens_flags.value) + " " + this.getOpenFlags(),
                    "msg_attr_opens_flags", Icons.AccessFlag
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "opens_to_count", this.opens_to_count.value,
                    "msg_attr_opens_to_count", Icons.Counter
            );
            startPosMoving += u2.LENGTH;

            if (this.opens_to_count.value > 0) {
                final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH * this.opens_to_count.value,
                        String.format("opens_to_index [%d]", this.opens_to_count.value),
                        "msg_attr_opens_to_index"
                ));
                parentNode.add(exportsToIndexNode);

                for (int i = 0; i < this.opens_to_index.length; i++) {
                    cpIndex = this.opens_to_index[i].value;
                    this.addNode(exportsToIndexNode,
                            startPosMoving + i * u2.LENGTH,
                            u2.LENGTH,
                            String.format("opens_to_index %d", i + 1),
                            String.format(TEXT_CPINDEX_VALUE, cpIndex, "opens module", classFile.getCPDescription(cpIndex)),
                            "msg_attr_opens_to_index",
                            Icons.Module
                    );
                }
            }
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
    public static final class Provides extends FileComponent implements GenerateTreeNodeClassFile {

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

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            final ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.provides_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "provides_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "provides class", classFile.getCPDescription(cpIndex)),
                    "msg_attr_provides_index", Icons.Class
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "provides_with_count", this.provides_with_count.value,
                    "msg_attr_provides_with_count", Icons.Counter
            );
            startPosMoving += u2.LENGTH;

            if (this.provides_with_count.value > 0) {
                final DefaultMutableTreeNode exportsToIndexNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        u2.LENGTH * this.provides_with_count.value,
                        String.format("provides_with_index [%d]", this.provides_with_count.value),
                        MESSAGES.getString("msg_attr_provides_with_index")
                ));
                parentNode.add(exportsToIndexNode);

                for (int i = 0; i < this.provides_with_index.length; i++) {
                    this.addNode(exportsToIndexNode,
                            startPosMoving + i * u2.LENGTH,
                            u2.LENGTH,
                            String.format("provides_with_index %d", i + 1),
                            String.format(TEXT_CPINDEX_VALUE, cpIndex, "provides class", classFile.getCPDescription(cpIndex)),
                            "msg_attr_provides_with_index",
                            Icons.Class
                    );
                }
            }
        } // End of generateTreeNode()
    }

}
