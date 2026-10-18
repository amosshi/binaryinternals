/*
 * Plugin.java    Apr 12, 2011, 13:04
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.plugin;

import org.binaryinternals.commonlib.core.PluginDescriptor;
import org.binaryinternals.commonlib.core.FileFormat;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.binaryinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class PluginManager {

    private static final Logger LOGGER = Logger.getLogger(PluginManager.class.getName());
    public static final String PLUGIN_DIR = resolvePluginDir();
    /**
     * Jar file name and the plug-in descriptor.
     */
    static final Map<String, PluginDescriptor> PLUGINS = new TreeMap<>();

    static {
        loadPlugins();
    }

    private PluginManager() {
    }

    /**
     * The "libs" folder next to the running jar, so it works wherever the
     * process is launched from (e.g. a double-click). Falls back to the
     * working directory when not running from a jar.
     */
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

    /**
     * The class loader is intentionally kept open, because the plug-in classes
     * are loaded lazily from the jars for as long as the application runs.
     */
    @SuppressWarnings("resource")
    private static void loadPlugins() {
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

        final Iterator<ServiceLoader.Provider<PluginDescriptor>> providers =
                ServiceLoader.load(PluginDescriptor.class, loader).stream().iterator();
        while (providers.hasNext()) {
            try {
                final ServiceLoader.Provider<PluginDescriptor> provider = providers.next();
                PLUGINS.put(jarName(provider.type()), provider.get());
            } catch (ServiceConfigurationError | RuntimeException ex) {
                LOGGER.log(Level.SEVERE, "Failed to load a plugin", ex);
            }
        }
    }

    private static String jarName(final Class<?> type) {
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

    public static String getPlugedExtensions() {
        if (PLUGINS.isEmpty()) {
            return "";
        }
        return PLUGINS.values().stream()
                .flatMap(plugin -> Arrays.stream(plugin.getExtensions()))
                .collect(Collectors.joining(", ", " - ", ", ..."));
    }

    public static void initChooseFilters(JFileChooser chooser) {
        FileNameExtensionFilter filter;
        for (PluginDescriptor plugin : PLUGINS.values()) {
            filter = new FileNameExtensionFilter(
                    plugin.getExtensionDescription(),
                    plugin.getExtensions());
            chooser.addChoosableFileFilter(filter);
        }
    }

    public static FileFormat getFile(final Path file) throws FileFormatException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Class<? extends FileFormat> fileFormatClass = null;
        final String name = String.valueOf(file.getFileName());
        final int dot = name.lastIndexOf('.');
        final String ext = dot < 0 ? "" : name.substring(dot + 1);

        for (PluginDescriptor plugin : PLUGINS.values()) {
            if (isContain(plugin.getExtensions(), ext)) {
                fileFormatClass = plugin.getFileFormatClass();
                break;
            }
        }
        if (fileFormatClass == null) {
            fileFormatClass = DefaultFileFormat.class;
        }

        Constructor<? extends FileFormat> c = fileFormatClass.getConstructor(Path.class);
        return c.newInstance(file);
    }

    static boolean isContain(String[] exts, String ext) {
        return exts != null && Arrays.stream(exts).anyMatch(ext::equalsIgnoreCase);
    }

    /**
     * Return the loaded {@link #PLUGINS}.
     *
     * @return Loaded plug-ins
     */
    public static Map<String, PluginDescriptor> getPlugins() {
        return Collections.unmodifiableMap(PLUGINS);
    }
}
