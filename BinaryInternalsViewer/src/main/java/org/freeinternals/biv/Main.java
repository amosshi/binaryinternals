/*
 * Main.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
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

/**
 *
 *
 * @author Amos Shi
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 4876543219876500000L;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String TITLE = "Binary Internals Viewer ";
    private static final String TITLE_EXT = " - " + TITLE;
    private final JPanel filedropPanel = new JPanel();
    private final Set<File> recentFiles = new HashSet<>();
    private final JMenu menu_FileRecentFile = new JMenu("Recent Files");
    private JSplitPaneFile contentPane = null;

    @SuppressWarnings("LeakingThisInConstructor")
    private Main() {
        this.setTitle(TITLE + PluginManager.getPlugedExtensions());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UITool.centerJFrame(this);
        this.createMenu();
        this.filedropPanel.setBackground(Color.WHITE);
        this.filedropPanel.setLayout(new BorderLayout());
        this.add(this.filedropPanel, BorderLayout.CENTER);

        this.enalbeFileDrop(this.filedropPanel);
        this.enalbeFileDrop(this.getJMenuBar());
        this.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
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
        menuItem_FileOpen.addActionListener((final ActionEvent e) -> {
            menu_FileOpen();
        });
        menuFile.add(menuItem_FileOpen);

        // File --> Close
        final JMenuItem menuItem_FileClose = new JMenuItem("Close", UIManager.getIcon("InternalFrame.iconifyIcon"));
        menuItem_FileClose.setMnemonic(KeyEvent.VK_C);
        menuItem_FileClose.addActionListener((final ActionEvent e) -> {
            closeFile();
        });
        menuFile.add(menuItem_FileClose);

        menuFile.addSeparator();
        // File --> Recent Files
        this.menu_FileRecentFile.setMnemonic(KeyEvent.VK_R);
        menuFile.add(this.menu_FileRecentFile);
        //
        menuFile.addSeparator();

        // File --> Exit
        final JMenuItem menuItem_FileExit = new JMenuItem("Exit", UIManager.getIcon("Table.ascendingSortIcon"));
        menuItem_FileExit.setMnemonic(KeyEvent.VK_X);
        menuItem_FileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X,
                ActionEvent.ALT_MASK));
        menuItem_FileExit.addActionListener((final ActionEvent e) -> {
            Main.this.dispatchEvent((new WindowEvent(Main.this, WindowEvent.WINDOW_CLOSING)));
        });
        menuFile.add(menuItem_FileExit);

        // Help
        final JMenu menuHelp = new JMenu("Help");
        menuFile.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // Help --> Homepage
        final JMenuItem menuItem_HelpHomepage = new JMenuItem("Homepage", UIManager.getIcon("FileView.computerIcon"));
        menuItem_HelpHomepage.setMnemonic(KeyEvent.VK_P);
        menuItem_HelpHomepage.addActionListener((final ActionEvent e) -> {
            menu_HelpHomepage();
        });
        menuHelp.add(menuItem_HelpHomepage);

        // Help --> Plugins
        final JMenuItem menuItem_HelpPlugins = new JMenuItem("Plug-ins");
        menuItem_HelpPlugins.setMnemonic(KeyEvent.VK_A);
        menuItem_HelpPlugins.addActionListener((final ActionEvent e) -> {
            menu_HelpPlugins();
        });
        menuHelp.add(menuItem_HelpPlugins);

        // Help --> About
        final JMenuItem menuItem_HelpAbout = new JMenuItem("About");
        menuItem_HelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItem_HelpAbout.addActionListener((final ActionEvent e) -> {
            menu_HelpAbout();
        });
        menuHelp.add(menuItem_HelpAbout);
    }

    /**
     * Support drag and drop. Only the 1st file are handled, if more than 1 file
     * are dropped.
     */
    private void enalbeFileDrop(Component c) {
        c.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    openFile(droppedFiles.get(0));
                } catch (UnsupportedFlavorException | IOException e) {
                    LOGGER.warning(e.getMessage());
                }
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
        // Close any open file first if exists
        this.closeFile();

        // Update Recent files menu item
        this.menu_FileRecentFile.removeAll();
        this.recentFiles.add(file);
        this.recentFiles.forEach(recent -> {
            this.menu_FileRecentFile.add(new JMenuItem(new AbstractAction(recent.getAbsolutePath()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFile(recent);
                }
            }));
        });

        // Add the file to UI
        try {
            this.contentPane = new JSplitPaneFile(file, this);
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.ERROR_MESSAGE);
        }

        this.setTitle(file.getName() + TITLE_EXT);
        this.filedropPanel.add(this.contentPane, BorderLayout.CENTER);

        // Resize after adding new content
        this.setSize(this.getWidth() + 2, this.getHeight());
        this.setSize(this.getWidth() - 2, this.getHeight());
    }

    private void closeFile() {
        this.setTitle(TITLE + PluginManager.getPlugedExtensions());
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
