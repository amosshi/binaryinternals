/*
 * PluginDescriptorPNG.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.zip;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.format.zip.ZIPFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorZIP implements PluginDescriptor{

    @Override
    public String getExtensionDescription() {
        return "ZIP file (*.jar, *.jmod, *.zip)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"jar", "jmod", "zip"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ZIPFile.class;
    }

}
