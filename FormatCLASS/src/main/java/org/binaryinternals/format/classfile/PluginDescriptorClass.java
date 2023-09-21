/*
 * PluginDescriptorClass.java    Apr 17, 2021, 15:02
 *
 * Copyright 2021, BinaryInternals.org.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.classfile;

import org.binaryinternals.commonlib.core.PluginDescriptor;
import org.binaryinternals.commonlib.core.FileFormat;

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
