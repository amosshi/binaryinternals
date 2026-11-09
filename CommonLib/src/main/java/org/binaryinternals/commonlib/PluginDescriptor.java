/*
 * PluginDescriptor.java    Apr 16, 2011, 15:34
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.commonlib;

import java.util.List;

/// Describes a file format plug-in: the file extensions it handles and the class that parses them.
///
/// @author Amos Shi
public interface PluginDescriptor {

    /// Gets the description of the file extensions, shown in the file chooser filter.
    ///
    /// @return Description of the file type
    String getExtensionDescription();

    /// Gets the file extensions handled by this plug-in, without the leading dot.
    ///
    /// @return File extensions, e.g. `class`
    List<String> getExtensions();

    /// Gets the class that parses the files of this plug-in.
    ///
    /// @return The [FileFormat] sub-class, it must have a public constructor taking a `java.nio.file.Path`
    Class<? extends FileFormat> getFileFormatClass();
}
