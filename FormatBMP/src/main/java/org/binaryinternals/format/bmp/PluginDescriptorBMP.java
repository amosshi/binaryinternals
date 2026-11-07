/*
 * PluginDescriptorBMP.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.bmp;

import java.util.List;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Plug-in descriptor for the BMP file format.
///
/// @author Amos Shi
public final class PluginDescriptorBMP implements PluginDescriptor {

    /// Public no-argument constructor, required by `ServiceLoader`.
    public PluginDescriptorBMP() {
        // Nothing to initialize
    }

    @Override
    public String getExtensionDescription() {
        return "Bitmap Images (*.bmp)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("bmp");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return BMPFile.class;
    }
}
