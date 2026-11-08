/*
 * DefaultFileFormat.java    Apr 14, 2011, 23:55
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;

/// The file format for files no plug-in handles, shown as raw binary data only.
///
/// @author Amos Shi
public final class DefaultFileFormat extends FileFormat {

    public DefaultFileFormat(final Path file) throws IOException {
        super(file);
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        // No structure to show, only the binary data
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
