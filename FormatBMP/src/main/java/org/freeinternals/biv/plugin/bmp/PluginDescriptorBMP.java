/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.bmp;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.bmp.BMPFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorBMP implements PluginDescriptor{

    public String getExtensionDescription() {
        return "Bitmap Images (*.bmp)";
    }

    public String[] getExtensions() {
        return new String[]{"bmp"};
    }

    public Class getFileFormatClass() {
        return BMPFile.class;
    }

}
