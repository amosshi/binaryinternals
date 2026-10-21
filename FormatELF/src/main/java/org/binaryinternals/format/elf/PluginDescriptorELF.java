/*
 * PluginDescriptorELF.java    June 23, 2015, 21:45
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.elf;

import java.util.List;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorELF implements PluginDescriptor {

    public PluginDescriptorELF() {
    }

    @Override
    public String getExtensionDescription() {
        return "Executable and Linkable Format (*.axf, *.bin, *.elf, *.o, *.prx, *.puff, *.so)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("axf", "bin", "elf", "o", "prx", "puff", "so");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ElfFile.class;
    }

}
