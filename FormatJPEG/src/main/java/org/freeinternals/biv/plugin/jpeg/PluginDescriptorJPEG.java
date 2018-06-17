/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.jpeg;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.jpeg.JPEGFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorJPEG implements PluginDescriptor{

    public String getExtensionDescription() {
        return "JPEG Image (*.jpg,*jpeg)";
    }

    public String[] getExtensions() {
        return new String[]{"jpeg", "jpg"};
    }

    public Class getFileFormatClass() {
        return JPEGFile.class;
    }

}
