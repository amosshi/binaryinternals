/*
 * Main.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.plugin.PluginManager;

/**
 *
 *
 * @author Amos Shi
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 4876543219876500001L;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    /**
     * Size ratio of the pop-up window and its parent.
     */
    private static final float POPUP_RATIO = 0.8f;
    private static final String URI_HOMEPAGE = "https://github.com/amosshi/binaryinternals";
    private static final String TITLE = "Binary Internals Viewer ";
    private static final String MASS_TEST_MODE_PROPERTY = "org.binaryinternals.masstestmode";
    private static final int RECENT_FILES_MAX = 10;
    private static final int EXIT_OK = 0;
    private static final int EXIT_FAILED = 1;
    /**
     * Name of the detail popup windows, to find them among the owned windows.
     */
    private static final String POPUP_NAME = "detailPopup";

    private final JPanel filedropPanel = new JPanel();
    private final transient SequencedSet<Path> recentFiles = new LinkedHashSet<>();
    private final JMenu menuFileRecentFile = new JMenu("Recent Files");
    private final JFileChooser fileChooser = new JFileChooser();
    private JSplitPaneFile contentPane = null;
    private transient SwingWorker<FileFormat, Void> loader = null;

    private Main() {
        this.setTitle(TITLE + PluginManager.getPluggedExtensions());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        PluginManager.initChooseFilters(this.fileChooser);
        this.centerJFrame();
        this.createMenu();
        this.filedropPanel.setBackground(Color.WHITE);
        this.filedropPanel.setLayout(new BorderLayout());
        this.add(this.filedropPanel, BorderLayout.CENTER);

        this.enableFileDrop(this.filedropPanel);
        this.enableFileDrop(this.getJMenuBar());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final Main main = new Main();
            main.setVisible(true);
            if (args.length > 0) {
                main.openCommandLineFile(args[0]);
            }
        });
    }

    /**
     * Accept file name at command line.
     */
    private void openCommandLineFile(final String fileName) {
        Path file = null;
        try {
            file = Path.of(fileName);
        } catch (InvalidPathException _) {
            LOGGER.log(Level.WARNING, "The provided file name is invalid: filename={0}", fileName);
        }
        if (file != null && Files.exists(file)) {
            this.openFile(file, succeeded -> exitIfMassTestMode(succeeded ? EXIT_OK : EXIT_FAILED));
        } else {
            LOGGER.log(Level.WARNING, "The provided file does not exist: filename={0}", fileName);
            exitIfMassTestMode(EXIT_FAILED);
        }
    }

    /**
     * Exit immediately for mass test mode.
     *
     * @param status Exit status, non-zero when the file failed to open
     */
    private static void exitIfMassTestMode(final int status) {
        if (isMassTestMode()) {
            System.exit(status);
        }
    }

    private static boolean isMassTestMode() {
        return Boolean.getBoolean(MASS_TEST_MODE_PROPERTY);
    }

    /**
     * Set a {@code JFrame} window to screen center.
     */
    private void centerJFrame() {
        // Set main window size
        // The usable area, excluding the taskbar and the menu bar
        final Rectangle d = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setSize(
                (int) (d.getWidth() * POPUP_RATIO),
                (int) (d.getHeight() * POPUP_RATIO));

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
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuItemFileOpen.addActionListener(_ -> menuFileOpen());
        menuFile.add(menuItemFileOpen);

        // File --> Close
        final JMenuItem menuItemFileClose = new JMenuItem("Close", UIManager.getIcon("InternalFrame.iconifyIcon"));
        menuItemFileClose.setMnemonic(KeyEvent.VK_C);
        menuItemFileClose.addActionListener(_ -> closeFile());
        menuFile.add(menuItemFileClose);

        menuFile.addSeparator();
        // File --> Recent Files
        this.menuFileRecentFile.setMnemonic(KeyEvent.VK_R);
        menuFile.add(this.menuFileRecentFile);
        //
        menuFile.addSeparator();

        // File --> Exit
        final JMenuItem menuItemFileExit = new JMenuItem("Exit");
        menuItemFileExit.setMnemonic(KeyEvent.VK_E);
        menuItemFileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuItemFileExit.addActionListener(_ -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        menuFile.add(menuItemFileExit);

        // Help
        final JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // Help --> Homepage
        final JMenuItem menuItemHelpHomepage = new JMenuItem("Homepage", UIManager.getIcon("FileView.computerIcon"));
        menuItemHelpHomepage.setMnemonic(KeyEvent.VK_H);
        menuItemHelpHomepage.addActionListener(_ -> menuHelpHomepage());
        menuHelp.add(menuItemHelpHomepage);

        // Help --> Plugins
        final JMenuItem menuItemHelpPlugins = new JMenuItem("Plug-ins");
        menuItemHelpPlugins.setMnemonic(KeyEvent.VK_P);
        menuItemHelpPlugins.addActionListener(_ -> menuHelpPlugins());
        menuHelp.add(menuItemHelpPlugins);

        // Help --> Plugins
        final JMenuItem menuItemHelpIcons = new JMenuItem("Icons");
        menuItemHelpIcons.setMnemonic(KeyEvent.VK_I);
        menuItemHelpIcons.addActionListener(_ -> menuHelpIcons());
        menuHelp.add(menuItemHelpIcons);

        // Help --> About
        final JMenuItem menuItemHelpAbout = new JMenuItem("About");
        menuItemHelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItemHelpAbout.addActionListener(_ -> menuHelpAbout());
        menuHelp.add(menuItemHelpAbout);
    }

    /**
     * Support drag and drop. Only the 1st file are handled, if more than 1 file
     * are dropped.
     */
    private void enableFileDrop(Component c) {
        c.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                if (!evt.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    evt.rejectDrop();
                    return;
                }
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    final Object drops = evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (drops instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof File dropped) {
                        openFile(dropped.toPath());
                        evt.dropComplete(true);
                    } else {
                        evt.dropComplete(false);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    LOGGER.warning(e.getMessage());
                    evt.dropComplete(false);
                }
            }
        });
    }

    private void menuFileOpen() {
        // The chooser is reused, so it remembers the last folder
        final int returnVal = this.fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.openFile(this.fileChooser.getSelectedFile().toPath());
        }
    }

    /**
     * Open the file in the background.
     */
    private void openFile(final Path file) {
        this.openFile(file, _ -> { });
    }

    /**
     * Parse the file in the background, then show it on the event thread.
     *
     * @param path File to open
     * @param onFinished Called on the event thread with whether the file is shown
     */
    private void openFile(final Path path, final Consumer<Boolean> onFinished) {
        final Path file = path.toAbsolutePath().normalize();
        // Close any open or loading file first
        this.closeFile();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final SwingWorker<FileFormat, Void> worker = new SwingWorker<>() {
            @Override
            protected FileFormat doInBackground() throws Exception {
                return PluginManager.getFile(file);
            }

            @Override
            protected void done() {
                // A newer open superseded this one, possibly after it already completed
                final boolean current = loader == this;
                boolean succeeded = false;
                try {
                    if (current && !isCancelled()) {
                        showFile(file, get());
                        succeeded = true;
                    }
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    showOpenError(file, ex.getCause());
                } catch (RuntimeException | LinkageError | StackOverflowError ex) {
                    // Building the tree of a deeply nested file can overflow the stack
                    showOpenError(file, ex);
                } finally {
                    if (current) {
                        // Release the result, the shown file is held by the content pane
                        loader = null;
                        setCursor(Cursor.getDefaultCursor());
                    }
                    onFinished.accept(succeeded);
                }
            }
        };
        this.loader = worker;
        worker.execute();
    }

    private void showFile(final Path file, final FileFormat format) {
        this.contentPane = new JSplitPaneFile(format, this);
        this.updateRecentFiles(file);

        this.setTitle(String.join(" - ", fileName(file), file.toString(), this.contentPane.getFileFormatName(), TITLE));
        this.filedropPanel.add(this.contentPane, BorderLayout.CENTER);

        // Resize after adding new content
        this.revalidate();
        this.repaint();
    }

    private static String fileName(final Path file) {
        return String.valueOf(file.getFileName());
    }

    private void showOpenError(final Path file, final Throwable ex) {
        this.recentFiles.remove(file);
        this.refreshRecentFilesMenu();

        String message;
        if (ex instanceof OutOfMemoryError) {
            message = "Out of memory while opening " + fileName(file);
        } else {
            message = ex.getMessage();
            if ((message == null || message.isBlank()) && ex.getCause() != null) {
                message = ex.getCause().getMessage();
            }
            if (message == null || message.isBlank()) {
                message = ex.toString();
            }
        }
        LOGGER.log(Level.SEVERE, message, ex);
        if (isMassTestMode()) {
            // A modal dialog would block the exit until the test times out
            return;
        }
        JOptionPane.showMessageDialog(
                this,
                message,
                this.getTitle(),
                JOptionPane.ERROR_MESSAGE);
    }

    private void updateRecentFiles(final Path file) {
        // Re-adding moves the file to the most recent position, which is listed first
        this.recentFiles.addFirst(file);
        while (this.recentFiles.size() > RECENT_FILES_MAX) {
            this.recentFiles.removeLast();
        }
        this.refreshRecentFilesMenu();
    }

    private void refreshRecentFilesMenu() {
        this.menuFileRecentFile.removeAll();
        this.recentFiles.forEach(recent -> {
            final JMenuItem item = new JMenuItem(recent.toString());
            item.addActionListener(_ -> openFile(recent));
            this.menuFileRecentFile.add(item);
        });
    }

    private void closeFile() {
        // Stop any file still loading, so it does not show up after closing
        if (this.loader != null) {
            this.loader.cancel(true);
            this.loader = null;
            this.setCursor(Cursor.getDefaultCursor());
        }

        this.setTitle(TITLE + PluginManager.getPluggedExtensions());
        // Detail popups show parts of the closed file
        for (Window window : this.getOwnedWindows()) {
            if (POPUP_NAME.equals(window.getName())) {
                window.dispose();
            }
        }

        // Clear Content
        if (this.contentPane != null) {
            this.filedropPanel.remove(this.contentPane);
            this.validate();
        }
        this.contentPane = null;

        // Refresh the view
        this.revalidate();
        this.repaint();
    }

    private void menuHelpAbout() {
        final JDialogAbout about = new JDialogAbout(this, "About");
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }

    private void menuHelpHomepage() {
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                throw new UnsupportedOperationException("Opening a browser is not supported: " + URI_HOMEPAGE);
            }
            Desktop.getDesktop().browse(URI.create(URI_HOMEPAGE));
        } catch (UnsupportedOperationException | IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void menuHelpIcons() {
        final JDialogIcons icons = new JDialogIcons(this, "Icons");
        icons.setLocationRelativeTo(this);
        icons.setVisible(true);
    }

    private void menuHelpPlugins() {
        final JDialogPlugins plugins = new JDialogPlugins(this, "Plug-ins");
        plugins.setLocationRelativeTo(this);
        plugins.setVisible(true);
    }

    /**
     * Show a popup window with given message. A panel can be in only one
     * window, so the popup already showing the panel is reused.
     *
     * @param frame Parent window
     * @param panel Content in panel
     * @param title Popup window title
     */
    static void showPopup(final JFrame frame, final JPanel panel, final String title) {
        if (frame == null || panel == null) {
            return;
        }

        if (SwingUtilities.getWindowAncestor(panel) instanceof JDialog shown
                && POPUP_NAME.equals(shown.getName()) && shown.isDisplayable()) {
            shown.toFront();
            return;
        }

        final JDialog popup = new JDialog(frame, title);
        popup.setName(POPUP_NAME);
        popup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        popup.setSize(
                (int) (frame.getWidth() * POPUP_RATIO),
                (int) (frame.getHeight() * POPUP_RATIO));
        popup.setLayout(new BorderLayout());
        popup.add(panel, BorderLayout.CENTER);
        popup.setLocationRelativeTo(frame);
        popup.setVisible(true);
    }
}
