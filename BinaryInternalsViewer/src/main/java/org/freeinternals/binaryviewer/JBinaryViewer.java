/*
 * JBinaryViewer.java    September 3, 2007, 12:07 AM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.binaryviewer;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
     * @see DataViewer#selectedStartIndex
     */
    private int selectedStartIndex = 0;

    /**
     * Length to be high-lighted.
     *
     * @see DataViewer#selectedLength
     */
    private int selectedLength = 0;

    /**
     * Constructor.
     */
    public JBinaryViewer() {
        this.setLayout(new BorderLayout());
        // this.setFont(JBinaryViewer.FONT);
        this.addComponentListener(new ComponentResizedAdapter());
        this.addMouseWheelListener(new MouseWheelAdapter());

        // Vertical Bar
        this.vBar = new JScrollBar();
        this.vBar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(final AdjustmentEvent e) {
                updateViewContent();
            }
        });
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
        this.rawViewer.addKeyListener(new KeyboardAdapter());
        this.asciiViewer = new JAsciiDataViewer();
        this.asciiViewer.addKeyListener(new KeyboardAdapter());

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
     * Set the binary data to be displayed.
     *
     * @param bytes Binary data to be displayed
     */
    public void setData(final byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.data = bytes.clone();

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
        int count = 0;
        int max = number;
        while (max > 0) {
            count++;
            max -= ROW_ITEM_MAX;
        }
        return count;
    }

    private int getExtent() {
        return (int) Math.ceil(this.getSize().getHeight() / ITEM_HEIGHT);
    }

    private void updateViewContent() {
        // Update Extent
        int extent = this.getExtent();
        if (extent <= 0) {
            return;                                                             // The window Hight is (nearly) zero
        }
        this.vBar.setVisibleAmount(extent);

        if ((extent + this.vBar.getValue()) > (this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT)) {
            int diff = (this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT) - extent;
            diff = (diff > 0) ? diff : 0;
            this.vBar.setValue(diff);
        }

        if (extent > this.rowMax + JBinaryViewer.ROW_EMPTYROW_COUNT) {
            this.vBar.setVisible(false);
        } else {
            this.vBar.setVisible(true);
        }

        // Revise row viewer, raw data viewer, ASCII data viewer
        this.rowViewer.setData(this.vBar.getValue(), extent, this.rowMax);
        if (this.data != null && this.data.length > 0) {
            // Calc the buffer data
            int startPos = this.vBar.getValue() * JBinaryViewer.ROW_ITEM_MAX;
            int dataSize = extent * JBinaryViewer.ROW_ITEM_MAX;

            dataSize = Math.min(dataSize, this.data.length - startPos);
            if (dataSize > 0) {
                byte[] buf = new byte[dataSize];
                System.arraycopy(this.data, startPos, buf, 0, dataSize);

                // Set the buffer
                this.rawViewer.setData(buf);
                this.asciiViewer.setData(buf);
            }
        }

        // Revise selection
        this.updateSelection();
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
        if (this.data.length < (selectionStart + length - 1)) {
            return;
        }

        this.selectedStartIndex = selectionStart;
        this.selectedLength = length;

        this.ensureVisible(selectionStart);
        this.updateSelection();
    }

    private void updateSelection() {
        final int startPos = this.selectedStartIndex - this.vBar.getValue() * JBinaryViewer.ROW_ITEM_MAX;
        final int lengtgMax = this.getExtent() * JBinaryViewer.ROW_ITEM_MAX;
        int length;

        if (startPos > 0) {
            length = Math.min(this.selectedLength, lengtgMax);                  // Improve Performance
            this.rawViewer.setSelection(startPos, length);
            this.asciiViewer.setSelection(startPos, length);
        } else if ((startPos + this.selectedLength) > 0) {
            length = Math.min(startPos + this.selectedLength, lengtgMax);       // Improve Performance
            this.rawViewer.setSelection(0, length);
            this.asciiViewer.setSelection(0, length);
        } else {
            this.rawViewer.setSelection(0, 0);
            this.asciiViewer.setSelection(0, 0);
        }
    }

    /**
     * Ensure the byte at <code>startPos</code> is visible.
     *
     * @param startPos the start position of the byte(s) to be visible
     */
    private void ensureVisible(final int startPos) {
        if ((this.data == null) || (startPos < 0) || (this.data.length < (startPos - 1))) {
            return;
        }

        int rowId = this.getRowCount(startPos) - 1;
        int low = this.vBar.getValue();
        int high = low + this.getExtent() - 1;
        if (rowId < low || rowId > high) {
            this.vBar.setValue(rowId);
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    class ComponentResizedAdapter extends ComponentAdapter {

        @Override
        public void componentResized(final ComponentEvent e) {
            super.componentResized(e);
            updateViewContent();
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    class MouseWheelAdapter implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            switch (e.getScrollType()) {
                case MouseWheelEvent.WHEEL_UNIT_SCROLL:
                    JBinaryViewer.this.vBar.setValue(
                            JBinaryViewer.this.vBar.getValue()
                            + e.getUnitsToScroll());
                    break;
                case MouseWheelEvent.WHEEL_BLOCK_SCROLL:
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    class KeyboardAdapter implements KeyListener {

        @Override
        public void keyTyped(final KeyEvent e) {
        }

        @Override
        public void keyPressed(final KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_HOME:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getMinimum());
                    break;

                case KeyEvent.VK_END:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getMaximum());
                    break;

                case KeyEvent.VK_UP:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getValue() - 1);
                    break;
                case KeyEvent.VK_DOWN:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getValue() + 1);
                    break;

                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_PAGE_UP:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getValue() - JBinaryViewer.this.vBar.getVisibleAmount());
                    break;

                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_PAGE_DOWN:
                    JBinaryViewer.this.vBar.setValue(JBinaryViewer.this.vBar.getValue() + JBinaryViewer.this.vBar.getVisibleAmount());
                    break;

                default:
                    break;
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
        }
    }
}
