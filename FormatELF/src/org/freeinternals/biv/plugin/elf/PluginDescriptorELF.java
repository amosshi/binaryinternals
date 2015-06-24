/*
 * PluginDescriptorELF.java    June 23, 2015, 21:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin.elf;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.elf.ElfFile;

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
    public Class getFileFormatClass() {
        return ElfFile.class;
    }

}
