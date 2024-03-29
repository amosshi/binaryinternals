/*
 * PluginDescriptorPNG.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.png;

import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PluginDescriptor;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorPNG implements PluginDescriptor{

    @Override
    public String getExtensionDescription() {
        return "Portable Network Graphic (*.png)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"png"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return PNGFile.class;
    }

}
