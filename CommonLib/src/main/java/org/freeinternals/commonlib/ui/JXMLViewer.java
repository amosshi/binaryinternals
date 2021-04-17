/*
 * JPanelForTree.java    Nov 07, 2009, 08:41
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.io.InputStream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * Panel based XML data viewer.
 *
 * @author Amos Shi
 */
public class JXMLViewer extends JPanel {

    private static final long serialVersionUID = 4876543219876500005L;
    /**
     * Tabbed Pane for "XML View" and "XML Plain Text".
     */
    private final JTabbedPane tabbedPane;

    /**
     * Constructor.
     *
     * @param xml XML data to be displayed
     */
    public JXMLViewer(final InputStream xml) {
        this.tabbedPane = new JTabbedPane();
        if (xml instanceof PosDataInputStream) {
            byte[] buf = ((PosDataInputStream) xml).getBuf();
            StringBuilder sb = new StringBuilder(buf.length + 1);
            for (byte b : buf) {
                sb.append((char) b);
            }

            JTextArea textPlainText = new JTextArea(sb.toString());
            textPlainText.setLineWrap(true);
            textPlainText.setEditable(false);
            tabbedPane.addTab("XML Plain Text", new JScrollPane(textPlainText));
        }

        this.setLayout(new BorderLayout());
        this.add(this.tabbedPane, BorderLayout.CENTER);
    }
}
