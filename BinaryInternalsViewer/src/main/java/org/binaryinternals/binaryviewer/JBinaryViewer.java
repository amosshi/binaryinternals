/*
 * JBinaryViewer.java    September 3, 2007, 12:07 AM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.binaryviewer;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SpringLayout;

/**
 * Viewer for binary data. It contains three columns: row numbers, data in HEX,
 * data in ASCII.
 *
 * @author Amos Shi
 */
public final class JBinaryViewer extends JPanel {

    private static final long serialVersionUID = 4876543219876500005L;

    /**
     * Height for each row.
     */
    public static final int ITEM_HEIGHT = 22;

    /**
     * Number of bytes to be shown in one row.
     */
    public static final int ROW_ITEM_MAX = 16;

    /**
     * 0-based row item index.
     */
    public static final int ROW_ITEM_MAX_INDEX = ROW_ITEM_MAX - 1;

    /**
     * Spare space in bottom.
     */
    private static final int ROW_EMPTYROW_COUNT = 10;

    /**
     * Constant value for <code>-4</code>.
     */
    private static final int MINUS_4 = -4;

    /**
     * Column 1: row viewer.
     */
    private final JRowViewer rowViewer;

    /**
     * Column 2: Data viewer in HEX format.
     */
    private final JRawDataViewer rawViewer;

    /**
     * Column 3: Data viewer in ASCII format.
     */
    private final JAsciiDataViewer asciiViewer;

    /**
     * Binary data to be shown.
     */
    private byte[] data = null;

    /**
     * Vertical scroll bar for paging.
     */
    private final JScrollBar vBar;

    /**
     * Max number of rows will be shown, due to the {@link #data} size.
     */
    private int rowMax;

    /**
     * Start index to be high-lighted.
     *
     */
    private int selectedStartIndex = 0;

    /**
     * Length to be high-lighted.
     *
     */
    private int selectedLength = 0;

    /**
     * Guards {@link #updateViewContent()} against re-entry from scroll bar events.
     */
    private boolean updating = false;

    /**
     * Fraction of a wheel step not yet applied, for high-resolution wheels.
     */
    private double wheelRemainder = 0;

    /**
     * Keyboard navigation shared by the data viewers.
     */
    private final transient KeyListener keyboard = new KeyAdapter() {
        @Override
        public void keyPressed(final KeyEvent e) {
            final JScrollBar bar = JBinaryViewer.this.vBar;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_HOME -> bar.setValue(bar.getMinimum());
                case KeyEvent.VK_END -> bar.setValue(bar.getMaximum());
                case KeyEvent.VK_UP -> bar.setValue(bar.getValue() - 1);
                case KeyEvent.VK_DOWN -> bar.setValue(bar.getValue() + 1);
                case KeyEvent.VK_LEFT, KeyEvent.VK_PAGE_UP -> bar.setValue(bar.getValue() - bar.getVisibleAmount());
                case KeyEvent.VK_RIGHT, KeyEvent.VK_PAGE_DOWN -> bar.setValue(bar.getValue() + bar.getVisibleAmount());
                default -> {
                    // Other keys are ignored
                }
            }
        }
    };

    /**
     * Constructor.
     */
    public JBinaryViewer() {
        this.setLayout(new BorderLayout());
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                updateViewContent();
            }
        });
        this.addMouseWheelListener(this::mouseWheelMoved);

        // Vertical Bar
        this.vBar = new JScrollBar();
        this.vBar.addAdjustmentListener((final AdjustmentEvent e) -> updateViewContent());
        this.vBar.setVisible(false);

        this.add(this.vBar, BorderLayout.EAST);

        // Content Panel
        final JPanel panel = new JPanel();
        final SpringLayout panelLayout = new SpringLayout();
        int left;
        int right;

        panel.setLayout(panelLayout);

        this.rowViewer = new JRowViewer();
        this.rawViewer = new JRawDataViewer();
        this.rawViewer.addKeyListener(this.keyboard);
        this.asciiViewer = new JAsciiDataViewer();
        this.asciiViewer.addKeyListener(this.keyboard);

        panel.add(this.rowViewer);
        panel.add(this.rawViewer);
        panel.add(this.asciiViewer);

        panelLayout.putConstraint(SpringLayout.WEST, this.rowViewer, 2, SpringLayout.WEST, panel);
        panelLayout.putConstraint(SpringLayout.NORTH, this.rowViewer, 2, SpringLayout.NORTH, panel);
        panelLayout.putConstraint(SpringLayout.SOUTH, this.rowViewer, MINUS_4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.rowViewer, JRowViewer.WIDTH_VALUE, SpringLayout.WEST, panel);

        left = 2 + JRowViewer.WIDTH_VALUE + 2;
        right = left + JRawDataViewer.WIDTH_VALUE;
        panelLayout.putConstraint(SpringLayout.WEST, this.rawViewer, left, SpringLayout.WEST, panel);
        panelLayout.putConstraint(SpringLayout.NORTH, this.rawViewer, 2, SpringLayout.NORTH, panel);
        panelLayout.putConstraint(SpringLayout.SOUTH, this.rawViewer, MINUS_4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.rawViewer, right, SpringLayout.WEST, panel);

        left = right + 2;
        right = left + JAsciiDataViewer.WIDTH_VALUE;
        panelLayout.putConstraint(SpringLayout.WEST, this.asciiViewer, left, SpringLayout.WEST, panel);
        panelLayout.putConstraint(SpringLayout.NORTH, this.asciiViewer, 2, SpringLayout.NORTH, panel);
        panelLayout.putConstraint(SpringLayout.SOUTH, this.asciiViewer, MINUS_4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.asciiViewer, right, SpringLayout.WEST, panel);

        this.add(panel, BorderLayout.CENTER);
    }

    /**
     * Set the binary data to be displayed. The array is shared, not copied, and
     * must not be modified while it is displayed.
     *
     * @param bytes Binary data to be displayed
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Avoid copying a large file")
    public void setData(final byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.data = bytes;

        // Calc the max row count
        this.rowMax = this.getRowMax();
        this.vBar.setMaximum(this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT);
        this.vBar.setValue(0);

        this.updateViewContent();
    }

    private int getRowMax() {
        return (this.data != null)
                ? this.getRowCount(this.data.length)
                : 0;
    }

    /**
     * Return 1-based row count number.
     *
     * @param number to explain
     * @return 1-based row number
     */
    private int getRowCount(final int number) {
        return number > 0 ? Math.ceilDiv(number, ROW_ITEM_MAX) : 0;
    }

    private int getExtent() {
        return Math.ceilDiv(this.getHeight(), ITEM_HEIGHT);
    }

    private void updateViewContent() {
        if (this.updating) {
            return;
        }
        this.updating = true;
        try {
            this.doUpdateViewContent();
        } finally {
            this.updating = false;
        }
    }

    private void doUpdateViewContent() {
        // Update Extent
        final int extent = this.getExtent();
        if (extent <= 0) {
            return;                                                             // The window Hight is (nearly) zero
        }
        this.vBar.setVisibleAmount(extent);

        if ((extent + this.vBar.getValue()) > (this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT)) {
            final int diff = (this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT) - extent;
            this.vBar.setValue(Math.max(diff, 0));
        }
        this.vBar.setVisible(extent < this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT);

        // Revise row viewer, raw data viewer, ASCII data viewer
        this.rowViewer.setData(this.vBar.getValue(), extent, this.rowMax);

        int startPos = 0;
        int dataSize = 0;
        if (this.data != null) {
            startPos = this.vBar.getValue() * JBinaryViewer.ROW_ITEM_MAX;
            dataSize = Math.min(extent * JBinaryViewer.ROW_ITEM_MAX, this.data.length - startPos);
        }

        // Selection relative to the window, limited to the window for performance
        final int windowSelStart = this.selectedStartIndex - startPos;
        final int selStart = Math.max(windowSelStart, 0);
        final int selLength = Math.clamp((long) windowSelStart + this.selectedLength - selStart, 0, Math.max(dataSize, 0));

        this.rawViewer.setContent(this.data, startPos, dataSize, selStart, selLength);
        this.asciiViewer.setContent(this.data, startPos, dataSize, selStart, selLength);
    }

    /**
     * Selects the bytes between the specified start position and length.
     *
     * @param selectionStart the start position of the bytes
     * @param length the length of the bytes
     */
    public void setSelection(final int selectionStart, final int length) {
        if ((this.data == null) || (selectionStart < 0)) {
            return;
        }
        if (this.data.length < (long) selectionStart + length) {
            return;
        }

        this.selectedStartIndex = selectionStart;
        this.selectedLength = length;

        // Moving the scroll bar already refreshes the view
        if (!this.ensureVisible(selectionStart)) {
            this.updateViewContent();
        }
    }

    /**
     * Ensure the byte at <code>startPos</code> is visible.
     *
     * @param startPos the start position of the byte(s) to be visible
     * @return true if the scroll bar was moved, which refreshes the view
     */
    private boolean ensureVisible(final int startPos) {
        if ((this.data == null) || (startPos < 0) || (this.data.length <= startPos)) {
            return false;
        }

        final int rowId = startPos / ROW_ITEM_MAX;
        int low = this.vBar.getValue();
        int high = low + this.getExtent() - 1;
        if (rowId < low || rowId > high) {
            this.vBar.setValue(rowId);
            return this.vBar.getValue() != low;
        }
        return false;
    }

    private void mouseWheelMoved(final MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            this.wheelRemainder += e.getPreciseWheelRotation() * e.getScrollAmount();
            final int steps = (int) this.wheelRemainder;
            if (steps != 0) {
                this.wheelRemainder -= steps;
                this.vBar.setValue(this.vBar.getValue() + steps);
            }
        }
    }
}
