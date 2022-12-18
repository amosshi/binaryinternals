/*
 * Plugin.java    Apr 12, 2011, 13:04
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin;

import org.freeinternals.commonlib.core.PluginDescriptor;
import org.freeinternals.commonlib.core.FileFormat;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class PluginManager {

    public static final String MANIFEST_ATTR_NAME = "biv-plugin";
    public static final String PLUGIN_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "libs";
    /**
     * Jar file name and the plug-in descriptor.
     */
    static final Map<String, PluginDescriptor> PLUGINS = new HashMap<>(10);

    static {
        loadPlugins();
    }

    private PluginManager() {
    }

    /**
     * <pre>
     * java:S135 - Loops should not contain more than a single "break" or "continue" statement --- multiple "continue" make code more readable
     * java:S3776 - Cognitive Complexity of methods should not be too high
     * </pre>
     */
    @SuppressWarnings({"java:S135", "java:S3776"}) 
    private static void loadPlugins() {
        File pluginFolder = new File(PLUGIN_DIR);
        String pluginDescClassName;

        if (!pluginFolder.exists()) {
            // The plugin folder does not exist
            return;
        }

        File[] pluginFiles = pluginFolder.listFiles();
        if (pluginFiles == null) {
            return;
        }

        for (File plguinFile : pluginFiles) {
            if (plguinFile.isFile() && plguinFile.getName().toLowerCase().endsWith(".jar")) {
                try (JarFile jar = new JarFile(plguinFile)) {
                    Manifest mf = jar.getManifest();
                    if (mf == null) {
                        continue;
                    }

                    pluginDescClassName = mf.getMainAttributes().getValue(MANIFEST_ATTR_NAME);
                    if (pluginDescClassName == null || pluginDescClassName.length() == 0) {
                        continue;
                    }

                    loadPlugin(plguinFile, pluginDescClassName);
                } catch (Exception ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void loadPlugin(File pluginFile, String pluginDescClassName) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        URL url = pluginFile.toURI().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{url})) {
            Class<?> cls = loader.loadClass(pluginDescClassName);
            if (cls == null) {
                return;
            }

            PLUGINS.put(pluginFile.getName(), (PluginDescriptor) cls.getDeclaredConstructor().newInstance());
        }
    }

    public static String getPlugedExtensions() {
        StringBuilder builder = new StringBuilder(16);
        if (!PLUGINS.isEmpty()) {
            builder.append(" - ");
            PLUGINS.values().stream().map(plugin -> plugin.getExtensions()).forEachOrdered(exts -> {
                for (String ext : exts) {
                    builder.append(ext);
                    builder.append(", ");
                }
            });
            builder.append(" ...");
        }

        return builder.toString();
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

    public static FileFormat getFile(final File file) throws FileFormatException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Class<? extends FileFormat> fileFormatClass = null;
        String ext = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        for (PluginDescriptor plugin : PLUGINS.values()) {
            if (isContain(plugin.getExtensions(), ext)) {
                fileFormatClass = plugin.getFileFormatClass();
            }
        }
        if (fileFormatClass == null) {
            fileFormatClass = DefaultFileFormat.class;
        }

        Constructor<? extends FileFormat> c = fileFormatClass.getConstructor(File.class);
        return c.newInstance(file);
    }

    static boolean isContain(String[] exts, String ext) {
        boolean result = false;
        if (exts != null && exts.length != 0) {
            for (String item : exts) {
                if (item.equalsIgnoreCase(ext)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Return the loaded {@link #PLUGINS}.
     *
     * @return Loaded plug-ins
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "We need it")
    public static Map<String, PluginDescriptor> getPlugins() {
        return PLUGINS;
    }
}
