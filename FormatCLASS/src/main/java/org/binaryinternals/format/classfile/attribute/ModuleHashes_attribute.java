/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
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
 *
 * The class for the {@code ModuleHashes} attribute. The
 * {@code AttributeModulePackages} attribute has the following format:
 *
 * <pre>
 *    ModuleHashes_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        // index to CONSTANT_utf8_info structure with algorithm name
 *        u2 algorithm_index;
 *
 *        // the number of entries in the hashes table
 *        u2 hashes_count;
 *        {   u2 module_name_index (index to CONSTANT_Module_info structure)
 *            u2 hash_length;
 *            u1 hash[hash_length];
 *        } hashes[hashes_count];
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since OpenJDK 9
 *
 * @see
 * <a href="http://mail.openjdk.java.net/pipermail/jigsaw-dev/2017-February/011262.html">
 * OpenJDK specific attribute specifications</a>
 * @see
 * <a href="https://hg.openjdk.java.net/jdk9/dev/jdk/file/65464a307408/src/java.base/share/classes/jdk/internal/module/ClassFileAttributes.java">
 * ModuleHashes attribute.</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class ModuleHashes_attribute extends attribute_info {

    /**
     * Index to CONSTANT_utf8_info structure with algorithm name.
     */
    public final u2 algorithm_index;

    /**
     * The value of the {@link #hashes_count} item indicates the number of
     * entries in the {@link #hashes} table.
     */
    public final u2 hashes_count;
    public final Hashes[] hashes;

    ModuleHashes_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.algorithm_index = new u2(posDataInputStream);
        this.hashes_count = new u2(posDataInputStream);
        if (this.hashes_count.value > 0) {
            this.hashes = new Hashes[this.hashes_count.value];
            for (int i = 0; i < this.hashes_count.value; i++) {
                this.hashes[i] = new Hashes(posDataInputStream);
            }
        } else {
            this.hashes = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, final FileFormat fileFormat) {
        ClassFile classFile = (ClassFile) fileFormat;
        int startPosMoving = super.startPos + 6;

        int cpIndex = this.algorithm_index.value;
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "algorithm_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "algorithm name", classFile.getCPDescription(cpIndex)),
                "msg_attr_ModuleHashes_algorithm_index", Icons.Name
        );
        startPosMoving += u2.LENGTH;

        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "hashes_count", this.hashes_count.value,
                "msg_attr_ModuleHashes_hashes_count", Icons.Counter
        );
        startPosMoving += u2.LENGTH;

        if (this.hashes_count.value > 0) {
            ModuleHashes_attribute.Hashes hashLastItem = this.hashes[this.hashes_count.value - 1];
            final DefaultMutableTreeNode providesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    startPosMoving,
                    hashLastItem.getStartPos() + hashLastItem.getLength() - startPosMoving,
                    String.format("hashes [%d]", this.hashes_count.value)
            ));
            parentNode.add(providesNode);

            for (int i = 0; i < this.hashes.length; i++) {
                DefaultMutableTreeNode hashNode = this.addNode(providesNode,
                        this.hashes[i].getStartPos(),
                        this.hashes[i].getLength(),
                        String.format("hash %d", i + 1),
                        classFile.getCPDescription(this.hashes[i].module_name_index.value),
                        "msg_attr_ModuleHashes_hashes",
                        Icons.Data
                );
                this.hashes[i].generateTreeNode(hashNode, fileFormat);
            }
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_ModuleHashes";
    }

    /**
     * The {@code hashes} structure in {@code ModuleHashes} attribute.
     *
     * @author Amos Shi
     * @since OpenJDK 9
     * @see Module
     */
    public static final class Hashes extends FileComponent implements GenerateTreeNodeClassFile {

        /**
         * Index to CONSTANT_Module_info structure.
         */
        public final u2 module_name_index;
        public final u2 hash_length;
        public final u1[] hash;

        private Hashes(final PosDataInputStream posDataInputStream) throws IOException {
            this.startPos = posDataInputStream.getPos();

            this.module_name_index = new u2(posDataInputStream);
            this.hash_length = new u2(posDataInputStream);
            if (this.hash_length.value > 0) {
                this.hash = new u1[this.hash_length.value];
                for (int i = 0; i < this.hash_length.value; i++) {
                    this.hash[i] = new u1(posDataInputStream);
                }
            } else {
                this.hash = null;
            }

            this.length = posDataInputStream.getPos() - this.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            ClassFile classFile = (ClassFile) fileFormat;
            int startPosMoving = this.getStartPos();

            int cpIndex = this.module_name_index.value;
            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "module_name_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "module name", classFile.getCPDescription(cpIndex)),
                    "msg_attr_ModuleHashes_module_name_index", Icons.Name
            );
            startPosMoving += u2.LENGTH;

            this.addNode(parentNode,
                    startPosMoving, u2.LENGTH,
                    "hash_length", this.hash_length.value,
                    "msg_attr_ModuleHashes_hash_length", Icons.Length
            );
            startPosMoving += u2.LENGTH;

            if (this.hash_length.value > 0) {
                this.addNode(parentNode,
                        startPosMoving, u1.LENGTH * this.hash_length.value,
                        "hash", "raw hash data",
                        "msg_attr_ModuleHashes_hash", Icons.Checksum
                );
            }
        }
    }

}
