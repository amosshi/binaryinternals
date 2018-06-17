/*
 * JSplitPaneClassFile.java    19:58, April 04, 2009
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.freeinternals.javaclassviewer.ui.JSplitPaneClassFile;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JFrameClassFile extends JFrame {

    private static final long serialVersionUID = 4876543219876500000L;

    JFrameClassFile(final String title, final byte[] b, JFrame top) {
        this.setTitle(title);
        this.setSize(990, 691);
        this.setLayout(new BorderLayout());

        final JSplitPaneClassFile pane = new JSplitPaneClassFile(b, top);
        this.add(pane, BorderLayout.CENTER);
    }
}
