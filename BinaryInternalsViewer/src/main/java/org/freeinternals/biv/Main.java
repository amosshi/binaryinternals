/*
 * Main.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.freeinternals.biv.plugin.PluginManager;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.FileFormatException;

/**
 * 
 * 
 * @author Amos Shi
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 4876543219876500000L;
    private final JPanel filedropPanel = new JPanel();
    private JSplitPaneFile contentPane = null;

    @SuppressWarnings("LeakingThisInConstructor")
    private Main() {
        this.setTitle("Binary Internals Viewer " + PluginManager.getPlugedExtensions());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UITool.centerJFrame(this);
        this.createMenu();
        this.filedropPanel.setBackground(Color.WHITE);
        this.filedropPanel.setLayout(new BorderLayout());
        this.add(this.filedropPanel, BorderLayout.CENTER);

        this.enalbeFileDrop();
        this.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    private void createMenu() {
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // File
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        // File --> Open
        final JMenuItem menuItem_FileOpen = new JMenuItem("Open...", UIManager.getIcon("FileView.directoryIcon"));
        menuItem_FileOpen.setMnemonic(KeyEvent.VK_O);
        menuItem_FileOpen.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItem_FileOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_FileOpen();
            }
        });
        menuFile.add(menuItem_FileOpen);

        // File --> Close
        final JMenuItem menuItem_FileClose = new JMenuItem("Close", UIManager.getIcon("InternalFrame.iconifyIcon"));
        menuItem_FileClose.setMnemonic(KeyEvent.VK_C);
        menuItem_FileClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                closeFile();
            }
        });
        menuFile.add(menuItem_FileClose);

        //
        //menuFile.addSeparator();

        // File --> Recent Files
        //JMenu menu_FileRecentFile = new JMenu("Recent Files");
        //menu_FileRecentFile.setMnemonic(KeyEvent.VK_R);
        //menuFile.add(menu_FileRecentFile);

        //
        menuFile.addSeparator();

        // File --> Exit
        final JMenuItem menuItem_FileExit = new JMenuItem("Exit", UIManager.getIcon("Table.ascendingSortIcon"));
        menuItem_FileExit.setMnemonic(KeyEvent.VK_X);
        menuItem_FileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X,
                ActionEvent.ALT_MASK));
        menuItem_FileExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        menuFile.add(menuItem_FileExit);

        // Help
        final JMenu menuHelp = new JMenu("Help");
        menuFile.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // Help --> Homepage
        final JMenuItem menuItem_HelpHomepage = new JMenuItem("Homepage", UIManager.getIcon("FileView.computerIcon"));
        menuItem_HelpHomepage.setMnemonic(KeyEvent.VK_P);
        menuItem_HelpHomepage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_HelpHomepage();
            }
        });
        menuHelp.add(menuItem_HelpHomepage);

        // Help --> Plugins
        final JMenuItem menuItem_HelpPlugins = new JMenuItem("Plug-ins");
        menuItem_HelpPlugins.setMnemonic(KeyEvent.VK_A);
        menuItem_HelpPlugins.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_HelpPlugins();
            }
        });
        menuHelp.add(menuItem_HelpPlugins);

        // Help --> About
        final JMenuItem menuItem_HelpAbout = new JMenuItem("About");
        menuItem_HelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItem_HelpAbout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_HelpAbout();
            }
        });
        menuHelp.add(menuItem_HelpAbout);
    }

    private void enalbeFileDrop(){
        // only the 1st file are handled
        new FileDrop(this.filedropPanel, new FileDrop.Listener() {

            public void filesDropped(java.io.File[] files) {
                openFile(files[0]);
            }
        });
        //new FileDrop(System.out, this.getJMenuBar(), new FileDrop.Listener() {
        new FileDrop(this.getJMenuBar(), new FileDrop.Listener() {

            public void filesDropped(java.io.File[] files) {
                openFile(files[0]);
            }
        });
    }

    private void menu_FileOpen() {

        final JFileChooser chooser = new JFileChooser();
        PluginManager.initChooseFilters(chooser);

        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.openFile(chooser.getSelectedFile());
        }
    }

    private void openFile(final File file) {
        this.closeFile();      // Close any open file first

        try {
            this.contentPane = new JSplitPaneFile(file, this);
        } catch (FileFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, t);
            JOptionPane.showMessageDialog(
                    this,
                    t.getMessage(),
                    this.getTitle(),
                    JOptionPane.ERROR_MESSAGE);
        }
        //this.add(this.contentPane, BorderLayout.CENTER);
        this.filedropPanel.add(this.contentPane, BorderLayout.CENTER);

        // Resize after adding new content
        this.setSize(this.getWidth() + 2, this.getHeight());
        this.setSize(this.getWidth() - 2, this.getHeight());
    }

    private void closeFile() {
        // Clear Content
        if (this.contentPane != null) {
            //this.remove(this.contentPane);
            this.filedropPanel.remove(this.contentPane);
            this.validate();
        }
        this.contentPane = null;

        // Refersh the view
        this.setSize(this.getWidth() - 1, this.getHeight());
    }

    private void menu_HelpPlugins() {
        final JDialogPlugins plugins = new JDialogPlugins(this, "Plug-ins");
        plugins.setLocationRelativeTo(this);
        plugins.setVisible(true);
    }
    
    private void menu_HelpAbout() {
        final JDialogAbout about = new JDialogAbout(this, "About");
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }

    private void menu_HelpHomepage() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/amosshi/freeinternals"));
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}
