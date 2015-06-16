/*
 * PluginDescriptorPNG.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.png;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.png.PNGFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorPNG implements PluginDescriptor{

    public String getExtensionDescription() {
        return "Portable Network Graphic (*.png)";
    }

    public String[] getExtensions() {
        return new String[]{"PNG"};
    }

    public Class getFileFormatClass() {
        return PNGFile.class;
    }

}
