/*
 * PluginDescriptorClass.java    Apr 17, 2021, 15:02
 *
 * Copyright 2021, BinaryInternals.org.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.classfile;

import org.binaryinternals.commonlib.PluginDescriptor;
import java.util.List;
import org.binaryinternals.commonlib.FileFormat;

/// @author Amos Shi
public class PluginDescriptorClass implements PluginDescriptor{

    public PluginDescriptorClass() {
    }

    @Override
    public String getExtensionDescription() {
        return "Java class File (*.class)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("class");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return ClassFile.class;
    }

}
