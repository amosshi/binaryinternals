/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.jpeg;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.format.jpeg.JPEGFile;

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
