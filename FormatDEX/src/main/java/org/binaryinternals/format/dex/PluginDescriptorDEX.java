/*
 * PluginDescriptor.java    Apr 16, 2011, 20:14
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PluginDescriptor;


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
