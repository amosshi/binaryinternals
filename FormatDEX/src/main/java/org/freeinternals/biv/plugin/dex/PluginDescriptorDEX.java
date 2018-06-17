/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin.dex;

import org.freeinternals.biv.plugin.PluginDescriptor;
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

    public String[] getExtensions() {
        return new String[]{"dex"};
    }

    public Class getFileFormatClass() {
        return DexFile.class;
    }

}
