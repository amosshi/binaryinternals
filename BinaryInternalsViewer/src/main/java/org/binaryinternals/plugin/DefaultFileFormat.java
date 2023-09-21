/*
 * FileFormatDefault.java    Apr 14, 2011, 23:55
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.plugin;

import org.binaryinternals.commonlib.core.FileFormat;
import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.Icons;

/**
 *
 * @author Amos Shi
 */
public class DefaultFileFormat extends FileFormat{


    public DefaultFileFormat(final File file) throws IOException, FileFormatException {
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
    public Icons getIcon() {
        return Icons.BinaryFile;
    }
}
