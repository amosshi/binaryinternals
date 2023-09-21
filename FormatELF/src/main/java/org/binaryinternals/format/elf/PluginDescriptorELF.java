/*
 * PluginDescriptorELF.java    June 23, 2015, 21:45
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.elf;

import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PluginDescriptor;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorELF implements PluginDescriptor {

    @Override
    public String getExtensionDescription() {
        return "Executable and Linkable Format (*.axf, *.bin, *.elf, *.o, *.prx, *.puff, *.so)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"axf", "bin", "elf", "o", "prx", "puff", "so"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ElfFile.class;
    }

}
