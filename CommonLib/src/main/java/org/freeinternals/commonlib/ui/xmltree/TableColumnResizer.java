package org.freeinternals.commonlib.ui.xmltree;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumn;

/**
 * Allows table columns to be resized not only using the header but from any
 * rows. Based on the BasicTableHeaderUI.MouseInputHandler code.
 *
 * Created by <code>Santhosh Kumar T</code>. Miner change may be done on
 * formatting, annotation, java doc, etc, for check style.
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer
 * for Swing</a>
 */
public final class TableColumnResizer extends MouseInputAdapter {

    /**
     * Mouse cursor for resize.
     */
    public static final Cursor RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    /**
     * Constant value -3.
     */
    private static final int MINUS_3 = -3;
    /**
     * Mouse X offset.
     */
    private int mouseXOffset;
    /**
     * Mouse cursor for others.
     */
    private Cursor otherCursor = RESIZE_CURSOR;
    /**
     * Swing table.
     */
    private final JTable table;

    /**
     * Constructor.
     *
     * @param t Swing table
     */
    public TableColumnResizer(final JTable t) {
        this.table = t;
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }

    private boolean canResize(final TableColumn column) {
        return column != null
                && table.getTableHeader().getResizingAllowed()
                && column.getResizable();
    }

    private TableColumn getResizingColumn(final Point p) {
        return getResizingColumn(p, table.columnAtPoint(p));
    }

    private TableColumn getResizingColumn(final Point p, final int column) {
        if (column == -1) {
            return null;
        }
        int row = table.rowAtPoint(p);
        if (row == -1) {
            return null;
        }
        Rectangle r = table.getCellRect(row, column, true);
        r.grow(MINUS_3, 0);
        if (r.contains(p)) {
            return null;
        }

        int midPoint = r.x + r.width / 2;
        int columnIndex;
        if (table.getTableHeader().getComponentOrientation().isLeftToRight()) {
            columnIndex = (p.x < midPoint) ? column - 1 : column;
        } else {
            columnIndex = (p.x < midPoint) ? column : column - 1;
        }
        if (columnIndex == -1) {
            return null;
        }

        return table.getTableHeader().getColumnModel().getColumn(columnIndex);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        table.getTableHeader().setDraggedColumn(null);
        table.getTableHeader().setResizingColumn(null);
        table.getTableHeader().setDraggedDistance(0);

        Point p = e.getPoint();

        // First find which header cell was hit
        int index = table.columnAtPoint(p);
        if (index == -1) {
            return;
        }

        // The last 3 pixels + 3 pixels of next column are for resizing
        TableColumn resizingColumn = getResizingColumn(p, index);
        if (!canResize(resizingColumn)) {
            return;
        }

        table.getTableHeader().setResizingColumn(resizingColumn);
        if (table.getTableHeader().getComponentOrientation().isLeftToRight()) {
            mouseXOffset = p.x - resizingColumn.getWidth();
        } else {
            mouseXOffset = p.x + resizingColumn.getWidth();
        }
    }

    private void swapCursor() {
        Cursor tmp = table.getCursor();
        table.setCursor(otherCursor);
        otherCursor = tmp;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (canResize(getResizingColumn(e.getPoint()))
                != (table.getCursor() == RESIZE_CURSOR)) {
            swapCursor();
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        int mouseX = e.getX();

        TableColumn resizingColumn = table.getTableHeader().getResizingColumn();

        boolean headerLeftToRight
                = table.getTableHeader().getComponentOrientation().isLeftToRight();

        if (resizingColumn != null) {
            int oldWidth = resizingColumn.getWidth();
            int newWidth;
            if (headerLeftToRight) {
                newWidth = mouseX - mouseXOffset;
            } else {
                newWidth = mouseXOffset - mouseX;
            }
            resizingColumn.setWidth(newWidth);

            Container container = table.getTableHeader().getParent().getParent();
            if ((table.getTableHeader().getParent() == null)
                    || (container == null)
                    || !(container instanceof JScrollPane)) {
                return;
            }

            if (!container.getComponentOrientation().isLeftToRight()
                    && !headerLeftToRight) {
                if (table != null) {
                    JViewport viewport = ((JScrollPane) container).getViewport();
                    int viewportWidth = viewport.getWidth();
                    int diff = newWidth - oldWidth;
                    int newHeaderWidth = table.getWidth() + diff;

                    /* Resize a table */
                    Dimension tableSize = table.getSize();
                    tableSize.width += diff;
                    table.setSize(tableSize);

                    /*
                     * If this table is in AUTO_RESIZE_OFF mode and has a horizontal
                     * scrollbar, we need to update a view's position.
                     */
                    if ((newHeaderWidth >= viewportWidth)
                            && (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF)) {
                        Point p = viewport.getViewPosition();
                        p.x
                                = Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff));
                        viewport.setViewPosition(p);

                        /* Update the original X offset value. */
                        mouseXOffset += diff;
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        table.getTableHeader().setResizingColumn(null);
        table.getTableHeader().setDraggedColumn(null);
    }
}
