/*
 * FileFormatDefault.java    Apr 14, 2011, 23:55
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.plugin;

import java.util.Optional;
import org.binaryinternals.commonlib.FileFormat;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.Icons;

/**
 *
 * @author Amos Shi
 */
public class DefaultFileFormat extends FileFormat{


    public DefaultFileFormat(final Path file) throws IOException {
        super(file);
    }


    @Override
    @SuppressWarnings("java:S1186")  // Methods should not be empty --- Ignore this rule
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
    @Override
    public String getContentTabName() {
       return "Binary Data";
    }

    @Override
    public Optional<Icons> getIcon() {
        return Optional.of(Icons.BinaryFile);
    }
}
