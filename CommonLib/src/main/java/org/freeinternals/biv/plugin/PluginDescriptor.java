/*
 * PluginDescriptor.java    Apr 16, 2011, 15:34
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.plugin;

/**
 *
 * @author Amos Shi
 */
public interface PluginDescriptor {

    String getExtensionDescription();

    String[] getExtensions();

    Class getFileFormatClass();
}
