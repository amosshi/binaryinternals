/*
 * JPanelForTree.java    Nov 07, 2009, 08:41
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.xmltree.XMLTreeTable;
import org.freeinternals.commonlib.ui.xmltree.XMLTreeTableModel;
import org.xml.sax.InputSource;

/**
 *
 * @author Amos Shi
 */
public class JXMLViewer extends JPanel {

    private static final long serialVersionUID = 4876543219876500005L;
    public final JTabbedPane tabbedPane;

    public JXMLViewer(InputStream xml){
        this.tabbedPane = new JTabbedPane();

        try {
            XMLTreeTable treeTable = new XMLTreeTable(new XMLTreeTableModel(new InputSource(xml)));
            this.tabbedPane.addTab("XML View", new JScrollPaneTreeTable(treeTable));
        } catch (Exception ex) {
            //Logger.getLogger(JXMLViewer.class.getName()).log(Level.SEVERE, null, ex);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            // The title
            JLabel label = new JLabel("Exception occured when parsing the XML data.");
            label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            label.setForeground(Color.red);
            panel.add(label, BorderLayout.NORTH);

            // The exception
            ByteArrayOutputStream output = new ByteArrayOutputStream(2096);
            ex.printStackTrace(new PrintStream(output));

            JTextArea textException = new JTextArea();
            textException.setLineWrap(true);
            textException.setEditable(false);
            textException.setText(output.toString());

            panel.add(new JScrollPane(textException), BorderLayout.CENTER);

            //
            this.tabbedPane.addTab("XML View", panel);
        }

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

    @SuppressWarnings("PackageVisibleInnerClass")
    class JScrollPaneTreeTable extends JScrollPane {

        private static final long serialVersionUID = 4876543219876500005L;

        private JScrollPaneTreeTable(Component component) {
            super(component);
        }

        @Override
        public void setColumnHeaderView(Component view) {
        }
    }
}
