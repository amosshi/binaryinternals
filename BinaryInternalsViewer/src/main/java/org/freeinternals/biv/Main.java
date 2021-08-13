/*
 * Main.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import javax.swing.WindowConstants;
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
    private static final String URI_HOMEPAGE = "https://github.com/amosshi/freeinternals";
    private static final String TITLE = "Binary Internals Viewer ";
    private static final String TITLE_EXT = " - " + TITLE;
    private static final String MASS_TEST_MODE_PROPERTY = "org.freeinternals.masstestmode";

    private final JPanel filedropPanel = new JPanel();
    private final Set<File> recentFiles = new HashSet<>();
    private final JMenu menuFileRecentFile = new JMenu("Recent Files");
    private JSplitPaneFile contentPane = null;

    @SuppressWarnings("LeakingThisInConstructor")
    @SuppressFBWarnings(value = "DM_EXIT", justification = "This is desigend for mass test mode")
    private Main(final String[] args) {
        this.setTitle(TITLE + PluginManager.getPlugedExtensions());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.centerJFrame();
        this.createMenu();
        this.filedropPanel.setBackground(Color.WHITE);
        this.filedropPanel.setLayout(new BorderLayout());
        this.add(this.filedropPanel, BorderLayout.CENTER);

        this.enalbeFileDrop(this.filedropPanel);
        this.enalbeFileDrop(this.getJMenuBar());
        this.setVisible(true);

        // Accept file name at command line
        if (args.length > 0) {
            final String fileName = args[0];
            final File file = new File(fileName);
            if (file.exists()) {
                try {
                    this.openFile(file);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, String.format("Failed to open the file. filename=%s", fileName), e);
                }
            } else {
                LOGGER.log(Level.WARNING, "The provided file does not exist: filename={0}", fileName);
            }

            // Exit immediately for mass test mode
            if (Boolean.TRUE.equals(Boolean.valueOf(System.getProperty(Main.MASS_TEST_MODE_PROPERTY, "false")))) {
                System.exit(0);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(args).setVisible(true));
    }

    /**
     * Set a {@code JFrame} window to screen center.
     */
    private void centerJFrame() {
        // Set main window size
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(
                (int) (d.getWidth() * UITool.POPUP_RATIO),
                (int) (d.getHeight() * UITool.POPUP_RATIO));

        // Center the main window
        this.setLocationRelativeTo(null);
    }

    private void createMenu() {
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // File
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        // File --> Open
        final JMenuItem menuItemFileOpen = new JMenuItem("Open...", UIManager.getIcon("FileView.directoryIcon"));
        menuItemFileOpen.setMnemonic(KeyEvent.VK_O);
        menuItemFileOpen.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItemFileOpen.addActionListener((final ActionEvent e) -> menuFileOpen());
        menuFile.add(menuItemFileOpen);

        // File --> Close
        final JMenuItem menuItemFileClose = new JMenuItem("Close", UIManager.getIcon("InternalFrame.iconifyIcon"));
        menuItemFileClose.setMnemonic(KeyEvent.VK_C);
        menuItemFileClose.addActionListener((final ActionEvent e) -> closeFile());
        menuFile.add(menuItemFileClose);

        menuFile.addSeparator();
        // File --> Recent Files
        this.menuFileRecentFile.setMnemonic(KeyEvent.VK_R);
        menuFile.add(this.menuFileRecentFile);
        //
        menuFile.addSeparator();

        // File --> Exit
        final JMenuItem menuItemFileExit = new JMenuItem("Exit", UIManager.getIcon("Table.ascendingSortIcon"));
        menuItemFileExit.setMnemonic(KeyEvent.VK_X);
        menuItemFileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X,
                ActionEvent.ALT_MASK));
        menuItemFileExit.addActionListener((final ActionEvent e)
                -> Main.this.dispatchEvent((new WindowEvent(Main.this, WindowEvent.WINDOW_CLOSING)))
        );
        menuFile.add(menuItemFileExit);

        // Help
        final JMenu menuHelp = new JMenu("Help");
        menuFile.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // Help --> Homepage
        final JMenuItem menuItemHelpHomepage = new JMenuItem("Homepage", UIManager.getIcon("FileView.computerIcon"));
        menuItemHelpHomepage.setMnemonic(KeyEvent.VK_P);
        menuItemHelpHomepage.addActionListener((final ActionEvent e) -> menuHelpHomepage());
        menuHelp.add(menuItemHelpHomepage);

        // Help --> Plugins
        final JMenuItem menuItemHelpPlugins = new JMenuItem("Plug-ins");
        menuItemHelpPlugins.setMnemonic(KeyEvent.VK_A);
        menuItemHelpPlugins.addActionListener((final ActionEvent e) -> menuHelpPlugins());
        menuHelp.add(menuItemHelpPlugins);

        // Help --> About
        final JMenuItem menuItemHelpAbout = new JMenuItem("About");
        menuItemHelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItemHelpAbout.addActionListener((final ActionEvent e) -> menuHelpAbout());
        menuHelp.add(menuItemHelpAbout);
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

    private void menuFileOpen() {
        final JFileChooser chooser = new JFileChooser();
        PluginManager.initChooseFilters(chooser);

        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.openFile(chooser.getSelectedFile());
        }
    }

    @SuppressWarnings("java:S1181")  // Throwable and Error should not be caught  --- We need to cache all exception here
    private void openFile(final File file) {
        // Close any open file first if exists
        this.closeFile();

        // Update Recent files menu item
        this.menuFileRecentFile.removeAll();
        this.recentFiles.add(file);
        this.recentFiles.forEach(recent
                -> this.menuFileRecentFile.add(new JMenuItem(new AbstractAction(recent.getAbsolutePath()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openFile(recent);
                    }
                }))
        );

        // Add the file to UI
        try {
            this.contentPane = new JSplitPaneFile(file, this);
        } catch (Throwable ex) {
            String message = ex.getMessage();
            if (message == null || message.trim().length() < 1) {
                message = ex.getCause() != null ? ex.getCause().getMessage() : "";
            }
            LOGGER.log(Level.SEVERE, message, ex);
            JOptionPane.showMessageDialog(
                    this,
                    message,
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
            this.filedropPanel.remove(this.contentPane);
            this.validate();
        }
        this.contentPane = null;

        // Refersh the view
        this.setSize(this.getWidth() - 1, this.getHeight());
    }

    private void menuHelpPlugins() {
        final JDialogPlugins plugins = new JDialogPlugins(this, "Plug-ins");
        plugins.setLocationRelativeTo(this);
        plugins.setVisible(true);
    }

    private void menuHelpAbout() {
        final JDialogAbout about = new JDialogAbout(this, "About");
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }

    private void menuHelpHomepage() {
        try {
            Desktop.getDesktop().browse(new URI(URI_HOMEPAGE));
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}
