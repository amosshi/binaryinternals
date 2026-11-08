/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.util.List;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Plug-in descriptor for the Android DEX file format.
///
/// @author Amos Shi
public final class PluginDescriptorDEX implements PluginDescriptor {

    /// Public no-argument constructor, required by `ServiceLoader`.
    public PluginDescriptorDEX() {
        // Nothing to initialize
    }

    @Override
    public String getExtensionDescription() {
        return "Android DEX File (*.dex)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("dex");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return DexFile.class;
    }

}
