/*
 * PluginManager.java    Apr 12, 2011, 13:04
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Loads the file format plug-ins, and opens a file with the plug-in for its
/// extension.
///
/// @author Amos Shi
public final class PluginManager {

    private static final Logger LOGGER = Logger.getLogger(PluginManager.class.getName());
    /// Directory of the plug-in jar files.
    public static final String PLUGIN_DIR = resolvePluginDir();
    /// Plug-in descriptor class name and the plug-in descriptor. Keyed by class
    /// rather than jar, since several plug-ins can share a location, e.g.
    /// `target/classes` directories when running from an IDE.
    static final SortedMap<String, PluginDescriptor> PLUGINS = Collections.unmodifiableSortedMap(loadPlugins());

    private PluginManager() {
    }

    /// The "libs" folder next to the running jar, so it works wherever the
    /// process is launched from (e.g. a double-click). Falls back to the
    /// working directory when not running from a jar.
    private static String resolvePluginDir() {
        try {
            final CodeSource source = PluginManager.class.getProtectionDomain().getCodeSource();
            if (source != null) {
                final Path location = Path.of(source.getLocation().toURI());
                final Path parent = location.getParent();
                if (Files.isRegularFile(location) && parent != null) {
                    return parent.resolve("libs").toString();
                }
            }
        } catch (URISyntaxException | RuntimeException e) {
            LOGGER.log(Level.FINE, "Cannot locate the jar, use the working directory", e);
        }
        return Path.of(System.getProperty("user.dir"), "libs").toString();
    }

    /// The class loader is intentionally kept open, because the plug-in classes
    /// are loaded lazily from the jars for as long as the application runs.
    @SuppressWarnings("resource")
    private static SortedMap<String, PluginDescriptor> loadPlugins() {
        final Path pluginFolder = Path.of(PLUGIN_DIR);
        final List<URL> urls = new ArrayList<>();
        if (Files.isDirectory(pluginFolder)) {
            try (DirectoryStream<Path> jars = Files.newDirectoryStream(pluginFolder, "*.{jar,JAR}")) {
                for (Path jarPath : jars) {
                    urls.add(jarPath.toUri().toURL());
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to list the plugin folder " + PLUGIN_DIR, ex);
            }
        }

        final ClassLoader parent = PluginManager.class.getClassLoader();
        final ClassLoader loader = urls.isEmpty() ? parent : new URLClassLoader(urls.toArray(URL[]::new), parent);

        final SortedMap<String, PluginDescriptor> plugins = new TreeMap<>();
        final Iterator<ServiceLoader.Provider<PluginDescriptor>> providers
                = ServiceLoader.load(PluginDescriptor.class, loader).stream().iterator();
        while (providers.hasNext()) {
            try {
                final ServiceLoader.Provider<PluginDescriptor> provider = providers.next();
                plugins.put(provider.type().getName(), provider.get());
            } catch (ServiceConfigurationError | RuntimeException ex) {
                LOGGER.log(Level.SEVERE, "Failed to load a plugin", ex);
            }
        }
        return plugins;
    }

    /// Get the jar file name where a class is loaded from.
    ///
    /// @param type The class
    /// @return Jar file name, or the class name if the location is unknown
    public static String getJarName(final Class<?> type) {
        final CodeSource source = type.getProtectionDomain().getCodeSource();
        if (source != null) {
            try {
                final Path fileName = Path.of(source.getLocation().toURI()).getFileName();
                if (fileName != null) {
                    return fileName.toString();
                }
            } catch (URISyntaxException | RuntimeException e) {
                LOGGER.log(Level.FINE, "Cannot locate the plugin jar", e);
            }
        }
        return type.getName();
    }

    /// Gets all file extensions handled by the loaded plug-ins, for the window title.
    ///
    /// @return The extensions, e.g. ` - class, dex, ...`; empty string if no plug-in is loaded
    public static String getPluggedExtensions() {
        if (PLUGINS.isEmpty()) {
            return "";
        }
        return PLUGINS.values().stream()
                .flatMap(plugin -> plugin.getExtensions().stream())
                .collect(Collectors.joining(", ", " - ", ", ..."));
    }

    /// Adds one file filter per loaded plug-in to the file chooser.
    ///
    /// @param chooser The file chooser to add the filters to
    public static void initChooseFilters(final JFileChooser chooser) {
        for (final PluginDescriptor plugin : PLUGINS.values()) {
            chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                    plugin.getExtensionDescription(),
                    plugin.getExtensions().toArray(String[]::new)));
        }
    }

    /// Parses a file with the plug-in handling its file extension, or as raw binary data if no plug-in handles it.
    ///
    /// @param file The file to parse
    /// @return The parsed file
    /// @throws IOException if the file cannot be read or parsed
    /// @throws ReflectiveOperationException if the plug-in file format class cannot be created
    public static FileFormat getFile(final Path file) throws IOException,
            ReflectiveOperationException {
        final String name = String.valueOf(file.getFileName());
        final int dot = name.lastIndexOf('.');
        final String ext = dot < 0 ? "" : name.substring(dot + 1);

        final Class<? extends FileFormat> fileFormatClass = PLUGINS.values().stream()
                .filter(plugin -> isContain(plugin.getExtensions(), ext))
                .findFirst()
                .<Class<? extends FileFormat>>map(PluginDescriptor::getFileFormatClass)
                .orElse(DefaultFileFormat.class);

        final Constructor<? extends FileFormat> c = fileFormatClass.getConstructor(Path.class);
        try {
            return c.newInstance(file);
        } catch (InvocationTargetException ex) {
            // Surface what the plug-in actually threw, e.g. OutOfMemoryError
            switch (ex.getCause()) {
                case Error e -> throw e;
                case RuntimeException e -> throw e;
                case IOException e -> throw e;
                case null, default -> throw ex;
            }
        }
    }

    static boolean isContain(final List<String> exts, final String ext) {
        return exts != null && exts.stream().anyMatch(ext::equalsIgnoreCase);
    }

    /// Return the loaded [#PLUGINS].
    ///
    /// @return Loaded plug-ins
    public static SortedMap<String, PluginDescriptor> getPlugins() {
        return PLUGINS;
    }
}
