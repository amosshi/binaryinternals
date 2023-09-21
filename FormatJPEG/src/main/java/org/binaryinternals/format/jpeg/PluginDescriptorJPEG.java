/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import org.binaryinternals.commonlib.core.PluginDescriptor;
import org.binaryinternals.commonlib.core.FileFormat;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorJPEG implements PluginDescriptor{

    @Override
    public String getExtensionDescription() {
        return "JPEG Image (*.jpg,*jpeg)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"jpeg", "jpg"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return JPEGFile.class;
    }

}
