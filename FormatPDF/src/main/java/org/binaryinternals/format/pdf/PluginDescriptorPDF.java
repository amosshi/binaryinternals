/*
 * PluginDescriptorPDF.java    May 17, 2011, 09:26
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.pdf;

import java.util.List;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PluginDescriptor;

/**
 *
 * @author Amos Shi
 */
public class PluginDescriptorPDF implements PluginDescriptor {

    @Override
    public String getExtensionDescription() {
        return "Portable Document Format (*.pdf)";
    }

    @Override
    public List<String> getExtensions() {
        return List.of("pdf");
    }

    @Override
    public Class<? extends FileFormat> getFileFormatClass() {
        return PDFFile.class;
    }
}
