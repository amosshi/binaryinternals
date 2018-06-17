/*
 * JBinaryViewer.java    September 3, 2007, 12:07 AM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.awt.Font;
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
import org.freeinternals.commonlib.ui.binviewer.JAsciiDataViewer;
import org.freeinternals.commonlib.ui.binviewer.JRawDataViewer;
import org.freeinternals.commonlib.ui.binviewer.JRowViewer;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JBinaryViewer extends JPanel {

    private static final long serialVersionUID = 4876543219876500005L;
    public static final Font FONT = new Font(Font.DIALOG_INPUT, Font.PLAIN, 14);
    public static final int ITEM_HEIGHT = 22; // 20;
    public static final int ROW_ITEM_MAX = 16;
    public static final int ROW_ITEM_MAX_INDEX = ROW_ITEM_MAX - 1;
    private final JRowViewer rowViewer;
    private final JRawDataViewer rawViewer;
    private final JAsciiDataViewer asciiViewer;
    private byte[] data = null;
    private static final int ROW_EMPTYROW_COUNT = 10;
    private JScrollBar vBar;
    private int rowMax;
    private int selectedStartIndex = 0;
    private int selectedLength = 0;

    public JBinaryViewer() {
        this.setLayout(new BorderLayout());
        // this.setFont(JBinaryViewer.FONT);
        this.addComponentListener(new ComponentResizedAdapter());
        this.addMouseWheelListener(new MouseWheelAdapter());

        // Vertical Bar
        this.vBar = new JScrollBar();
        this.vBar.addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateViewContent();
            }
        });
        this.vBar.setVisible(false);

        this.add(this.vBar, BorderLayout.EAST);

        // Content Panel
        final JPanel panel = new JPanel();
        final SpringLayout panelLayout = new SpringLayout();
        int left, right;

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
        panelLayout.putConstraint(SpringLayout.SOUTH, this.rowViewer, -4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.rowViewer, JRowViewer.WIDTH_VALUE, SpringLayout.WEST, panel);

        left = 2 + JRowViewer.WIDTH_VALUE + 2;
        right = left + JRawDataViewer.WIDTH_VALUE;
        panelLayout.putConstraint(SpringLayout.WEST, this.rawViewer, left, SpringLayout.WEST, panel);
        panelLayout.putConstraint(SpringLayout.NORTH, this.rawViewer, 2, SpringLayout.NORTH, panel);
        panelLayout.putConstraint(SpringLayout.SOUTH, this.rawViewer, -4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.rawViewer, right, SpringLayout.WEST, panel);

        left = right + 2;
        right = left + JAsciiDataViewer.WIDTH_VALUE;
        panelLayout.putConstraint(SpringLayout.WEST, this.asciiViewer, left, SpringLayout.WEST, panel);
        panelLayout.putConstraint(SpringLayout.NORTH, this.asciiViewer, 2, SpringLayout.NORTH, panel);
        panelLayout.putConstraint(SpringLayout.SOUTH, this.asciiViewer, -4, SpringLayout.SOUTH, panel);
        panelLayout.putConstraint(SpringLayout.EAST, this.asciiViewer, right, SpringLayout.WEST, panel);

        this.add(panel, BorderLayout.CENTER);
    }

    public void setData(final byte[] data) {
        if (data == null) {
            return;
        }

        this.data = data.clone();

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
     */
    private int getRowCount(int number) {
        int count = 0;
        while (number > 0) {
            count++;
            number -= ROW_ITEM_MAX;
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
            byte[] buf = null;
            int startPos = this.vBar.getValue() * JBinaryViewer.ROW_ITEM_MAX;
            int dataSize = extent * JBinaryViewer.ROW_ITEM_MAX;

            dataSize = Math.min(dataSize, this.data.length - startPos);

            if (dataSize > 0) {
                buf = new byte[dataSize];
                System.arraycopy(this.data, startPos, buf, 0, dataSize);
            }

            // Set the buffer
            this.rawViewer.setData(buf);
            this.asciiViewer.setData(buf);
        }

        // Revise selection
        this.updateSelection();
    }

    /**
     * Selects the bytes between the specified start position and length.
     *
     * @param selectionStart the start position of the bytes
     * @param length         the length of the bytes
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
            length =  Math.min(this.selectedLength, lengtgMax);                 // Improve Performance
            this.rawViewer.setSelection(startPos, length);
            this.asciiViewer.setSelection(startPos, length);
        } else if ((length = startPos + this.selectedLength) > 0) {
            length =  Math.min(length, lengtgMax);                              // Improve Performance
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
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            updateViewContent();
        }
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    class MouseWheelAdapter implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            switch(e.getScrollType()){
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

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
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

        public void keyReleased(KeyEvent e) {
        }
    }
}
