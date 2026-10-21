/*
 * module-info.java    October 20, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

/// Web server and HTTP API to view the internals of binary files, using the
/// same format plug-ins as the desktop viewer.
///
/// @author Amos Shi
module org.binaryinternals.web {
    requires static com.github.spotbugs.annotations;
    requires transitive org.binaryinternals.commonlib;
    requires jdk.httpserver;

    uses org.binaryinternals.commonlib.core.PluginDescriptor;

    exports org.binaryinternals.web;
}
