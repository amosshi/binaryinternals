/*
 * ElfFile.java    June 23, 2015, 21:47
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class ElfFile extends FileFormat {

    public ElfFile(File file) throws IOException, FileFormatException {
        super(file);
    }

    @Override
    public String getContentTabName() {
        return "Executable and Linkable Format";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
    }
}
