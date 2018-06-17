/*
 * PluginDescriptorELF.java    June 23, 2015, 21:45
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin.pe;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.pe.PeFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorPE implements PluginDescriptor {

    @Override
    public String getExtensionDescription() {
        return "Portable Executable (*.cpl, *.exe, *.dll, *.ocx, *.sys, *.scr, *.drv, *.efi, *.fon)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"cpl", "exe", "dll", "ocx", "sys", "scr", "drv", "efi", "fon"};
    }

    @Override
    public Class getFileFormatClass() {
        return PeFile.class;
    }

}
