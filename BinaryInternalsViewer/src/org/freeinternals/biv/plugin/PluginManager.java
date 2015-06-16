/*
 * Plugin.java    Apr 12, 2011, 13:04
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class PluginManager {

    public static final String MANIFEST_ATTR_NAME = "biv-plugin";
    public static final String PLUGIN_DIR = System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "plugin";
    static final List<PluginDescriptor> plugins = Collections.synchronizedList(new ArrayList<PluginDescriptor>(10));

    static {
        loadPlugins();
    }

    private static void loadPlugins() {
        File pluginFolder = new File(PLUGIN_DIR);
        File pluginFiles[];
        String pluginDescClassName;

        if (pluginFolder.exists() == false) {
            // The plugin folder does not exist
            return;
        }

        pluginFiles = pluginFolder.listFiles();
        for (File plguinFile : pluginFiles) {
            if (plguinFile.isFile() & plguinFile.getName().toLowerCase().endsWith(".jar")) {
                try {
                    JarFile jar = new JarFile(plguinFile);
                    if (jar == null) {
                        continue;
                    }

                    Manifest mf = jar.getManifest();
                    if (mf == null) {
                        continue;
                    }

                    pluginDescClassName = mf.getMainAttributes().getValue(MANIFEST_ATTR_NAME);
                    if (pluginDescClassName == null || pluginDescClassName.length() == 0) {
                        continue;
                    }

                    loadPlugin(plguinFile, pluginDescClassName);
                } catch (IOException ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void loadPlugin(File plguinFile, String pluginDescClassName) throws Exception {
        URL url = plguinFile.toURI().toURL();
        ClassLoader loader = new URLClassLoader(new URL[]{url});
        Class cls = loader.loadClass(pluginDescClassName);
        if (cls == null) {
            return;
        }
        plugins.add((PluginDescriptor) cls.newInstance());
    }

    public static String getPlugedExtensions(){
        StringBuilder builder = new StringBuilder(16);
        if (plugins.size() > 0) {
            builder.append(" - ");
            for (PluginDescriptor plugin : plugins) {
                String[] exts = plugin.getExtensions();
                for (String ext : exts) {
                    builder.append(ext);
                    builder.append(", ");
                }
            }
            builder.append(" ...");
        }
    
        return builder.toString();
    }
    
    public static void initChooseFilters(JFileChooser chooser) {
        FileNameExtensionFilter filter;
        for (PluginDescriptor plugin : plugins) {
            filter = new FileNameExtensionFilter(
                    plugin.getExtensionDescription(),
                    plugin.getExtensions());
            chooser.addChoosableFileFilter(filter);
        }
    }

    public static FileFormat getFile(final File file) throws FileFormatException, Throwable {
        Class fileFormatClass = null;
        Constructor<FileFormat> c = null;
        FileFormat ff = null;
        String ext = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        for (PluginDescriptor plugin : plugins) {
            if (isContain(plugin.getExtensions(), ext)) {
                fileFormatClass = plugin.getFileFormatClass();
            }
        }
        if (fileFormatClass == null) {
            fileFormatClass = DefaultFileFormat.class;
        }

        try {
            c = fileFormatClass.getConstructor(File.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (SecurityException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        try {
            ff = c.newInstance(file);
        } catch (InstantiationException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex.getCause();
        }

        return ff;
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
}
