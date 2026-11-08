/*
 * PluginDescriptorPDF.java    May 17, 2011, 09:26
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.pdf;

import java.util.List;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Plug-in descriptor for the PDF file format.
///
/// @author Amos Shi
public final class PluginDescriptorPDF implements PluginDescriptor {

    /// Public no-argument constructor, required by `ServiceLoader`.
    public PluginDescriptorPDF() {
        // Nothing to initialize
    }

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
