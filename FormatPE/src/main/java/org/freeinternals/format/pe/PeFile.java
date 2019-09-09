/*
 * PeFile.java    June 23, 2015, 21:58
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.pe;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class PeFile extends FileFormat {

    public PeFile(File file) throws IOException, FileFormatException {
        super(file);
    }

    @Override
    public String getContentTabName() {
        return "Portable Executable File";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
