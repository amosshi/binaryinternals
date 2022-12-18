/*
 * PluginDescriptorClass.java    Apr 17, 2021, 15:02
 *
 * Copyright 2021, FreeInternals.org.
 * Use is subject to license terms.
 */

package org.freeinternals.biv.plugin;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.format.classfile.ClassFile;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorClass implements PluginDescriptor{

    @Override
    public String getExtensionDescription() {
        return "Java class File (*.class)";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{"class"};
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ClassFile.class;
    }

}
