/*
 * PluginDescriptorZIP.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import java.util.List;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Plug-in descriptor for the ZIP file format.
///
/// @author Amos Shi
public final class PluginDescriptorZIP implements PluginDescriptor {

    /// Public no-argument constructor, required by `ServiceLoader`.
    public PluginDescriptorZIP() {
        // Nothing to initialize
    }

    @Override
    public String getExtensionDescription() {
        return "ZIP file (*.jar, *.zip)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("jar", "zip");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ZIPFile.class;
    }
}
