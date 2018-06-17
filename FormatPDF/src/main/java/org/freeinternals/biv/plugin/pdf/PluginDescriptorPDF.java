/*
 * PluginDescriptorPDF.java    May 17, 2011, 09:26
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin.pdf;

import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.format.pdf.PDFFile;

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
    public String[] getExtensions() {
        return new String[]{"pdf"};
    }

    @Override
    public Class getFileFormatClass() {
        return PDFFile.class;
    }
}
