/*
 * JLabelHyperLink.java    August 21, 2010, 10:32
 *
 * Copyright 2009, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.app;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * Hyper Link enabled JLabel.
 *
 * @author Amos Shi
 */
public class JLabelHyperLink extends JLabel {

    private static final long serialVersionUID = 4876543219876500005L;

    /**
     * Text of the label.
     */
    private final String text;
    /**
     * Target URL of the label.
     */
    private final String url;
    /**
     * Flag indicates whether current JVM support browser or not.
     */
    private final boolean isSupported;

    /**
     * Create a new Hyper Link enabled JLabel.
     *
     * @param txt Text of the label
     * @param link Target Link URL
     */
    public JLabelHyperLink(final String txt, final String link) {
        this.text = txt;
        this.url = link;

        this.isSupported = isBrowseSupported();

        this.setText(false);
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(final MouseEvent e) {
                setText(isSupported);
                if (isSupported) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                setText(false);
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                try {
                    if (isSupported) {
                        Desktop.getDesktop().browse(URI.create(JLabelHyperLink.this.url));
                    }
                } catch (IOException | RuntimeException ex) {
                    Logger.getLogger(JLabelHyperLink.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static boolean isBrowseSupported() {
        try {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void setText(final boolean b) {
        if (!b) {
            setText("<html><font color=blue><u>" + text);
        } else {
            setText("<html><font color=red><u>" + text);
        }
    }
}
