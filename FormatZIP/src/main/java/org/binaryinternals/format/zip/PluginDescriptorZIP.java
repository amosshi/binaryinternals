/*
 * PluginDescriptorPNG.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import java.util.List;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PluginDescriptor;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorZIP implements PluginDescriptor{

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
