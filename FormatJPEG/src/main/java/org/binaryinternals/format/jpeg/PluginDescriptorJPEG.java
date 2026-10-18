/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg;

import org.binaryinternals.commonlib.core.PluginDescriptor;
import java.util.List;
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
    public List<String> getExtensions() {
        return List.of("jpeg", "jpg");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return JPEGFile.class;
    }

}
