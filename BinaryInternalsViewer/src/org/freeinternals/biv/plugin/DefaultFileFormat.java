/*
 * FileFormatDefault.java    Apr 14, 2011, 23:55
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin;

import org.freeinternals.commonlib.core.FileFormat;
import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class DefaultFileFormat extends FileFormat{


    public DefaultFileFormat(final File file) throws IOException, FileFormatException {
        super(file);
    }

    @Override
    public String getContentTabName() {
       return "Binary Data";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
