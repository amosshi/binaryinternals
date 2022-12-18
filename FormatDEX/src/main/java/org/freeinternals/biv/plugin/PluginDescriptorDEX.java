/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin;

import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PluginDescriptor;
import org.freeinternals.format.dex.DexFile;


/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorDEX implements PluginDescriptor{

    @Override
    public String getExtensionDescription() {
        return "Android DEX File (*.dex)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"dex"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return DexFile.class;
    }

}
