/*
 * ImageLoader.java    September 21, 2010, 20:34
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv.jpeg.ui.resource;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Amos Shi
 */
public class ImageLoader {

    private static Icon shortcutIcon = null;

    public static Icon getShortcutIcon() {
        if (shortcutIcon == null) {
            URL url = ImageLoader.class.getResource("shortcut.png");
            shortcutIcon = new ImageIcon(url);
        }
        return shortcutIcon;
    }
}
