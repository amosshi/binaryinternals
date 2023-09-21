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
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * Hyper Link enabled JLabel.
 *
 * @author Amos Shi
 */
public class JLabelHyperLink extends JLabel {

    private static final long serialVersionUID = 4876543219876500000L;

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
    private boolean isSupported;

    /**
     * Create a new Hyper Link enabled JLabel.
     *
     * @param txt Text of the label
     * @param link Target Link URL
     */
    public JLabelHyperLink(final String txt, final String link) {
        this.text = txt;
        this.url = link;

        try {
            this.isSupported = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        } catch (Exception e) {
            this.isSupported = false;
        }

        this.setText(false);
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(final MouseEvent e) {
                setText(isSupported);
                if (isSupported) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                setText(false);
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(JLabelHyperLink.this.url));
                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(JLabelHyperLink.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void setText(final boolean b) {
        if (!b) {
            setText("<html><font color=blue><u>" + text);
        } else {
            setText("<html><font color=red><u>" + text);
        }
    }
}
